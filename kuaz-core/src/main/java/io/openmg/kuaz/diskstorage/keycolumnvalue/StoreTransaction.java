package io.openmg.kuaz.diskstorage.keycolumnvalue;

import io.openmg.kuaz.diskstorage.BaseTransactionConfig;
import io.openmg.kuaz.diskstorage.BaseTransactionConfigurable;

/**
 * A transaction handle uniquely identifies a transaction on the storage backend.
 * <p/>
 * All modifications to the storage backend must occur within the context of a single
 * transaction. Such a transaction is identified to the Titan middleware by a StoreTransaction.
 * Graph transactions rely on the existence of a storage backend transaction.
 * <p/>
 * Note, that a StoreTransaction by itself does not provide any isolation or consistency guarantees (e.g. ACID).
 * Graph Transactions can only extend such guarantees if they are supported by the respective storage backend.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public interface StoreTransaction extends BaseTransactionConfigurable {


}
