package io.openmg.kuaz.diskstorage.log.kcvs;

import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.StaticBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface ExternalPersistor {

    public void add(StaticBuffer key, Entry cell);

}
