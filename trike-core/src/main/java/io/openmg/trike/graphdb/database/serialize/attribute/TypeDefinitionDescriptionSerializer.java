package io.openmg.trike.graphdb.database.serialize.attribute;

import com.google.common.base.Preconditions;
import io.openmg.trike.core.attribute.AttributeSerializer;
import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;
import io.openmg.trike.graphdb.database.serialize.DataOutput;
import io.openmg.trike.graphdb.database.serialize.Serializer;
import io.openmg.trike.graphdb.database.serialize.SerializerInjected;
import io.openmg.trike.graphdb.types.TypeDefinitionCategory;
import io.openmg.trike.graphdb.types.TypeDefinitionDescription;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class TypeDefinitionDescriptionSerializer implements AttributeSerializer<TypeDefinitionDescription>, SerializerInjected {

    private Serializer serializer;

    @Override
    public TypeDefinitionDescription read(ScanBuffer buffer) {
        TypeDefinitionCategory defCategory = serializer.readObjectNotNull(buffer, TypeDefinitionCategory.class);
        Object modifier = serializer.readClassAndObject(buffer);
        return new TypeDefinitionDescription(defCategory,modifier);
    }

    @Override
    public void write(WriteBuffer buffer, TypeDefinitionDescription attribute) {
        DataOutput out = (DataOutput)buffer;
        out.writeObjectNotNull(attribute.getCategory());
        out.writeClassAndObject(attribute.getModifier());
    }

    @Override
    public void setSerializer(Serializer serializer) {
        Preconditions.checkNotNull(serializer);
        this.serializer=serializer;
    }
}
