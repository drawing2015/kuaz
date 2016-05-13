package io.openmg.kuaz.graphdb.log;

import com.carrotsearch.hppc.cursors.LongObjectCursor;
import io.openmg.kuaz.core.EdgeLabel;
import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.core.log.Change;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.graphdb.database.log.TransactionLogHeader;
import io.openmg.kuaz.graphdb.internal.ElementLifeCycle;
import io.openmg.kuaz.graphdb.internal.InternalRelation;
import io.openmg.kuaz.graphdb.internal.InternalRelationType;
import io.openmg.kuaz.graphdb.internal.InternalVertex;
import io.openmg.kuaz.graphdb.relations.*;
import io.openmg.kuaz.graphdb.transaction.StandardTitanTx;
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
