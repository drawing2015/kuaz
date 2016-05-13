package io.openmg.kuaz.graphdb.database.serialize.attribute;

import io.openmg.kuaz.core.attribute.AttributeSerializer;
import io.openmg.kuaz.storage.ScanBuffer;
import io.openmg.kuaz.storage.WriteBuffer;

import java.lang.reflect.Array;

public class ShortArraySerializer extends ArraySerializer implements AttributeSerializer<short[]> {

    @Override
    public short[] convert(Object value) {
        return convertInternal(value, short.class, Short.class);
    }

    @Override
    protected Object getArray(int length) {
        return new short[length];
    }

    @Override
    protected void setArray(Object array, int pos, Object value) {
        Array.setShort(array,pos,(Short)value);
    }

    //############### Serialization ###################

    @Override
    public short[] read(ScanBuffer buffer) {
        int length = getLength(buffer);
        if (length<0) return null;
        return buffer.getShorts(length);
    }

    @Override
    public void write(WriteBuffer buffer, short[] attribute) {
        writeLength(buffer,attribute);
        if (attribute!=null) for (int i = 0; i < attribute.length; i++) buffer.putShort(attribute[i]);
    }
}