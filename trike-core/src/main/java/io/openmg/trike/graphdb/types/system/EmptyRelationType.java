package io.openmg.trike.graphdb.types.system;

import com.google.common.collect.ImmutableSet;
import io.openmg.trike.graphdb.internal.Order;
import io.openmg.trike.graphdb.internal.InternalRelationType;
import io.openmg.trike.graphdb.types.IndexType;
import io.openmg.trike.core.schema.SchemaStatus;

import java.util.Collections;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public abstract class EmptyRelationType extends EmptyVertex implements InternalRelationType {

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public long[] getSignature() {
        return new long[0];
    }

    @Override
    public long[] getSortKey() {
        return new long[0];
    }

    @Override
    public Order getSortOrder() {
        return Order.ASC;
    }

    @Override
    public InternalRelationType getBaseType() {
        return null;
    }

    @Override
    public Iterable<InternalRelationType> getRelationIndexes() {
        return ImmutableSet.of((InternalRelationType)this);
    }

    @Override
    public SchemaStatus getStatus() {
        return SchemaStatus.ENABLED;
    }

    @Override
    public Iterable<IndexType> getKeyIndexes() {
        return Collections.EMPTY_LIST;
    }

    public Integer getTTL() {
        return 0;
    }

    @Override
    public String toString() {
        return name();
    }
}
