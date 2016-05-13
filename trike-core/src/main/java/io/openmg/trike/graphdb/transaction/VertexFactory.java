package io.openmg.trike.graphdb.transaction;

import io.openmg.trike.graphdb.internal.InternalVertex;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface VertexFactory {

    public InternalVertex getInternalVertex(long id);

}
