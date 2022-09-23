// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import java.util.Set;
import java.util.Iterator;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;

public class DeleteFieldManager extends AbstractFieldManager
{
    private final ObjectProvider op;
    private boolean nullBidirIfNotDependent;
    
    public DeleteFieldManager(final ObjectProvider op) {
        this(op, false);
    }
    
    public DeleteFieldManager(final ObjectProvider op, final boolean nullBidirIfNotDependent) {
        this.nullBidirIfNotDependent = false;
        this.op = op;
        this.nullBidirIfNotDependent = nullBidirIfNotDependent;
    }
    
    protected void processPersistable(final Object pc) {
        final ObjectProvider pcOP = this.op.getExecutionContext().findObjectProvider(pc);
        if (pcOP != null && (pcOP.isDeleting() || pcOP.becomingDeleted())) {
            return;
        }
        this.op.getExecutionContext().deleteObjectInternal(pc);
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        if (value != null) {
            final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final ExecutionContext ec = this.op.getExecutionContext();
            final RelationType relationType = mmd.getRelationType(ec.getClassLoaderResolver());
            if (RelationType.isRelationSingleValued(relationType)) {
                if (mmd.isDependent()) {
                    this.processPersistable(value);
                }
                else if (this.nullBidirIfNotDependent && RelationType.isBidirectional(relationType) && !mmd.isEmbedded()) {
                    final ObjectProvider valueOP = ec.findObjectProvider(value);
                    if (valueOP != null && !valueOP.getLifecycleState().isDeleted() && !valueOP.isDeleting()) {
                        final AbstractMemberMetaData relMmd = mmd.getRelatedMemberMetaData(ec.getClassLoaderResolver())[0];
                        valueOP.replaceFieldMakeDirty(relMmd.getAbsoluteFieldNumber(), null);
                        valueOP.flush();
                    }
                }
            }
            else if (RelationType.isRelationMultiValued(relationType)) {
                final ApiAdapter api = ec.getApiAdapter();
                if (value instanceof Collection) {
                    boolean dependent = mmd.getCollection().isDependentElement();
                    if (mmd.isCascadeRemoveOrphans()) {
                        dependent = true;
                    }
                    if (dependent) {
                        final Collection coll = (Collection)value;
                        for (final Object element : coll) {
                            if (api.isPersistable(element)) {
                                this.processPersistable(element);
                            }
                        }
                    }
                    else if (this.nullBidirIfNotDependent && RelationType.isBidirectional(relationType) && !mmd.isEmbedded() && !mmd.getCollection().isEmbeddedElement() && relationType == RelationType.ONE_TO_MANY_BI) {
                        final Collection coll = (Collection)value;
                        for (final Object element : coll) {
                            if (api.isPersistable(element)) {
                                final ObjectProvider elementOP = ec.findObjectProvider(element);
                                if (elementOP == null || elementOP.getLifecycleState().isDeleted() || elementOP.isDeleting()) {
                                    continue;
                                }
                                final AbstractMemberMetaData relMmd2 = mmd.getRelatedMemberMetaData(ec.getClassLoaderResolver())[0];
                                elementOP.replaceFieldMakeDirty(relMmd2.getAbsoluteFieldNumber(), null);
                                elementOP.flush();
                            }
                        }
                    }
                }
                else if (value instanceof Map) {
                    final Map map = (Map)value;
                    if (mmd.hasMap() && mmd.getMap().isDependentKey()) {
                        final Set keys = map.keySet();
                        for (final Object mapKey : keys) {
                            if (api.isPersistable(mapKey)) {
                                this.processPersistable(mapKey);
                            }
                        }
                    }
                    if (mmd.hasMap() && mmd.getMap().isDependentValue()) {
                        final Collection values = map.values();
                        for (final Object mapValue : values) {
                            if (api.isPersistable(mapValue)) {
                                this.processPersistable(mapValue);
                            }
                        }
                    }
                }
                else if (value instanceof Object[] && mmd.hasArray() && mmd.getArray().isDependentElement()) {
                    for (int i = 0; i < Array.getLength(value); ++i) {
                        final Object element2 = Array.get(value, i);
                        if (api.isPersistable(element2)) {
                            this.processPersistable(element2);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
    }
}
