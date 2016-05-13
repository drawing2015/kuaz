package io.openmg.kuaz.diskstorage.keycolumnvalue.cache;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.openmg.kuaz.diskstorage.BackendException;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.EntryList;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.keycolumnvalue.*;
import io.openmg.kuaz.diskstorage.util.CacheMetricsAction;
import io.openmg.kuaz.util.stats.MetricManager;

import java.util.List;
import java.util.Map;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public abstract class KCVSCache extends KCVSProxy {

    public static final List<Entry> NO_DELETIONS = ImmutableList.of();

    private final String metricsName;
    private final boolean validateKeysOnly = true;

    protected KCVSCache(KeyColumnValueStore store, String metricsName) {
        super(store);
        this.metricsName = metricsName;
    }

    protected boolean hasValidateKeysOnly() {
        return validateKeysOnly;
    }

    protected void incActionBy(int by, CacheMetricsAction action, StoreTransaction txh) {
        assert by>=1;
        if (metricsName!=null && txh.getConfiguration().hasGroupName()) {
            MetricManager.INSTANCE.getCounter(txh.getConfiguration().getGroupName(), metricsName, action.getName()).inc(by);
        }
    }

    public abstract void clearCache();

    protected abstract void invalidate(StaticBuffer key, List<CachableStaticBuffer> entries);

    @Override
    public void mutate(StaticBuffer key, List<Entry> additions, List<StaticBuffer> deletions, StoreTransaction txh) throws BackendException {
        throw new UnsupportedOperationException("Only supports mutateEntries()");
    }

    public void mutateEntries(StaticBuffer key, List<Entry> additions, List<Entry> deletions, StoreTransaction txh) throws BackendException {
        assert txh instanceof CacheTransaction;
        ((CacheTransaction) txh).mutate(this, key, additions, deletions);
    }

    @Override
    protected final StoreTransaction unwrapTx(StoreTransaction txh) {
        assert txh instanceof CacheTransaction;
        return ((CacheTransaction) txh).getWrappedTransaction();
    }

    public EntryList getSliceNoCache(KeySliceQuery query, StoreTransaction txh) throws BackendException {
        return store.getSlice(query,unwrapTx(txh));
    }

    public Map<StaticBuffer, EntryList> getSliceNoCache(List<StaticBuffer> keys, SliceQuery query, StoreTransaction txh) throws BackendException {
        return store.getSlice(keys,query,unwrapTx(txh));
    }

}
