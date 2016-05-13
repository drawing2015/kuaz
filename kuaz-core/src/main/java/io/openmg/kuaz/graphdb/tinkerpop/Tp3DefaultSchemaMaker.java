package io.openmg.kuaz.graphdb.tinkerpop;

import io.openmg.kuaz.core.Cardinality;
import io.openmg.kuaz.core.EdgeLabel;
import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.core.VertexLabel;
import io.openmg.kuaz.core.schema.DefaultSchemaMaker;
import io.openmg.kuaz.core.schema.EdgeLabelMaker;
import io.openmg.kuaz.core.schema.PropertyKeyMaker;
import io.openmg.kuaz.core.schema.VertexLabelMaker;

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
