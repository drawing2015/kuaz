package io.openmg.kuaz.diskstorage.keycolumnvalue.keyvalue;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import io.openmg.kuaz.diskstorage.Mutation;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.keycolumnvalue.cache.KCVEntryMutation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * {@link Mutation} for {@link KeyValueStore}.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public class KVMutation extends Mutation<KeyValueEntry,StaticBuffer> {

    public KVMutation(List<KeyValueEntry> additions, List<StaticBuffer> deletions) {
        super(additions, deletions);
    }

    public KVMutation() {
        super();
    }

    private static Function<KeyValueEntry,StaticBuffer> ENTRY2KEY_FCT = new Function<KeyValueEntry, StaticBuffer>() {
        @Nullable
        @Override
        public StaticBuffer apply(@Nullable KeyValueEntry keyValueEntry) {
            return keyValueEntry.getKey();
        }
    };

    @Override
    public void consolidate() {
        super.consolidate(ENTRY2KEY_FCT, Functions.<StaticBuffer>identity());
    }

    @Override
    public boolean isConsolidated() {
        return super.isConsolidated(ENTRY2KEY_FCT, Functions.<StaticBuffer>identity());
    }

}
