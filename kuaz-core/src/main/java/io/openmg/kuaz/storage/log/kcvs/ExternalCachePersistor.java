package io.openmg.kuaz.storage.log.kcvs;

import com.google.common.collect.Lists;
import io.openmg.kuaz.core.TitanException;
import io.openmg.kuaz.storage.BackendException;
import io.openmg.kuaz.storage.Entry;
import io.openmg.kuaz.storage.StaticBuffer;
import io.openmg.kuaz.storage.keycolumnvalue.cache.CacheTransaction;
import io.openmg.kuaz.storage.keycolumnvalue.cache.KCVSCache;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class ExternalCachePersistor implements ExternalPersistor {

    private final KCVSCache kcvs;
    private final CacheTransaction tx;

    public ExternalCachePersistor(KCVSCache kcvs, CacheTransaction tx) {
        this.kcvs = kcvs;
        this.tx = tx;
    }

    @Override
    public void add(StaticBuffer key, Entry cell) {
        try {
            kcvs.mutateEntries(key, Lists.newArrayList(cell), KCVSCache.NO_DELETIONS,tx);
        } catch (BackendException e) {
            throw new TitanException("Unexpected storage exception in log persistence against cache",e);
        }
    }
}
