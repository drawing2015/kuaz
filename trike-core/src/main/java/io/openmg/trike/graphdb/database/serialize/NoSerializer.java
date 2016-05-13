package io.openmg.trike.graphdb.database.serialize;

import io.openmg.trike.core.attribute.AttributeSerializer;
import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class NoSerializer<V> implements AttributeSerializer<V> {

    private final Class<V> datatype;

    public NoSerializer(Class<V> datatype) {
        this.datatype = datatype;
    }

    private final IllegalArgumentException error() {
        return new IllegalArgumentException("Serializing objects of type ["+datatype+"] is not supported");
    }

    @Override
    public V read(ScanBuffer buffer) {
        throw error();
    }

    @Override
    public void write(WriteBuffer buffer, V attribute) {
        throw error();
    }

    @Override
    public void verifyAttribute(V value) {
        throw error();
    }

    @Override
    public V convert(Object value) {
        throw error();
    }

}
