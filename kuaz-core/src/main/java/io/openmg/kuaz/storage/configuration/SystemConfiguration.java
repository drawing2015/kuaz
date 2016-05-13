package io.openmg.kuaz.storage.configuration;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface SystemConfiguration extends Configuration {

    @Override
    public SystemConfiguration restrictTo(final String... umbrellaElements);




}
