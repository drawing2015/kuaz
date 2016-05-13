package io.openmg.kuaz.diskstorage;

import io.openmg.kuaz.graphdb.database.serialize.DataOutput;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface LoggableTransaction extends BaseTransaction {

    public void logMutations(DataOutput out);

}
