package io.openmg.kuaz.diskstorage.keycolumnvalue.cache;

import com.google.common.base.Function;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.Mutation;
import io.openmg.kuaz.diskstorage.StaticBuffer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class KCVEntryMutation extends Mutation<Entry,Entry> {

    public KCVEntryMutation(List<Entry> additions, List<Entry> deletions) {
        super(additions, deletions);
    }

    public static final Function<Entry,StaticBuffer> ENTRY2COLUMN_FCT = new Function<Entry, StaticBuffer>() {
        @Nullable
        @Override
        public StaticBuffer apply(@Nullable Entry entry) {
            return entry.getColumn();
        }
    };

    @Override
    public void consolidate() {
        super.consolidate(ENTRY2COLUMN_FCT,ENTRY2COLUMN_FCT);
    }

    @Override
    public boolean isConsolidated() {
        return super.isConsolidated(ENTRY2COLUMN_FCT,ENTRY2COLUMN_FCT);
    }

}
