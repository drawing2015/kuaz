package io.openmg.trike.graphdb.schema;

import io.openmg.trike.core.Cardinality;
import io.openmg.trike.core.Multiplicity;
import org.apache.tinkerpop.gremlin.structure.Direction;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public abstract class RelationTypeDefinition extends SchemaElementDefinition {

    private final Multiplicity multiplicity;

    public RelationTypeDefinition(String name, long id, Multiplicity multiplicity) {
        super(name, id);
        this.multiplicity = multiplicity;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public Cardinality getCardinality() {
        return multiplicity.getCardinality();
    }

    public abstract boolean isUnidirected(Direction dir);

}
