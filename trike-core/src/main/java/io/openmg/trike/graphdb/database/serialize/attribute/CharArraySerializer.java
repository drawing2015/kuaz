package io.openmg.trike.graphdb.database.serialize.attribute;

import io.openmg.trike.core.attribute.AttributeSerializer;
import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;

import java.lang.reflect.Array;

public class CharArraySerializer extends ArraySerializer implements AttributeSerializer<char[]> {


    @Override
    public char[] convert(Object value) {
        return convertInternal(value, char.class, Character.class);
    }

    @Override
    protected Object getArray(int length) {
        return new char[length];
    }

    @Override
    protected void setArray(Object array, int pos, Object value) {
        Array.setChar(array,pos,((Character)value));
    }

    //############### Serialization ###################

    @Override
    public char[] read(ScanBuffer buffer) {
        int length = getLength(buffer);
        if (length<0) return null;
        return buffer.getChars(length);
    }

    @Override
    public void write(WriteBuffer buffer, char[] attribute) {
        writeLength(buffer,attribute);
        if (attribute!=null) for (int i = 0; i < attribute.length; i++) buffer.putChar(attribute[i]);
    }
}