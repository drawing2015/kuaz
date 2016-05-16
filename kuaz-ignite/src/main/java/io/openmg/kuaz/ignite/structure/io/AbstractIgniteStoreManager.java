package io.openmg.kuaz.ignite.structure.io;

import static com.thinkaurelius.titan.diskstorage.configuration.ConfigOption.Type.GLOBAL_OFFLINE;
import static com.thinkaurelius.titan.diskstorage.configuration.ConfigOption.Type.LOCAL;

import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.common.DistributedStoreManager;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigNamespace;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigOption;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.TransactionConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.transactions.TransactionIsolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by zizai (http://github.com/zizai).
 */
public abstract class AbstractIgniteStoreManager extends DistributedStoreManager implements KeyColumnValueStoreManager {

    private static final Logger log = LoggerFactory.getLogger(AbstractIgniteStoreManager.class);

    public static final ConfigNamespace IGNITE_NS = new ConfigNamespace(GraphDatabaseConfiguration.STORAGE_NS,
                                                                        "ignite", "Ignite storage backend options");

    public static final ConfigOption<String> IGNITE_GROUP = new ConfigOption<>(IGNITE_NS,
                                                                               "group",
                                                                               "The name of Titan's ignite group. This config just a prefix of whole cache name",
                                                                               LOCAL,
                                                                               "kuaz");

    public static final ConfigOption<String> GRID_NAME = new ConfigOption<>(IGNITE_NS,
                                                                            "grid-name",
                                                                            "The name of Titan's ignite grid name.",
                                                                            LOCAL,
                                                                            "graph");

    public static final ConfigOption<Integer> RECONNECT_COUNT = new ConfigOption<>(IGNITE_NS,
                                                                                   "reconnect-count",
                                                                                   "tcp ip finder reconnect-count",
                                                                                   LOCAL,
                                                                                   Integer.MAX_VALUE);

    public static final ConfigOption<Long> METRICS_LOG_FREQUENCY = new ConfigOption<>(IGNITE_NS,
                                                                                      "metrics-log-frequency",
                                                                                      "Ignite metrics log frequency",
                                                                                      LOCAL,
                                                                                      4 * 3600 * 1000L);

    public static final ConfigOption<Boolean> PEER_CLASS_LOADING = new ConfigOption<>(IGNITE_NS,
                                                                                      "peer-class-loading",
                                                                                      "Enable Ignite peer class loading",
                                                                                      LOCAL,
                                                                                      false);

    public static final ConfigNamespace IGNITE_CACHE_NS = new ConfigNamespace(IGNITE_NS,
                                                                              "cache",
                                                                              "Ignite cache options");

    public static final ConfigOption<CacheMode> CACHE_MODE = new ConfigOption<>(IGNITE_CACHE_NS,
                                                                                "mode",
                                                                                "Ignite cache mode",
                                                                                GLOBAL_OFFLINE,
                                                                                CacheMode.PARTITIONED);

    public static final ConfigOption<Integer> CACHE_BACKUPS = new ConfigOption<>(IGNITE_CACHE_NS,
                                                                                 "backups",
                                                                                 "Ignite cache backups",
                                                                                 GLOBAL_OFFLINE,
                                                                                 1);

    public static final ConfigOption<CacheAtomicityMode> CACHE_ATOMICITY_MODE = new ConfigOption<>(IGNITE_CACHE_NS,
                                                                                                   "cache-atomicity-mode",
                                                                                                   "Ignite cache atomicity modes",
                                                                                                   LOCAL,
                                                                                                   CacheAtomicityMode.ATOMIC);

    public static final ConfigNamespace IGNITE_TRANSACTION_NS = new ConfigNamespace(IGNITE_NS,
                                                                                    "transaction",
                                                                                    "Ignite transaction options");

    public static final ConfigOption<TransactionIsolation> TRANSACTION_ISOLATION = new ConfigOption<>(IGNITE_TRANSACTION_NS,
                                                                                                      "isolation",
                                                                                                      "",
                                                                                                      LOCAL,
                                                                                                      TransactionIsolation.READ_COMMITTED);


    protected final String group;

    private final String gridName;
    private final int reconnectCount;
    private final long metricsLogLrequency;
    private final boolean peerClassLoading;
    private final TransactionIsolation transactionIsolation;

    protected Ignite ignite;

    public AbstractIgniteStoreManager(Configuration storageConfig) {
        super(storageConfig, 47100);
        group = storageConfig.get(IGNITE_GROUP);
        gridName = storageConfig.get(GRID_NAME);
        reconnectCount = storageConfig.get(RECONNECT_COUNT);
        metricsLogLrequency = storageConfig.get(METRICS_LOG_FREQUENCY);
        peerClassLoading = storageConfig.get(PEER_CLASS_LOADING);

        transactionIsolation = storageConfig.get(TRANSACTION_ISOLATION);

        initIgniteClient();
    }

    protected void initIgniteClient() {
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList(hostnames));
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        tcpDiscoverySpi.setIpFinder(ipFinder)
                       .setReconnectCount(reconnectCount);

        log.info("Connection ignite cluster[{}]", hostnames);

        TransactionConfiguration transactionConfiguration = new TransactionConfiguration();
        transactionConfiguration.setDefaultTxIsolation(transactionIsolation);

        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setGridName(gridName)
                           .setMetricsLogFrequency(metricsLogLrequency)
                           .setClientMode(true)
                           .setPeerClassLoadingEnabled(peerClassLoading)
                           .setDiscoverySpi(tcpDiscoverySpi)
                           .setTransactionConfiguration(transactionConfiguration);

        ignite = Ignition.start(igniteConfiguration);
    }

    protected void ensureOpen() throws BackendException {

    }

    @Override
    public String toString() {
        return "AbstractIgniteStoreManager{" +
            "group='" + group + '\'' +
            ", gridName='" + gridName + '\'' +
            '}';
    }

}
