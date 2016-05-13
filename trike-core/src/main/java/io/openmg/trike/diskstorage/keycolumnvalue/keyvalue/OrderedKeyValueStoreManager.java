package io.openmg.trike.diskstorage.keycolumnvalue.keyvalue;

import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.keycolumnvalue.StoreTransaction;

import java.util.Map;

/**
 * A {@link KeyValueStoreManager} where the stores maintain keys in their natural order.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public interface OrderedKeyValueStoreManager extends KeyValueStoreManager {

    /**
     * Opens an ordered database by the given name. If the database does not exist, it is
     * created. If it has already been opened, the existing handle is returned.
     * <p/>
     *
     * @param name Name of database
     * @return Database Handle
     * @throws io.openmg.trike.diskstorage.BackendException
     *
     */
    @Override
    public OrderedKeyValueStore openDatabase(String name) throws BackendException;


    /**
     * Executes multiple mutations at once. Each store (identified by a string name) in the mutations map is associated
     * with a {@link KVMutation} that contains all the mutations for that particular store.
     *
     * This is an optional operation. Check {@link #getFeatures()} if it is supported by a particular implementation.
     *
     * @param mutations
     * @param txh
     * @throws io.openmg.trike.diskstorage.BackendException
     */
    public void mutateMany(Map<String, KVMutation> mutations, StoreTransaction txh) throws BackendException;

}
