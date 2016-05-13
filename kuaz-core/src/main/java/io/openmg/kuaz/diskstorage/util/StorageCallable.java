package io.openmg.kuaz.diskstorage.util;

import java.util.concurrent.Callable;

import io.openmg.kuaz.diskstorage.BackendException;

/**
 * Like {@link Callable}, except the exception type is narrowed from
 * {@link Exception} to {@link io.openmg.kuaz.diskstorage.BackendException}.
 * 
 * @param <T>
 *            call return type
 */
public interface StorageCallable<T> extends Callable<T> {

    @Override
    public T call() throws BackendException;
}
