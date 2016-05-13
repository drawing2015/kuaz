package io.openmg.kuaz.graphdb.schema;

import io.openmg.kuaz.core.VertexLabel;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class VertexLabelDefinition extends SchemaElementDefinition {

    private final boolean isPartitioned;
    private final boolean isStatic;

    public VertexLabelDefinition(String name, long id, boolean isPartitioned, boolean isStatic) {
        super(name, id);
        this.isPartitioned = isPartitioned;
        this.isStatic = isStatic;
    }

    public VertexLabelDefinition(VertexLabel vl) {
        this(vl.name(),vl.longId(),vl.isPartitioned(),vl.isStatic());
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isPartitioned() {
        return isPartitioned;
    }

    public boolean hasDefaultConfiguration() {
        return isPartitioned==false && isStatic==false;
    }

}