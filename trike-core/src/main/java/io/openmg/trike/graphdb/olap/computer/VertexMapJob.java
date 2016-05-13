package io.openmg.trike.graphdb.olap.computer;

import com.google.common.collect.ImmutableMap;
import io.openmg.trike.core.TitanGraph;
import io.openmg.trike.core.TitanVertex;
import io.openmg.trike.diskstorage.EntryList;
import io.openmg.trike.diskstorage.configuration.Configuration;
import io.openmg.trike.diskstorage.keycolumnvalue.SliceQuery;
import io.openmg.trike.diskstorage.keycolumnvalue.scan.ScanMetrics;
import io.openmg.trike.graphdb.database.StandardTitanGraph;
import io.openmg.trike.graphdb.idmanagement.IDManager;
import io.openmg.trike.graphdb.olap.QueryContainer;
import io.openmg.trike.graphdb.olap.VertexJobConverter;
import io.openmg.trike.graphdb.olap.VertexScanJob;
import io.openmg.trike.graphdb.vertices.PreloadedVertex;
import io.openmg.trike.util.datastructures.Retriever;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.process.computer.MapReduce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class VertexMapJob implements VertexScanJob {

    private static final Logger log =
            LoggerFactory.getLogger(VertexMapJob.class);


    public static final PreloadedVertex.AccessCheck MAPREDUCE_CHECK = new PreloadedVertex.AccessCheck() {
        @Override
        public final void accessEdges() {
            throw GraphComputer.Exceptions.incidentAndAdjacentElementsCanNotBeAccessedInMapReduce();
        }

        @Override
        public final void accessProperties() {
            return; //Allowed
        }

        @Override
        public void accessSetProperty() {
            throw GraphComputer.Exceptions.vertexPropertiesCanNotBeUpdatedInMapReduce();
        }

        @Override
        public Retriever<SliceQuery, EntryList> retrieveSliceQuery() {
            return PreloadedVertex.EMPTY_RETRIEVER;
        }
    };

    private final IDManager idManager;
    private final Map<MapReduce, FulgoraMapEmitter> mapJobs;
    private final FulgoraVertexMemory vertexMemory;

    public static final String MAP_JOB_SUCCESS = "map-success";
    public static final String MAP_JOB_FAILURE = "map-fail";

    private VertexMapJob(IDManager idManager, FulgoraVertexMemory vertexMemory,
                         Map<MapReduce, FulgoraMapEmitter> mapJobs) {
        this.mapJobs = mapJobs;
        this.vertexMemory = vertexMemory;
        this.idManager = idManager;
    }

    @Override
    public VertexMapJob clone() {
        ImmutableMap.Builder<MapReduce, FulgoraMapEmitter> cloneMap = ImmutableMap.builder();
        for (Map.Entry<MapReduce, FulgoraMapEmitter> entry : mapJobs.entrySet()) {
            cloneMap.put(entry.getKey().clone(), entry.getValue());
        }
        return new VertexMapJob(idManager, vertexMemory, cloneMap.build());
    }

    @Override
    public void workerIterationStart(TitanGraph graph, Configuration config, ScanMetrics metrics) {
        for (Map.Entry<MapReduce, FulgoraMapEmitter> mapJob : mapJobs.entrySet()) {
            mapJob.getKey().workerStart(MapReduce.Stage.MAP);
        }
    }

    @Override
    public void workerIterationEnd(ScanMetrics metrics) {
        for (Map.Entry<MapReduce, FulgoraMapEmitter> mapJob : mapJobs.entrySet()) {
            mapJob.getKey().workerEnd(MapReduce.Stage.MAP);
        }
    }

    @Override
    public void process(TitanVertex vertex, ScanMetrics metrics) {
        PreloadedVertex v = (PreloadedVertex) vertex;
        if (vertexMemory != null) {
            VertexMemoryHandler vh = new VertexMemoryHandler(vertexMemory, v);
            v.setPropertyMixing(vh);
        }
        v.setAccessCheck(MAPREDUCE_CHECK);
        if (idManager.isPartitionedVertex(v.longId()) && !idManager.isCanonicalVertexId(v.longId())) {
            return; //Only consider the canonical partition vertex representative
        } else {
            for (Map.Entry<MapReduce, FulgoraMapEmitter> mapJob : mapJobs.entrySet()) {
                MapReduce job = mapJob.getKey();
                try {
                    job.map(v, mapJob.getValue());
                    metrics.incrementCustom(MAP_JOB_SUCCESS);
                } catch (Throwable ex) {
                    log.error("Encountered exception executing map job [" + job + "] on vertex [" + vertex + "]:", ex);
                    metrics.incrementCustom(MAP_JOB_FAILURE);
                }
            }
        }
    }

    @Override
    public void getQueries(QueryContainer queries) {

    }

    public static Executor getVertexMapJob(StandardTitanGraph graph, FulgoraVertexMemory vertexMemory,
                                           Map<MapReduce, FulgoraMapEmitter> mapJobs) {
        VertexMapJob job = new VertexMapJob(graph.getIDManager(), vertexMemory, mapJobs);
        for (Map.Entry<MapReduce, FulgoraMapEmitter> mapJob : mapJobs.entrySet()) {
            mapJob.getKey().workerStart(MapReduce.Stage.MAP);
        }
        return new Executor(graph, job);
    }

    public static class Executor extends VertexJobConverter {

        private Executor(TitanGraph graph, VertexMapJob job) {
            super(graph, job);
        }

        private Executor(final Executor copy) {
            super(copy);
        }

        @Override
        public List<SliceQuery> getQueries() {
            List<SliceQuery> queries = super.getQueries();
            queries.add(VertexProgramScanJob.SYSTEM_PROPS_QUERY);
            return queries;
        }

        @Override
        public void workerIterationEnd(ScanMetrics metrics) {
            super.workerIterationEnd(metrics);
        }

        @Override
        public Executor clone() {
            return new Executor(this);
        }

    }

}


