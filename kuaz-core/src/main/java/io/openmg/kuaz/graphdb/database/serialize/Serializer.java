package io.openmg.kuaz.graphdb.database.serialize;

import io.openmg.kuaz.storage.ScanBuffer;

import java.io.Closeable;

public interface Serializer extends AttributeHandler, Closeable {

    public Object readClassAndObject(ScanBuffer buffer);

    public <T> T readObject(ScanBuffer buffer, Class<T> type);

    public <T> T readObjectByteOrder(ScanBuffer buffer, Class<T> type);

    public <T> T readObjectNotNull(ScanBuffer buffer, Class<T> type);

    public DataOutput getDataOutput(int initialCapacity);

}
