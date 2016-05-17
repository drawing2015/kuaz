package io.openmg.kuaz.ignite.structure.io;

import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.BaseTransactionConfig;
import com.thinkaurelius.titan.diskstorage.PermanentBackendException;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.StoreMetaData;
import com.thinkaurelius.titan.diskstorage.common.AbstractStoreTransaction;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KCVMutation;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyRange;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StandardStoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class IgniteStoreManager extends AbstractIgniteStoreManager {

    private final int backups;
    private final CacheMode mode;
    private final CacheAtomicityMode atomicityMode;

    private Map<String, IgniteStore> openStores = new ConcurrentHashMap<>();
    private Map<String, IgniteCache<ByteBuffer, LinkedHashMap<ByteBuffer, ByteBuffer>>> caches = new ConcurrentHashMap<>();
    private Set<String> cacheNames = new HashSet<>();

    public IgniteStoreManager(Configuration storageConfig, int portDefault) {
        super(storageConfig, portDefault);
        backups = storageConfig.get(CACHE_BACKUPS);
        mode = storageConfig.get(CACHE_MODE);
        atomicityMode = storageConfig.get(CACHE_ATOMICITY_MODE);
    }

    @Override
    public Deployment getDeployment() {
        return Deployment.REMOTE;
    }

    @Override
    public KeyColumnValueStore openDatabase(String name, StoreMetaData.Container metaData) throws BackendException {
        if (openStores.containsKey(getCacheName(name))) {
            return openStores.get(name);
        }
        IgniteCache cache = ignite.getOrCreateCache(buildCacheConfiguration(getCacheName(name)));
        caches.put(getCacheName(name), cache);
        IgniteStore store = new IgniteStore(cache, this);
        openStores.put(getCacheName(name), store);
        cacheNames.add(getCacheName(name));
        return null;
    }

    @Override
    public void mutateMany(Map<String, Map<StaticBuffer, KCVMutation>> mutations, StoreTransaction txh) throws BackendException {
        ensureOpen();
        IgniteStoreTransaction tx = getTxn(txh);
        for (Map.Entry<String, Map<StaticBuffer, KCVMutation>> mutation : mutations.entrySet()) {
            //TODO use ignite transaction
            IgniteCache<ByteBuffer, LinkedHashMap<ByteBuffer, ByteBuffer>> cache = caches.get(mutation.getKey());
            for (Map.Entry<StaticBuffer, KCVMutation> kcv : mutation.getValue().entrySet()) {
                //first load the value from ignite
                LinkedHashMap<ByteBuffer, ByteBuffer> values = cache.get(kcv.getKey().asByteBuffer());
                //TODO deal with ttl
                if (kcv.getValue().hasAdditions()) {
                    kcv.getValue().getAdditions().forEach(entry -> values.put(entry.getColumn().asByteBuffer(), entry.getValue().asByteBuffer()));
                }
                if (kcv.getValue().hasDeletions()) {
                    kcv.getValue().getDeletions().forEach(buffer -> values.remove(buffer.asByteBuffer()));
                }
                cache.put(kcv.getKey().asByteBuffer(), values);
            }
        }
    }

    @Override
    public IgniteStoreTransaction beginTransaction(BaseTransactionConfig config) throws BackendException {
        return new IgniteStoreTransaction(config);
    }

    @Override
    public void close() throws BackendException {
        openStores.clear();
        try {
            ignite.close();
        } catch (IgniteException e) {
            throw new PermanentBackendException(e);
        }
    }

    @Override
    public void clearStorage() throws BackendException {
        //if ignite close can't clearStorage, so should open it again
        try {
            initIgniteClient();
        } catch (Exception e) {
            throw new PermanentBackendException("can't init ignite client", e);
        }
        ensureOpen();
        for (String cacheName : cacheNames) {
            ignite.destroyCache(cacheName);
        }
    }

    @Override
    public StoreFeatures getFeatures() {
        return new StandardStoreFeatures.Builder()
            .batchMutation(true)
            .cellTTL(true)
            .distributed(true)
            .keyOrdered(false)
            .localKeyPartition(false)
            .locking(true)
            .multiQuery(true)
            .orderedScan(false)
            .unorderedScan(false)
            .persists(true)
            .storeTTL(true)
            .storeTTL(true)
            .transactional(true)
            .timestamps(false)
            .build();
    }

    @Override
    public String getName() {
        return group;
    }

    @Override
    public List<KeyRange> getLocalKeyPartition() throws BackendException {
        throw new UnsupportedOperationException();
    }

    protected String getCacheName(String storeName) {
        return group + "." + storeName;
    }

    /**
     * @param cacheName store name
     * @return CacheConfiguration {@link CacheConfiguration}
     */
    protected CacheConfiguration<ByteBuffer, LinkedHashMap<ByteBuffer, ByteBuffer>> buildCacheConfiguration(String cacheName) {
        CacheConfiguration<ByteBuffer, LinkedHashMap<ByteBuffer, ByteBuffer>> configuration = new CacheConfiguration<>();
        configuration.setBackups(backups);
        configuration.setCacheMode(mode);
        configuration.setName(cacheName);
        configuration.setAtomicityMode(atomicityMode);
        return configuration;
    }

    // https://apacheignite.readme.io/docs/jcache
    // https://github.com/apache/ignite/tree/master/examples/src/main/java8/org/apache/ignite/examples/java8/datagrid

    private class IgniteStoreTransaction extends AbstractStoreTransaction implements StoreTransaction {

        public IgniteStoreTransaction(BaseTransactionConfig config) {
            super(config);
        }

        public boolean isAtomic() {
            return atomicityMode.equals(CacheAtomicityMode.ATOMIC);
        }
    }

    public static IgniteStoreTransaction getTxn(StoreTransaction transaction) {
        return (IgniteStoreTransaction) transaction;
    }

}
