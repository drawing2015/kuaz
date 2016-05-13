package io.openmg.kuaz.diskstorage.util;

import java.util.concurrent.Callable;

public interface UncheckedCallable<T> extends Callable<T> {
    
    @Override
    public T call();
}
