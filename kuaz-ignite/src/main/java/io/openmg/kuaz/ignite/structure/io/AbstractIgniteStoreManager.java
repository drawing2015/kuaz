package io.openmg.kuaz.ignite.structure.io;

import com.thinkaurelius.titan.diskstorage.common.DistributedStoreManager;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigNamespace;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigOption;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zizai (http://github.com/zizai).
 */
public abstract class AbstractIgniteStoreManager extends DistributedStoreManager implements KeyColumnValueStoreManager {

    private static final Logger log = LoggerFactory.getLogger(AbstractIgniteStoreManager.class);

    public static final ConfigNamespace IGNITE_NS = new ConfigNamespace(GraphDatabaseConfiguration.STORAGE_NS,
                                                                        "ignite", "Ignite storage backend options");

    public static final ConfigOption<String> IGNITE_GROUP =
        new ConfigOption<>(IGNITE_NS, "group",
                           "The name of Titan's ignite group. This config just a prefix of whole cache name",
                           ConfigOption.Type.LOCAL, "kuaz");

    public static final ConfigOption<String> GRID_NAME =
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

    public static final ConfigNamespace IGNITE_CACHE_NS = new ConfigNamespace(IGNITE_NS, "cache", "Ignite cache options");

    public static final ConfigOption<CacheMode> CACHE_MODE =
        new ConfigOption<>(IGNITE_CACHE_NS, "mode",
                           "Ignite cache mode",
                           ConfigOption.Type.GLOBAL_OFFLINE, CacheMode.PARTITIONED);

    public static final ConfigOption<Integer> CACHE_BACKUPS =
        new ConfigOption<>(IGNITE_CACHE_NS, "backups",
                           "Ignite cache backups",
                           ConfigOption.Type.GLOBAL_OFFLINE, 1);


    protected final String group;

    private final String gridName;
    private final int reconnectCount;
    private final long metricsLogLrequency;
    private final boolean peerClassLoading;

    protected final int backups;
    protected final CacheMode mode;

    protected final Map<String, IgniteStore> openStores;

    protected final Ignite ignite;

    public AbstractIgniteStoreManager(Configuration storageConfig, int portDefault) {
        super(storageConfig, portDefault);
        group = storageConfig.get(IGNITE_GROUP);
        gridName = storageConfig.get(GRID_NAME);
        reconnectCount = storageConfig.get(RECONNECT_COUNT);
        metricsLogLrequency = storageConfig.get(METRICS_LOG_FREQUENCY);
        peerClassLoading = storageConfig.get(PEER_CLASS_LOADING);

        backups = storageConfig.get(CACHE_BACKUPS);
        mode = storageConfig.get(CACHE_MODE);

        openStores = new ConcurrentHashMap<>();

        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList(hostnames));
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        tcpDiscoverySpi.setIpFinder(ipFinder)
                       .setReconnectCount(reconnectCount);

        log.info("Connection ignite cluster[{}]", hostnames);
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setGridName(gridName)
                           .setMetricsLogFrequency(metricsLogLrequency)
                           .setClientMode(true)
                           .setPeerClassLoadingEnabled(peerClassLoading)
                           .setDiscoverySpi(tcpDiscoverySpi);

        ignite = Ignition.start(igniteConfiguration);
    }

}
