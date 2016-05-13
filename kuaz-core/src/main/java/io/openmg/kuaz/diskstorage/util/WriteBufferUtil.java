package io.openmg.kuaz.diskstorage.util;

import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.WriteBuffer;

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
