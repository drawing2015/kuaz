package io.openmg.kuaz.graphdb.util;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class ExceptionFactory {

    public static final void graphShutdown() {
        throw new IllegalStateException("Graph has been shut down");
    }

}
