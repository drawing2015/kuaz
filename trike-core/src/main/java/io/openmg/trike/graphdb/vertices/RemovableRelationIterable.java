package io.openmg.trike.graphdb.vertices;

import io.openmg.trike.core.TitanRelation;
import io.openmg.trike.graphdb.internal.InternalRelation;

import java.util.Iterator;

public class RemovableRelationIterable<O extends TitanRelation>
        implements Iterable<O> {

    private final Iterable<InternalRelation> iterable;

    public RemovableRelationIterable(Iterable<InternalRelation> iter) {
        iterable = iter;
    }

    @Override
    public Iterator<O> iterator() {
        return new RemovableRelationIterator<O>(iterable.iterator());
    }

}
