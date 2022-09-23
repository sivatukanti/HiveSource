// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.scostore.PersistableRelationStore;
import org.datanucleus.store.types.SCOCollection;
import java.util.Collection;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import java.sql.ResultSet;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.rdbms.mapping.AppIDObjectIdFieldManager;
import org.datanucleus.store.exceptions.NotYetFlushedException;
import org.datanucleus.store.exceptions.ReachableObjectNotCascadedException;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.store.StoreManager;
import org.datanucleus.metadata.MetaDataManager;
import java.sql.PreparedStatement;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ExecutionContext;
import org.datanucleus.identity.OID;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.FieldMetaData;
import org.datanucleus.store.fieldmanager.SingleValueFieldManager;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.NucleusContext;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.metadata.ColumnMetaDataContainer;
import org.datanucleus.store.rdbms.mapping.CorrespondentColumnsMapper;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class PersistableMapping extends MultiMapping implements MappingCallbacks
{
    protected AbstractClassMetaData cmd;
    
    @Override
    public Class getJavaType() {
        return null;
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(mmd, table, clr);
        this.prepareDatastoreMapping(clr);
    }
    
    protected void prepareDatastoreMapping(final ClassLoaderResolver clr) {
        if (this.roleForMember != 3) {
            if (this.roleForMember != 4) {
                if (this.roleForMember != 5) {
                    if (this.roleForMember != 6) {
                        final AbstractClassMetaData refCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(this.mmd.getType(), clr);
                        JavaTypeMapping referenceMapping = null;
                        if (refCmd == null) {
                            throw new NucleusUserException("You have a field " + this.mmd.getFullFieldName() + " that has type " + this.mmd.getTypeName() + " but this type has no known metadata. Your mapping is incorrect");
                        }
                        if (refCmd.getInheritanceMetaData() != null && refCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                            final AbstractClassMetaData[] cmds = this.storeMgr.getClassesManagingTableForClass(refCmd, clr);
                            if (cmds == null || cmds.length <= 0) {
                                return;
                            }
                            if (cmds.length > 1) {
                                NucleusLogger.PERSISTENCE.warn("Field " + this.mmd.getFullFieldName() + " represents either a 1-1 relation, " + "or a N-1 relation where the other end uses \"subclass-table\" inheritance strategy and more " + "than 1 subclasses with a table. This is not fully supported");
                            }
                            referenceMapping = this.storeMgr.getDatastoreClass(cmds[0].getFullClassName(), clr).getIdMapping();
                        }
                        else {
                            referenceMapping = this.storeMgr.getDatastoreClass(this.mmd.getType().getName(), clr).getIdMapping();
                        }
                        final CorrespondentColumnsMapper correspondentColumnsMapping = new CorrespondentColumnsMapper(this.mmd, referenceMapping, true);
                        final RelationType relationType = this.mmd.getRelationType(clr);
                        boolean createDatastoreMappings = true;
                        if (relationType == RelationType.MANY_TO_ONE_BI) {
                            final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(clr);
                            createDatastoreMappings = (relatedMmds[0].getJoinMetaData() == null);
                        }
                        else if (relationType == RelationType.ONE_TO_ONE_BI) {
                            createDatastoreMappings = (this.mmd.getMappedBy() == null);
                        }
                        if (relationType == RelationType.MANY_TO_ONE_UNI) {
                            this.storeMgr.newJoinDatastoreContainerObject(this.mmd, clr);
                        }
                        else {
                            for (int i = 0; i < referenceMapping.getNumberOfDatastoreMappings(); ++i) {
                                final DatastoreMapping refDatastoreMapping = referenceMapping.getDatastoreMapping(i);
                                final JavaTypeMapping mapping = this.storeMgr.getMappingManager().getMapping(refDatastoreMapping.getJavaTypeMapping().getJavaType());
                                this.addJavaTypeMapping(mapping);
                                if (createDatastoreMappings) {
                                    final ColumnMetaData colmd = correspondentColumnsMapping.getColumnMetaDataByIdentifier(refDatastoreMapping.getColumn().getIdentifier());
                                    if (colmd == null) {
                                        throw new NucleusUserException(PersistableMapping.LOCALISER_RDBMS.msg("041038", refDatastoreMapping.getColumn().getIdentifier(), this.toString())).setFatal();
                                    }
                                    final MappingManager mmgr = this.storeMgr.getMappingManager();
                                    final Column col = mmgr.createColumn(this.mmd, this.table, mapping, colmd, refDatastoreMapping.getColumn(), clr);
                                    final DatastoreMapping datastoreMapping = mmgr.createDatastoreMapping(mapping, col, refDatastoreMapping.getJavaTypeMapping().getJavaTypeForDatastoreMapping(i));
                                    this.addDatastoreMapping(datastoreMapping);
                                }
                                else {
                                    mapping.setReferenceMapping(referenceMapping);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public Object getValueForDatastoreMapping(final NucleusContext nucleusCtx, final int index, final Object value) {
        final ExecutionContext ec = nucleusCtx.getApiAdapter().getExecutionContext(value);
        if (this.cmd == null) {
            this.cmd = nucleusCtx.getMetaDataManager().getMetaDataForClass(this.getType(), (ec != null) ? ec.getClassLoaderResolver() : nucleusCtx.getClassLoaderResolver(null));
        }
        if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
            final AbstractMemberMetaData mmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(this.cmd.getPKMemberPositions()[index]);
            ObjectProvider sm = null;
            if (ec != null) {
                sm = ec.findObjectProvider(value);
            }
            if (sm != null) {
                if (!mmd.isPrimaryKey()) {
                    sm.isLoaded(mmd.getAbsoluteFieldNumber());
                }
                final FieldManager fm = new SingleValueFieldManager();
                sm.provideFields(new int[] { mmd.getAbsoluteFieldNumber() }, fm);
                return fm.fetchObjectField(mmd.getAbsoluteFieldNumber());
            }
            if (mmd instanceof FieldMetaData) {
                return ClassUtils.getValueOfFieldByReflection(value, mmd.getName());
            }
            return ClassUtils.getValueOfMethodByReflection(value, ClassUtils.getJavaBeanGetterName(mmd.getName(), false), (Object[])null);
        }
        else {
            if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
                final OID oid = (OID)nucleusCtx.getApiAdapter().getIdForObject(value);
                return (oid != null) ? oid.getKeyValue() : null;
            }
            return null;
        }
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] param, final Object value) {
        this.setObject(ec, ps, param, value, null, -1);
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] param, final Object value, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        if (value == null) {
            this.setObjectAsNull(ec, ps, param);
        }
        else {
            this.setObjectAsValue(ec, ps, param, value, ownerOP, ownerFieldNumber);
        }
    }
    
    private void setObjectAsNull(final ExecutionContext ec, final PreparedStatement ps, final int[] param) {
        int n = 0;
        for (int i = 0; i < this.javaTypeMappings.length; ++i) {
            final JavaTypeMapping mapping = this.javaTypeMappings[i];
            if (mapping.getNumberOfDatastoreMappings() > 0) {
                final int[] posMapping = new int[mapping.getNumberOfDatastoreMappings()];
                for (int j = 0; j < posMapping.length; ++j) {
                    posMapping[j] = param[n++];
                }
                mapping.setObject(ec, ps, posMapping, null);
            }
        }
    }
    
    private boolean hasDatastoreAttributedPrimaryKeyValues(final MetaDataManager mdm, final StoreManager srm, final ClassLoaderResolver clr) {
        boolean hasDatastoreAttributedPrimaryKeyValues = false;
        if (this.mmd != null && this.roleForMember != 4 && this.roleForMember != 3 && this.roleForMember != 5 && this.roleForMember != 6) {
            final AbstractClassMetaData acmd = mdm.getMetaDataForClass(this.mmd.getType(), clr);
            if (acmd.getIdentityType() == IdentityType.APPLICATION) {
                for (int i = 0; i < acmd.getPKMemberPositions().length; ++i) {
                    final IdentityStrategy strategy = acmd.getMetaDataForManagedMemberAtAbsolutePosition(acmd.getPKMemberPositions()[i]).getValueStrategy();
                    if (strategy != null) {
                        hasDatastoreAttributedPrimaryKeyValues |= srm.isStrategyDatastoreAttributed(acmd, acmd.getPKMemberPositions()[i]);
                    }
                }
            }
        }
        return hasDatastoreAttributedPrimaryKeyValues;
    }
    
    private void setObjectAsValue(final ExecutionContext ec, final PreparedStatement ps, final int[] param, Object value, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        final ApiAdapter api = ec.getApiAdapter();
        if (!api.isPersistable(value)) {
            throw new NucleusException(PersistableMapping.LOCALISER_RDBMS.msg("041016", value.getClass(), value)).setFatal();
        }
        final ObjectProvider valueSM = ec.findObjectProvider(value);
        try {
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            final boolean hasDatastoreAttributedPrimaryKeyValues = this.hasDatastoreAttributedPrimaryKeyValues(ec.getMetaDataManager(), this.storeMgr, clr);
            boolean inserted = false;
            if (ownerFieldNumber >= 0) {
                inserted = this.storeMgr.isObjectInserted(valueSM, ownerFieldNumber);
            }
            else if (this.mmd == null) {
                inserted = this.storeMgr.isObjectInserted(valueSM, this.type);
            }
            if (valueSM != null) {
                if (ec.getApiAdapter().isDetached(value) && valueSM.getReferencedPC() != null && ownerOP != null && this.mmd != null) {
                    ownerOP.replaceFieldMakeDirty(ownerFieldNumber, valueSM.getReferencedPC());
                }
                if (valueSM.isWaitingToBeFlushedToDatastore()) {
                    valueSM.flush();
                }
            }
            else if (ec.getApiAdapter().isDetached(value)) {
                final Object attachedValue = ec.persistObjectInternal(value, null, -1, 0);
                if (attachedValue != value && ownerOP != null) {
                    ownerOP.replaceFieldMakeDirty(ownerFieldNumber, attachedValue);
                    value = attachedValue;
                }
            }
            if (inserted || !ec.isInserting(value) || (!hasDatastoreAttributedPrimaryKeyValues && this.mmd != null && this.mmd.isPrimaryKey()) || (!hasDatastoreAttributedPrimaryKeyValues && ownerOP == valueSM && api.getIdForObject(value) != null)) {
                Object id = api.getIdForObject(value);
                boolean requiresPersisting = false;
                if (ec.getApiAdapter().isDetached(value) && ownerOP != null) {
                    if (ownerOP.isInserting()) {
                        if (!ec.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.attachSameDatastore")) {
                            if (ec.getObjectFromCache(api.getIdForObject(value)) == null) {
                                try {
                                    final Object obj = ec.findObject(api.getIdForObject(value), true, false, value.getClass().getName());
                                    if (obj != null) {
                                        final ObjectProvider objSM = ec.findObjectProvider(obj);
                                        if (objSM != null) {
                                            ec.evictFromTransaction(objSM);
                                        }
                                        ec.removeObjectFromLevel1Cache(api.getIdForObject(value));
                                    }
                                }
                                catch (NucleusObjectNotFoundException onfe) {
                                    requiresPersisting = true;
                                }
                            }
                        }
                    }
                    else {
                        requiresPersisting = true;
                    }
                }
                else if (id == null) {
                    requiresPersisting = true;
                }
                else {
                    final ExecutionContext pcEC = ec.getApiAdapter().getExecutionContext(value);
                    if (pcEC != null && ec != pcEC) {
                        throw new NucleusUserException(PersistableMapping.LOCALISER_RDBMS.msg("041015"), id);
                    }
                }
                if (requiresPersisting) {
                    if (this.mmd != null && !this.mmd.isCascadePersist() && !ec.getApiAdapter().isDetached(value)) {
                        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug(PersistableMapping.LOCALISER.msg("007006", this.mmd.getFullFieldName()));
                        }
                        throw new ReachableObjectNotCascadedException(this.mmd.getFullFieldName(), value);
                    }
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(PersistableMapping.LOCALISER.msg("007007", (this.mmd != null) ? this.mmd.getFullFieldName() : null));
                    }
                    try {
                        final Object pcNew = ec.persistObjectInternal(value, null, -1, 0);
                        if (hasDatastoreAttributedPrimaryKeyValues) {
                            ec.flushInternal(false);
                        }
                        id = api.getIdForObject(pcNew);
                        if (ec.getApiAdapter().isDetached(value) && ownerOP != null) {
                            ownerOP.replaceFieldMakeDirty(ownerFieldNumber, pcNew);
                            final RelationType relationType = this.mmd.getRelationType(clr);
                            if (relationType == RelationType.MANY_TO_ONE_BI) {
                                if (NucleusLogger.PERSISTENCE.isInfoEnabled()) {
                                    NucleusLogger.PERSISTENCE.info("PCMapping.setObject : object " + ownerOP.getInternalObjectId() + " has field " + ownerFieldNumber + " that is 1-N bidirectional." + " Have just attached the N side so should really update the reference in the 1 side collection" + " to refer to this attached object. Not yet implemented");
                                }
                            }
                            else if (relationType == RelationType.ONE_TO_ONE_BI) {
                                final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(clr);
                                final ObjectProvider relatedSM = ec.findObjectProvider(pcNew);
                                relatedSM.replaceFieldMakeDirty(relatedMmds[0].getAbsoluteFieldNumber(), ownerOP.getObject());
                            }
                        }
                    }
                    catch (NotYetFlushedException e) {
                        this.setObjectAsNull(ec, ps, param);
                        throw new NotYetFlushedException(value);
                    }
                }
                if (valueSM != null) {
                    valueSM.setStoringPC();
                }
                if (this.getNumberOfDatastoreMappings() > 0) {
                    if (id instanceof OID) {
                        final OID oid = (OID)id;
                        try {
                            this.getDatastoreMapping(0).setObject(ps, param[0], oid.getKeyValue());
                        }
                        catch (Exception e2) {
                            this.getDatastoreMapping(0).setObject(ps, param[0], oid.getKeyValue().toString());
                        }
                    }
                    else {
                        boolean fieldsSet = false;
                        if (api.isSingleFieldIdentity(id) && this.javaTypeMappings.length > 1) {
                            final Object key = api.getTargetKeyForSingleFieldIdentity(id);
                            final AbstractClassMetaData keyCmd = ec.getMetaDataManager().getMetaDataForClass(key.getClass(), clr);
                            if (keyCmd != null && keyCmd.getIdentityType() == IdentityType.NONDURABLE) {
                                final ObjectProvider keyOP = ec.findObjectProvider(key);
                                final int[] fieldNums = keyCmd.getAllMemberPositions();
                                final FieldManager fm = new AppIDObjectIdFieldManager(param, ec, ps, this.javaTypeMappings);
                                for (int i = 0; i < fieldNums.length; ++i) {
                                    keyOP.provideFields(new int[] { fieldNums[i] }, fm);
                                }
                                fieldsSet = true;
                            }
                        }
                        if (!fieldsSet) {
                            final FieldManager fm2 = new AppIDObjectIdFieldManager(param, ec, ps, this.javaTypeMappings);
                            api.copyPkFieldsToPersistableObjectFromId(value, id, fm2);
                        }
                    }
                }
            }
            else {
                if (valueSM != null) {
                    valueSM.setStoringPC();
                }
                if (this.getNumberOfDatastoreMappings() > 0) {
                    this.setObjectAsNull(ec, ps, param);
                    throw new NotYetFlushedException(value);
                }
            }
        }
        finally {
            if (valueSM != null) {
                valueSM.unsetStoringPC();
            }
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] resultIndexes) {
        if (this.storeMgr.getResultValueAtPosition(rs, this, resultIndexes[0]) == null) {
            return null;
        }
        if (this.cmd == null) {
            this.cmd = ec.getMetaDataManager().getMetaDataForClass(this.getType(), ec.getClassLoaderResolver());
        }
        if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
            return MappingHelper.getObjectForDatastoreIdentity(ec, this, rs, resultIndexes, this.cmd);
        }
        if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
            return MappingHelper.getObjectForApplicationIdentity(ec, this, rs, resultIndexes, this.cmd);
        }
        return null;
    }
    
    @Override
    public void postFetch(final ObjectProvider sm) {
    }
    
    @Override
    public void insertPostProcessing(final ObjectProvider op) {
    }
    
    @Override
    public void postInsert(final ObjectProvider sm) {
        final Object pc = sm.provideField(this.mmd.getAbsoluteFieldNumber());
        if (pc == null) {
            return;
        }
        final ClassLoaderResolver clr = sm.getExecutionContext().getClassLoaderResolver();
        final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(clr);
        final RelationType relationType = this.mmd.getRelationType(clr);
        if (relationType == RelationType.ONE_TO_ONE_BI) {
            final ObjectProvider otherSM = sm.getExecutionContext().findObjectProvider(pc);
            final AbstractMemberMetaData relatedMmd = this.mmd.getRelatedMemberMetaDataForObject(clr, sm.getObject(), pc);
            if (relatedMmd == null) {
                throw new NucleusUserException("You have a field " + this.mmd.getFullFieldName() + " that is 1-1 bidir yet cannot find the equivalent field at the other side. Why is that?");
            }
            final Object relatedValue = otherSM.provideField(relatedMmd.getAbsoluteFieldNumber());
            if (relatedValue == null) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(PersistableMapping.LOCALISER_RDBMS.msg("041018", sm.getObjectAsPrintable(), this.mmd.getFullFieldName(), StringUtils.toJVMIDString(pc), relatedMmd.getFullFieldName()));
                }
                otherSM.replaceField(relatedMmd.getAbsoluteFieldNumber(), sm.getObject());
            }
            else if (relatedValue != sm.getObject()) {
                throw new NucleusUserException(PersistableMapping.LOCALISER_RDBMS.msg("041020", sm.getObjectAsPrintable(), this.mmd.getFullFieldName(), StringUtils.toJVMIDString(pc), StringUtils.toJVMIDString(relatedValue)));
            }
        }
        else if (relationType == RelationType.MANY_TO_ONE_BI && relatedMmds[0].hasCollection()) {
            final ObjectProvider otherSM = sm.getExecutionContext().findObjectProvider(pc);
            if (otherSM != null) {
                final Collection relatedColl = (Collection)otherSM.provideField(relatedMmds[0].getAbsoluteFieldNumber());
                if (relatedColl != null && !(relatedColl instanceof SCOCollection)) {
                    final boolean contained = relatedColl.contains(sm.getObject());
                    if (!contained) {
                        NucleusLogger.PERSISTENCE.info(PersistableMapping.LOCALISER_RDBMS.msg("041022", sm.getObjectAsPrintable(), this.mmd.getFullFieldName(), StringUtils.toJVMIDString(pc), relatedMmds[0].getFullFieldName()));
                    }
                }
            }
        }
        else if (relationType == RelationType.MANY_TO_ONE_UNI) {
            ObjectProvider otherSM = sm.getExecutionContext().findObjectProvider(pc);
            if (otherSM == null) {
                final Object other = sm.getExecutionContext().persistObjectInternal(pc, null, -1, 0);
                otherSM = sm.getExecutionContext().findObjectProvider(other);
            }
            final PersistableRelationStore store = (PersistableRelationStore)this.storeMgr.getBackingStoreForField(sm.getExecutionContext().getClassLoaderResolver(), this.mmd, this.mmd.getType());
            store.add(sm, otherSM);
        }
    }
    
    @Override
    public void postUpdate(final ObjectProvider sm) {
        final Object pc = sm.provideField(this.mmd.getAbsoluteFieldNumber());
        final ClassLoaderResolver clr = sm.getExecutionContext().getClassLoaderResolver();
        final RelationType relationType = this.mmd.getRelationType(clr);
        if (pc == null) {
            if (relationType == RelationType.MANY_TO_ONE_UNI) {
                final PersistableRelationStore store = (PersistableRelationStore)this.storeMgr.getBackingStoreForField(sm.getExecutionContext().getClassLoaderResolver(), this.mmd, this.mmd.getType());
                store.remove(sm);
            }
            return;
        }
        ObjectProvider otherSM = sm.getExecutionContext().findObjectProvider(pc);
        if (otherSM == null && (relationType == RelationType.ONE_TO_ONE_BI || relationType == RelationType.MANY_TO_ONE_BI || relationType == RelationType.MANY_TO_ONE_UNI)) {
            final Object other = sm.getExecutionContext().persistObjectInternal(pc, null, -1, 0);
            otherSM = sm.getExecutionContext().findObjectProvider(other);
        }
        if (relationType == RelationType.MANY_TO_ONE_UNI) {
            final PersistableRelationStore store2 = (PersistableRelationStore)this.storeMgr.getBackingStoreForField(sm.getExecutionContext().getClassLoaderResolver(), this.mmd, this.mmd.getType());
            store2.update(sm, otherSM);
        }
    }
    
    @Override
    public void preDelete(final ObjectProvider sm) {
        final ExecutionContext ec = sm.getExecutionContext();
        final int fieldNumber = this.mmd.getAbsoluteFieldNumber();
        try {
            sm.isLoaded(fieldNumber);
        }
        catch (RuntimeException re) {
            return;
        }
        final Object pc = sm.provideField(fieldNumber);
        if (pc == null) {
            return;
        }
        final ClassLoaderResolver clr = sm.getExecutionContext().getClassLoaderResolver();
        final RelationType relationType = this.mmd.getRelationType(clr);
        if (relationType == RelationType.MANY_TO_ONE_UNI) {
            final PersistableRelationStore store = (PersistableRelationStore)this.storeMgr.getBackingStoreForField(sm.getExecutionContext().getClassLoaderResolver(), this.mmd, this.mmd.getType());
            store.remove(sm);
        }
        boolean dependent = this.mmd.isDependent();
        if (this.mmd.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(clr);
        boolean hasFK = false;
        if (!dependent) {
            if (this.mmd.getForeignKeyMetaData() != null) {
                hasFK = true;
            }
            if (relatedMmds != null && relatedMmds[0].getForeignKeyMetaData() != null) {
                hasFK = true;
            }
            if (ec.getStringProperty("datanucleus.deletionPolicy").equals("JDO2")) {
                hasFK = false;
            }
        }
        if (relationType == RelationType.ONE_TO_ONE_UNI || (relationType == RelationType.ONE_TO_ONE_BI && this.mmd.getMappedBy() == null)) {
            if (dependent) {
                final boolean relatedObjectDeleted = ec.getApiAdapter().isDeleted(pc);
                if (this.isNullable() && !relatedObjectDeleted) {
                    sm.replaceFieldMakeDirty(fieldNumber, null);
                    this.storeMgr.getPersistenceHandler().updateObject(sm, new int[] { fieldNumber });
                    if (!relatedObjectDeleted) {
                        ec.deleteObjectInternal(pc);
                    }
                }
                else {
                    NucleusLogger.DATASTORE_PERSIST.warn("Delete of " + StringUtils.toJVMIDString(sm.getObject()) + " needs delete of related object at " + this.mmd.getFullFieldName() + " but cannot delete it direct since FK is here");
                }
            }
            else {
                final AbstractMemberMetaData relatedMmd = this.mmd.getRelatedMemberMetaDataForObject(clr, sm.getObject(), pc);
                if (relatedMmd != null) {
                    final ObjectProvider otherSM = ec.findObjectProvider(pc);
                    if (otherSM != null) {
                        final Object currentValue = otherSM.provideField(relatedMmd.getAbsoluteFieldNumber());
                        if (currentValue != null) {
                            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                                NucleusLogger.PERSISTENCE.debug(PersistableMapping.LOCALISER_RDBMS.msg("041019", StringUtils.toJVMIDString(pc), relatedMmd.getFullFieldName(), sm.getObjectAsPrintable()));
                            }
                            otherSM.replaceFieldMakeDirty(relatedMmd.getAbsoluteFieldNumber(), null);
                            if (ec.getManageRelations()) {
                                otherSM.getExecutionContext().getRelationshipManager(otherSM).relationChange(relatedMmd.getAbsoluteFieldNumber(), sm.getObject(), null);
                            }
                        }
                    }
                }
            }
        }
        else if (relationType == RelationType.ONE_TO_ONE_BI && this.mmd.getMappedBy() != null) {
            final DatastoreClass relatedTable = this.storeMgr.getDatastoreClass(relatedMmds[0].getClassName(), clr);
            final JavaTypeMapping relatedMapping = relatedTable.getMemberMapping(relatedMmds[0]);
            final boolean isNullable = relatedMapping.isNullable();
            final ObjectProvider otherSM2 = ec.findObjectProvider(pc);
            if (dependent) {
                if (isNullable) {
                    otherSM2.replaceFieldMakeDirty(relatedMmds[0].getAbsoluteFieldNumber(), null);
                    this.storeMgr.getPersistenceHandler().updateObject(otherSM2, new int[] { relatedMmds[0].getAbsoluteFieldNumber() });
                }
                ec.deleteObjectInternal(pc);
            }
            else if (!hasFK && this.isNullable()) {
                final Object currentRelatedValue = otherSM2.provideField(relatedMmds[0].getAbsoluteFieldNumber());
                if (currentRelatedValue != null) {
                    otherSM2.replaceFieldMakeDirty(relatedMmds[0].getAbsoluteFieldNumber(), null);
                    this.storeMgr.getPersistenceHandler().updateObject(otherSM2, new int[] { relatedMmds[0].getAbsoluteFieldNumber() });
                    if (ec.getManageRelations()) {
                        otherSM2.getExecutionContext().getRelationshipManager(otherSM2).relationChange(relatedMmds[0].getAbsoluteFieldNumber(), sm.getObject(), null);
                    }
                }
            }
        }
        else if (relationType == RelationType.MANY_TO_ONE_BI) {
            final ObjectProvider otherSM3 = ec.findObjectProvider(pc);
            if (relatedMmds[0].getJoinMetaData() == null) {
                if (!otherSM3.isDeleting()) {
                    if (dependent) {
                        if (this.isNullable()) {
                            sm.replaceFieldMakeDirty(fieldNumber, null);
                            this.storeMgr.getPersistenceHandler().updateObject(sm, new int[] { fieldNumber });
                        }
                        if (!ec.getApiAdapter().isDeleted(pc)) {
                            ec.deleteObjectInternal(pc);
                        }
                    }
                    else if (relatedMmds[0].hasCollection()) {
                        if (!ec.getApiAdapter().isDeleted(otherSM3.getObject()) && !otherSM3.isDeleting()) {
                            ec.markDirty(otherSM3, false);
                            otherSM3.isLoaded(relatedMmds[0].getAbsoluteFieldNumber());
                            final Collection otherColl = (Collection)otherSM3.provideField(relatedMmds[0].getAbsoluteFieldNumber());
                            if (otherColl != null) {
                                if (ec.getManageRelations()) {
                                    otherSM3.getExecutionContext().getRelationshipManager(otherSM3).relationRemove(relatedMmds[0].getAbsoluteFieldNumber(), sm.getObject());
                                }
                                NucleusLogger.PERSISTENCE.debug("ManagedRelationships : delete of object causes removal from collection at " + relatedMmds[0].getFullFieldName());
                                otherColl.remove(sm.getObject());
                            }
                        }
                    }
                    else if (relatedMmds[0].hasMap()) {}
                }
            }
            else if (dependent) {
                ec.deleteObjectInternal(pc);
            }
            else if (relatedMmds[0].hasCollection()) {
                if (!ec.getApiAdapter().isDeleted(otherSM3.getObject()) && !otherSM3.isDeleting()) {
                    ec.markDirty(otherSM3, false);
                    otherSM3.isLoaded(relatedMmds[0].getAbsoluteFieldNumber());
                    final Collection otherColl = (Collection)otherSM3.provideField(relatedMmds[0].getAbsoluteFieldNumber());
                    if (otherColl != null) {
                        NucleusLogger.PERSISTENCE.debug("ManagedRelationships : delete of object causes removal from collection at " + relatedMmds[0].getFullFieldName());
                        otherColl.remove(sm.getObject());
                    }
                }
            }
            else if (relatedMmds[0].hasMap()) {}
        }
        else if (relationType == RelationType.MANY_TO_ONE_UNI && dependent) {
            ec.deleteObjectInternal(pc);
        }
    }
}
