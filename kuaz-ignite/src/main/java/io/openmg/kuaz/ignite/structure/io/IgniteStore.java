package io.openmg.kuaz.ignite.structure.io;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.Entry;
import com.thinkaurelius.titan.diskstorage.EntryList;
import com.thinkaurelius.titan.diskstorage.EntryMetaData;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KCVMutation;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyIterator;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyRangeQuery;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeySliceQuery;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.SliceQuery;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayBuffer;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayEntry;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayEntryList;

import org.apache.ignite.IgniteCache;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ranger Tsao(cao.zhifu@gmail.com)
 */
public class IgniteStore implements KeyColumnValueStore {

    //TODO could use a object instead of LinkedHashMap,then have column index
    private IgniteCache<ByteBuffer, LinkedHashMap<ByteBuffer, ByteBuffer>> cache;
    private KeyColumnValueStoreManager storeManager;

    public IgniteStore(IgniteCache<ByteBuffer, LinkedHashMap<ByteBuffer, ByteBuffer>> cache,
                       KeyColumnValueStoreManager storeManager) {
        this.cache = cache;
        this.storeManager = storeManager;
    }

    @Override
    public EntryList getSlice(KeySliceQuery query, StoreTransaction txh) throws BackendException {
        ensureOpen();
        Map<StaticBuffer, EntryList> result = getSlice(Arrays.asList(query.getKey()), query, txh);
        return Iterables.getOnlyElement(result.values(), EntryList.EMPTY_LIST);
    }

    @Override
    public Map<StaticBuffer, EntryList> getSlice(List<StaticBuffer> keys, SliceQuery query, StoreTransaction txh) throws BackendException {
        ensureOpen();
        Map<ByteBuffer, LinkedHashMap<ByteBuffer, ByteBuffer>> hints = cache.getAll(keys.stream().map(StaticBuffer::asByteBuffer).collect(Collectors.toSet()));
        Map<StaticBuffer, EntryList> result = new HashMap<>(hints.size());
        for (Map.Entry<ByteBuffer, LinkedHashMap<ByteBuffer, ByteBuffer>> entry : hints.entrySet()) {
            result.put(StaticArrayBuffer.of(entry.getKey()), buildEntryList(entry.getValue()));
        }
        return result;
    }

    private EntryList buildEntryList(LinkedHashMap<ByteBuffer, ByteBuffer> values) {
        return StaticArrayEntryList.ofByteBuffer(values.entrySet(), new StaticArrayEntry.GetColVal<Map.Entry<ByteBuffer, ByteBuffer>, ByteBuffer>() {
            @Override
            public ByteBuffer getColumn(Map.Entry<ByteBuffer, ByteBuffer> element) {
                return element.getKey();
            }

            @Override
            public ByteBuffer getValue(Map.Entry<ByteBuffer, ByteBuffer> element) {
                return element.getValue();
            }

            @Override
            public EntryMetaData[] getMetaSchema(Map.Entry<ByteBuffer, ByteBuffer> element) {
                return new EntryMetaData[0];
            }

            @Override
            public Object getMetaData(Map.Entry<ByteBuffer, ByteBuffer> element, EntryMetaData meta) {
                return null;
            }
        });
    }

    @Override
    public void mutate(StaticBuffer key, List<Entry> additions, List<StaticBuffer> deletions, StoreTransaction txh) throws BackendException {
        mutateMany(ImmutableMap.of(key, new KCVMutation(additions, deletions)), txh);
    }

    public void mutateMany(Map<StaticBuffer, KCVMutation> mutations, StoreTransaction txh) throws BackendException {
        storeManager.mutateMany(ImmutableMap.of(getName(), mutations), txh);
    }

    @Override
    public void acquireLock(StaticBuffer key, StaticBuffer column, StaticBuffer expectedValue, StoreTransaction txh) throws BackendException {
        throw new UnsupportedOperationException();//TODO ignite support lock
    }

    @Override
    public KeyIterator getKeys(KeyRangeQuery query, StoreTransaction txh) throws BackendException {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyIterator getKeys(SliceQuery query, StoreTransaction txh) throws BackendException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public void close() throws BackendException {
        ensureOpen();
        cache.close();
    }

    private void ensureOpen() throws BackendException {

    }

}
