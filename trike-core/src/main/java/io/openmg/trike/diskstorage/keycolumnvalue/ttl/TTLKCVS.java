package io.openmg.trike.diskstorage.keycolumnvalue.ttl;

import io.openmg.trike.diskstorage.BackendException;
import io.openmg.trike.diskstorage.Entry;
import io.openmg.trike.diskstorage.StaticBuffer;
import io.openmg.trike.diskstorage.keycolumnvalue.KCVSProxy;
import io.openmg.trike.diskstorage.keycolumnvalue.KeyColumnValueStore;
import io.openmg.trike.diskstorage.keycolumnvalue.StoreTransaction;

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
