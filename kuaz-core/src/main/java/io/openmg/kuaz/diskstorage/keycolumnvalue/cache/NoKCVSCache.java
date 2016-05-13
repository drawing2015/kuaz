package io.openmg.kuaz.diskstorage.keycolumnvalue.cache;

import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.EntryList;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.keycolumnvalue.KeyColumnValueStore;
import io.openmg.kuaz.diskstorage.keycolumnvalue.KeySliceQuery;
import io.openmg.kuaz.diskstorage.keycolumnvalue.SliceQuery;
import io.openmg.kuaz.diskstorage.keycolumnvalue.StoreTransaction;

import java.util.List;
import java.util.Map;

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
