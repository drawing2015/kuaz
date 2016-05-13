package io.openmg.kuaz.graphdb.relations;

import io.openmg.kuaz.graphdb.internal.InternalRelation;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public interface StandardRelation extends InternalRelation {

    public long getPreviousID();

    public void setPreviousID(long previousID);

}
