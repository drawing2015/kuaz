package io.openmg.kuaz.graphdb.database.serialize.attribute;

import io.openmg.kuaz.core.attribute.AttributeSerializer;
import io.openmg.kuaz.diskstorage.ScanBuffer;
import io.openmg.kuaz.diskstorage.WriteBuffer;

import java.lang.reflect.Array;

public class FloatArraySerializer extends ArraySerializer implements AttributeSerializer<float[]> {

    @Override
    public float[] convert(Object value) {
        return convertInternal(value, float.class, Float.class);
    }

    @Override
    protected Object getArray(int length) {
        return new float[length];
    }

    @Override
    protected void setArray(Object array, int pos, Object value) {
        Array.setFloat(array,pos,((Float)value));
    }

    //############### Serialization ###################

    @Override
    public float[] read(ScanBuffer buffer) {
        int length = getLength(buffer);
        if (length<0) return null;
        return buffer.getFloats(length);
    }

    @Override
    public void write(WriteBuffer buffer, float[] attribute) {
        writeLength(buffer,attribute);
        if (attribute!=null) for (int i = 0; i < attribute.length; i++) buffer.putFloat(attribute[i]);
    }
}