package io.openmg.kuaz.graphdb.types.vertices;

import io.openmg.kuaz.core.EdgeLabel;
import io.openmg.kuaz.graphdb.transaction.StandardTitanTx;
import io.openmg.kuaz.graphdb.types.TypeDefinitionCategory;
import org.apache.tinkerpop.gremlin.structure.Direction;

public class EdgeLabelVertex extends RelationTypeVertex implements EdgeLabel {

    public EdgeLabelVertex(StandardTitanTx tx, long id, byte lifecycle) {
        super(tx, id, lifecycle);
    }

    @Override
    public boolean isDirected() {
        return isUnidirected(Direction.BOTH);
    }

    @Override
    public boolean isUnidirected() {
        return isUnidirected(Direction.OUT);

    }

    @Override
    public boolean isUnidirected(Direction dir) {
        return getDefinition().getValue(TypeDefinitionCategory.UNIDIRECTIONAL,Direction.class)==dir;
    }

    @Override
    public final boolean isPropertyKey() {
        return false;
    }

    @Override
    public final boolean isEdgeLabel() {
        return true;
    }
}
