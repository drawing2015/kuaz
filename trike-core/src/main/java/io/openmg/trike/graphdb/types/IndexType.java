package io.openmg.trike.graphdb.types;

import io.openmg.trike.core.PropertyKey;
import io.openmg.trike.core.schema.TitanSchemaType;
import io.openmg.trike.graphdb.internal.ElementCategory;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface IndexType {

    public ElementCategory getElement();

    public IndexField[] getFieldKeys();

    public IndexField getField(PropertyKey key);

    public boolean indexesKey(PropertyKey key);

    public boolean isCompositeIndex();

    public boolean isMixedIndex();

    public boolean hasSchemaTypeConstraint();

    public TitanSchemaType getSchemaTypeConstraint();

    public String getBackingIndexName();

    public String getName();

    /**
     * Resets the internal caches used to speed up lookups on this index.
     * This is needed when the index gets modified in {@link io.openmg.trike.graphdb.database.management.ManagementSystem}.
     */
    public void resetCache();

    //TODO: Add in the future
    //public And getCondition();


}
