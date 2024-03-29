package io.openmg.kuaz.ignite.process.computer;

import org.apache.tinkerpop.gremlin.process.computer.MapReduce;
import org.apache.tinkerpop.gremlin.process.traversal.util.FastNoSuchElementException;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zizai (http://github.com/zizai).
 */
public final class CombineIterator<K, V, OK, OV> implements Iterator<Tuple2<OK, OV>> {

    private final Iterator<Tuple2<K, V>> inputIterator;
    private final MapReduce<K, V, OK, OV, ?> mapReduce;
    private final CombineIteratorEmitter combineIteratorEmitter = new CombineIterator.CombineIteratorEmitter();
    private final Map<K, List<V>> combineMap = new ConcurrentHashMap<>();
    private boolean combined = true;

    public CombineIterator(final MapReduce<K, V, OK, OV, ?> mapReduce, final Iterator<Tuple2<K, V>> inputIterator) {
        this.inputIterator = inputIterator;
        this.mapReduce = mapReduce;
        this.mapReduce.workerStart(MapReduce.Stage.COMBINE);
    }

    @Override
    public boolean hasNext() {
        if (!this.combineMap.isEmpty())
            return true;
        else if (!this.inputIterator.hasNext()) {
            this.mapReduce.workerEnd(MapReduce.Stage.COMBINE);
            return false;
        } else {
            this.processNext();
            return this.hasNext();
        }
    }

    @Override
    public Tuple2<OK, OV> next() {
        if (!this.combineMap.isEmpty())
            return this.nextFromCombineMap();
        else if (!this.inputIterator.hasNext()) {
            this.mapReduce.workerEnd(MapReduce.Stage.COMBINE);
            throw FastNoSuchElementException.instance();
        } else {
            this.processNext();
            return this.next();
        }
    }

    private static final int MAX_SIZE = 5000;

    private void processNext() {
        int combinedSize = this.combineMap.size();
        while (combinedSize < MAX_SIZE && this.inputIterator.hasNext()) {
            final Tuple2<K, V> keyValue = this.inputIterator.next();
            List<V> values = this.combineMap.get(keyValue._1());
            if (null == values) {
                values = new ArrayList<>();
                this.combineMap.put(keyValue._1(), values);
            }
            values.add(keyValue._2());
            combinedSize++;
            this.combined = false;
            if (combinedSize >= MAX_SIZE) {
                this.doCombine();
                combinedSize = this.combineMap.size();
            }
        }
    }

    private void doCombine() {
        if (!this.combined) {
            for (final K key : this.combineMap.keySet()) {
                final List<V> values2 = this.combineMap.get(key);
                if (values2.size() > 1) {
                    this.combineMap.remove(key);
                    this.mapReduce.combine(key, values2.iterator(), this.combineIteratorEmitter);
                }
            }
            this.combined = true;
        }
    }

    private Tuple2<OK, OV> nextFromCombineMap() {
        this.doCombine();
        final OK key = (OK) this.combineMap.keySet().iterator().next();
        final List<OV> values = (List<OV>) this.combineMap.get(key);
        final Tuple2<OK, OV> keyValue = new Tuple2<>(key, values.remove(0));
        if (values.isEmpty())
            this.combineMap.remove(key);
        return keyValue;
    }

    private class CombineIteratorEmitter implements MapReduce.ReduceEmitter<OK, OV> {
        @Override
        public void emit(final OK key, OV value) {
            List<V> values = combineMap.get(key);
            if (null == values) {
                values = new ArrayList<>();
                combineMap.put((K) key, values);
            }
            values.add((V) value);
        }
    }
}
