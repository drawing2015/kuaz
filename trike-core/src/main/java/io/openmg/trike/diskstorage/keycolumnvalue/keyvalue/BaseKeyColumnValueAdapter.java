package io.openmg.trike.diskstorage.keycolumnvalue.keyvalue;

import com.google.common.base.Preconditions;
import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.EntryList;
import io.openmg.trike.diskstorage.StaticBuffer;
import io.openmg.trike.diskstorage.keycolumnvalue.*;

import java.util.List;
import java.util.Map;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public abstract class BaseKeyColumnValueAdapter implements KeyColumnValueStore {

    private final KeyValueStore store;
    private boolean isClosed = false;

    public BaseKeyColumnValueAdapter(KeyValueStore store) {
        Preconditions.checkNotNull(store);
        this.store = store;
    }

    @Override
    public String getName() {
        return store.getName();
    }

    @Override
    public void close() throws BackendException {
        store.close();
        isClosed=true;
    }

    public boolean isClosed() {
        return isClosed;
    }


}
