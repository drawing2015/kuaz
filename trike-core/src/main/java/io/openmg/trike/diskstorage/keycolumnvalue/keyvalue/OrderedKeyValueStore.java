package io.openmg.trike.diskstorage.keycolumnvalue.keyvalue;

import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.StaticBuffer;
import io.openmg.trike.diskstorage.keycolumnvalue.StoreTransaction;
import io.openmg.trike.diskstorage.util.RecordIterator;

import java.util.List;
import java.util.Map;

/**
 * A {@link KeyValueStore} where the keys are ordered such that keys can be retrieved in order.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public interface OrderedKeyValueStore extends KeyValueStore {

    /**
     * Inserts the given key-value pair into the store. If the key already exists, its value is overwritten by the given one.
     *
     * @param key
     * @param value
     * @param txh
     * @throws io.openmg.trike.diskstorage.BackendException
     */
    public void insert(StaticBuffer key, StaticBuffer value, StoreTransaction txh) throws BackendException;

    /**
     * Returns a list of all Key-value pairs ({@link KeyValueEntry} where the keys satisfy the given {@link KVQuery}.
     * That means, the key lies between the query's start and end buffers, satisfied the filter condition (if any) and the position
     * of the result in the result list iterator is less than the given limit.
     *
     * The operation is executed inside the context of the given transaction.
     *
     * @param query
     * @param txh
     * @return
     * @throws io.openmg.trike.diskstorage.BackendException
     */
    public RecordIterator<KeyValueEntry> getSlice(KVQuery query, StoreTransaction txh) throws BackendException;


    /**
     * Like {@link #getSlice(KVQuery, io.openmg.trike.diskstorage.keycolumnvalue.StoreTransaction)} but executes
     * all of the given queries at once and returns a map of all the result sets of each query.
     *
     * Only supported when the given store implementation supports multi-query, i.e.
     * {@link io.openmg.trike.diskstorage.keycolumnvalue.StoreFeatures#hasMultiQuery()} return true. Otherwise
     * this method may throw a {@link UnsupportedOperationException}.
     *
     * @param queries
     * @param txh
     * @return
     * @throws BackendException
     */
    public Map<KVQuery,RecordIterator<KeyValueEntry>> getSlices(List<KVQuery> queries, StoreTransaction txh) throws BackendException;

}
