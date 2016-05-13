package io.openmg.trike.graphdb.relations;

import com.google.common.base.Preconditions;
import io.openmg.trike.core.PropertyKey;
import io.openmg.trike.core.TitanElement;
import io.openmg.trike.core.TitanProperty;
import io.openmg.trike.graphdb.internal.InternalRelation;
import io.openmg.trike.graphdb.internal.InternalRelationType;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.NoSuchElementException;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class SimpleTitanProperty<V> implements TitanProperty<V> {

    private final PropertyKey key;
    private final V value;
    private final InternalRelation relation;

    public SimpleTitanProperty(InternalRelation relation, PropertyKey key, V value) {
        this.key = key;
        this.value = value;
        this.relation = relation;
    }

    @Override
    public PropertyKey propertyKey() {
        return key;
    }

    @Override
    public V value() throws NoSuchElementException {
        return value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public TitanElement element() {
        return relation;
    }

    @Override
    public void remove() {
        Preconditions.checkArgument(!relation.isRemoved(), "Cannot modified removed relation");
        relation.it().removePropertyDirect(key);
    }

    @Override
    public String toString() {
        return StringFactory.propertyString(this);
    }

    @Override
    public int hashCode() {
        return ElementHelper.hashCode(this);
    }

    @Override
    public boolean equals(Object oth) {
        return ElementHelper.areEqual(this, oth);
    }

}
