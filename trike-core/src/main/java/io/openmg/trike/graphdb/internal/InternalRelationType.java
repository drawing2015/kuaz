package io.openmg.trike.graphdb.internal;

import io.openmg.trike.core.schema.ConsistencyModifier;
import io.openmg.trike.core.Multiplicity;
import io.openmg.trike.core.RelationType;
import io.openmg.trike.graphdb.types.IndexType;
import io.openmg.trike.core.schema.SchemaStatus;
import org.apache.tinkerpop.gremlin.structure.Direction;

/**
 * Internal Type interface adding methods that should only be used by Titan
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface InternalRelationType extends RelationType, InternalVertex {

    public boolean isInvisibleType();

    public long[] getSignature();

    public long[] getSortKey();

    public Order getSortOrder();

    public Multiplicity multiplicity();

    public ConsistencyModifier getConsistencyModifier();

    public Integer getTTL();

    public boolean isUnidirected(Direction dir);

    public InternalRelationType getBaseType();

    public Iterable<InternalRelationType> getRelationIndexes();

    public SchemaStatus getStatus();

    public Iterable<IndexType> getKeyIndexes();
}
