package io.openmg.kuaz.graphdb.database.cache;

import io.openmg.kuaz.diskstorage.EntryList;
import io.openmg.kuaz.diskstorage.util.CacheMetricsAction;
import io.openmg.kuaz.graphdb.configuration.GraphDatabaseConfiguration;
import io.openmg.kuaz.graphdb.transaction.StandardTitanTx;
import io.openmg.kuaz.graphdb.types.system.BaseRelationType;
import io.openmg.kuaz.util.stats.MetricManager;
import org.apache.tinkerpop.gremlin.structure.Direction;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class MetricInstrumentedSchemaCache implements SchemaCache {

    public static final String METRICS_NAME = "schemacache";

    public static final String METRICS_TYPENAME = "name";
    public static final String METRICS_RELATIONS = "relations";

    private final SchemaCache cache;

    public MetricInstrumentedSchemaCache(final StoreRetrieval retriever) {
        cache = new StandardSchemaCache(new StoreRetrieval() {
            @Override
            public Long retrieveSchemaByName(String typeName) {
                incAction(METRICS_TYPENAME,CacheMetricsAction.MISS);
                return retriever.retrieveSchemaByName(typeName);
            }

            @Override
            public EntryList retrieveSchemaRelations(long schemaId, BaseRelationType type, Direction dir) {
                incAction(METRICS_RELATIONS,CacheMetricsAction.MISS);
                return retriever.retrieveSchemaRelations(schemaId, type, dir);
            }
        });
    }

    private void incAction(String type, CacheMetricsAction action) {
        MetricManager.INSTANCE.getCounter(GraphDatabaseConfiguration.METRICS_SYSTEM_PREFIX_DEFAULT, METRICS_NAME, type, action.getName()).inc();
    }

    @Override
    public Long getSchemaId(String schemaName) {
        incAction(METRICS_TYPENAME,CacheMetricsAction.RETRIEVAL);
        return cache.getSchemaId(schemaName);
    }

    @Override
    public EntryList getSchemaRelations(long schemaId, BaseRelationType type, Direction dir) {
        incAction(METRICS_RELATIONS,CacheMetricsAction.RETRIEVAL);
        return cache.getSchemaRelations(schemaId, type, dir);
    }

    @Override
    public void expireSchemaElement(long schemaId) {
        cache.expireSchemaElement(schemaId);
    }

}
