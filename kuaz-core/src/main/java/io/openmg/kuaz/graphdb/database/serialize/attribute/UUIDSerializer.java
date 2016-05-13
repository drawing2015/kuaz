package io.openmg.kuaz.graphdb.database.serialize.attribute;

import com.google.common.base.Preconditions;
import io.openmg.kuaz.core.attribute.AttributeSerializer;
import io.openmg.kuaz.storage.ScanBuffer;
import io.openmg.kuaz.storage.WriteBuffer;

import java.util.UUID;

/**
 *  @author Bryn Cooke (bryn.cooke@datastax.com)
 */
public class UUIDSerializer implements AttributeSerializer<UUID>  {

    @Override
    public UUID read(ScanBuffer buffer) {
        long mostSignificantBits = buffer.getLong();
        long leastSignificantBits = buffer.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    @Override
    public void write(WriteBuffer buffer, UUID attribute) {
        buffer.putLong(attribute.getMostSignificantBits());
        buffer.putLong(attribute.getLeastSignificantBits());
    }

    @Override
    public UUID convert(Object value) {
        Preconditions.checkNotNull(value);
        if(value instanceof String){
            return UUID.fromString((String) value);
        }
        return null;
    }
}
