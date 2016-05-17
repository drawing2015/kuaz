package io.openmg.kuaz.structure.configuration;

import com.thinkaurelius.titan.diskstorage.configuration.ConfigNamespace;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigOption;
import com.thinkaurelius.titan.diskstorage.configuration.ReadConfiguration;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import org.apache.spark.SparkConf;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class KuazGraphConfiguration extends GraphDatabaseConfiguration {

    public KuazGraphConfiguration(ReadConfiguration localConfig) {
        super(localConfig);
    }

    public static final ConfigNamespace KUAZ_NS = new ConfigNamespace(ROOT_NS, "kuaz",
            "Kuaz configuration options");

    public static final ConfigOption<String> KUAZ_COMPUTER = new ConfigOption<String>(KUAZ_NS, "computer",
            "The implementation of graph computer that will be used by gremlin server",
            ConfigOption.Type.LOCAL,
            "io.openmg.kuaz.ignite.process.computer.SparkGraphComputer");

    public static final ConfigNamespace SPARK_NS = new ConfigNamespace(ROOT_NS, "spark",
            "Spark configuration options");

}
