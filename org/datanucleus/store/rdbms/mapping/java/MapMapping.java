// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.types.SCO;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.types.backed.BackedSCO;
import org.datanucleus.store.types.SCOContainer;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.store.exceptions.ReachableObjectNotCascadedException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.state.ObjectProvider;
import java.util.Map;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class MapMapping extends AbstractContainerMapping implements MappingCallbacks
{
    @Override
    public Class getJavaType() {
        return Map.class;
    }
    
    @Override
    public void insertPostProcessing(final ObjectProvider ownerOP) {
    }
    
    @Override
    public void postInsert(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final Map value = (Map)ownerOP.provideField(this.getAbsoluteFieldNumber());
        if (this.containerIsStoredInSingleColumn()) {
            if (value != null) {
                SCOUtils.validateObjectsForWriting(ec, value.keySet());
                SCOUtils.validateObjectsForWriting(ec, value.values());
            }
            return;
        }
        if (value == null) {
            this.replaceFieldWithWrapper(ownerOP, null, false, false);
            return;
        }
        if (!this.mmd.isCascadePersist()) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(MapMapping.LOCALISER.msg("007006", this.mmd.getFullFieldName()));
            }
            final ApiAdapter api = ec.getApiAdapter();
            final Set entries = value.entrySet();
            for (final Map.Entry entry : entries) {
                if (api.isPersistable(entry.getKey()) && !api.isPersistent(entry.getKey()) && !api.isDetached(entry.getKey())) {
                    throw new ReachableObjectNotCascadedException(this.mmd.getFullFieldName(), entry.getKey());
                }
                if (api.isPersistable(entry.getValue()) && !api.isPersistent(entry.getValue()) && !api.isDetached(entry.getValue())) {
                    throw new ReachableObjectNotCascadedException(this.mmd.getFullFieldName(), entry.getValue());
                }
            }
            this.replaceFieldWithWrapper(ownerOP, value, false, false);
        }
        else {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(MapMapping.LOCALISER.msg("007007", this.mmd.getFullFieldName()));
            }
            if (value.size() > 0) {
                final RDBMSStoreManager storeMgr = this.table.getStoreManager();
                ((MapStore)storeMgr.getBackingStoreForField(ownerOP.getExecutionContext().getClassLoaderResolver(), this.mmd, value.getClass())).putAll(ownerOP, value);
                this.replaceFieldWithWrapper(ownerOP, value, false, false);
                ec.flushInternal(true);
            }
            else if (this.mmd.getRelationType(ownerOP.getExecutionContext().getClassLoaderResolver()) == RelationType.MANY_TO_MANY_BI) {
                this.replaceFieldWithWrapper(ownerOP, null, false, false);
            }
            else {
                this.replaceFieldWithWrapper(ownerOP, value, false, false);
            }
        }
    }
    
    @Override
    public void postUpdate(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final RDBMSStoreManager storeMgr = this.table.getStoreManager();
        final Map value = (Map)ownerOP.provideField(this.getAbsoluteFieldNumber());
        if (this.containerIsStoredInSingleColumn()) {
            if (value != null) {
                SCOUtils.validateObjectsForWriting(ec, value.keySet());
                SCOUtils.validateObjectsForWriting(ec, value.values());
            }
            return;
        }
        if (value == null) {
            this.replaceFieldWithWrapper(ownerOP, null, false, false);
            return;
        }
        if (value instanceof SCOContainer) {
            final SCOContainer sco = (SCOContainer)value;
            if (ownerOP.getObject() == sco.getOwner() && this.mmd.getName().equals(sco.getFieldName())) {
                ownerOP.getExecutionContext().flushOperationsForBackingStore(((BackedSCO)sco).getBackingStore(), ownerOP);
                return;
            }
            if (sco.getOwner() != null) {
                throw new NucleusException("Owned second-class object was somehow assigned to a field other than its owner's").setFatal();
            }
        }
        if (!this.mmd.isCascadeUpdate()) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(MapMapping.LOCALISER.msg("007008", this.mmd.getFullFieldName()));
            }
            return;
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(MapMapping.LOCALISER.msg("007009", this.mmd.getFullFieldName()));
        }
        final MapStore store = (MapStore)storeMgr.getBackingStoreForField(ec.getClassLoaderResolver(), this.mmd, value.getClass());
        store.clear(ownerOP);
        store.putAll(ownerOP, value);
        this.replaceFieldWithWrapper(ownerOP, value, false, false);
    }
    
    @Override
    public void preDelete(final ObjectProvider ownerOP) {
        if (this.containerIsStoredInSingleColumn()) {
            return;
        }
        ownerOP.isLoaded(this.getAbsoluteFieldNumber());
        Map value = (Map)ownerOP.provideField(this.getAbsoluteFieldNumber());
        if (value != null) {
            if (!(value instanceof SCO)) {
                value = (Map)ownerOP.wrapSCOField(this.mmd.getAbsoluteFieldNumber(), value, false, false, true);
            }
            value.clear();
            ownerOP.getExecutionContext().flushOperationsForBackingStore(((BackedSCO)value).getBackingStore(), ownerOP);
        }
    }
}
