package io.openmg.kuaz.diskstorage.common;

import io.openmg.kuaz.diskstorage.BaseTransactionConfig;

/**
 * Dummy transaction object that does nothing
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class NoOpStoreTransaction extends AbstractStoreTransaction {

    public NoOpStoreTransaction(BaseTransactionConfig config) {
        super(config);
    }
}
