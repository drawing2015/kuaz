package io.openmg.kuaz.graphdb.database.serialize.attribute;

import com.google.common.base.Preconditions;
import io.openmg.kuaz.core.attribute.AttributeSerializer;
import io.openmg.kuaz.storage.ScanBuffer;
import io.openmg.kuaz.storage.WriteBuffer;

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
