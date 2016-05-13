package io.openmg.trike.graphdb.database.indexing;

import com.google.common.base.Preconditions;
import io.openmg.trike.core.attribute.Cmp;
import io.openmg.trike.core.attribute.Contain;
import io.openmg.trike.diskstorage.indexing.IndexFeatures;
import io.openmg.trike.diskstorage.indexing.IndexInformation;
import io.openmg.trike.diskstorage.indexing.KeyInformation;
import io.openmg.trike.graphdb.query.TitanPredicate;
import org.apache.commons.lang.StringUtils;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class StandardIndexInformation implements IndexInformation {

    public static final StandardIndexInformation INSTANCE = new StandardIndexInformation();

    private static final IndexFeatures STANDARD_FEATURES = new IndexFeatures.Builder().build();

    private StandardIndexInformation() {
    }

    @Override
    public boolean supports(KeyInformation information, TitanPredicate titanPredicate) {
        return titanPredicate == Cmp.EQUAL || titanPredicate == Contain.IN;
    }

    @Override
    public boolean supports(KeyInformation information) {
        return true;
    }

    @Override
    public String mapKey2Field(String key, KeyInformation information) {
        return key;
    }

    @Override
    public IndexFeatures getFeatures() {
        return STANDARD_FEATURES;
    }
}
