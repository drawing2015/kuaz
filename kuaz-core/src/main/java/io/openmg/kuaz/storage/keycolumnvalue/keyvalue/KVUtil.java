package io.openmg.kuaz.storage.keycolumnvalue.keyvalue;

import io.openmg.kuaz.storage.*;
import io.openmg.kuaz.storage.keycolumnvalue.StoreTransaction;
import io.openmg.kuaz.storage.util.RecordIterator;
import io.openmg.kuaz.storage.util.StaticArrayEntry;
import io.openmg.kuaz.storage.util.StaticArrayEntryList;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Utility methods for interacting with {@link KeyValueStore}.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public class KVUtil {
    public static final RecordIterator<KeyValueEntry> EMPTY_ITERATOR = new RecordIterator<KeyValueEntry>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public KeyValueEntry next() {
            throw new NoSuchElementException();
        }

        @Override
        public void close() {
            
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };

    public static EntryList getSlice(OrderedKeyValueStore store, StaticBuffer keyStart, StaticBuffer keyEnd, StoreTransaction txh) throws BackendException {
        return convert(store.getSlice(new KVQuery(keyStart,keyEnd), txh));
    }

    public static EntryList getSlice(OrderedKeyValueStore store, StaticBuffer keyStart, StaticBuffer keyEnd, int limit, StoreTransaction txh) throws BackendException {
        return convert(store.getSlice(new KVQuery(keyStart, keyEnd, limit), txh));
    }

    public static EntryList convert(RecordIterator<KeyValueEntry> iter) throws BackendException {
        try {
            return StaticArrayEntryList.ofStaticBuffer(iter, KVEntryGetter.INSTANCE);
        } finally {
            try {
                iter.close();
            } catch (IOException e) {
                throw new TemporaryBackendException(e);
            }
        }
    }

    private static enum KVEntryGetter implements StaticArrayEntry.GetColVal<KeyValueEntry,StaticBuffer> {
        INSTANCE;

        @Override
        public StaticBuffer getColumn(KeyValueEntry element) {
            return element.getKey();
        }

        @Override
        public StaticBuffer getValue(KeyValueEntry element) {
            return element.getValue();
        }

        @Override
        public EntryMetaData[] getMetaSchema(KeyValueEntry element) {
            return StaticArrayEntry.EMPTY_SCHEMA;
        }

        @Override
        public Object getMetaData(KeyValueEntry element, EntryMetaData meta) {
            throw new UnsupportedOperationException("Unsupported meta data: " + meta);
        }
    };

}
