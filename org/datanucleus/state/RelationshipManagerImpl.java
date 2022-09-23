// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.ClassConstants;
import org.datanucleus.store.types.SCOCollection;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import java.util.Set;
import java.util.Iterator;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.metadata.RelationType;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.Localiser;

public class RelationshipManagerImpl implements RelationshipManager
{
    protected static final Localiser LOCALISER;
    final ObjectProvider ownerOP;
    final ExecutionContext ec;
    final Object pc;
    final Map<Integer, Object> fieldChanges;
    
    public RelationshipManagerImpl(final ObjectProvider op) {
        this.ownerOP = op;
        this.ec = op.getExecutionContext();
        this.pc = op.getObject();
        this.fieldChanges = new HashMap<Integer, Object>();
    }
    
    @Override
    public void clearFields() {
        this.fieldChanges.clear();
    }
    
    @Override
    public void relationChange(final int fieldNumber, final Object oldValue, final Object newValue) {
        if (this.ownerOP.getExecutionContext().isManagingRelations()) {
            return;
        }
        final Integer fieldKey = fieldNumber;
        final AbstractClassMetaData cmd = this.ownerOP.getClassMetaData();
        final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        final RelationType relationType = mmd.getRelationType(this.ownerOP.getExecutionContext().getClassLoaderResolver());
        if (relationType == RelationType.ONE_TO_ONE_BI || relationType == RelationType.MANY_TO_ONE_BI) {
            if (!this.fieldChanges.containsKey(fieldKey)) {
                this.fieldChanges.put(fieldKey, oldValue);
            }
            return;
        }
        List<RelationChange> changes = this.fieldChanges.get(fieldKey);
        if (changes == null) {
            changes = new ArrayList<RelationChange>();
            this.fieldChanges.put(fieldKey, changes);
        }
        if ((relationType == RelationType.ONE_TO_MANY_BI || relationType == RelationType.MANY_TO_MANY_BI) && mmd.hasCollection()) {
            if (oldValue == null) {
                if (newValue != null) {
                    final Iterator iter = ((Collection)newValue).iterator();
                    while (iter.hasNext()) {
                        changes.add(new RelationChange(ChangeType.ADD_OBJECT, iter.next()));
                    }
                }
            }
            else if (newValue == null) {
                final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaData(this.ownerOP.getExecutionContext().getClassLoaderResolver())[0];
                for (final Object element : (Collection)oldValue) {
                    if (this.ownerOP.getLifecycleState().isDeleted) {
                        this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(this.ownerOP.getExecutionContext().getApiAdapter().getIdForObject(element));
                        final ObjectProvider elementOP = this.ownerOP.getExecutionContext().findObjectProvider(element);
                        if (relationType == RelationType.ONE_TO_MANY_BI) {
                            this.ec.getRelationshipManager(elementOP).relationChange(relatedMmd.getAbsoluteFieldNumber(), this.ownerOP.getObject(), null);
                        }
                        else {
                            if (relationType != RelationType.MANY_TO_MANY_BI) {
                                continue;
                            }
                            this.ec.getRelationshipManager(elementOP).relationRemove(relatedMmd.getAbsoluteFieldNumber(), this.ownerOP.getObject());
                        }
                    }
                    else {
                        changes.add(new RelationChange(ChangeType.REMOVE_OBJECT, element));
                    }
                }
            }
            else {
                for (final Object newElem : (Collection)newValue) {
                    final Iterator oldIter = ((Collection)oldValue).iterator();
                    boolean alreadyExists = false;
                    while (oldIter.hasNext()) {
                        final Object oldElem = oldIter.next();
                        if (newElem == oldElem) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        final ObjectProvider elemOP = this.ownerOP.getExecutionContext().findObjectProvider(newElem);
                        if (elemOP != null) {
                            final AbstractMemberMetaData elemMmd = mmd.getRelatedMemberMetaData(this.ownerOP.getExecutionContext().getClassLoaderResolver())[0];
                            final Object oldOwner = elemOP.provideField(elemMmd.getAbsoluteFieldNumber());
                            if (!elemOP.isFieldLoaded(elemMmd.getAbsoluteFieldNumber())) {
                                elemOP.loadField(elemMmd.getAbsoluteFieldNumber());
                            }
                            if (oldOwner != null) {
                                final ObjectProvider oldOwnerOP = this.ownerOP.getExecutionContext().findObjectProvider(oldOwner);
                                if (oldOwnerOP != null) {
                                    this.ec.getRelationshipManager(oldOwnerOP).relationRemove(fieldNumber, newElem);
                                }
                            }
                        }
                        this.relationAdd(fieldNumber, newElem);
                    }
                }
                for (final Object oldElem2 : (Collection)oldValue) {
                    final Iterator newIter = ((Collection)newValue).iterator();
                    boolean stillExists = false;
                    while (newIter.hasNext()) {
                        final Object newElem2 = newIter.next();
                        if (oldElem2 == newElem2) {
                            stillExists = true;
                            break;
                        }
                    }
                    if (!stillExists) {
                        this.relationRemove(fieldNumber, oldElem2);
                    }
                }
            }
        }
    }
    
    @Override
    public void relationAdd(final int fieldNumber, final Object val) {
        if (this.ownerOP.getExecutionContext().isManagingRelations()) {
            return;
        }
        final AbstractClassMetaData cmd = this.ownerOP.getClassMetaData();
        final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        final RelationType relationType = mmd.getRelationType(this.ownerOP.getExecutionContext().getClassLoaderResolver());
        if (relationType != RelationType.ONE_TO_MANY_BI && relationType != RelationType.MANY_TO_MANY_BI) {
            return;
        }
        final ObjectProvider elemOP = this.ownerOP.getExecutionContext().findObjectProvider(val);
        if (elemOP != null) {
            final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaData(this.ownerOP.getExecutionContext().getClassLoaderResolver())[0];
            if (elemOP.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                final Object currentOwnerId = this.ownerOP.getExecutionContext().getApiAdapter().getIdForObject(elemOP.provideField(relatedMmd.getAbsoluteFieldNumber()));
                this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(currentOwnerId);
            }
        }
        final Integer fieldKey = fieldNumber;
        final Object changes = this.fieldChanges.get(fieldKey);
        ArrayList changeList = null;
        if (changes == null) {
            changeList = new ArrayList();
        }
        else {
            changeList = (ArrayList)changes;
        }
        final RelationChange change = new RelationChange(ChangeType.ADD_OBJECT, val);
        this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(this.ownerOP.getExecutionContext().getApiAdapter().getIdForObject(val));
        changeList.add(change);
        this.fieldChanges.put(fieldKey, changeList);
    }
    
    @Override
    public void relationRemove(final int fieldNumber, final Object val) {
        if (this.ownerOP.getExecutionContext().isManagingRelations()) {
            return;
        }
        final AbstractClassMetaData cmd = this.ownerOP.getClassMetaData();
        final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        final RelationType relationType = mmd.getRelationType(this.ownerOP.getExecutionContext().getClassLoaderResolver());
        if (relationType != RelationType.ONE_TO_MANY_BI && relationType != RelationType.MANY_TO_MANY_BI) {
            return;
        }
        final Integer fieldKey = fieldNumber;
        final Object changes = this.fieldChanges.get(fieldKey);
        ArrayList changeList = null;
        if (changes == null) {
            changeList = new ArrayList();
            this.fieldChanges.put(fieldKey, changeList);
        }
        else {
            changeList = (ArrayList)changes;
        }
        final RelationChange change = new RelationChange(ChangeType.REMOVE_OBJECT, val);
        this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(this.ownerOP.getExecutionContext().getApiAdapter().getIdForObject(val));
        changeList.add(change);
    }
    
    @Override
    public boolean managesField(final int fieldNumber) {
        return this.fieldChanges.containsKey(fieldNumber);
    }
    
    @Override
    public void checkConsistency() {
        final Set entries = this.fieldChanges.entrySet();
        final Iterator iter = entries.iterator();
        final AbstractClassMetaData cmd = this.ownerOP.getClassMetaData();
        final ExecutionContext ec = this.ownerOP.getExecutionContext();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            final int fieldNumber = entry.getKey();
            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            final Object oldValue = entry.getValue();
            final RelationType relationType = mmd.getRelationType(clr);
            if (relationType == RelationType.ONE_TO_ONE_BI) {
                final Object newValue = this.ownerOP.provideField(fieldNumber);
                this.checkOneToOneBidirectionalRelation(mmd, clr, ec, oldValue, newValue);
            }
            else if (relationType == RelationType.MANY_TO_ONE_BI) {
                final Object newValue = this.ownerOP.provideField(fieldNumber);
                this.checkManyToOneBidirectionalRelation(mmd, clr, ec, oldValue, newValue);
            }
            else if (relationType == RelationType.ONE_TO_MANY_BI) {
                final List changes = (List)oldValue;
                this.checkOneToManyBidirectionalRelation(mmd, clr, ec, changes);
            }
            else {
                if (relationType != RelationType.MANY_TO_MANY_BI) {
                    continue;
                }
                final List changes = (List)oldValue;
                this.checkManyToManyBidirectionalRelation(mmd, clr, ec, changes);
            }
        }
    }
    
    @Override
    public void process() {
        final Set entries = this.fieldChanges.entrySet();
        final Iterator iter = entries.iterator();
        final AbstractClassMetaData cmd = this.ownerOP.getClassMetaData();
        final ExecutionContext ec = this.ownerOP.getExecutionContext();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            final int fieldNumber = entry.getKey();
            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            final Object oldValue = entry.getValue();
            final RelationType relationType = mmd.getRelationType(clr);
            if (relationType == RelationType.ONE_TO_ONE_BI) {
                final Object newValue = this.ownerOP.provideField(fieldNumber);
                this.processOneToOneBidirectionalRelation(mmd, clr, ec, oldValue, newValue);
            }
            else if (relationType == RelationType.MANY_TO_ONE_BI) {
                final Object newValue = this.ownerOP.provideField(fieldNumber);
                this.processManyToOneBidirectionalRelation(mmd, clr, ec, oldValue, newValue);
            }
            else if (relationType == RelationType.ONE_TO_MANY_BI) {
                final List changes = (List)oldValue;
                this.processOneToManyBidirectionalRelation(mmd, clr, ec, changes);
            }
            else {
                if (relationType != RelationType.MANY_TO_MANY_BI) {
                    continue;
                }
                final List changes = (List)oldValue;
                this.processManyToManyBidirectionalRelation(mmd, clr, ec, changes);
            }
        }
    }
    
    protected void checkOneToOneBidirectionalRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final ExecutionContext ec, final Object oldValue, final Object newValue) {
        if (newValue != null) {
            final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaDataForObject(clr, this.pc, newValue);
            final ObjectProvider newOP = ec.findObjectProvider(newValue);
            if (newOP != null && relatedMmd != null) {
                if (!newOP.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                    newOP.loadField(relatedMmd.getAbsoluteFieldNumber());
                }
                final Object newValueFieldValue = newOP.provideField(relatedMmd.getAbsoluteFieldNumber());
                if (newValueFieldValue != this.pc) {
                    final RelationshipManager newRelMgr = ec.getRelationshipManager(newOP);
                    if (newRelMgr != null && newRelMgr.managesField(relatedMmd.getAbsoluteFieldNumber())) {
                        if (newValueFieldValue == null) {
                            final String msg = RelationshipManagerImpl.LOCALISER.msg("013003", StringUtils.toJVMIDString(this.pc), mmd.getName(), StringUtils.toJVMIDString(newValue), relatedMmd.getName());
                            NucleusLogger.PERSISTENCE.error(msg);
                            throw new NucleusUserException(msg);
                        }
                        final String msg = RelationshipManagerImpl.LOCALISER.msg("013002", StringUtils.toJVMIDString(this.pc), mmd.getName(), StringUtils.toJVMIDString(newValue), relatedMmd.getName(), StringUtils.toJVMIDString(newValueFieldValue));
                        NucleusLogger.PERSISTENCE.error(msg);
                        throw new NucleusUserException(msg);
                    }
                }
            }
        }
    }
    
    protected void checkOneToManyBidirectionalRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final ExecutionContext ec, final List<RelationChange> changes) {
        for (final RelationChange change : changes) {
            if (change.type == ChangeType.ADD_OBJECT) {
                if (this.ownerOP.getExecutionContext().getApiAdapter().isDeleted(change.value)) {
                    throw new NucleusUserException(RelationshipManagerImpl.LOCALISER.msg("013008", StringUtils.toJVMIDString(this.pc), mmd.getName(), StringUtils.toJVMIDString(change.value)));
                }
                final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaData(clr)[0];
                final ObjectProvider newElementOP = ec.findObjectProvider(change.value);
                if (newElementOP == null || !newElementOP.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                    continue;
                }
                final RelationshipManager newElementRelMgr = ec.getRelationshipManager(newElementOP);
                if (newElementRelMgr == null || !newElementRelMgr.managesField(relatedMmd.getAbsoluteFieldNumber())) {
                    continue;
                }
                final Object newValueFieldValue = newElementOP.provideField(relatedMmd.getAbsoluteFieldNumber());
                if (newValueFieldValue == this.pc || newValueFieldValue == null) {
                    continue;
                }
                final ApiAdapter api = ec.getApiAdapter();
                final Object id1 = api.getIdForObject(this.pc);
                final Object id2 = api.getIdForObject(newValueFieldValue);
                if (id1 != null && id2 != null && id1.equals(id2)) {
                    continue;
                }
                throw new NucleusUserException(RelationshipManagerImpl.LOCALISER.msg("013009", StringUtils.toJVMIDString(this.pc), mmd.getName(), StringUtils.toJVMIDString(change.value), StringUtils.toJVMIDString(newValueFieldValue)));
            }
            else {
                if (change.type != ChangeType.REMOVE_OBJECT) {
                    continue;
                }
                if (this.ownerOP.getExecutionContext().getApiAdapter().isDeleted(change.value)) {
                    continue;
                }
                final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaData(clr)[0];
                final ObjectProvider newElementOP = ec.findObjectProvider(change.value);
                if (newElementOP == null || !newElementOP.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                    continue;
                }
                final RelationshipManager newElementRelMgr = ec.getRelationshipManager(newElementOP);
                if (newElementRelMgr == null || !newElementRelMgr.managesField(relatedMmd.getAbsoluteFieldNumber())) {
                    continue;
                }
                final Object newValueFieldValue = newElementOP.provideField(relatedMmd.getAbsoluteFieldNumber());
                if (newValueFieldValue == this.pc) {
                    throw new NucleusUserException(RelationshipManagerImpl.LOCALISER.msg("013010", StringUtils.toJVMIDString(this.pc), mmd.getName(), StringUtils.toJVMIDString(change.value)));
                }
                continue;
            }
        }
    }
    
    protected void checkManyToOneBidirectionalRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final ExecutionContext ec, final Object oldValue, final Object newValue) {
    }
    
    protected void checkManyToManyBidirectionalRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final ExecutionContext ec, final List<RelationChange> changes) {
    }
    
    protected void processOneToOneBidirectionalRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final ExecutionContext ec, final Object oldValue, final Object newValue) {
        if (oldValue != null) {
            final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaDataForObject(clr, this.pc, oldValue);
            final ObjectProvider oldOP = ec.findObjectProvider(oldValue);
            if (oldOP != null) {
                final boolean oldIsDeleted = ec.getApiAdapter().isDeleted(oldOP.getObject());
                if (!oldIsDeleted) {
                    if (!oldOP.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                        oldOP.loadField(relatedMmd.getAbsoluteFieldNumber());
                    }
                    final Object oldValueFieldValue = oldOP.provideField(relatedMmd.getAbsoluteFieldNumber());
                    if (oldValueFieldValue != null) {
                        if (oldValueFieldValue == this.pc) {
                            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                                NucleusLogger.PERSISTENCE.debug(RelationshipManagerImpl.LOCALISER.msg("013004", StringUtils.toJVMIDString(oldValue), relatedMmd.getFullFieldName(), StringUtils.toJVMIDString(this.pc), StringUtils.toJVMIDString(newValue)));
                            }
                            oldOP.replaceFieldValue(relatedMmd.getAbsoluteFieldNumber(), null);
                        }
                    }
                }
            }
        }
        if (newValue != null) {
            final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaDataForObject(clr, this.pc, newValue);
            final ObjectProvider newOP = ec.findObjectProvider(newValue);
            if (newOP != null && relatedMmd != null) {
                if (!newOP.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                    newOP.loadField(relatedMmd.getAbsoluteFieldNumber());
                }
                final Object newValueFieldValue = newOP.provideField(relatedMmd.getAbsoluteFieldNumber());
                if (newValueFieldValue == null) {
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(RelationshipManagerImpl.LOCALISER.msg("013005", StringUtils.toJVMIDString(newValue), relatedMmd.getFullFieldName(), StringUtils.toJVMIDString(this.pc)));
                    }
                    newOP.replaceFieldValue(relatedMmd.getAbsoluteFieldNumber(), this.pc);
                }
                else if (newValueFieldValue != this.pc) {
                    final ObjectProvider newValueFieldOP = ec.findObjectProvider(newValueFieldValue);
                    if (newValueFieldOP != null) {
                        if (!newValueFieldOP.isFieldLoaded(mmd.getAbsoluteFieldNumber())) {
                            newValueFieldOP.loadField(mmd.getAbsoluteFieldNumber());
                        }
                        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug(RelationshipManagerImpl.LOCALISER.msg("013004", StringUtils.toJVMIDString(newValueFieldValue), mmd.getFullFieldName(), StringUtils.toJVMIDString(newValue), StringUtils.toJVMIDString(this.pc)));
                        }
                        newValueFieldOP.replaceFieldValue(mmd.getAbsoluteFieldNumber(), null);
                    }
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(RelationshipManagerImpl.LOCALISER.msg("013005", StringUtils.toJVMIDString(newValue), relatedMmd.getFullFieldName(), StringUtils.toJVMIDString(this.pc)));
                    }
                    newOP.replaceFieldValue(relatedMmd.getAbsoluteFieldNumber(), this.pc);
                }
            }
        }
    }
    
    protected void processOneToManyBidirectionalRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final ExecutionContext ec, final List<RelationChange> changes) {
        for (final RelationChange change : changes) {
            if (change.type != ChangeType.ADD_OBJECT && change.type != ChangeType.REMOVE_OBJECT) {
                continue;
            }
            ObjectProvider op = ec.findObjectProvider(change.value);
            if (op == null && ec.getApiAdapter().isDetached(change.value)) {
                final Object attached = ec.getAttachedObjectForId(ec.getApiAdapter().getIdForObject(change.value));
                if (attached != null) {
                    op = ec.findObjectProvider(attached);
                }
            }
            if (op == null) {
                continue;
            }
            if (change.type == ChangeType.ADD_OBJECT) {
                final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaData(clr)[0];
                if (op.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                    final Object currentVal = op.provideField(relatedMmd.getAbsoluteFieldNumber());
                    if (currentVal == this.ownerOP.getObject()) {
                        continue;
                    }
                    op.replaceFieldValue(relatedMmd.getAbsoluteFieldNumber(), this.ownerOP.getObject());
                }
                else {
                    this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(op.getInternalObjectId());
                }
            }
            else {
                if (change.type != ChangeType.REMOVE_OBJECT) {
                    continue;
                }
                final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaData(clr)[0];
                if (op.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                    final Object currentVal = op.provideField(relatedMmd.getAbsoluteFieldNumber());
                    if (currentVal != this.ownerOP.getObject()) {
                        continue;
                    }
                    op.replaceFieldValue(relatedMmd.getAbsoluteFieldNumber(), null);
                }
                else {
                    this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(op.getInternalObjectId());
                }
            }
        }
    }
    
    protected void processManyToOneBidirectionalRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final ExecutionContext ec, final Object oldValue, final Object newValue) {
        if (oldValue != null) {
            final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaDataForObject(clr, this.pc, oldValue);
            final ObjectProvider oldOP = ec.findObjectProvider(oldValue);
            if (oldOP != null && relatedMmd != null && oldOP.getLoadedFields()[relatedMmd.getAbsoluteFieldNumber()]) {
                if (oldOP.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                    final Object oldContainerValue = oldOP.provideField(relatedMmd.getAbsoluteFieldNumber());
                    if (oldContainerValue instanceof Collection) {
                        final Collection oldColl = (Collection)oldContainerValue;
                        if (oldColl.contains(this.pc)) {
                            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                                NucleusLogger.PERSISTENCE.debug(RelationshipManagerImpl.LOCALISER.msg("013006", StringUtils.toJVMIDString(this.pc), mmd.getFullFieldName(), relatedMmd.getFullFieldName(), StringUtils.toJVMIDString(oldValue)));
                            }
                            if (oldColl instanceof SCOCollection) {
                                ((SCOCollection)oldColl).remove(this.pc, false);
                            }
                            else {
                                oldColl.remove(this.pc);
                            }
                        }
                    }
                }
            }
            else if (oldOP != null) {
                this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(oldOP.getInternalObjectId());
            }
        }
        if (newValue != null) {
            final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaDataForObject(clr, this.pc, newValue);
            final ObjectProvider newOP = ec.findObjectProvider(newValue);
            if (newOP != null && relatedMmd != null && newOP.getLoadedFields()[relatedMmd.getAbsoluteFieldNumber()]) {
                final Object newContainerValue = newOP.provideField(relatedMmd.getAbsoluteFieldNumber());
                if (newContainerValue instanceof Collection) {
                    final Collection newColl = (Collection)newContainerValue;
                    if (!newColl.contains(this.pc)) {
                        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug(RelationshipManagerImpl.LOCALISER.msg("013007", StringUtils.toJVMIDString(this.pc), mmd.getFullFieldName(), relatedMmd.getFullFieldName(), StringUtils.toJVMIDString(newValue)));
                        }
                        newColl.add(this.pc);
                    }
                }
            }
            else {
                this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(this.ownerOP.getExecutionContext().getApiAdapter().getIdForObject(newValue));
            }
        }
    }
    
    protected void processManyToManyBidirectionalRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final ExecutionContext ec, final List<RelationChange> changes) {
        for (final RelationChange change : changes) {
            if (change.type != ChangeType.ADD_OBJECT && change.type != ChangeType.REMOVE_OBJECT) {
                continue;
            }
            ObjectProvider op = ec.findObjectProvider(change.value);
            if (op == null && ec.getApiAdapter().isDetached(change.value)) {
                final Object attached = ec.getAttachedObjectForId(ec.getApiAdapter().getIdForObject(change.value));
                if (attached != null) {
                    op = ec.findObjectProvider(attached);
                }
            }
            if (op == null) {
                continue;
            }
            if (change.type == ChangeType.ADD_OBJECT) {
                final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaData(clr)[0];
                this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(op.getInternalObjectId());
                this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(this.ownerOP.getInternalObjectId());
                if (this.ownerOP.isFieldLoaded(mmd.getAbsoluteFieldNumber()) && !this.ownerOP.getLifecycleState().isDeleted) {
                    final Collection currentVal = (Collection)this.ownerOP.provideField(mmd.getAbsoluteFieldNumber());
                    if (currentVal != null && !currentVal.contains(op.getObject())) {
                        currentVal.add(op.getObject());
                    }
                }
                if (!op.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber())) {
                    continue;
                }
                final Collection currentVal = (Collection)op.provideField(relatedMmd.getAbsoluteFieldNumber());
                if (currentVal == null || currentVal.contains(this.ownerOP.getObject())) {
                    continue;
                }
                currentVal.add(this.ownerOP.getObject());
            }
            else {
                if (change.type != ChangeType.REMOVE_OBJECT) {
                    continue;
                }
                final AbstractMemberMetaData relatedMmd = mmd.getRelatedMemberMetaData(clr)[0];
                this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(op.getInternalObjectId());
                this.ownerOP.getExecutionContext().removeObjectFromLevel2Cache(this.ownerOP.getInternalObjectId());
                if (this.ownerOP.isFieldLoaded(mmd.getAbsoluteFieldNumber()) && !this.ownerOP.getLifecycleState().isDeleted) {
                    final Collection currentVal = (Collection)this.ownerOP.provideField(mmd.getAbsoluteFieldNumber());
                    if (!op.getLifecycleState().isDeleted && currentVal != null && currentVal.contains(op.getObject())) {
                        currentVal.remove(op.getObject());
                    }
                    else {
                        this.ownerOP.unloadField(mmd.getName());
                    }
                }
                if (!op.isFieldLoaded(relatedMmd.getAbsoluteFieldNumber()) || op.getLifecycleState().isDeleted) {
                    continue;
                }
                final Collection currentVal = (Collection)op.provideField(relatedMmd.getAbsoluteFieldNumber());
                if (currentVal == null || !currentVal.contains(this.ownerOP.getObject())) {
                    continue;
                }
                currentVal.remove(this.ownerOP.getObject());
            }
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    private enum ChangeType
    {
        ADD_OBJECT, 
        REMOVE_OBJECT, 
        CHANGE_OBJECT;
    }
    
    private static class RelationChange
    {
        ChangeType type;
        Object value;
        Object oldValue;
        
        public RelationChange(final ChangeType type, final Object val) {
            this.type = type;
            this.value = val;
        }
        
        public RelationChange(final ChangeType type, final Object val, final Object oldVal) {
            this.type = type;
            this.value = val;
            this.oldValue = oldVal;
        }
        
        @Override
        public String toString() {
            if (this.oldValue != null) {
                return "RelationChange type=" + this.type + " value=" + StringUtils.toJVMIDString(this.oldValue) + " -> " + StringUtils.toJVMIDString(this.value);
            }
            return "RelationChange type=" + this.type + " value=" + StringUtils.toJVMIDString(this.value);
        }
    }
}
