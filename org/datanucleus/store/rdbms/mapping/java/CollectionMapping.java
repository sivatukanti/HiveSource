// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.types.backed.BackedSCO;
import org.datanucleus.store.types.SCOContainer;
import org.datanucleus.store.types.SCO;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.store.exceptions.ReachableObjectNotCascadedException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.state.ObjectProvider;
import java.util.Collection;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class CollectionMapping extends AbstractContainerMapping implements MappingCallbacks
{
    @Override
    public Class getJavaType() {
        return Collection.class;
    }
    
    @Override
    public void insertPostProcessing(final ObjectProvider ownerOP) {
    }
    
    @Override
    public void postInsert(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final Collection value = (Collection)ownerOP.provideField(this.getAbsoluteFieldNumber());
        if (this.containerIsStoredInSingleColumn()) {
            SCOUtils.validateObjectsForWriting(ec, value);
            return;
        }
        if (value == null) {
            this.replaceFieldWithWrapper(ownerOP, null, false, false);
            return;
        }
        final Object[] collElements = value.toArray();
        if (!this.mmd.isCascadePersist()) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(CollectionMapping.LOCALISER.msg("007006", this.mmd.getFullFieldName()));
            }
            for (int i = 0; i < collElements.length; ++i) {
                if (!ec.getApiAdapter().isDetached(collElements[i]) && !ec.getApiAdapter().isPersistent(collElements[i])) {
                    throw new ReachableObjectNotCascadedException(this.mmd.getFullFieldName(), collElements[i]);
                }
            }
            this.replaceFieldWithWrapper(ownerOP, value, false, false);
        }
        else {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(CollectionMapping.LOCALISER.msg("007007", this.mmd.getFullFieldName()));
            }
            boolean needsAttaching = false;
            for (int j = 0; j < collElements.length; ++j) {
                if (ownerOP.getExecutionContext().getApiAdapter().isDetached(collElements[j])) {
                    needsAttaching = true;
                    break;
                }
            }
            if (needsAttaching) {
                final SCO collWrapper = this.replaceFieldWithWrapper(ownerOP, null, false, false);
                collWrapper.attachCopy(value);
            }
            else if (value.size() > 0) {
                ((CollectionStore)this.storeMgr.getBackingStoreForField(ownerOP.getExecutionContext().getClassLoaderResolver(), this.mmd, value.getClass())).addAll(ownerOP, value, 0);
                this.replaceFieldWithWrapper(ownerOP, value, false, false);
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
        final Collection value = (Collection)ownerOP.provideField(this.getAbsoluteFieldNumber());
        if (this.containerIsStoredInSingleColumn()) {
            SCOUtils.validateObjectsForWriting(ec, value);
            return;
        }
        if (value == null) {
            ((CollectionStore)this.storeMgr.getBackingStoreForField(ec.getClassLoaderResolver(), this.mmd, null)).clear(ownerOP);
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
                throw new NucleusException(CollectionMapping.LOCALISER.msg("CollectionMapping.WrongOwnerError")).setFatal();
            }
        }
        if (!this.mmd.isCascadeUpdate()) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(CollectionMapping.LOCALISER.msg("007008", this.mmd.getFullFieldName()));
            }
            return;
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(CollectionMapping.LOCALISER.msg("007009", this.mmd.getFullFieldName()));
        }
        final CollectionStore backingStore = (CollectionStore)this.storeMgr.getBackingStoreForField(ec.getClassLoaderResolver(), this.mmd, value.getClass());
        backingStore.update(ownerOP, value);
        this.replaceFieldWithWrapper(ownerOP, value, false, false);
    }
    
    @Override
    public void preDelete(final ObjectProvider ownerOP) {
        if (this.containerIsStoredInSingleColumn()) {
            return;
        }
        ownerOP.isLoaded(this.getAbsoluteFieldNumber());
        Collection value = (Collection)ownerOP.provideField(this.getAbsoluteFieldNumber());
        if (value == null) {
            return;
        }
        boolean dependent = this.mmd.getCollection().isDependentElement();
        if (this.mmd.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        final boolean hasJoin = this.mmd.getJoinMetaData() != null;
        boolean hasFK = false;
        if (!hasJoin) {
            if (this.mmd.getElementMetaData() != null && this.mmd.getElementMetaData().getForeignKeyMetaData() != null) {
                hasFK = true;
            }
            else if (this.mmd.getForeignKeyMetaData() != null) {
                hasFK = true;
            }
            final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(ownerOP.getExecutionContext().getClassLoaderResolver());
            if (relatedMmds != null && relatedMmds[0].getForeignKeyMetaData() != null) {
                hasFK = true;
            }
        }
        if (ownerOP.getExecutionContext().getStringProperty("datanucleus.deletionPolicy").equals("JDO2")) {
            hasFK = false;
        }
        if (ownerOP.getExecutionContext().getManageRelations()) {
            ownerOP.getExecutionContext().getRelationshipManager(ownerOP).relationChange(this.getAbsoluteFieldNumber(), value, null);
        }
        if (dependent || hasJoin || !hasFK) {
            if (!(value instanceof SCO)) {
                value = (Collection)ownerOP.wrapSCOField(this.getAbsoluteFieldNumber(), value, false, false, true);
            }
            value.clear();
            ownerOP.getExecutionContext().flushOperationsForBackingStore(((BackedSCO)value).getBackingStore(), ownerOP);
        }
    }
}
