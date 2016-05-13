package io.openmg.trike.diskstorage;

import io.openmg.trike.graphdb.database.serialize.DataOutput;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface LoggableTransaction extends BaseTransaction {

    public void logMutations(DataOutput out);

}
