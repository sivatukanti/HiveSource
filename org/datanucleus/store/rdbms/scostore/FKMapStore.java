// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.mapping.java.SerialisedMapping;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.Transaction;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedKeyPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import java.util.NoSuchElementException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.scostore.SetStore;
import java.util.Iterator;
import org.datanucleus.ExecutionContext;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.FieldValues;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.rdbms.exceptions.ClassDefinitionException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.query.StatementParameterMapping;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;

public class FKMapStore extends AbstractMapStore
{
    private String updateFkStmt;
    private String getStmtLocked;
    private String getStmtUnlocked;
    private StatementClassMapping getMappingDef;
    private StatementParameterMapping getMappingParams;
    private final int ownerFieldNumber;
    protected int keyFieldNumber;
    private int valueFieldNumber;
    
    public FKMapStore(final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
        this.getStmtLocked = null;
        this.getStmtUnlocked = null;
        this.getMappingDef = null;
        this.getMappingParams = null;
        this.keyFieldNumber = -1;
        this.valueFieldNumber = -1;
        this.setOwner(mmd);
        final MapMetaData mapmd = (MapMetaData)mmd.getContainer();
        if (mapmd == null) {
            throw new NucleusUserException(FKMapStore.LOCALISER.msg("056002", mmd.getFullFieldName()));
        }
        boolean keyStoredInValue = false;
        if (mmd.getKeyMetaData() != null && mmd.getKeyMetaData().getMappedBy() != null) {
            keyStoredInValue = true;
        }
        else if (mmd.getValueMetaData() != null && mmd.getValueMetaData().getMappedBy() == null) {
            throw new NucleusUserException(FKMapStore.LOCALISER.msg("056071", mmd.getFullFieldName()));
        }
        this.keyType = mapmd.getKeyType();
        this.valueType = mapmd.getValueType();
        final Class keyClass = clr.classForName(this.keyType);
        final Class valueClass = clr.classForName(this.valueType);
        final ApiAdapter api = this.getStoreManager().getApiAdapter();
        if (keyStoredInValue && !api.isPersistable(valueClass)) {
            throw new NucleusUserException(FKMapStore.LOCALISER.msg("056072", mmd.getFullFieldName(), this.valueType));
        }
        if (!keyStoredInValue && !api.isPersistable(keyClass)) {
            throw new NucleusUserException(FKMapStore.LOCALISER.msg("056073", mmd.getFullFieldName(), this.keyType));
        }
        final String ownerFieldName = mmd.getMappedBy();
        if (keyStoredInValue) {
            this.vmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(valueClass, clr);
            if (this.vmd == null) {
                throw new NucleusUserException(FKMapStore.LOCALISER.msg("056070", this.valueType, mmd.getFullFieldName()));
            }
            this.valueTable = storeMgr.getDatastoreClass(this.valueType, clr);
            this.valueMapping = storeMgr.getDatastoreClass(this.valueType, clr).getIdMapping();
            this.valuesAreEmbedded = false;
            this.valuesAreSerialised = false;
            if (mmd.getMappedBy() != null) {
                final AbstractMemberMetaData vofmd = this.vmd.getMetaDataForMember(ownerFieldName);
                if (vofmd == null) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056067", mmd.getFullFieldName(), ownerFieldName, valueClass.getName()));
                }
                if (!clr.isAssignableFrom(vofmd.getType(), mmd.getAbstractClassMetaData().getFullClassName())) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056068", mmd.getFullFieldName(), vofmd.getFullFieldName(), vofmd.getTypeName(), mmd.getAbstractClassMetaData().getFullClassName()));
                }
                this.ownerFieldNumber = this.vmd.getAbsolutePositionOfMember(ownerFieldName);
                this.ownerMapping = this.valueTable.getMemberMapping(vofmd);
                if (this.ownerMapping == null) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("RDBMS.SCO.Map.InverseOwnerMappedByFieldNotPresent", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.valueType, ownerFieldName));
                }
                if (this.isEmbeddedMapping(this.ownerMapping)) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056055", ownerFieldName, this.valueType, vofmd.getTypeName(), mmd.getClassName()));
                }
            }
            else {
                this.ownerFieldNumber = -1;
                this.ownerMapping = this.valueTable.getExternalMapping(mmd, 5);
                if (this.ownerMapping == null) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056056", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.valueType));
                }
            }
            if (mmd.getKeyMetaData() == null || mmd.getKeyMetaData().getMappedBy() == null) {
                throw new NucleusUserException(FKMapStore.LOCALISER.msg("056050", valueClass.getName()));
            }
            AbstractMemberMetaData vkfmd = null;
            final String key_field_name = mmd.getKeyMetaData().getMappedBy();
            if (key_field_name != null) {
                vkfmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForMember(valueClass, clr, key_field_name);
                if (vkfmd == null) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056052", valueClass.getName(), key_field_name));
                }
            }
            if (vkfmd == null) {
                throw new ClassDefinitionException(FKMapStore.LOCALISER.msg("056050", mmd.getFullFieldName()));
            }
            if (!ClassUtils.typesAreCompatible(vkfmd.getType(), this.keyType, clr)) {
                throw new NucleusUserException(FKMapStore.LOCALISER.msg("056051", mmd.getFullFieldName(), this.keyType, vkfmd.getType().getName()));
            }
            final String keyFieldName = vkfmd.getName();
            this.keyFieldNumber = this.vmd.getAbsolutePositionOfMember(keyFieldName);
            this.keyMapping = this.valueTable.getMemberMapping(this.vmd.getMetaDataForManagedMemberAtAbsolutePosition(this.keyFieldNumber));
            if (this.keyMapping == null) {
                throw new NucleusUserException(FKMapStore.LOCALISER.msg("056053", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.valueType, keyFieldName));
            }
            if (!this.keyMapping.hasSimpleDatastoreRepresentation()) {
                throw new NucleusUserException("Invalid field type for map key field: " + mmd.getFullFieldName());
            }
            this.keysAreEmbedded = this.isEmbeddedMapping(this.keyMapping);
            this.keysAreSerialised = this.isEmbeddedMapping(this.keyMapping);
            this.mapTable = this.valueTable;
            if (mmd.getMappedBy() != null && this.ownerMapping.getTable() != this.mapTable) {
                this.mapTable = this.ownerMapping.getTable();
            }
        }
        else {
            this.kmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(keyClass, clr);
            if (this.kmd == null) {
                throw new NucleusUserException(FKMapStore.LOCALISER.msg("056069", this.keyType, mmd.getFullFieldName()));
            }
            this.valueTable = storeMgr.getDatastoreClass(this.keyType, clr);
            this.keyMapping = storeMgr.getDatastoreClass(this.keyType, clr).getIdMapping();
            this.keysAreEmbedded = false;
            this.keysAreSerialised = false;
            if (mmd.getMappedBy() != null) {
                final AbstractMemberMetaData kofmd = this.kmd.getMetaDataForMember(ownerFieldName);
                if (kofmd == null) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056067", mmd.getFullFieldName(), ownerFieldName, keyClass.getName()));
                }
                if (!ClassUtils.typesAreCompatible(kofmd.getType(), mmd.getAbstractClassMetaData().getFullClassName(), clr)) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056068", mmd.getFullFieldName(), kofmd.getFullFieldName(), kofmd.getTypeName(), mmd.getAbstractClassMetaData().getFullClassName()));
                }
                this.ownerFieldNumber = this.kmd.getAbsolutePositionOfMember(ownerFieldName);
                this.ownerMapping = this.valueTable.getMemberMapping(kofmd);
                if (this.ownerMapping == null) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("RDBMS.SCO.Map.InverseOwnerMappedByFieldNotPresent", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.keyType, ownerFieldName));
                }
                if (this.isEmbeddedMapping(this.ownerMapping)) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056055", ownerFieldName, this.keyType, kofmd.getTypeName(), mmd.getClassName()));
                }
            }
            else {
                this.ownerFieldNumber = -1;
                this.ownerMapping = this.valueTable.getExternalMapping(mmd, 5);
                if (this.ownerMapping == null) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056056", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.keyType));
                }
            }
            if (mmd.getValueMetaData() == null || mmd.getValueMetaData().getMappedBy() == null) {
                throw new NucleusUserException(FKMapStore.LOCALISER.msg("056057", keyClass.getName()));
            }
            AbstractMemberMetaData vkfmd = null;
            final String value_field_name = mmd.getValueMetaData().getMappedBy();
            if (value_field_name != null) {
                vkfmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForMember(keyClass, clr, value_field_name);
                if (vkfmd == null) {
                    throw new NucleusUserException(FKMapStore.LOCALISER.msg("056059", keyClass.getName(), value_field_name));
                }
            }
            if (vkfmd == null) {
                throw new ClassDefinitionException(FKMapStore.LOCALISER.msg("056057", mmd.getFullFieldName()));
            }
            if (!ClassUtils.typesAreCompatible(vkfmd.getType(), this.valueType, clr)) {
                throw new NucleusUserException(FKMapStore.LOCALISER.msg("056058", mmd.getFullFieldName(), this.valueType, vkfmd.getType().getName()));
            }
            final String valueFieldName = vkfmd.getName();
            this.valueFieldNumber = this.kmd.getAbsolutePositionOfMember(valueFieldName);
            this.valueMapping = this.valueTable.getMemberMapping(this.kmd.getMetaDataForManagedMemberAtAbsolutePosition(this.valueFieldNumber));
            if (this.valueMapping == null) {
                throw new NucleusUserException(FKMapStore.LOCALISER.msg("056054", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.keyType, valueFieldName));
            }
            if (!this.valueMapping.hasSimpleDatastoreRepresentation()) {
                throw new NucleusUserException("Invalid field type for map value field: " + mmd.getFullFieldName());
            }
            this.valuesAreEmbedded = this.isEmbeddedMapping(this.valueMapping);
            this.valuesAreSerialised = this.isEmbeddedMapping(this.valueMapping);
            this.mapTable = this.valueTable;
            if (mmd.getMappedBy() != null && this.ownerMapping.getTable() != this.mapTable) {
                this.mapTable = this.ownerMapping.getTable();
            }
        }
        this.initialise();
    }
    
    @Override
    protected void initialise() {
        super.initialise();
        this.updateFkStmt = this.getUpdateFkStmt();
    }
    
    private boolean updateValueFk(final ObjectProvider op, final Object value, final Object owner) {
        if (value == null) {
            return false;
        }
        this.validateValueForWriting(op, value);
        return this.updateValueFkInternal(op, value, owner);
    }
    
    private boolean updateKeyFk(final ObjectProvider op, final Object key, final Object owner) {
        if (key == null) {
            return false;
        }
        this.validateKeyForWriting(op, key);
        return this.updateKeyFkInternal(op, key, owner);
    }
    
    @Override
    protected void validateValueType(final ClassLoaderResolver clr, final Object value) {
        if (value == null) {
            throw new NullPointerException(FKMapStore.LOCALISER.msg("056063"));
        }
        super.validateValueType(clr, value);
    }
    
    @Override
    public Object put(final ObjectProvider op, final Object newKey, final Object newValue) {
        if (this.keyFieldNumber >= 0) {
            this.validateKeyForWriting(op, newKey);
            this.validateValueType(op.getExecutionContext().getClassLoaderResolver(), newValue);
        }
        else {
            this.validateKeyType(op.getExecutionContext().getClassLoaderResolver(), newKey);
            this.validateValueForWriting(op, newValue);
        }
        Object oldValue = this.get(op, newKey);
        if (oldValue != newValue) {
            if (this.vmd != null) {
                if (oldValue != null && !oldValue.equals(newValue)) {
                    this.removeValue(op, newKey, oldValue);
                }
                final ExecutionContext ec = op.getExecutionContext();
                final Object newOwner = op.getObject();
                if (ec.getApiAdapter().isPersistent(newValue)) {
                    if (ec != ec.getApiAdapter().getExecutionContext(newValue)) {
                        throw new NucleusUserException(FKMapStore.LOCALISER.msg("RDBMS.SCO.Map.WriteValueInvalidWithDifferentPM"), ec.getApiAdapter().getIdForObject(newValue));
                    }
                    final ObjectProvider vsm = ec.findObjectProvider(newValue);
                    if (this.ownerFieldNumber >= 0) {
                        vsm.isLoaded(this.ownerFieldNumber);
                        final Object oldOwner = vsm.provideField(this.ownerFieldNumber);
                        vsm.replaceFieldMakeDirty(this.ownerFieldNumber, newOwner);
                        if (ec.getManageRelations()) {
                            ec.getRelationshipManager(vsm).relationChange(this.ownerFieldNumber, oldOwner, newOwner);
                        }
                    }
                    else {
                        this.updateValueFk(op, newValue, newOwner);
                    }
                    vsm.isLoaded(this.keyFieldNumber);
                    final Object oldKey = vsm.provideField(this.keyFieldNumber);
                    vsm.replaceFieldMakeDirty(this.keyFieldNumber, newKey);
                    if (ec.getManageRelations()) {
                        ec.getRelationshipManager(vsm).relationChange(this.keyFieldNumber, oldKey, newKey);
                    }
                }
                else {
                    ec.persistObjectInternal(newValue, new FieldValues() {
                        @Override
                        public void fetchFields(final ObjectProvider vsm) {
                            if (FKMapStore.this.ownerFieldNumber >= 0) {
                                vsm.replaceFieldMakeDirty(FKMapStore.this.ownerFieldNumber, newOwner);
                            }
                            vsm.replaceFieldMakeDirty(FKMapStore.this.keyFieldNumber, newKey);
                            final JavaTypeMapping externalFKMapping = FKMapStore.this.valueTable.getExternalMapping(FKMapStore.this.ownerMemberMetaData, 5);
                            if (externalFKMapping != null) {
                                vsm.setAssociatedValue(externalFKMapping, op.getObject());
                            }
                        }
                        
                        @Override
                        public void fetchNonLoadedFields(final ObjectProvider op) {
                        }
                        
                        @Override
                        public FetchPlan getFetchPlanForLoading() {
                            return null;
                        }
                    }, 0);
                }
            }
            else {
                final ExecutionContext ec = op.getExecutionContext();
                final Object newOwner = op.getObject();
                if (ec.getApiAdapter().isPersistent(newKey)) {
                    if (ec != ec.getApiAdapter().getExecutionContext(newKey)) {
                        throw new NucleusUserException(FKMapStore.LOCALISER.msg("056060"), ec.getApiAdapter().getIdForObject(newKey));
                    }
                    final ObjectProvider vsm = ec.findObjectProvider(newKey);
                    if (this.ownerFieldNumber >= 0) {
                        vsm.isLoaded(this.ownerFieldNumber);
                        final Object oldOwner = vsm.provideField(this.ownerFieldNumber);
                        vsm.replaceFieldMakeDirty(this.ownerFieldNumber, newOwner);
                        if (ec.getManageRelations()) {
                            ec.getRelationshipManager(vsm).relationChange(this.ownerFieldNumber, oldOwner, newOwner);
                        }
                    }
                    else {
                        this.updateKeyFk(op, newKey, newOwner);
                    }
                    vsm.isLoaded(this.valueFieldNumber);
                    oldValue = vsm.provideField(this.valueFieldNumber);
                    vsm.replaceFieldMakeDirty(this.valueFieldNumber, newValue);
                    if (ec.getManageRelations()) {
                        ec.getRelationshipManager(vsm).relationChange(this.valueFieldNumber, oldValue, newValue);
                    }
                }
                else {
                    final Object newValueObj = newValue;
                    ec.persistObjectInternal(newKey, new FieldValues() {
                        @Override
                        public void fetchFields(final ObjectProvider vsm) {
                            if (FKMapStore.this.ownerFieldNumber >= 0) {
                                vsm.replaceFieldMakeDirty(FKMapStore.this.ownerFieldNumber, newOwner);
                            }
                            vsm.replaceFieldMakeDirty(FKMapStore.this.valueFieldNumber, newValueObj);
                            final JavaTypeMapping externalFKMapping = FKMapStore.this.valueTable.getExternalMapping(FKMapStore.this.ownerMemberMetaData, 5);
                            if (externalFKMapping != null) {
                                vsm.setAssociatedValue(externalFKMapping, op.getObject());
                            }
                        }
                        
                        @Override
                        public void fetchNonLoadedFields(final ObjectProvider op) {
                        }
                        
                        @Override
                        public FetchPlan getFetchPlanForLoading() {
                            return null;
                        }
                    }, 0);
                }
            }
        }
        if (this.ownerMemberMetaData.getMap().isDependentValue() && oldValue != null && !this.containsValue(op, oldValue)) {
            op.getExecutionContext().deleteObjectInternal(oldValue);
        }
        return oldValue;
    }
    
    @Override
    public Object remove(final ObjectProvider op, final Object key) {
        if (!this.allowNulls && key == null) {
            return null;
        }
        final Object oldValue = this.get(op, key);
        return this.remove(op, key, oldValue);
    }
    
    @Override
    public Object remove(final ObjectProvider op, final Object key, final Object oldValue) {
        final ExecutionContext ec = op.getExecutionContext();
        if (this.keyFieldNumber >= 0) {
            if (oldValue != null) {
                boolean deletingValue = false;
                final ObjectProvider vsm = ec.findObjectProvider(oldValue);
                if (this.ownerMemberMetaData.getMap().isDependentValue()) {
                    deletingValue = true;
                    ec.deleteObjectInternal(oldValue);
                    vsm.flush();
                }
                else if (this.ownerMapping.isNullable()) {
                    if (this.ownerFieldNumber >= 0) {
                        final Object oldOwner = vsm.provideField(this.ownerFieldNumber);
                        vsm.replaceFieldMakeDirty(this.ownerFieldNumber, null);
                        vsm.flush();
                        if (ec.getManageRelations()) {
                            ec.getRelationshipManager(vsm).relationChange(this.ownerFieldNumber, oldOwner, null);
                        }
                    }
                    else {
                        this.updateValueFkInternal(op, oldValue, null);
                    }
                }
                else {
                    deletingValue = true;
                    ec.deleteObjectInternal(oldValue);
                    vsm.flush();
                }
                if (this.ownerMemberMetaData.getMap().isDependentKey()) {
                    if (!deletingValue && this.keyMapping.isNullable()) {
                        vsm.replaceFieldMakeDirty(this.keyFieldNumber, null);
                        vsm.flush();
                        if (ec.getManageRelations()) {
                            ec.getRelationshipManager(vsm).relationChange(this.keyFieldNumber, key, null);
                        }
                    }
                    op.getExecutionContext().deleteObjectInternal(key);
                    final ObjectProvider keyOP = ec.findObjectProvider(key);
                    keyOP.flush();
                }
            }
        }
        else if (key != null) {
            boolean deletingKey = false;
            final ObjectProvider ksm = ec.findObjectProvider(key);
            if (this.ownerMemberMetaData.getMap().isDependentKey()) {
                deletingKey = true;
                ec.deleteObjectInternal(key);
                ksm.flush();
            }
            else if (this.ownerMapping.isNullable()) {
                if (this.ownerFieldNumber >= 0) {
                    final Object oldOwner = ksm.provideField(this.ownerFieldNumber);
                    ksm.replaceFieldMakeDirty(this.ownerFieldNumber, null);
                    ksm.flush();
                    if (ec.getManageRelations()) {
                        ec.getRelationshipManager(ksm).relationChange(this.ownerFieldNumber, oldOwner, null);
                    }
                }
                else {
                    this.updateKeyFkInternal(op, key, null);
                }
            }
            else {
                deletingKey = true;
                ec.deleteObjectInternal(key);
                ksm.flush();
            }
            if (this.ownerMemberMetaData.getMap().isDependentValue()) {
                if (!deletingKey && this.valueMapping.isNullable()) {
                    ksm.replaceFieldMakeDirty(this.valueFieldNumber, null);
                    ksm.flush();
                    if (ec.getManageRelations()) {
                        ec.getRelationshipManager(ksm).relationChange(this.valueFieldNumber, oldValue, null);
                    }
                }
                op.getExecutionContext().deleteObjectInternal(oldValue);
                final ObjectProvider valOP = ec.findObjectProvider(oldValue);
                valOP.flush();
            }
        }
        return oldValue;
    }
    
    private void removeValue(final ObjectProvider op, final Object key, final Object oldValue) {
        final ExecutionContext ec = op.getExecutionContext();
        if (this.keyMapping.isNullable()) {
            final ObjectProvider vsm = ec.findObjectProvider(oldValue);
            vsm.replaceFieldMakeDirty(this.keyFieldNumber, null);
            if (ec.getManageRelations()) {
                ec.getRelationshipManager(vsm).relationChange(this.keyFieldNumber, key, null);
            }
            if (this.ownerFieldNumber >= 0) {
                final Object oldOwner = vsm.provideField(this.ownerFieldNumber);
                vsm.replaceFieldMakeDirty(this.ownerFieldNumber, null);
                if (ec.getManageRelations()) {
                    ec.getRelationshipManager(vsm).relationChange(this.ownerFieldNumber, oldOwner, null);
                }
            }
            else {
                this.updateValueFk(op, oldValue, null);
            }
        }
        else {
            ec.deleteObjectInternal(oldValue);
        }
    }
    
    @Override
    public void clear(final ObjectProvider op) {
        final Iterator iter = this.keySetStore().iterator(op);
        while (iter.hasNext()) {
            final Object key = iter.next();
            if (key == null && !this.allowNulls) {
                continue;
            }
            this.remove(op, key);
        }
    }
    
    public void clearKeyOfValue(final ObjectProvider op, final Object key, final Object oldValue) {
        final ExecutionContext ec = op.getExecutionContext();
        if (this.keyMapping.isNullable()) {
            final ObjectProvider vsm = ec.findObjectProvider(oldValue);
            if (!ec.getApiAdapter().isDeleted(oldValue)) {
                vsm.replaceFieldMakeDirty(this.keyFieldNumber, null);
                if (ec.getManageRelations()) {
                    ec.getRelationshipManager(vsm).relationChange(this.keyFieldNumber, key, null);
                }
            }
        }
        else {
            ec.deleteObjectInternal(oldValue);
        }
    }
    
    @Override
    public synchronized SetStore keySetStore() {
        return new MapKeySetStore(this.valueTable, this, this.clr, this.ownerMapping, this.keyMapping, this.ownerMemberMetaData);
    }
    
    @Override
    public synchronized SetStore valueSetStore() {
        return new MapValueSetStore(this.valueTable, this, this.clr, this.ownerMapping, this.valueMapping, this.ownerMemberMetaData);
    }
    
    @Override
    public synchronized SetStore entrySetStore() {
        return new MapEntrySetStore(this.valueTable, this, this.clr, this.ownerMapping, this.keyMapping, this.valueMapping, this.ownerMemberMetaData);
    }
    
    private String getUpdateFkStmt() {
        final StringBuffer stmt = new StringBuffer("UPDATE ");
        stmt.append(this.getMapTable().toString());
        stmt.append(" SET ");
        for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append(" = ");
            stmt.append(((AbstractDatastoreMapping)this.ownerMapping.getDatastoreMapping(i)).getUpdateInputParameter());
        }
        stmt.append(" WHERE ");
        if (this.keyFieldNumber >= 0) {
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.valueMapping, null, true);
        }
        else {
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.keyMapping, null, true);
        }
        return stmt.toString();
    }
    
    protected boolean updateValueFkInternal(final ObjectProvider op, final Object value, final Object owner) {
        final ExecutionContext ec = op.getExecutionContext();
        boolean retval;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, this.updateFkStmt, false);
                try {
                    int jdbcPosition = 1;
                    if (owner == null) {
                        if (this.ownerMemberMetaData != null) {
                            this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(1, this.ownerMapping), null, op, this.ownerMemberMetaData.getAbsoluteFieldNumber());
                        }
                        else {
                            this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(1, this.ownerMapping), null);
                        }
                        jdbcPosition += this.ownerMapping.getNumberOfDatastoreMappings();
                    }
                    else {
                        jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    }
                    jdbcPosition = BackingStoreHelper.populateValueInStatement(ec, ps, value, jdbcPosition, this.valueMapping);
                    sqlControl.executeStatementUpdate(ec, mconn, this.updateFkStmt, ps, true);
                    retval = true;
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(FKMapStore.LOCALISER.msg("056027", this.updateFkStmt), e);
        }
        return retval;
    }
    
    protected boolean updateKeyFkInternal(final ObjectProvider op, final Object key, final Object owner) {
        final ExecutionContext ec = op.getExecutionContext();
        boolean retval;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, this.updateFkStmt, false);
                try {
                    int jdbcPosition = 1;
                    if (owner == null) {
                        if (this.ownerMemberMetaData != null) {
                            this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(1, this.ownerMapping), null, op, this.ownerMemberMetaData.getAbsoluteFieldNumber());
                        }
                        else {
                            this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(1, this.ownerMapping), null);
                        }
                        jdbcPosition += this.ownerMapping.getNumberOfDatastoreMappings();
                    }
                    else {
                        jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    }
                    jdbcPosition = BackingStoreHelper.populateKeyInStatement(ec, ps, key, jdbcPosition, this.keyMapping);
                    sqlControl.executeStatementUpdate(ec, mconn, this.updateFkStmt, ps, true);
                    retval = true;
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(FKMapStore.LOCALISER.msg("056027", this.updateFkStmt), e);
        }
        return retval;
    }
    
    @Override
    protected Object getValue(final ObjectProvider ownerOP, final Object key) throws NoSuchElementException {
        if (!this.validateKeyForReading(ownerOP, key)) {
            return null;
        }
        final ExecutionContext ec = ownerOP.getExecutionContext();
        if (this.getStmtLocked == null) {
            synchronized (this) {
                final SQLStatement sqlStmt = this.getSQLStatementForGet(ownerOP);
                this.getStmtUnlocked = sqlStmt.getSelectStatement().toSQL();
                sqlStmt.addExtension("lock-for-update", true);
                this.getStmtLocked = sqlStmt.getSelectStatement().toSQL();
            }
        }
        final Transaction tx = ec.getTransaction();
        final String stmt = (tx.getSerializeRead() != null && tx.getSerializeRead()) ? this.getStmtLocked : this.getStmtUnlocked;
        Object value = null;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                final StatementMappingIndex ownerIdx = this.getMappingParams.getMappingForParameter("owner");
                for (int numParams = ownerIdx.getNumberOfParameterOccurrences(), paramInstance = 0; paramInstance < numParams; ++paramInstance) {
                    ownerIdx.getMapping().setObject(ec, ps, ownerIdx.getParameterPositionsForOccurrence(paramInstance), ownerOP.getObject());
                }
                final StatementMappingIndex keyIdx = this.getMappingParams.getMappingForParameter("key");
                for (int numParams = keyIdx.getNumberOfParameterOccurrences(), paramInstance2 = 0; paramInstance2 < numParams; ++paramInstance2) {
                    keyIdx.getMapping().setObject(ec, ps, keyIdx.getParameterPositionsForOccurrence(paramInstance2), key);
                }
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        final boolean found = rs.next();
                        if (!found) {
                            throw new NoSuchElementException();
                        }
                        if (this.valuesAreEmbedded || this.valuesAreSerialised) {
                            final int[] param = new int[this.valueMapping.getNumberOfDatastoreMappings()];
                            for (int i = 0; i < param.length; ++i) {
                                param[i] = i + 1;
                            }
                            if (this.valueMapping instanceof SerialisedPCMapping || this.valueMapping instanceof SerialisedReferenceMapping || this.valueMapping instanceof EmbeddedKeyPCMapping) {
                                final int ownerFieldNumber = ((JoinTable)this.mapTable).getOwnerMemberMetaData().getAbsoluteFieldNumber();
                                value = this.valueMapping.getObject(ec, rs, param, ownerOP, ownerFieldNumber);
                            }
                            else {
                                value = this.valueMapping.getObject(ec, rs, param);
                            }
                        }
                        else if (this.valueMapping instanceof ReferenceMapping) {
                            final int[] param = new int[this.valueMapping.getNumberOfDatastoreMappings()];
                            for (int i = 0; i < param.length; ++i) {
                                param[i] = i + 1;
                            }
                            value = this.valueMapping.getObject(ec, rs, param);
                        }
                        else {
                            final ResultObjectFactory rof = this.storeMgr.newResultObjectFactory(this.vmd, this.getMappingDef, false, null, this.clr.classForName(this.valueType));
                            value = rof.getObject(ec, rs);
                        }
                        JDBCUtils.logWarnings(rs);
                    }
                    finally {
                        rs.close();
                    }
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(FKMapStore.LOCALISER.msg("056014", stmt), e);
        }
        return value;
    }
    
    protected SQLStatement getSQLStatementForGet(final ObjectProvider ownerOP) {
        SQLStatement sqlStmt = null;
        final ClassLoaderResolver clr = ownerOP.getExecutionContext().getClassLoaderResolver();
        final Class valueCls = clr.classForName(this.valueType);
        if (this.ownerMemberMetaData.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            this.getMappingDef = new StatementClassMapping();
            if (this.valueTable.getDiscriminatorMetaData() != null && this.valueTable.getDiscriminatorMetaData().getStrategy() != DiscriminatorStrategy.NONE) {
                if (ClassUtils.isReferenceType(valueCls)) {
                    final String[] clsNames = this.storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(this.valueType, clr);
                    final Class[] cls = new Class[clsNames.length];
                    for (int i = 0; i < clsNames.length; ++i) {
                        cls[i] = clr.classForName(clsNames[i]);
                    }
                    sqlStmt = new DiscriminatorStatementGenerator(this.storeMgr, clr, cls, true, (DatastoreIdentifier)null, (String)null).getStatement();
                }
                else {
                    sqlStmt = new DiscriminatorStatementGenerator(this.storeMgr, clr, valueCls, true, null, null).getStatement();
                }
                this.iterateUsingDiscriminator = true;
            }
            else {
                final UnionStatementGenerator stmtGen = new UnionStatementGenerator(this.storeMgr, clr, valueCls, true, null, null);
                stmtGen.setOption("selectNucleusType");
                this.getMappingDef.setNucleusTypeColumnName("NUCLEUS_TYPE");
                sqlStmt = stmtGen.getStatement();
            }
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.getMappingDef, ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.vmd, 0);
        }
        else {
            sqlStmt = new SQLStatement(this.storeMgr, this.mapTable, null, null);
            sqlStmt.setClassLoaderResolver(clr);
            if (this.vmd != null) {
                final SQLTable valueSqlTbl = sqlStmt.leftOuterJoin(sqlStmt.getPrimaryTable(), this.valueMapping, this.valueTable, null, this.valueTable.getIdMapping(), null, null);
                SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, this.getMappingDef, ownerOP.getExecutionContext().getFetchPlan(), valueSqlTbl, this.vmd, 0);
            }
            else {
                sqlStmt.select(sqlStmt.getPrimaryTable(), this.valueMapping, null);
            }
        }
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        final SQLTable ownerSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.ownerMapping);
        final SQLExpression ownerExpr = exprFactory.newExpression(sqlStmt, ownerSqlTbl, this.ownerMapping);
        final SQLExpression ownerVal = exprFactory.newLiteralParameter(sqlStmt, this.ownerMapping, null, "OWNER");
        sqlStmt.whereAnd(ownerExpr.eq(ownerVal), true);
        if (this.keyMapping instanceof SerialisedMapping) {
            final SQLExpression keyExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.keyMapping);
            final SQLExpression keyVal = exprFactory.newLiteralParameter(sqlStmt, this.keyMapping, null, "KEY");
            sqlStmt.whereAnd(new BooleanExpression(keyExpr, Expression.OP_LIKE, keyVal), true);
        }
        else {
            final SQLExpression keyExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), this.keyMapping);
            final SQLExpression keyVal = exprFactory.newLiteralParameter(sqlStmt, this.keyMapping, null, "KEY");
            sqlStmt.whereAnd(keyExpr.eq(keyVal), true);
        }
        int inputParamNum = 1;
        final StatementMappingIndex ownerIdx = new StatementMappingIndex(this.ownerMapping);
        final StatementMappingIndex keyIdx = new StatementMappingIndex(this.keyMapping);
        if (sqlStmt.getNumberOfUnions() > 0) {
            for (int j = 0; j < sqlStmt.getNumberOfUnions() + 1; ++j) {
                final int[] ownerPositions = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
                for (int k = 0; k < ownerPositions.length; ++k) {
                    ownerPositions[k] = inputParamNum++;
                }
                ownerIdx.addParameterOccurrence(ownerPositions);
                final int[] keyPositions = new int[this.keyMapping.getNumberOfDatastoreMappings()];
                for (int l = 0; l < keyPositions.length; ++l) {
                    keyPositions[l] = inputParamNum++;
                }
                keyIdx.addParameterOccurrence(keyPositions);
            }
        }
        else {
            final int[] ownerPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int m = 0; m < ownerPositions2.length; ++m) {
                ownerPositions2[m] = inputParamNum++;
            }
            ownerIdx.addParameterOccurrence(ownerPositions2);
            final int[] keyPositions2 = new int[this.keyMapping.getNumberOfDatastoreMappings()];
            for (int k = 0; k < keyPositions2.length; ++k) {
                keyPositions2[k] = inputParamNum++;
            }
            keyIdx.addParameterOccurrence(keyPositions2);
        }
        (this.getMappingParams = new StatementParameterMapping()).addMappingForParameter("owner", ownerIdx);
        this.getMappingParams.addMappingForParameter("key", keyIdx);
        return sqlStmt;
    }
}
