package io.openmg.kuaz.storage.locking.consistentkey;

import io.openmg.kuaz.storage.StaticBuffer;

import java.time.Instant;

public class TimestampRid {
    
    private final Instant timestamp;
    private final StaticBuffer rid;
    
    public TimestampRid(Instant timestamp, StaticBuffer rid) {
        this.timestamp = timestamp;
        this.rid = rid;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public StaticBuffer getRid() {
        return rid;
    }
}
