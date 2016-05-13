package io.openmg.trike.graphdb.query.condition;

import com.google.common.base.Preconditions;
import io.openmg.trike.core.TitanEdge;
import io.openmg.trike.core.TitanRelation;
import io.openmg.trike.core.TitanVertex;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class IncidenceDirectionCondition<E extends TitanRelation> extends Literal<E> {

    private final Direction direction;
    private final TitanVertex otherVertex;

    public IncidenceDirectionCondition(Direction direction, TitanVertex otherVertex) {
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(otherVertex);
        this.direction = direction;
        this.otherVertex = otherVertex;
    }

    @Override
    public boolean evaluate(E relation) {
        return relation.isEdge() && ((TitanEdge) relation).vertex(direction).equals(otherVertex);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getType()).append(direction).append(otherVertex).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other==null || !getClass().isInstance(other))
            return false;

        IncidenceDirectionCondition oth = (IncidenceDirectionCondition)other;
        return direction==oth.direction && otherVertex.equals(oth.otherVertex);
    }

    @Override
    public String toString() {
        return "incidence["+ direction + "-" + otherVertex + "]";
    }
}
