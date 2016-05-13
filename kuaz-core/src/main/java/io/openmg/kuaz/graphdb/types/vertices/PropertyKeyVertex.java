package io.openmg.kuaz.graphdb.types.vertices;

import io.openmg.kuaz.core.Cardinality;
import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.graphdb.transaction.StandardTitanTx;
import io.openmg.kuaz.graphdb.types.TypeDefinitionCategory;
import org.apache.tinkerpop.gremlin.structure.Direction;

public class PropertyKeyVertex extends RelationTypeVertex implements PropertyKey {

    public PropertyKeyVertex(StandardTitanTx tx, long id, byte lifecycle) {
        super(tx, id, lifecycle);
    }

    @Override
    public Class<?> dataType() {
        return getDefinition().getValue(TypeDefinitionCategory.DATATYPE,Class.class);
    }

    @Override
    public Cardinality cardinality() {
        return super.multiplicity().getCardinality();
    }

    @Override
    public final boolean isPropertyKey() {
        return true;
    }

    @Override
    public final boolean isEdgeLabel() {
        return false;
    }

    @Override
    public boolean isUnidirected(Direction dir) {
        return dir==Direction.OUT;
    }
}
