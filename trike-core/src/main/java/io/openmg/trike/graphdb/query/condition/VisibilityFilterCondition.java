package io.openmg.trike.graphdb.query.condition;

import io.openmg.trike.core.TitanElement;
import io.openmg.trike.core.TitanRelation;
import io.openmg.trike.core.schema.TitanSchemaElement;
import io.openmg.trike.core.TitanVertex;
import io.openmg.trike.graphdb.internal.InternalElement;
import io.openmg.trike.graphdb.types.system.SystemRelationType;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Evaluates elements based on their visibility
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class VisibilityFilterCondition<E extends TitanElement> extends Literal<E> {

    public enum Visibility { NORMAL, SYSTEM }

    private final Visibility visibility;

    public VisibilityFilterCondition(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean evaluate(E element) {
        switch(visibility) {
            case NORMAL: return !((InternalElement)element).isInvisible();
            case SYSTEM: return (element instanceof TitanRelation &&
                                    ((TitanRelation)element).getType() instanceof SystemRelationType)
                    || (element instanceof TitanVertex && element instanceof TitanSchemaElement);
            default: throw new AssertionError("Unrecognized visibility: " + visibility);
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getType()).append(visibility).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || !(other == null || !getClass().isInstance(other));

    }

    @Override
    public String toString() {
        return "visibility:"+visibility.toString().toLowerCase();
    }
}
