package io.openmg.kuaz.ignite.process.computer;

import com.thinkaurelius.titan.core.TitanVertex;
import io.openmg.kuaz.ignite.structure.io.IgniteGraphRDD;
import io.openmg.kuaz.process.computer.AbstractKuazGraphComputer;
import io.openmg.kuaz.process.computer.ComputerSubmissionHelper;
import io.openmg.kuaz.structure.KuazFactory;
import io.openmg.kuaz.structure.KuazGraph;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.ignite.spark.JavaIgniteContext;
import org.apache.ignite.spark.JavaIgniteRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.tinkerpop.gremlin.process.computer.ComputerResult;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.process.computer.VertexProgram;
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkMemory;
import org.apache.tinkerpop.gremlin.spark.process.computer.payload.ViewIncomingPayload;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class SparkGraphComputer extends AbstractKuazGraphComputer {

    private final Configuration sparkConfiguration;

    public SparkGraphComputer(KuazGraph kuazGraph) {
        super(kuazGraph);
        this.sparkConfiguration = new PropertiesConfiguration();
    }

    @Override
    public GraphComputer configure(final String key, final Object value) {
        this.sparkConfiguration.setProperty(key, value);
        return this;
    }

    @Override
    public Future<ComputerResult> submit() {
        this.validateStatePriorToExecution();
        return ComputerSubmissionHelper.runWithBackgroundThread(this::submitWithExecutor, "SparkSubmitter");
    }

    private Future<ComputerResult> submitWithExecutor(Executor exec) {
        // create the completable future                                                    
        return CompletableFuture.<ComputerResult>supplyAsync(() -> {
            final long startTime = System.currentTimeMillis();

            final IgniteGraphRDD graphRDD = new IgniteGraphRDD();
            SparkMemory memory = null;

            // create the spark configuration from the graph computer configuration
            final SparkConf sparkConf = this.configuration.getSparkConf();

            // execute the vertex program and map reducers and if there is a failure, auto-close the spark context
            try {
                final JavaSparkContext sparkContext = new JavaSparkContext(SparkContext.getOrCreate(sparkConf));
                final JavaIgniteContext igniteContext = new JavaIgniteContext(sparkContext, this.configuration.getIgniteConfPath());

                // add the project jars to the cluster
                //this.loadJars(sparkContext, hadoopConfiguration);

                // this is the context RDD holder that prevents GC
                //Spark.create(sparkContext.sc());
                updateLocalConfiguration(sparkContext, sparkConf);

                // create a message-passing friendly rdd from the input rdd
                JavaIgniteRDD<Object, TitanVertex> computedGraphRDD = null;
                boolean partitioned = false;
                JavaIgniteRDD<Object, TitanVertex> loadedGraphRDD = graphRDD.readGraphRDD(this.configuration.getUniqueGraphId(), igniteContext);

                ////////////////////////////////
                // process the vertex program //
                ////////////////////////////////
                if (null != this.vertexProgram) {
                    // set up the vertex program and wire up configurations
                    JavaIgniteRDD<Object, ViewIncomingPayload<Object>> viewIncomingRDD = null;
                    memory = new SparkMemory(this.vertexProgram, this.mapReducers, sparkContext);
                    this.vertexProgram.setup(memory);
                    memory.broadcastMemory(sparkContext);
                    final HadoopConfiguration vertexProgramConfiguration = new HadoopConfiguration();
                    this.vertexProgram.storeState(vertexProgramConfiguration);
                    ConfigurationUtils.copy(vertexProgramConfiguration, apacheConfiguration);
                    ConfUtil.mergeApacheIntoHadoopConfiguration(vertexProgramConfiguration, hadoopConfiguration);
                    // execute the vertex program
                    while (true) {
                        memory.setInExecute(true);
                        viewIncomingRDD = SparkExecutor.executeVertexProgramIteration(loadedGraphRDD, viewIncomingRDD, memory, vertexProgramConfiguration);
                        memory.setInExecute(false);
                        if (this.vertexProgram.terminate(memory))
                            break;
                        else {
                            memory.incrIteration();
                            memory.broadcastMemory(sparkContext);
                        }
                    }
                    memory.complete(); // drop all transient memory keys
                    // write the computed graph to the respective output (rdd or output format)
                    computedGraphRDD = SparkExecutor.prepareFinalGraphRDD(loadedGraphRDD, viewIncomingRDD, this.vertexProgram.getVertexComputeKeys());
                    if (null != outputRDD && !this.persist.equals(Persist.NOTHING)) {
                        outputRDD.writeGraphRDD(apacheConfiguration, computedGraphRDD);
                    }
                }

                final boolean computedGraphCreated = computedGraphRDD != null;
                if (!computedGraphCreated)
                    computedGraphRDD = loadedGraphRDD;

                final Memory.Admin finalMemory = null == memory ? new MapMemory() : new MapMemory(memory);

                //////////////////////////////
                // process the map reducers //
                //////////////////////////////
                if (!this.mapReducers.isEmpty()) {
                    if (computedGraphCreated && !outputToSpark) {
                        // drop all the edges of the graph as they are not used in mapReduce processing
                        computedGraphRDD = computedGraphRDD.mapValues(vertexWritable -> {
                            vertexWritable.get().dropEdges(Direction.BOTH);
                            return vertexWritable;
                        });
                        // if there is only one MapReduce to execute, don't bother wasting the clock cycles.
                        if (this.mapReducers.size() > 1)
                            computedGraphRDD = computedGraphRDD.persist(StorageLevel.fromString(hadoopConfiguration.get(Constants.GREMLIN_SPARK_GRAPH_STORAGE_LEVEL, "MEMORY_ONLY")));
                    }

                    for (final MapReduce mapReduce : this.mapReducers) {
                        // execute the map reduce job
                        final HadoopConfiguration newApacheConfiguration = new HadoopConfiguration(apacheConfiguration);
                        mapReduce.storeState(newApacheConfiguration);
                        // map
                        final JavaPairRDD mapRDD = SparkExecutor.executeMap((JavaPairRDD) computedGraphRDD, mapReduce, newApacheConfiguration);
                        // combine
                        final JavaPairRDD combineRDD = mapReduce.doStage(MapReduce.Stage.COMBINE) ? SparkExecutor.executeCombine(mapRDD, newApacheConfiguration) : mapRDD;
                        // reduce
                        final JavaPairRDD reduceRDD = mapReduce.doStage(MapReduce.Stage.REDUCE) ? SparkExecutor.executeReduce(combineRDD, mapReduce, newApacheConfiguration) : combineRDD;
                        // write the map reduce output back to disk and computer result memory
                        if (null != outputRDD)
                            mapReduce.addResultToMemory(finalMemory, outputRDD.writeMemoryRDD(apacheConfiguration, mapReduce.getMemoryKey(), reduceRDD));

                    }
                }

                // unpersist the loaded graph if it will not be used again (no PersistedInputRDD)
                // if the graphRDD was loaded from Spark, but then partitioned, its a different RDD
                if ((!inputFromSpark || partitioned || filtered) && computedGraphCreated)
                    loadedGraphRDD.unpersist();
                // unpersist the computed graph if it will not be used again (no PersistedOutputRDD)
                if (!outputToSpark || this.persist.equals(GraphComputer.Persist.NOTHING))
                    computedGraphRDD.unpersist();
                // delete any file system or rdd data if persist nothing
                if (null != outputLocation && this.persist.equals(GraphComputer.Persist.NOTHING)) {
                    if (outputToHDFS)
                        fileSystemStorage.rm(outputLocation);
                    if (outputToSpark)
                        sparkContextStorage.rm(outputLocation);
                }
                // update runtime and return the newly computed graph
                finalMemory.setRuntime(System.currentTimeMillis() - startTime);
                return new DefaultComputerResult(InputOutputHelper.getOutputGraph(apacheConfiguration, this.resultGraph, this.persist), finalMemory.asImmutable());
            } finally {
                if (!apacheConfiguration.getBoolean(Constants.GREMLIN_SPARK_PERSIST_CONTEXT, false))
                    Spark.close();
            }
        }, exec);
    }


    /**
     * When using a persistent context the running Context's configuration will override a passed
     * in configuration. Spark allows us to override these inherited properties via
     * SparkContext.setLocalProperty
     */
    private void updateLocalConfiguration(final JavaSparkContext sparkContext, final SparkConf sparkConfiguration) {
        /*
         * While we could enumerate over the entire SparkConfiguration and copy into the Thread
         * Local properties of the Spark Context this could cause adverse effects with future
         * versions of Spark. Since the api for setting multiple local properties at once is
         * restricted as private, we will only set those properties we know can effect SparkGraphComputer
         * Execution rather than applying the entire configuration.
         */
        final String[] validPropertyNames = {
                "spark.job.description",
                "spark.jobGroup.id",
                "spark.job.interruptOnCancel",
                "spark.scheduler.pool"
        };

        for (String propertyName : validPropertyNames) {
            if (sparkConfiguration.contains(propertyName)) {
                String propertyValue = sparkConfiguration.get(propertyName);
                this.logger.info("Setting Thread Local SparkContext Property - "
                        + propertyName + " : " + propertyValue);

                sparkContext.setLocalProperty(propertyName, sparkConfiguration.get(propertyName));
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        final FileConfiguration configuration = new PropertiesConfiguration(args[0]);
        new SparkGraphComputer(KuazFactory.open(configuration)).program(VertexProgram.createVertexProgram(KuazFactory.open(configuration), configuration)).submit().get();
    }
}
