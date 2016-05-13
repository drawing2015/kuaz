package io.openmg.kuaz.diskstorage.locking;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface LockerProvider {

    public Locker getLocker(String lockerName);

}
