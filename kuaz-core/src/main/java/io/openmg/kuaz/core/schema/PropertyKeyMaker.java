package io.openmg.kuaz.core.schema;

import io.openmg.kuaz.core.Cardinality;
import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.core.RelationType;

/**
 * Used to define new {@link io.openmg.kuaz.core.PropertyKey}s.
 * An property key is defined by its name, {@link Cardinality}, its data type, and its signature - all of which
 * can be specified in this builder.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface PropertyKeyMaker extends RelationTypeMaker {

    /**
     * Configures the {@link io.openmg.kuaz.core.Cardinality} of this property key.
     *
     * @param cardinality
     * @return this PropertyKeyMaker
     */
    public PropertyKeyMaker cardinality(Cardinality cardinality);

    /**
     * Configures the data type for this property key.
     * <p/>
     * Property instances for this key will only accept values that are instances of this class.
     * Every property key must have its data type configured. Setting the data type to Object.class allows
     * any type of value but comes at the expense of longer serialization because class information
     * is stored with the value.
     * <p/>
     * It is strongly advised to pick an appropriate data type class so Titan can enforce it throughout the database.
     *
     * @param clazz Data type to be configured.
     * @return this PropertyKeyMaker
     * @see io.openmg.kuaz.core.PropertyKey#dataType()
     */
    public PropertyKeyMaker dataType(Class<?> clazz);

    @Override
    public PropertyKeyMaker signature(PropertyKey... types);


    /**
     * Defines the {@link io.openmg.kuaz.core.PropertyKey} specified by this PropertyKeyMaker and returns the resulting key.
     *
     * @return the created {@link PropertyKey}
     */
    @Override
    public PropertyKey make();
}
