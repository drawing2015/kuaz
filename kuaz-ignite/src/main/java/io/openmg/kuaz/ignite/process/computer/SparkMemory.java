package io.openmg.kuaz.ignite.process.computer;

import org.apache.spark.Accumulator;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.tinkerpop.gremlin.hadoop.structure.io.ObjectWritable;
import org.apache.tinkerpop.gremlin.process.computer.*;
import org.apache.tinkerpop.gremlin.process.computer.util.MemoryHelper;
import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.spark.process.computer.MemoryAccumulator;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zizai (http://github.com/zizai).
 */
public final class SparkMemory implements Memory.Admin, Serializable {

    public final Map<String, MemoryComputeKey> memoryComputeKeys = new HashMap<>();
    private final Map<String, Accumulator<ObjectWritable>> sparkMemory = new HashMap<>();
    private final AtomicInteger iteration = new AtomicInteger(0);
    private final AtomicLong runtime = new AtomicLong(0l);
    private Broadcast<Map<String, Object>> broadcast;
    private boolean inExecute = false;

    public SparkMemory(final VertexProgram<?> vertexProgram, final Set<MapReduce> mapReducers, final JavaSparkContext sparkContext) {
        if (null != vertexProgram) {
            for (final MemoryComputeKey key : vertexProgram.getMemoryComputeKeys()) {
                this.memoryComputeKeys.put(key.getKey(), key);
            }
        }
        for (final MapReduce mapReduce : mapReducers) {
            this.memoryComputeKeys.put(mapReduce.getMemoryKey(), MemoryComputeKey.of(mapReduce.getMemoryKey(), Operator.assign, false, false));
        }
        for (final MemoryComputeKey memoryComputeKey : this.memoryComputeKeys.values()) {
            this.sparkMemory.put(
                    memoryComputeKey.getKey(),
                    sparkContext.accumulator(ObjectWritable.empty(), memoryComputeKey.getKey(), new MemoryAccumulator<>(memoryComputeKey)));
        }
        this.broadcast = sparkContext.broadcast(Collections.emptyMap());
    }

    @Override
    public Set<String> keys() {
        if (this.inExecute)
            return this.broadcast.getValue().keySet();
        else {
            final Set<String> trueKeys = new HashSet<>();
            this.sparkMemory.forEach((key, value) -> {
                if (!value.value().isEmpty())
                    trueKeys.add(key);
            });
            return Collections.unmodifiableSet(trueKeys);
        }
    }

    @Override
    public void incrIteration() {
        this.iteration.getAndIncrement();
    }

    @Override
    public void setIteration(final int iteration) {
        this.iteration.set(iteration);
    }

    @Override
    public int getIteration() {
        return this.iteration.get();
    }

    @Override
    public void setRuntime(final long runTime) {
        this.runtime.set(runTime);
    }

    @Override
    public long getRuntime() {
        return this.runtime.get();
    }

    @Override
    public <R> R get(final String key) throws IllegalArgumentException {
        if (!this.memoryComputeKeys.containsKey(key))
            throw Memory.Exceptions.memoryDoesNotExist(key);
        if (this.inExecute && !this.memoryComputeKeys.get(key).isBroadcast())
            throw Memory.Exceptions.memoryDoesNotExist(key);
        final ObjectWritable<R> r = (ObjectWritable<R>) (this.inExecute ? this.broadcast.value().get(key) : this.sparkMemory.get(key).value());
        if (null == r || r.isEmpty())
            throw Memory.Exceptions.memoryDoesNotExist(key);
        else
            return r.get();
    }

    @Override
    public void add(final String key, final Object value) {
        checkKeyValue(key, value);
        if (this.inExecute)
            this.sparkMemory.get(key).add(new ObjectWritable<>(value));
        else
            throw Memory.Exceptions.memoryAddOnlyDuringVertexProgramExecute(key);
    }

    @Override
    public void set(final String key, final Object value) {
        checkKeyValue(key, value);
        if (this.inExecute)
            throw Memory.Exceptions.memorySetOnlyDuringVertexProgramSetUpAndTerminate(key);
        else
            this.sparkMemory.get(key).setValue(new ObjectWritable<>(value));
    }

    @Override
    public String toString() {
        return StringFactory.memoryString(this);
    }

    protected void complete() {
        this.memoryComputeKeys.values().stream().filter(MemoryComputeKey::isTransient).forEach(memoryComputeKey -> this.sparkMemory.remove(memoryComputeKey.getKey()));
    }

    protected void setInExecute(final boolean inExecute) {
        this.inExecute = inExecute;
    }

    protected void broadcastMemory(final JavaSparkContext sparkContext) {
        this.broadcast.destroy(true); // do we need to block?
        final Map<String, Object> toBroadcast = new HashMap<>();
        this.sparkMemory.forEach((key, object) -> {
            if (!object.value().isEmpty() && this.memoryComputeKeys.get(key).isBroadcast())
                toBroadcast.put(key, object.value());
        });
        this.broadcast = sparkContext.broadcast(toBroadcast);
    }

    private void checkKeyValue(final String key, final Object value) {
        if (!this.memoryComputeKeys.containsKey(key))
            throw GraphComputer.Exceptions.providedKeyIsNotAMemoryComputeKey(key);
        MemoryHelper.validateValue(value);
    }
}