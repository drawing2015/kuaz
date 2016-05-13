package io.openmg.kuaz.diskstorage.util;

import java.io.IOException;
import java.util.concurrent.Callable;

public interface IOCallable<T> extends Callable<T> {

    @Override
    public T call() throws IOException;
}