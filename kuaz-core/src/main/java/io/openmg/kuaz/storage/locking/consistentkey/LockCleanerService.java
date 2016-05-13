package io.openmg.kuaz.storage.locking.consistentkey;

import io.openmg.kuaz.storage.keycolumnvalue.StoreTransaction;
import io.openmg.kuaz.storage.util.KeyColumn;

import java.time.Instant;

public interface LockCleanerService {
    public void clean(KeyColumn target, Instant cutoff, StoreTransaction tx);
}
