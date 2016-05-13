package io.openmg.trike.graphdb.database.serialize;

/**
 * Marks a {@link io.openmg.trike.core.attribute.AttributeSerializer} that requires a {@link io.openmg.trike.graphdb.database.serialize.Serializer}
 * to serialize the internal state. It is expected that the serializer is passed into this object upon initialization and before usage.
 * Furthermore, such serializers will convert the {@link io.openmg.trike.diskstorage.WriteBuffer} passed into the
 * {@link io.openmg.trike.core.attribute.AttributeSerializer}'s write methods to be cast to {@link io.openmg.trike.graphdb.database.serialize.DataOutput}.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface SerializerInjected {

    public void setSerializer(Serializer serializer);

}
