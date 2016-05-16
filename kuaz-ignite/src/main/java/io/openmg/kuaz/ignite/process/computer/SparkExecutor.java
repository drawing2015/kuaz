package io.openmg.kuaz.ignite.process.computer;

import com.google.common.base.Optional;
import com.thinkaurelius.titan.core.TitanVertex;
import io.openmg.kuaz.structure.KuazFactory;
import io.openmg.kuaz.structure.KuazGraph;
import org.apache.commons.configuration.Configuration;
import org.apache.ignite.spark.JavaIgniteRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.tinkerpop.gremlin.process.computer.*;
import org.apache.tinkerpop.gremlin.process.computer.util.ComputerGraph;
import org.apache.tinkerpop.gremlin.process.computer.util.VertexProgramHelper;
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkMessenger;
import org.apache.tinkerpop.gremlin.spark.process.computer.payload.*;
import org.apache.tinkerpop.gremlin.structure.util.Attachable;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedFactory;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertexProperty;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by zizai (http://github.com/zizai).
 */
public final class SparkExecutor {

    private SparkExecutor() {
    }

    //////////////////
    // DATA LOADING //
    //////////////////

    public static JavaPairRDD<Object, TitanVertex> applyGraphFilter(JavaIgniteRDD<Object, TitanVertex> graphRDD, final GraphFilter graphFilter) {
        return graphRDD.mapPartitionsToPair(partitionIterator -> {
            final GraphFilter gFilter = graphFilter.clone();
            return () -> IteratorUtils.filter(partitionIterator, tuple -> (tuple._2().applyGraphFilter(gFilter)).isPresent());
        }, true);
    }


    ////////////////////
    // VERTEX PROGRAM //
    ////////////////////

    public static <M> JavaIgniteRDD<Object, ViewIncomingPayload<M>> executeVertexProgramIteration(
            final JavaIgniteRDD<Object, TitanVertex> graphRDD,
            final JavaIgniteRDD<Object, ViewIncomingPayload<M>> viewIncomingRDD,
            final SparkMemory memory,
            final Configuration apacheConfiguration) {

        // the graphRDD and the viewRDD must have the same partitioner
        if (null != viewIncomingRDD)
            assert graphRDD.partitioner().get().equals(viewIncomingRDD.partitioner().get());

        final JavaIgniteRDD<Object, ViewOutgoingPayload<M>> viewOutgoingRDD = (((null == viewIncomingRDD) ?
                graphRDD.mapValues(titanVertex -> new Tuple2<>(titanVertex, Optional.<ViewIncomingPayload<M>>absent())) : // first iteration will not have any views or messages
                graphRDD.leftOuterJoin(viewIncomingRDD))                                                   // every other iteration may have views and messages
                // for each partition of vertices emit a view and their outgoing messages
                .mapPartitionsToPair(partitionIterator -> {
                    
                    //HadoopPools.initialize(apacheConfiguration);
                    final VertexProgram<M> workerVertexProgram = VertexProgram.<VertexProgram<M>>createVertexProgram(KuazFactory.open(apacheConfiguration), apacheConfiguration); // each partition(Spark)/worker(TP3) has a local copy of the vertex program (a worker's task)
                    final String[] elementComputeKeysArray = VertexProgramHelper.vertexComputeKeysAsArray(workerVertexProgram.getVertexComputeKeys()); // the compute keys as an array
                    final SparkMessenger<M> messenger = new SparkMessenger<>();
                    workerVertexProgram.workerIterationStart(memory.asImmutable()); // start the worker
                    
                    return () -> IteratorUtils.map(partitionIterator, vertexViewIncoming -> {
                        
                        final TitanVertex vertex = vertexViewIncoming._2()._1(); // get the vertex from the vertex writable
                        final boolean hasViewAndMessages = vertexViewIncoming._2()._2().isPresent(); // if this is the first iteration, then there are no views or messages
                        final List<DetachedVertexProperty<Object>> previousView = hasViewAndMessages ? vertexViewIncoming._2()._2().get().getView() : memory.isInitialIteration() ? new ArrayList<>() : Collections.emptyList();
                        
                        // revive compute properties if they already exist
                        if (memory.isInitialIteration() && elementComputeKeysArray.length > 0) {
                            vertex.properties(elementComputeKeysArray).forEachRemaining(vertexProperty -> previousView.add(DetachedFactory.detach(vertexProperty, true)));
                        }
                        
                        // drop any computed properties that are cached in memory
                        //if (elementComputeKeysArray.length > 0)
                        //    vertex.dropVertexProperties(elementComputeKeysArray);

                        final List<M> incomingMessages = hasViewAndMessages ? vertexViewIncoming._2()._2().get().getIncomingMessages() : Collections.emptyList();
                        
                        previousView.forEach(property -> property.attach(Attachable.Method.create(vertex)));  // attach the view to the vertex
                        // previousView.clear(); // no longer needed so kill it from memory
                        ///
                        
                        messenger.setVertexAndIncomingMessages(vertex, incomingMessages); // set the messenger with the incoming messages
                        workerVertexProgram.execute(ComputerGraph.vertexProgram(vertex, workerVertexProgram), messenger, memory); // execute the vertex program on this vertex for this iteration
                        // incomingMessages.clear(); // no longer needed so kill it from memory
                        ///
                        
                        final List<DetachedVertexProperty<Object>> nextView = elementComputeKeysArray.length == 0 ?  // not all vertex programs have compute keys
                                Collections.emptyList() :
                                IteratorUtils.list(IteratorUtils.map(vertex.properties(elementComputeKeysArray), property -> DetachedFactory.detach(property, true)));
                        final List<Tuple2<Object, M>> outgoingMessages = messenger.getOutgoingMessages(); // get the outgoing messages
                        
                        if (!partitionIterator.hasNext())
                            workerVertexProgram.workerIterationEnd(memory.asImmutable()); // if no more vertices in the partition, end the worker's iteration
                        return new Tuple2<>(vertex.id(), new ViewOutgoingPayload<>(nextView, outgoingMessages));
                    });
                }, true)); // true means that the partition is preserved
        
        // the graphRDD and the viewRDD must have the same partitioner
        assert graphRDD.partitioner().get().equals(viewOutgoingRDD.partitioner().get());
        
        // "message pass" by reducing on the vertex object id of the view and message payloads
        final MessageCombiner<M> messageCombiner = VertexProgram.<VertexProgram<M>>createVertexProgram(KuazFactory.open(apacheConfiguration), apacheConfiguration).getMessageCombiner().orElse(null);
        final JavaIgniteRDD<Object, ViewIncomingPayload<M>> newViewIncomingRDD = viewOutgoingRDD
                .flatMapToPair(tuple -> () -> IteratorUtils.<Tuple2<Object, Payload>>concat(
                        IteratorUtils.of(new Tuple2<>(tuple._1(), tuple._2().getView())),      // emit the view payload
                        IteratorUtils.map(tuple._2().getOutgoingMessages().iterator(), message -> new Tuple2<>(message._1(), new MessagePayload<>(message._2())))))  // emit the outgoing message payloads one by one
                .reduceByKey(graphRDD.partitioner().get(), (a, b) -> {      // reduce the view and outgoing messages into a single payload object representing the new view and incoming messages for a vertex
                    if (a instanceof ViewIncomingPayload) {
                        ((ViewIncomingPayload<M>) a).mergePayload(b, messageCombiner);
                        return a;
                    } else if (b instanceof ViewIncomingPayload) {
                        ((ViewIncomingPayload<M>) b).mergePayload(a, messageCombiner);
                        return b;
                    } else {
                        final ViewIncomingPayload<M> c = new ViewIncomingPayload<>(messageCombiner);
                        c.mergePayload(a, messageCombiner);
                        c.mergePayload(b, messageCombiner);
                        return c;
                    }
                })
                .filter(payload -> !(payload._2() instanceof MessagePayload)) // this happens if there is a message to a vertex that does not exist
                .filter(payload -> !((payload._2() instanceof ViewIncomingPayload) && !((ViewIncomingPayload<M>) payload._2()).hasView())) // this happens if there are many messages to a vertex that does not exist
                .mapValues(payload -> payload instanceof ViewIncomingPayload ?
                        (ViewIncomingPayload<M>) payload :                    // this happens if there is a vertex with incoming messages
                        new ViewIncomingPayload<>((ViewPayload) payload));    // this happens if there is a vertex with no incoming messages
        
        // the graphRDD and the viewRDD must have the same partitioner
        assert graphRDD.partitioner().get().equals(newViewIncomingRDD.partitioner().get());
        newViewIncomingRDD
                .foreachPartition(partitionIterator -> {
                    //HadoopPools.initialize(apacheConfiguration);
                }); // need to complete a task so its BSP and the memory for this iteration is updated
        return newViewIncomingRDD;
    }

    public static <M> JavaIgniteRDD<Object, TitanVertex> prepareFinalGraphRDD(final JavaIgniteRDD<Object, TitanVertex> graphRDD, final JavaIgniteRDD<Object, ViewIncomingPayload<M>> viewIncomingRDD, final Set<VertexComputeKey> vertexComputeKeys) {
        // the graphRDD and the viewRDD must have the same partitioner
        assert (graphRDD.partitioner().get().equals(viewIncomingRDD.partitioner().get()));
        // attach the final computed view to the cached graph
        return (JavaIgniteRDD<Object, TitanVertex>) graphRDD.leftOuterJoin(viewIncomingRDD)
                .mapValues(tuple -> {
                    final TitanVertex vertex = tuple._1();
                    final List<DetachedVertexProperty<Object>> view = tuple._2().isPresent() ? tuple._2().get().getView() : Collections.emptyList();
                    for (final DetachedVertexProperty<Object> property : view) {
                        //vertex.dropVertexProperties(property.key());
                        if (!VertexProgramHelper.isTransientVertexComputeKey(property.key(), vertexComputeKeys))
                            property.attach(Attachable.Method.create(vertex));
                    }
                    return tuple._1();
                });
    }

    /////////////////
    // MAP REDUCE //
    ////////////////

    public static <K, V> JavaIgniteRDD<K, V> executeMap(final JavaIgniteRDD<Object, TitanVertex> graphRDD, final MapReduce<K, V, ?, ?, ?> mapReduce, final Configuration apacheConfiguration) {
        JavaIgniteRDD<K, V> mapRDD = graphRDD.mapPartitionsToPair(partitionIterator -> {
            //HadoopPools.initialize(apacheConfiguration);
            return () -> new MapIterator<>(MapReduce.<MapReduce<K, V, ?, ?, ?>>createMapReduce(KuazFactory.open(apacheConfiguration), apacheConfiguration), partitionIterator);
        });
        if (mapReduce.getMapKeySort().isPresent())
            mapRDD = (JavaIgniteRDD<K, V>) mapRDD.sortByKey(mapReduce.getMapKeySort().get(), true, 1);
        return mapRDD;
    }

    public static <K, V, OK, OV> JavaIgniteRDD<OK, OV> executeCombine(final JavaIgniteRDD<K, V> mapRDD, final Configuration apacheConfiguration) {
        return mapRDD.mapPartitionsToPair(partitionIterator -> {
            //HadoopPools.initialize(apacheConfiguration);
            return () -> new CombineIterator<>(MapReduce.createMapReduce(KuazFactory.open(apacheConfiguration), apacheConfiguration), partitionIterator);
        });
    }

    public static <K, V, OK, OV> JavaIgniteRDD<OK, OV> executeReduce(final JavaIgniteRDD<K, V> mapOrCombineRDD, final MapReduce<K, V, OK, OV, ?> mapReduce, final Configuration apacheConfiguration) {
        JavaIgniteRDD<OK, OV> reduceRDD = mapOrCombineRDD.groupByKey().mapPartitionsToPair(partitionIterator -> {
            //HadoopPools.initialize(apacheConfiguration);
            return () -> new ReduceIterator<>(MapReduce.<MapReduce<K, V, OK, OV, ?>>createMapReduce(KuazFactory.open(apacheConfiguration), apacheConfiguration), partitionIterator);
        });
        if (mapReduce.getReduceKeySort().isPresent())
            reduceRDD = (JavaIgniteRDD<OK, OV>) reduceRDD.sortByKey(mapReduce.getReduceKeySort().get(), true, 1);
        return reduceRDD;
    }
}
