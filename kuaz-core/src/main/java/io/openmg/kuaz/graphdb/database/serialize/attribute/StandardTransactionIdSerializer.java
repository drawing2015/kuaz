package io.openmg.kuaz.graphdb.database.serialize.attribute;

import io.openmg.kuaz.core.attribute.AttributeSerializer;

import io.openmg.kuaz.storage.ScanBuffer;
import io.openmg.kuaz.storage.WriteBuffer;
import io.openmg.kuaz.graphdb.database.serialize.DataOutput;
import io.openmg.kuaz.graphdb.database.serialize.Serializer;
import io.openmg.kuaz.graphdb.database.serialize.SerializerInjected;
import io.openmg.kuaz.graphdb.log.StandardTransactionId;

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
