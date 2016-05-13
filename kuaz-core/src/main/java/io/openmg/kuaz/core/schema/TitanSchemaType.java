package io.openmg.kuaz.core.schema;

/**
 * A TitanSchemaType is a {@link TitanSchemaElement} that represents a label or key
 * used in the graph. As such, a schema type is either a {@link io.openmg.kuaz.core.RelationType}
 * or a {@link io.openmg.kuaz.core.VertexLabel}.
 * <p/>
 * TitanSchemaTypes are a special {@link TitanSchemaElement} in that they are referenced from the
 * main graph when creating vertices, edges, and properties.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface TitanSchemaType extends TitanSchemaElement {
}
