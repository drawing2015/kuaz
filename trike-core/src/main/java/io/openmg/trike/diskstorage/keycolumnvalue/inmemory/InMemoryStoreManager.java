package io.openmg.trike.diskstorage.keycolumnvalue.inmemory;

import com.google.common.base.Preconditions;
import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.StaticBuffer;
import io.openmg.trike.diskstorage.BaseTransactionConfig;
import io.openmg.trike.diskstorage.StoreMetaData;
import io.openmg.trike.diskstorage.common.AbstractStoreTransaction;
import io.openmg.trike.diskstorage.configuration.Configuration;
import io.openmg.trike.diskstorage.keycolumnvalue.*;
import io.openmg.trike.graphdb.configuration.GraphDatabaseConfiguration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory backend storage engine.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class InMemoryStoreManager implements KeyColumnValueStoreManager {

    private final ConcurrentHashMap<String, InMemoryKeyColumnValueStore> stores;

    private final StoreFeatures features;

    public InMemoryStoreManager() {
        this(Configuration.EMPTY);
    }

    public InMemoryStoreManager(final Configuration configuration) {

        stores = new ConcurrentHashMap<String, InMemoryKeyColumnValueStore>();

        features = new StandardStoreFeatures.Builder()
            .orderedScan(true)
            .unorderedScan(true)
            .keyOrdered(true)
            .persists(false)
            .keyConsistent(GraphDatabaseConfiguration.buildGraphConfiguration())
            .build();

//        features = new StoreFeatures();
//        features.supportsOrderedScan = true;
//        features.supportsUnorderedScan = true;
//        features.supportsBatchMutation = false;
//        features.supportsTxIsolation = false;
//        features.supportsConsistentKeyOperations = true;
//        features.supportsLocking = false;
//        features.isDistributed = false;
//        features.supportsMultiQuery = false;
//        features.isKeyOrdered = true;
//        features.hasLocalKeyPartition = false;
    }

    @Override
    public StoreTransaction beginTransaction(final BaseTransactionConfig config) throws BackendException {
        return new InMemoryTransaction(config);
    }

    @Override
    public void close() throws BackendException {
        for (InMemoryKeyColumnValueStore store : stores.values()) {
            store.close();
        }
        stores.clear();
    }

    @Override
    public void clearStorage() throws BackendException {
        for (InMemoryKeyColumnValueStore store : stores.values()) {
            store.clear();
        }
    }

    @Override
    public StoreFeatures getFeatures() {
        return features;
    }

    @Override
    public KeyColumnValueStore openDatabase(final String name, StoreMetaData.Container metaData) throws BackendException {
        if (!stores.containsKey(name)) {
            stores.putIfAbsent(name, new InMemoryKeyColumnValueStore(name));
        }
        KeyColumnValueStore store = stores.get(name);
        Preconditions.checkNotNull(store);
        return store;
    }

    @Override
    public void mutateMany(Map<String, Map<StaticBuffer, KCVMutation>> mutations, StoreTransaction txh) throws BackendException {
        for (Map.Entry<String, Map<StaticBuffer, KCVMutation>> storeMut : mutations.entrySet()) {
            KeyColumnValueStore store = stores.get(storeMut.getKey());
            Preconditions.checkNotNull(store);
            for (Map.Entry<StaticBuffer, KCVMutation> keyMut : storeMut.getValue().entrySet()) {
                store.mutate(keyMut.getKey(), keyMut.getValue().getAdditions(), keyMut.getValue().getDeletions(), txh);
            }
        }
    }

    @Override
    public List<KeyRange> getLocalKeyPartition() throws BackendException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return toString();
    }

    private class InMemoryTransaction extends AbstractStoreTransaction {

        public InMemoryTransaction(final BaseTransactionConfig config) {
            super(config);
        }
    }
}
