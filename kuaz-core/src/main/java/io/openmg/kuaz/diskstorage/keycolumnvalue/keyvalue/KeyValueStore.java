package io.openmg.kuaz.diskstorage.keycolumnvalue.keyvalue;

import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.keycolumnvalue.StoreTransaction;

/**
 * Interface for a data store that represents data in the simple key->value data model where each key is uniquely
 * associated with a value. Keys and values are generic ByteBuffers.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public interface KeyValueStore {

    /**
     * Deletes the given key from the store.
     *
     * @param key
     * @param txh
     * @throws io.openmg.kuaz.diskstorage.BackendException
     */
    public void delete(StaticBuffer key, StoreTransaction txh) throws BackendException;

    /**
     * Returns the value associated with the given key.
     *
     * @param key
     * @param txh
     * @return
     * @throws io.openmg.kuaz.diskstorage.BackendException
     */
    public StaticBuffer get(StaticBuffer key, StoreTransaction txh) throws BackendException;

    /**
     * Returns true iff the store contains the given key, else false
     *
     * @param key
     * @param txh
     * @return
     * @throws io.openmg.kuaz.diskstorage.BackendException
     */
    public boolean containsKey(StaticBuffer key, StoreTransaction txh) throws BackendException;


    /**
     * Acquires a lock for the given key and expected value (null, if not value is expected).
     *
     * @param key
     * @param expectedValue
     * @param txh
     * @throws io.openmg.kuaz.diskstorage.BackendException
     */
    public void acquireLock(StaticBuffer key, StaticBuffer expectedValue, StoreTransaction txh) throws BackendException;

    /**
     * Returns the name of this store
     *
     * @return
     */
    public String getName();

    /**
     * Closes this store and releases its resources.
     *
     * @throws io.openmg.kuaz.diskstorage.BackendException
     */
    public void close() throws BackendException;

}
