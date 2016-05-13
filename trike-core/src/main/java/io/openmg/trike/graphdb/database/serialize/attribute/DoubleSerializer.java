package io.openmg.trike.graphdb.database.serialize.attribute;

import com.google.common.base.Preconditions;
import io.openmg.trike.core.attribute.AttributeSerializer;
import io.openmg.trike.diskstorage.ScanBuffer;
import io.openmg.trike.diskstorage.WriteBuffer;
import io.openmg.trike.graphdb.database.serialize.AttributeUtil;
import io.openmg.trike.graphdb.database.serialize.OrderPreservingSerializer;
import io.openmg.trike.util.encoding.NumericUtils;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class DoubleSerializer implements OrderPreservingSerializer<Double> {

    private final LongSerializer longs = new LongSerializer();

    @Override
    public Double convert(Object value) {
        if (value instanceof Number) {
            return Double.valueOf(((Number) value).doubleValue());
        } else if (value instanceof String) {
            return Double.valueOf(Double.parseDouble((String) value));
        } else return null;
    }

    @Override
    public Double read(ScanBuffer buffer) {
        return buffer.getDouble();
    }

    @Override
    public void write(WriteBuffer buffer, Double attribute) {
        buffer.putDouble(attribute.doubleValue());
    }

    @Override
    public Double readByteOrder(ScanBuffer buffer) {
        return NumericUtils.sortableLongToDouble(longs.readByteOrder(buffer));
    }

    @Override
    public void writeByteOrder(WriteBuffer buffer, Double attribute) {
        longs.writeByteOrder(buffer, NumericUtils.doubleToSortableLong(attribute));
    }
}
