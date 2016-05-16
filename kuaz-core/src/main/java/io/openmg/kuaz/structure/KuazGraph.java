package io.openmg.kuaz.structure;

import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import io.openmg.kuaz.process.computer.AbstractKuazGraphComputer;
import io.openmg.kuaz.structure.configuration.KuazGraphConfiguration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class KuazGraph extends StandardTitanGraph {

    private final KuazGraphConfiguration configuration;

    public KuazGraph(KuazGraphConfiguration configuration) {
        super(configuration);

        this.configuration = configuration;
    }

    @Override
    public <C extends GraphComputer> C compute(final Class<C> graphComputerClass) {
        try {
            if (AbstractKuazGraphComputer.class.isAssignableFrom(graphComputerClass))
                return graphComputerClass.getConstructor(KuazGraph.class).newInstance(this);
            else
                throw Graph.Exceptions.graphDoesNotSupportProvidedGraphComputer(graphComputerClass);
        } catch (final Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public GraphComputer compute() {
        try {
            return this.compute((Class<? extends GraphComputer>) Class.forName(this.configuration.KUAZ_COMPUTER.getDefaultValue()));
        } catch (final Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public KuazGraphConfiguration getConfiguration() {
        return configuration;
    }
}
