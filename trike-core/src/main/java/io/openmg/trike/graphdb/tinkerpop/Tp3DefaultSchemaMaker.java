package io.openmg.trike.graphdb.tinkerpop;

import io.openmg.trike.core.Cardinality;
import io.openmg.trike.core.EdgeLabel;
import io.openmg.trike.core.PropertyKey;
import io.openmg.trike.core.VertexLabel;
import io.openmg.trike.core.schema.DefaultSchemaMaker;
import io.openmg.trike.core.schema.EdgeLabelMaker;
import io.openmg.trike.core.schema.PropertyKeyMaker;
import io.openmg.trike.core.schema.VertexLabelMaker;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class Tp3DefaultSchemaMaker implements DefaultSchemaMaker {

    public static final DefaultSchemaMaker INSTANCE = new Tp3DefaultSchemaMaker();

    private Tp3DefaultSchemaMaker() {
    }

    @Override
    public Cardinality defaultPropertyCardinality(String key) {
        return Cardinality.LIST;
    }

    @Override
    public boolean ignoreUndefinedQueryTypes() {
        return true;
    }

}
