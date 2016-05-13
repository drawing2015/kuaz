package io.openmg.kuaz.storage.configuration;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface ConcurrentWriteConfiguration extends WriteConfiguration {

    public<O> void set(String key, O value, O expectedValue);

}
