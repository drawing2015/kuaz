package io.openmg.kuaz.graphdb.transaction;

import io.openmg.kuaz.diskstorage.util.RecordIterator;
import io.openmg.kuaz.graphdb.database.StandardTitanGraph;
import io.openmg.kuaz.graphdb.idmanagement.IDManager;
import io.openmg.kuaz.graphdb.internal.InternalVertex;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class VertexIterable implements Iterable<InternalVertex> {

    private final StandardTitanTx tx;
    private final StandardTitanGraph graph;

    public VertexIterable(final StandardTitanGraph graph, final StandardTitanTx tx) {
        this.graph = graph;
        this.tx = tx;
    }

    @Override
    public Iterator<InternalVertex> iterator() {
        return new Iterator<InternalVertex>() {

            RecordIterator<Long> iterator = graph.getVertexIDs(tx.getTxHandle());
            InternalVertex nextVertex = nextVertex();

            private InternalVertex nextVertex() {
                InternalVertex v = null;
                while (v == null && iterator.hasNext()) {
                    long nextId = iterator.next().longValue();
                    //Filter out invisible vertices
                    if (IDManager.VertexIDType.Invisible.is(nextId)) continue;

                    v = tx.getInternalVertex(nextId);
                    //Filter out deleted vertices and types
                    if (v.isRemoved()) v = null;
                }
                return v;
            }

            @Override
            public boolean hasNext() {
                return nextVertex != null;
            }

            @Override
            public InternalVertex next() {
                if (!hasNext()) throw new NoSuchElementException();
                InternalVertex returnVertex = nextVertex;
                nextVertex = nextVertex();
                return returnVertex;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}