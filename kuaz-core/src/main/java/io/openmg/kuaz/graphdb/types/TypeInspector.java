package io.openmg.kuaz.graphdb.types;

import io.openmg.kuaz.core.EdgeLabel;
import io.openmg.kuaz.core.PropertyKey;
import io.openmg.kuaz.core.RelationType;
import io.openmg.kuaz.core.VertexLabel;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface TypeInspector {

    public default PropertyKey getExistingPropertyKey(long id) {
        return (PropertyKey)getExistingRelationType(id);
    }

    public default EdgeLabel getExistingEdgeLabel(long id) {
        return (EdgeLabel)getExistingRelationType(id);
    }

    public RelationType getExistingRelationType(long id);

    public VertexLabel getExistingVertexLabel(long id);

    public boolean containsRelationType(String name);

    public RelationType getRelationType(String name);

}
