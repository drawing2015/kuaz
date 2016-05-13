package io.openmg.kuaz.graphdb.transaction.indexcache;

import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.core.TitanVertexProperty;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface IndexCache {

    public void add(TitanVertexProperty property);

    public void remove(TitanVertexProperty property);

    public Iterable<TitanVertexProperty> get(Object value, PropertyKey key);

}
