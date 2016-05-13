package io.openmg.trike.graphdb.database.serialize.attribute;

import com.google.common.base.Preconditions;
import io.openmg.trike.core.attribute.AttributeSerializer;
import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class ObjectSerializer implements AttributeSerializer<Object> {

    @Override
    public Object read(ScanBuffer buffer) {
        Preconditions.checkArgument(buffer.getByte()==1,"Invalid serialization state");
        return new Object();
    }

    @Override
    public void write(WriteBuffer buffer, Object attribute) {
        buffer.putByte((byte)1);
    }

}
