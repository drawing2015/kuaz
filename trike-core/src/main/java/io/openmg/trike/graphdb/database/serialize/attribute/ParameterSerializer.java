package io.openmg.trike.graphdb.database.serialize.attribute;

import com.google.common.base.Preconditions;
import io.openmg.trike.core.attribute.AttributeSerializer;
import io.openmg.trike.core.schema.Parameter;
import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;
import io.openmg.trike.graphdb.database.serialize.DataOutput;
import io.openmg.trike.graphdb.database.serialize.Serializer;
import io.openmg.trike.graphdb.database.serialize.SerializerInjected;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class ParameterSerializer implements AttributeSerializer<Parameter>, SerializerInjected {

    private Serializer serializer;

    @Override
    public Parameter read(ScanBuffer buffer) {
        String key = serializer.readObjectNotNull(buffer,String.class);
        Object value = serializer.readClassAndObject(buffer);
        return new Parameter(key,value);
    }

    @Override
    public void write(WriteBuffer buffer, Parameter attribute) {
        DataOutput out = (DataOutput)buffer;
        out.writeObjectNotNull(attribute.key());
        out.writeClassAndObject(attribute.value());
    }


    @Override
    public void setSerializer(Serializer serializer) {
        Preconditions.checkNotNull(serializer);
        this.serializer=serializer;
    }
}
