package io.openmg.kuaz.core.schema;

import io.openmg.kuaz.core.EdgeLabel;
import io.openmg.kuaz.core.Multiplicity;
import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.core.RelationType;

/**
 * Used to define new {@link io.openmg.kuaz.core.EdgeLabel}s.
 * An edge label is defined by its name, {@link Multiplicity}, its directionality, and its signature - all of which
 * can be specified in this builder.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface EdgeLabelMaker extends RelationTypeMaker {

    /**
     * Sets the multiplicity of this label. The default multiplicity is {@link io.openmg.kuaz.core.Multiplicity#MULTI}.
     * @return this EdgeLabelMaker
     * @see Multiplicity
     */
    public EdgeLabelMaker multiplicity(Multiplicity multiplicity);

    /**
     * Configures the label to be directed.
     * <p/>
     * By default, the label is directed.
     *
     * @return this EdgeLabelMaker
     * @see io.openmg.kuaz.core.EdgeLabel#isDirected()
     */
    public EdgeLabelMaker directed();

    /**
     * Configures the label to be unidirected.
     * <p/>
     * By default, the type is directed.
     *
     * @return this EdgeLabelMaker
     * @see io.openmg.kuaz.core.EdgeLabel#isUnidirected()
     */
    public EdgeLabelMaker unidirected();


    @Override
    public EdgeLabelMaker signature(PropertyKey... types);


    /**
     * Defines the {@link io.openmg.kuaz.core.EdgeLabel} specified by this EdgeLabelMaker and returns the resulting label
     *
     * @return the created {@link EdgeLabel}
     */
    @Override
    public EdgeLabel make();

}
