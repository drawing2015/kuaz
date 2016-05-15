package io.openmg.kuaz.structure.io.ignite;

import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.BaseTransactionConfig;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.StoreMetaData;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KCVMutation;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyRange;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;

import java.util.List;
import java.util.Map;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class IgniteStoreManager extends AbstractIgniteStoreManager {


    public IgniteStoreManager(Configuration storageConfig, int portDefault) {
        super(storageConfig, portDefault);
    }

    @Override
    public Deployment getDeployment() {
        return Deployment.REMOTE;
    }

    @Override
    public KeyColumnValueStore openDatabase(String name, StoreMetaData.Container metaData) throws BackendException {
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

    // https://apacheignite.readme.io/docs/jcache
    // https://github.com/apache/ignite/tree/master/examples/src/main/java8/org/apache/ignite/examples/java8/datagrid

}
