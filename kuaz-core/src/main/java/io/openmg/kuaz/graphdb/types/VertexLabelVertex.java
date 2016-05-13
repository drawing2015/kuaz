package io.openmg.kuaz.graphdb.types;

import io.openmg.kuaz.graphdb.internal.InternalVertexLabel;
import io.openmg.kuaz.graphdb.transaction.StandardTitanTx;
import io.openmg.kuaz.graphdb.types.vertices.TitanSchemaVertex;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class VertexLabelVertex extends TitanSchemaVertex implements InternalVertexLabel {


    public VertexLabelVertex(StandardTitanTx tx, long id, byte lifecycle) {
        super(tx, id, lifecycle);
    }

    @Override
    public boolean isPartitioned() {
        return getDefinition().getValue(TypeDefinitionCategory.PARTITIONED, Boolean.class);
    }

    @Override
    public boolean isStatic() {
        return getDefinition().getValue(TypeDefinitionCategory.STATIC, Boolean.class);
    }

    @Override
    public boolean hasDefaultConfiguration() {
        return !isPartitioned() && !isStatic();
    }

    private Integer ttl = null;

    @Override
    public int getTTL() {
        if (null == ttl) {
            ttl = TypeUtil.getTTL(this);
        }
        return ttl;
    }

}
