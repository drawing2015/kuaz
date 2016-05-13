package io.openmg.kuaz.graphdb.database.serialize;

import io.openmg.kuaz.storage.StaticBuffer;
import io.openmg.kuaz.storage.WriteBuffer;

public interface DataOutput extends WriteBuffer {

    @Override
    public DataOutput putLong(long val);

    @Override
    public DataOutput putInt(int val);

    @Override
    public DataOutput putShort(short val);

    @Override
    public DataOutput putByte(byte val);

    @Override
    public DataOutput putBytes(byte[] val);

    @Override
    public DataOutput putBytes(StaticBuffer val);

    @Override
    public DataOutput putChar(char val);

    @Override
    public DataOutput putFloat(float val);

    @Override
    public DataOutput putDouble(double val);

    public DataOutput writeObject(Object object, Class<?> type);

    public DataOutput writeObjectByteOrder(Object object, Class<?> type);

    public DataOutput writeObjectNotNull(Object object);

    public DataOutput writeClassAndObject(Object object);

}
