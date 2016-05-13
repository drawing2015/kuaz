package io.openmg.kuaz.diskstorage.indexing;

import com.google.common.base.Preconditions;
import io.openmg.kuaz.core.Cardinality;
import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.core.schema.Parameter;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class StandardKeyInformation implements KeyInformation {

    private final Class<?> dataType;
    private final Parameter[] parameters;
    private final Cardinality cardinality;


    public StandardKeyInformation(Class<?> dataType, Cardinality cardinality, Parameter... parameters) {
        Preconditions.checkNotNull(dataType);
        Preconditions.checkNotNull(parameters);
        this.dataType = dataType;
        this.parameters = parameters;
        this.cardinality = cardinality;
    }

    public StandardKeyInformation(PropertyKey key, Parameter... parameters) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(parameters);
        this.dataType = key.dataType();
        this.parameters = parameters;
        this.cardinality = key.cardinality();
    }


    @Override
    public Class<?> getDataType() {
        return dataType;
    }

    public boolean hasParameters() {
        return parameters.length>0;
    }

    @Override
    public Parameter[] getParameters() {
        return parameters;
    }

    @Override
    public Cardinality getCardinality() {
        return cardinality;
    }

}
