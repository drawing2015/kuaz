package io.openmg.trike.diskstorage.keycolumnvalue.keyvalue;

import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.keycolumnvalue.StoreManager;

/**
 * {@link StoreManager} for {@link KeyValueStore}.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public interface KeyValueStoreManager extends StoreManager {

    /**
     * Opens a key-value database by the given name. If the database does not exist, it is
     * created. If it has already been opened, the existing handle is returned.
     * <p/>
     *
     * @param name Name of database
     * @return Database Handle
     * @throws io.openmg.trike.diskstorage.BackendException
     *
     */
    public KeyValueStore openDatabase(String name) throws BackendException;



}
