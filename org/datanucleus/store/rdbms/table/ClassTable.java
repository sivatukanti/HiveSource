// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import java.sql.SQLException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.mapping.MappingConsumer;
import org.datanucleus.store.rdbms.mapping.java.LongMapping;
import org.datanucleus.store.rdbms.mapping.java.IntegerMapping;
import org.datanucleus.store.rdbms.mapping.java.IndexMapping;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.exceptions.NoSuchPersistentFieldException;
import org.datanucleus.metadata.InterfaceMetaData;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.store.rdbms.exceptions.ClassDefinitionException;
import org.datanucleus.metadata.ExtensionMetaData;
import java.util.StringTokenizer;
import java.util.Properties;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.metadata.ForeignKeyAction;
import org.datanucleus.util.ClassUtils;
import java.util.ArrayList;
import org.datanucleus.metadata.IndexMetaData;
import org.datanucleus.store.rdbms.key.Index;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedPCMapping;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.OrderMetaData;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.exceptions.DuplicateColumnException;
import org.datanucleus.store.rdbms.mapping.CorrespondentColumnsMapper;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.metadata.ColumnMetaDataContainer;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import java.util.List;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import org.datanucleus.metadata.JoinMetaData;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.metadata.FieldPersistenceModifier;
import java.util.Iterator;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.java.DiscriminatorMapping;
import org.datanucleus.store.rdbms.mapping.java.VersionTimestampMapping;
import java.sql.Timestamp;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.java.VersionLongMapping;
import org.datanucleus.metadata.VersionStrategy;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InheritanceStrategy;
import java.util.HashMap;
import java.util.HashSet;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import java.util.Set;
import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.util.MacroString;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Collection;
import org.datanucleus.metadata.ClassMetaData;

public class ClassTable extends AbstractClassTable implements DatastoreClass
{
    private final ClassMetaData cmd;
    private final Collection<AbstractClassMetaData> managedClassMetaData;
    private final Map<String, Collection<AbstractMemberMetaData>> callbacksAppliedForManagedClass;
    private ClassTable supertable;
    private Map<String, SecondaryTable> secondaryTables;
    private Map<AbstractMemberMetaData, JavaTypeMapping> externalFkMappings;
    private Map<AbstractMemberMetaData, JavaTypeMapping> externalFkDiscriminatorMappings;
    private Map<AbstractMemberMetaData, JavaTypeMapping> externalOrderMappings;
    private MacroString tableDef;
    private String createStatementDDL;
    Map<AbstractMemberMetaData, CandidateKey> candidateKeysByMapField;
    Set<Column> unmappedColumns;
    protected transient String managingClassCurrent;
    protected boolean runCallbacksAfterManageClass;
    
    public ClassTable(final DatastoreIdentifier tableName, final RDBMSStoreManager storeMgr, final ClassMetaData cmd) {
        super(tableName, storeMgr);
        this.managedClassMetaData = new HashSet<AbstractClassMetaData>();
        this.callbacksAppliedForManagedClass = new HashMap<String, Collection<AbstractMemberMetaData>>();
        this.candidateKeysByMapField = new HashMap<AbstractMemberMetaData, CandidateKey>();
        this.unmappedColumns = null;
        this.managingClassCurrent = null;
        this.runCallbacksAfterManageClass = false;
        this.cmd = cmd;
        if (cmd.getInheritanceMetaData().getStrategy() != InheritanceStrategy.NEW_TABLE && cmd.getInheritanceMetaData().getStrategy() != InheritanceStrategy.COMPLETE_TABLE) {
            throw new NucleusUserException(ClassTable.LOCALISER.msg("057003", cmd.getFullClassName(), cmd.getInheritanceMetaData().getStrategy().toString())).setFatal();
        }
        this.highestMemberNumber = cmd.getNoOfManagedMembers() + cmd.getNoOfInheritedManagedMembers();
        final String tableImpStr = cmd.getValueForExtension("ddl-imports");
        String tableDefStr = null;
        if (this.dba.getVendorID() != null) {
            tableDefStr = cmd.getValueForExtension("ddl-definition-" + this.dba.getVendorID());
        }
        if (tableDefStr == null) {
            tableDefStr = cmd.getValueForExtension("ddl-definition");
        }
        if (tableDefStr != null) {
            this.tableDef = new MacroString(cmd.getFullClassName(), tableImpStr, tableDefStr);
        }
    }
    
    @Override
    public void preInitialize(final ClassLoaderResolver clr) {
        this.assertIsPKUninitialized();
        if (this.cmd.getInheritanceMetaData().getStrategy() != InheritanceStrategy.COMPLETE_TABLE) {
            this.supertable = this.getSupertable(this.cmd, clr);
            if (this.supertable != null && !this.supertable.isInitialized() && !this.supertable.isPKInitialized()) {
                this.supertable.preInitialize(clr);
            }
        }
        if (!this.isPKInitialized()) {
            this.initializePK(clr);
        }
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        if (this.isInitialized()) {
            return;
        }
        if (this.supertable != null) {
            this.supertable.initialize(clr);
        }
        this.initializeForClass(this.cmd, clr);
        final MappingManager mapMgr = this.storeMgr.getMappingManager();
        this.versionMetaData = this.cmd.getVersionMetaDataForTable();
        if (this.versionMetaData != null && this.versionMetaData.getFieldName() == null) {
            if (this.versionMetaData.getVersionStrategy() == VersionStrategy.NONE) {
                this.versionMapping = new VersionLongMapping(this, mapMgr.getMapping(Long.class));
            }
            else if (this.versionMetaData.getVersionStrategy() == VersionStrategy.VERSION_NUMBER) {
                this.versionMapping = new VersionLongMapping(this, mapMgr.getMapping(Long.class));
            }
            else if (this.versionMetaData.getVersionStrategy() == VersionStrategy.DATE_TIME) {
                if (!this.dba.supportsOption("DateTimeStoresMillisecs")) {
                    throw new NucleusException("Class " + this.cmd.getFullClassName() + " is defined " + "to use date-time versioning, yet this datastore doesnt support storing " + "milliseconds in DATETIME/TIMESTAMP columns. Use version-number");
                }
                this.versionMapping = new VersionTimestampMapping(this, mapMgr.getMapping(Timestamp.class));
            }
        }
        final DiscriminatorMetaData dismd = this.cmd.getDiscriminatorMetaDataForTable();
        if (dismd != null) {
            this.discriminatorMetaData = dismd;
            if (this.storeMgr.getBooleanProperty("datanucleus.rdbms.discriminatorPerSubclassTable")) {
                this.discriminatorMapping = DiscriminatorMapping.createDiscriminatorMapping(this, dismd);
            }
            else {
                final ClassTable tableWithDiscrim = this.getTableWithDiscriminator();
                if (tableWithDiscrim == this) {
                    this.discriminatorMapping = DiscriminatorMapping.createDiscriminatorMapping(this, dismd);
                }
            }
        }
        if (this.storeMgr.getStringProperty("datanucleus.TenantID") != null) {
            if (!"true".equalsIgnoreCase(this.cmd.getValueForExtension("multitenancy-disable"))) {
                final ColumnMetaData colmd = new ColumnMetaData();
                if (this.cmd.hasExtension("multitenancy-column-name")) {
                    colmd.setName(this.cmd.getValueForExtension("multitenancy-column-name"));
                }
                if (this.cmd.hasExtension("multitenancy-jdbc-type")) {
                    colmd.setJdbcType(this.cmd.getValueForExtension("multitenancy-jdbc-type"));
                }
                if (this.cmd.hasExtension("multitenancy-column-length")) {
                    colmd.setLength(this.cmd.getValueForExtension("multitenancy-column-length"));
                }
                this.addMultitenancyMapping(colmd);
            }
        }
        if (this.secondaryTables != null) {
            final Set secondaryTableNames = this.secondaryTables.keySet();
            for (final String secondaryTableName : secondaryTableNames) {
                final SecondaryTable second = this.secondaryTables.get(secondaryTableName);
                if (!second.isInitialized()) {
                    second.initialize(clr);
                }
            }
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(ClassTable.LOCALISER.msg("057023", this));
        }
        this.storeMgr.registerTableInitialized(this);
        this.state = 2;
    }
    
    @Override
    public void postInitialize(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
        this.runCallBacks(clr);
        if (this.tableDef != null) {
            this.createStatementDDL = this.tableDef.substituteMacros(new MacroString.MacroHandler() {
                @Override
                public void onIdentifierMacro(final MacroString.IdentifierMacro im) {
                    ClassTable.this.storeMgr.resolveIdentifierMacro(im, clr);
                }
                
                @Override
                public void onParameterMacro(final MacroString.ParameterMacro pm) {
                    throw new NucleusUserException(AbstractTable.LOCALISER.msg("057033", ClassTable.this.cmd.getFullClassName(), pm));
                }
            }, clr);
        }
    }
    
    public void manageClass(final AbstractClassMetaData theCmd, final ClassLoaderResolver clr) {
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(ClassTable.LOCALISER.msg("057024", this.toString(), theCmd.getFullClassName(), theCmd.getInheritanceMetaData().getStrategy().toString()));
        }
        this.managingClassCurrent = theCmd.getFullClassName();
        this.managedClassMetaData.add(theCmd);
        this.manageMembers(theCmd, clr, theCmd.getManagedMembers());
        this.manageMembers(theCmd, clr, theCmd.getOverriddenMembers());
        this.manageUnmappedColumns(theCmd, clr);
        this.managingClassCurrent = null;
        if (this.runCallbacksAfterManageClass) {
            this.runCallBacks(clr);
            this.runCallbacksAfterManageClass = false;
        }
    }
    
    @Override
    public String[] getManagedClasses() {
        final String[] classNames = new String[this.managedClassMetaData.size()];
        final Iterator<AbstractClassMetaData> iter = this.managedClassMetaData.iterator();
        int i = 0;
        while (iter.hasNext()) {
            classNames[i++] = iter.next().getFullClassName();
        }
        return classNames;
    }
    
    private void manageMembers(final AbstractClassMetaData theCmd, final ClassLoaderResolver clr, final AbstractMemberMetaData[] mmds) {
        for (int fieldNumber = 0; fieldNumber < mmds.length; ++fieldNumber) {
            final AbstractMemberMetaData mmd = mmds[fieldNumber];
            if (!mmd.isPrimaryKey()) {
                if (this.managesMember(mmd.getFullFieldName())) {
                    if (!mmd.getClassName(true).equals(theCmd.getFullClassName())) {
                        final JavaTypeMapping fieldMapping = this.getMappingForMemberName(mmd.getFullFieldName());
                        final ColumnMetaData[] colmds = mmd.getColumnMetaData();
                        if (colmds != null && colmds.length > 0) {
                            int colnum = 0;
                            final IdentifierFactory idFactory = this.getStoreManager().getIdentifierFactory();
                            for (int i = 0; i < fieldMapping.getNumberOfDatastoreMappings(); ++i) {
                                final Column col = fieldMapping.getDatastoreMapping(i).getColumn();
                                col.setIdentifier(idFactory.newColumnIdentifier(colmds[colnum].getName()));
                                col.setColumnMetaData(colmds[colnum]);
                                if (++colnum == colmds.length) {
                                    break;
                                }
                            }
                            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                                final StringBuffer columnsStr = new StringBuffer();
                                for (int j = 0; j < fieldMapping.getNumberOfDatastoreMappings(); ++j) {
                                    if (j > 0) {
                                        columnsStr.append(",");
                                    }
                                    columnsStr.append(fieldMapping.getDatastoreMapping(j).getColumn());
                                }
                                if (fieldMapping.getNumberOfDatastoreMappings() == 0) {
                                    columnsStr.append("[none]");
                                }
                                final StringBuffer datastoreMappingTypes = new StringBuffer();
                                for (int k = 0; k < fieldMapping.getNumberOfDatastoreMappings(); ++k) {
                                    if (k > 0) {
                                        datastoreMappingTypes.append(',');
                                    }
                                    datastoreMappingTypes.append(fieldMapping.getDatastoreMapping(k).getClass().getName());
                                }
                                NucleusLogger.DATASTORE_SCHEMA.debug(ClassTable.LOCALISER.msg("057010", mmd.getFullFieldName(), columnsStr.toString(), fieldMapping.getClass().getName(), datastoreMappingTypes.toString()));
                            }
                        }
                    }
                }
                else {
                    if (mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
                        boolean isPrimary = true;
                        if (mmd.getTable() != null && mmd.getJoinMetaData() == null) {
                            isPrimary = false;
                        }
                        if (isPrimary) {
                            final JavaTypeMapping fieldMapping2 = this.storeMgr.getMappingManager().getMapping(this, mmd, clr, 2);
                            if (theCmd != this.cmd && theCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE && fieldMapping2.getNumberOfDatastoreMappings() > 0) {
                                for (int numCols = fieldMapping2.getNumberOfDatastoreMappings(), colNum = 0; colNum < numCols; ++colNum) {
                                    final Column col2 = fieldMapping2.getDatastoreMapping(colNum).getColumn();
                                    if (col2.getDefaultValue() == null && !col2.isNullable()) {
                                        NucleusLogger.DATASTORE_SCHEMA.debug("Member " + mmd.getFullFieldName() + " uses superclass-table yet the field is not marked as nullable " + " nor does it have a default value, so setting the column as nullable");
                                        col2.setNullable();
                                    }
                                }
                            }
                            this.addMemberMapping(fieldMapping2);
                        }
                        else {
                            if (this.secondaryTables == null) {
                                this.secondaryTables = new HashMap<String, SecondaryTable>();
                            }
                            SecondaryTable secTable = this.secondaryTables.get(mmd.getTable());
                            if (secTable == null) {
                                final JoinMetaData[] joinmds = theCmd.getJoinMetaData();
                                JoinMetaData joinmd = null;
                                if (joinmds != null) {
                                    for (int l = 0; l < joinmds.length; ++l) {
                                        if (joinmds[l].getTable().equalsIgnoreCase(mmd.getTable()) && (joinmds[l].getCatalog() == null || (joinmds[l].getCatalog() != null && joinmds[l].getCatalog().equalsIgnoreCase(mmd.getCatalog()))) && (joinmds[l].getSchema() == null || (joinmds[l].getSchema() != null && joinmds[l].getSchema().equalsIgnoreCase(mmd.getSchema())))) {
                                            joinmd = joinmds[l];
                                            break;
                                        }
                                    }
                                }
                                final DatastoreIdentifier secTableIdentifier = this.storeMgr.getIdentifierFactory().newTableIdentifier(mmd.getTable());
                                String catalogName = mmd.getCatalog();
                                if (catalogName == null) {
                                    catalogName = this.getCatalogName();
                                }
                                String schemaName = mmd.getSchema();
                                if (schemaName == null) {
                                    schemaName = this.getSchemaName();
                                }
                                secTableIdentifier.setCatalogName(catalogName);
                                secTableIdentifier.setSchemaName(schemaName);
                                secTable = new SecondaryTable(secTableIdentifier, this.storeMgr, this, joinmd, clr);
                                secTable.preInitialize(clr);
                                secTable.initialize(clr);
                                secTable.postInitialize(clr);
                                this.secondaryTables.put(mmd.getTable(), secTable);
                            }
                            secTable.addMemberMapping(this.storeMgr.getMappingManager().getMapping(secTable, mmd, clr, 2));
                        }
                    }
                    else if (mmd.getPersistenceModifier() != FieldPersistenceModifier.TRANSACTIONAL) {
                        throw new NucleusException(ClassTable.LOCALISER.msg("057006", mmd.getName())).setFatal();
                    }
                    boolean needsFKToContainerOwner = false;
                    final RelationType relationType = mmd.getRelationType(clr);
                    if (relationType == RelationType.ONE_TO_MANY_BI) {
                        final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(clr);
                        if (mmd.getJoinMetaData() == null && relatedMmds[0].getJoinMetaData() == null) {
                            needsFKToContainerOwner = true;
                        }
                    }
                    else if (relationType == RelationType.ONE_TO_MANY_UNI && mmd.getJoinMetaData() == null) {
                        needsFKToContainerOwner = true;
                    }
                    if (needsFKToContainerOwner) {
                        if ((mmd.getCollection() != null && !SCOUtils.collectionHasSerialisedElements(mmd)) || (mmd.getArray() != null && !SCOUtils.arrayIsStoredInSingleColumn(mmd, this.storeMgr.getMetaDataManager()))) {
                            AbstractClassMetaData elementCmd = null;
                            if (mmd.hasCollection()) {
                                elementCmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(mmd.getCollection().getElementType(), clr);
                            }
                            else {
                                elementCmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(mmd.getType().getComponentType(), clr);
                            }
                            if (elementCmd == null) {
                                final String[] implClassNames = this.storeMgr.getMetaDataManager().getClassesImplementingInterface(mmd.getCollection().getElementType(), clr);
                                if (implClassNames != null && implClassNames.length > 0) {
                                    final AbstractClassMetaData[] elementCmds = new AbstractClassMetaData[implClassNames.length];
                                    for (int j = 0; j < implClassNames.length; ++j) {
                                        elementCmds[j] = this.storeMgr.getMetaDataManager().getMetaDataForClass(implClassNames[j], clr);
                                    }
                                    for (int j = 0; j < elementCmds.length; ++j) {
                                        this.storeMgr.addSchemaCallback(elementCmds[j].getFullClassName(), mmd);
                                        final DatastoreClass dc = this.storeMgr.getDatastoreClass(elementCmds[j].getFullClassName(), clr);
                                        if (dc == null) {
                                            throw new NucleusException("Unable to add foreign-key to " + elementCmds[j].getFullClassName() + " to " + this + " since element has no table!");
                                        }
                                        final ClassTable ct = (ClassTable)dc;
                                        if (ct.isInitialized()) {
                                            ct.runCallBacks(clr);
                                        }
                                    }
                                }
                                else if (mmd.hasCollection()) {
                                    NucleusLogger.METADATA.warn(ClassTable.LOCALISER.msg("057016", theCmd.getFullClassName(), mmd.getCollection().getElementType()));
                                }
                                else {
                                    NucleusLogger.METADATA.warn(ClassTable.LOCALISER.msg("057014", theCmd.getFullClassName(), mmd.getType().getComponentType().getName()));
                                }
                            }
                            else {
                                AbstractClassMetaData[] elementCmds2 = null;
                                if (elementCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                                    elementCmds2 = this.storeMgr.getClassesManagingTableForClass(elementCmd, clr);
                                }
                                else {
                                    elementCmds2 = new ClassMetaData[] { (ClassMetaData)elementCmd };
                                }
                                for (int i = 0; i < elementCmds2.length; ++i) {
                                    this.storeMgr.addSchemaCallback(elementCmds2[i].getFullClassName(), mmd);
                                    final DatastoreClass dc2 = this.storeMgr.getDatastoreClass(elementCmds2[i].getFullClassName(), clr);
                                    if (dc2 == null) {
                                        throw new NucleusException("Unable to add foreign-key to " + elementCmds2[i].getFullClassName() + " to " + this + " since element has no table!");
                                    }
                                    final ClassTable ct2 = (ClassTable)dc2;
                                    if (ct2.isInitialized()) {
                                        ct2.runCallBacks(clr);
                                    }
                                }
                            }
                        }
                        else if (mmd.getMap() != null && !SCOUtils.mapHasSerialisedKeysAndValues(mmd)) {
                            if (mmd.getKeyMetaData() != null && mmd.getKeyMetaData().getMappedBy() != null) {
                                final AbstractClassMetaData valueCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(mmd.getMap().getValueType(), clr);
                                if (valueCmd == null) {
                                    NucleusLogger.METADATA.warn(ClassTable.LOCALISER.msg("057018", theCmd.getFullClassName(), mmd.getMap().getValueType()));
                                }
                                else {
                                    AbstractClassMetaData[] valueCmds = null;
                                    if (valueCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                                        valueCmds = this.storeMgr.getClassesManagingTableForClass(valueCmd, clr);
                                    }
                                    else {
                                        valueCmds = new ClassMetaData[] { (ClassMetaData)valueCmd };
                                    }
                                    for (int i = 0; i < valueCmds.length; ++i) {
                                        this.storeMgr.addSchemaCallback(valueCmds[i].getFullClassName(), mmd);
                                        final DatastoreClass dc2 = this.storeMgr.getDatastoreClass(valueCmds[i].getFullClassName(), clr);
                                        final ClassTable ct2 = (ClassTable)dc2;
                                        if (ct2.isInitialized()) {
                                            ct2.runCallBacks(clr);
                                        }
                                    }
                                }
                            }
                            else if (mmd.getValueMetaData() != null && mmd.getValueMetaData().getMappedBy() != null) {
                                final AbstractClassMetaData keyCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(mmd.getMap().getKeyType(), clr);
                                if (keyCmd == null) {
                                    NucleusLogger.METADATA.warn(ClassTable.LOCALISER.msg("057019", theCmd.getFullClassName(), mmd.getMap().getKeyType()));
                                }
                                else {
                                    AbstractClassMetaData[] keyCmds = null;
                                    if (keyCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                                        keyCmds = this.storeMgr.getClassesManagingTableForClass(keyCmd, clr);
                                    }
                                    else {
                                        keyCmds = new ClassMetaData[] { (ClassMetaData)keyCmd };
                                    }
                                    for (int i = 0; i < keyCmds.length; ++i) {
                                        this.storeMgr.addSchemaCallback(keyCmds[i].getFullClassName(), mmd);
                                        final DatastoreClass dc2 = this.storeMgr.getDatastoreClass(keyCmds[i].getFullClassName(), clr);
                                        final ClassTable ct2 = (ClassTable)dc2;
                                        if (ct2.isInitialized()) {
                                            ct2.runCallBacks(clr);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void manageUnmappedColumns(final AbstractClassMetaData theCmd, final ClassLoaderResolver clr) {
        final List cols = theCmd.getUnmappedColumns();
        if (cols != null && cols.size() > 0) {
            for (final ColumnMetaData colmd : cols) {
                if (colmd.getJdbcType().equals("VARCHAR") && colmd.getLength() == null) {
                    colmd.setLength(this.storeMgr.getIntProperty("datanucleus.rdbms.stringDefaultLength"));
                }
                final IdentifierFactory idFactory = this.getStoreManager().getIdentifierFactory();
                final DatastoreIdentifier colIdentifier = idFactory.newIdentifier(IdentifierType.COLUMN, colmd.getName());
                final Column col = this.addColumn(null, colIdentifier, null, colmd);
                final SQLTypeInfo sqlTypeInfo = this.storeMgr.getSQLTypeInfoForJDBCType(JDBCUtils.getJDBCTypeForName(colmd.getJdbcType()));
                col.setTypeInfo(sqlTypeInfo);
                if (this.unmappedColumns == null) {
                    this.unmappedColumns = new HashSet<Column>();
                }
                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(ClassTable.LOCALISER.msg("057011", col.toString(), colmd.getJdbcType()));
                }
                this.unmappedColumns.add(col);
            }
        }
    }
    
    @Override
    public boolean managesClass(final String className) {
        if (className == null) {
            return false;
        }
        for (final AbstractClassMetaData managedCmd : this.managedClassMetaData) {
            if (managedCmd.getFullClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void initializePK(final ClassLoaderResolver clr) {
        this.assertIsPKUninitialized();
        final AbstractMemberMetaData[] membersToAdd = new AbstractMemberMetaData[this.cmd.getNoOfPrimaryKeyMembers()];
        int pkFieldNum = 0;
        int fieldCount = this.cmd.getNoOfManagedMembers();
        boolean hasPrimaryKeyInThisClass = false;
        if (this.cmd.getNoOfPrimaryKeyMembers() > 0) {
            this.pkMappings = new JavaTypeMapping[this.cmd.getNoOfPrimaryKeyMembers()];
            if (this.cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
                final AbstractClassMetaData baseCmd = this.cmd.getBaseAbstractClassMetaData();
                fieldCount = baseCmd.getNoOfManagedMembers();
                for (int relFieldNum = 0; relFieldNum < fieldCount; ++relFieldNum) {
                    final AbstractMemberMetaData mmd = baseCmd.getMetaDataForManagedMemberAtRelativePosition(relFieldNum);
                    if (mmd.isPrimaryKey()) {
                        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
                            membersToAdd[pkFieldNum++] = mmd;
                            hasPrimaryKeyInThisClass = true;
                        }
                        else if (mmd.getPersistenceModifier() != FieldPersistenceModifier.TRANSACTIONAL) {
                            throw new NucleusException(ClassTable.LOCALISER.msg("057006", mmd.getName())).setFatal();
                        }
                        if (mmd.getValueStrategy() == IdentityStrategy.IDENTITY && !this.dba.supportsOption("IdentityColumns")) {
                            throw new NucleusException(ClassTable.LOCALISER.msg("057020", this.cmd.getFullClassName(), mmd.getName())).setFatal();
                        }
                    }
                }
            }
            else {
                for (int relFieldNum2 = 0; relFieldNum2 < fieldCount; ++relFieldNum2) {
                    final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtRelativePosition(relFieldNum2);
                    if (fmd.isPrimaryKey()) {
                        if (fmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
                            membersToAdd[pkFieldNum++] = fmd;
                            hasPrimaryKeyInThisClass = true;
                        }
                        else if (fmd.getPersistenceModifier() != FieldPersistenceModifier.TRANSACTIONAL) {
                            throw new NucleusException(ClassTable.LOCALISER.msg("057006", fmd.getName())).setFatal();
                        }
                        if (fmd.getValueStrategy() == IdentityStrategy.IDENTITY && !this.dba.supportsOption("IdentityColumns")) {
                            throw new NucleusException(ClassTable.LOCALISER.msg("057020", this.cmd.getFullClassName(), fmd.getName())).setFatal();
                        }
                    }
                }
            }
        }
        if (!hasPrimaryKeyInThisClass) {
            if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
                DatastoreClass superTable = this.storeMgr.getDatastoreClass(this.cmd.getPersistenceCapableSuperclass(), clr);
                if (this.isPKInitialized()) {
                    return;
                }
                if (superTable == null && this.cmd.getPersistenceCapableSuperclass() != null) {
                    AbstractClassMetaData supercmd = this.cmd.getSuperAbstractClassMetaData();
                    while (supercmd.getPersistenceCapableSuperclass() != null) {
                        superTable = this.storeMgr.getDatastoreClass(supercmd.getPersistenceCapableSuperclass(), clr);
                        if (this.isPKInitialized()) {
                            return;
                        }
                        if (superTable != null) {
                            break;
                        }
                        supercmd = supercmd.getSuperAbstractClassMetaData();
                        if (supercmd == null) {
                            break;
                        }
                    }
                }
                if (superTable != null) {
                    ColumnMetaDataContainer colContainer = null;
                    if (this.cmd.getInheritanceMetaData() != null) {
                        colContainer = this.cmd.getInheritanceMetaData().getJoinMetaData();
                    }
                    if (colContainer == null) {
                        colContainer = this.cmd.getPrimaryKeyMetaData();
                    }
                    this.addApplicationIdUsingClassTableId(colContainer, superTable, clr, this.cmd);
                }
                else {
                    final AbstractClassMetaData pkCmd = this.getClassWithPrimaryKeyForClass(this.cmd.getSuperAbstractClassMetaData(), clr);
                    if (pkCmd != null) {
                        this.pkMappings = new JavaTypeMapping[pkCmd.getNoOfPrimaryKeyMembers()];
                        pkFieldNum = 0;
                        fieldCount = pkCmd.getNoOfInheritedManagedMembers() + pkCmd.getNoOfManagedMembers();
                        for (int absFieldNum = 0; absFieldNum < fieldCount; ++absFieldNum) {
                            AbstractMemberMetaData fmd2 = pkCmd.getMetaDataForManagedMemberAtAbsolutePosition(absFieldNum);
                            if (fmd2.isPrimaryKey()) {
                                final AbstractMemberMetaData overriddenFmd = this.cmd.getOverriddenMember(fmd2.getName());
                                if (overriddenFmd != null) {
                                    fmd2 = overriddenFmd;
                                }
                                if (fmd2.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
                                    membersToAdd[pkFieldNum++] = fmd2;
                                }
                                else if (fmd2.getPersistenceModifier() != FieldPersistenceModifier.TRANSACTIONAL) {
                                    throw new NucleusException(ClassTable.LOCALISER.msg("057006", fmd2.getName())).setFatal();
                                }
                            }
                        }
                    }
                }
            }
            else if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
                ColumnMetaData colmd = null;
                if (this.cmd.getIdentityMetaData() != null && this.cmd.getIdentityMetaData().getColumnMetaData() != null) {
                    colmd = this.cmd.getIdentityMetaData().getColumnMetaData();
                }
                if (colmd == null && this.cmd.getPrimaryKeyMetaData() != null && this.cmd.getPrimaryKeyMetaData().getColumnMetaData() != null && this.cmd.getPrimaryKeyMetaData().getColumnMetaData().length > 0) {
                    colmd = this.cmd.getPrimaryKeyMetaData().getColumnMetaData()[0];
                }
                this.addDatastoreId(colmd, null, this.cmd);
            }
            else if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {}
        }
        for (int i = 0; i < membersToAdd.length; ++i) {
            if (membersToAdd[i] != null) {
                try {
                    final DatastoreClass datastoreClass = this.getStoreManager().getDatastoreClass(membersToAdd[i].getType().getName(), clr);
                    if (datastoreClass.getIdMapping() == null) {
                        throw new NucleusException("Unsupported relationship with field " + membersToAdd[i].getFullFieldName()).setFatal();
                    }
                }
                catch (NoTableManagedException ex) {}
                final JavaTypeMapping fieldMapping = this.storeMgr.getMappingManager().getMapping(this, membersToAdd[i], clr, 2);
                this.addMemberMapping(fieldMapping);
                this.pkMappings[i] = fieldMapping;
            }
        }
        this.initializeIDMapping();
        this.state = 1;
    }
    
    private AbstractClassMetaData getClassWithPrimaryKeyForClass(final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        if (cmd == null) {
            return null;
        }
        if (cmd.getSuperAbstractClassMetaData() == null) {
            return cmd;
        }
        if (cmd.getNoOfPrimaryKeyMembers() > 0 && cmd.getSuperAbstractClassMetaData().getNoOfPrimaryKeyMembers() == 0) {
            return cmd;
        }
        return this.getClassWithPrimaryKeyForClass(cmd.getSuperAbstractClassMetaData(), clr);
    }
    
    private void initializeForClass(final AbstractClassMetaData theCmd, final ClassLoaderResolver clr) {
        final String columnOrdering = this.storeMgr.getStringProperty("datanucleus.rdbms.tableColumnOrder");
        if (columnOrdering.equalsIgnoreCase("superclass-first")) {
            final AbstractClassMetaData parentCmd = theCmd.getSuperAbstractClassMetaData();
            if (parentCmd != null) {
                if (this.cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
                    this.initializeForClass(parentCmd, clr);
                }
                else if (parentCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                    this.initializeForClass(parentCmd, clr);
                }
            }
            this.manageClass(theCmd, clr);
        }
        else {
            this.manageClass(theCmd, clr);
            final AbstractClassMetaData parentCmd = theCmd.getSuperAbstractClassMetaData();
            if (parentCmd != null) {
                if (this.cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
                    this.initializeForClass(parentCmd, clr);
                }
                else if (parentCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                    this.initializeForClass(parentCmd, clr);
                }
            }
        }
    }
    
    private void runCallBacks(final ClassLoaderResolver clr) {
        for (final AbstractClassMetaData managedCmd : this.managedClassMetaData) {
            if (this.managingClassCurrent != null && this.managingClassCurrent.equals(managedCmd.getFullClassName())) {
                this.runCallbacksAfterManageClass = true;
                break;
            }
            Collection processedCallbacks = this.callbacksAppliedForManagedClass.get(managedCmd.getFullClassName());
            final Collection c = this.storeMgr.getSchemaCallbacks().get(managedCmd.getFullClassName());
            if (c == null) {
                continue;
            }
            if (processedCallbacks == null) {
                processedCallbacks = new HashSet();
                this.callbacksAppliedForManagedClass.put(managedCmd.getFullClassName(), processedCallbacks);
            }
            for (final AbstractMemberMetaData callbackMmd : c) {
                if (processedCallbacks.contains(callbackMmd)) {
                    continue;
                }
                processedCallbacks.add(callbackMmd);
                if (callbackMmd.getJoinMetaData() != null) {
                    continue;
                }
                final AbstractMemberMetaData ownerFmd = callbackMmd;
                if (ownerFmd.getMappedBy() != null) {
                    final AbstractMemberMetaData fmd = managedCmd.getMetaDataForMember(ownerFmd.getMappedBy());
                    if (fmd == null) {
                        throw new NucleusUserException(ClassTable.LOCALISER.msg("057036", ownerFmd.getMappedBy(), managedCmd.getFullClassName(), ownerFmd.getFullFieldName()));
                    }
                    if (ownerFmd.getMap() != null && this.storeMgr.getBooleanProperty("datanucleus.rdbms.uniqueConstraints.mapInverse")) {
                        this.initializeFKMapUniqueConstraints(ownerFmd);
                    }
                    boolean duplicate = false;
                    JavaTypeMapping fkDiscrimMapping = null;
                    JavaTypeMapping orderMapping = null;
                    if (ownerFmd.hasExtension("relation-discriminator-column")) {
                        String colName = ownerFmd.getValueForExtension("relation-discriminator-column");
                        if (colName == null) {
                            colName = "RELATION_DISCRIM";
                        }
                        final Set fkDiscrimEntries = this.getExternalFkDiscriminatorMappings().entrySet();
                        for (final Map.Entry entry : fkDiscrimEntries) {
                            final JavaTypeMapping discrimMapping = entry.getValue();
                            final String discrimColName = discrimMapping.getDatastoreMapping(0).getColumn().getColumnMetaData().getName();
                            if (discrimColName.equalsIgnoreCase(colName)) {
                                duplicate = true;
                                fkDiscrimMapping = discrimMapping;
                                orderMapping = this.getExternalOrderMappings().get(entry.getKey());
                                break;
                            }
                        }
                        if (!duplicate) {
                            final ColumnMetaData colmd = new ColumnMetaData();
                            colmd.setName(colName);
                            colmd.setAllowsNull(Boolean.TRUE);
                            fkDiscrimMapping = this.storeMgr.getMappingManager().getMapping(String.class);
                            fkDiscrimMapping.setTable(this);
                            ColumnCreator.createIndexColumn(fkDiscrimMapping, this.storeMgr, clr, this, colmd, false);
                        }
                        if (fkDiscrimMapping != null) {
                            this.getExternalFkDiscriminatorMappings().put(ownerFmd, fkDiscrimMapping);
                        }
                    }
                    this.addOrderMapping(ownerFmd, orderMapping, clr);
                }
                else {
                    String ownerClassName = ownerFmd.getAbstractClassMetaData().getFullClassName();
                    JavaTypeMapping fkMapping = new PersistableMapping();
                    fkMapping.setTable(this);
                    fkMapping.initialize(this.storeMgr, ownerClassName);
                    JavaTypeMapping fkDiscrimMapping = null;
                    JavaTypeMapping orderMapping = null;
                    boolean duplicate2 = false;
                    try {
                        DatastoreClass ownerTbl = this.storeMgr.getDatastoreClass(ownerClassName, clr);
                        if (ownerTbl == null) {
                            final AbstractClassMetaData[] ownerParentCmds = this.storeMgr.getClassesManagingTableForClass(ownerFmd.getAbstractClassMetaData(), clr);
                            if (ownerParentCmds.length > 1) {
                                throw new NucleusUserException("Relation (" + ownerFmd.getFullFieldName() + ") with multiple related tables (using subclass-table). Not supported");
                            }
                            ownerClassName = ownerParentCmds[0].getFullClassName();
                            ownerTbl = this.storeMgr.getDatastoreClass(ownerClassName, clr);
                        }
                        final JavaTypeMapping ownerIdMapping = ownerTbl.getIdMapping();
                        ColumnMetaDataContainer colmdContainer = null;
                        if (ownerFmd.hasCollection() || ownerFmd.hasArray()) {
                            colmdContainer = ownerFmd.getElementMetaData();
                        }
                        else if (ownerFmd.hasMap() && ownerFmd.getKeyMetaData() != null && ownerFmd.getKeyMetaData().getMappedBy() != null) {
                            colmdContainer = ownerFmd.getValueMetaData();
                        }
                        else if (ownerFmd.hasMap() && ownerFmd.getValueMetaData() != null && ownerFmd.getValueMetaData().getMappedBy() != null) {
                            colmdContainer = ownerFmd.getKeyMetaData();
                        }
                        final CorrespondentColumnsMapper correspondentColumnsMapping = new CorrespondentColumnsMapper(colmdContainer, ownerIdMapping, true);
                        for (int countIdFields = ownerIdMapping.getNumberOfDatastoreMappings(), i = 0; i < countIdFields; ++i) {
                            final DatastoreMapping refDatastoreMapping = ownerIdMapping.getDatastoreMapping(i);
                            final JavaTypeMapping mapping = this.storeMgr.getMappingManager().getMapping(refDatastoreMapping.getJavaTypeMapping().getJavaType());
                            final ColumnMetaData colmd2 = correspondentColumnsMapping.getColumnMetaDataByIdentifier(refDatastoreMapping.getColumn().getIdentifier());
                            if (colmd2 == null) {
                                throw new NucleusUserException(ClassTable.LOCALISER.msg("057035", refDatastoreMapping.getColumn().getIdentifier(), this.toString())).setFatal();
                            }
                            DatastoreIdentifier identifier = null;
                            final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
                            if (colmd2.getName() == null || colmd2.getName().length() < 1) {
                                identifier = idFactory.newForeignKeyFieldIdentifier(ownerFmd, null, refDatastoreMapping.getColumn().getIdentifier(), this.storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(mapping.getJavaType()), 1);
                            }
                            else {
                                identifier = idFactory.newColumnIdentifier(colmd2.getName());
                            }
                            final Column refColumn = this.addColumn(mapping.getJavaType().getName(), identifier, mapping, colmd2);
                            refDatastoreMapping.getColumn().copyConfigurationTo(refColumn);
                            if (colmd2.getAllowsNull() == null || (colmd2.getAllowsNull() != null && colmd2.isAllowsNull())) {
                                refColumn.setNullable();
                            }
                            fkMapping.addDatastoreMapping(this.getStoreManager().getMappingManager().createDatastoreMapping(mapping, refColumn, refDatastoreMapping.getJavaTypeMapping().getJavaType().getName()));
                            ((PersistableMapping)fkMapping).addJavaTypeMapping(mapping);
                        }
                    }
                    catch (DuplicateColumnException dce) {
                        if (!ownerFmd.hasExtension("relation-discriminator-column")) {
                            throw dce;
                        }
                        final Iterator fkIter = this.getExternalFkMappings().entrySet().iterator();
                        fkMapping = null;
                        while (fkIter.hasNext()) {
                            final Map.Entry entry = fkIter.next();
                            final JavaTypeMapping existingFkMapping = entry.getValue();
                            for (int j = 0; j < existingFkMapping.getNumberOfDatastoreMappings(); ++j) {
                                if (existingFkMapping.getDatastoreMapping(j).getColumn().getIdentifier().toString().equals(dce.getConflictingColumn().getIdentifier().toString())) {
                                    fkMapping = existingFkMapping;
                                    fkDiscrimMapping = this.externalFkDiscriminatorMappings.get(entry.getKey());
                                    orderMapping = this.getExternalOrderMappings().get(entry.getKey());
                                    break;
                                }
                            }
                        }
                        if (fkMapping == null) {
                            throw dce;
                        }
                        duplicate2 = true;
                    }
                    if (!duplicate2 && ownerFmd.hasExtension("relation-discriminator-column")) {
                        String colName2 = ownerFmd.getValueForExtension("relation-discriminator-column");
                        if (colName2 == null) {
                            colName2 = "RELATION_DISCRIM";
                        }
                        final ColumnMetaData colmd3 = new ColumnMetaData();
                        colmd3.setName(colName2);
                        colmd3.setAllowsNull(Boolean.TRUE);
                        fkDiscrimMapping = this.storeMgr.getMappingManager().getMapping(String.class);
                        fkDiscrimMapping.setTable(this);
                        ColumnCreator.createIndexColumn(fkDiscrimMapping, this.storeMgr, clr, this, colmd3, false);
                    }
                    this.getExternalFkMappings().put(ownerFmd, fkMapping);
                    if (fkDiscrimMapping != null) {
                        this.getExternalFkDiscriminatorMappings().put(ownerFmd, fkDiscrimMapping);
                    }
                    this.addOrderMapping(ownerFmd, orderMapping, clr);
                }
            }
        }
    }
    
    private JavaTypeMapping addOrderMapping(final AbstractMemberMetaData fmd, JavaTypeMapping orderMapping, final ClassLoaderResolver clr) {
        boolean needsOrderMapping = false;
        final OrderMetaData omd = fmd.getOrderMetaData();
        if (fmd.hasArray()) {
            needsOrderMapping = true;
        }
        else if (List.class.isAssignableFrom(fmd.getType())) {
            needsOrderMapping = true;
            if (omd != null && !omd.isIndexedList()) {
                needsOrderMapping = false;
            }
        }
        else if (Collection.class.isAssignableFrom(fmd.getType()) && omd != null && omd.isIndexedList()) {
            needsOrderMapping = true;
            if (omd.getMappedBy() != null) {
                orderMapping = this.getMemberMapping(omd.getMappedBy());
            }
        }
        if (needsOrderMapping) {
            this.state = 0;
            if (orderMapping == null) {
                orderMapping = this.addOrderColumn(fmd, clr);
            }
            this.getExternalOrderMappings().put(fmd, orderMapping);
            this.state = 2;
        }
        return orderMapping;
    }
    
    @Override
    public String getType() {
        return this.cmd.getFullClassName();
    }
    
    @Override
    public IdentityType getIdentityType() {
        return this.cmd.getIdentityType();
    }
    
    @Override
    public final VersionMetaData getVersionMetaData() {
        return this.versionMetaData;
    }
    
    @Override
    public final DiscriminatorMetaData getDiscriminatorMetaData() {
        return this.discriminatorMetaData;
    }
    
    public final ClassTable getTableWithDiscriminator() {
        if (this.supertable != null) {
            final ClassTable tbl = this.supertable.getTableWithDiscriminator();
            if (tbl != null) {
                return tbl;
            }
        }
        if (this.discriminatorMetaData != null) {
            return this;
        }
        if (this.cmd.getInheritanceMetaData() != null && this.cmd.getInheritanceMetaData().getDiscriminatorMetaData() != null) {
            return this;
        }
        return null;
    }
    
    @Override
    public boolean isObjectIdDatastoreAttributed() {
        final boolean attributed = this.storeMgr.isStrategyDatastoreAttributed(this.cmd, -1);
        if (attributed) {
            return true;
        }
        for (int i = 0; i < this.columns.size(); ++i) {
            final Column col = this.columns.get(i);
            if (col.isPrimaryKey() && col.isIdentity()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isBaseDatastoreClass() {
        return this.supertable == null;
    }
    
    @Override
    public DatastoreClass getBaseDatastoreClass() {
        if (this.supertable != null) {
            return this.supertable.getBaseDatastoreClass();
        }
        return this;
    }
    
    @Override
    public DatastoreClass getSuperDatastoreClass() {
        this.assertIsInitialized();
        return this.supertable;
    }
    
    @Override
    public boolean isSuperDatastoreClass(final DatastoreClass table) {
        return this == table || (this.supertable != null && (table == this.supertable || this.supertable.isSuperDatastoreClass(table)));
    }
    
    @Override
    public Collection getSecondaryDatastoreClasses() {
        return (this.secondaryTables != null) ? this.secondaryTables.values() : null;
    }
    
    @Override
    public JavaTypeMapping getVersionMapping(final boolean allowSuperclasses) {
        if (this.versionMapping != null) {
            return this.versionMapping;
        }
        if (allowSuperclasses && this.supertable != null) {
            return this.supertable.getVersionMapping(allowSuperclasses);
        }
        return null;
    }
    
    @Override
    public JavaTypeMapping getDiscriminatorMapping(final boolean allowSuperclasses) {
        if (this.discriminatorMapping != null) {
            return this.discriminatorMapping;
        }
        if (allowSuperclasses && this.supertable != null) {
            return this.supertable.getDiscriminatorMapping(allowSuperclasses);
        }
        return null;
    }
    
    public ClassTable getTableManagingMapping(final JavaTypeMapping mapping) {
        if (this.managesMapping(mapping)) {
            return this;
        }
        if (this.supertable != null) {
            return this.supertable.getTableManagingMapping(mapping);
        }
        return null;
    }
    
    private ClassTable getSupertable(final AbstractClassMetaData theCmd, final ClassLoaderResolver clr) {
        if (this.cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
            return null;
        }
        final AbstractClassMetaData superCmd = theCmd.getSuperAbstractClassMetaData();
        if (superCmd == null) {
            return null;
        }
        if (superCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.NEW_TABLE) {
            return (ClassTable)this.storeMgr.getDatastoreClass(superCmd.getFullClassName(), clr);
        }
        if (superCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
            return this.getSupertable(superCmd, clr);
        }
        return this.getSupertable(superCmd, clr);
    }
    
    @Override
    public DatastoreClass getBaseDatastoreClassWithMember(final AbstractMemberMetaData mmd) {
        if (mmd == null) {
            return null;
        }
        if (mmd.isPrimaryKey() && this.getSuperDatastoreClass() != null) {
            return this.getSuperDatastoreClass().getBaseDatastoreClassWithMember(mmd);
        }
        if (this.memberMappingsMap.get(mmd) != null) {
            return this;
        }
        if (this.externalFkMappings != null && this.externalFkMappings.get(mmd) != null) {
            return this;
        }
        if (this.externalFkDiscriminatorMappings != null && this.externalFkDiscriminatorMappings.get(mmd) != null) {
            return this;
        }
        if (this.externalOrderMappings != null && this.externalOrderMappings.get(mmd) != null) {
            return this;
        }
        if (this.getSuperDatastoreClass() == null) {
            return this;
        }
        return this.getSuperDatastoreClass().getBaseDatastoreClassWithMember(mmd);
    }
    
    ClassMetaData getClassMetaData() {
        return this.cmd;
    }
    
    @Override
    protected Set getExpectedIndices(final ClassLoaderResolver clr) {
        boolean autoMode = false;
        if (this.storeMgr.getStringProperty("datanucleus.rdbms.constraintCreateMode").equals("DataNucleus")) {
            autoMode = true;
        }
        final Set indices = new HashSet();
        final Set memberNumbersSet = this.memberMappingsMap.keySet();
        for (final AbstractMemberMetaData fmd : memberNumbersSet) {
            final JavaTypeMapping fieldMapping = this.memberMappingsMap.get(fmd);
            if (fieldMapping instanceof EmbeddedPCMapping) {
                final EmbeddedPCMapping embMapping = (EmbeddedPCMapping)fieldMapping;
                for (int i = 0; i < embMapping.getNumberOfJavaTypeMappings(); ++i) {
                    final JavaTypeMapping embFieldMapping = embMapping.getJavaTypeMapping(i);
                    final IndexMetaData imd = embFieldMapping.getMemberMetaData().getIndexMetaData();
                    if (imd != null) {
                        final Index index = TableUtils.getIndexForField(this, imd, embFieldMapping);
                        if (index != null) {
                            indices.add(index);
                        }
                    }
                }
            }
            else {
                if (fieldMapping instanceof SerialisedMapping) {
                    continue;
                }
                final IndexMetaData imd2 = fmd.getIndexMetaData();
                if (imd2 != null) {
                    final Index index2 = TableUtils.getIndexForField(this, imd2, fieldMapping);
                    if (index2 == null) {
                        continue;
                    }
                    indices.add(index2);
                }
                else {
                    if (!autoMode || fmd.getIndexed() != null || fmd.isPrimaryKey()) {
                        continue;
                    }
                    final RelationType relationType = fmd.getRelationType(clr);
                    if (relationType == RelationType.ONE_TO_ONE_UNI) {
                        if (fieldMapping instanceof ReferenceMapping) {
                            final ReferenceMapping refMapping = (ReferenceMapping)fieldMapping;
                            if (refMapping.getMappingStrategy() != 0 || refMapping.getJavaTypeMapping() == null) {
                                continue;
                            }
                            int colNum = 0;
                            final JavaTypeMapping[] implMappings = refMapping.getJavaTypeMapping();
                            for (int j = 0; j < implMappings.length; ++j) {
                                final int numColsInImpl = implMappings[j].getNumberOfDatastoreMappings();
                                final Index index3 = new Index(this, false, null);
                                for (int k = 0; k < numColsInImpl; ++k) {
                                    index3.setColumn(k, fieldMapping.getDatastoreMapping(colNum++).getColumn());
                                }
                                indices.add(index3);
                            }
                        }
                        else {
                            final Index index4 = new Index(this, false, null);
                            for (int l = 0; l < fieldMapping.getNumberOfDatastoreMappings(); ++l) {
                                index4.setColumn(l, fieldMapping.getDatastoreMapping(l).getColumn());
                            }
                            indices.add(index4);
                        }
                    }
                    else if (relationType == RelationType.ONE_TO_ONE_BI && fmd.getMappedBy() == null) {
                        final Index index4 = new Index(this, false, null);
                        for (int l = 0; l < fieldMapping.getNumberOfDatastoreMappings(); ++l) {
                            index4.setColumn(l, fieldMapping.getDatastoreMapping(l).getColumn());
                        }
                        indices.add(index4);
                    }
                    else {
                        if (relationType != RelationType.MANY_TO_ONE_BI) {
                            continue;
                        }
                        final AbstractMemberMetaData relMmd = fmd.getRelatedMemberMetaData(clr)[0];
                        if (relMmd.getJoinMetaData() != null || fmd.getJoinMetaData() != null) {
                            continue;
                        }
                        final Index index5 = new Index(this, false, null);
                        for (int m = 0; m < fieldMapping.getNumberOfDatastoreMappings(); ++m) {
                            index5.setColumn(m, fieldMapping.getDatastoreMapping(m).getColumn());
                        }
                        indices.add(index5);
                    }
                }
            }
        }
        if (this.versionMapping != null) {
            final IndexMetaData idxmd = this.getVersionMetaData().getIndexMetaData();
            if (idxmd != null) {
                final Index index6 = new Index(this, idxmd.isUnique(), idxmd.getValueForExtension("extended-setting"));
                if (idxmd.getName() != null) {
                    index6.setName(idxmd.getName());
                }
                for (int countVersionFields = this.versionMapping.getNumberOfDatastoreMappings(), i = 0; i < countVersionFields; ++i) {
                    index6.addColumn(this.versionMapping.getDatastoreMapping(i).getColumn());
                }
                indices.add(index6);
            }
        }
        if (this.discriminatorMapping != null) {
            final DiscriminatorMetaData dismd = this.getDiscriminatorMetaData();
            final IndexMetaData idxmd2 = dismd.getIndexMetaData();
            if (idxmd2 != null) {
                final Index index7 = new Index(this, idxmd2.isUnique(), idxmd2.getValueForExtension("extended-setting"));
                if (idxmd2.getName() != null) {
                    index7.setName(idxmd2.getName());
                }
                for (int countDiscrimFields = this.discriminatorMapping.getNumberOfDatastoreMappings(), i2 = 0; i2 < countDiscrimFields; ++i2) {
                    index7.addColumn(this.discriminatorMapping.getDatastoreMapping(i2).getColumn());
                }
                indices.add(index7);
            }
        }
        final Set orderMappingsEntries = this.getExternalOrderMappings().entrySet();
        for (final Map.Entry entry : orderMappingsEntries) {
            final AbstractMemberMetaData fmd2 = entry.getKey();
            final JavaTypeMapping mapping = entry.getValue();
            final OrderMetaData omd = fmd2.getOrderMetaData();
            if (omd != null && omd.getIndexMetaData() != null) {
                final Index index = this.getIndexForIndexMetaDataAndMapping(omd.getIndexMetaData(), mapping);
                if (index == null) {
                    continue;
                }
                indices.add(index);
            }
        }
        for (final AbstractClassMetaData thisCmd : this.managedClassMetaData) {
            final IndexMetaData[] classIndices = thisCmd.getIndexMetaData();
            if (classIndices != null) {
                for (int l = 0; l < classIndices.length; ++l) {
                    final Index index = this.getIndexForIndexMetaData(classIndices[l]);
                    if (index != null) {
                        indices.add(index);
                    }
                }
            }
        }
        return indices;
    }
    
    private Index getIndexForIndexMetaDataAndMapping(final IndexMetaData imd, final JavaTypeMapping mapping) {
        final boolean unique = imd.isUnique();
        final Index index = new Index(this, unique, imd.getValueForExtension("extended-setting"));
        if (imd.getName() != null) {
            index.setName(imd.getName());
        }
        for (int numCols = mapping.getNumberOfDatastoreMappings(), i = 0; i < numCols; ++i) {
            index.addColumn(mapping.getDatastoreMapping(i).getColumn());
        }
        return index;
    }
    
    private Index getIndexForIndexMetaData(final IndexMetaData imd) {
        final boolean unique = imd.isUnique();
        final Index index = new Index(this, unique, imd.getValueForExtension("extended-setting"));
        if (imd.getName() != null) {
            index.setName(imd.getName());
        }
        final ColumnMetaData[] colmds = imd.getColumnMetaData();
        final String[] memberNames = imd.getMemberNames();
        if (colmds != null && colmds.length > 0) {
            for (int i = 0; i < colmds.length; ++i) {
                final DatastoreIdentifier colName = this.storeMgr.getIdentifierFactory().newColumnIdentifier(colmds[i].getName());
                final Column col = this.columnsByName.get(colName);
                if (col == null) {
                    NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("058001", this.toString(), index.getName(), colmds[i].getName()));
                    break;
                }
                index.addColumn(col);
            }
        }
        else {
            if (memberNames == null || memberNames.length <= 0) {
                NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("058002", this.toString(), index.getName()));
                return null;
            }
            for (int i = 0; i < memberNames.length; ++i) {
                final AbstractMemberMetaData realFmd = this.getMetaDataForMember(memberNames[i]);
                final JavaTypeMapping fieldMapping = this.memberMappingsMap.get(realFmd);
                for (int countFields = fieldMapping.getNumberOfDatastoreMappings(), j = 0; j < countFields; ++j) {
                    index.addColumn(fieldMapping.getDatastoreMapping(j).getColumn());
                }
            }
        }
        return index;
    }
    
    @Override
    public List getExpectedForeignKeys(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
        boolean autoMode = false;
        if (this.storeMgr.getStringProperty("datanucleus.rdbms.constraintCreateMode").equals("DataNucleus")) {
            autoMode = true;
        }
        final ArrayList foreignKeys = new ArrayList();
        final Set memberNumbersSet = this.memberMappingsMap.keySet();
        for (final AbstractMemberMetaData mmd : memberNumbersSet) {
            final JavaTypeMapping memberMapping = this.memberMappingsMap.get(mmd);
            if (mmd.getEmbeddedMetaData() != null && memberMapping instanceof EmbeddedPCMapping) {
                final EmbeddedPCMapping embMapping = (EmbeddedPCMapping)memberMapping;
                this.addExpectedForeignKeysForEmbeddedPCField(foreignKeys, autoMode, clr, embMapping);
            }
            else if (ClassUtils.isReferenceType(mmd.getType()) && memberMapping instanceof ReferenceMapping) {
                final Collection fks = TableUtils.getForeignKeysForReferenceField(memberMapping, mmd, autoMode, this.storeMgr, clr);
                foreignKeys.addAll(fks);
            }
            else {
                if (this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(mmd.getType(), clr) == null || memberMapping.getNumberOfDatastoreMappings() <= 0 || !(memberMapping instanceof PersistableMapping)) {
                    continue;
                }
                final ForeignKey fk = TableUtils.getForeignKeyForPCField(memberMapping, mmd, autoMode, this.storeMgr, clr);
                if (fk == null) {
                    continue;
                }
                foreignKeys.add(fk);
            }
        }
        final ForeignKeyMetaData idFkmd = (this.cmd.getInheritanceMetaData().getJoinMetaData() != null) ? this.cmd.getInheritanceMetaData().getJoinMetaData().getForeignKeyMetaData() : null;
        if (this.supertable != null && (autoMode || (idFkmd != null && idFkmd.getDeleteAction() != ForeignKeyAction.NONE))) {
            final ForeignKey fk2 = new ForeignKey(this.getIdMapping(), this.dba, this.supertable, false);
            if (idFkmd != null && idFkmd.getName() != null) {
                fk2.setName(idFkmd.getName());
            }
            foreignKeys.add(0, fk2);
        }
        for (final AbstractClassMetaData thisCmd : this.managedClassMetaData) {
            final ForeignKeyMetaData[] fkmds = thisCmd.getForeignKeyMetaData();
            if (fkmds != null) {
                for (int i = 0; i < fkmds.length; ++i) {
                    final ForeignKey fk3 = this.getForeignKeyForForeignKeyMetaData(fkmds[i]);
                    if (fk3 != null) {
                        foreignKeys.add(fk3);
                    }
                }
            }
        }
        final Map externalFks = this.getExternalFkMappings();
        if (!externalFks.isEmpty()) {
            final Collection externalFkKeys = externalFks.entrySet();
            for (final Map.Entry<AbstractMemberMetaData, JavaTypeMapping> entry : externalFkKeys) {
                final AbstractMemberMetaData fmd = entry.getKey();
                final DatastoreClass referencedTable = this.storeMgr.getDatastoreClass(fmd.getAbstractClassMetaData().getFullClassName(), clr);
                if (referencedTable != null) {
                    ForeignKeyMetaData fkmd = fmd.getForeignKeyMetaData();
                    if (fkmd == null && fmd.getElementMetaData() != null) {
                        fkmd = fmd.getElementMetaData().getForeignKeyMetaData();
                    }
                    if ((fkmd == null || fkmd.getDeleteAction() == ForeignKeyAction.NONE) && !autoMode) {
                        continue;
                    }
                    final JavaTypeMapping fkMapping = entry.getValue();
                    final ForeignKey fk4 = new ForeignKey(fkMapping, this.dba, referencedTable, true);
                    fk4.setForMetaData(fkmd);
                    if (foreignKeys.contains(fk4)) {
                        continue;
                    }
                    foreignKeys.add(fk4);
                }
            }
        }
        return foreignKeys;
    }
    
    private void addExpectedForeignKeysForEmbeddedPCField(final List foreignKeys, final boolean autoMode, final ClassLoaderResolver clr, final EmbeddedPCMapping embeddedMapping) {
        for (int i = 0; i < embeddedMapping.getNumberOfJavaTypeMappings(); ++i) {
            final JavaTypeMapping embFieldMapping = embeddedMapping.getJavaTypeMapping(i);
            if (embFieldMapping instanceof EmbeddedPCMapping) {
                this.addExpectedForeignKeysForEmbeddedPCField(foreignKeys, autoMode, clr, (EmbeddedPCMapping)embFieldMapping);
            }
            else {
                final AbstractMemberMetaData embFmd = embFieldMapping.getMemberMetaData();
                if (ClassUtils.isReferenceType(embFmd.getType()) && embFieldMapping instanceof ReferenceMapping) {
                    final Collection fks = TableUtils.getForeignKeysForReferenceField(embFieldMapping, embFmd, autoMode, this.storeMgr, clr);
                    foreignKeys.addAll(fks);
                }
                else if (this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(embFmd.getType(), clr) != null && embFieldMapping.getNumberOfDatastoreMappings() > 0 && embFieldMapping instanceof PersistableMapping) {
                    final ForeignKey fk = TableUtils.getForeignKeyForPCField(embFieldMapping, embFmd, autoMode, this.storeMgr, clr);
                    if (fk != null) {
                        foreignKeys.add(fk);
                    }
                }
            }
        }
    }
    
    private ForeignKey getForeignKeyForForeignKeyMetaData(final ForeignKeyMetaData fkmd) {
        if (fkmd == null) {
            return null;
        }
        final ForeignKey fk = new ForeignKey(fkmd.isDeferred());
        fk.setForMetaData(fkmd);
        if (fkmd.getFkDefinitionApplies()) {
            return fk;
        }
        final AbstractClassMetaData acmd = this.cmd;
        if (fkmd.getTable() == null) {
            NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("058105", acmd.getFullClassName()));
            return null;
        }
        final DatastoreIdentifier tableId = this.storeMgr.getIdentifierFactory().newTableIdentifier(fkmd.getTable());
        final ClassTable refTable = (ClassTable)this.storeMgr.getDatastoreClass(tableId);
        if (refTable == null) {
            NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("058106", acmd.getFullClassName(), fkmd.getTable()));
            return null;
        }
        final PrimaryKey pk = refTable.getPrimaryKey();
        final List targetCols = pk.getColumns();
        final List sourceCols = new ArrayList();
        final ColumnMetaData[] colmds = fkmd.getColumnMetaData();
        final String[] memberNames = fkmd.getMemberNames();
        if (colmds != null && colmds.length > 0) {
            for (int i = 0; i < colmds.length; ++i) {
                final DatastoreIdentifier colId = this.storeMgr.getIdentifierFactory().newColumnIdentifier(colmds[i].getName());
                final Column sourceCol = this.columnsByName.get(colId);
                if (sourceCol == null) {
                    NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("058107", acmd.getFullClassName(), fkmd.getTable(), colmds[i].getName(), this.toString()));
                    return null;
                }
                sourceCols.add(sourceCol);
            }
        }
        else if (memberNames != null && memberNames.length > 0) {
            for (int i = 0; i < memberNames.length; ++i) {
                final AbstractMemberMetaData realFmd = this.getMetaDataForMember(memberNames[i]);
                final JavaTypeMapping fieldMapping = this.memberMappingsMap.get(realFmd);
                for (int countCols = fieldMapping.getNumberOfDatastoreMappings(), j = 0; j < countCols; ++j) {
                    sourceCols.add(fieldMapping.getDatastoreMapping(j).getColumn());
                }
            }
        }
        if (sourceCols.size() != targetCols.size()) {
            NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("058108", acmd.getFullClassName(), fkmd.getTable(), "" + sourceCols.size(), "" + targetCols.size()));
        }
        if (sourceCols.size() > 0) {
            for (int i = 0; i < sourceCols.size(); ++i) {
                final Column source = sourceCols.get(i);
                final String targetColName = colmds[i].getTarget();
                Column target = targetCols.get(i);
                if (targetColName != null) {
                    for (int j = 0; j < targetCols.size(); ++j) {
                        final Column targetCol = targetCols.get(j);
                        if (targetCol.getIdentifier().getIdentifierName().equalsIgnoreCase(targetColName)) {
                            target = targetCol;
                            break;
                        }
                    }
                }
                fk.addColumn(source, target);
            }
        }
        return fk;
    }
    
    @Override
    protected List getExpectedCandidateKeys() {
        this.assertIsInitialized();
        final List candidateKeys = super.getExpectedCandidateKeys();
        final Set fieldNumbersSet = this.memberMappingsMap.keySet();
        for (final AbstractMemberMetaData fmd : fieldNumbersSet) {
            final JavaTypeMapping fieldMapping = this.memberMappingsMap.get(fmd);
            if (fieldMapping instanceof EmbeddedPCMapping) {
                final EmbeddedPCMapping embMapping = (EmbeddedPCMapping)fieldMapping;
                for (int i = 0; i < embMapping.getNumberOfJavaTypeMappings(); ++i) {
                    final JavaTypeMapping embFieldMapping = embMapping.getJavaTypeMapping(i);
                    final UniqueMetaData umd = embFieldMapping.getMemberMetaData().getUniqueMetaData();
                    if (umd != null) {
                        final CandidateKey ck = TableUtils.getCandidateKeyForField(this, umd, embFieldMapping);
                        if (ck != null) {
                            candidateKeys.add(ck);
                        }
                    }
                }
            }
            else {
                final UniqueMetaData umd2 = fmd.getUniqueMetaData();
                if (umd2 == null) {
                    continue;
                }
                final CandidateKey ck2 = TableUtils.getCandidateKeyForField(this, umd2, fieldMapping);
                if (ck2 == null) {
                    continue;
                }
                candidateKeys.add(ck2);
            }
        }
        for (final AbstractClassMetaData thisCmd : this.managedClassMetaData) {
            final UniqueMetaData[] classCKs = thisCmd.getUniqueMetaData();
            if (classCKs != null) {
                for (int i = 0; i < classCKs.length; ++i) {
                    final CandidateKey ck3 = this.getCandidateKeyForUniqueMetaData(classCKs[i]);
                    if (ck3 != null) {
                        candidateKeys.add(ck3);
                    }
                }
            }
        }
        return candidateKeys;
    }
    
    private CandidateKey getCandidateKeyForUniqueMetaData(final UniqueMetaData umd) {
        final CandidateKey ck = new CandidateKey(this);
        if (umd.getName() != null) {
            ck.setName(umd.getName());
        }
        final ColumnMetaData[] colmds = umd.getColumnMetaData();
        final String[] memberNames = umd.getMemberNames();
        if (colmds != null && colmds.length > 0) {
            for (int i = 0; i < colmds.length; ++i) {
                final DatastoreIdentifier colName = this.storeMgr.getIdentifierFactory().newColumnIdentifier(colmds[i].getName());
                final Column col = this.columnsByName.get(colName);
                if (col == null) {
                    NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("058202", this.toString(), ck.getName(), colmds[i].getName()));
                    break;
                }
                ck.addColumn(col);
            }
        }
        else {
            if (memberNames == null || memberNames.length <= 0) {
                NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("058203", this.toString(), ck.getName()));
                return null;
            }
            for (int i = 0; i < memberNames.length; ++i) {
                final AbstractMemberMetaData realMmd = this.getMetaDataForMember(memberNames[i]);
                if (realMmd == null) {
                    NucleusLogger.DATASTORE_SCHEMA.warn("Unique metadata defined to use field " + memberNames[i] + " which doesn't exist in this class");
                    return null;
                }
                final JavaTypeMapping memberMapping = this.memberMappingsMap.get(realMmd);
                for (int countFields = memberMapping.getNumberOfDatastoreMappings(), j = 0; j < countFields; ++j) {
                    ck.addColumn(memberMapping.getDatastoreMapping(j).getColumn());
                }
            }
        }
        return ck;
    }
    
    @Override
    public PrimaryKey getPrimaryKey() {
        final PrimaryKey pk = super.getPrimaryKey();
        final PrimaryKeyMetaData pkmd = this.cmd.getPrimaryKeyMetaData();
        if (pkmd != null && pkmd.getName() != null) {
            pk.setName(pkmd.getName());
        }
        return pk;
    }
    
    @Override
    protected List getSQLCreateStatements(final Properties props) {
        Properties tableProps = null;
        List stmts;
        if (this.createStatementDDL != null) {
            stmts = new ArrayList();
            final StringTokenizer tokens = new StringTokenizer(this.createStatementDDL, ";");
            while (tokens.hasMoreTokens()) {
                stmts.add(tokens.nextToken());
            }
        }
        else {
            if (this.cmd.getExtensions() != null) {
                tableProps = new Properties();
                final ExtensionMetaData[] emds = this.cmd.getExtensions();
                for (int i = 0; i < emds.length; ++i) {
                    if (emds[i].getVendorName().equalsIgnoreCase("datanucleus")) {
                        tableProps.put(emds[i].getKey(), emds[i].getValue());
                    }
                }
            }
            stmts = super.getSQLCreateStatements(tableProps);
        }
        if (this.secondaryTables != null) {
            final Set secondaryTableNames = this.secondaryTables.keySet();
            final Iterator iter = secondaryTableNames.iterator();
            while (iter.hasNext()) {
                final SecondaryTable secTable = this.secondaryTables.get(iter.next());
                stmts.addAll(secTable.getSQLCreateStatements(tableProps));
            }
        }
        stmts.addAll(this.getSQLAddUniqueConstraintsStatements());
        return stmts;
    }
    
    @Override
    protected List getSQLDropStatements() {
        this.assertIsInitialized();
        final ArrayList stmts = new ArrayList();
        if (this.secondaryTables != null) {
            final Set secondaryTableNames = this.secondaryTables.keySet();
            final Iterator iter = secondaryTableNames.iterator();
            while (iter.hasNext()) {
                final SecondaryTable secTable = this.secondaryTables.get(iter.next());
                stmts.addAll(secTable.getSQLDropStatements());
            }
        }
        stmts.add(this.dba.getDropTableStatement(this));
        return stmts;
    }
    
    private List getSQLAddUniqueConstraintsStatements() {
        final ArrayList stmts = new ArrayList();
        int ckNum = 0;
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        final Iterator<CandidateKey> cks = this.candidateKeysByMapField.values().iterator();
        while (cks.hasNext()) {
            final DatastoreIdentifier ckName = idFactory.newCandidateKeyIdentifier(this, ++ckNum);
            final CandidateKey ck = cks.next();
            ck.setName(ckName.getIdentifierName());
            final String ckSql = this.dba.getAddCandidateKeyStatement(ck, idFactory);
            if (ckSql != null) {
                stmts.add(ckSql);
            }
        }
        return stmts;
    }
    
    private void initializeFKMapUniqueConstraints(final AbstractMemberMetaData ownerMmd) {
        AbstractMemberMetaData mfmd = null;
        final String map_field_name = ownerMmd.getMappedBy();
        if (map_field_name != null) {
            mfmd = this.cmd.getMetaDataForMember(map_field_name);
            if (mfmd == null) {
                for (final AbstractClassMetaData managedCmd : this.managedClassMetaData) {
                    mfmd = managedCmd.getMetaDataForMember(map_field_name);
                    if (mfmd != null) {
                        break;
                    }
                }
            }
            if (mfmd == null) {
                throw new NucleusUserException(ClassTable.LOCALISER.msg("057036", map_field_name, this.cmd.getFullClassName(), ownerMmd.getFullFieldName()));
            }
            if (ownerMmd.getJoinMetaData() == null) {
                if (ownerMmd.getKeyMetaData() != null && ownerMmd.getKeyMetaData().getMappedBy() != null) {
                    AbstractMemberMetaData kmd = null;
                    final String key_field_name = ownerMmd.getKeyMetaData().getMappedBy();
                    if (key_field_name != null) {
                        kmd = this.cmd.getMetaDataForMember(key_field_name);
                    }
                    if (kmd == null) {
                        for (final AbstractClassMetaData managedCmd2 : this.managedClassMetaData) {
                            kmd = managedCmd2.getMetaDataForMember(key_field_name);
                            if (kmd != null) {
                                break;
                            }
                        }
                    }
                    if (kmd == null) {
                        throw new ClassDefinitionException(ClassTable.LOCALISER.msg("057007", mfmd.getFullFieldName(), key_field_name));
                    }
                    final JavaTypeMapping ownerMapping = this.getMemberMapping(map_field_name);
                    final JavaTypeMapping keyMapping = this.getMemberMapping(kmd.getName());
                    if ((this.dba.supportsOption("NullsInCandidateKeys") || (!ownerMapping.isNullable() && !keyMapping.isNullable())) && keyMapping.getTable() == this && ownerMapping.getTable() == this) {
                        final CandidateKey ck = new CandidateKey(this);
                        final HashSet addedColumns = new HashSet();
                        for (int countOwnerFields = ownerMapping.getNumberOfDatastoreMappings(), i = 0; i < countOwnerFields; ++i) {
                            final Column col = ownerMapping.getDatastoreMapping(i).getColumn();
                            addedColumns.add(col);
                            ck.addColumn(col);
                        }
                        for (int countKeyFields = keyMapping.getNumberOfDatastoreMappings(), j = 0; j < countKeyFields; ++j) {
                            final Column col2 = keyMapping.getDatastoreMapping(j).getColumn();
                            if (!addedColumns.contains(col2)) {
                                addedColumns.add(col2);
                                ck.addColumn(col2);
                            }
                            else {
                                NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("057041", ownerMmd.getName()));
                            }
                        }
                        if (this.candidateKeysByMapField.put(mfmd, ck) != null) {
                            NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("057012", mfmd.getFullFieldName(), ownerMmd.getFullFieldName()));
                        }
                    }
                }
                else {
                    if (ownerMmd.getValueMetaData() == null || ownerMmd.getValueMetaData().getMappedBy() == null) {
                        throw new ClassDefinitionException(ClassTable.LOCALISER.msg("057009", ownerMmd.getFullFieldName()));
                    }
                    AbstractMemberMetaData vmd = null;
                    final String value_field_name = ownerMmd.getValueMetaData().getMappedBy();
                    if (value_field_name != null) {
                        vmd = this.cmd.getMetaDataForMember(value_field_name);
                    }
                    if (vmd == null) {
                        throw new ClassDefinitionException(ClassTable.LOCALISER.msg("057008", mfmd));
                    }
                    final JavaTypeMapping ownerMapping = this.getMemberMapping(map_field_name);
                    final JavaTypeMapping valueMapping = this.getMemberMapping(vmd.getName());
                    if ((this.dba.supportsOption("NullsInCandidateKeys") || (!ownerMapping.isNullable() && !valueMapping.isNullable())) && valueMapping.getTable() == this && ownerMapping.getTable() == this) {
                        final CandidateKey ck = new CandidateKey(this);
                        final HashSet addedColumns = new HashSet();
                        for (int countOwnerFields = ownerMapping.getNumberOfDatastoreMappings(), i = 0; i < countOwnerFields; ++i) {
                            final Column col = ownerMapping.getDatastoreMapping(i).getColumn();
                            addedColumns.add(col);
                            ck.addColumn(col);
                        }
                        for (int countValueFields = valueMapping.getNumberOfDatastoreMappings(), j = 0; j < countValueFields; ++j) {
                            final Column col2 = valueMapping.getDatastoreMapping(j).getColumn();
                            if (!addedColumns.contains(col2)) {
                                addedColumns.add(col2);
                                ck.addColumn(col2);
                            }
                            else {
                                NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("057042", ownerMmd.getName()));
                            }
                        }
                        if (this.candidateKeysByMapField.put(mfmd, ck) != null) {
                            NucleusLogger.DATASTORE_SCHEMA.warn(ClassTable.LOCALISER.msg("057012", mfmd.getFullFieldName(), ownerMmd.getFullFieldName()));
                        }
                    }
                }
            }
        }
    }
    
    private void initializeIDMapping() {
        if (this.idMapping != null) {
            return;
        }
        final PersistableMapping mapping = new PersistableMapping();
        mapping.setTable(this);
        mapping.initialize(this.getStoreManager(), this.cmd.getFullClassName());
        if (this.getIdentityType() == IdentityType.DATASTORE) {
            mapping.addJavaTypeMapping(this.datastoreIDMapping);
        }
        else if (this.getIdentityType() == IdentityType.APPLICATION) {
            for (int i = 0; i < this.pkMappings.length; ++i) {
                mapping.addJavaTypeMapping(this.pkMappings[i]);
            }
        }
        this.idMapping = mapping;
    }
    
    @Override
    public JavaTypeMapping getIdMapping() {
        return this.idMapping;
    }
    
    private Map<AbstractMemberMetaData, JavaTypeMapping> getExternalOrderMappings() {
        if (this.externalOrderMappings == null) {
            this.externalOrderMappings = new HashMap<AbstractMemberMetaData, JavaTypeMapping>();
        }
        return this.externalOrderMappings;
    }
    
    public boolean hasExternalFkMappings() {
        return this.externalFkMappings != null && this.externalFkMappings.size() > 0;
    }
    
    private Map<AbstractMemberMetaData, JavaTypeMapping> getExternalFkMappings() {
        if (this.externalFkMappings == null) {
            this.externalFkMappings = new HashMap<AbstractMemberMetaData, JavaTypeMapping>();
        }
        return this.externalFkMappings;
    }
    
    @Override
    public JavaTypeMapping getExternalMapping(final AbstractMemberMetaData mmd, final int mappingType) {
        if (mappingType == 5) {
            return this.getExternalFkMappings().get(mmd);
        }
        if (mappingType == 6) {
            return this.getExternalFkDiscriminatorMappings().get(mmd);
        }
        if (mappingType == 4) {
            return this.getExternalOrderMappings().get(mmd);
        }
        return null;
    }
    
    @Override
    public AbstractMemberMetaData getMetaDataForExternalMapping(final JavaTypeMapping mapping, final int mappingType) {
        if (mappingType == 5) {
            final Set entries = this.getExternalFkMappings().entrySet();
            for (final Map.Entry entry : entries) {
                if (entry.getValue() == mapping) {
                    return entry.getKey();
                }
            }
        }
        else if (mappingType == 6) {
            final Set entries = this.getExternalFkDiscriminatorMappings().entrySet();
            for (final Map.Entry entry : entries) {
                if (entry.getValue() == mapping) {
                    return entry.getKey();
                }
            }
        }
        else if (mappingType == 4) {
            final Set entries = this.getExternalOrderMappings().entrySet();
            for (final Map.Entry entry : entries) {
                if (entry.getValue() == mapping) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    
    private Map<AbstractMemberMetaData, JavaTypeMapping> getExternalFkDiscriminatorMappings() {
        if (this.externalFkDiscriminatorMappings == null) {
            this.externalFkDiscriminatorMappings = new HashMap<AbstractMemberMetaData, JavaTypeMapping>();
        }
        return this.externalFkDiscriminatorMappings;
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        if (mmd == null) {
            return null;
        }
        if (mmd instanceof PropertyMetaData && mmd.getAbstractClassMetaData() instanceof InterfaceMetaData) {
            return this.getMemberMapping(mmd.getName());
        }
        if (mmd.isPrimaryKey()) {
            this.assertIsPKInitialized();
        }
        else {
            this.assertIsInitialized();
        }
        JavaTypeMapping m = this.memberMappingsMap.get(mmd);
        if (m != null) {
            return m;
        }
        final int ifc = this.cmd.getNoOfInheritedManagedMembers();
        if (mmd.getAbsoluteFieldNumber() < ifc && this.supertable != null) {
            m = this.supertable.getMemberMapping(mmd);
            if (m != null) {
                return m;
            }
        }
        if (this.secondaryTables != null) {
            final Collection secTables = this.secondaryTables.values();
            for (final SecondaryTable secTable : secTables) {
                m = secTable.getMemberMapping(mmd);
                if (m != null) {
                    return m;
                }
            }
        }
        return null;
    }
    
    @Override
    public JavaTypeMapping getMemberMappingInDatastoreClass(final AbstractMemberMetaData mmd) {
        if (mmd == null) {
            return null;
        }
        if (mmd instanceof PropertyMetaData && mmd.getAbstractClassMetaData() instanceof InterfaceMetaData) {
            return this.getMemberMapping(mmd.getName());
        }
        if (mmd.isPrimaryKey()) {
            this.assertIsPKInitialized();
        }
        else {
            this.assertIsInitialized();
        }
        final JavaTypeMapping m = this.memberMappingsMap.get(mmd);
        if (m != null) {
            return m;
        }
        if (this.pkMappings != null) {
            for (int i = 0; i < this.pkMappings.length; ++i) {
                final JavaTypeMapping pkMapping = this.pkMappings[i];
                if (pkMapping.getMemberMetaData() == mmd) {
                    return pkMapping;
                }
            }
        }
        return null;
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final String memberName) {
        this.assertIsInitialized();
        final AbstractMemberMetaData mmd = this.getMetaDataForMember(memberName);
        final JavaTypeMapping m = this.getMemberMapping(mmd);
        if (m == null) {
            throw new NoSuchPersistentFieldException(this.cmd.getFullClassName(), memberName);
        }
        return m;
    }
    
    AbstractMemberMetaData getMetaDataForMember(final String memberName) {
        AbstractMemberMetaData mmd = this.cmd.getMetaDataForMember(memberName);
        if (mmd == null) {
            for (final AbstractClassMetaData theCmd : this.managedClassMetaData) {
                final AbstractMemberMetaData foundMmd = theCmd.getMetaDataForMember(memberName);
                if (foundMmd != null) {
                    if (mmd != null && (!mmd.toString().equalsIgnoreCase(foundMmd.toString()) || mmd.getType() != foundMmd.getType())) {
                        final String errMsg = "Table " + this.getIdentifier() + " manages at least 2 subclasses that both define a field \"" + memberName + "\", " + "and the fields' metadata is different or they have different type! That means you can get e.g. wrong fetch results.";
                        NucleusLogger.DATASTORE_SCHEMA.error(errMsg);
                        throw new NucleusException(errMsg).setFatal();
                    }
                    mmd = foundMmd;
                }
            }
        }
        return mmd;
    }
    
    void assertPCClass(final ObjectProvider op) {
        final Class c = op.getObject().getClass();
        if (!op.getExecutionContext().getClassLoaderResolver().isAssignableFrom(this.cmd.getFullClassName(), c)) {
            throw new NucleusException(ClassTable.LOCALISER.msg("057013", this.cmd.getFullClassName(), c)).setFatal();
        }
    }
    
    private JavaTypeMapping addOrderColumn(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        final Class indexType = Integer.class;
        final JavaTypeMapping indexMapping = new IndexMapping();
        indexMapping.initialize(this.storeMgr, indexType.getName());
        indexMapping.setMemberMetaData(mmd);
        indexMapping.setTable(this);
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        DatastoreIdentifier indexColumnName = null;
        ColumnMetaData colmd = null;
        final OrderMetaData omd = mmd.getOrderMetaData();
        if (omd != null) {
            colmd = ((omd.getColumnMetaData() != null && omd.getColumnMetaData().length > 0) ? omd.getColumnMetaData()[0] : null);
            if (omd.getMappedBy() != null) {
                this.state = 2;
                final JavaTypeMapping orderMapping = this.getMemberMapping(omd.getMappedBy());
                if (orderMapping == null) {
                    throw new NucleusUserException(ClassTable.LOCALISER.msg("057021", mmd.getFullFieldName(), omd.getMappedBy()));
                }
                if (!(orderMapping instanceof IntegerMapping) && !(orderMapping instanceof LongMapping)) {
                    throw new NucleusUserException(ClassTable.LOCALISER.msg("057022", mmd.getFullFieldName(), omd.getMappedBy()));
                }
                return orderMapping;
            }
            else {
                String colName = null;
                if (omd.getColumnMetaData() != null && omd.getColumnMetaData().length > 0 && omd.getColumnMetaData()[0].getName() != null) {
                    colName = omd.getColumnMetaData()[0].getName();
                    indexColumnName = idFactory.newColumnIdentifier(colName);
                }
            }
        }
        if (indexColumnName == null) {
            indexColumnName = idFactory.newForeignKeyFieldIdentifier(mmd, null, null, this.storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(indexType), 7);
        }
        final Column column = this.addColumn(indexType.getName(), indexColumnName, indexMapping, colmd);
        if (colmd == null || colmd.getAllowsNull() == null || (colmd.getAllowsNull() != null && colmd.isAllowsNull())) {
            column.setNullable();
        }
        this.storeMgr.getMappingManager().createDatastoreMapping(indexMapping, column, indexType.getName());
        return indexMapping;
    }
    
    @Override
    public void providePrimaryKeyMappings(final MappingConsumer consumer) {
        consumer.preConsumeMapping(this.highestMemberNumber + 1);
        if (this.pkMappings != null) {
            final int[] primaryKeyFieldNumbers = this.cmd.getPKMemberPositions();
            for (int i = 0; i < this.pkMappings.length; ++i) {
                final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(primaryKeyFieldNumbers[i]);
                consumer.consumeMapping(this.pkMappings[i], fmd);
            }
        }
        else {
            final int[] primaryKeyFieldNumbers = this.cmd.getPKMemberPositions();
            for (int countPkFields = this.cmd.getNoOfPrimaryKeyMembers(), j = 0; j < countPkFields; ++j) {
                final AbstractMemberMetaData pkfmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(primaryKeyFieldNumbers[j]);
                consumer.consumeMapping(this.getMemberMapping(pkfmd), pkfmd);
            }
        }
    }
    
    @Override
    public final void provideExternalMappings(final MappingConsumer consumer, final int mappingType) {
        if (mappingType == 5 && this.externalFkMappings != null) {
            consumer.preConsumeMapping(this.highestMemberNumber + 1);
            for (final AbstractMemberMetaData fmd : this.externalFkMappings.keySet()) {
                final JavaTypeMapping fieldMapping = this.externalFkMappings.get(fmd);
                if (fieldMapping != null) {
                    consumer.consumeMapping(fieldMapping, 5);
                }
            }
        }
        else if (mappingType == 6 && this.externalFkDiscriminatorMappings != null) {
            consumer.preConsumeMapping(this.highestMemberNumber + 1);
            for (final AbstractMemberMetaData fmd : this.externalFkDiscriminatorMappings.keySet()) {
                final JavaTypeMapping fieldMapping = this.externalFkDiscriminatorMappings.get(fmd);
                if (fieldMapping != null) {
                    consumer.consumeMapping(fieldMapping, 6);
                }
            }
        }
        else if (mappingType == 4 && this.externalOrderMappings != null) {
            consumer.preConsumeMapping(this.highestMemberNumber + 1);
            for (final AbstractMemberMetaData fmd : this.externalOrderMappings.keySet()) {
                final JavaTypeMapping fieldMapping = this.externalOrderMappings.get(fmd);
                if (fieldMapping != null) {
                    consumer.consumeMapping(fieldMapping, 4);
                }
            }
        }
    }
    
    @Override
    public void provideMappingsForMembers(final MappingConsumer consumer, final AbstractMemberMetaData[] fieldMetaData, final boolean includeSecondaryTables) {
        super.provideMappingsForMembers(consumer, fieldMetaData, true);
        if (includeSecondaryTables && this.secondaryTables != null) {
            final Collection secTables = this.secondaryTables.values();
            for (final SecondaryTable secTable : secTables) {
                secTable.provideMappingsForMembers(consumer, fieldMetaData, false);
            }
        }
    }
    
    @Override
    public void provideUnmappedColumns(final MappingConsumer consumer) {
        if (this.unmappedColumns != null) {
            final Iterator<Column> iter = this.unmappedColumns.iterator();
            while (iter.hasNext()) {
                consumer.consumeUnmappedColumn(iter.next());
            }
        }
    }
    
    @Override
    public boolean validateConstraints(final Connection conn, final boolean autoCreate, final Collection autoCreateErrors, final ClassLoaderResolver clr) throws SQLException {
        boolean modified = false;
        if (super.validateConstraints(conn, autoCreate, autoCreateErrors, clr)) {
            modified = true;
        }
        if (this.secondaryTables != null) {
            final Collection secTables = this.secondaryTables.values();
            for (final SecondaryTable secTable : secTables) {
                if (secTable.validateConstraints(conn, autoCreate, autoCreateErrors, clr)) {
                    modified = true;
                }
            }
        }
        return modified;
    }
}
