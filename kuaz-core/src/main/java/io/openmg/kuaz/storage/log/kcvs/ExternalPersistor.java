package io.openmg.kuaz.storage.log.kcvs;

import io.openmg.kuaz.storage.Entry;
import io.openmg.kuaz.storage.StaticBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface ExternalPersistor {

    public void add(StaticBuffer key, Entry cell);

}
