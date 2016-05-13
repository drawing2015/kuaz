package io.openmg.kuaz.storage.locking.consistentkey;


import io.openmg.kuaz.storage.locking.TemporaryLockingException;

public class ExpiredLockException extends TemporaryLockingException {

    public ExpiredLockException(String msg) {
        super(msg);
    }

    public ExpiredLockException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ExpiredLockException(Throwable cause) {
        super(cause);
    }
}
