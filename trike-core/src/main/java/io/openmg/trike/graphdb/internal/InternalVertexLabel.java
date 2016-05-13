package io.openmg.trike.graphdb.internal;

import io.openmg.trike.core.VertexLabel;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface InternalVertexLabel extends VertexLabel {

    public boolean hasDefaultConfiguration();

    public int getTTL();


}
