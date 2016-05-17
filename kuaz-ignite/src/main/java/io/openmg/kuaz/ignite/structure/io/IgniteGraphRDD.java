package io.openmg.kuaz.ignite.structure.io;

import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.diskstorage.Backend;
import org.apache.spark.api.java.JavaPairRDD;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class IgniteGraphRDD {

    public JavaPairRDD<Object, TitanVertex> readGraphRDD(final IgniteStoreManager igniteStoreManager) {
        String cacheName = igniteStoreManager.getCacheName(Backend.EDGESTORE_NAME);
        return igniteStoreManager.getIgniteContext().fromCache(cacheName).mapValues(v -> v);
    }

    public void writeGraphRDD(final IgniteStoreManager igniteStoreManager, final JavaPairRDD graphRDD) {
        String cacheName = igniteStoreManager.getCacheName(Backend.EDGESTORE_NAME);
        igniteStoreManager.getIgniteContext().fromCache(cacheName).savePairs(graphRDD);
    }

}
