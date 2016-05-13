package io.openmg.kuaz.diskstorage.keycolumnvalue;

import com.google.common.base.Preconditions;
import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.BaseTransactionConfig;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.StoreMetaData;
import io.openmg.kuaz.diskstorage.configuration.Configuration;

import java.util.List;
import java.util.Map;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class KCVSManagerProxy implements KeyColumnValueStoreManager {

    protected final KeyColumnValueStoreManager manager;

    public KCVSManagerProxy(KeyColumnValueStoreManager manager) {
        Preconditions.checkArgument(manager != null);
        this.manager = manager;
    }

    @Override
    public StoreTransaction beginTransaction(BaseTransactionConfig config) throws BackendException {
        return manager.beginTransaction(config);
    }

    @Override
    public void close() throws BackendException {
        manager.close();
    }

    @Override
    public void clearStorage() throws BackendException {
        manager.clearStorage();
    }

    @Override
    public StoreFeatures getFeatures() {
        return manager.getFeatures();
    }

    @Override
    public String getName() {
        return manager.getName();
    }

    @Override
    public List<KeyRange> getLocalKeyPartition() throws BackendException {
        return manager.getLocalKeyPartition();
    }

    @Override
    public KeyColumnValueStore openDatabase(String name, StoreMetaData.Container metaData) throws BackendException {
        return manager.openDatabase(name, metaData);
    }

    @Override
    public void mutateMany(Map<String, Map<StaticBuffer, KCVMutation>> mutations, StoreTransaction txh) throws BackendException {
        manager.mutateMany(mutations,txh);
    }

}
