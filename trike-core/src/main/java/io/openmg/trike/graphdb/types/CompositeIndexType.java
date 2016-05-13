package io.openmg.trike.graphdb.types;

import io.openmg.trike.core.Cardinality;
import io.openmg.trike.core.schema.ConsistencyModifier;
import io.openmg.trike.core.schema.SchemaStatus;

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
