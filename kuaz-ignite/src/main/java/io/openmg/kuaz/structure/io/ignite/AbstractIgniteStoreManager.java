package io.openmg.kuaz.structure.io.ignite;

import com.thinkaurelius.titan.diskstorage.common.DistributedStoreManager;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigNamespace;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigOption;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zizai (http://github.com/zizai).
 */
public abstract class AbstractIgniteStoreManager extends DistributedStoreManager implements KeyColumnValueStoreManager {

    private static final Logger log = LoggerFactory.getLogger(AbstractIgniteStoreManager.class);

    public static final ConfigNamespace IGNITE_NS =
        new ConfigNamespace(GraphDatabaseConfiguration.STORAGE_NS, "ignite", "Ignite storage backend options");

    public static final ConfigOption<String> IGNITE_GROUP =
        new ConfigOption<>(IGNITE_NS, "group",
                           "The name of Titan's ignite group. This config just a prefix of whole cache name",
                           ConfigOption.Type.LOCAL, "kuaz");

    public static final ConfigOption<String> IGNITE_GRID_NAME =
        new ConfigOption<>(IGNITE_NS, "grid-name",
                           "The name of Titan's ignite grid name.",
                           ConfigOption.Type.LOCAL, "graph");

    public static final ConfigOption<Integer> RECONNECT_COUNT =
        new ConfigOption<>(IGNITE_NS, "reconnect-count",
                           "tcp ip finder reconnect-count",
                           ConfigOption.Type.LOCAL, Integer.MAX_VALUE);

    public static final ConfigOption<Long> METRICS_LOG_FREQUENCY =
        new ConfigOption<>(IGNITE_NS, "metrics-log-frequency",
                           "Ignite metrics log frequency",
                           ConfigOption.Type.LOCAL, 4 * 3600 * 1000L);

    public static final ConfigOption<Boolean> PEER_CLASS_LOADING =
        new ConfigOption<>(IGNITE_NS, "peer-class-loading",
                           "Enable Ignite peer class loading",
                           ConfigOption.Type.LOCAL, false);


    private final String group;

    public AbstractIgniteStoreManager(Configuration storageConfig, int portDefault) {
        super(storageConfig, portDefault);
        group = storageConfig.get(IGNITE_GROUP);
    }

    // 初始化和设置后端参数，参考 AbstractCassandraStoreManager

}
