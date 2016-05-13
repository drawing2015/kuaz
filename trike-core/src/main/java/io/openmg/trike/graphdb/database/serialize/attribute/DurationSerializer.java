package io.openmg.trike.graphdb.database.serialize.attribute;

import io.openmg.trike.core.attribute.AttributeSerializer;
import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;

import java.time.Duration;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class DurationSerializer implements AttributeSerializer<Duration> {

    private final LongSerializer secondsSerializer = new LongSerializer();
    private final IntegerSerializer nanosSerializer = new IntegerSerializer();

    @Override
    public Duration read(ScanBuffer buffer) {
        long seconds = secondsSerializer.read(buffer);
        long nanos = nanosSerializer.read(buffer);
        return Duration.ofSeconds(seconds, nanos);
    }

    @Override
    public void write(WriteBuffer buffer, Duration attribute) {
        secondsSerializer.write(buffer,attribute.getSeconds());
        nanosSerializer.write(buffer,attribute.getNano());
    }
}
