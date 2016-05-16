package io.openmg.kuaz.structure;

import com.thinkaurelius.titan.diskstorage.configuration.backend.CommonsConfiguration;
import io.openmg.kuaz.structure.configuration.KuazGraphConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * Created by zizai (http://github.com/zizai).
 */
public class KuazFactory {

    public static KuazGraph open(Configuration configuration) {
        return new KuazGraph(new KuazGraphConfiguration(new CommonsConfiguration(configuration)));
    }
}
