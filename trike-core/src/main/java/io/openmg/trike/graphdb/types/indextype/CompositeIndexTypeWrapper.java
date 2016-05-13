package io.openmg.trike.graphdb.types.indextype;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import io.openmg.trike.core.*;
import io.openmg.trike.core.Cardinality;
import io.openmg.trike.core.schema.ConsistencyModifier;
import io.openmg.trike.core.schema.Parameter;
import io.openmg.trike.core.schema.SchemaStatus;
import io.openmg.trike.graphdb.types.ParameterType;
import io.openmg.trike.graphdb.types.*;
import org.apache.tinkerpop.gremlin.structure.Direction;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class CompositeIndexTypeWrapper extends IndexTypeWrapper implements CompositeIndexType {

    public CompositeIndexTypeWrapper(SchemaSource base) {
        super(base);
    }

    @Override
    public boolean isCompositeIndex() {
        return true;
    }

    @Override
    public boolean isMixedIndex() {
        return false;
    }

    @Override
    public long getID() {
        return base.longId();
    }

    @Override
    public SchemaStatus getStatus() {
        return base.getStatus();
    }

    IndexField[] fields = null;

    @Override
    public IndexField[] getFieldKeys() {
        IndexField[] result = fields;
        if (result==null) {
            Iterable<SchemaSource.Entry> entries = base.getRelated(TypeDefinitionCategory.INDEX_FIELD,Direction.OUT);
            int numFields = Iterables.size(entries);
            result = new IndexField[numFields];
            for (SchemaSource.Entry entry : entries) {
                Integer value = ParameterType.INDEX_POSITION.findParameter((Parameter[]) entry.getModifier(),null);
                Preconditions.checkNotNull(value);
                int pos = value;
                Preconditions.checkArgument(pos>=0 && pos<numFields,"Invalid field position: %s",pos);
                assert entry.getSchemaType() instanceof PropertyKey;
                result[pos]=IndexField.of((PropertyKey)entry.getSchemaType());
            }
            fields=result;
        }
        assert result!=null;
        return result;
    }

    @Override
    public void resetCache() {
        super.resetCache();
        fields = null;
    }

    @Override
    public Cardinality getCardinality() {
        return base.getDefinition().getValue(TypeDefinitionCategory.INDEX_CARDINALITY,Cardinality.class);
    }

    private ConsistencyModifier consistency = null;

    public ConsistencyModifier getConsistencyModifier() {
        if (consistency==null) {
            consistency = TypeUtil.getConsistencyModifier(base);
        }
        return consistency;
    }
}
