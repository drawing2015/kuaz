package io.openmg.trike.graphdb.types;

import com.google.common.base.Preconditions;
import io.openmg.trike.core.*;
import io.openmg.trike.core.schema.EdgeLabelMaker;
import io.openmg.trike.core.Multiplicity;
import io.openmg.trike.graphdb.database.IndexSerializer;
import io.openmg.trike.graphdb.database.serialize.AttributeHandler;
import io.openmg.trike.graphdb.internal.Order;
import io.openmg.trike.graphdb.internal.TitanSchemaCategory;
import io.openmg.trike.graphdb.transaction.StandardTitanTx;
import org.apache.tinkerpop.gremlin.structure.Direction;

import static io.openmg.trike.graphdb.types.TypeDefinitionCategory.INVISIBLE;
import static io.openmg.trike.graphdb.types.TypeDefinitionCategory.UNIDIRECTIONAL;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class StandardEdgeLabelMaker extends StandardRelationTypeMaker implements EdgeLabelMaker {

    private Direction unidirectionality;

    public StandardEdgeLabelMaker(final StandardTitanTx tx,
                                  final String name, final IndexSerializer indexSerializer,
                                  final AttributeHandler attributeHandler) {
        super(tx, name, indexSerializer, attributeHandler);
        unidirectionality = Direction.BOTH;
    }

    @Override
    TitanSchemaCategory getSchemaCategory() {
        return TitanSchemaCategory.EDGELABEL;
    }

    @Override
    public StandardEdgeLabelMaker directed() {
        unidirectionality = Direction.BOTH;
        return this;
    }

    @Override
    public StandardEdgeLabelMaker unidirected() {
        return unidirected(Direction.OUT);
    }

    public StandardEdgeLabelMaker unidirected(Direction dir) {
        Preconditions.checkNotNull(dir);
        unidirectionality = dir;
        return this;
    }

    @Override
    public StandardEdgeLabelMaker multiplicity(Multiplicity multiplicity) {
        super.multiplicity(multiplicity);
        return this;
    }

    @Override
    public StandardEdgeLabelMaker signature(PropertyKey... types) {
        super.signature(types);
        return this;
    }

    @Override
    public StandardEdgeLabelMaker sortKey(PropertyKey... types) {
        super.sortKey(types);
        return this;
    }

    @Override
    public StandardEdgeLabelMaker sortOrder(Order order) {
        super.sortOrder(order);
        return this;
    }

    @Override
    public StandardEdgeLabelMaker invisible() {
        super.invisible();
        return this;
    }

    @Override
    public EdgeLabel make() {
        TypeDefinitionMap definition = makeDefinition();
        Preconditions.checkArgument(unidirectionality==Direction.BOTH || !getMultiplicity().isUnique(unidirectionality.opposite()),
                "Unidirectional labels cannot have restricted multiplicity at the other end");
        Preconditions.checkArgument(unidirectionality==Direction.BOTH || !hasSortKey() ||
                !getMultiplicity().isUnique(unidirectionality),
                "Unidirectional labels with restricted multiplicity cannot have a sort key");
        Preconditions.checkArgument(unidirectionality!=Direction.IN || definition.getValue(INVISIBLE,Boolean.class));


        definition.setValue(UNIDIRECTIONAL, unidirectionality);
        return tx.makeEdgeLabel(getName(), definition);
    }

}
