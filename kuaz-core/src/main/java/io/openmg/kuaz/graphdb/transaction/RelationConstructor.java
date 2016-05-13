package io.openmg.kuaz.graphdb.transaction;

import com.google.common.base.Preconditions;
import io.openmg.kuaz.core.EdgeLabel;
import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.core.TitanRelation;
import io.openmg.kuaz.storage.Entry;
import io.openmg.kuaz.graphdb.database.EdgeSerializer;
import io.openmg.kuaz.graphdb.internal.InternalRelation;
import io.openmg.kuaz.graphdb.internal.InternalRelationType;
import io.openmg.kuaz.graphdb.internal.InternalVertex;
import io.openmg.kuaz.graphdb.relations.CacheEdge;
import io.openmg.kuaz.graphdb.relations.CacheVertexProperty;
import io.openmg.kuaz.graphdb.relations.RelationCache;
import io.openmg.kuaz.graphdb.types.TypeInspector;
import io.openmg.kuaz.graphdb.types.TypeUtil;
import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.Iterator;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class RelationConstructor {

    public static RelationCache readRelationCache(Entry data, StandardTitanTx tx) {
        return tx.getEdgeSerializer().readRelation(data, false, tx);
    }

    public static Iterable<TitanRelation> readRelation(final InternalVertex vertex, final Iterable<Entry> data, final StandardTitanTx tx) {
        return new Iterable<TitanRelation>() {
            @Override
            public Iterator<TitanRelation> iterator() {
                return new Iterator<TitanRelation>() {

                    Iterator<Entry> iter = data.iterator();
                    TitanRelation current = null;

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public TitanRelation next() {
                        current = readRelation(vertex,iter.next(),tx);
                        return current;
                    }

                    @Override
                    public void remove() {
                        Preconditions.checkState(current!=null);
                        current.remove();
                    }
                };
            }
        };
    }

    public static InternalRelation readRelation(final InternalVertex vertex, final Entry data, final StandardTitanTx tx) {
        RelationCache relation = tx.getEdgeSerializer().readRelation(data, true, tx);
        return readRelation(vertex,relation,data,tx,tx);
    }

    public static InternalRelation readRelation(final InternalVertex vertex, final Entry data,
                                                final EdgeSerializer serializer, final TypeInspector types,
                                                final VertexFactory vertexFac) {
        RelationCache relation = serializer.readRelation(data, true, types);
        return readRelation(vertex,relation,data,types,vertexFac);
    }


    private static InternalRelation readRelation(final InternalVertex vertex, final RelationCache relation,
                                         final Entry data, final TypeInspector types, final VertexFactory vertexFac) {
        InternalRelationType type = TypeUtil.getBaseType((InternalRelationType) types.getExistingRelationType(relation.typeId));

        if (type.isPropertyKey()) {
            assert relation.direction == Direction.OUT;
            return new CacheVertexProperty(relation.relationId, (PropertyKey) type, vertex, relation.getValue(), data);
        }

        if (type.isEdgeLabel()) {
            InternalVertex otherVertex = vertexFac.getInternalVertex(relation.getOtherVertexId());
            switch (relation.direction) {
                case IN:
                    return new CacheEdge(relation.relationId, (EdgeLabel) type, otherVertex, vertex, data);

                case OUT:
                    return new CacheEdge(relation.relationId, (EdgeLabel) type, vertex, otherVertex, data);

                default:
                    throw new AssertionError();
            }
        }

        throw new AssertionError();
    }

}
