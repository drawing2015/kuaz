package io.openmg.kuaz.diskstorage.keycolumnvalue.cache;

import io.openmg.kuaz.diskstorage.StaticBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface CachableStaticBuffer extends StaticBuffer {

    public int getCacheMarker();

}
