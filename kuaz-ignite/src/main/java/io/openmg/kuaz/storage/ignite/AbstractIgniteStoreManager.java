package io.openmg.kuaz.storage.ignite;

import java.util.*;

import com.google.common.collect.ImmutableMap;
import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.BaseTransactionConfig;
import com.thinkaurelius.titan.diskstorage.common.DistributedStoreManager;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigElement;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigNamespace;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigOption;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StandardStoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.thinkaurelius.titan.graphdb.configuration.PreInitializeConfigOptions;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zizai (http://github.com/zizai).
 */
public abstract class AbstractIgniteStoreManager extends DistributedStoreManager implements KeyColumnValueStoreManager {

    // 初始化和设置后端参数，参考 AbstractCassandraStoreManager
}
