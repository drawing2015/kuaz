package io.openmg.kuaz.diskstorage.keycolumnvalue;

import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.EntryList;
import io.openmg.kuaz.diskstorage.StaticBuffer;

import java.util.List;
import java.util.Map;

/**
 * Wraps a {@link KeyColumnValueStore} and throws exceptions when a mutation is attempted.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public class ReadOnlyKeyColumnValueStore extends KCVSProxy {

    public ReadOnlyKeyColumnValueStore(KeyColumnValueStore store) {
        super(store);
    }

    @Override
    public void acquireLock(StaticBuffer key, StaticBuffer column, StaticBuffer expectedValue,
                            StoreTransaction txh) throws BackendException {
        throw new UnsupportedOperationException("Cannot lock on a read-only store");
    }

    @Override
    public void mutate(StaticBuffer key, List<Entry> additions, List<StaticBuffer> deletions, StoreTransaction txh) throws BackendException {
        throw new UnsupportedOperationException("Cannot mutate a read-only store");
    }

}
