package io.openmg.kuaz.graphdb.types;

import io.openmg.kuaz.core.Cardinality;
import io.openmg.kuaz.core.schema.ConsistencyModifier;
import io.openmg.kuaz.core.schema.SchemaStatus;

/**
* @author Matthias Broecheler (me@matthiasb.com)
*/
public interface CompositeIndexType extends IndexType {

    public long getID();

    public IndexField[] getFieldKeys();

    public SchemaStatus getStatus();

    /*
     * single == unique,
     */
    public Cardinality getCardinality();

    public ConsistencyModifier getConsistencyModifier();
}
