package io.openmg.trike.graphdb.database.serialize.attribute;

import io.openmg.trike.core.attribute.AttributeSerializer;

import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;
import io.openmg.trike.graphdb.database.serialize.DataOutput;
import io.openmg.trike.graphdb.database.serialize.Serializer;
import io.openmg.trike.graphdb.database.serialize.SerializerInjected;
import io.openmg.trike.graphdb.log.StandardTransactionId;

import java.time.Instant;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class StandardTransactionIdSerializer implements AttributeSerializer<StandardTransactionId>, SerializerInjected {

    private Serializer serializer;

    @Override
    public StandardTransactionId read(ScanBuffer buffer) {
        return new StandardTransactionId(serializer.readObjectNotNull(buffer,String.class),
                serializer.readObjectNotNull(buffer,Long.class),
                serializer.readObjectNotNull(buffer,Instant.class));
    }

    @Override
    public void write(WriteBuffer buffer, StandardTransactionId attribute) {
        DataOutput out = (DataOutput)buffer;
        out.writeObjectNotNull(attribute.getInstanceId());
        out.writeObjectNotNull(attribute.getTransactionId());
        out.writeObjectNotNull(attribute.getTransactionTime());
    }

    @Override
    public void setSerializer(Serializer serializer) {
        this.serializer=serializer;
    }
}
