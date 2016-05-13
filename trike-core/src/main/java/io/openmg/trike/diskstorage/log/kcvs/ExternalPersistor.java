package io.openmg.trike.diskstorage.log.kcvs;

import io.openmg.trike.diskstorage.Entry;
import io.openmg.trike.diskstorage.StaticBuffer;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface ExternalPersistor {

    public void add(StaticBuffer key, Entry cell);

}
