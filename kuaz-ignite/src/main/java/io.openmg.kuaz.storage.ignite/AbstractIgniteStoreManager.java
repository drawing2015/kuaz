package io.openmg.kuaz.storage.ignite;

import java.util.*;

import com.google.common.collect.ImmutableMap;
import io.openmg.kuaz.core.TitanException;
import io.openmg.kuaz.storage.BackendException;
import io.openmg.kuaz.storage.BaseTransactionConfig;
import io.openmg.kuaz.storage.common.DistributedStoreManager;
import io.openmg.kuaz.storage.configuration.ConfigElement;
import io.openmg.kuaz.storage.configuration.ConfigNamespace;
import io.openmg.kuaz.storage.configuration.ConfigOption;
import io.openmg.kuaz.storage.configuration.Configuration;
import io.openmg.kuaz.storage.keycolumnvalue.KeyColumnValueStoreManager;
import io.openmg.kuaz.storage.keycolumnvalue.StandardStoreFeatures;
import io.openmg.kuaz.storage.keycolumnvalue.StoreFeatures;
import io.openmg.kuaz.storage.keycolumnvalue.StoreTransaction;
import io.openmg.kuaz.graphdb.configuration.GraphDatabaseConfiguration;
import io.openmg.kuaz.graphdb.configuration.PreInitializeConfigOptions;

import static io.openmg.kuaz.graphdb.configuration.GraphDatabaseConfiguration.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zizai (http://github.com/zizai).
 */
public abstract class AbstractIgniteStoreManager extends DistributedStoreManager implements KeyColumnValueStoreManager {

    // 初始化和设置后端参数，参考 AbstractCassandraStoreManager
}
