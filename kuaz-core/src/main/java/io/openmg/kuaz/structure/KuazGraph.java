package io.openmg.kuaz.structure;

import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import io.openmg.kuaz.process.computer.KuazGraphComputer;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class KuazGraph extends StandardTitanGraph {

    public KuazGraph(GraphDatabaseConfiguration configuration) {
        super(configuration);
    }

    @Override
    public KuazGraphComputer compute() throws IllegalArgumentException {
        KuazGraph graph = this;
        return new KuazGraphComputer(graph);
    }
}
