package io.openmg.kuaz.ignite.structure.io;

import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.Entry;
import com.thinkaurelius.titan.diskstorage.EntryList;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyIterator;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyRangeQuery;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeySliceQuery;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.SliceQuery;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;

import org.apache.ignite.IgniteCache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ranger Tsao(cao.zhifu@gmail.com)
 */
public class IgniteStore implements KeyColumnValueStore {

    //TODO could use a object instead of LinkedHashMap
    private IgniteCache<StaticBuffer, LinkedHashMap<StaticBuffer, StaticBuffer>> cache;

    public IgniteStore(IgniteCache<StaticBuffer, LinkedHashMap<StaticBuffer, StaticBuffer>> cache) {
        this.cache = cache;
    }

    @Override
    public EntryList getSlice(KeySliceQuery query, StoreTransaction txh) throws BackendException {
        return null;
    }

    @Override
    public Map<StaticBuffer, EntryList> getSlice(List<StaticBuffer> keys, SliceQuery query, StoreTransaction txh) throws BackendException {
        return null;
    }

    @Override
    public void mutate(StaticBuffer key, List<Entry> additions, List<StaticBuffer> deletions, StoreTransaction txh) throws BackendException {

    }

    @Override
    public void acquireLock(StaticBuffer key, StaticBuffer column, StaticBuffer expectedValue, StoreTransaction txh) throws BackendException {

    }

    @Override
    public KeyIterator getKeys(KeyRangeQuery query, StoreTransaction txh) throws BackendException {
        return null;
    }

    @Override
    public KeyIterator getKeys(SliceQuery query, StoreTransaction txh) throws BackendException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void close() throws BackendException {

    }

}
