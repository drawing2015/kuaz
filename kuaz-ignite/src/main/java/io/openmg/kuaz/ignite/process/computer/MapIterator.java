package io.openmg.kuaz.ignite.process.computer;

import com.thinkaurelius.titan.core.TitanVertex;
import org.apache.tinkerpop.gremlin.process.computer.MapReduce;
import org.apache.tinkerpop.gremlin.process.computer.util.ComputerGraph;
import org.apache.tinkerpop.gremlin.process.traversal.util.FastNoSuchElementException;
import scala.Tuple2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by zizai (http://github.com/zizai).
 */
public final class MapIterator<K, V> implements Iterator<Tuple2<K, V>> {

    private final Iterator<Tuple2<Object, TitanVertex>> inputIterator;
    private final MapReduce<K, V, ?, ?, ?> mapReduce;
    private final Queue<Tuple2<K, V>> queue = new LinkedList<>();
    private final MapIteratorEmitter mapIteratorEmitter = new MapIteratorEmitter();

    public MapIterator(final MapReduce<K, V, ?, ?, ?> mapReduce, final Iterator<Tuple2<Object, TitanVertex>> inputIterator) {
        this.inputIterator = inputIterator;
        this.mapReduce = mapReduce;
        this.mapReduce.workerStart(MapReduce.Stage.MAP);
    }


    @Override
    public boolean hasNext() {
        if (!this.queue.isEmpty())
            return true;
        else if (!this.inputIterator.hasNext()) {
            this.mapReduce.workerEnd(MapReduce.Stage.MAP);
            return false;
        } else {
            this.processNext();
            return this.hasNext();
        }
    }

    @Override
    public Tuple2<K, V> next() {
        if (!this.queue.isEmpty())
            return this.queue.remove();
        else if (!this.inputIterator.hasNext()) {
            this.mapReduce.workerEnd(MapReduce.Stage.MAP);
            throw FastNoSuchElementException.instance();
        } else {
            this.processNext();
            return this.next();
        }
    }

    private void processNext() {
        this.mapReduce.map(ComputerGraph.mapReduce(this.inputIterator.next()._2()), this.mapIteratorEmitter);
    }

    private class MapIteratorEmitter implements MapReduce.MapEmitter<K, V> {

        @Override
        public void emit(final K key, V value) {
            queue.add(new Tuple2<>(key, value));
        }
    }
}

