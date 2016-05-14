package io.openmg.kuaz.process.computer;

import io.openmg.kuaz.structure.KuazGraph;
import org.apache.tinkerpop.gremlin.process.computer.*;
import org.apache.tinkerpop.gremlin.process.computer.util.DefaultComputerResult;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * Created by zizai (http://github.com/zizai).
 */
public final class KuazGraphComputer implements GraphComputer {

    private ResultGraph resultGraph = null;
    private Persist persist = null;

    private final KuazGraph kuazGraph;
    private VertexProgram<?> vertexProgram;
    protected final Logger logger;
    private boolean executed = false;
    private final Set<MapReduce> mapReducers = new HashSet<>();
    private int workers = 1;
    private final GraphFilter graphFilter = new GraphFilter();

    public KuazGraphComputer(final KuazGraph kuazGraph) {
        this.kuazGraph = kuazGraph;
        this.logger = LoggerFactory.getLogger(this.getClass());


    }

    @Override
    public Future<ComputerResult> submit() {
        //this.validateStatePriorToExecution();
        return ComputerSubmissionHelper.runWithBackgroundThread(this::submitWithExecutor, "SparkSubmitter");
    }

    private Future<ComputerResult> submitWithExecutor(Executor exec) {
        // create the completable future                                                    
        return CompletableFuture.<ComputerResult>supplyAsync(() -> {
            //
        }, exec);
    }


    @Override
    public GraphComputer result(final ResultGraph resultGraph) {
        this.resultGraph = resultGraph;
        return this;
    }

    @Override
    public GraphComputer persist(final Persist persist) {
        this.persist = persist;
        return this;
    }

    @Override
    public GraphComputer program(final VertexProgram vertexProgram) {
        this.vertexProgram = vertexProgram;
        return this;
    }

    @Override
    public GraphComputer mapReduce(final MapReduce mapReduce) {
        this.mapReducers.add(mapReduce);
        return this;
    }

    @Override
    public GraphComputer workers(final int workers) {
        this.workers = workers;
        return this;
    }

    @Override
    public GraphComputer vertices(final Traversal<Vertex, Vertex> vertexFilter) {
        this.graphFilter.setVertexFilter(vertexFilter);
        return this;
    }

    @Override
    public GraphComputer edges(final Traversal<Vertex, Edge> edgeFilter) {
        this.graphFilter.setEdgeFilter(edgeFilter);
        return this;
    }

    @Override
    public Features features() {
        return new Features() {

            @Override
            public int getMaxWorkers() {
                return 1;
            }

            @Override
            public boolean supportsVertexAddition() {
                return false;
            }

            @Override
            public boolean supportsVertexRemoval() {
                return false;
            }

            @Override
            public boolean supportsVertexPropertyRemoval() {
                return false;
            }

            @Override
            public boolean supportsEdgeAddition() {
                return false;
            }

            @Override
            public boolean supportsEdgeRemoval() {
                return false;
            }

            @Override
            public boolean supportsEdgePropertyAddition() {
                return false;
            }

            @Override
            public boolean supportsEdgePropertyRemoval() {
                return false;
            }
        };
    }
}
