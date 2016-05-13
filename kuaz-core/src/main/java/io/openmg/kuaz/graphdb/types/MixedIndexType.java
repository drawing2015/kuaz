package io.openmg.kuaz.graphdb.types;

import io.openmg.kuaz.core.PropertyKey;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface MixedIndexType extends IndexType {

    public ParameterIndexField[] getFieldKeys();

    public ParameterIndexField getField(PropertyKey key);

    public String getStoreName();

}
