package io.openmg.kuaz.storage.keycolumnvalue.keyvalue;

import io.openmg.kuaz.storage.StaticBuffer;

/**
 * Representation of a (key,value) pair.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */

public class KeyValueEntry {

    private final StaticBuffer key;
    private final StaticBuffer value;

    public KeyValueEntry(StaticBuffer key, StaticBuffer value) {
        assert key != null;
        assert value != null;
        this.key = key;
        this.value = value;
    }

    public StaticBuffer getKey() {
        return key;
    }


    public StaticBuffer getValue() {
        return value;
    }


}
