package io.openmg.kuaz.storage.keycolumnvalue.keyvalue;

import io.openmg.kuaz.storage.BackendException;
import io.openmg.kuaz.storage.keycolumnvalue.StoreManager;

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
     * @throws io.openmg.kuaz.storage.BackendException
     *
     */
    public KeyValueStore openDatabase(String name) throws BackendException;



}
