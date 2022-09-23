// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;

public class NullifyRelationFieldManager extends AbstractFieldManager
{
    private final ObjectProvider op;
    
    public NullifyRelationFieldManager(final ObjectProvider op) {
        this.op = op;
    }
    
    @Override
    public Object fetchObjectField(final int fieldNumber) {
        final Object value = this.op.provideField(fieldNumber);
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        final RelationType relType = mmd.getRelationType(this.op.getExecutionContext().getClassLoaderResolver());
        if (value == null) {
            return null;
        }
        if (RelationType.isRelationSingleValued(relType)) {
            this.op.makeDirty(fieldNumber);
            return null;
        }
        if (RelationType.isRelationMultiValued(relType)) {
            if (value instanceof Collection) {
                this.op.makeDirty(fieldNumber);
                ((Collection)value).clear();
                return value;
            }
            if (value instanceof Map) {
                this.op.makeDirty(fieldNumber);
                ((Map)value).clear();
                return value;
            }
            if (!value.getClass().isArray() || Object.class.isAssignableFrom(value.getClass().getComponentType())) {}
        }
        return value;
    }
    
    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        return true;
    }
    
    @Override
    public char fetchCharField(final int fieldNumber) {
        return '0';
    }
    
    @Override
    public byte fetchByteField(final int fieldNumber) {
        return 0;
    }
    
    @Override
    public double fetchDoubleField(final int fieldNumber) {
        return 0.0;
    }
    
    @Override
    public float fetchFloatField(final int fieldNumber) {
        return 0.0f;
    }
    
    @Override
    public int fetchIntField(final int fieldNumber) {
        return 0;
    }
    
    @Override
    public long fetchLongField(final int fieldNumber) {
        return 0L;
    }
    
    @Override
    public short fetchShortField(final int fieldNumber) {
        return 0;
    }
    
    @Override
    public String fetchStringField(final int fieldNumber) {
        return "";
    }
}
