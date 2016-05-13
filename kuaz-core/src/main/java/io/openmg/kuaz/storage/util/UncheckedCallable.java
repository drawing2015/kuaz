package io.openmg.kuaz.storage.util;

import java.util.concurrent.Callable;

public interface UncheckedCallable<T> extends Callable<T> {
    
    @Override
    public T call();
}
