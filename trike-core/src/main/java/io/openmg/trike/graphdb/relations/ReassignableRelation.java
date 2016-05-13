package io.openmg.trike.graphdb.relations;

import io.openmg.trike.graphdb.internal.InternalVertex;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface ReassignableRelation {

    public void setVertexAt(int pos, InternalVertex vertex);

}
