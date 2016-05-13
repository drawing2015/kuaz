package io.openmg.trike.util.datastructures;

/**
 * Factory interface for the factory design pattern
 *
 * @param <O> Type created by the factory
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface Factory<O> {

    public O create();

}
