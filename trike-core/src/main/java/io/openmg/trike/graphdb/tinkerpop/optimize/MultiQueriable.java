package io.openmg.trike.graphdb.tinkerpop.optimize;

import org.apache.tinkerpop.gremlin.process.traversal.Step;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface MultiQueriable<S,E> extends Step<S,E> {

    void setUseMultiQuery(boolean useMultiQuery);

}
