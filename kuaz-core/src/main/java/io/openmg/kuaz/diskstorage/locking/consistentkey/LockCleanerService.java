package io.openmg.kuaz.diskstorage.locking.consistentkey;

import io.openmg.kuaz.diskstorage.keycolumnvalue.StoreTransaction;
import io.openmg.kuaz.diskstorage.util.KeyColumn;

import java.time.Instant;

public interface LockCleanerService {
    public void clean(KeyColumn target, Instant cutoff, StoreTransaction tx);
}
