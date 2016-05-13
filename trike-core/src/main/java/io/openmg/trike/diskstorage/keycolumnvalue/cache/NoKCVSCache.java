package io.openmg.trike.diskstorage.keycolumnvalue.cache;

import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.EntryList;
import io.openmg.trike.diskstorage.StaticBuffer;
import io.openmg.trike.diskstorage.keycolumnvalue.KeyColumnValueStore;
import io.openmg.trike.diskstorage.keycolumnvalue.KeySliceQuery;
import io.openmg.trike.diskstorage.keycolumnvalue.SliceQuery;
import io.openmg.trike.diskstorage.keycolumnvalue.StoreTransaction;

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
