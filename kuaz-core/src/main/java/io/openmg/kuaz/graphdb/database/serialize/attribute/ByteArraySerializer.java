package io.openmg.kuaz.graphdb.database.serialize.attribute;

import java.lang.reflect.Array;

import io.openmg.kuaz.core.attribute.AttributeSerializer;
import io.openmg.kuaz.storage.ScanBuffer;
import io.openmg.kuaz.storage.WriteBuffer;

public class ByteArraySerializer extends ArraySerializer implements AttributeSerializer<byte[]> {

    @Override
    public byte[] convert(Object value) {
        return convertInternal(value, byte.class, Byte.class);
    }

    @Override
    protected Object getArray(int length) {
        return new byte[length];
    }

    @Override
    protected void setArray(Object array, int pos, Object value) {
        Array.setByte(array,pos,((Byte)value));
    }

    //############### Serialization ###################

    @Override
    public byte[] read(ScanBuffer buffer) {
        int length = getLength(buffer);
        if (length<0) return null;
        return buffer.getBytes(length);
    }

    @Override
    public void write(WriteBuffer buffer, byte[] attribute) {
        writeLength(buffer,attribute);
        if (attribute!=null) for (int i = 0; i < attribute.length; i++) buffer.putByte(attribute[i]);
    }
}