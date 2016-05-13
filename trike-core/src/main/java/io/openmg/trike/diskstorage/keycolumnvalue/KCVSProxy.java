package io.openmg.trike.diskstorage.keycolumnvalue;

import com.google.common.base.Preconditions;
import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.Entry;
import io.openmg.trike.diskstorage.EntryList;
import io.openmg.trike.diskstorage.StaticBuffer;
import io.openmg.trike.diskstorage.keycolumnvalue.cache.CacheTransaction;

import java.util.List;
import java.util.Map;

/**
 * Wraps a {@link io.openmg.trike.diskstorage.keycolumnvalue.KeyColumnValueStore} as a proxy as a basis for
 * other wrappers
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public class KCVSProxy implements KeyColumnValueStore {

    protected final KeyColumnValueStore store;

    public KCVSProxy(KeyColumnValueStore store) {
        Preconditions.checkArgument(store!=null);
        this.store = store;
    }

    protected StoreTransaction unwrapTx(StoreTransaction txh) {
        return txh;
    }

    @Override
    public void close() throws BackendException {
        store.close();
    }

    @Override
    public void acquireLock(StaticBuffer key, StaticBuffer column, StaticBuffer expectedValue,
                            StoreTransaction txh) throws BackendException {
        store.acquireLock(key,column,expectedValue,unwrapTx(txh));
    }

    @Override
    public KeyIterator getKeys(KeyRangeQuery keyQuery, StoreTransaction txh) throws BackendException {
        return store.getKeys(keyQuery, unwrapTx(txh));
    }

    @Override
    public KeyIterator getKeys(SliceQuery columnQuery, StoreTransaction txh) throws BackendException {
        return store.getKeys(columnQuery, unwrapTx(txh));
    }

    @Override
    public String getName() {
        return store.getName();
    }

    @Override
    public void mutate(StaticBuffer key, List<Entry> additions, List<StaticBuffer> deletions, StoreTransaction txh) throws BackendException {
        store.mutate(key, additions, deletions, unwrapTx(txh));
    }

    @Override
    public EntryList getSlice(KeySliceQuery query, StoreTransaction txh) throws BackendException {
        return store.getSlice(query, unwrapTx(txh));
    }

    @Override
    public Map<StaticBuffer,EntryList> getSlice(List<StaticBuffer> keys, SliceQuery query, StoreTransaction txh) throws BackendException {
        return store.getSlice(keys, query, unwrapTx(txh));
    }
}
