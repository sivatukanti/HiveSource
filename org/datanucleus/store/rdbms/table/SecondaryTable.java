// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.store.rdbms.mapping.MappingConsumer;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.metadata.ForeignKeyAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.ColumnMetaDataContainer;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.metadata.JoinMetaData;

public class SecondaryTable extends AbstractClassTable implements SecondaryDatastoreClass
{
    private ClassTable primaryTable;
    private JoinMetaData joinMetaData;
    
    SecondaryTable(final DatastoreIdentifier tableName, final RDBMSStoreManager storeMgr, final ClassTable primaryTable, final JoinMetaData jmd, final ClassLoaderResolver clr) {
        super(tableName, storeMgr);
        if (primaryTable == null) {
            throw new NucleusUserException(SecondaryTable.LOCALISER.msg("057045", tableName.getIdentifierName()));
        }
        this.primaryTable = primaryTable;
        this.joinMetaData = jmd;
        if (this.joinMetaData == null) {
            final JoinMetaData[] joins = this.primaryTable.getClassMetaData().getJoinMetaData();
            for (int i = 0; i < joins.length; ++i) {
                if (tableName.getIdentifierName().equals(joins[i].getTable())) {
                    this.joinMetaData = joins[i];
                    break;
                }
            }
        }
    }
    
    @Override
    public void preInitialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
        if (!this.isPKInitialized()) {
            this.initializePK(clr);
        }
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(SecondaryTable.LOCALISER.msg("057023", this));
        }
        this.state = 2;
    }
    
    @Override
    public void postInitialize(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
    }
    
    @Override
    protected void initializePK(final ClassLoaderResolver clr) {
        this.assertIsPKUninitialized();
        if (this.primaryTable.getIdentityType() == IdentityType.APPLICATION) {
            this.addApplicationIdUsingClassTableId(this.joinMetaData, this.primaryTable, clr, this.primaryTable.getClassMetaData());
        }
        else if (this.primaryTable.getIdentityType() == IdentityType.DATASTORE) {
            ColumnMetaData colmd = null;
            if (this.joinMetaData != null && this.joinMetaData.getColumnMetaData() != null && this.joinMetaData.getColumnMetaData().length > 0) {
                colmd = this.joinMetaData.getColumnMetaData()[0];
            }
            this.addDatastoreId(colmd, this.primaryTable, this.primaryTable.getClassMetaData());
        }
        this.state = 1;
    }
    
    @Override
    public PrimaryKey getPrimaryKey() {
        final PrimaryKey pk = super.getPrimaryKey();
        if (this.joinMetaData == null) {
            throw new NucleusUserException("A relationship to a secondary table requires a <join> specification. The secondary table is " + this.getDatastoreIdentifierFullyQualified() + " and the primary table is " + this.getPrimaryTable() + ". The fields mapped to this secondary table are: " + this.memberMappingsMap.keySet().toString());
        }
        final PrimaryKeyMetaData pkmd = this.joinMetaData.getPrimaryKeyMetaData();
        if (pkmd != null && pkmd.getName() != null) {
            pk.setName(pkmd.getName());
        }
        return pk;
    }
    
    @Override
    public DatastoreClass getPrimaryDatastoreClass() {
        return this.primaryTable;
    }
    
    @Override
    public JoinMetaData getJoinMetaData() {
        return this.joinMetaData;
    }
    
    @Override
    public IdentityType getIdentityType() {
        return this.primaryTable.getIdentityType();
    }
    
    @Override
    public String getType() {
        return this.primaryTable.getType();
    }
    
    @Override
    public boolean isObjectIdDatastoreAttributed() {
        return false;
    }
    
    @Override
    public boolean isBaseDatastoreClass() {
        return this.primaryTable.isBaseDatastoreClass();
    }
    
    @Override
    public DatastoreClass getBaseDatastoreClass() {
        return this.primaryTable.getBaseDatastoreClass();
    }
    
    @Override
    public DatastoreClass getBaseDatastoreClassWithMember(final AbstractMemberMetaData mmd) {
        return this.primaryTable.getBaseDatastoreClassWithMember(mmd);
    }
    
    @Override
    public DatastoreClass getSuperDatastoreClass() {
        return null;
    }
    
    @Override
    public boolean isSuperDatastoreClass(final DatastoreClass table) {
        return false;
    }
    
    @Override
    public Collection getSecondaryDatastoreClasses() {
        return null;
    }
    
    @Override
    public boolean managesClass(final String className) {
        return false;
    }
    
    @Override
    public String[] getManagedClasses() {
        return null;
    }
    
    protected List getExpectedForeignKeys() {
        this.assertIsInitialized();
        boolean autoMode = false;
        if (this.storeMgr.getStringProperty("datanucleus.rdbms.constraintCreateMode").equals("DataNucleus")) {
            autoMode = true;
        }
        final ArrayList foreignKeys = new ArrayList();
        final ForeignKeyMetaData fkmd = (this.joinMetaData != null) ? this.joinMetaData.getForeignKeyMetaData() : null;
        if (autoMode || (fkmd != null && fkmd.getDeleteAction() != ForeignKeyAction.NONE)) {
            final ForeignKey fk = new ForeignKey(this.getIdMapping(), this.dba, this.primaryTable, fkmd != null && fkmd.isDeferred());
            if (fkmd != null && fkmd.getName() != null) {
                fk.setName(fkmd.getName());
            }
            foreignKeys.add(0, fk);
        }
        return foreignKeys;
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        this.assertIsInitialized();
        final JavaTypeMapping m = this.memberMappingsMap.get(mmd);
        if (m != null) {
            return m;
        }
        return null;
    }
    
    @Override
    public JavaTypeMapping getMemberMappingInDatastoreClass(final AbstractMemberMetaData mmd) {
        return this.getMemberMapping(mmd);
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final String fieldName) {
        return this.getMemberMapping(this.primaryTable.getMetaDataForMember(fieldName));
    }
    
    @Override
    public JavaTypeMapping getIdMapping() {
        if (this.idMapping != null) {
            return this.idMapping;
        }
        final PersistableMapping mapping = new PersistableMapping();
        mapping.initialize(this.getStoreManager(), this.primaryTable.getClassMetaData().getFullClassName());
        if (this.getIdentityType() == IdentityType.DATASTORE) {
            mapping.addJavaTypeMapping(this.datastoreIDMapping);
        }
        else if (this.getIdentityType() == IdentityType.APPLICATION) {
            for (int i = 0; i < this.pkMappings.length; ++i) {
                mapping.addJavaTypeMapping(this.pkMappings[i]);
            }
        }
        return this.idMapping = mapping;
    }
    
    @Override
    public void providePrimaryKeyMappings(final MappingConsumer consumer) {
        consumer.preConsumeMapping(this.highestMemberNumber + 1);
        final ClassMetaData cmd = this.primaryTable.getClassMetaData();
        if (this.pkMappings != null) {
            final int[] primaryKeyFieldNumbers = cmd.getPKMemberPositions();
            for (int i = 0; i < this.pkMappings.length; ++i) {
                final AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(primaryKeyFieldNumbers[i]);
                consumer.consumeMapping(this.pkMappings[i], fmd);
            }
        }
        else {
            final int[] primaryKeyFieldNumbers = cmd.getPKMemberPositions();
            for (int countPkFields = cmd.getNoOfPrimaryKeyMembers(), j = 0; j < countPkFields; ++j) {
                final AbstractMemberMetaData pkfmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(primaryKeyFieldNumbers[j]);
                consumer.consumeMapping(this.getMemberMapping(pkfmd), pkfmd);
            }
        }
    }
    
    @Override
    public void provideExternalMappings(final MappingConsumer consumer, final int mappingType) {
    }
    
    @Override
    public void provideUnmappedColumns(final MappingConsumer consumer) {
    }
    
    @Override
    public JavaTypeMapping getExternalMapping(final AbstractMemberMetaData fmd, final int mappingType) {
        throw new NucleusException("N/A").setFatal();
    }
    
    @Override
    public AbstractMemberMetaData getMetaDataForExternalMapping(final JavaTypeMapping mapping, final int mappingType) {
        throw new NucleusException("N/A").setFatal();
    }
}
