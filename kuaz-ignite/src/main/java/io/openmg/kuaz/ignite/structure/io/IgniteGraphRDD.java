package io.openmg.kuaz.ignite.structure.io;

import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.diskstorage.Backend;
import org.apache.commons.configuration.Configuration;
import org.apache.ignite.spark.JavaIgniteContext;
import org.apache.ignite.spark.JavaIgniteRDD;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class IgniteGraphRDD {

    public JavaIgniteRDD<Object, TitanVertex> readGraphRDD(final String graphName, final JavaIgniteContext igniteContext) {
        String cacheName = IgniteStoreManager.getEdgeStoreCacheName(graphName);
        return igniteContext.fromCache(cacheName);
    }

}
