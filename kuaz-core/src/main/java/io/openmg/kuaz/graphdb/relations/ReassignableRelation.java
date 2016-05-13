package io.openmg.kuaz.graphdb.relations;

import io.openmg.kuaz.graphdb.internal.InternalVertex;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface ReassignableRelation {

    public void setVertexAt(int pos, InternalVertex vertex);

}
