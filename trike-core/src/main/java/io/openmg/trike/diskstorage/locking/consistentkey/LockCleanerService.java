package io.openmg.trike.diskstorage.locking.consistentkey;

import io.openmg.trike.diskstorage.keycolumnvalue.StoreTransaction;
import io.openmg.trike.diskstorage.util.KeyColumn;

import java.time.Instant;

public interface LockCleanerService {
    public void clean(KeyColumn target, Instant cutoff, StoreTransaction tx);
}
