package io.openmg.trike.graphdb.log;

import com.carrotsearch.hppc.cursors.LongObjectCursor;
import io.openmg.trike.core.EdgeLabel;
import io.openmg.trike.core.PropertyKey;
import io.openmg.trike.core.log.Change;
import io.openmg.trike.diskstorage.Entry;
import io.openmg.trike.graphdb.database.log.TransactionLogHeader;
import io.openmg.trike.graphdb.internal.ElementLifeCycle;
import io.openmg.trike.graphdb.internal.InternalRelation;
import io.openmg.trike.graphdb.internal.InternalRelationType;
import io.openmg.trike.graphdb.internal.InternalVertex;
import io.openmg.trike.graphdb.relations.*;
import io.openmg.trike.graphdb.transaction.StandardTitanTx;
import org.apache.tinkerpop.gremlin.structure.Direction;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class ModificationDeserializer {


    public static InternalRelation parseRelation(TransactionLogHeader.Modification modification, StandardTitanTx tx) {
        Change state = modification.state;
        assert state.isProper();
        long outVertexId = modification.outVertexId;
        Entry relEntry = modification.relationEntry;
        InternalVertex outVertex = tx.getInternalVertex(outVertexId);
        //Special relation parsing, compare to {@link RelationConstructor}
        RelationCache relCache = tx.getEdgeSerializer().readRelation(relEntry, false, tx);
        assert relCache.direction == Direction.OUT;
        InternalRelationType type = (InternalRelationType)tx.getExistingRelationType(relCache.typeId);
        assert type.getBaseType()==null;
        InternalRelation rel;
        if (type.isPropertyKey()) {
            if (state==Change.REMOVED) {
                rel = new StandardVertexProperty(relCache.relationId,(PropertyKey)type,outVertex,relCache.getValue(), ElementLifeCycle.Removed);
            } else {
                rel = new CacheVertexProperty(relCache.relationId,(PropertyKey)type,outVertex,relCache.getValue(),relEntry);
            }
        } else {
            assert type.isEdgeLabel();
            InternalVertex otherVertex = tx.getInternalVertex(relCache.getOtherVertexId());
            if (state==Change.REMOVED) {
                rel = new StandardEdge(relCache.relationId, (EdgeLabel) type, outVertex, otherVertex,ElementLifeCycle.Removed);
            } else {
                rel = new CacheEdge(relCache.relationId, (EdgeLabel) type, outVertex, otherVertex,relEntry);
            }
        }
        if (state==Change.REMOVED && relCache.hasProperties()) { //copy over properties
            for (LongObjectCursor<Object> entry : relCache) {
                rel.setPropertyDirect(tx.getExistingPropertyKey(entry.key),entry.value);
            }
        }
        return rel;
    }

}
