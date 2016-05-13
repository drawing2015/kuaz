package io.openmg.trike.graphdb.database.management;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import io.openmg.trike.core.PropertyKey;
import io.openmg.trike.core.TitanGraph;
import io.openmg.trike.core.schema.SchemaStatus;
import io.openmg.trike.core.schema.TitanGraphIndex;
import io.openmg.trike.core.schema.TitanManagement;
import io.openmg.trike.diskstorage.util.time.Timer;
import io.openmg.trike.diskstorage.util.time.TimestampProviders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GraphIndexStatusWatcher
        extends AbstractIndexStatusWatcher<GraphIndexStatusReport, GraphIndexStatusWatcher> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphIndexStatusWatcher.class);

    private String graphIndexName;

    public GraphIndexStatusWatcher(TitanGraph g, String graphIndexName) {
        super(g);
        this.graphIndexName = graphIndexName;
    }

    @Override
    protected GraphIndexStatusWatcher self() {
        return this;
    }

    @Override
    public GraphIndexStatusReport call() throws InterruptedException {
        Preconditions.checkNotNull(g, "Graph instance must not be null");
        Preconditions.checkNotNull(graphIndexName, "Index name must not be null");
        Preconditions.checkNotNull(status, "Target status must not be null");

        Map<String, SchemaStatus> notConverged = new HashMap<>();
        Map<String, SchemaStatus> converged = new HashMap<>();
        TitanGraphIndex idx;

        Timer t = new Timer(TimestampProviders.MILLI).start();
        boolean timedOut;
        while (true) {
            TitanManagement mgmt = null;
            try {
                mgmt = g.openManagement();
                idx = mgmt.getGraphIndex(graphIndexName);
                for (PropertyKey pk : idx.getFieldKeys()) {
                    SchemaStatus s = idx.getIndexStatus(pk);
                    LOGGER.debug("Key {} has status {}", pk, s);
                    if (!status.equals(s))
                        notConverged.put(pk.toString(), s);
                    else
                        converged.put(pk.toString(), s);
                }
            } finally {
                if (null != mgmt)
                    mgmt.rollback(); // Let an exception here propagate up the stack
            }

            String waitingOn = Joiner.on(",").withKeyValueSeparator("=").join(notConverged);
            if (!notConverged.isEmpty()) {
                LOGGER.info("Some key(s) on index {} do not currently have status {}: {}", graphIndexName, status, waitingOn);
            } else {
                LOGGER.info("All {} key(s) on index {} have status {}", converged.size(), graphIndexName, status);
                return new GraphIndexStatusReport(true, graphIndexName, status, notConverged, converged, t.elapsed());
            }

            timedOut = null != timeout && 0 < t.elapsed().compareTo(timeout);

            if (timedOut) {
                LOGGER.info("Timed out ({}) while waiting for index {} to converge on status {}",
                        timeout, graphIndexName, status);
                return new GraphIndexStatusReport(false, graphIndexName, status, notConverged, converged, t.elapsed());
            }
            notConverged.clear();
            converged.clear();

            Thread.sleep(poll.toMillis());
        }
    }
}
