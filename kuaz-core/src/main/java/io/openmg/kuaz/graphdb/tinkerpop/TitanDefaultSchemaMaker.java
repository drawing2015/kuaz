package io.openmg.kuaz.graphdb.tinkerpop;

import io.openmg.kuaz.core.*;
import io.openmg.kuaz.core.schema.DefaultSchemaMaker;
import io.openmg.kuaz.core.schema.EdgeLabelMaker;
import io.openmg.kuaz.core.schema.PropertyKeyMaker;
import io.openmg.kuaz.core.schema.VertexLabelMaker;

/**
 * {@link io.openmg.kuaz.core.schema.DefaultSchemaMaker} implementation for Blueprints graphs
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
