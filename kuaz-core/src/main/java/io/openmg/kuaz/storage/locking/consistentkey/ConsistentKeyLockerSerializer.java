package io.openmg.kuaz.storage.locking.consistentkey;

import io.openmg.kuaz.storage.ReadBuffer;
import io.openmg.kuaz.storage.StaticBuffer;
import io.openmg.kuaz.storage.WriteBuffer;
import io.openmg.kuaz.storage.util.StaticArrayBuffer;
import io.openmg.kuaz.storage.util.WriteBufferUtil;
import io.openmg.kuaz.storage.util.WriteByteBuffer;
import io.openmg.kuaz.storage.util.time.TimestampProvider;

import java.time.Instant;

/**
 * Translate locking coordinates and metadata (data keys, data columns, data
 * values, timestamps, and rids) into keys, columns, and values compatible with
 * {@link ConsistentKeyLocker} and vice-versa.
 */
public class ConsistentKeyLockerSerializer {
     
    public StaticBuffer toLockKey(StaticBuffer key, StaticBuffer column) {
        WriteBuffer b = new WriteByteBuffer(key.length() + column.length() + 4);
        b.putInt(key.length());
        WriteBufferUtil.put(b,key);
        WriteBufferUtil.put(b,column);
        return b.getStaticBuffer();
    }
    
    public StaticBuffer toLockCol(Instant ts, StaticBuffer rid, TimestampProvider provider) {
        WriteBuffer b = new WriteByteBuffer(rid.length() + 8);
        b.putLong(provider.getTime(ts));
        WriteBufferUtil.put(b, rid);
        return b.getStaticBuffer();
    }
    
    public TimestampRid fromLockColumn(StaticBuffer lockKey, TimestampProvider provider) {
        ReadBuffer r = lockKey.asReadBuffer();
        int len = r.length();
        long tsNS = r.getLong();
        len -= 8;
        byte[] curRid = new byte[len];
        for (int i = 0; r.hasRemaining(); i++) {
            curRid[i] = r.getByte();
        }
        StaticBuffer rid = new StaticArrayBuffer(curRid);
        Instant time = provider.getTime(tsNS);
        return new TimestampRid(time, rid);
    }
}
