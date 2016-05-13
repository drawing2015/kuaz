package io.openmg.kuaz.diskstorage.keycolumnvalue.ttl;

import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.keycolumnvalue.KCVSProxy;
import io.openmg.kuaz.diskstorage.keycolumnvalue.KeyColumnValueStore;
import io.openmg.kuaz.diskstorage.keycolumnvalue.StoreTransaction;

import java.util.List;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class TTLKCVS extends KCVSProxy {

    private final int ttl;

    public TTLKCVS(KeyColumnValueStore store, int ttl) {
        super(store);
        this.ttl = ttl;
    }

    @Override
    public void mutate(StaticBuffer key, List<Entry> additions, List<StaticBuffer> deletions, StoreTransaction txh) throws BackendException {
        TTLKCVSManager.applyTTL(additions, ttl);
        store.mutate(key, additions, deletions, unwrapTx(txh));
    }

}
