package io.openmg.kuaz.diskstorage.common;

import com.google.common.base.Preconditions;
import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.BaseTransactionConfig;
import io.openmg.kuaz.diskstorage.keycolumnvalue.StoreTransaction;

/**
 * Abstract implementation of {@link StoreTransaction} to be used as the basis for more specific implementations.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public abstract class AbstractStoreTransaction implements StoreTransaction {

    private final BaseTransactionConfig config;

    public AbstractStoreTransaction(BaseTransactionConfig config) {
        Preconditions.checkNotNull(config);
        this.config = config;
    }

    @Override
    public void commit() throws BackendException {
    }

    @Override
    public void rollback() throws BackendException {
    }

    @Override
    public BaseTransactionConfig getConfiguration() {
        return config;
    }

}
