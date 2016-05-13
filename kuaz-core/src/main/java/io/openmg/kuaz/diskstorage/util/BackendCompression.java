package io.openmg.kuaz.diskstorage.util;

import io.openmg.kuaz.diskstorage.StaticBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public interface BackendCompression {

    public StaticBuffer compress(StaticBuffer value);

    public StaticBuffer decompress(StaticBuffer value);

    public static final BackendCompression NO_COMPRESSION = new BackendCompression() {
        @Override
        public StaticBuffer compress(StaticBuffer value) {
            return value;
        }

        @Override
        public StaticBuffer decompress(StaticBuffer value) {
            return value;
        }
    };

}
