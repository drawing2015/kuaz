package io.openmg.trike.diskstorage.util;

import com.google.common.base.Preconditions;
import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.BaseTransactionConfig;
import io.openmg.trike.diskstorage.BaseTransactionConfigurable;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class DefaultTransaction implements BaseTransactionConfigurable {

    private final BaseTransactionConfig config;

    public DefaultTransaction(BaseTransactionConfig config) {
        Preconditions.checkNotNull(config);
        this.config = config;
    }

    @Override
    public BaseTransactionConfig getConfiguration() {
        return config;
    }

    @Override
    public void commit() throws BackendException {
    }

    @Override
    public void rollback() throws BackendException {
    }

}
