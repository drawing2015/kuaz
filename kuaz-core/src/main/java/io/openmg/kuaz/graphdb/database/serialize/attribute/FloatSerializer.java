package io.openmg.kuaz.graphdb.database.serialize.attribute;

import io.openmg.kuaz.storage.ScanBuffer;
import io.openmg.kuaz.storage.WriteBuffer;
import io.openmg.kuaz.graphdb.database.serialize.OrderPreservingSerializer;
import io.openmg.kuaz.util.encoding.NumericUtils;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class FloatSerializer implements OrderPreservingSerializer<Float> {

    private final IntegerSerializer ints = new IntegerSerializer();

    @Override
    public Float convert(Object value) {
        if (value instanceof Number) {
            double d = ((Number)value).doubleValue();
            if (d<-Float.MAX_VALUE || d>Float.MAX_VALUE) throw new IllegalArgumentException("Value too large for float: " + value);
            return Float.valueOf((float)d);
        } else if (value instanceof String) {
            return Float.valueOf(Float.parseFloat((String) value));
        } else return null;
    }

    @Override
    public Float read(ScanBuffer buffer) {
        return buffer.getFloat();
    }

    @Override
    public void write(WriteBuffer buffer, Float attribute) {
        buffer.putFloat(attribute.floatValue());
    }

    @Override
    public Float readByteOrder(ScanBuffer buffer) {
        return NumericUtils.sortableIntToFloat(ints.readByteOrder(buffer));
    }

    @Override
    public void writeByteOrder(WriteBuffer buffer, Float attribute) {
        ints.writeByteOrder(buffer, NumericUtils.floatToSortableInt(attribute));
    }
}
