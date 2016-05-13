package io.openmg.kuaz.graphdb.database;

import io.openmg.kuaz.storage.Entry;
import io.openmg.kuaz.graphdb.relations.RelationCache;
import io.openmg.kuaz.graphdb.types.TypeInspector;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface RelationReader {

    public RelationCache parseRelation(Entry data, boolean parseHeaderOnly, TypeInspector tx);

}
