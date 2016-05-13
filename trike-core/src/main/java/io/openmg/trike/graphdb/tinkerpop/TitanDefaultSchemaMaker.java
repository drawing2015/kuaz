package io.openmg.trike.graphdb.tinkerpop;

import io.openmg.trike.core.*;
import io.openmg.trike.core.schema.DefaultSchemaMaker;
import io.openmg.trike.core.schema.EdgeLabelMaker;
import io.openmg.trike.core.schema.PropertyKeyMaker;
import io.openmg.trike.core.schema.VertexLabelMaker;

/**
 * {@link io.openmg.trike.core.schema.DefaultSchemaMaker} implementation for Blueprints graphs
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class TitanDefaultSchemaMaker implements DefaultSchemaMaker {

    public static final DefaultSchemaMaker INSTANCE = new TitanDefaultSchemaMaker();

    private TitanDefaultSchemaMaker() {
    }

    @Override
    public Cardinality defaultPropertyCardinality(String key) {
        return Cardinality.SINGLE;
    }


    @Override
    public boolean ignoreUndefinedQueryTypes() {
        return true;
    }
}
