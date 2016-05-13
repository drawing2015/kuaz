package io.openmg.kuaz.storage.common;

import io.openmg.kuaz.storage.BaseTransactionConfig;

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
