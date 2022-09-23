// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.util.List;
import java.lang.reflect.Array;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.scostore.ArrayStore;
import org.datanucleus.store.exceptions.ReachableObjectNotCascadedException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class ArrayMapping extends AbstractContainerMapping implements MappingCallbacks
{
    @Override
    public Class getJavaType() {
        if (this.mmd != null) {
            return this.mmd.getType();
        }
        return null;
    }
    
    @Override
    protected boolean containerIsStoredInSingleColumn() {
        return super.containerIsStoredInSingleColumn() || (this.mmd != null && this.mmd.hasArray() && this.mmd.getJoinMetaData() == null && MetaDataUtils.getInstance().arrayStorableAsByteArrayInSingleColumn(this.mmd));
    }
    
    @Override
    public void insertPostProcessing(final ObjectProvider op) {
    }
    
    @Override
    public void postInsert(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final Object value = ownerOP.provideField(this.getAbsoluteFieldNumber());
        if (this.containerIsStoredInSingleColumn()) {
            SCOUtils.validateObjectsForWriting(ec, value);
            return;
        }
        if (value == null) {
            return;
        }
        if (!this.mmd.isCascadePersist()) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(ArrayMapping.LOCALISER.msg("007006", this.mmd.getFullFieldName()));
            }
            if (!this.mmd.getType().getComponentType().isPrimitive()) {
                final Object[] array = (Object[])value;
                for (int i = 0; i < array.length; ++i) {
                    if (!ec.getApiAdapter().isDetached(array[i]) && !ec.getApiAdapter().isPersistent(array[i])) {
                        throw new ReachableObjectNotCascadedException(this.mmd.getFullFieldName(), array[i]);
                    }
                }
            }
        }
        else {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(ArrayMapping.LOCALISER.msg("007007", this.mmd.getFullFieldName()));
            }
            ((ArrayStore)this.storeMgr.getBackingStoreForField(ownerOP.getExecutionContext().getClassLoaderResolver(), this.mmd, null)).set(ownerOP, value);
        }
    }
    
    @Override
    public void postFetch(final ObjectProvider sm) {
        if (this.containerIsStoredInSingleColumn()) {
            return;
        }
        final List elements = ((ArrayStore)this.storeMgr.getBackingStoreForField(sm.getExecutionContext().getClassLoaderResolver(), this.mmd, null)).getArray(sm);
        if (elements != null) {
            final boolean primitiveArray = this.mmd.getType().getComponentType().isPrimitive();
            final Object array = Array.newInstance(this.mmd.getType().getComponentType(), elements.size());
            for (int i = 0; i < elements.size(); ++i) {
                final Object element = elements.get(i);
                if (primitiveArray) {
                    if (element instanceof Boolean) {
                        Array.setBoolean(array, i, (boolean)element);
                    }
                    else if (element instanceof Byte) {
                        Array.setByte(array, i, (byte)element);
                    }
                    else if (element instanceof Character) {
                        Array.setChar(array, i, (char)element);
                    }
                    else if (element instanceof Double) {
                        Array.setDouble(array, i, (double)element);
                    }
                    else if (element instanceof Float) {
                        Array.setFloat(array, i, (float)element);
                    }
                    else if (element instanceof Integer) {
                        Array.setInt(array, i, (int)element);
                    }
                    else if (element instanceof Long) {
                        Array.setLong(array, i, (long)element);
                    }
                    else if (element instanceof Short) {
                        Array.setShort(array, i, (short)element);
                    }
                }
                else {
                    Array.set(array, i, element);
                }
            }
            if (elements.size() == 0) {
                sm.replaceFieldMakeDirty(this.getAbsoluteFieldNumber(), null);
            }
            else {
                sm.replaceFieldMakeDirty(this.getAbsoluteFieldNumber(), array);
            }
        }
        else {
            sm.replaceFieldMakeDirty(this.getAbsoluteFieldNumber(), null);
        }
    }
    
    @Override
    public void postUpdate(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final Object value = ownerOP.provideField(this.getAbsoluteFieldNumber());
        if (this.containerIsStoredInSingleColumn()) {
            SCOUtils.validateObjectsForWriting(ec, value);
            return;
        }
        if (value == null) {
            ((ArrayStore)this.storeMgr.getBackingStoreForField(ec.getClassLoaderResolver(), this.mmd, null)).clear(ownerOP);
            return;
        }
        if (!this.mmd.isCascadeUpdate()) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(ArrayMapping.LOCALISER.msg("007008", this.mmd.getFullFieldName()));
            }
            return;
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ArrayMapping.LOCALISER.msg("007009", this.mmd.getFullFieldName()));
        }
        final ArrayStore backingStore = (ArrayStore)this.storeMgr.getBackingStoreForField(ec.getClassLoaderResolver(), this.mmd, null);
        backingStore.clear(ownerOP);
        backingStore.set(ownerOP, value);
    }
    
    @Override
    public void preDelete(final ObjectProvider sm) {
        if (this.containerIsStoredInSingleColumn()) {
            return;
        }
        sm.isLoaded(this.getAbsoluteFieldNumber());
        final Object value = sm.provideField(this.getAbsoluteFieldNumber());
        if (value == null) {
            return;
        }
        final ArrayStore backingStore = (ArrayStore)this.storeMgr.getBackingStoreForField(sm.getExecutionContext().getClassLoaderResolver(), this.mmd, null);
        backingStore.clear(sm);
    }
}
