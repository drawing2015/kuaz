package io.openmg.kuaz.graphdb.database.serialize.attribute;

import io.openmg.kuaz.core.attribute.AttributeSerializer;
import io.openmg.kuaz.diskstorage.ScanBuffer;
import io.openmg.kuaz.diskstorage.WriteBuffer;

import java.time.Instant;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class InstantSerializer implements AttributeSerializer<Instant> {

    private final LongSerializer secondsSerializer = new LongSerializer();
    private final IntegerSerializer nanosSerializer = new IntegerSerializer();

    @Override
    public Instant read(ScanBuffer buffer) {
        long seconds = secondsSerializer.read(buffer);
        long nanos = nanosSerializer.read(buffer);
        return Instant.ofEpochSecond(seconds, nanos);
    }

    @Override
    public void write(WriteBuffer buffer, Instant attribute) {
        secondsSerializer.write(buffer, attribute.getEpochSecond());
        nanosSerializer.write(buffer,attribute.getNano());
    }
}
