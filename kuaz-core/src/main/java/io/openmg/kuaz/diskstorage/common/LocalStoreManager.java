package io.openmg.kuaz.diskstorage.common;

import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.configuration.Configuration;
import io.openmg.kuaz.diskstorage.util.DirectoryUtil;

import java.io.File;

import static io.openmg.kuaz.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY;

/**
 * Abstract Store Manager used as the basis for local StoreManager implementations.
 * Simplifies common configuration management.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public abstract class LocalStoreManager extends AbstractStoreManager {

    protected final File directory;

    public LocalStoreManager(Configuration storageConfig) throws BackendException {
        super(storageConfig);
        String storageDir = storageConfig.get(STORAGE_DIRECTORY);
        if (null == storageDir) {
            directory = null;
        } else { 
            directory = DirectoryUtil.getOrCreateDataDirectory(storageDir);
        }
    }
}
