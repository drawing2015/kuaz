package io.openmg.kuaz.storage.keycolumnvalue.cache;

import io.openmg.kuaz.storage.StaticBuffer;
import io.openmg.kuaz.storage.keycolumnvalue.KeyColumnValueStore;

import java.util.List;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class NoKCVSCache extends KCVSCache {


    public NoKCVSCache(KeyColumnValueStore store) {
        super(store, null);
    }

    @Override
    public void clearCache() {
    }

    @Override
    protected void invalidate(StaticBuffer key, List<CachableStaticBuffer> entries) {
    }

}
