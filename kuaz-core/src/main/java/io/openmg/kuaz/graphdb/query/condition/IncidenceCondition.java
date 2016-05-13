package io.openmg.kuaz.graphdb.query.condition;

import com.google.common.base.Preconditions;
import io.openmg.kuaz.core.TitanEdge;
import io.openmg.kuaz.core.TitanRelation;
import io.openmg.kuaz.core.TitanVertex;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class IncidenceCondition<E extends TitanRelation> extends Literal<E> {

    private final TitanVertex baseVertex;
    private final TitanVertex otherVertex;

    public IncidenceCondition(TitanVertex baseVertex, TitanVertex otherVertex) {
        Preconditions.checkNotNull(baseVertex);
        Preconditions.checkNotNull(otherVertex);
        this.baseVertex = baseVertex;
        this.otherVertex = otherVertex;
    }

    @Override
    public boolean evaluate(E relation) {
        return relation.isEdge() && ((TitanEdge) relation).otherVertex(baseVertex).equals(otherVertex);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getType()).append(baseVertex).append(otherVertex).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other==null || !getClass().isInstance(other))
            return false;

        IncidenceCondition oth = (IncidenceCondition)other;
        return baseVertex.equals(oth.baseVertex) && otherVertex.equals(oth.otherVertex);
    }

    @Override
    public String toString() {
        return "incidence["+ baseVertex + "-" + otherVertex + "]";
    }
}
