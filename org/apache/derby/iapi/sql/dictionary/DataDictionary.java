// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.services.daemon.IndexStatisticsDaemon;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.types.NumberDataValue;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import java.util.Hashtable;
import org.apache.derby.iapi.sql.compile.Visitable;
import java.util.List;
import java.util.Dictionary;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public interface DataDictionary
{
    public static final String MODULE = "org.apache.derby.iapi.sql.dictionary.DataDictionary";
    public static final int DD_VERSION_CURRENT = -1;
    public static final int DD_VERSION_CS_5_0 = 80;
    public static final int DD_VERSION_CS_5_1 = 90;
    public static final int DD_VERSION_CS_5_2 = 100;
    public static final int DD_VERSION_CS_8_1 = 110;
    public static final int DD_VERSION_CS_10_0 = 120;
    public static final int DD_VERSION_DERBY_10_1 = 130;
    public static final int DD_VERSION_DERBY_10_2 = 140;
    public static final int DD_VERSION_DERBY_10_3 = 150;
    public static final int DD_VERSION_DERBY_10_4 = 160;
    public static final int DD_VERSION_DERBY_10_5 = 170;
    public static final int DD_VERSION_DERBY_10_6 = 180;
    public static final int DD_VERSION_DERBY_10_7 = 190;
    public static final int DD_VERSION_DERBY_10_8 = 200;
    public static final int DD_VERSION_DERBY_10_9 = 210;
    public static final int DD_VERSION_DERBY_10_10 = 220;
    public static final String DATABASE_ID = "derby.databaseID";
    public static final String CORE_DATA_DICTIONARY_VERSION = "DataDictionaryVersion";
    public static final String CREATE_DATA_DICTIONARY_VERSION = "CreateDataDictionaryVersion";
    public static final String SOFT_DATA_DICTIONARY_VERSION = "derby.softDataDictionaryVersion";
    public static final String PROPERTY_CONGLOMERATE_VERSION = "PropertyConglomerateVersion";
    public static final DataTypeDescriptor TYPE_SYSTEM_IDENTIFIER = DataTypeDescriptor.getBuiltInDataTypeDescriptor(12, false, 128);
    public static final TypeDescriptor CATALOG_TYPE_SYSTEM_IDENTIFIER = DataDictionary.TYPE_SYSTEM_IDENTIFIER.getCatalogType();
    public static final int SYSCONGLOMERATES_CATALOG_NUM = 0;
    public static final int SYSTABLES_CATALOG_NUM = 1;
    public static final int SYSCOLUMNS_CATALOG_NUM = 2;
    public static final int SYSSCHEMAS_CATALOG_NUM = 3;
    public static final int SYSCONSTRAINTS_CATALOG_NUM = 4;
    public static final int SYSKEYS_CATALOG_NUM = 5;
    public static final int SYSDEPENDS_CATALOG_NUM = 6;
    public static final int SYSALIASES_CATALOG_NUM = 7;
    public static final int SYSVIEWS_CATALOG_NUM = 8;
    public static final int SYSCHECKS_CATALOG_NUM = 9;
    public static final int SYSFOREIGNKEYS_CATALOG_NUM = 10;
    public static final int SYSSTATEMENTS_CATALOG_NUM = 11;
    public static final int SYSFILES_CATALOG_NUM = 12;
    public static final int SYSTRIGGERS_CATALOG_NUM = 13;
    public static final int SYSSTATISTICS_CATALOG_NUM = 14;
    public static final int SYSDUMMY1_CATALOG_NUM = 15;
    public static final int SYSTABLEPERMS_CATALOG_NUM = 16;
    public static final int SYSCOLPERMS_CATALOG_NUM = 17;
    public static final int SYSROUTINEPERMS_CATALOG_NUM = 18;
    public static final int SYSROLES_CATALOG_NUM = 19;
    public static final int SYSSEQUENCES_CATALOG_NUM = 20;
    public static final int SYSPERMS_CATALOG_NUM = 21;
    public static final int SYSUSERS_CATALOG_NUM = 22;
    public static final int NOTNULL_CONSTRAINT = 1;
    public static final int PRIMARYKEY_CONSTRAINT = 2;
    public static final int UNIQUE_CONSTRAINT = 3;
    public static final int CHECK_CONSTRAINT = 4;
    public static final int DROP_CONSTRAINT = 5;
    public static final int FOREIGNKEY_CONSTRAINT = 6;
    public static final int COMPILE_ONLY_MODE = 0;
    public static final int DDL_MODE = 1;
    
    void clearCaches(final boolean p0) throws StandardException;
    
    void clearCaches() throws StandardException;
    
    void clearSequenceCaches() throws StandardException;
    
    int startReading(final LanguageConnectionContext p0) throws StandardException;
    
    void doneReading(final int p0, final LanguageConnectionContext p1) throws StandardException;
    
    void startWriting(final LanguageConnectionContext p0) throws StandardException;
    
    void transactionFinished() throws StandardException;
    
    ExecutionFactory getExecutionFactory();
    
    DataValueFactory getDataValueFactory();
    
    DataDescriptorGenerator getDataDescriptorGenerator();
    
    String getAuthorizationDatabaseOwner();
    
    boolean usesSqlAuthorization();
    
    int getCollationTypeOfSystemSchemas();
    
    int getCollationTypeOfUserSchemas();
    
    SchemaDescriptor getSchemaDescriptor(final String p0, final TransactionController p1, final boolean p2) throws StandardException;
    
    SchemaDescriptor getSchemaDescriptor(final UUID p0, final TransactionController p1) throws StandardException;
    
    SchemaDescriptor getSchemaDescriptor(final UUID p0, final int p1, final TransactionController p2) throws StandardException;
    
    boolean existsSchemaOwnedBy(final String p0, final TransactionController p1) throws StandardException;
    
    PasswordHasher makePasswordHasher(final Dictionary p0) throws StandardException;
    
    SchemaDescriptor getSystemSchemaDescriptor() throws StandardException;
    
    SchemaDescriptor getSysIBMSchemaDescriptor() throws StandardException;
    
    SchemaDescriptor getDeclaredGlobalTemporaryTablesSchemaDescriptor() throws StandardException;
    
    boolean isSystemSchemaName(final String p0) throws StandardException;
    
    void dropRoleGrant(final String p0, final String p1, final String p2, final TransactionController p3) throws StandardException;
    
    void dropRoleGrantsByGrantee(final String p0, final TransactionController p1) throws StandardException;
    
    void dropRoleGrantsByName(final String p0, final TransactionController p1) throws StandardException;
    
    RoleClosureIterator createRoleClosureIterator(final TransactionController p0, final String p1, final boolean p2) throws StandardException;
    
    void dropAllPermsByGrantee(final String p0, final TransactionController p1) throws StandardException;
    
    void dropSchemaDescriptor(final String p0, final TransactionController p1) throws StandardException;
    
    boolean isSchemaEmpty(final SchemaDescriptor p0) throws StandardException;
    
    TableDescriptor getTableDescriptor(final String p0, final SchemaDescriptor p1, final TransactionController p2) throws StandardException;
    
    TableDescriptor getTableDescriptor(final UUID p0) throws StandardException;
    
    void dropTableDescriptor(final TableDescriptor p0, final SchemaDescriptor p1, final TransactionController p2) throws StandardException;
    
    void updateLockGranularity(final TableDescriptor p0, final SchemaDescriptor p1, final char p2, final TransactionController p3) throws StandardException;
    
    ColumnDescriptor getColumnDescriptorByDefaultId(final UUID p0) throws StandardException;
    
    void dropColumnDescriptor(final UUID p0, final String p1, final TransactionController p2) throws StandardException;
    
    void dropAllColumnDescriptors(final UUID p0, final TransactionController p1) throws StandardException;
    
    void dropAllTableAndColPermDescriptors(final UUID p0, final TransactionController p1) throws StandardException;
    
    void updateSYSCOLPERMSforAddColumnToUserTable(final UUID p0, final TransactionController p1) throws StandardException;
    
    void updateSYSCOLPERMSforDropColumn(final UUID p0, final TransactionController p1, final ColumnDescriptor p2) throws StandardException;
    
    void dropAllRoutinePermDescriptors(final UUID p0, final TransactionController p1) throws StandardException;
    
    ViewDescriptor getViewDescriptor(final UUID p0) throws StandardException;
    
    ViewDescriptor getViewDescriptor(final TableDescriptor p0) throws StandardException;
    
    void dropViewDescriptor(final ViewDescriptor p0, final TransactionController p1) throws StandardException;
    
    ConstraintDescriptor getConstraintDescriptor(final UUID p0) throws StandardException;
    
    ConstraintDescriptor getConstraintDescriptor(final String p0, final UUID p1) throws StandardException;
    
    ConstraintDescriptorList getConstraintDescriptors(final TableDescriptor p0) throws StandardException;
    
    ConstraintDescriptorList getActiveConstraintDescriptors(final ConstraintDescriptorList p0) throws StandardException;
    
    boolean activeConstraint(final ConstraintDescriptor p0) throws StandardException;
    
    ConstraintDescriptor getConstraintDescriptor(final TableDescriptor p0, final UUID p1) throws StandardException;
    
    ConstraintDescriptor getConstraintDescriptorById(final TableDescriptor p0, final UUID p1) throws StandardException;
    
    ConstraintDescriptor getConstraintDescriptorByName(final TableDescriptor p0, final SchemaDescriptor p1, final String p2, final boolean p3) throws StandardException;
    
    TableDescriptor getConstraintTableDescriptor(final UUID p0) throws StandardException;
    
    ConstraintDescriptorList getForeignKeys(final UUID p0) throws StandardException;
    
    void addConstraintDescriptor(final ConstraintDescriptor p0, final TransactionController p1) throws StandardException;
    
    void dropConstraintDescriptor(final ConstraintDescriptor p0, final TransactionController p1) throws StandardException;
    
    void dropAllConstraintDescriptors(final TableDescriptor p0, final TransactionController p1) throws StandardException;
    
    void updateConstraintDescriptor(final ConstraintDescriptor p0, final UUID p1, final int[] p2, final TransactionController p3) throws StandardException;
    
    SubKeyConstraintDescriptor getSubKeyConstraint(final UUID p0, final int p1) throws StandardException;
    
    SPSDescriptor getSPSDescriptor(final UUID p0) throws StandardException;
    
    SPSDescriptor getSPSDescriptor(final String p0, final SchemaDescriptor p1) throws StandardException;
    
    List getAllSPSDescriptors() throws StandardException;
    
    DataTypeDescriptor[] getSPSParams(final SPSDescriptor p0, final List p1) throws StandardException;
    
    void addSPSDescriptor(final SPSDescriptor p0, final TransactionController p1) throws StandardException;
    
    void updateSPS(final SPSDescriptor p0, final TransactionController p1, final boolean p2) throws StandardException;
    
    void dropSPSDescriptor(final SPSDescriptor p0, final TransactionController p1) throws StandardException;
    
    void dropSPSDescriptor(final UUID p0, final TransactionController p1) throws StandardException;
    
    void invalidateAllSPSPlans(final LanguageConnectionContext p0) throws StandardException;
    
    void invalidateAllSPSPlans() throws StandardException;
    
    TriggerDescriptor getTriggerDescriptor(final UUID p0) throws StandardException;
    
    TriggerDescriptor getTriggerDescriptor(final String p0, final SchemaDescriptor p1) throws StandardException;
    
    String getTriggerActionString(final Visitable p0, final String p1, final String p2, final String p3, final int[] p4, final int[] p5, final int p6, final TableDescriptor p7, final int p8, final boolean p9) throws StandardException;
    
    GenericDescriptorList getTriggerDescriptors(final TableDescriptor p0) throws StandardException;
    
    void updateTriggerDescriptor(final TriggerDescriptor p0, final UUID p1, final int[] p2, final TransactionController p3) throws StandardException;
    
    void dropTriggerDescriptor(final TriggerDescriptor p0, final TransactionController p1) throws StandardException;
    
    Hashtable hashAllConglomerateDescriptorsByNumber(final TransactionController p0) throws StandardException;
    
    Hashtable hashAllTableDescriptorsByTableId(final TransactionController p0) throws StandardException;
    
    ConglomerateDescriptor getConglomerateDescriptor(final UUID p0) throws StandardException;
    
    ConglomerateDescriptor[] getConglomerateDescriptors(final UUID p0) throws StandardException;
    
    ConglomerateDescriptor getConglomerateDescriptor(final long p0) throws StandardException;
    
    ConglomerateDescriptor[] getConglomerateDescriptors(final long p0) throws StandardException;
    
    ConglomerateDescriptor getConglomerateDescriptor(final String p0, final SchemaDescriptor p1, final boolean p2) throws StandardException;
    
    void dropConglomerateDescriptor(final ConglomerateDescriptor p0, final TransactionController p1) throws StandardException;
    
    void dropAllConglomerateDescriptors(final TableDescriptor p0, final TransactionController p1) throws StandardException;
    
    void updateConglomerateDescriptor(final ConglomerateDescriptor[] p0, final long p1, final TransactionController p2) throws StandardException;
    
    void updateConglomerateDescriptor(final ConglomerateDescriptor p0, final long p1, final TransactionController p2) throws StandardException;
    
    List getDependentsDescriptorList(final String p0) throws StandardException;
    
    List getProvidersDescriptorList(final String p0) throws StandardException;
    
    List getAllDependencyDescriptorsList() throws StandardException;
    
    void dropStoredDependency(final DependencyDescriptor p0, final TransactionController p1) throws StandardException;
    
    void dropDependentsStoredDependencies(final UUID p0, final TransactionController p1) throws StandardException;
    
    UUIDFactory getUUIDFactory();
    
    AliasDescriptor getAliasDescriptorForUDT(final TransactionController p0, final DataTypeDescriptor p1) throws StandardException;
    
    AliasDescriptor getAliasDescriptor(final UUID p0) throws StandardException;
    
    AliasDescriptor getAliasDescriptor(final String p0, final String p1, final char p2) throws StandardException;
    
    List getRoutineList(final String p0, final String p1, final char p2) throws StandardException;
    
    void dropAliasDescriptor(final AliasDescriptor p0, final TransactionController p1) throws StandardException;
    
    void updateUser(final UserDescriptor p0, final TransactionController p1) throws StandardException;
    
    UserDescriptor getUser(final String p0) throws StandardException;
    
    void dropUser(final String p0, final TransactionController p1) throws StandardException;
    
    int getEngineType();
    
    FileInfoDescriptor getFileInfoDescriptor(final UUID p0) throws StandardException;
    
    FileInfoDescriptor getFileInfoDescriptor(final SchemaDescriptor p0, final String p1) throws StandardException;
    
    void dropFileInfoDescriptor(final FileInfoDescriptor p0) throws StandardException;
    
    RowLocation[] computeAutoincRowLocations(final TransactionController p0, final TableDescriptor p1) throws StandardException;
    
    RowLocation getRowLocationTemplate(final LanguageConnectionContext p0, final TableDescriptor p1) throws StandardException;
    
    NumberDataValue getSetAutoincrementValue(final RowLocation p0, final TransactionController p1, final boolean p2, final NumberDataValue p3, final boolean p4) throws StandardException;
    
    void setAutoincrementValue(final TransactionController p0, final UUID p1, final String p2, final long p3, final boolean p4) throws StandardException;
    
    void getCurrentValueAndAdvance(final String p0, final NumberDataValue p1) throws StandardException;
    
    Long peekAtSequence(final String p0, final String p1) throws StandardException;
    
    List getStatisticsDescriptors(final TableDescriptor p0) throws StandardException;
    
    void dropStatisticsDescriptors(final UUID p0, final UUID p1, final TransactionController p2) throws StandardException;
    
    DependencyManager getDependencyManager();
    
    int getCacheMode();
    
    String getSystemSQLName();
    
    void addDescriptor(final TupleDescriptor p0, final TupleDescriptor p1, final int p2, final boolean p3, final TransactionController p4) throws StandardException;
    
    void addDescriptorArray(final TupleDescriptor[] p0, final TupleDescriptor p1, final int p2, final boolean p3, final TransactionController p4) throws StandardException;
    
    boolean checkVersion(final int p0, final String p1) throws StandardException;
    
    boolean isReadOnlyUpgrade();
    
    boolean addRemovePermissionsDescriptor(final boolean p0, final PermissionsDescriptor p1, final String p2, final TransactionController p3) throws StandardException;
    
    TablePermsDescriptor getTablePermissions(final UUID p0, final String p1) throws StandardException;
    
    TablePermsDescriptor getTablePermissions(final UUID p0) throws StandardException;
    
    ColPermsDescriptor getColumnPermissions(final UUID p0, final int p1, final boolean p2, final String p3) throws StandardException;
    
    ColPermsDescriptor getColumnPermissions(final UUID p0, final String p1, final boolean p2, final String p3) throws StandardException;
    
    ColPermsDescriptor getColumnPermissions(final UUID p0) throws StandardException;
    
    RoutinePermsDescriptor getRoutinePermissions(final UUID p0, final String p1) throws StandardException;
    
    RoutinePermsDescriptor getRoutinePermissions(final UUID p0) throws StandardException;
    
    String getVTIClass(final TableDescriptor p0, final boolean p1) throws StandardException;
    
    String getBuiltinVTIClass(final TableDescriptor p0, final boolean p1) throws StandardException;
    
    RoleGrantDescriptor getRoleDefinitionDescriptor(final String p0) throws StandardException;
    
    RoleGrantDescriptor getRoleGrantDescriptor(final UUID p0) throws StandardException;
    
    RoleGrantDescriptor getRoleGrantDescriptor(final String p0, final String p1, final String p2) throws StandardException;
    
    void dropDependentsStoredDependencies(final UUID p0, final TransactionController p1, final boolean p2) throws StandardException;
    
    boolean existsGrantToAuthid(final String p0, final TransactionController p1) throws StandardException;
    
    void updateMetadataSPSes(final TransactionController p0) throws StandardException;
    
    void dropSequenceDescriptor(final SequenceDescriptor p0, final TransactionController p1) throws StandardException;
    
    SequenceDescriptor getSequenceDescriptor(final UUID p0) throws StandardException;
    
    SequenceDescriptor getSequenceDescriptor(final SchemaDescriptor p0, final String p1) throws StandardException;
    
    PermDescriptor getGenericPermissions(final UUID p0, final String p1, final String p2, final String p3) throws StandardException;
    
    PermDescriptor getGenericPermissions(final UUID p0) throws StandardException;
    
    void dropAllPermDescriptors(final UUID p0, final TransactionController p1) throws StandardException;
    
    boolean doCreateIndexStatsRefresher();
    
    void createIndexStatsRefresher(final Database p0, final String p1);
    
    IndexStatisticsDaemon getIndexStatsRefresher(final boolean p0);
    
    void disableIndexStatsRefresher();
    
    DependableFinder getDependableFinder(final int p0);
    
    DependableFinder getColumnDependableFinder(final int p0, final byte[] p1);
}
