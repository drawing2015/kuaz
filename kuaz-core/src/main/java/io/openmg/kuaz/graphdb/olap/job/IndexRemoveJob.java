package io.openmg.kuaz.graphdb.olap.job;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.openmg.kuaz.core.TitanException;
import io.openmg.kuaz.core.TitanGraph;
import io.openmg.kuaz.core.schema.RelationTypeIndex;
import io.openmg.kuaz.core.schema.SchemaStatus;
import io.openmg.kuaz.core.schema.TitanGraphIndex;
import io.openmg.kuaz.diskstorage.BackendTransaction;
import io.openmg.kuaz.diskstorage.Entry;
import io.openmg.kuaz.diskstorage.EntryList;
import io.openmg.kuaz.diskstorage.StaticBuffer;
import io.openmg.kuaz.diskstorage.configuration.ConfigElement;
import io.openmg.kuaz.diskstorage.configuration.ConfigOption;
import io.openmg.kuaz.diskstorage.configuration.Configuration;
import io.openmg.kuaz.diskstorage.configuration.ModifiableConfiguration;
import io.openmg.kuaz.diskstorage.keycolumnvalue.SliceQuery;
import io.openmg.kuaz.diskstorage.keycolumnvalue.cache.KCVSCache;
import io.openmg.kuaz.diskstorage.keycolumnvalue.scan.ScanJob;
import io.openmg.kuaz.diskstorage.keycolumnvalue.scan.ScanMetrics;
import io.openmg.kuaz.diskstorage.util.BufferUtil;
import io.openmg.kuaz.graphdb.configuration.GraphDatabaseConfiguration;
import io.openmg.kuaz.graphdb.database.IndexSerializer;
import io.openmg.kuaz.graphdb.database.StandardTitanGraph;
import io.openmg.kuaz.graphdb.database.management.ManagementSystem;
import io.openmg.kuaz.graphdb.database.management.RelationTypeIndexWrapper;
import io.openmg.kuaz.graphdb.idmanagement.IDManager;
import io.openmg.kuaz.graphdb.internal.InternalRelationType;
import io.openmg.kuaz.graphdb.olap.QueryContainer;
import io.openmg.kuaz.graphdb.olap.VertexJobConverter;
import io.openmg.kuaz.graphdb.transaction.StandardTitanTx;
import io.openmg.kuaz.graphdb.types.CompositeIndexType;
import io.openmg.kuaz.graphdb.types.IndexType;
import io.openmg.kuaz.graphdb.types.vertices.TitanSchemaVertex;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class IndexRemoveJob extends IndexUpdateJob implements ScanJob {

    private final VertexJobConverter.GraphProvider graph = new VertexJobConverter.GraphProvider();

    public static final String DELETED_RECORDS_COUNT = "deletes";

    private IndexSerializer indexSerializer;
    private long graphIndexId;
    private IDManager idManager;

    public IndexRemoveJob() {
        super();
    }

    protected IndexRemoveJob(IndexRemoveJob copy) {
        super(copy);
        if (copy.graph.isProvided()) this.graph.setGraph(copy.graph.get());
    }

    public IndexRemoveJob(final TitanGraph graph, final String indexName, final String indexType) {
        super(indexName,indexType);
        this.graph.setGraph(graph);
    }

    @Override
    public void workerIterationEnd(ScanMetrics metrics) {
        super.workerIterationEnd(metrics);
        graph.close();
    }

    @Override
    public void workerIterationStart(Configuration config, Configuration graphConf, ScanMetrics metrics) {
        graph.initializeGraph(graphConf);
        indexSerializer = graph.get().getIndexSerializer();
        idManager = graph.get().getIDManager();
        try {
            super.workerIterationStart(graph.get(), config, metrics);
        } catch (Throwable e) {
            graph.close();
            throw e;
        }
    }

    @Override
    protected void validateIndexStatus() {
        if (index instanceof RelationTypeIndex) {
            //Nothing specific to be done
        } else if (index instanceof TitanGraphIndex) {
            TitanGraphIndex gindex = (TitanGraphIndex)index;
            if (gindex.isMixedIndex())
                throw new UnsupportedOperationException("Cannot remove mixed indexes through Titan. This can " +
                        "only be accomplished in the indexing system directly.");
            CompositeIndexType indexType = (CompositeIndexType)mgmt.getSchemaVertex(index).asIndexType();
            graphIndexId = indexType.getID();
        } else throw new UnsupportedOperationException("Unsupported index found: "+index);

        //Must be a relation type index or a composite graph index
        TitanSchemaVertex schemaVertex = mgmt.getSchemaVertex(index);
        SchemaStatus actualStatus = schemaVertex.getStatus();
        Preconditions.checkArgument(actualStatus==SchemaStatus.DISABLED,"The index [%s] must be disabled before it can be removed",indexName);
    }

    @Override
    public void process(StaticBuffer key, Map<SliceQuery, EntryList> entries, ScanMetrics metrics) {
        //The queries are already tailored enough => everything should be removed
        try {
            BackendTransaction mutator = writeTx.getTxHandle();
            final List<Entry> deletions;
            if (entries.size()==1) deletions = Iterables.getOnlyElement(entries.values());
            else {
                int size = IteratorUtils.stream(entries.values().iterator()).map( e -> e.size()).reduce(0, (x,y) -> x+y);
                deletions = new ArrayList<>(size);
                entries.values().forEach(e -> deletions.addAll(e));
            }
            metrics.incrementCustom(DELETED_RECORDS_COUNT,deletions.size());
            if (isRelationTypeIndex()) {
                mutator.mutateEdges(key, KCVSCache.NO_ADDITIONS, deletions);
            } else {
                mutator.mutateIndex(key, KCVSCache.NO_ADDITIONS, deletions);
            }
        } catch (final Exception e) {
            mgmt.rollback();
            writeTx.rollback();
            metrics.incrementCustom(FAILED_TX);
            throw new TitanException(e.getMessage(), e);
        }
    }

    @Override
    public List<SliceQuery> getQueries() {
        if (isGlobalGraphIndex()) {
            //Everything
            return ImmutableList.of(new SliceQuery(BufferUtil.zeroBuffer(1), BufferUtil.oneBuffer(128)));
        } else {
            RelationTypeIndexWrapper wrapper = (RelationTypeIndexWrapper)index;
            InternalRelationType wrappedType = wrapper.getWrappedType();
            Direction direction=null;
            for (Direction dir : Direction.values()) if (wrappedType.isUnidirected(dir)) direction=dir;
            assert direction!=null;

            StandardTitanTx tx = (StandardTitanTx)graph.get().buildTransaction().readOnly().start();
            try {
                QueryContainer qc = new QueryContainer(tx);
                qc.addQuery().type(wrappedType).direction(direction).relations();
                return qc.getSliceQueries();
            } finally {
                tx.rollback();
            }
        }
    }

    @Override
    public Predicate<StaticBuffer> getKeyFilter() {
        if (isGlobalGraphIndex()) {
            assert graphIndexId>0;
            return (k -> {
                try {
                    return indexSerializer.getIndexIdFromKey(k) == graphIndexId;
                } catch (RuntimeException e) {
                    log.error("Filtering key {} due to exception", k, e);
                    return false;
                }
            });
        } else {
            return buffer -> {
                long vertexId = idManager.getKeyID(buffer);
                if (IDManager.VertexIDType.Invisible.is(vertexId)) return false;
                else return true;
            };
        }
    }

    @Override
    public IndexRemoveJob clone() {
        return new IndexRemoveJob(this);
    }
}
