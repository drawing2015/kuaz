package io.openmg.kuaz.graphdb.internal;

import io.openmg.kuaz.core.schema.ConsistencyModifier;
import io.openmg.kuaz.core.Multiplicity;
import io.openmg.kuaz.core.RelationType;
import io.openmg.kuaz.graphdb.types.IndexType;
import io.openmg.kuaz.core.schema.SchemaStatus;
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
