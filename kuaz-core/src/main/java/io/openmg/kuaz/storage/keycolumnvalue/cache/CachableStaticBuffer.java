package io.openmg.kuaz.storage.keycolumnvalue.cache;

import io.openmg.kuaz.storage.StaticBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface CachableStaticBuffer extends StaticBuffer {

    public int getCacheMarker();

}
