// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.mapping.MappingConsumer;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.mapping.java.StringMapping;
import org.datanucleus.store.rdbms.mapping.java.IntegerMapping;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import java.util.Properties;
import org.datanucleus.store.valuegenerator.AbstractGenerator;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.identity.OID;
import org.datanucleus.store.rdbms.mapping.java.OIDMapping;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.ColumnMetaDataContainer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.ClassLoaderResolver;
import java.util.HashMap;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;

public abstract class AbstractClassTable extends TableImpl
{
    protected Map<AbstractMemberMetaData, JavaTypeMapping> memberMappingsMap;
    protected JavaTypeMapping datastoreIDMapping;
    protected JavaTypeMapping[] pkMappings;
    protected JavaTypeMapping idMapping;
    protected JavaTypeMapping versionMapping;
    protected VersionMetaData versionMetaData;
    protected DiscriminatorMetaData discriminatorMetaData;
    protected JavaTypeMapping discriminatorMapping;
    protected int highestMemberNumber;
    protected JavaTypeMapping tenantMapping;
    
    public AbstractClassTable(final DatastoreIdentifier tableName, final RDBMSStoreManager storeMgr) {
        super(tableName, storeMgr);
        this.memberMappingsMap = new HashMap<AbstractMemberMetaData, JavaTypeMapping>();
        this.highestMemberNumber = 0;
    }
    
    public Table getPrimaryTable() {
        return this;
    }
    
    protected abstract void initializePK(final ClassLoaderResolver p0);
    
    public boolean managesMember(final String memberName) {
        return memberName != null && this.getMappingForMemberName(memberName) != null;
    }
    
    protected JavaTypeMapping getMappingForMemberName(final String memberName) {
        final Set<AbstractMemberMetaData> fields = this.memberMappingsMap.keySet();
        for (final AbstractMemberMetaData mmd : fields) {
            if (mmd.getFullFieldName().equals(memberName)) {
                return this.memberMappingsMap.get(mmd);
            }
        }
        return null;
    }
    
    public boolean managesMapping(final JavaTypeMapping mapping) {
        final Collection<JavaTypeMapping> mappings = this.memberMappingsMap.values();
        return mappings.contains(mapping) || mapping == this.discriminatorMapping || mapping == this.versionMapping || mapping == this.datastoreIDMapping || mapping == this.idMapping || mapping == this.tenantMapping;
    }
    
    final void addApplicationIdUsingClassTableId(final ColumnMetaDataContainer columnContainer, final DatastoreClass refTable, final ClassLoaderResolver clr, final AbstractClassMetaData cmd) {
        ColumnMetaData[] userdefinedCols = null;
        int nextUserdefinedCol = 0;
        if (columnContainer != null) {
            userdefinedCols = columnContainer.getColumnMetaData();
        }
        this.pkMappings = new JavaTypeMapping[cmd.getPKMemberPositions().length];
        for (int i = 0; i < cmd.getPKMemberPositions().length; ++i) {
            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(cmd.getPKMemberPositions()[i]);
            final JavaTypeMapping mapping = refTable.getMemberMapping(mmd);
            if (mapping == null) {
                throw new NucleusUserException("Cannot find mapping for field " + mmd.getFullFieldName() + " in table " + refTable.toString() + " " + StringUtils.objectArrayToString(refTable.getColumns()));
            }
            final JavaTypeMapping masterMapping = this.storeMgr.getMappingManager().getMapping(clr.classForName(mapping.getType()));
            masterMapping.setMemberMetaData(mmd);
            masterMapping.setTable(this);
            this.pkMappings[i] = masterMapping;
            for (int j = 0; j < mapping.getNumberOfDatastoreMappings(); ++j) {
                JavaTypeMapping m = masterMapping;
                final Column refColumn = mapping.getDatastoreMapping(j).getColumn();
                if (mapping instanceof PersistableMapping) {
                    m = this.storeMgr.getMappingManager().getMapping(clr.classForName(refColumn.getJavaTypeMapping().getType()));
                    ((PersistableMapping)masterMapping).addJavaTypeMapping(m);
                }
                ColumnMetaData userdefinedColumn = null;
                if (userdefinedCols != null) {
                    for (int k = 0; k < userdefinedCols.length; ++k) {
                        if (refColumn.getIdentifier().toString().equals(userdefinedCols[k].getTarget())) {
                            userdefinedColumn = userdefinedCols[k];
                            break;
                        }
                    }
                    if (userdefinedColumn == null && nextUserdefinedCol < userdefinedCols.length) {
                        userdefinedColumn = userdefinedCols[nextUserdefinedCol++];
                    }
                }
                Column idColumn = null;
                if (userdefinedColumn != null) {
                    idColumn = this.addColumn(refColumn.getStoredJavaType(), this.storeMgr.getIdentifierFactory().newIdentifier(IdentifierType.COLUMN, userdefinedColumn.getName()), m, refColumn.getColumnMetaData());
                }
                else {
                    idColumn = this.addColumn(refColumn.getStoredJavaType(), refColumn.getIdentifier(), m, refColumn.getColumnMetaData());
                }
                if (mapping.getDatastoreMapping(j).getColumn().getColumnMetaData() != null) {
                    refColumn.copyConfigurationTo(idColumn);
                }
                idColumn.setAsPrimaryKey();
                this.getStoreManager().getMappingManager().createDatastoreMapping(m, idColumn, refColumn.getJavaTypeMapping().getType());
            }
            final int absoluteFieldNumber = mmd.getAbsoluteFieldNumber();
            if (absoluteFieldNumber > this.highestMemberNumber) {
                this.highestMemberNumber = absoluteFieldNumber;
            }
        }
    }
    
    void addDatastoreId(final ColumnMetaData columnMetaData, final DatastoreClass refTable, final AbstractClassMetaData cmd) {
        (this.datastoreIDMapping = new OIDMapping()).setTable(this);
        this.datastoreIDMapping.initialize(this.storeMgr, cmd.getFullClassName());
        ColumnMetaData colmd = null;
        if (columnMetaData == null) {
            colmd = new ColumnMetaData();
        }
        else {
            colmd = columnMetaData;
        }
        if (colmd.getName() == null) {
            if (refTable != null) {
                colmd.setName(this.storeMgr.getIdentifierFactory().newColumnIdentifier(refTable.getIdentifier().getIdentifierName(), this.storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(OID.class), 1).getIdentifierName());
            }
            else {
                colmd.setName(this.storeMgr.getIdentifierFactory().newColumnIdentifier(this.identifier.getIdentifierName(), this.storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(OID.class), 0).getIdentifierName());
            }
        }
        final Column idColumn = this.addColumn(OID.class.getName(), this.storeMgr.getIdentifierFactory().newIdentifier(IdentifierType.COLUMN, colmd.getName()), this.datastoreIDMapping, colmd);
        idColumn.setAsPrimaryKey();
        String strategyName = cmd.getIdentityMetaData().getValueStrategy().toString();
        if (cmd.getIdentityMetaData().getValueStrategy().equals(IdentityStrategy.CUSTOM)) {
            strategyName = cmd.getIdentityMetaData().getValueStrategy().getCustomName();
        }
        Class valueGeneratedType = Long.class;
        try {
            AbstractGenerator generator = (AbstractGenerator)this.storeMgr.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store_valuegenerator", new String[] { "name", "unique" }, new String[] { strategyName, "true" }, "class-name", new Class[] { String.class, Properties.class }, new Object[] { null, null });
            if (generator == null) {
                generator = (AbstractGenerator)this.storeMgr.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store_valuegenerator", new String[] { "name", "datastore" }, new String[] { strategyName, this.storeMgr.getStoreManagerKey() }, "class-name", new Class[] { String.class, Properties.class }, new Object[] { null, null });
            }
            if (generator != null) {
                valueGeneratedType = (Class)generator.getClass().getMethod("getStorageClass", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
            }
        }
        catch (Exception e) {
            NucleusLogger.VALUEGENERATION.warn("Error retrieving storage class for value-generator " + strategyName + " " + e.getMessage());
        }
        this.storeMgr.getMappingManager().createDatastoreMapping(this.datastoreIDMapping, idColumn, valueGeneratedType.getName());
        if (this.isObjectIdDatastoreAttributed() && this instanceof DatastoreClass && ((DatastoreClass)this).isBaseDatastoreClass()) {
            idColumn.setIdentity(true);
        }
        if (idColumn.isIdentity() && !this.dba.supportsOption("IdentityColumns")) {
            throw new NucleusException(AbstractClassTable.LOCALISER.msg("057020", cmd.getFullClassName(), "datastore-identity")).setFatal();
        }
    }
    
    protected void addMultitenancyMapping(final ColumnMetaData colmd) {
        String colName = "TENANT_ID";
        if (colmd != null && colmd.getName() != null) {
            colName = colmd.getName();
        }
        String typeName = String.class.getName();
        if (colmd != null && colmd.getJdbcType() != null && colmd.getJdbcType().equalsIgnoreCase("INTEGER")) {
            typeName = Integer.class.getName();
        }
        if (typeName.equals(Integer.class.getName())) {
            this.tenantMapping = new IntegerMapping();
        }
        else {
            this.tenantMapping = new StringMapping();
        }
        this.tenantMapping.setTable(this);
        this.tenantMapping.initialize(this.storeMgr, typeName);
        final Column tenantColumn = this.addColumn(typeName, this.storeMgr.getIdentifierFactory().newIdentifier(IdentifierType.COLUMN, colName), this.tenantMapping, colmd);
        this.storeMgr.getMappingManager().createDatastoreMapping(this.tenantMapping, tenantColumn, typeName);
    }
    
    protected void addMemberMapping(final JavaTypeMapping fieldMapping) {
        final AbstractMemberMetaData mmd = fieldMapping.getMemberMetaData();
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            final StringBuffer columnsStr = new StringBuffer();
            for (int i = 0; i < fieldMapping.getNumberOfDatastoreMappings(); ++i) {
                if (i > 0) {
                    columnsStr.append(",");
                }
                columnsStr.append(fieldMapping.getDatastoreMapping(i).getColumn());
            }
            if (fieldMapping.getNumberOfDatastoreMappings() == 0) {
                columnsStr.append("[none]");
            }
            final StringBuffer datastoreMappingTypes = new StringBuffer();
            for (int j = 0; j < fieldMapping.getNumberOfDatastoreMappings(); ++j) {
                if (j > 0) {
                    datastoreMappingTypes.append(',');
                }
                datastoreMappingTypes.append(fieldMapping.getDatastoreMapping(j).getClass().getName());
            }
            NucleusLogger.DATASTORE_SCHEMA.debug(AbstractClassTable.LOCALISER.msg("057010", mmd.getFullFieldName(), columnsStr.toString(), fieldMapping.getClass().getName(), datastoreMappingTypes.toString()));
        }
        this.memberMappingsMap.put(mmd, fieldMapping);
        final int absoluteFieldNumber = mmd.getAbsoluteFieldNumber();
        if (absoluteFieldNumber > this.highestMemberNumber) {
            this.highestMemberNumber = absoluteFieldNumber;
        }
    }
    
    public abstract IdentityType getIdentityType();
    
    public abstract boolean isObjectIdDatastoreAttributed();
    
    public JavaTypeMapping getDatastoreObjectIdMapping() {
        this.assertIsInitialized();
        return this.datastoreIDMapping;
    }
    
    @Override
    public JavaTypeMapping getVersionMapping(final boolean allowSuperclasses) {
        return this.versionMapping;
    }
    
    @Override
    public JavaTypeMapping getDiscriminatorMapping(final boolean allowSuperclasses) {
        return this.discriminatorMapping;
    }
    
    @Override
    public JavaTypeMapping getMultitenancyMapping() {
        return this.tenantMapping;
    }
    
    public final void provideDatastoreIdMappings(final MappingConsumer consumer) {
        consumer.preConsumeMapping(this.highestMemberNumber + 1);
        if (this.getIdentityType() == IdentityType.DATASTORE) {
            consumer.consumeMapping(this.getDatastoreObjectIdMapping(), 2);
        }
    }
    
    public abstract void providePrimaryKeyMappings(final MappingConsumer p0);
    
    public final void provideNonPrimaryKeyMappings(final MappingConsumer consumer) {
        consumer.preConsumeMapping(this.highestMemberNumber + 1);
        final Set fieldNumbersSet = this.memberMappingsMap.keySet();
        for (final AbstractMemberMetaData mmd : fieldNumbersSet) {
            final JavaTypeMapping memberMapping = this.memberMappingsMap.get(mmd);
            if (memberMapping != null && !mmd.isPrimaryKey()) {
                consumer.consumeMapping(memberMapping, mmd);
            }
        }
    }
    
    public void provideMappingsForMembers(final MappingConsumer consumer, final AbstractMemberMetaData[] mmds, final boolean includeSecondaryTables) {
        consumer.preConsumeMapping(this.highestMemberNumber + 1);
        for (int i = 0; i < mmds.length; ++i) {
            final JavaTypeMapping fieldMapping = this.memberMappingsMap.get(mmds[i]);
            if (fieldMapping != null && !mmds[i].isPrimaryKey()) {
                consumer.consumeMapping(fieldMapping, mmds[i]);
            }
        }
    }
    
    public final void provideVersionMappings(final MappingConsumer consumer) {
        consumer.preConsumeMapping(this.highestMemberNumber + 1);
        if (this.getVersionMapping(false) != null) {
            consumer.consumeMapping(this.getVersionMapping(false), 1);
        }
    }
    
    public final void provideDiscriminatorMappings(final MappingConsumer consumer) {
        consumer.preConsumeMapping(this.highestMemberNumber + 1);
        if (this.getDiscriminatorMapping(false) != null) {
            consumer.consumeMapping(this.getDiscriminatorMapping(false), 3);
        }
    }
    
    public final void provideMultitenancyMapping(final MappingConsumer consumer) {
        consumer.preConsumeMapping(this.highestMemberNumber + 1);
        if (this.tenantMapping != null) {
            consumer.consumeMapping(this.tenantMapping, 7);
        }
    }
}
