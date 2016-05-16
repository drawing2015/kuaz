package io.openmg.kuaz.process.computer;

import io.openmg.kuaz.structure.KuazGraph;
import io.openmg.kuaz.structure.configuration.KuazGraphConfiguration;
import org.apache.tinkerpop.gremlin.process.computer.*;
import org.apache.tinkerpop.gremlin.process.computer.util.GraphComputerHelper;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by zizai (http://github.com/zizai).
 */
public abstract class AbstractKuazGraphComputer implements GraphComputer {

    protected ResultGraph resultGraph = null;
    protected Persist persist = null;

    protected final KuazGraph kuazGraph;
    protected final KuazGraphConfiguration configuration;
    protected VertexProgram<?> vertexProgram;
    protected final Logger logger;
    protected boolean executed = false;
    protected final Set<MapReduce> mapReducers = new HashSet<>();
    protected int workers = 1;
    protected final GraphFilter graphFilter = new GraphFilter();

    public AbstractKuazGraphComputer(final KuazGraph kuazGraph) {
        this.kuazGraph = kuazGraph;
        this.configuration = kuazGraph.getConfiguration();
        this.logger = LoggerFactory.getLogger(this.getClass());
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

    @Override
    public String toString() {
        return StringFactory.graphComputerString(this);
    }

    protected void validateStatePriorToExecution() {
        // a graph computer can only be executed one time
        if (this.executed)
            throw Exceptions.computerHasAlreadyBeenSubmittedAVertexProgram();
        else
            this.executed = true;
        // it is not possible execute a computer if it has no vertex program nor mapreducers
        if (null == this.vertexProgram && this.mapReducers.isEmpty())
            throw GraphComputer.Exceptions.computerHasNoVertexProgramNorMapReducers();
        // it is possible to run mapreducers without a vertex program
        if (null != this.vertexProgram) {
            GraphComputerHelper.validateProgramOnComputer(this, vertexProgram);
            this.mapReducers.addAll(this.vertexProgram.getMapReducers());
        }
        // if the user didn't set desired persistence/resultgraph, then get from vertex program or else, no persistence
        this.persist = GraphComputerHelper.getPersistState(Optional.ofNullable(this.vertexProgram), Optional.ofNullable(this.persist));
        this.resultGraph = GraphComputerHelper.getResultGraphState(Optional.ofNullable(this.vertexProgram), Optional.ofNullable(this.resultGraph));
        // determine persistence and result graph options
        if (!this.features().supportsResultGraphPersistCombination(this.resultGraph, this.persist))
            throw GraphComputer.Exceptions.resultGraphPersistCombinationNotSupported(this.resultGraph, this.persist);
        // if too many workers are requested, throw appropriate exception
        if (this.workers > this.features().getMaxWorkers())
            throw GraphComputer.Exceptions.computerRequiresMoreWorkersThanSupported(this.workers, this.features().getMaxWorkers());
    }

}
