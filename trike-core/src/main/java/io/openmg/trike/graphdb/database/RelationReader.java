package io.openmg.trike.graphdb.database;

import io.openmg.trike.diskstorage.Entry;
import io.openmg.trike.graphdb.relations.RelationCache;
import io.openmg.trike.graphdb.types.TypeInspector;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface RelationReader {

    public RelationCache parseRelation(Entry data, boolean parseHeaderOnly, TypeInspector tx);

}
