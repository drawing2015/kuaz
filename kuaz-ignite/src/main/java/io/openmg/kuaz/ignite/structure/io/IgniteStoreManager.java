package io.openmg.kuaz.ignite.structure.io;

import com.thinkaurelius.titan.diskstorage.*;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KCVMutation;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyRange;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;

import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class IgniteStoreManager extends AbstractIgniteStoreManager {

    protected final int backups;
    protected final CacheMode mode;

    protected final Map<String, IgniteStore> openStores;

    public IgniteStoreManager(Configuration storageConfig, int portDefault) {
        super(storageConfig, portDefault);
        backups = storageConfig.get(CACHE_BACKUPS);
        mode = storageConfig.get(CACHE_MODE);

        openStores = new ConcurrentHashMap<>();
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
        IgniteStore store = new IgniteStore(ignite.getOrCreateCache(buildCacheConfiguration(getCacheName(name))));
        openStores.put(getCacheName(name), store);
        return null;
    }

    @Override
    public void mutateMany(Map<String, Map<StaticBuffer, KCVMutation>> mutations, StoreTransaction txh) throws BackendException {

    }

    @Override
    public StoreTransaction beginTransaction(BaseTransactionConfig config) throws BackendException {
        return null;
    }

    @Override
    public void close() throws BackendException {

    }

    @Override
    public void clearStorage() throws BackendException {

    }

    @Override
    public StoreFeatures getFeatures() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<KeyRange> getLocalKeyPartition() throws BackendException {
        return null;
    }

    public static String getEdgeStoreCacheName(String graphName) {
        return graphName + "_" + Backend.EDGESTORE_NAME;
    }

    protected String getCacheName(String storeName) {
        return group + "." + storeName;
    }

    /**
     * @param cacheName store name
     * @return CacheConfiguration {@link CacheConfiguration}
     */
    protected CacheConfiguration<StaticBuffer, LinkedHashMap<StaticBuffer, StaticBuffer>> buildCacheConfiguration(String cacheName) {
        CacheConfiguration<StaticBuffer, LinkedHashMap<StaticBuffer, StaticBuffer>> configuration = new CacheConfiguration<>();
        configuration.setBackups(backups);
        configuration.setCacheMode(mode);
        configuration.setName(cacheName);
        return configuration;
    }

    // https://apacheignite.readme.io/docs/jcache
    // https://github.com/apache/ignite/tree/master/examples/src/main/java8/org/apache/ignite/examples/java8/datagrid

}
