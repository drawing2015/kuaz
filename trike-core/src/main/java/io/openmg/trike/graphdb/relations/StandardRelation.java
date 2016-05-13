package io.openmg.trike.graphdb.relations;

import io.openmg.trike.graphdb.internal.InternalRelation;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public interface StandardRelation extends InternalRelation {

    public long getPreviousID();

    public void setPreviousID(long previousID);

}
