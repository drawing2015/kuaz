package io.openmg.kuaz.graphdb.transaction;

import io.openmg.kuaz.graphdb.internal.InternalVertex;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface VertexFactory {

    public InternalVertex getInternalVertex(long id);

}
