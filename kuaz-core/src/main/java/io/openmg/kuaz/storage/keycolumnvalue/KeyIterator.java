package io.openmg.kuaz.storage.keycolumnvalue;

import io.openmg.kuaz.storage.Entry;
import io.openmg.kuaz.storage.StaticBuffer;
import io.openmg.kuaz.storage.util.RecordIterator;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public interface KeyIterator extends RecordIterator<StaticBuffer> {

    /**
     * Returns an iterator over all entries associated with the current
     * key that match the column range specified in the query.
     * </p>
     * Closing the returned sub-iterator has no effect on this iterator.
     *
     * Calling {@link #next()} might close previously returned RecordIterators
     * depending on the implementation, hence it is important to iterate over
     * (and close) the RecordIterator before calling {@link #next()} or {@link #hasNext()}.
     *
     * @return
     */
    public RecordIterator<Entry> getEntries();

}
