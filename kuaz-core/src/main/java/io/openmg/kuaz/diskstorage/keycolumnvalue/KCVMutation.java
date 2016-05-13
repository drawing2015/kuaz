package io.openmg.kuaz.diskstorage.keycolumnvalue;

import com.google.common.base.Functions;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.Mutation;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.keycolumnvalue.cache.KCVEntryMutation;

import java.util.List;

/**
 * {@link Mutation} type for {@link KeyColumnValueStore}.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class KCVMutation extends Mutation<Entry,StaticBuffer> {

    public KCVMutation(List<Entry> additions, List<StaticBuffer> deletions) {
        super(additions, deletions);
    }

    @Override
    public void consolidate() {
        super.consolidate(KCVEntryMutation.ENTRY2COLUMN_FCT, Functions.<StaticBuffer>identity());
    }

    @Override
    public boolean isConsolidated() {
        return super.isConsolidated(KCVEntryMutation.ENTRY2COLUMN_FCT, Functions.<StaticBuffer>identity());
    }
}
