package io.openmg.kuaz.graphdb.internal;

import io.openmg.kuaz.core.VertexLabel;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface InternalVertexLabel extends VertexLabel {

    public boolean hasDefaultConfiguration();

    public int getTTL();


}
