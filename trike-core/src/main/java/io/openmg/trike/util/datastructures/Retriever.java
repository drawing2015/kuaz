package io.openmg.trike.util.datastructures;

import java.util.Collections;

/**
 * Interface for the Retriever design pattern.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface Retriever<I,O> {

    public O get(I input);

}
