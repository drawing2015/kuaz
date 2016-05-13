package io.openmg.trike.diskstorage.util;

import io.openmg.trike.diskstorage.StaticBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class WriteBufferUtil {


    public static final WriteBuffer put(WriteBuffer out, byte[] bytes) {
        for (int i=0;i<bytes.length;i++) out.putByte(bytes[i]);
        return out;
    }

    public static final WriteBuffer put(WriteBuffer out, StaticBuffer in) {
        for (int i=0;i<in.length();i++) out.putByte(in.getByte(i));
        return out;
    }

}
