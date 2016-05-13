package io.openmg.trike.diskstorage.util;

import com.google.common.base.Preconditions;
import io.openmg.trike.diskstorage.StaticBuffer;
import static io.openmg.trike.diskstorage.util.StaticArrayBuffer.*;
import io.openmg.trike.diskstorage.WriteBuffer;

import java.nio.ByteBuffer;


/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class WriteByteBuffer implements WriteBuffer {

    public static final int DEFAULT_CAPACITY = 64;
    public static final int MAX_BUFFER_CAPACITY = 128 * 1024 * 1024; //128 MB

    private ByteBuffer buffer;

    public WriteByteBuffer() {
        this(DEFAULT_CAPACITY);
    }

    public WriteByteBuffer(int capacity) {
        Preconditions.checkArgument(capacity<=MAX_BUFFER_CAPACITY,"Capacity exceeds max buffer capacity: %s",MAX_BUFFER_CAPACITY);
        buffer = ByteBuffer.allocate(capacity);
    }

    private void require(int size) {
        if (buffer.capacity()-buffer.position()<size) {
            //Need to resize
            int newcapacity = buffer.position() + size + buffer.capacity(); //extra capacity as buffer
            Preconditions.checkArgument(newcapacity<=MAX_BUFFER_CAPACITY,"Capacity exceeds max buffer capacity: %s",MAX_BUFFER_CAPACITY);
            ByteBuffer newBuffer = ByteBuffer.allocate(newcapacity);
            buffer.flip();
            newBuffer.put(buffer);
            buffer=newBuffer;
        }
    }

    @Override
    public WriteBuffer putLong(long val) {
        require(LONG_LEN);
        buffer.putLong(val);
        return this;
    }

    @Override
    public WriteBuffer putInt(int val) {
        require(INT_LEN);
        buffer.putInt(val);
        return this;
    }

    @Override
    public WriteBuffer putShort(short val) {
        require(SHORT_LEN);
        buffer.putShort(val);
        return this;
    }

    @Override
    public WriteBuffer putBoolean(boolean val) {
        return putByte((byte)(val?1:0));
    }

    @Override
    public WriteBuffer putByte(byte val) {
        require(BYTE_LEN);
        buffer.put(val);
        return this;
    }

    @Override
    public WriteBuffer putBytes(byte[] val) {
        require(BYTE_LEN*val.length);
        buffer.put(val);
        return this;
    }

    @Override
    public WriteBuffer putBytes(final StaticBuffer val) {
        require(BYTE_LEN*val.length());
        val.as(new Factory<Boolean>() {
            @Override
            public Boolean get(byte[] array, int offset, int limit) {
                buffer.put(array,offset,val.length());
                return Boolean.TRUE;
            }
        });
        return this;
    }

    @Override
    public WriteBuffer putChar(char val) {
        require(CHAR_LEN);
        buffer.putChar(val);
        return this;
    }

    @Override
    public WriteBuffer putFloat(float val) {
        require(FLOAT_LEN);
        buffer.putFloat(val);
        return this;
    }

    @Override
    public WriteBuffer putDouble(double val) {
        require(DOUBLE_LEN);
        buffer.putDouble(val);
        return this;
    }

    @Override
    public int getPosition() {
        return buffer.position();
    }

    @Override
    public StaticBuffer getStaticBuffer() {
        return getStaticBufferFlipBytes(0,0);
    }

    @Override
    public StaticBuffer getStaticBufferFlipBytes(int from, int to) {
        ByteBuffer b = buffer.duplicate();
        b.flip();
        Preconditions.checkArgument(from>=0 && from<=to);
        Preconditions.checkArgument(to<=b.limit());
        for (int i=from;i<to;i++) b.put(i,(byte)~b.get(i));
        return StaticArrayBuffer.of(b);
    }
}
