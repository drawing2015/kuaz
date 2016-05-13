package io.openmg.trike.diskstorage.keycolumnvalue.cache;

import io.openmg.trike.diskstorage.StaticBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface CachableStaticBuffer extends StaticBuffer {

    public int getCacheMarker();

}
