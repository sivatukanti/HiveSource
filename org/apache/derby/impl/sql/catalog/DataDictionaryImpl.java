// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.impl.services.daemon.IndexStatisticsDaemonImpl;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.sql.dictionary.PermDescriptor;
import org.apache.derby.iapi.sql.dictionary.TablePermsDescriptor;
import java.io.InputStream;
import java.io.IOException;
import java.security.AccessController;
import org.apache.derby.iapi.sql.dictionary.RoutinePermsDescriptor;
import java.util.Enumeration;
import org.apache.derby.iapi.sql.dictionary.SequenceDescriptor;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.NumberDataValue;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.services.io.Storable;
import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.store.access.FileResource;
import java.io.File;
import org.apache.derby.impl.sql.execute.JarUtil;
import org.apache.derby.iapi.sql.dictionary.UserDescriptor;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.catalog.types.BaseTypeIdImpl;
import org.apache.derby.iapi.sql.execute.TupleFilter;
import org.apache.derby.iapi.sql.dictionary.DependencyDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptorList;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.SubCheckConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ForeignKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.KeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.SubKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.SubConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.impl.sql.compile.TableName;
import org.apache.derby.impl.sql.compile.ColumnReference;
import java.util.Collections;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.impl.sql.compile.CollectNodesVisitor;
import java.util.Arrays;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.dictionary.GenericDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.catalog.DefaultInfo;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.SPSDescriptor;
import org.apache.derby.iapi.sql.dictionary.FileInfoDescriptor;
import org.apache.derby.iapi.sql.dictionary.ViewDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.dictionary.RoleClosureIterator;
import java.util.LinkedList;
import org.apache.derby.iapi.sql.dictionary.RoleGrantDescriptor;
import java.util.Iterator;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColPermsDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.DefaultDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.catalog.AliasInfo;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.types.SQLChar;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.sql.execute.ScanQualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.catalog.UUID;
import java.security.SecureRandom;
import org.apache.derby.iapi.sql.dictionary.PasswordHasher;
import java.util.Dictionary;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.ShExQual;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.impl.sql.depend.BasicDependencyManager;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.property.PersistentSet;
import java.util.HashSet;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.cache.CacheFactory;
import java.io.Serializable;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.sql.conn.LanguageConnectionFactory;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.services.locks.ShExLockable;
import java.util.Hashtable;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.store.access.TransactionController;
import java.util.Properties;
import org.apache.derby.iapi.services.daemon.IndexStatisticsDaemon;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import java.security.PrivilegedAction;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

public final class DataDictionaryImpl implements DataDictionary, CacheableFactory, ModuleControl, ModuleSupportable, PrivilegedAction
{
    private static final String CFG_SYSTABLES_ID = "SystablesIdentifier";
    private static final String CFG_SYSTABLES_INDEX1_ID = "SystablesIndex1Identifier";
    private static final String CFG_SYSTABLES_INDEX2_ID = "SystablesIndex2Identifier";
    private static final String CFG_SYSCOLUMNS_ID = "SyscolumnsIdentifier";
    private static final String CFG_SYSCOLUMNS_INDEX1_ID = "SyscolumnsIndex1Identifier";
    private static final String CFG_SYSCOLUMNS_INDEX2_ID = "SyscolumnsIndex2Identifier";
    private static final String CFG_SYSCONGLOMERATES_ID = "SysconglomeratesIdentifier";
    private static final String CFG_SYSCONGLOMERATES_INDEX1_ID = "SysconglomeratesIndex1Identifier";
    private static final String CFG_SYSCONGLOMERATES_INDEX2_ID = "SysconglomeratesIndex2Identifier";
    private static final String CFG_SYSCONGLOMERATES_INDEX3_ID = "SysconglomeratesIndex3Identifier";
    private static final String CFG_SYSSCHEMAS_ID = "SysschemasIdentifier";
    private static final String CFG_SYSSCHEMAS_INDEX1_ID = "SysschemasIndex1Identifier";
    private static final String CFG_SYSSCHEMAS_INDEX2_ID = "SysschemasIndex2Identifier";
    private static final int SYSCONGLOMERATES_CORE_NUM = 0;
    private static final int SYSTABLES_CORE_NUM = 1;
    private static final int SYSCOLUMNS_CORE_NUM = 2;
    private static final int SYSSCHEMAS_CORE_NUM = 3;
    private static final int NUM_CORE = 4;
    private static final String[][] SYSFUN_FUNCTIONS;
    private static final int SYSFUN_DETERMINISTIC_INDEX = 4;
    private static final int SYSFUN_VARARGS_INDEX = 5;
    private static final int SYSFUN_FIRST_PARAMETER_INDEX = 6;
    private final AliasDescriptor[] sysfunDescriptors;
    private TabInfoImpl[] coreInfo;
    private SchemaDescriptor systemSchemaDesc;
    private SchemaDescriptor sysIBMSchemaDesc;
    private SchemaDescriptor declaredGlobalTemporaryTablesSchemaDesc;
    private SchemaDescriptor systemUtilSchemaDesc;
    private static final String[] nonCoreNames;
    private static final int NUM_NONCORE;
    private static final String[] systemSchemaNames;
    private DD_Version dictionaryVersion;
    private DD_Version softwareVersion;
    private String authorizationDatabaseOwner;
    private boolean usesSqlAuthorization;
    private TabInfoImpl[] noncoreInfo;
    public DataDescriptorGenerator dataDescriptorGenerator;
    private DataValueFactory dvf;
    AccessFactory af;
    private ExecutionFactory exFactory;
    protected UUIDFactory uuidFactory;
    private IndexStatisticsDaemon indexRefresher;
    Properties startupParameters;
    int engineType;
    protected boolean booting;
    private TransactionController bootingTC;
    protected DependencyManager dmgr;
    CacheManager OIDTdCache;
    CacheManager nameTdCache;
    private CacheManager spsNameCache;
    private CacheManager sequenceGeneratorCache;
    private Hashtable spsIdHash;
    int tdCacheSize;
    int stmtCacheSize;
    private int seqgenCacheSize;
    CacheManager permissionsCache;
    int permissionsCacheSize;
    ShExLockable cacheCoordinator;
    public LockFactory lockFactory;
    volatile int cacheMode;
    volatile int ddlUsers;
    volatile int readersInDDLMode;
    private HashMap sequenceIDs;
    private boolean readOnlyUpgrade;
    private boolean indexStatsUpdateDisabled;
    private boolean indexStatsUpdateLogging;
    private String indexStatsUpdateTracing;
    private int systemSQLNameNumber;
    private GregorianCalendar calendarForLastSystemSQLName;
    private long timeForLastSystemSQLName;
    private static final String[] sysUtilProceduresWithPublicAccess;
    private static final String[] sysUtilFunctionsWithPublicAccess;
    private int collationTypeOfSystemSchemas;
    private int collationTypeOfUserSchemas;
    static final int DROP = 0;
    static final int EXISTS = 1;
    private static final Comparator OFFSET_COMPARATOR;
    private String spsSet;
    private static final String[] colPrivTypeMap;
    private static final String[] colPrivTypeMapForGrant;
    private String[][] DIAG_VTI_TABLE_CLASSES;
    private String[][] DIAG_VTI_TABLE_FUNCTION_CLASSES;
    
    public DataDictionaryImpl() {
        this.sysfunDescriptors = new AliasDescriptor[DataDictionaryImpl.SYSFUN_FUNCTIONS.length];
        this.cacheMode = 0;
        this.calendarForLastSystemSQLName = new GregorianCalendar();
        this.DIAG_VTI_TABLE_CLASSES = new String[][] { { "LOCK_TABLE", "org.apache.derby.diag.LockTable" }, { "STATEMENT_CACHE", "org.apache.derby.diag.StatementCache" }, { "TRANSACTION_TABLE", "org.apache.derby.diag.TransactionTable" }, { "ERROR_MESSAGES", "org.apache.derby.diag.ErrorMessages" } };
        this.DIAG_VTI_TABLE_FUNCTION_CLASSES = new String[][] { { "SPACE_TABLE", "org.apache.derby.diag.SpaceTable" }, { "ERROR_LOG_READER", "org.apache.derby.diag.ErrorLogReader" }, { "STATEMENT_DURATION", "org.apache.derby.diag.StatementDuration" }, { "CONTAINED_ROLES", "org.apache.derby.diag.ContainedRoles" } };
    }
    
    public boolean canSupport(final Properties properties) {
        return Monitor.isDesiredType(properties, 2);
    }
    
    public void boot(final boolean b, final Properties startupParameters) throws StandardException {
        this.softwareVersion = new DD_Version(this, 220);
        this.startupParameters = startupParameters;
        this.uuidFactory = Monitor.getMonitor().getUUIDFactory();
        this.engineType = Monitor.getEngineType(startupParameters);
        this.collationTypeOfSystemSchemas = 0;
        this.getBuiltinSystemSchemas();
        this.dvf = ((LanguageConnectionFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.sql.conn.LanguageConnectionFactory", startupParameters)).getDataValueFactory();
        this.exFactory = (ExecutionFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.sql.execute.ExecutionFactory", startupParameters);
        this.initializeCatalogInfo();
        this.booting = true;
        if (this.dataDescriptorGenerator == null) {
            this.dataDescriptorGenerator = new DataDescriptorGenerator(this);
        }
        if (!b) {
            this.coreInfo[1].setHeapConglomerate(this.getBootParameter(startupParameters, "SystablesIdentifier", true));
            this.coreInfo[1].setIndexConglomerate(0, this.getBootParameter(startupParameters, "SystablesIndex1Identifier", true));
            this.coreInfo[1].setIndexConglomerate(1, this.getBootParameter(startupParameters, "SystablesIndex2Identifier", true));
            this.coreInfo[2].setHeapConglomerate(this.getBootParameter(startupParameters, "SyscolumnsIdentifier", true));
            this.coreInfo[2].setIndexConglomerate(0, this.getBootParameter(startupParameters, "SyscolumnsIndex1Identifier", true));
            this.coreInfo[2].setIndexConglomerate(1, this.getBootParameter(startupParameters, "SyscolumnsIndex2Identifier", false));
            this.coreInfo[0].setHeapConglomerate(this.getBootParameter(startupParameters, "SysconglomeratesIdentifier", true));
            this.coreInfo[0].setIndexConglomerate(0, this.getBootParameter(startupParameters, "SysconglomeratesIndex1Identifier", true));
            this.coreInfo[0].setIndexConglomerate(1, this.getBootParameter(startupParameters, "SysconglomeratesIndex2Identifier", true));
            this.coreInfo[0].setIndexConglomerate(2, this.getBootParameter(startupParameters, "SysconglomeratesIndex3Identifier", true));
            this.coreInfo[3].setHeapConglomerate(this.getBootParameter(startupParameters, "SysschemasIdentifier", true));
            this.coreInfo[3].setIndexConglomerate(0, this.getBootParameter(startupParameters, "SysschemasIndex1Identifier", true));
            this.coreInfo[3].setIndexConglomerate(1, this.getBootParameter(startupParameters, "SysschemasIndex2Identifier", true));
        }
        this.tdCacheSize = PropertyUtil.intPropertyValue("derby.language.tableDescriptorCacheSize", startupParameters.getProperty("derby.language.tableDescriptorCacheSize"), 0, Integer.MAX_VALUE, 64);
        this.stmtCacheSize = PropertyUtil.intPropertyValue("derby.language.spsCacheSize", startupParameters.getProperty("derby.language.spsCacheSize"), 0, Integer.MAX_VALUE, 32);
        this.seqgenCacheSize = PropertyUtil.intPropertyValue("derby.language.sequenceGeneratorCacheSize", startupParameters.getProperty("derby.language.sequenceGeneratorCacheSize"), 0, Integer.MAX_VALUE, 32);
        this.permissionsCacheSize = PropertyUtil.intPropertyValue("derby.language.permissionsCacheSize", startupParameters.getProperty("derby.language.permissionsCacheSize"), 0, Integer.MAX_VALUE, 64);
        this.indexStatsUpdateDisabled = !PropertyUtil.getSystemBoolean("derby.storage.indexStats.auto", true);
        this.indexStatsUpdateLogging = PropertyUtil.getSystemBoolean("derby.storage.indexStats.log");
        this.indexStatsUpdateTracing = PropertyUtil.getSystemProperty("derby.storage.indexStats.trace", "off");
        final CacheFactory cacheFactory = (CacheFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.cache.CacheFactory");
        this.OIDTdCache = cacheFactory.newCacheManager(this, "TableDescriptorOIDCache", this.tdCacheSize, this.tdCacheSize);
        this.nameTdCache = cacheFactory.newCacheManager(this, "TableDescriptorNameCache", this.tdCacheSize, this.tdCacheSize);
        if (this.stmtCacheSize > 0) {
            this.spsNameCache = cacheFactory.newCacheManager(this, "SPSNameDescriptorCache", this.stmtCacheSize, this.stmtCacheSize);
            this.spsIdHash = new Hashtable(this.stmtCacheSize);
        }
        this.sequenceGeneratorCache = cacheFactory.newCacheManager(this, "SequenceGeneratorCache", this.seqgenCacheSize, this.seqgenCacheSize);
        this.sequenceIDs = new HashMap();
        this.cacheCoordinator = new ShExLockable();
        this.af = (AccessFactory)Monitor.findServiceModule(this, "org.apache.derby.iapi.store.access.AccessFactory");
        this.lockFactory = this.af.getLockFactory();
        final ContextManager currentContextManager = ContextService.getFactory().getCurrentContextManager();
        this.bootingTC = null;
        try {
            this.bootingTC = this.af.getTransaction(currentContextManager);
            this.exFactory.newExecutionContext(currentContextManager);
            final DataDescriptorGenerator dataDescriptorGenerator = this.getDataDescriptorGenerator();
            String s;
            if (b) {
                s = startupParameters.getProperty("collation", "UCS_BASIC");
                this.bootingTC.setProperty("derby.database.collation", s, true);
            }
            else {
                s = startupParameters.getProperty("derby.database.collation", "UCS_BASIC");
            }
            this.collationTypeOfUserSchemas = DataTypeDescriptor.getCollationType(s);
            this.declaredGlobalTemporaryTablesSchemaDesc = this.newDeclaredGlobalTemporaryTablesSchemaDesc("SESSION");
            final boolean nativeAuthenticationEnabled = PropertyUtil.nativeAuthenticationEnabled(startupParameters);
            if (b) {
                this.authorizationDatabaseOwner = IdUtil.getUserAuthorizationId(IdUtil.getUserNameFromURLProps(startupParameters));
                final HashSet set = new HashSet();
                this.createDictionaryTables(startupParameters, this.bootingTC, dataDescriptorGenerator);
                this.create_SYSIBM_procedures(this.bootingTC, set);
                this.createSystemSps(this.bootingTC);
                this.create_SYSCS_procedures(this.bootingTC, set);
                this.grantPublicAccessToSystemRoutines(set, this.bootingTC, this.authorizationDatabaseOwner);
                this.dictionaryVersion = this.softwareVersion;
                this.bootingTC.setProperty("DataDictionaryVersion", this.dictionaryVersion, true);
                this.bootingTC.setProperty("CreateDataDictionaryVersion", this.dictionaryVersion, true);
                if (PropertyUtil.getSystemBoolean("derby.database.sqlAuthorization")) {
                    this.bootingTC.setProperty("derby.database.sqlAuthorization", "true", true);
                }
                if (PropertyUtil.getSystemBoolean("derby.database.sqlAuthorization") || nativeAuthenticationEnabled) {
                    this.usesSqlAuthorization = true;
                }
                this.bootingTC.setProperty("derby.authentication.builtin.algorithm", this.findDefaultBuiltinAlgorithm(), false);
            }
            else {
                this.loadDictionaryTables(this.bootingTC, dataDescriptorGenerator, startupParameters);
                final String databaseProperty = PropertyUtil.getDatabaseProperty(this.bootingTC, "derby.storage.indexStats.auto");
                if (databaseProperty != null) {
                    this.indexStatsUpdateDisabled = !Boolean.valueOf(databaseProperty);
                }
                final String databaseProperty2 = PropertyUtil.getDatabaseProperty(this.bootingTC, "derby.storage.indexStats.log");
                if (databaseProperty2 != null) {
                    this.indexStatsUpdateLogging = Boolean.valueOf(databaseProperty2);
                }
                final String databaseProperty3 = PropertyUtil.getDatabaseProperty(this.bootingTC, "derby.storage.indexStats.trace");
                if (databaseProperty3 != null) {
                    if (!databaseProperty3.equalsIgnoreCase("off") && !databaseProperty3.equalsIgnoreCase("log") && !databaseProperty3.equalsIgnoreCase("stdout") && !databaseProperty3.equalsIgnoreCase("both")) {
                        this.indexStatsUpdateTracing = "off";
                    }
                    else {
                        this.indexStatsUpdateTracing = databaseProperty3;
                    }
                }
                final String databaseProperty4 = PropertyUtil.getDatabaseProperty(this.bootingTC, "derby.database.sqlAuthorization");
                if (Boolean.valueOf(startupParameters.getProperty("softUpgradeNoFeatureCheck"))) {
                    if (this.dictionaryVersion.majorVersionNumber >= 140) {
                        this.usesSqlAuthorization = (Boolean.valueOf(databaseProperty4) || nativeAuthenticationEnabled);
                    }
                }
                else if (Boolean.valueOf(databaseProperty4) || nativeAuthenticationEnabled) {
                    this.checkVersion(140, "sqlAuthorization");
                    this.usesSqlAuthorization = true;
                }
            }
            this.bootingTC.commit();
            currentContextManager.getContext("ExecutionContext").popMe();
        }
        finally {
            if (this.bootingTC != null) {
                this.bootingTC.destroy();
                this.bootingTC = null;
            }
        }
        this.setDependencyManager();
        this.booting = false;
    }
    
    private String findDefaultBuiltinAlgorithm() {
        try {
            MessageDigest.getInstance("SHA-256");
            return "SHA-256";
        }
        catch (NoSuchAlgorithmException ex) {
            return "SHA-1";
        }
    }
    
    private CacheManager getPermissionsCache() throws StandardException {
        if (this.permissionsCache == null) {
            final CacheFactory cacheFactory = (CacheFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.cache.CacheFactory");
            this.permissionsCacheSize = PropertyUtil.getServiceInt(getLCC().getTransactionExecute(), "derby.language.permissionsCacheSize", 40, Integer.MAX_VALUE, this.permissionsCacheSize);
            this.permissionsCache = cacheFactory.newCacheManager(this, "PermissionsCache", this.permissionsCacheSize, this.permissionsCacheSize);
        }
        return this.permissionsCache;
    }
    
    protected void setDependencyManager() {
        this.dmgr = new BasicDependencyManager(this);
    }
    
    public DependencyManager getDependencyManager() {
        return this.dmgr;
    }
    
    public void stop() {
        if (this.indexRefresher != null) {
            this.indexRefresher.stop();
        }
    }
    
    public Cacheable newCacheable(final CacheManager cacheManager) {
        if (cacheManager == this.OIDTdCache) {
            return new OIDTDCacheable(this);
        }
        if (cacheManager == this.nameTdCache) {
            return new NameTDCacheable(this);
        }
        if (cacheManager == this.permissionsCache) {
            return new PermissionsCacheable(this);
        }
        if (cacheManager == this.sequenceGeneratorCache) {
            return new SequenceUpdater.SyssequenceUpdater(this);
        }
        return new SPSNameCacheable(this);
    }
    
    public int startReading(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final int incrementBindCount = languageConnectionContext.incrementBindCount();
        int i = 0;
        int cacheMode;
        do {
            if (i != 0) {
                try {
                    this.lockFactory.zeroDurationlockObject(languageConnectionContext.getTransactionExecute().getLockSpace(), this.cacheCoordinator, ShExQual.SH, -1);
                }
                catch (StandardException ex) {
                    languageConnectionContext.decrementBindCount();
                    throw ex;
                }
                i = 0;
            }
            synchronized (this) {
                cacheMode = this.getCacheMode();
                if (incrementBindCount != 1) {
                    continue;
                }
                if (cacheMode == 0) {
                    boolean lockObject;
                    try {
                        final CompatibilitySpace lockSpace = languageConnectionContext.getTransactionExecute().getLockSpace();
                        lockObject = this.lockFactory.lockObject(lockSpace, lockSpace.getOwner(), this.cacheCoordinator, ShExQual.SH, 0);
                    }
                    catch (StandardException ex2) {
                        languageConnectionContext.decrementBindCount();
                        throw ex2;
                    }
                    if (lockObject) {
                        continue;
                    }
                    i = 1;
                }
                else {
                    ++this.readersInDDLMode;
                }
            }
        } while (i != 0);
        return cacheMode;
    }
    
    public void doneReading(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final int decrementBindCount = languageConnectionContext.decrementBindCount();
        synchronized (this) {
            if (decrementBindCount == 0) {
                if (n == 0) {
                    if (languageConnectionContext.getStatementContext() != null && languageConnectionContext.getStatementContext().inUse()) {
                        final CompatibilitySpace lockSpace = languageConnectionContext.getTransactionExecute().getLockSpace();
                        this.lockFactory.unlock(lockSpace, lockSpace.getOwner(), this.cacheCoordinator, ShExQual.SH);
                    }
                }
                else {
                    --this.readersInDDLMode;
                    if (this.ddlUsers == 0 && this.readersInDDLMode == 0) {
                        this.clearCaches();
                        this.setCacheMode(0);
                    }
                }
            }
        }
    }
    
    public void startWriting(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        int i = 1;
        if (languageConnectionContext.getBindCount() != 0) {
            throw StandardException.newException("XCL21.S");
        }
        if (!languageConnectionContext.dataDictionaryInWriteMode()) {
            int n = 0;
            while (i != 0) {
                if (n > 4 && this.getCacheMode() == 0) {
                    this.lockFactory.zeroDurationlockObject(languageConnectionContext.getTransactionExecute().getLockSpace(), this.cacheCoordinator, ShExQual.EX, -2);
                    n = 1;
                }
                if (n > 0) {
                    try {
                        Thread.sleep((long)(Math.random() * 1131.0 % 20.0));
                    }
                    catch (InterruptedException ex) {
                        throw StandardException.interrupt(ex);
                    }
                }
                Label_0201: {
                    synchronized (this) {
                        if (this.getCacheMode() == 0) {
                            if (!this.lockFactory.zeroDurationlockObject(languageConnectionContext.getTransactionExecute().getLockSpace(), this.cacheCoordinator, ShExQual.EX, 0)) {
                                break Label_0201;
                            }
                            this.setCacheMode(1);
                            this.clearCaches(false);
                        }
                        ++this.ddlUsers;
                    }
                    languageConnectionContext.setDataDictionaryWriteMode();
                    i = 0;
                }
                ++n;
            }
        }
    }
    
    public void transactionFinished() throws StandardException {
        synchronized (this) {
            --this.ddlUsers;
            if (this.ddlUsers == 0 && this.readersInDDLMode == 0) {
                this.clearCaches();
                this.setCacheMode(0);
            }
        }
    }
    
    public int getCacheMode() {
        return this.cacheMode;
    }
    
    private void setCacheMode(final int cacheMode) {
        this.cacheMode = cacheMode;
    }
    
    public DataDescriptorGenerator getDataDescriptorGenerator() {
        return this.dataDescriptorGenerator;
    }
    
    public String getAuthorizationDatabaseOwner() {
        return this.authorizationDatabaseOwner;
    }
    
    public boolean usesSqlAuthorization() {
        return this.usesSqlAuthorization;
    }
    
    public int getCollationTypeOfSystemSchemas() {
        return this.collationTypeOfSystemSchemas;
    }
    
    public int getCollationTypeOfUserSchemas() {
        return this.collationTypeOfUserSchemas;
    }
    
    public DataValueFactory getDataValueFactory() {
        return this.dvf;
    }
    
    public ExecutionFactory getExecutionFactory() {
        return this.exFactory;
    }
    
    private void getBuiltinSystemSchemas() {
        if (this.systemSchemaDesc != null) {
            return;
        }
        this.systemSchemaDesc = this.newSystemSchemaDesc("SYS", "8000000d-00d0-fd77-3ed8-000a0a0b1900");
        this.sysIBMSchemaDesc = this.newSystemSchemaDesc("SYSIBM", "c013800d-00f8-5b53-28a9-00000019ed88");
        this.systemUtilSchemaDesc = this.newSystemSchemaDesc("SYSCS_UTIL", "c013800d-00fb-2649-07ec-000000134f30");
    }
    
    public PasswordHasher makePasswordHasher(final Dictionary dictionary) throws StandardException {
        final boolean checkVersion = this.checkVersion(180, null);
        final boolean checkVersion2 = this.checkVersion(210, null);
        if (!checkVersion) {
            return null;
        }
        final String s = (String)PropertyUtil.getPropertyFromSet(dictionary, "derby.authentication.builtin.algorithm");
        if (s == null) {
            return null;
        }
        byte[] generateRandomSalt = null;
        int intProperty = 1;
        if (s != null && s.length() > 0 && checkVersion2) {
            generateRandomSalt = this.generateRandomSalt(dictionary);
            intProperty = this.getIntProperty(dictionary, "derby.authentication.builtin.iterations", 1000, 1, Integer.MAX_VALUE);
        }
        return new PasswordHasher(s, generateRandomSalt, intProperty);
    }
    
    private byte[] generateRandomSalt(final Dictionary dictionary) {
        final int intProperty = this.getIntProperty(dictionary, "derby.authentication.builtin.saltLength", 16, 0, Integer.MAX_VALUE);
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] bytes = new byte[intProperty];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
    
    private int getIntProperty(final Dictionary dictionary, final String s, final int n, final int n2, final int n3) {
        final String s2 = (String)PropertyUtil.getPropertyFromSet(dictionary, s);
        if (s2 != null) {
            try {
                final int int1 = Integer.parseInt(s2);
                if (int1 >= n2 && int1 <= n3) {
                    return int1;
                }
            }
            catch (NumberFormatException ex) {}
        }
        return n;
    }
    
    public SchemaDescriptor getSystemSchemaDescriptor() throws StandardException {
        return this.systemSchemaDesc;
    }
    
    public SchemaDescriptor getSystemUtilSchemaDescriptor() throws StandardException {
        return this.systemUtilSchemaDesc;
    }
    
    public SchemaDescriptor getSysIBMSchemaDescriptor() throws StandardException {
        return this.sysIBMSchemaDesc;
    }
    
    public SchemaDescriptor getDeclaredGlobalTemporaryTablesSchemaDescriptor() throws StandardException {
        return this.declaredGlobalTemporaryTablesSchemaDesc;
    }
    
    public boolean isSystemSchemaName(final String anObject) throws StandardException {
        boolean equals = false;
        int n = DataDictionaryImpl.systemSchemaNames.length - 1;
        while (n >= 0 && !(equals = DataDictionaryImpl.systemSchemaNames[n--].equals(anObject))) {}
        return equals;
    }
    
    public SchemaDescriptor getSchemaDescriptor(final String anObject, TransactionController transactionCompile, final boolean b) throws StandardException {
        if (transactionCompile == null) {
            transactionCompile = this.getTransactionCompile();
        }
        if (this.getSystemSchemaDescriptor().getSchemaName().equals(anObject)) {
            return this.getSystemSchemaDescriptor();
        }
        if (this.getSysIBMSchemaDescriptor().getSchemaName().equals(anObject) && this.dictionaryVersion.checkVersion(100, null)) {
            return this.getSysIBMSchemaDescriptor();
        }
        final SchemaDescriptor locateSchemaRow = this.locateSchemaRow(anObject, transactionCompile);
        if (locateSchemaRow == null && this.getDeclaredGlobalTemporaryTablesSchemaDescriptor().getSchemaName().equals(anObject)) {
            return this.getDeclaredGlobalTemporaryTablesSchemaDescriptor();
        }
        if (locateSchemaRow == null && b) {
            throw StandardException.newException("42Y07", anObject);
        }
        return locateSchemaRow;
    }
    
    private SchemaDescriptor locateSchemaRow(final UUID uuid, final TransactionController transactionController) throws StandardException {
        return this.locateSchemaRowBody(uuid, 4, transactionController);
    }
    
    private SchemaDescriptor locateSchemaRow(final UUID uuid, final int n, final TransactionController transactionController) throws StandardException {
        return this.locateSchemaRowBody(uuid, n, transactionController);
    }
    
    private SchemaDescriptor locateSchemaRowBody(final UUID uuid, final int n, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[3];
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return (SchemaDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, tabInfoImpl, null, null, false, n, transactionController);
    }
    
    private SchemaDescriptor locateSchemaRow(final String s, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[3];
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, sqlVarchar);
        return (SchemaDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, tabInfoImpl, null, null, false, 4, transactionController);
    }
    
    public SchemaDescriptor getSchemaDescriptor(final UUID uuid, final TransactionController transactionController) throws StandardException {
        return this.getSchemaDescriptorBody(uuid, 4, transactionController);
    }
    
    public SchemaDescriptor getSchemaDescriptor(final UUID uuid, final int n, final TransactionController transactionController) throws StandardException {
        return this.getSchemaDescriptorBody(uuid, n, transactionController);
    }
    
    private SchemaDescriptor getSchemaDescriptorBody(final UUID uuid, final int n, TransactionController transactionCompile) throws StandardException {
        if (transactionCompile == null) {
            transactionCompile = this.getTransactionCompile();
        }
        if (uuid != null) {
            if (this.getSystemSchemaDescriptor().getUUID().equals(uuid)) {
                return this.getSystemSchemaDescriptor();
            }
            if (this.getSysIBMSchemaDescriptor().getUUID().equals(uuid)) {
                return this.getSysIBMSchemaDescriptor();
            }
        }
        if (!this.booting) {
            final LanguageConnectionContext lcc = getLCC();
            if (lcc != null) {
                final SchemaDescriptor defaultSchema = lcc.getDefaultSchema();
                if (defaultSchema != null && (uuid == null || uuid.equals(defaultSchema.getUUID()))) {
                    return defaultSchema;
                }
            }
        }
        return this.locateSchemaRow(uuid, n, transactionCompile);
    }
    
    public boolean existsSchemaOwnedBy(final String s, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[3];
        final SYSSCHEMASRowFactory sysschemasRowFactory = (SYSSCHEMASRowFactory)tabInfoImpl.getCatalogRowFactory();
        final ConglomerateController openConglomerate = transactionController.openConglomerate(tabInfoImpl.getHeapConglomerate(), false, 0, 6, 4);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final ScanQualifier[][] scanQualifier = this.exFactory.getScanQualifier(1);
        scanQualifier[0][0].setQualifier(2, sqlVarchar, 2, false, false, false);
        final ScanController openScan = transactionController.openScan(tabInfoImpl.getHeapConglomerate(), false, 0, 6, 4, null, null, 0, scanQualifier, null, 0);
        boolean b = false;
        try {
            if (openScan.fetchNext(sysschemasRowFactory.makeEmptyRow().getRowArray())) {
                b = true;
            }
        }
        finally {
            if (openScan != null) {
                openScan.close();
            }
            if (openConglomerate != null) {
                openConglomerate.close();
            }
        }
        return b;
    }
    
    public void addDescriptor(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2, final int n, final boolean b, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = (n < 4) ? this.coreInfo[n] : this.getNonCoreTI(n);
        final int insertRow = tabInfoImpl.insertRow(tabInfoImpl.getCatalogRowFactory().makeRow(tupleDescriptor, tupleDescriptor2), transactionController);
        if (!b && insertRow != -1) {
            throw this.duplicateDescriptorException(tupleDescriptor, tupleDescriptor2);
        }
    }
    
    private StandardException duplicateDescriptorException(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) {
        if (tupleDescriptor2 != null) {
            return StandardException.newException("X0Y32.S", tupleDescriptor.getDescriptorType(), tupleDescriptor.getDescriptorName(), tupleDescriptor2.getDescriptorType(), tupleDescriptor2.getDescriptorName());
        }
        return StandardException.newException("X0Y68.S", tupleDescriptor.getDescriptorType(), tupleDescriptor.getDescriptorName());
    }
    
    public void addDescriptorArray(final TupleDescriptor[] array, final TupleDescriptor tupleDescriptor, final int n, final boolean b, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = (n < 4) ? this.coreInfo[n] : this.getNonCoreTI(n);
        final CatalogRowFactory catalogRowFactory = tabInfoImpl.getCatalogRowFactory();
        final ExecRow[] array2 = new ExecRow[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = catalogRowFactory.makeRow(array[i], tupleDescriptor);
        }
        final int insertRowList = tabInfoImpl.insertRowList(array2, transactionController);
        if (!b && insertRowList != -1) {
            throw this.duplicateDescriptorException(array[insertRowList], tupleDescriptor);
        }
    }
    
    public void dropRoleGrant(final String s, final String s2, final String s3, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(19);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLVarchar sqlVarchar2 = new SQLVarchar(s2);
        final SQLVarchar sqlVarchar3 = new SQLVarchar(s3);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(3);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, sqlVarchar2);
        indexableRow.setColumn(3, sqlVarchar3);
        nonCoreTI.deleteRow(transactionController, indexableRow, 0);
    }
    
    public void dropSchemaDescriptor(final String s, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[3];
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, sqlVarchar);
        tabInfoImpl.deleteRow(transactionController, indexableRow, 0);
    }
    
    public TableDescriptor getTableDescriptor(final String s, final SchemaDescriptor schemaDescriptor, final TransactionController transactionController) throws StandardException {
        TableDescriptor tableDescriptor = null;
        final SchemaDescriptor schemaDescriptor2 = (schemaDescriptor == null) ? this.getSystemSchemaDescriptor() : schemaDescriptor;
        final UUID uuid = schemaDescriptor2.getUUID();
        if ("SYSCS_DIAG".equals(schemaDescriptor2.getSchemaName())) {
            final TableDescriptor tableDescriptor2 = new TableDescriptor(this, s, schemaDescriptor2, 5, 'R');
            if (this.getVTIClass(tableDescriptor2, false) != null) {
                return tableDescriptor2;
            }
        }
        final TableKey tableKey = new TableKey(uuid, s);
        if (this.getCacheMode() == 0) {
            final NameTDCacheable nameTDCacheable = (NameTDCacheable)this.nameTdCache.find(tableKey);
            if (nameTDCacheable != null) {
                tableDescriptor = nameTDCacheable.getTableDescriptor();
                tableDescriptor.setReferencedColumnMap(null);
                this.nameTdCache.release(nameTDCacheable);
            }
            return tableDescriptor;
        }
        return this.getTableDescriptorIndex1Scan(s, uuid.toString());
    }
    
    private TableDescriptor getTableDescriptorIndex1Scan(final String s, final String s2) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[1];
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLChar sqlChar = new SQLChar(s2);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, sqlChar);
        return this.finishTableDescriptor((TableDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, tabInfoImpl, null, null, false));
    }
    
    TableDescriptor getUncachedTableDescriptor(final TableKey tableKey) throws StandardException {
        return this.getTableDescriptorIndex1Scan(tableKey.getTableName(), tableKey.getSchemaId().toString());
    }
    
    public TableDescriptor getTableDescriptor(final UUID uuid) throws StandardException {
        TableDescriptor tableDescriptor = null;
        if (this.getCacheMode() == 0) {
            final OIDTDCacheable oidtdCacheable = (OIDTDCacheable)this.OIDTdCache.find(uuid);
            if (oidtdCacheable != null) {
                tableDescriptor = oidtdCacheable.getTableDescriptor();
                tableDescriptor.setReferencedColumnMap(null);
                this.OIDTdCache.release(oidtdCacheable);
            }
            return tableDescriptor;
        }
        return this.getTableDescriptorIndex2Scan(uuid.toString());
    }
    
    protected TableDescriptor getUncachedTableDescriptor(final UUID uuid) throws StandardException {
        return this.getTableDescriptorIndex2Scan(uuid.toString());
    }
    
    private TableDescriptor getTableDescriptorIndex2Scan(final String s) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[1];
        final SQLChar sqlChar = new SQLChar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, sqlChar);
        return this.finishTableDescriptor((TableDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, tabInfoImpl, null, null, false));
    }
    
    private TableDescriptor finishTableDescriptor(final TableDescriptor tableDescriptor) throws StandardException {
        if (tableDescriptor != null) {
            synchronized (tableDescriptor) {
                this.getColumnDescriptorsScan(tableDescriptor);
                this.getConglomerateDescriptorsScan(tableDescriptor);
            }
        }
        return tableDescriptor;
    }
    
    public boolean isSchemaEmpty(final SchemaDescriptor schemaDescriptor) throws StandardException {
        final TransactionController transactionCompile = this.getTransactionCompile();
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(schemaDescriptor.getUUID());
        return !this.isSchemaReferenced(transactionCompile, this.coreInfo[1], 0, 2, idValueAsCHAR) && !this.isSchemaReferenced(transactionCompile, this.getNonCoreTI(4), 1, 2, idValueAsCHAR) && !this.isSchemaReferenced(transactionCompile, this.getNonCoreTI(11), 1, 2, idValueAsCHAR) && !this.isSchemaReferenced(transactionCompile, this.getNonCoreTI(13), 1, 2, idValueAsCHAR) && !this.isSchemaReferenced(transactionCompile, this.getNonCoreTI(7), 0, 1, idValueAsCHAR) && (this.dictionaryVersion.majorVersionNumber < 180 || !this.isSchemaReferenced(transactionCompile, this.getNonCoreTI(20), 1, 1, idValueAsCHAR));
    }
    
    protected boolean isSchemaReferenced(final TransactionController transactionController, final TabInfoImpl tabInfoImpl, final int n, final int n2, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        ConglomerateController openConglomerate = null;
        ScanController openScan = null;
        final FormatableBitSet set = new FormatableBitSet(n2);
        tabInfoImpl.getCatalogRowFactory();
        set.set(n2 - 1);
        final ScanQualifier[][] scanQualifier = this.exFactory.getScanQualifier(1);
        scanQualifier[0][0].setQualifier(n2 - 1, dataValueDescriptor, 2, false, false, false);
        boolean next;
        try {
            openConglomerate = transactionController.openConglomerate(tabInfoImpl.getHeapConglomerate(), false, 0, 6, 4);
            openScan = transactionController.openScan(tabInfoImpl.getIndexConglomerate(n), false, 0, 6, 4, set, null, 1, scanQualifier, null, -1);
            next = openScan.next();
        }
        finally {
            if (openScan != null) {
                openScan.close();
            }
            if (openConglomerate != null) {
                openConglomerate.close();
            }
        }
        return next;
    }
    
    public void dropTableDescriptor(final TableDescriptor tableDescriptor, final SchemaDescriptor schemaDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[1];
        final SQLVarchar sqlVarchar = new SQLVarchar(tableDescriptor.getName());
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(schemaDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, idValueAsCHAR);
        tabInfoImpl.deleteRow(transactionController, indexableRow, 0);
    }
    
    public void updateLockGranularity(final TableDescriptor tableDescriptor, final SchemaDescriptor schemaDescriptor, final char c, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[1];
        final SYSTABLESRowFactory systablesRowFactory = (SYSTABLESRowFactory)tabInfoImpl.getCatalogRowFactory();
        final SQLVarchar sqlVarchar = new SQLVarchar(tableDescriptor.getName());
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(schemaDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, idValueAsCHAR);
        final ExecRow row = systablesRowFactory.makeRow(tableDescriptor, schemaDescriptor);
        final boolean[] array = new boolean[2];
        for (int i = 0; i < 2; ++i) {
            array[i] = false;
        }
        tabInfoImpl.updateRow(indexableRow, row, 0, array, null, transactionController);
    }
    
    void upgradeCLOBGETSUBSTRING_10_6(final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(7);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(3);
        final SQLVarchar sqlVarchar = new SQLVarchar("CLOBGETSUBSTRING");
        final SQLChar sqlChar = new SQLChar(new String(new char[] { 'F' }));
        indexableRow.setColumn(1, new SQLChar("c013800d-00f8-5b53-28a9-00000019ed88"));
        indexableRow.setColumn(2, sqlVarchar);
        indexableRow.setColumn(3, sqlChar);
        final AliasDescriptor aliasDescriptor = (AliasDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, true, 4, transactionController);
        final RoutineAliasInfo routineAliasInfo = (RoutineAliasInfo)aliasDescriptor.getAliasInfo();
        nonCoreTI.updateRow(indexableRow, nonCoreTI.getCatalogRowFactory().makeRow(new AliasDescriptor(this, aliasDescriptor.getUUID(), aliasDescriptor.getObjectName(), aliasDescriptor.getSchemaUUID(), aliasDescriptor.getJavaClassName(), aliasDescriptor.getAliasType(), aliasDescriptor.getNameSpace(), aliasDescriptor.getSystemAlias(), new RoutineAliasInfo(routineAliasInfo.getMethodName(), routineAliasInfo.getParameterCount(), routineAliasInfo.getParameterNames(), routineAliasInfo.getParameterTypes(), routineAliasInfo.getParameterModes(), routineAliasInfo.getMaxDynamicResultSets(), routineAliasInfo.getParameterStyle(), routineAliasInfo.getSQLAllowed(), routineAliasInfo.isDeterministic(), routineAliasInfo.hasVarargs(), routineAliasInfo.hasDefinersRights(), routineAliasInfo.calledOnNullInput(), DataTypeDescriptor.getCatalogType(12, 10890)), aliasDescriptor.getSpecificName()), null), 0, new boolean[] { false, false, false }, null, transactionController);
    }
    
    void upgradeSYSROUTINEPERMS_10_6(final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(7);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(3);
        final SQLVarchar sqlVarchar = new SQLVarchar("SYSCS_INPLACE_COMPRESS_TABLE");
        final SQLChar sqlChar = new SQLChar(new String(new char[] { 'P' }));
        indexableRow.setColumn(1, new SQLChar("c013800d-00fb-2649-07ec-000000134f30"));
        indexableRow.setColumn(2, sqlVarchar);
        indexableRow.setColumn(3, sqlChar);
        final UUID uuid = ((AliasDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, true, 4, transactionController)).getUUID();
        final TabInfoImpl nonCoreTI2 = this.getNonCoreTI(18);
        final ExecIndexRow indexableRow2 = this.exFactory.getIndexableRow(3);
        indexableRow2.setColumn(1, new SQLVarchar("PUBLIC"));
        indexableRow2.setColumn(2, new SQLChar(uuid.toString()));
        indexableRow2.setColumn(3, new SQLVarchar((String)null));
        nonCoreTI2.deleteRow(transactionController, indexableRow2, 0);
    }
    
    public ColumnDescriptor getColumnDescriptorByDefaultId(final UUID uuid) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[2];
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return (ColumnDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, tabInfoImpl, null, null, false);
    }
    
    private void getColumnDescriptorsScan(final TableDescriptor tableDescriptor) throws StandardException {
        this.getColumnDescriptorsScan(tableDescriptor.getUUID(), tableDescriptor.getColumnDescriptorList(), tableDescriptor);
    }
    
    private void getColumnDescriptorsScan(final UUID uuid, final ColumnDescriptorList list, final TupleDescriptor tupleDescriptor) throws StandardException {
        final ColumnDescriptorList list2 = new ColumnDescriptorList();
        final TabInfoImpl tabInfoImpl = this.coreInfo[2];
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        this.getDescriptorViaIndex(0, indexableRow, null, tabInfoImpl, tupleDescriptor, list, false);
        final int size = list.size();
        for (int i = 0; i < size; ++i) {
            list2.add(list.get(i));
        }
        for (int j = 0; j < size; ++j) {
            final ColumnDescriptor element = list2.elementAt(j);
            list.set(element.getPosition() - 1, element);
        }
    }
    
    public void dropColumnDescriptor(final UUID uuid, final String s, final TransactionController transactionController) throws StandardException {
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, idValueAsCHAR);
        indexableRow.setColumn(2, sqlVarchar);
        this.dropColumnDescriptorCore(transactionController, indexableRow);
    }
    
    public void dropAllColumnDescriptors(final UUID uuid, final TransactionController transactionController) throws StandardException {
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        this.dropColumnDescriptorCore(transactionController, indexableRow);
    }
    
    public void dropAllTableAndColPermDescriptors(final UUID uuid, final TransactionController transactionController) throws StandardException {
        if (!this.usesSqlAuthorization) {
            return;
        }
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        this.dropTablePermDescriptor(transactionController, indexableRow);
        this.dropColumnPermDescriptor(transactionController, indexableRow);
    }
    
    public void updateSYSCOLPERMSforAddColumnToUserTable(final UUID uuid, final TransactionController transactionController) throws StandardException {
        this.rewriteSYSCOLPERMSforAlterTable(uuid, transactionController, null);
    }
    
    public void updateSYSCOLPERMSforDropColumn(final UUID uuid, final TransactionController transactionController, final ColumnDescriptor columnDescriptor) throws StandardException {
        this.rewriteSYSCOLPERMSforAlterTable(uuid, transactionController, columnDescriptor);
    }
    
    private void rewriteSYSCOLPERMSforAlterTable(final UUID uuid, final TransactionController transactionController, final ColumnDescriptor columnDescriptor) throws StandardException {
        if (!this.usesSqlAuthorization) {
            return;
        }
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(17);
        final SYSCOLPERMSRowFactory syscolpermsRowFactory = (SYSCOLPERMSRowFactory)nonCoreTI.getCatalogRowFactory();
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        final List sList = newSList();
        this.getDescriptorViaIndex(2, indexableRow, null, nonCoreTI, null, sList, false);
        final boolean[] array = new boolean[3];
        final int[] array2 = { 6 };
        for (final ColPermsDescriptor colPermsDescriptor : sList) {
            this.removePermEntryInCache(colPermsDescriptor);
            final ExecIndexRow buildIndexKeyRow = syscolpermsRowFactory.buildIndexKeyRow(1, colPermsDescriptor);
            final ExecRow row = nonCoreTI.getRow(transactionController, buildIndexKeyRow, 1);
            FormatableBitSet set = (FormatableBitSet)row.getColumn(6).getObject();
            if (columnDescriptor == null) {
                set.grow(set.getLength() + 1);
            }
            else {
                final FormatableBitSet set2 = new FormatableBitSet(set);
                set2.shrink(set.getLength() - 1);
                for (int i = columnDescriptor.getPosition() - 1; i < set2.getLength(); ++i) {
                    if (set.isSet(i + 1)) {
                        set2.set(i);
                    }
                    else {
                        set2.clear(i);
                    }
                }
                set = set2;
            }
            row.setColumn(6, new UserType(set));
            nonCoreTI.updateRow(buildIndexKeyRow, row, 1, array, array2, transactionController);
        }
    }
    
    private void removePermEntryInCache(final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        final Cacheable cached = this.getPermissionsCache().findCached(permissionsDescriptor);
        if (cached != null) {
            this.getPermissionsCache().remove(cached);
        }
    }
    
    public void dropAllRoutinePermDescriptors(final UUID uuid, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(18);
        final SYSROUTINEPERMSRowFactory sysroutinepermsRowFactory = (SYSROUTINEPERMSRowFactory)nonCoreTI.getCatalogRowFactory();
        if (!this.usesSqlAuthorization) {
            return;
        }
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        ExecRow row;
        while ((row = nonCoreTI.getRow(transactionController, indexableRow, 2)) != null) {
            final PermissionsDescriptor permissionsDescriptor = (PermissionsDescriptor)sysroutinepermsRowFactory.buildDescriptor(row, null, this);
            this.removePermEntryInCache(permissionsDescriptor);
            nonCoreTI.deleteRow(transactionController, sysroutinepermsRowFactory.buildIndexKeyRow(1, permissionsDescriptor), 1);
        }
    }
    
    public void dropRoleGrantsByGrantee(final String s, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(19);
        this.visitRoleGrants(nonCoreTI, (SYSROLESRowFactory)nonCoreTI.getCatalogRowFactory(), 2, s, transactionController, 0);
    }
    
    private boolean existsRoleGrantByGrantee(final String s, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(19);
        return this.visitRoleGrants(nonCoreTI, (SYSROLESRowFactory)nonCoreTI.getCatalogRowFactory(), 2, s, transactionController, 1);
    }
    
    public void dropRoleGrantsByName(final String s, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(19);
        this.visitRoleGrants(nonCoreTI, (SYSROLESRowFactory)nonCoreTI.getCatalogRowFactory(), 1, s, transactionController, 0);
    }
    
    private boolean visitRoleGrants(final TabInfoImpl tabInfoImpl, final SYSROLESRowFactory sysrolesRowFactory, final int n, final String s, final TransactionController transactionController, final int n2) throws StandardException {
        final ConglomerateController openConglomerate = transactionController.openConglomerate(tabInfoImpl.getHeapConglomerate(), false, 0, 6, 4);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final ScanQualifier[][] scanQualifier = this.exFactory.getScanQualifier(1);
        scanQualifier[0][0].setQualifier(n - 1, sqlVarchar, 2, false, false, false);
        final ScanController openScan = transactionController.openScan(tabInfoImpl.getIndexConglomerate(0), false, 0, 6, 4, null, null, 0, scanQualifier, null, 0);
        try {
            final ExecIndexRow indexRowFromHeapRow = getIndexRowFromHeapRow(tabInfoImpl.getIndexRowGenerator(0), openConglomerate.newRowLocationTemplate(), sysrolesRowFactory.makeEmptyRow());
            while (openScan.fetchNext(indexRowFromHeapRow.getRowArray())) {
                if (n2 == 1) {
                    return true;
                }
                if (n2 != 0) {
                    continue;
                }
                tabInfoImpl.deleteRow(transactionController, indexRowFromHeapRow, 0);
            }
        }
        finally {
            if (openScan != null) {
                openScan.close();
            }
            if (openConglomerate != null) {
                openConglomerate.close();
            }
        }
        return false;
    }
    
    HashMap getRoleGrantGraph(final TransactionController transactionController, final boolean b) throws StandardException {
        final HashMap<Object, List<?>> hashMap = new HashMap<Object, List<?>>();
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(19);
        final SYSROLESRowFactory sysrolesRowFactory = (SYSROLESRowFactory)nonCoreTI.getCatalogRowFactory();
        final SQLVarchar sqlVarchar = new SQLVarchar("N");
        final ScanQualifier[][] scanQualifier = this.exFactory.getScanQualifier(1);
        scanQualifier[0][0].setQualifier(5, sqlVarchar, 2, false, false, false);
        final ScanController openScan = transactionController.openScan(nonCoreTI.getHeapConglomerate(), false, 0, 6, 4, null, null, 0, scanQualifier, null, 0);
        final ExecRow emptyRow = sysrolesRowFactory.makeEmptyRow();
        while (openScan.fetchNext(emptyRow.getRowArray())) {
            final RoleGrantDescriptor roleGrantDescriptor = (RoleGrantDescriptor)sysrolesRowFactory.buildDescriptor(emptyRow, null, this);
            final RoleGrantDescriptor roleDefinitionDescriptor = this.getRoleDefinitionDescriptor(roleGrantDescriptor.getGrantee());
            if (roleDefinitionDescriptor == null) {
                continue;
            }
            String s;
            if (b) {
                s = roleDefinitionDescriptor.getRoleName();
            }
            else {
                s = roleGrantDescriptor.getRoleName();
            }
            List<?> value = hashMap.get(s);
            if (value == null) {
                value = new LinkedList<Object>();
            }
            value.add(roleGrantDescriptor);
            hashMap.put(s, value);
        }
        openScan.close();
        return hashMap;
    }
    
    public RoleClosureIterator createRoleClosureIterator(final TransactionController transactionController, final String s, final boolean b) throws StandardException {
        return new RoleClosureIteratorImpl(s, b, this, transactionController);
    }
    
    public void dropAllPermsByGrantee(final String s, final TransactionController transactionController) throws StandardException {
        this.dropPermsByGrantee(s, transactionController, 16, 0, 1);
        this.dropPermsByGrantee(s, transactionController, 17, 0, 1);
        this.dropPermsByGrantee(s, transactionController, 18, 0, 1);
    }
    
    private void dropPermsByGrantee(final String s, final TransactionController transactionController, final int n, final int n2, final int n3) throws StandardException {
        this.visitPermsByGrantee(s, transactionController, n, n2, n3, 0);
    }
    
    private boolean existsPermByGrantee(final String s, final TransactionController transactionController, final int n, final int n2, final int n3) throws StandardException {
        return this.visitPermsByGrantee(s, transactionController, n, n2, n3, 1);
    }
    
    private boolean visitPermsByGrantee(final String s, final TransactionController transactionController, final int n, final int n2, final int n3, final int n4) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(n);
        final PermissionsCatalogRowFactory permissionsCatalogRowFactory = (PermissionsCatalogRowFactory)nonCoreTI.getCatalogRowFactory();
        final ConglomerateController openConglomerate = transactionController.openConglomerate(nonCoreTI.getHeapConglomerate(), false, 0, 6, 4);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final ScanQualifier[][] scanQualifier = this.exFactory.getScanQualifier(1);
        scanQualifier[0][0].setQualifier(n3 - 1, sqlVarchar, 2, false, false, false);
        final ScanController openScan = transactionController.openScan(nonCoreTI.getIndexConglomerate(n2), false, 0, 6, 4, null, null, 0, scanQualifier, null, 0);
        try {
            final ExecRow emptyRow = permissionsCatalogRowFactory.makeEmptyRow();
            final ExecIndexRow indexRowFromHeapRow = getIndexRowFromHeapRow(nonCoreTI.getIndexRowGenerator(n2), openConglomerate.newRowLocationTemplate(), emptyRow);
            while (openScan.fetchNext(indexRowFromHeapRow.getRowArray())) {
                openConglomerate.fetch((RowLocation)indexRowFromHeapRow.getColumn(indexRowFromHeapRow.nColumns()), emptyRow.getRowArray(), null);
                if (n4 == 1) {
                    return true;
                }
                if (n4 != 0) {
                    continue;
                }
                this.removePermEntryInCache((PermissionsDescriptor)permissionsCatalogRowFactory.buildDescriptor(emptyRow, null, this));
                nonCoreTI.deleteRow(transactionController, indexRowFromHeapRow, n2);
            }
        }
        finally {
            if (openScan != null) {
                openScan.close();
            }
            if (openConglomerate != null) {
                openConglomerate.close();
            }
        }
        return false;
    }
    
    private void dropColumnDescriptorCore(final TransactionController transactionController, final ExecIndexRow execIndexRow) throws StandardException {
        this.coreInfo[2].deleteRow(transactionController, execIndexRow, 0);
    }
    
    private void dropTablePermDescriptor(final TransactionController transactionController, final ExecIndexRow execIndexRow) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(16);
        final SYSTABLEPERMSRowFactory systablepermsRowFactory = (SYSTABLEPERMSRowFactory)nonCoreTI.getCatalogRowFactory();
        ExecRow row;
        while ((row = nonCoreTI.getRow(transactionController, execIndexRow, 2)) != null) {
            final PermissionsDescriptor permissionsDescriptor = (PermissionsDescriptor)systablepermsRowFactory.buildDescriptor(row, null, this);
            this.removePermEntryInCache(permissionsDescriptor);
            nonCoreTI.deleteRow(transactionController, systablepermsRowFactory.buildIndexKeyRow(1, permissionsDescriptor), 1);
        }
    }
    
    private void dropColumnPermDescriptor(final TransactionController transactionController, final ExecIndexRow execIndexRow) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(17);
        final SYSCOLPERMSRowFactory syscolpermsRowFactory = (SYSCOLPERMSRowFactory)nonCoreTI.getCatalogRowFactory();
        ExecRow row;
        while ((row = nonCoreTI.getRow(transactionController, execIndexRow, 2)) != null) {
            final PermissionsDescriptor permissionsDescriptor = (PermissionsDescriptor)syscolpermsRowFactory.buildDescriptor(row, null, this);
            this.removePermEntryInCache(permissionsDescriptor);
            nonCoreTI.deleteRow(transactionController, syscolpermsRowFactory.buildIndexKeyRow(1, permissionsDescriptor), 1);
        }
    }
    
    private void updateColumnDescriptor(final ColumnDescriptor columnDescriptor, final UUID uuid, final String s, final int[] array, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[2];
        final SYSCOLUMNSRowFactory syscolumnsRowFactory = (SYSCOLUMNSRowFactory)tabInfoImpl.getCatalogRowFactory();
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, idValueAsCHAR);
        indexableRow.setColumn(2, sqlVarchar);
        final ExecRow row = syscolumnsRowFactory.makeRow(columnDescriptor, null);
        final boolean[] array2 = new boolean[syscolumnsRowFactory.getNumIndexes()];
        if (array == null) {
            array2[1] = (array2[0] = true);
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == 2 || array[i] == 1) {
                    array2[0] = true;
                    break;
                }
                if (array[i] == 6) {
                    array2[1] = true;
                    break;
                }
            }
        }
        tabInfoImpl.updateRow(indexableRow, row, 0, array2, array, transactionController);
    }
    
    public ViewDescriptor getViewDescriptor(final UUID uuid) throws StandardException {
        return this.getViewDescriptor(this.getTableDescriptor(uuid));
    }
    
    public ViewDescriptor getViewDescriptor(final TableDescriptor tableDescriptor) throws StandardException {
        if (tableDescriptor.getViewDescriptor() != null) {
            return tableDescriptor.getViewDescriptor();
        }
        synchronized (tableDescriptor) {
            if (tableDescriptor.getViewDescriptor() != null) {
                return tableDescriptor.getViewDescriptor();
            }
            tableDescriptor.setViewDescriptor(this.getViewDescriptorScan(tableDescriptor));
        }
        return tableDescriptor.getViewDescriptor();
    }
    
    private ViewDescriptor getViewDescriptorScan(final TableDescriptor tableDescriptor) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(8);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(tableDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        final ViewDescriptor viewDescriptor = (ViewDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
        if (viewDescriptor != null) {
            viewDescriptor.setViewName(tableDescriptor.getName());
        }
        return viewDescriptor;
    }
    
    public void dropViewDescriptor(final ViewDescriptor viewDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(8);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(viewDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, indexableRow, 0);
    }
    
    private FileInfoDescriptor getFileInfoDescriptorIndex2Scan(final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(12);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return (FileInfoDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public FileInfoDescriptor getFileInfoDescriptor(final UUID uuid) throws StandardException {
        return this.getFileInfoDescriptorIndex2Scan(uuid);
    }
    
    private FileInfoDescriptor getFileInfoDescriptorIndex1Scan(final UUID uuid, final String s) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(12);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, idValueAsCHAR);
        return (FileInfoDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public FileInfoDescriptor getFileInfoDescriptor(final SchemaDescriptor schemaDescriptor, final String s) throws StandardException {
        return this.getFileInfoDescriptorIndex1Scan(schemaDescriptor.getUUID(), s);
    }
    
    public void dropFileInfoDescriptor(final FileInfoDescriptor fileInfoDescriptor) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(12);
        final TransactionController transactionExecute = this.getTransactionExecute();
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(fileInfoDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionExecute, indexableRow, 1);
    }
    
    public SPSDescriptor getSPSDescriptor(final UUID key) throws StandardException {
        this.getNonCoreTI(11);
        SPSDescriptor spsDescriptor2;
        if (this.spsNameCache != null && this.getCacheMode() == 0) {
            final SPSDescriptor spsDescriptor = this.spsIdHash.get(key);
            if (spsDescriptor != null) {
                return spsDescriptor;
            }
            spsDescriptor2 = this.getSPSDescriptorIndex2Scan(key.toString());
            final TableKey tableKey = new TableKey(spsDescriptor2.getSchemaDescriptor().getUUID(), spsDescriptor2.getName());
            try {
                this.spsNameCache.release(this.spsNameCache.create(tableKey, spsDescriptor2));
            }
            catch (StandardException ex) {
                if ("XBCA0.S".equals(ex.getMessageId())) {
                    return spsDescriptor2;
                }
                throw ex;
            }
        }
        else {
            spsDescriptor2 = this.getSPSDescriptorIndex2Scan(key.toString());
        }
        return spsDescriptor2;
    }
    
    void spsCacheEntryAdded(final SPSDescriptor value) {
        this.spsIdHash.put(value.getUUID(), value);
    }
    
    void spsCacheEntryRemoved(final SPSDescriptor spsDescriptor) {
        this.spsIdHash.remove(spsDescriptor.getUUID());
    }
    
    public SPSDescriptor getUncachedSPSDescriptor(final TableKey tableKey) throws StandardException {
        return this.getSPSDescriptorIndex1Scan(tableKey.getTableName(), tableKey.getSchemaId().toString());
    }
    
    protected SPSDescriptor getUncachedSPSDescriptor(final UUID uuid) throws StandardException {
        return this.getSPSDescriptorIndex2Scan(uuid.toString());
    }
    
    private SPSDescriptor getSPSDescriptorIndex2Scan(final String s) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(11);
        final SQLChar sqlChar = new SQLChar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, sqlChar);
        return (SPSDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public SPSDescriptor getSPSDescriptor(final String s, final SchemaDescriptor schemaDescriptor) throws StandardException {
        SPSDescriptor spsDescriptor = null;
        final UUID uuid = schemaDescriptor.getUUID();
        if (this.spsNameCache != null && this.getCacheMode() == 0) {
            final SPSNameCacheable spsNameCacheable = (SPSNameCacheable)this.spsNameCache.find(new TableKey(uuid, s));
            if (spsNameCacheable != null) {
                spsDescriptor = spsNameCacheable.getSPSDescriptor();
                this.spsNameCache.release(spsNameCacheable);
            }
            return spsDescriptor;
        }
        return this.getSPSDescriptorIndex1Scan(s, uuid.toString());
    }
    
    private SPSDescriptor getSPSDescriptorIndex1Scan(final String s, final String s2) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(11);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLChar sqlChar = new SQLChar(s2);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, sqlChar);
        final SPSDescriptor spsDescriptor = (SPSDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, nonCoreTI, null, null, false);
        if (spsDescriptor != null) {
            final ArrayList list = new ArrayList();
            spsDescriptor.setParams(this.getSPSParams(spsDescriptor, list));
            spsDescriptor.setParameterDefaults(list.toArray());
        }
        return spsDescriptor;
    }
    
    public void addSPSDescriptor(final SPSDescriptor spsDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(11);
        final SYSSTATEMENTSRowFactory sysstatementsRowFactory = (SYSSTATEMENTSRowFactory)nonCoreTI.getCatalogRowFactory();
        final int insertRow;
        synchronized (spsDescriptor) {
            insertRow = nonCoreTI.insertRow(sysstatementsRowFactory.makeSYSSTATEMENTSrow(spsDescriptor.initiallyCompilable(), spsDescriptor), transactionController);
        }
        if (insertRow != -1) {
            throw StandardException.newException("X0Y32.S", spsDescriptor.getDescriptorType(), spsDescriptor.getDescriptorName(), spsDescriptor.getSchemaDescriptor().getDescriptorType(), spsDescriptor.getSchemaDescriptor().getSchemaName());
        }
        this.addSPSParams(spsDescriptor, transactionController);
    }
    
    private void addSPSParams(final SPSDescriptor spsDescriptor, final TransactionController transactionController) throws StandardException {
        final UUID uuid = spsDescriptor.getUUID();
        final DataTypeDescriptor[] params = spsDescriptor.getParams();
        final Object[] parameterDefaults = spsDescriptor.getParameterDefaults();
        if (params == null) {
            return;
        }
        for (int length = params.length, i = 0; i < length; ++i) {
            final int j = i + 1;
            this.addDescriptor(new ColumnDescriptor("PARAM" + j, j, params[i], (parameterDefaults == null || i >= parameterDefaults.length) ? null : ((DataValueDescriptor)parameterDefaults[i]), null, uuid, null, 0L, 0L, 0L), null, 2, false, transactionController);
        }
    }
    
    public DataTypeDescriptor[] getSPSParams(final SPSDescriptor spsDescriptor, final List list) throws StandardException {
        final ColumnDescriptorList list2 = new ColumnDescriptorList();
        this.getColumnDescriptorsScan(spsDescriptor.getUUID(), list2, spsDescriptor);
        final int size = list2.size();
        final DataTypeDescriptor[] array = new DataTypeDescriptor[size];
        for (int i = 0; i < size; ++i) {
            final ColumnDescriptor element = list2.elementAt(i);
            array[i] = element.getType();
            if (list != null) {
                list.add(element.getDefaultValue());
            }
        }
        return array;
    }
    
    public void updateSPS(final SPSDescriptor spsDescriptor, final TransactionController transactionController, final boolean b) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(11);
        final SYSSTATEMENTSRowFactory sysstatementsRowFactory = (SYSSTATEMENTSRowFactory)nonCoreTI.getCatalogRowFactory();
        int[] array;
        if (b) {
            array = new int[] { 5, 6, 7, 9, 10 };
        }
        else {
            array = new int[] { 5, 10 };
        }
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(spsDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.updateRow(indexableRow, sysstatementsRowFactory.makeSYSSTATEMENTSrow(false, spsDescriptor), 0, new boolean[2], array, transactionController);
        if (!b) {
            return;
        }
        if (spsDescriptor.getParams() == null) {
            return;
        }
        this.dropAllColumnDescriptors(spsDescriptor.getUUID(), transactionController);
        this.addSPSParams(spsDescriptor, transactionController);
    }
    
    public void invalidateAllSPSPlans() throws StandardException {
        this.invalidateAllSPSPlans((LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext"));
    }
    
    public void invalidateAllSPSPlans(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        this.startWriting(languageConnectionContext);
        final Iterator<SPSDescriptor> iterator = this.getAllSPSDescriptors().iterator();
        while (iterator.hasNext()) {
            iterator.next().makeInvalid(14, languageConnectionContext);
        }
    }
    
    void clearSPSPlans() throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(11);
        this.faultInTabInfo(nonCoreTI);
        final TransactionController transactionExecute = this.getTransactionExecute();
        final FormatableBitSet set = new FormatableBitSet(11);
        final FormatableBitSet set2 = new FormatableBitSet(11);
        set2.set(4);
        set2.set(9);
        final DataValueDescriptor[] array = { null, null, null, null, new SQLBoolean(false), null, null, null, null, new UserType(null), null };
        final ScanController openScan = transactionExecute.openScan(nonCoreTI.getHeapConglomerate(), false, 4, 7, 4, set, null, 0, null, null, 0);
        while (openScan.fetchNext(null)) {
            openScan.replace(array, set2);
        }
        openScan.close();
    }
    
    public void dropSPSDescriptor(final SPSDescriptor spsDescriptor, final TransactionController transactionController) throws StandardException {
        this.dropSPSDescriptor(spsDescriptor.getUUID(), transactionController);
    }
    
    public void dropSPSDescriptor(final UUID uuid, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(11);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, indexableRow, 0);
        this.dropAllColumnDescriptors(uuid, transactionController);
    }
    
    public List getAllSPSDescriptors() throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(11);
        final List sList = newSList();
        final FormatableBitSet set = new FormatableBitSet(nonCoreTI.getCatalogRowFactory().getHeapColumnCount());
        for (int i = 0; i < set.size(); ++i) {
            if (i + 1 == 10) {
                set.clear(i);
            }
            else {
                set.set(i);
            }
        }
        this.getDescriptorViaHeap(set, null, nonCoreTI, null, sList);
        return sList;
    }
    
    private ConstraintDescriptorList getAllConstraintDescriptors() throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(4);
        final ConstraintDescriptorList list = new ConstraintDescriptorList();
        this.getConstraintDescriptorViaHeap(null, nonCoreTI, null, list);
        return list;
    }
    
    private GenericDescriptorList getAllTriggerDescriptors() throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(13);
        final GenericDescriptorList list = new GenericDescriptorList();
        this.getDescriptorViaHeap(null, null, nonCoreTI, null, list);
        return list;
    }
    
    public String getTriggerActionString(final Visitable visitable, final String anObject, final String s, final String s2, final int[] array, final int[] array2, final int n, final TableDescriptor tableDescriptor, final int n2, final boolean b) throws StandardException {
        final boolean checkVersion = this.checkVersion(210, null);
        final StringBuffer sb = new StringBuffer();
        int n3 = 0;
        final int numberOfColumns = tableDescriptor.getNumberOfColumns();
        final int[] a = new int[numberOfColumns];
        if (array == null) {
            for (int i = 0; i < numberOfColumns; ++i) {
                a[i] = i + 1;
            }
        }
        else {
            Arrays.fill(a, -1);
            for (int j = 0; j < array.length; ++j) {
                a[array[j] - 1] = array[j];
            }
        }
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
        visitable.accept(collectNodesVisitor);
        final List list = collectNodesVisitor.getList();
        Collections.sort((List<Object>)list, DataDictionaryImpl.OFFSET_COMPARATOR);
        if (b) {
            final int[] a2 = new int[numberOfColumns];
            Arrays.fill(a2, -1);
            for (int k = 0; k < list.size(); ++k) {
                final ColumnReference columnReference = list.get(k);
                if (columnReference.getBeginOffset() != -1) {
                    final TableName tableNameNode = columnReference.getTableNameNode();
                    if (tableNameNode != null) {
                        if (anObject == null || !anObject.equals(tableNameNode.getTableName())) {
                            if (s == null) {
                                continue;
                            }
                            if (!s.equals(tableNameNode.getTableName())) {
                                continue;
                            }
                        }
                        if (tableNameNode.getBeginOffset() != -1) {
                            this.checkInvalidTriggerReference(tableNameNode.getTableName(), anObject, s, n2);
                            final String columnName = columnReference.getColumnName();
                            final ColumnDescriptor columnDescriptor;
                            if ((columnDescriptor = tableDescriptor.getColumnDescriptor(columnName)) == null) {
                                throw StandardException.newException("42X04", tableNameNode + "." + columnName);
                            }
                            if (checkVersion) {
                                final int position = columnDescriptor.getPosition();
                                a[position - 1] = position;
                                array2[position - 1] = (a2[position - 1] = position);
                            }
                        }
                    }
                }
            }
        }
        else if (array != null && array2 != null) {
            for (int l = 0; l < array2.length; ++l) {
                a[array2[l] - 1] = array2[l];
            }
        }
        final int[] justTheRequiredColumns = this.justTheRequiredColumns(a, tableDescriptor);
        for (int n4 = 0; n4 < list.size(); ++n4) {
            final ColumnReference columnReference2 = list.get(n4);
            if (columnReference2.getBeginOffset() != -1) {
                final TableName tableNameNode2 = columnReference2.getTableNameNode();
                if (tableNameNode2 != null) {
                    if (anObject == null || !anObject.equals(tableNameNode2.getTableName())) {
                        if (s == null) {
                            continue;
                        }
                        if (!s.equals(tableNameNode2.getTableName())) {
                            continue;
                        }
                    }
                    final int beginOffset = tableNameNode2.getBeginOffset();
                    final int endOffset = tableNameNode2.getEndOffset();
                    if (beginOffset != -1) {
                        final String columnName2 = columnReference2.getColumnName();
                        final int n5 = columnReference2.getEndOffset() - columnReference2.getBeginOffset() + 1;
                        sb.append(s2.substring(n3, beginOffset - n));
                        int n6 = -1;
                        final ColumnDescriptor columnDescriptor2 = tableDescriptor.getColumnDescriptor(columnName2);
                        if (columnDescriptor2 == null) {
                            throw StandardException.newException("42X04", tableNameNode2 + "." + columnName2);
                        }
                        final int position2 = columnDescriptor2.getPosition();
                        if (checkVersion && justTheRequiredColumns != null) {
                            for (int n7 = 0; n7 < justTheRequiredColumns.length; ++n7) {
                                if (justTheRequiredColumns[n7] == position2) {
                                    n6 = n7 + 1;
                                }
                            }
                        }
                        else {
                            n6 = position2;
                        }
                        sb.append(this.genColumnReferenceSQL(tableDescriptor, columnName2, tableNameNode2.getTableName(), tableNameNode2.getTableName().equals(anObject), n6));
                        n3 = endOffset - n + n5 + 2;
                    }
                }
            }
        }
        if (n3 < s2.length()) {
            sb.append(s2.substring(n3));
        }
        return sb.toString();
    }
    
    private int[] justTheRequiredColumns(final int[] array, final TableDescriptor tableDescriptor) {
        int n = 0;
        final int numberOfColumns = tableDescriptor.getNumberOfColumns();
        for (int i = 0; i < numberOfColumns; ++i) {
            if (array[i] != -1) {
                ++n;
            }
        }
        if (n > 0) {
            final int[] array2 = new int[n];
            int n2 = 0;
            for (int j = 0; j < numberOfColumns; ++j) {
                if (array[j] != -1) {
                    array2[n2++] = array[j];
                }
            }
            return array2;
        }
        return null;
    }
    
    private void checkInvalidTriggerReference(final String s, final String anObject, final String anObject2, final int n) throws StandardException {
        if (s.equals(anObject) && (n & 0x4) == 0x4) {
            throw StandardException.newException("42Y92", "INSERT", "new");
        }
        if (s.equals(anObject2) && (n & 0x2) == 0x2) {
            throw StandardException.newException("42Y92", "DELETE", "old");
        }
    }
    
    private String genColumnReferenceSQL(final TableDescriptor tableDescriptor, final String str, final String str2, final boolean b, final int n) throws StandardException {
        final ColumnDescriptor columnDescriptor;
        if ((columnDescriptor = tableDescriptor.getColumnDescriptor(str)) == null) {
            throw StandardException.newException("42X04", str2 + "." + str);
        }
        final DataTypeDescriptor type = columnDescriptor.getType();
        final TypeId typeId = type.getTypeId();
        if (!typeId.isXMLTypeId()) {
            final StringBuffer sb = new StringBuffer();
            sb.append("CAST (org.apache.derby.iapi.db.Factory::getTriggerExecutionContext().");
            sb.append(b ? "getOldRow()" : "getNewRow()");
            sb.append(".getObject(");
            sb.append(n);
            sb.append(") AS ");
            sb.append(typeId.userType() ? typeId.getSQLTypeName() : type.getSQLstring());
            sb.append(") ");
            return sb.toString();
        }
        final StringBuffer sb2 = new StringBuffer();
        sb2.append("XMLPARSE(DOCUMENT CAST( ");
        sb2.append("org.apache.derby.iapi.db.Factory::getTriggerExecutionContext().");
        sb2.append(b ? "getOldRow()" : "getNewRow()");
        sb2.append(".getString(");
        sb2.append(n);
        sb2.append(") AS CLOB) PRESERVE WHITESPACE ) ");
        return sb2.toString();
    }
    
    public TriggerDescriptor getTriggerDescriptor(final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(13);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return (TriggerDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public TriggerDescriptor getTriggerDescriptor(final String s, final SchemaDescriptor schemaDescriptor) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(13);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(schemaDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, idValueAsCHAR);
        return (TriggerDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public GenericDescriptorList getTriggerDescriptors(final TableDescriptor tableDescriptor) throws StandardException {
        if (tableDescriptor == null) {
            return this.getAllTriggerDescriptors();
        }
        final GenericDescriptorList triggerDescriptorList = tableDescriptor.getTriggerDescriptorList();
        synchronized (triggerDescriptorList) {
            if (!triggerDescriptorList.getScanned()) {
                this.getTriggerDescriptorsScan(tableDescriptor, false);
            }
        }
        return triggerDescriptorList;
    }
    
    private void getTriggerDescriptorsScan(final TableDescriptor tableDescriptor, final boolean b) throws StandardException {
        final GenericDescriptorList triggerDescriptorList = tableDescriptor.getTriggerDescriptorList();
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(13);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(tableDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        this.getDescriptorViaIndex(2, indexableRow, null, nonCoreTI, null, triggerDescriptorList, b);
        triggerDescriptorList.setScanned(true);
    }
    
    public void dropTriggerDescriptor(final TriggerDescriptor triggerDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(13);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(triggerDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, indexableRow, 0);
    }
    
    public void updateTriggerDescriptor(final TriggerDescriptor triggerDescriptor, final UUID uuid, final int[] array, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(13);
        final SYSTRIGGERSRowFactory systriggersRowFactory = (SYSTRIGGERSRowFactory)nonCoreTI.getCatalogRowFactory();
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        final ExecRow row = systriggersRowFactory.makeRow(triggerDescriptor, null);
        final boolean[] array2 = new boolean[3];
        if (array == null) {
            array2[0] = true;
            array2[2] = (array2[1] = true);
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                switch (array[i]) {
                    case 1: {
                        array2[0] = true;
                        break;
                    }
                    case 2:
                    case 3: {
                        array2[1] = true;
                        break;
                    }
                    case 9: {
                        array2[2] = true;
                        break;
                    }
                }
            }
        }
        nonCoreTI.updateRow(indexableRow, row, 0, array2, array, transactionController);
    }
    
    public ConstraintDescriptor getConstraintDescriptor(final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(4);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return this.getConstraintDescriptorViaIndex(0, indexableRow, nonCoreTI, null, null, false);
    }
    
    public ConstraintDescriptor getConstraintDescriptor(final String s, final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(4);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, idValueAsCHAR);
        return this.getConstraintDescriptorViaIndex(1, indexableRow, nonCoreTI, null, null, false);
    }
    
    public List getStatisticsDescriptors(final TableDescriptor tableDescriptor) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(14);
        final List sList = newSList();
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(tableDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, sList, false, 1, this.getTransactionCompile());
        return sList;
    }
    
    public ConstraintDescriptorList getConstraintDescriptors(final TableDescriptor tableDescriptor) throws StandardException {
        if (tableDescriptor == null) {
            return this.getAllConstraintDescriptors();
        }
        final ConstraintDescriptorList constraintDescriptorList = tableDescriptor.getConstraintDescriptorList();
        synchronized (constraintDescriptorList) {
            if (!constraintDescriptorList.getScanned()) {
                this.getConstraintDescriptorsScan(tableDescriptor, false);
            }
        }
        return constraintDescriptorList;
    }
    
    public ConstraintDescriptorList getActiveConstraintDescriptors(final ConstraintDescriptorList list) throws StandardException {
        return list;
    }
    
    public boolean activeConstraint(final ConstraintDescriptor constraintDescriptor) throws StandardException {
        return true;
    }
    
    public ConstraintDescriptor getConstraintDescriptor(final TableDescriptor tableDescriptor, final UUID uuid) throws StandardException {
        return this.getConstraintDescriptors(tableDescriptor).getConstraintDescriptor(uuid);
    }
    
    public ConstraintDescriptor getConstraintDescriptorById(final TableDescriptor tableDescriptor, final UUID uuid) throws StandardException {
        return this.getConstraintDescriptors(tableDescriptor).getConstraintDescriptorById(uuid);
    }
    
    public ConstraintDescriptor getConstraintDescriptorByName(final TableDescriptor tableDescriptor, final SchemaDescriptor schemaDescriptor, final String s, final boolean b) throws StandardException {
        if (b) {
            tableDescriptor.emptyConstraintDescriptorList();
            this.getConstraintDescriptorsScan(tableDescriptor, true);
        }
        return this.getConstraintDescriptors(tableDescriptor).getConstraintDescriptorByName(schemaDescriptor, s);
    }
    
    private void getConstraintDescriptorsScan(final TableDescriptor tableDescriptor, final boolean b) throws StandardException {
        final ConstraintDescriptorList constraintDescriptorList = tableDescriptor.getConstraintDescriptorList();
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(4);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(tableDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        this.getConstraintDescriptorViaIndex(2, indexableRow, nonCoreTI, tableDescriptor, constraintDescriptorList, b);
        constraintDescriptorList.setScanned(true);
    }
    
    protected ConstraintDescriptor getConstraintDescriptorViaIndex(final int n, final ExecIndexRow execIndexRow, final TabInfoImpl tabInfoImpl, final TableDescriptor tableDescriptor, final ConstraintDescriptorList list, final boolean b) throws StandardException {
        final SYSCONSTRAINTSRowFactory sysconstraintsRowFactory = (SYSCONSTRAINTSRowFactory)tabInfoImpl.getCatalogRowFactory();
        ConstraintDescriptor e = null;
        final TransactionController transactionCompile = this.getTransactionCompile();
        final ExecRow emptyRow = sysconstraintsRowFactory.makeEmptyRow();
        final ConglomerateController openConglomerate = transactionCompile.openConglomerate(tabInfoImpl.getHeapConglomerate(), false, 0, 6, 4);
        final ScanController openScan = transactionCompile.openScan(tabInfoImpl.getIndexConglomerate(n), false, b ? 4 : 0, 6, 4, null, execIndexRow.getRowArray(), 1, null, execIndexRow.getRowArray(), -1);
        while (openScan.next()) {
            SubConstraintDescriptor subConstraintDescriptor = null;
            final ExecIndexRow indexRowFromHeapRow = getIndexRowFromHeapRow(tabInfoImpl.getIndexRowGenerator(n), openConglomerate.newRowLocationTemplate(), emptyRow);
            openScan.fetch(indexRowFromHeapRow.getRowArray());
            openConglomerate.fetch((RowLocation)indexRowFromHeapRow.getColumn(indexRowFromHeapRow.nColumns()), emptyRow.getRowArray(), null);
            switch (sysconstraintsRowFactory.getConstraintType(emptyRow)) {
                case 2:
                case 3:
                case 6: {
                    subConstraintDescriptor = this.getSubKeyConstraint(sysconstraintsRowFactory.getConstraintId(emptyRow), sysconstraintsRowFactory.getConstraintType(emptyRow));
                    break;
                }
                case 4: {
                    subConstraintDescriptor = this.getSubCheckConstraint(sysconstraintsRowFactory.getConstraintId(emptyRow));
                    break;
                }
            }
            subConstraintDescriptor.setTableDescriptor(tableDescriptor);
            e = (ConstraintDescriptor)sysconstraintsRowFactory.buildDescriptor(emptyRow, subConstraintDescriptor, this);
            if (list == null) {
                break;
            }
            list.add(e);
        }
        openScan.close();
        openConglomerate.close();
        return e;
    }
    
    protected TupleDescriptor getConstraintDescriptorViaHeap(final ScanQualifier[][] array, final TabInfoImpl tabInfoImpl, final TupleDescriptor tupleDescriptor, final List list) throws StandardException {
        final SYSCONSTRAINTSRowFactory sysconstraintsRowFactory = (SYSCONSTRAINTSRowFactory)tabInfoImpl.getCatalogRowFactory();
        ConstraintDescriptor constraintDescriptor = null;
        final TransactionController transactionCompile = this.getTransactionCompile();
        final ExecRow emptyRow = sysconstraintsRowFactory.makeEmptyRow();
        final ScanController openScan = transactionCompile.openScan(tabInfoImpl.getHeapConglomerate(), false, 0, 7, 4, null, null, 0, array, null, 0);
        try {
            while (openScan.fetchNext(emptyRow.getRowArray())) {
                TupleDescriptor tupleDescriptor2 = null;
                switch (sysconstraintsRowFactory.getConstraintType(emptyRow)) {
                    case 2:
                    case 3:
                    case 6: {
                        tupleDescriptor2 = this.getSubKeyConstraint(sysconstraintsRowFactory.getConstraintId(emptyRow), sysconstraintsRowFactory.getConstraintType(emptyRow));
                        break;
                    }
                    case 4: {
                        tupleDescriptor2 = this.getSubCheckConstraint(sysconstraintsRowFactory.getConstraintId(emptyRow));
                        break;
                    }
                }
                constraintDescriptor = (ConstraintDescriptor)sysconstraintsRowFactory.buildDescriptor(emptyRow, tupleDescriptor2, this);
                if (list == null) {
                    break;
                }
                list.add(constraintDescriptor);
            }
        }
        finally {
            openScan.close();
        }
        return constraintDescriptor;
    }
    
    public TableDescriptor getConstraintTableDescriptor(final UUID uuid) throws StandardException {
        final List constraints = this.getConstraints(uuid, 0, 2);
        if (constraints.size() == 0) {
            return null;
        }
        return this.getTableDescriptor(constraints.get(0));
    }
    
    public ConstraintDescriptorList getForeignKeys(final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(10);
        final List sList = newSList();
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        this.getDescriptorViaIndex(1, indexableRow, null, nonCoreTI, null, sList, false);
        final ConstraintDescriptorList list = new ConstraintDescriptorList();
        for (final SubKeyConstraintDescriptor subKeyConstraintDescriptor : sList) {
            list.add(this.getConstraintDescriptors(this.getConstraintTableDescriptor(subKeyConstraintDescriptor.getUUID())).getConstraintDescriptorById(subKeyConstraintDescriptor.getUUID()));
        }
        return list;
    }
    
    public List getConstraints(final UUID uuid, final int n, final int n2) throws StandardException {
        ConglomerateController openConglomerate = null;
        ScanController openScan = null;
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(4);
        final SYSCONSTRAINTSRowFactory sysconstraintsRowFactory = (SYSCONSTRAINTSRowFactory)nonCoreTI.getCatalogRowFactory();
        final List sList = newSList();
        try {
            final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
            final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
            indexableRow.setColumn(1, idValueAsCHAR);
            final TransactionController transactionCompile = this.getTransactionCompile();
            final ExecRow emptyRow = sysconstraintsRowFactory.makeEmptyRow();
            openConglomerate = transactionCompile.openConglomerate(nonCoreTI.getHeapConglomerate(), false, 0, 6, 4);
            final ExecIndexRow indexRowFromHeapRow = getIndexRowFromHeapRow(nonCoreTI.getIndexRowGenerator(n), openConglomerate.newRowLocationTemplate(), emptyRow);
            final DataValueDescriptor[] array = new DataValueDescriptor[7];
            final FormatableBitSet set = new FormatableBitSet(7);
            set.set(n2 - 1);
            array[n2 - 1] = new SQLChar();
            openScan = transactionCompile.openScan(nonCoreTI.getIndexConglomerate(n), false, 0, 6, 4, null, indexableRow.getRowArray(), 1, null, indexableRow.getRowArray(), -1);
            while (openScan.fetchNext(indexRowFromHeapRow.getRowArray())) {
                openConglomerate.fetch((RowLocation)indexRowFromHeapRow.getColumn(indexRowFromHeapRow.nColumns()), array, set);
                sList.add(this.uuidFactory.recreateUUID((String)array[n2 - 1].getObject()));
            }
        }
        finally {
            if (openConglomerate != null) {
                openConglomerate.close();
            }
            if (openScan != null) {
                openScan.close();
            }
        }
        return sList;
    }
    
    public void addConstraintDescriptor(final ConstraintDescriptor constraintDescriptor, final TransactionController transactionController) throws StandardException {
        final int constraintType = constraintDescriptor.getConstraintType();
        this.addDescriptor(constraintDescriptor, constraintDescriptor.getSchemaDescriptor(), 4, false, transactionController);
        switch (constraintType) {
            case 2:
            case 3:
            case 6: {
                this.addSubKeyConstraint((KeyConstraintDescriptor)constraintDescriptor, transactionController);
                break;
            }
            case 4: {
                this.addDescriptor(constraintDescriptor, null, 9, true, transactionController);
                break;
            }
        }
    }
    
    public void updateConstraintDescriptor(final ConstraintDescriptor constraintDescriptor, final UUID uuid, final int[] array, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(4);
        final SYSCONSTRAINTSRowFactory sysconstraintsRowFactory = (SYSCONSTRAINTSRowFactory)nonCoreTI.getCatalogRowFactory();
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        final ExecRow row = sysconstraintsRowFactory.makeRow(constraintDescriptor, null);
        final boolean[] array2 = new boolean[3];
        if (array == null) {
            array2[0] = true;
            array2[2] = (array2[1] = true);
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                switch (array[i]) {
                    case 1: {
                        array2[0] = true;
                        break;
                    }
                    case 3:
                    case 5: {
                        array2[1] = true;
                        break;
                    }
                    case 2: {
                        array2[2] = true;
                        break;
                    }
                }
            }
        }
        nonCoreTI.updateRow(indexableRow, row, 0, array2, array, transactionController);
    }
    
    public void dropConstraintDescriptor(final ConstraintDescriptor constraintDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(4);
        switch (constraintDescriptor.getConstraintType()) {
            case 2:
            case 3:
            case 6: {
                this.dropSubKeyConstraint(constraintDescriptor, transactionController);
                break;
            }
            case 4: {
                this.dropSubCheckConstraint(constraintDescriptor.getUUID(), transactionController);
                break;
            }
        }
        final SQLVarchar sqlVarchar = new SQLVarchar(constraintDescriptor.getConstraintName());
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(constraintDescriptor.getSchemaDescriptor().getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, indexableRow, 1);
    }
    
    public void dropAllConstraintDescriptors(final TableDescriptor tableDescriptor, final TransactionController transactionController) throws StandardException {
        final Iterator<ConstraintDescriptor> iterator = this.getConstraintDescriptors(tableDescriptor).iterator();
        while (iterator.hasNext()) {
            this.dropConstraintDescriptor(iterator.next(), transactionController);
        }
        tableDescriptor.setConstraintDescriptorList(null);
    }
    
    public SubKeyConstraintDescriptor getSubKeyConstraint(final UUID uuid, final int n) throws StandardException {
        int n2;
        int n3;
        if (n == 6) {
            n2 = 10;
            n3 = 0;
        }
        else {
            n2 = 5;
            n3 = 0;
        }
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(n2);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return (SubKeyConstraintDescriptor)this.getDescriptorViaIndex(n3, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    private void addSubKeyConstraint(final KeyConstraintDescriptor keyConstraintDescriptor, final TransactionController transactionController) throws StandardException {
        TabInfoImpl tabInfoImpl;
        ExecRow execRow;
        if (keyConstraintDescriptor.getConstraintType() == 6) {
            final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)keyConstraintDescriptor;
            tabInfoImpl = this.getNonCoreTI(10);
            execRow = ((SYSFOREIGNKEYSRowFactory)tabInfoImpl.getCatalogRowFactory()).makeRow(foreignKeyConstraintDescriptor, null);
            final ReferencedKeyConstraintDescriptor referencedConstraint = foreignKeyConstraintDescriptor.getReferencedConstraint();
            referencedConstraint.incrementReferenceCount();
            this.updateConstraintDescriptor(referencedConstraint, referencedConstraint.getUUID(), new int[] { 7 }, transactionController);
        }
        else {
            tabInfoImpl = this.getNonCoreTI(5);
            execRow = ((SYSKEYSRowFactory)tabInfoImpl.getCatalogRowFactory()).makeRow(keyConstraintDescriptor, null);
        }
        tabInfoImpl.insertRow(execRow, transactionController);
    }
    
    private void dropSubKeyConstraint(final ConstraintDescriptor constraintDescriptor, final TransactionController transactionController) throws StandardException {
        int n;
        int n2;
        if (constraintDescriptor.getConstraintType() == 6) {
            n = 10;
            n2 = 0;
            if (constraintDescriptor.getConstraintType() == 6) {
                final ReferencedKeyConstraintDescriptor referencedKeyConstraintDescriptor = (ReferencedKeyConstraintDescriptor)this.getConstraintDescriptor(((ForeignKeyConstraintDescriptor)constraintDescriptor).getReferencedConstraintId());
                if (referencedKeyConstraintDescriptor != null) {
                    referencedKeyConstraintDescriptor.decrementReferenceCount();
                    this.updateConstraintDescriptor(referencedKeyConstraintDescriptor, referencedKeyConstraintDescriptor.getUUID(), new int[] { 7 }, transactionController);
                }
            }
        }
        else {
            n = 5;
            n2 = 0;
        }
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(n);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(constraintDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, indexableRow, n2);
    }
    
    private SubCheckConstraintDescriptor getSubCheckConstraint(final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(9);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return (SubCheckConstraintDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    private void dropSubCheckConstraint(final UUID uuid, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(9);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, indexableRow, 0);
    }
    
    public Hashtable hashAllConglomerateDescriptorsByNumber(final TransactionController transactionController) throws StandardException {
        final Hashtable<Long, ConglomerateDescriptor> hashtable = new Hashtable<Long, ConglomerateDescriptor>();
        final TabInfoImpl tabInfoImpl = this.coreInfo[0];
        final SYSCONGLOMERATESRowFactory sysconglomeratesRowFactory = (SYSCONGLOMERATESRowFactory)tabInfoImpl.getCatalogRowFactory();
        final ExecRow emptyRow = sysconglomeratesRowFactory.makeEmptyRow();
        final ScanController openScan = transactionController.openScan(tabInfoImpl.getHeapConglomerate(), false, 0, 6, 1, null, null, 1, null, null, -1);
        while (openScan.fetchNext(emptyRow.getRowArray())) {
            final ConglomerateDescriptor value = (ConglomerateDescriptor)sysconglomeratesRowFactory.buildDescriptor(emptyRow, null, this);
            hashtable.put(new Long(value.getConglomerateNumber()), value);
        }
        openScan.close();
        return hashtable;
    }
    
    public Hashtable hashAllTableDescriptorsByTableId(final TransactionController transactionController) throws StandardException {
        final Hashtable<UUID, TableDescriptor> hashtable = new Hashtable<UUID, TableDescriptor>();
        final TabInfoImpl tabInfoImpl = this.coreInfo[1];
        final SYSTABLESRowFactory systablesRowFactory = (SYSTABLESRowFactory)tabInfoImpl.getCatalogRowFactory();
        final ExecRow emptyRow = systablesRowFactory.makeEmptyRow();
        final ScanController openScan = transactionController.openScan(tabInfoImpl.getHeapConglomerate(), false, 0, 6, 1, null, null, 1, null, null, -1);
        while (openScan.fetchNext(emptyRow.getRowArray())) {
            final TableDescriptor value = (TableDescriptor)systablesRowFactory.buildDescriptor(emptyRow, null, this, 1);
            hashtable.put(value.getUUID(), value);
        }
        openScan.close();
        return hashtable;
    }
    
    public ConglomerateDescriptor getConglomerateDescriptor(final UUID uuid) throws StandardException {
        final ConglomerateDescriptor[] conglomerateDescriptors = this.getConglomerateDescriptors(uuid);
        if (conglomerateDescriptors.length == 0) {
            return null;
        }
        return conglomerateDescriptors[0];
    }
    
    public ConglomerateDescriptor[] getConglomerateDescriptors(final UUID uuid) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[0];
        final List sList = newSList();
        if (uuid != null) {
            final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
            final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
            indexableRow.setColumn(1, idValueAsCHAR);
            this.getDescriptorViaIndex(0, indexableRow, null, tabInfoImpl, null, sList, false);
        }
        else {
            this.getDescriptorViaHeap(null, null, tabInfoImpl, null, sList);
        }
        final ConglomerateDescriptor[] array = new ConglomerateDescriptor[sList.size()];
        sList.toArray(array);
        return array;
    }
    
    public ConglomerateDescriptor getConglomerateDescriptor(final long n) throws StandardException {
        final ConglomerateDescriptor[] conglomerateDescriptors = this.getConglomerateDescriptors(n);
        if (conglomerateDescriptors.length == 0) {
            return null;
        }
        return conglomerateDescriptors[0];
    }
    
    public ConglomerateDescriptor[] getConglomerateDescriptors(final long n) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[0];
        final SYSCONGLOMERATESRowFactory sysconglomeratesRowFactory = (SYSCONGLOMERATESRowFactory)tabInfoImpl.getCatalogRowFactory();
        final SQLLongint sqlLongint = new SQLLongint(n);
        final ScanQualifier[][] scanQualifier = this.exFactory.getScanQualifier(1);
        scanQualifier[0][0].setQualifier(3 - 1, sqlLongint, 2, false, false, false);
        final ConglomerateDescriptorList list = new ConglomerateDescriptorList();
        this.getDescriptorViaHeap(null, scanQualifier, tabInfoImpl, null, list);
        final int size = list.size();
        final ConglomerateDescriptor[] array = new ConglomerateDescriptor[size];
        for (int i = 0; i < size; ++i) {
            array[i] = list.get(i);
        }
        return array;
    }
    
    private void getConglomerateDescriptorsScan(final TableDescriptor tableDescriptor) throws StandardException {
        final ConglomerateDescriptorList conglomerateDescriptorList = tableDescriptor.getConglomerateDescriptorList();
        final TabInfoImpl tabInfoImpl = this.coreInfo[0];
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(tableDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        this.getDescriptorViaIndex(2, indexableRow, null, tabInfoImpl, null, conglomerateDescriptorList, false);
    }
    
    public ConglomerateDescriptor getConglomerateDescriptor(final String s, final SchemaDescriptor schemaDescriptor, final boolean b) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[0];
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(schemaDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, idValueAsCHAR);
        return (ConglomerateDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, tabInfoImpl, null, null, b);
    }
    
    public void dropConglomerateDescriptor(final ConglomerateDescriptor conglomerateDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[0];
        final SQLVarchar sqlVarchar = new SQLVarchar(conglomerateDescriptor.getConglomerateName());
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(conglomerateDescriptor.getSchemaID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, idValueAsCHAR);
        tabInfoImpl.deleteRow(transactionController, indexableRow, 1);
    }
    
    public void dropAllConglomerateDescriptors(final TableDescriptor tableDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[0];
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(tableDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        tabInfoImpl.deleteRow(transactionController, indexableRow, 2);
    }
    
    public void updateConglomerateDescriptor(final ConglomerateDescriptor conglomerateDescriptor, final long n, final TransactionController transactionController) throws StandardException {
        this.updateConglomerateDescriptor(new ConglomerateDescriptor[] { conglomerateDescriptor }, n, transactionController);
    }
    
    public void updateSystemSchemaAuthorization(final String s, final TransactionController transactionController) throws StandardException {
        this.updateSchemaAuth("SYS", s, transactionController);
        this.updateSchemaAuth("SYSIBM", s, transactionController);
        this.updateSchemaAuth("SYSCAT", s, transactionController);
        this.updateSchemaAuth("SYSFUN", s, transactionController);
        this.updateSchemaAuth("SYSPROC", s, transactionController);
        this.updateSchemaAuth("SYSSTAT", s, transactionController);
        this.updateSchemaAuth("NULLID", s, transactionController);
        this.updateSchemaAuth("SQLJ", s, transactionController);
        this.updateSchemaAuth("SYSCS_DIAG", s, transactionController);
        this.updateSchemaAuth("SYSCS_UTIL", s, transactionController);
        this.resetDatabaseOwner(transactionController);
    }
    
    public void updateSchemaAuth(final String s, final String s2, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[3];
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, sqlVarchar);
        final ExecRow emptyRow = ((SYSSCHEMASRowFactory)tabInfoImpl.getCatalogRowFactory()).makeEmptyRow();
        emptyRow.setColumn(3, new SQLVarchar(s2));
        tabInfoImpl.updateRow(indexableRow, emptyRow, 0, new boolean[] { false, false }, new int[] { 3 }, transactionController);
    }
    
    public void updateConglomerateDescriptor(final ConglomerateDescriptor[] array, final long conglomerateNumber, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[0];
        final SYSCONGLOMERATESRowFactory sysconglomeratesRowFactory = (SYSCONGLOMERATESRowFactory)tabInfoImpl.getCatalogRowFactory();
        final boolean[] array2 = { false, false, false };
        for (int i = 0; i < array.length; ++i) {
            final SQLChar idValueAsCHAR = getIDValueAsCHAR(array[i].getUUID());
            final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
            indexableRow.setColumn(1, idValueAsCHAR);
            array[i].setConglomerateNumber(conglomerateNumber);
            tabInfoImpl.updateRow(indexableRow, sysconglomeratesRowFactory.makeRow(array[i], null), 0, array2, null, transactionController);
        }
    }
    
    public List getDependentsDescriptorList(final String s) throws StandardException {
        final List sList = newSList();
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(6);
        final SQLChar sqlChar = new SQLChar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, sqlChar);
        this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, sList, false);
        return sList;
    }
    
    public List getProvidersDescriptorList(final String s) throws StandardException {
        final List sList = newSList();
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(6);
        final SQLChar sqlChar = new SQLChar(s);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, sqlChar);
        this.getDescriptorViaIndex(1, indexableRow, null, nonCoreTI, null, sList, false);
        return sList;
    }
    
    public List getAllDependencyDescriptorsList() throws StandardException {
        final List sList = newSList();
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(6);
        final SYSDEPENDSRowFactory sysdependsRowFactory = (SYSDEPENDSRowFactory)nonCoreTI.getCatalogRowFactory();
        final TransactionController transactionCompile = this.getTransactionCompile();
        final ExecRow emptyRow = sysdependsRowFactory.makeEmptyRow();
        final ScanController openScan = transactionCompile.openScan(nonCoreTI.getHeapConglomerate(), false, 0, 7, 4, null, null, 1, null, null, -1);
        while (openScan.fetchNext(emptyRow.getRowArray())) {
            sList.add(sysdependsRowFactory.buildDescriptor(emptyRow, null, this));
        }
        openScan.close();
        return sList;
    }
    
    public void dropStoredDependency(final DependencyDescriptor dependencyDescriptor, final TransactionController transactionController) throws StandardException {
        final UUID uuid = dependencyDescriptor.getUUID();
        final UUID providerID = dependencyDescriptor.getProviderID();
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(6);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRows(transactionController, indexableRow, 1, null, new DropDependencyFilter(providerID), indexableRow, -1, 0);
    }
    
    public void dropDependentsStoredDependencies(final UUID uuid, final TransactionController transactionController) throws StandardException {
        this.dropDependentsStoredDependencies(uuid, transactionController, true);
    }
    
    public void dropDependentsStoredDependencies(final UUID uuid, final TransactionController transactionController, final boolean b) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(6);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, indexableRow, 0, b);
    }
    
    public UUIDFactory getUUIDFactory() {
        return this.uuidFactory;
    }
    
    public AliasDescriptor getAliasDescriptorForUDT(TransactionController transactionCompile, final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        if (transactionCompile == null) {
            transactionCompile = this.getTransactionCompile();
        }
        if (dataTypeDescriptor == null) {
            return null;
        }
        final BaseTypeIdImpl baseTypeId = dataTypeDescriptor.getTypeId().getBaseTypeId();
        if (!baseTypeId.isAnsiUDT()) {
            return null;
        }
        return this.getAliasDescriptor(this.getSchemaDescriptor(baseTypeId.getSchemaName(), transactionCompile, true).getUUID().toString(), baseTypeId.getUnqualifiedName(), 'A');
    }
    
    public AliasDescriptor getAliasDescriptor(final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(7);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return (AliasDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public AliasDescriptor getAliasDescriptor(final String s, final String s2, final char c) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(7);
        final SQLVarchar sqlVarchar = new SQLVarchar(s2);
        final SQLChar sqlChar = new SQLChar(new String(new char[] { c }));
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(3);
        indexableRow.setColumn(1, new SQLChar(s));
        indexableRow.setColumn(2, sqlVarchar);
        indexableRow.setColumn(3, sqlChar);
        return (AliasDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public List getRoutineList(final String s, final String anObject, final char c) throws StandardException {
        if (s.equals("c013800d-00fb-2642-07ec-000000134f30") && c == 'F') {
            final ArrayList<AliasDescriptor> list = new ArrayList<AliasDescriptor>(1);
            for (int i = 0; i < DataDictionaryImpl.SYSFUN_FUNCTIONS.length; ++i) {
                final String[] array = DataDictionaryImpl.SYSFUN_FUNCTIONS[i];
                final String s2 = array[0];
                if (s2.equals(anObject)) {
                    AliasDescriptor aliasDescriptor = this.sysfunDescriptors[i];
                    if (aliasDescriptor == null) {
                        final TypeDescriptor catalogType = DataTypeDescriptor.getBuiltInDataTypeDescriptor(array[1]).getCatalogType();
                        final boolean booleanValue = Boolean.valueOf(array[4]);
                        final boolean booleanValue2 = Boolean.valueOf(array[5]);
                        final int n = array.length - 6;
                        final TypeDescriptor[] array2 = new TypeDescriptor[n];
                        final String[] array3 = new String[n];
                        final int[] array4 = new int[n];
                        for (int j = 0; j < n; ++j) {
                            array2[j] = DataTypeDescriptor.getBuiltInDataTypeDescriptor(array[6 + j]).getCatalogType();
                            array3[j] = "P" + (j + 1);
                            array4[j] = 1;
                        }
                        aliasDescriptor = new AliasDescriptor(this, this.uuidFactory.createUUID(), s2, this.uuidFactory.recreateUUID(s), array[2], 'F', 'F', true, new RoutineAliasInfo(array[3], n, array3, array2, array4, 0, (short)0, (short)3, booleanValue, booleanValue2, false, false, catalogType), null);
                        this.sysfunDescriptors[i] = aliasDescriptor;
                    }
                    list.add(aliasDescriptor);
                }
            }
            return list;
        }
        final AliasDescriptor aliasDescriptor2 = this.getAliasDescriptor(s, anObject, c);
        return (aliasDescriptor2 == null) ? Collections.EMPTY_LIST : Collections.singletonList(aliasDescriptor2);
    }
    
    public void dropAliasDescriptor(final AliasDescriptor aliasDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(7);
        final char[] value = { aliasDescriptor.getNameSpace() };
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(3);
        indexableRow.setColumn(1, getIDValueAsCHAR(aliasDescriptor.getSchemaUUID()));
        indexableRow.setColumn(2, new SQLVarchar(aliasDescriptor.getDescriptorName()));
        indexableRow.setColumn(3, new SQLChar(new String(value)));
        nonCoreTI.deleteRow(transactionController, indexableRow, 0);
    }
    
    public void updateUser(final UserDescriptor userDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(22);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, new SQLVarchar(userDescriptor.getUserName()));
        nonCoreTI.updateRow(indexableRow, nonCoreTI.getCatalogRowFactory().makeRow(userDescriptor, null), 0, new boolean[] { false }, new int[] { 2, 3, 4 }, transactionController);
    }
    
    public UserDescriptor getUser(final String s) throws StandardException {
        this.dictionaryVersion.checkVersion(210, "NATIVE AUTHENTICATION");
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(22);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, new SQLVarchar(s));
        return (UserDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public void dropUser(final String s, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(22);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, new SQLVarchar(s));
        nonCoreTI.deleteRow(transactionController, indexableRow, 0);
    }
    
    protected void loadDictionaryTables(final TransactionController transactionController, final DataDescriptorGenerator dataDescriptorGenerator, final Properties properties) throws StandardException {
        this.loadCatalogs(dataDescriptorGenerator, this.coreInfo);
        this.dictionaryVersion = (DD_Version)transactionController.getProperty("DataDictionaryVersion");
        if (PropertyUtil.nativeAuthenticationEnabled(properties)) {
            this.dictionaryVersion.checkVersion(210, "NATIVE AUTHENTICATION");
        }
        this.resetDatabaseOwner(transactionController);
        this.softwareVersion.upgradeIfNeeded(this.dictionaryVersion, transactionController, properties);
    }
    
    public void resetDatabaseOwner(final TransactionController transactionController) throws StandardException {
        this.authorizationDatabaseOwner = this.locateSchemaRow("SYSIBM", transactionController).getAuthorizationId();
        this.systemSchemaDesc.setAuthorizationId(this.authorizationDatabaseOwner);
        this.sysIBMSchemaDesc.setAuthorizationId(this.authorizationDatabaseOwner);
        this.systemUtilSchemaDesc.setAuthorizationId(this.authorizationDatabaseOwner);
    }
    
    public void loadCatalogs(final DataDescriptorGenerator dataDescriptorGenerator, final TabInfoImpl[] array) throws StandardException {
        for (final TabInfoImpl tabInfoImpl : array) {
            final int numberOfIndexes = tabInfoImpl.getNumberOfIndexes();
            if (numberOfIndexes > 0) {
                for (int j = 0; j < numberOfIndexes; ++j) {
                    this.initSystemIndexVariables(dataDescriptorGenerator, tabInfoImpl, j);
                }
            }
        }
    }
    
    protected void createDictionaryTables(final Properties properties, final TransactionController transactionController, final DataDescriptorGenerator dataDescriptorGenerator) throws StandardException {
        this.systemSchemaDesc = this.newSystemSchemaDesc("SYS", "8000000d-00d0-fd77-3ed8-000a0a0b1900");
        for (int i = 0; i < 4; ++i) {
            final TabInfoImpl tabInfoImpl = this.coreInfo[i];
            tabInfoImpl.setHeapConglomerate(this.createConglomerate(tabInfoImpl.getTableName(), transactionController, tabInfoImpl.getCatalogRowFactory().makeEmptyRow(), tabInfoImpl.getCreateHeapProperties()));
            if (this.coreInfo[i].getNumberOfIndexes() > 0) {
                this.bootStrapSystemIndexes(this.systemSchemaDesc, transactionController, dataDescriptorGenerator, tabInfoImpl);
            }
        }
        for (int j = 0; j < 4; ++j) {
            this.addSystemTableToDictionary(this.coreInfo[j], this.systemSchemaDesc, transactionController, dataDescriptorGenerator);
        }
        properties.put("SystablesIdentifier", Long.toString(this.coreInfo[1].getHeapConglomerate()));
        final String key = "SystablesIndex1Identifier";
        final TabInfoImpl tabInfoImpl2 = this.coreInfo[1];
        final SYSTABLESRowFactory systablesRowFactory = (SYSTABLESRowFactory)this.coreInfo[1].getCatalogRowFactory();
        properties.put(key, Long.toString(tabInfoImpl2.getIndexConglomerate(0)));
        final String key2 = "SystablesIndex2Identifier";
        final TabInfoImpl tabInfoImpl3 = this.coreInfo[1];
        final SYSTABLESRowFactory systablesRowFactory2 = (SYSTABLESRowFactory)this.coreInfo[1].getCatalogRowFactory();
        properties.put(key2, Long.toString(tabInfoImpl3.getIndexConglomerate(1)));
        properties.put("SyscolumnsIdentifier", Long.toString(this.coreInfo[2].getHeapConglomerate()));
        final String key3 = "SyscolumnsIndex1Identifier";
        final TabInfoImpl tabInfoImpl4 = this.coreInfo[2];
        final SYSCOLUMNSRowFactory syscolumnsRowFactory = (SYSCOLUMNSRowFactory)this.coreInfo[2].getCatalogRowFactory();
        properties.put(key3, Long.toString(tabInfoImpl4.getIndexConglomerate(0)));
        final String key4 = "SyscolumnsIndex2Identifier";
        final TabInfoImpl tabInfoImpl5 = this.coreInfo[2];
        final SYSCOLUMNSRowFactory syscolumnsRowFactory2 = (SYSCOLUMNSRowFactory)this.coreInfo[2].getCatalogRowFactory();
        properties.put(key4, Long.toString(tabInfoImpl5.getIndexConglomerate(1)));
        properties.put("SysconglomeratesIdentifier", Long.toString(this.coreInfo[0].getHeapConglomerate()));
        final String key5 = "SysconglomeratesIndex1Identifier";
        final TabInfoImpl tabInfoImpl6 = this.coreInfo[0];
        final SYSCONGLOMERATESRowFactory sysconglomeratesRowFactory = (SYSCONGLOMERATESRowFactory)this.coreInfo[0].getCatalogRowFactory();
        properties.put(key5, Long.toString(tabInfoImpl6.getIndexConglomerate(0)));
        final String key6 = "SysconglomeratesIndex2Identifier";
        final TabInfoImpl tabInfoImpl7 = this.coreInfo[0];
        final SYSCONGLOMERATESRowFactory sysconglomeratesRowFactory2 = (SYSCONGLOMERATESRowFactory)this.coreInfo[0].getCatalogRowFactory();
        properties.put(key6, Long.toString(tabInfoImpl7.getIndexConglomerate(1)));
        final String key7 = "SysconglomeratesIndex3Identifier";
        final TabInfoImpl tabInfoImpl8 = this.coreInfo[0];
        final SYSCONGLOMERATESRowFactory sysconglomeratesRowFactory3 = (SYSCONGLOMERATESRowFactory)this.coreInfo[0].getCatalogRowFactory();
        properties.put(key7, Long.toString(tabInfoImpl8.getIndexConglomerate(2)));
        properties.put("SysschemasIdentifier", Long.toString(this.coreInfo[3].getHeapConglomerate()));
        final String key8 = "SysschemasIndex1Identifier";
        final TabInfoImpl tabInfoImpl9 = this.coreInfo[3];
        final SYSSCHEMASRowFactory sysschemasRowFactory = (SYSSCHEMASRowFactory)this.coreInfo[3].getCatalogRowFactory();
        properties.put(key8, Long.toString(tabInfoImpl9.getIndexConglomerate(0)));
        final String key9 = "SysschemasIndex2Identifier";
        final TabInfoImpl tabInfoImpl10 = this.coreInfo[3];
        final SYSSCHEMASRowFactory sysschemasRowFactory2 = (SYSSCHEMASRowFactory)this.coreInfo[3].getCatalogRowFactory();
        properties.put(key9, Long.toString(tabInfoImpl10.getIndexConglomerate(1)));
        this.sysIBMSchemaDesc = this.addSystemSchema("SYSIBM", "c013800d-00f8-5b53-28a9-00000019ed88", transactionController);
        for (int k = 0; k < DataDictionaryImpl.NUM_NONCORE; ++k) {
            final int n = k + 4;
            final boolean b = n == 15;
            this.makeCatalog(this.getNonCoreTIByNumber(n), b ? this.sysIBMSchemaDesc : this.systemSchemaDesc, transactionController);
            if (b) {
                this.populateSYSDUMMY1(transactionController);
            }
            this.clearNoncoreTable(k);
        }
        this.addDescriptor(this.systemSchemaDesc, null, 3, false, transactionController);
        this.addSystemSchema("SYSCAT", "c013800d-00fb-2641-07ec-000000134f30", transactionController);
        this.addSystemSchema("SYSFUN", "c013800d-00fb-2642-07ec-000000134f30", transactionController);
        this.addSystemSchema("SYSPROC", "c013800d-00fb-2643-07ec-000000134f30", transactionController);
        this.addSystemSchema("SYSSTAT", "c013800d-00fb-2644-07ec-000000134f30", transactionController);
        this.addSystemSchema("NULLID", "c013800d-00fb-2647-07ec-000000134f30", transactionController);
        this.addSystemSchema("SQLJ", "c013800d-00fb-2648-07ec-000000134f30", transactionController);
        this.addSystemSchema("SYSCS_DIAG", "c013800d-00fb-2646-07ec-000000134f30", transactionController);
        this.addSystemSchema("SYSCS_UTIL", "c013800d-00fb-2649-07ec-000000134f30", transactionController);
        this.addDescriptor(new SchemaDescriptor(this, "APP", "APP", this.uuidFactory.recreateUUID("80000000-00d2-b38f-4cda-000a0a412c00"), false), null, 3, false, transactionController);
    }
    
    private SchemaDescriptor addSystemSchema(final String s, final String s2, final TransactionController transactionController) throws StandardException {
        final SchemaDescriptor schemaDescriptor = new SchemaDescriptor(this, s, this.authorizationDatabaseOwner, this.uuidFactory.recreateUUID(s2), true);
        this.addDescriptor(schemaDescriptor, null, 3, false, transactionController);
        return schemaDescriptor;
    }
    
    protected void upgradeMakeCatalog(final TransactionController transactionController, final int n) throws StandardException {
        TabInfoImpl nonCoreTIByNumber;
        if (n >= 4) {
            nonCoreTIByNumber = this.getNonCoreTIByNumber(n);
        }
        else {
            nonCoreTIByNumber = this.coreInfo[n];
        }
        this.makeCatalog(nonCoreTIByNumber, (n == 15) ? this.getSysIBMSchemaDescriptor() : this.getSystemSchemaDescriptor(), transactionController);
    }
    
    protected void upgradeJarStorage(final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(12);
        final SYSFILESRowFactory sysfilesRowFactory = (SYSFILESRowFactory)nonCoreTI.getCatalogRowFactory();
        final ExecRow emptyRow = sysfilesRowFactory.makeEmptyRow();
        final ScanController openScan = transactionController.openScan(nonCoreTI.getHeapConglomerate(), false, 0, 7, 4, null, null, 0, null, null, 0);
        final HashMap<String, Object> hashMap = new HashMap<String, Object>();
        try {
            while (openScan.fetchNext(emptyRow.getRowArray())) {
                final FileInfoDescriptor fileInfoDescriptor = (FileInfoDescriptor)sysfilesRowFactory.buildDescriptor(emptyRow, null, this);
                hashMap.put(fileInfoDescriptor.getSchemaDescriptor().getSchemaName(), null);
                JarUtil.upgradeJar(transactionController, fileInfoDescriptor);
            }
        }
        finally {
            openScan.close();
        }
        final Iterator<String> iterator = hashMap.keySet().iterator();
        final FileResource fileHandler = transactionController.getFileHandler();
        while (iterator.hasNext()) {
            fileHandler.removeJarDir("jar" + File.separatorChar + iterator.next());
        }
    }
    
    public void makeCatalog(final TabInfoImpl tabInfoImpl, final SchemaDescriptor schemaDescriptor, final TransactionController transactionController) throws StandardException {
        final DataDescriptorGenerator dataDescriptorGenerator = this.getDataDescriptorGenerator();
        tabInfoImpl.setHeapConglomerate(this.createConglomerate(tabInfoImpl.getTableName(), transactionController, tabInfoImpl.getCatalogRowFactory().makeEmptyRow(), tabInfoImpl.getCreateHeapProperties()));
        if (tabInfoImpl.getNumberOfIndexes() > 0) {
            this.bootStrapSystemIndexes(schemaDescriptor, transactionController, dataDescriptorGenerator, tabInfoImpl);
        }
        this.addSystemTableToDictionary(tabInfoImpl, schemaDescriptor, transactionController, dataDescriptorGenerator);
    }
    
    public void upgradeFixSystemColumnDefinition(final CatalogRowFactory catalogRowFactory, final int n, final TransactionController transactionController) throws StandardException {
        final SystemColumn[] buildColumnList = catalogRowFactory.buildColumnList();
        final TableDescriptor tableDescriptor = this.getTableDescriptor(catalogRowFactory.getCatalogName(), this.getSystemSchemaDescriptor(), transactionController);
        final ColumnDescriptor columnDescriptor = this.makeColumnDescriptor(buildColumnList[n - 1], n, tableDescriptor);
        this.updateColumnDescriptor(columnDescriptor, tableDescriptor.getUUID(), columnDescriptor.getColumnName(), new int[] { 4 }, transactionController);
    }
    
    public void upgrade_addColumns(final CatalogRowFactory catalogRowFactory, final int[] array, final TransactionController transactionController) throws StandardException {
        final SystemColumn[] buildColumnList = catalogRowFactory.buildColumnList();
        final ExecRow emptyRow = catalogRowFactory.makeEmptyRow();
        final int length = array.length;
        final SchemaDescriptor systemSchemaDescriptor = this.getSystemSchemaDescriptor();
        TableDescriptor tableDescriptor;
        long n;
        if (catalogRowFactory instanceof SYSTABLESRowFactory) {
            tableDescriptor = this.dataDescriptorGenerator.newTableDescriptor("SYSTABLES", systemSchemaDescriptor, 0, 'R');
            tableDescriptor.setUUID(this.getUUIDForCoreTable("SYSTABLES", systemSchemaDescriptor.getUUID().toString(), transactionController));
            n = this.coreInfo[1].getHeapConglomerate();
        }
        else if (catalogRowFactory instanceof SYSCOLUMNSRowFactory) {
            tableDescriptor = this.dataDescriptorGenerator.newTableDescriptor("SYSCOLUMNS", systemSchemaDescriptor, 0, 'R');
            tableDescriptor.setUUID(this.getUUIDForCoreTable("SYSCOLUMNS", systemSchemaDescriptor.getUUID().toString(), transactionController));
            n = this.coreInfo[2].getHeapConglomerate();
        }
        else {
            tableDescriptor = this.getTableDescriptor(catalogRowFactory.getCatalogName(), systemSchemaDescriptor, transactionController);
            n = tableDescriptor.getHeapConglomerateId();
        }
        this.widenConglomerate(emptyRow, array, n, transactionController);
        final ColumnDescriptor[] array2 = new ColumnDescriptor[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = this.makeColumnDescriptor(buildColumnList[array[i] - 1], i + 1, tableDescriptor);
        }
        this.addDescriptorArray(array2, tableDescriptor, 2, false, transactionController);
    }
    
    public void upgrade_addInvisibleColumns(final CatalogRowFactory catalogRowFactory, final int[] array, final TransactionController transactionController) throws StandardException {
        this.widenConglomerate(catalogRowFactory.makeEmptyRow(), array, this.getTableDescriptor(catalogRowFactory.getCatalogName(), this.getSystemSchemaDescriptor(), transactionController).getHeapConglomerateId(), transactionController);
    }
    
    private void widenConglomerate(final ExecRow execRow, final int[] array, final long n, final TransactionController transactionController) throws StandardException {
        for (final int n2 : array) {
            transactionController.addColumnToConglomerate(n, n2 - 1, execRow.getColumn(n2), 0);
        }
    }
    
    public long upgrade_makeOneIndex(final TransactionController transactionController, final TabInfoImpl tabInfoImpl, final int n, final long n2) throws StandardException {
        final SchemaDescriptor systemSchemaDescriptor = this.getSystemSchemaDescriptor();
        final ConglomerateDescriptor bootstrapOneIndex = this.bootstrapOneIndex(systemSchemaDescriptor, transactionController, this.getDataDescriptorGenerator(), tabInfoImpl, n, n2);
        final long conglomerateNumber = bootstrapOneIndex.getConglomerateNumber();
        this.addDescriptor(bootstrapOneIndex, systemSchemaDescriptor, 0, false, transactionController);
        return conglomerateNumber;
    }
    
    private UUID getUUIDForCoreTable(final String s, final String s2, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[1];
        final SYSTABLESRowFactory systablesRowFactory = (SYSTABLESRowFactory)tabInfoImpl.getCatalogRowFactory();
        final ExecRow valueRow = this.exFactory.getValueRow(1);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLChar sqlChar = new SQLChar(s2);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, sqlChar);
        final ConglomerateController openConglomerate = transactionController.openConglomerate(tabInfoImpl.getHeapConglomerate(), false, 0, 6, 4);
        final ExecIndexRow buildEmptyIndexRow = systablesRowFactory.buildEmptyIndexRow(0, openConglomerate.newRowLocationTemplate());
        final ScanController openScan = transactionController.openScan(tabInfoImpl.getIndexConglomerate(0), false, 0, 6, 4, null, indexableRow.getRowArray(), 1, null, indexableRow.getRowArray(), -1);
        if (openScan.fetchNext(buildEmptyIndexRow.getRowArray())) {
            final RowLocation rowLocation = (RowLocation)buildEmptyIndexRow.getColumn(buildEmptyIndexRow.nColumns());
            valueRow.setColumn(1, new SQLChar());
            new FormatableBitSet(1).set(0);
            openConglomerate.fetch(rowLocation, valueRow.getRowArray(), null);
        }
        openScan.close();
        openConglomerate.close();
        return this.uuidFactory.recreateUUID(valueRow.getColumn(1).toString());
    }
    
    void upgrade_initSystemTableCols(final TransactionController transactionController, final boolean b, final int n, final FormatableBitSet set, final DataValueDescriptor[] array) throws StandardException {
        final TabInfoImpl tabInfoImpl = b ? this.coreInfo[n] : this.getNonCoreTIByNumber(n);
        if (!b) {
            this.faultInTabInfo(tabInfoImpl);
        }
        final ScanController openScan = transactionController.openScan(tabInfoImpl.getHeapConglomerate(), false, 4, 7, 4, RowUtil.EMPTY_ROW_BITSET, null, 0, null, null, 0);
        while (openScan.next()) {
            openScan.replace(array, set);
        }
        openScan.close();
    }
    
    private void bootStrapSystemIndexes(final SchemaDescriptor schemaDescriptor, final TransactionController transactionController, final DataDescriptorGenerator dataDescriptorGenerator, final TabInfoImpl tabInfoImpl) throws StandardException {
        final ConglomerateDescriptor[] array = new ConglomerateDescriptor[tabInfoImpl.getNumberOfIndexes()];
        for (int i = 0; i < tabInfoImpl.getNumberOfIndexes(); ++i) {
            array[i] = this.bootstrapOneIndex(schemaDescriptor, transactionController, dataDescriptorGenerator, tabInfoImpl, i, tabInfoImpl.getHeapConglomerate());
        }
        for (int j = 0; j < tabInfoImpl.getNumberOfIndexes(); ++j) {
            this.addDescriptor(array[j], schemaDescriptor, 0, false, transactionController);
        }
    }
    
    public RowLocation[] computeAutoincRowLocations(final TransactionController transactionController, final TableDescriptor tableDescriptor) throws StandardException {
        if (!tableDescriptor.tableHasAutoincrement()) {
            return null;
        }
        final int numberOfColumns = tableDescriptor.getNumberOfColumns();
        final RowLocation[] array = new RowLocation[numberOfColumns];
        for (int i = 0; i < numberOfColumns; ++i) {
            final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(i + 1);
            if (columnDescriptor.isAutoincrement()) {
                array[i] = this.computeRowLocation(transactionController, tableDescriptor, columnDescriptor.getColumnName());
            }
        }
        return array;
    }
    
    public NumberDataValue getSetAutoincrementValue(final RowLocation rowLocation, final TransactionController transactionController, final boolean b, final NumberDataValue numberDataValue, final boolean b2) throws StandardException {
        final int n = 7;
        final TabInfoImpl tabInfoImpl = this.coreInfo[2];
        ConglomerateController openConglomerate = null;
        final ExecRow emptyRow = ((SYSCOLUMNSRowFactory)tabInfoImpl.getCatalogRowFactory()).makeEmptyRow();
        final FormatableBitSet set = new FormatableBitSet(9);
        set.set(n - 1);
        set.set(n);
        set.set(n + 1);
        try {
            openConglomerate = transactionController.openConglomerate(tabInfoImpl.getHeapConglomerate(), false, 0x4 | (b2 ? 0 : 128), 6, 4);
            openConglomerate.fetch(rowLocation, emptyRow.getRowArray(), set, b2);
            NumberDataValue plus = (NumberDataValue)emptyRow.getColumn(n);
            final long long1 = plus.getLong();
            if (b) {
                plus = plus.plus(plus, (NumberDataValue)emptyRow.getColumn(n + 2), plus);
                emptyRow.setColumn(n, plus);
                final FormatableBitSet set2 = new FormatableBitSet(9);
                set2.set(n - 1);
                openConglomerate.replace(rowLocation, emptyRow.getRowArray(), set2);
            }
            if (numberDataValue != null) {
                numberDataValue.setValue(long1);
                return numberDataValue;
            }
            plus.setValue(long1);
            return plus;
        }
        finally {
            if (openConglomerate != null) {
                openConglomerate.close();
            }
        }
    }
    
    private ConglomerateDescriptor bootstrapOneIndex(final SchemaDescriptor schemaDescriptor, final TransactionController transactionController, final DataDescriptorGenerator dataDescriptorGenerator, final TabInfoImpl tabInfoImpl, final int n, final long i) throws StandardException {
        final CatalogRowFactory catalogRowFactory = tabInfoImpl.getCatalogRowFactory();
        this.initSystemIndexVariables(dataDescriptorGenerator, tabInfoImpl, n);
        final IndexRowGenerator indexRowGenerator = tabInfoImpl.getIndexRowGenerator(n);
        final int indexColumnCount = tabInfoImpl.getIndexColumnCount(n);
        final boolean indexUnique = tabInfoImpl.isIndexUnique(n);
        final ExecIndexRow indexRowTemplate = indexRowGenerator.getIndexRowTemplate();
        final ExecRow emptyRow = catalogRowFactory.makeEmptyRow();
        final ConglomerateController openConglomerate = transactionController.openConglomerate(i, false, 0, 6, 4);
        final RowLocation rowLocationTemplate = openConglomerate.newRowLocationTemplate();
        openConglomerate.close();
        indexRowGenerator.getIndexRow(emptyRow, rowLocationTemplate, indexRowTemplate, null);
        final Properties createIndexProperties = tabInfoImpl.getCreateIndexProperties(n);
        createIndexProperties.put("baseConglomerateId", Long.toString(i));
        createIndexProperties.put("nUniqueColumns", Integer.toString(indexUnique ? indexColumnCount : (indexColumnCount + 1)));
        createIndexProperties.put("rowLocationColumn", Integer.toString(indexColumnCount));
        createIndexProperties.put("nKeyFields", Integer.toString(indexColumnCount + 1));
        final ConglomerateDescriptor conglomerateDescriptor = dataDescriptorGenerator.newConglomerateDescriptor(transactionController.createConglomerate("BTREE", indexRowTemplate.getRowArray(), null, null, createIndexProperties, 0), catalogRowFactory.getIndexName(n), true, indexRowGenerator, false, catalogRowFactory.getCanonicalIndexUUID(n), catalogRowFactory.getCanonicalTableUUID(), schemaDescriptor.getUUID());
        tabInfoImpl.setIndexConglomerate(conglomerateDescriptor);
        return conglomerateDescriptor;
    }
    
    public void initSystemIndexVariables(final DataDescriptorGenerator dataDescriptorGenerator, final TabInfoImpl tabInfoImpl, final int n) throws StandardException {
        final int indexColumnCount = tabInfoImpl.getIndexColumnCount(n);
        final int[] array = new int[indexColumnCount];
        for (int i = 0; i < indexColumnCount; ++i) {
            array[i] = tabInfoImpl.getBaseColumnPosition(n, i);
        }
        final boolean[] array2 = new boolean[array.length];
        for (int j = 0; j < array.length; ++j) {
            array2[j] = true;
        }
        IndexRowGenerator indexRowGenerator;
        if (this.softwareVersion.checkVersion(160, null)) {
            indexRowGenerator = new IndexRowGenerator("BTREE", tabInfoImpl.isIndexUnique(n), false, array, array2, array.length);
        }
        else {
            indexRowGenerator = new IndexRowGenerator("BTREE", tabInfoImpl.isIndexUnique(n), array, array2, array.length);
        }
        tabInfoImpl.setIndexRowGenerator(n, indexRowGenerator);
    }
    
    protected void populateSYSDUMMY1(final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(15);
        nonCoreTI.insertRow(nonCoreTI.getCatalogRowFactory().makeRow(null, null), transactionController);
    }
    
    public void clearCaches() throws StandardException {
        this.clearCaches(true);
    }
    
    public void clearCaches(final boolean b) throws StandardException {
        this.nameTdCache.cleanAll();
        this.nameTdCache.ageOut();
        this.OIDTdCache.cleanAll();
        this.OIDTdCache.ageOut();
        if (b) {
            this.clearSequenceCaches();
        }
        if (this.spsNameCache != null) {
            this.spsNameCache.cleanAll();
            this.spsNameCache.ageOut();
            this.spsIdHash.clear();
        }
    }
    
    public void clearSequenceCaches() throws StandardException {
        this.sequenceGeneratorCache.cleanAll();
        this.sequenceGeneratorCache.ageOut();
    }
    
    private void addSystemTableToDictionary(final TabInfoImpl tabInfoImpl, final SchemaDescriptor schemaDescriptor, final TransactionController transactionController, final DataDescriptorGenerator dataDescriptorGenerator) throws StandardException {
        final CatalogRowFactory catalogRowFactory = tabInfoImpl.getCatalogRowFactory();
        final String tableName = tabInfoImpl.getTableName();
        final long heapConglomerate = tabInfoImpl.getHeapConglomerate();
        final SystemColumn[] buildColumnList = catalogRowFactory.buildColumnList();
        final UUID canonicalHeapUUID = catalogRowFactory.getCanonicalHeapUUID();
        final String canonicalHeapName = catalogRowFactory.getCanonicalHeapName();
        final int length = buildColumnList.length;
        final TableDescriptor tableDescriptor = dataDescriptorGenerator.newTableDescriptor(tableName, schemaDescriptor, 1, 'R');
        tableDescriptor.setUUID(catalogRowFactory.getCanonicalTableUUID());
        this.addDescriptor(tableDescriptor, schemaDescriptor, 1, false, transactionController);
        this.addDescriptor(dataDescriptorGenerator.newConglomerateDescriptor(heapConglomerate, canonicalHeapName, false, null, false, canonicalHeapUUID, tableDescriptor.getUUID(), schemaDescriptor.getUUID()), schemaDescriptor, 0, false, transactionController);
        final ColumnDescriptor[] array = new ColumnDescriptor[length];
        for (int i = 0; i < length; ++i) {
            array[i] = this.makeColumnDescriptor(buildColumnList[i], i + 1, tableDescriptor);
        }
        this.addDescriptorArray(array, tableDescriptor, 2, false, transactionController);
        final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
        for (int j = 0; j < length; ++j) {
            columnDescriptorList.add(array[j]);
        }
    }
    
    private ColumnDescriptor makeColumnDescriptor(final SystemColumn systemColumn, final int n, final TableDescriptor tableDescriptor) throws StandardException {
        return new ColumnDescriptor(systemColumn.getName(), n, systemColumn.getType(), null, null, tableDescriptor, null, 0L, 0L);
    }
    
    private long createConglomerate(final String s, final TransactionController transactionController, final ExecRow execRow, final Properties properties) throws StandardException {
        return transactionController.createConglomerate("heap", execRow.getRowArray(), null, null, properties, 0);
    }
    
    private static SQLChar getIDValueAsCHAR(final UUID uuid) {
        return new SQLChar(uuid.toString());
    }
    
    public void initializeCatalogInfo() throws StandardException {
        this.initializeCoreInfo();
        this.initializeNoncoreInfo();
    }
    
    private void initializeCoreInfo() throws StandardException {
        final TabInfoImpl[] coreInfo = new TabInfoImpl[4];
        this.coreInfo = coreInfo;
        final TabInfoImpl[] array = coreInfo;
        final UUIDFactory uuidFactory = this.uuidFactory;
        array[1] = new TabInfoImpl(new SYSTABLESRowFactory(uuidFactory, this.exFactory, this.dvf));
        array[2] = new TabInfoImpl(new SYSCOLUMNSRowFactory(uuidFactory, this.exFactory, this.dvf));
        array[0] = new TabInfoImpl(new SYSCONGLOMERATESRowFactory(uuidFactory, this.exFactory, this.dvf));
        array[3] = new TabInfoImpl(new SYSSCHEMASRowFactory(uuidFactory, this.exFactory, this.dvf));
    }
    
    private void initializeNoncoreInfo() {
        this.noncoreInfo = new TabInfoImpl[DataDictionaryImpl.NUM_NONCORE];
    }
    
    public TransactionController getTransactionCompile() throws StandardException {
        if (this.bootingTC != null) {
            return this.bootingTC;
        }
        return getLCC().getTransactionCompile();
    }
    
    public TransactionController getTransactionExecute() throws StandardException {
        if (this.bootingTC != null) {
            return this.bootingTC;
        }
        return getLCC().getTransactionExecute();
    }
    
    private final TupleDescriptor getDescriptorViaIndex(final int n, final ExecIndexRow execIndexRow, final ScanQualifier[][] array, final TabInfoImpl tabInfoImpl, final TupleDescriptor tupleDescriptor, final List list, final boolean b) throws StandardException {
        return this.getDescriptorViaIndexMinion(n, execIndexRow, array, tabInfoImpl, tupleDescriptor, list, b, 4, this.getTransactionCompile());
    }
    
    private final TupleDescriptor getDescriptorViaIndex(final int n, final ExecIndexRow execIndexRow, final ScanQualifier[][] array, final TabInfoImpl tabInfoImpl, final TupleDescriptor tupleDescriptor, final List list, final boolean b, final int n2, TransactionController transactionCompile) throws StandardException {
        if (transactionCompile == null) {
            transactionCompile = this.getTransactionCompile();
        }
        return this.getDescriptorViaIndexMinion(n, execIndexRow, array, tabInfoImpl, tupleDescriptor, list, b, n2, transactionCompile);
    }
    
    private final TupleDescriptor getDescriptorViaIndexMinion(final int n, final ExecIndexRow execIndexRow, final ScanQualifier[][] array, final TabInfoImpl tabInfoImpl, final TupleDescriptor tupleDescriptor, final List list, final boolean b, final int n2, final TransactionController transactionController) throws StandardException {
        final CatalogRowFactory catalogRowFactory = tabInfoImpl.getCatalogRowFactory();
        TupleDescriptor buildDescriptor = null;
        final ExecRow emptyRow = catalogRowFactory.makeEmptyRow();
        final ConglomerateController openConglomerate = transactionController.openConglomerate(tabInfoImpl.getHeapConglomerate(), false, 0, 6, n2);
        final ScanController openScan = transactionController.openScan(tabInfoImpl.getIndexConglomerate(n), false, b ? 4 : 0, 6, n2, null, execIndexRow.getRowArray(), 1, array, execIndexRow.getRowArray(), -1);
        while (true) {
            final ExecIndexRow indexRowFromHeapRow = getIndexRowFromHeapRow(tabInfoImpl.getIndexRowGenerator(n), openConglomerate.newRowLocationTemplate(), emptyRow);
            if (!openScan.fetchNext(indexRowFromHeapRow.getRowArray())) {
                break;
            }
            final RowLocation rowLocation = (RowLocation)indexRowFromHeapRow.getColumn(indexRowFromHeapRow.nColumns());
            boolean fetch;
            try {
                fetch = openConglomerate.fetch(rowLocation, emptyRow.getRowArray(), null);
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (StandardException ex2) {
                throw ex2;
            }
            if (!fetch && n2 == 1) {
                buildDescriptor = null;
            }
            else {
                buildDescriptor = catalogRowFactory.buildDescriptor(emptyRow, tupleDescriptor, this);
            }
            if (list == null) {
                break;
            }
            if (buildDescriptor == null) {
                continue;
            }
            list.add(buildDescriptor);
        }
        openScan.close();
        openConglomerate.close();
        return buildDescriptor;
    }
    
    private void debugGenerateInfo(final StringBuffer sb, final TransactionController transactionController, final ConglomerateController conglomerateController, final TabInfoImpl tabInfoImpl, final int n) {
    }
    
    protected TupleDescriptor getDescriptorViaHeap(final FormatableBitSet set, final ScanQualifier[][] array, final TabInfoImpl tabInfoImpl, final TupleDescriptor tupleDescriptor, final List list) throws StandardException {
        final CatalogRowFactory catalogRowFactory = tabInfoImpl.getCatalogRowFactory();
        TupleDescriptor buildDescriptor = null;
        final TransactionController transactionCompile = this.getTransactionCompile();
        final ExecRow emptyRow = catalogRowFactory.makeEmptyRow();
        final ScanController openScan = transactionCompile.openScan(tabInfoImpl.getHeapConglomerate(), false, 0, 7, 4, set, null, 0, array, null, 0);
        while (openScan.fetchNext(emptyRow.getRowArray())) {
            buildDescriptor = catalogRowFactory.buildDescriptor(emptyRow, tupleDescriptor, this);
            if (list == null) {
                break;
            }
            list.add(buildDescriptor);
        }
        openScan.close();
        return buildDescriptor;
    }
    
    private TabInfoImpl getNonCoreTI(final int n) throws StandardException {
        final TabInfoImpl nonCoreTIByNumber = this.getNonCoreTIByNumber(n);
        this.faultInTabInfo(nonCoreTIByNumber);
        return nonCoreTIByNumber;
    }
    
    protected TabInfoImpl getNonCoreTIByNumber(final int n) throws StandardException {
        final int n2 = n - 4;
        TabInfoImpl tabInfoImpl = this.noncoreInfo[n2];
        if (tabInfoImpl == null) {
            final UUIDFactory uuidFactory = this.uuidFactory;
            switch (n) {
                case 4: {
                    tabInfoImpl = new TabInfoImpl(new SYSCONSTRAINTSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 5: {
                    tabInfoImpl = new TabInfoImpl(new SYSKEYSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 6: {
                    tabInfoImpl = new TabInfoImpl(new SYSDEPENDSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 8: {
                    tabInfoImpl = new TabInfoImpl(new SYSVIEWSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 9: {
                    tabInfoImpl = new TabInfoImpl(new SYSCHECKSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 10: {
                    tabInfoImpl = new TabInfoImpl(new SYSFOREIGNKEYSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 11: {
                    tabInfoImpl = new TabInfoImpl(new SYSSTATEMENTSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 12: {
                    tabInfoImpl = new TabInfoImpl(new SYSFILESRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 7: {
                    tabInfoImpl = new TabInfoImpl(new SYSALIASESRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 13: {
                    tabInfoImpl = new TabInfoImpl(new SYSTRIGGERSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 14: {
                    tabInfoImpl = new TabInfoImpl(new SYSSTATISTICSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 15: {
                    tabInfoImpl = new TabInfoImpl(new SYSDUMMY1RowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 16: {
                    tabInfoImpl = new TabInfoImpl(new SYSTABLEPERMSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 17: {
                    tabInfoImpl = new TabInfoImpl(new SYSCOLPERMSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 18: {
                    tabInfoImpl = new TabInfoImpl(new SYSROUTINEPERMSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 19: {
                    tabInfoImpl = new TabInfoImpl(new SYSROLESRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 20: {
                    tabInfoImpl = new TabInfoImpl(new SYSSEQUENCESRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 21: {
                    tabInfoImpl = new TabInfoImpl(new SYSPERMSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
                case 22: {
                    tabInfoImpl = new TabInfoImpl(new SYSUSERSRowFactory(uuidFactory, this.exFactory, this.dvf));
                    break;
                }
            }
            this.initSystemIndexVariables(tabInfoImpl);
            this.noncoreInfo[n2] = tabInfoImpl;
        }
        return tabInfoImpl;
    }
    
    protected void initSystemIndexVariables(final TabInfoImpl tabInfoImpl) throws StandardException {
        final int numberOfIndexes = tabInfoImpl.getNumberOfIndexes();
        if (numberOfIndexes > 0) {
            final DataDescriptorGenerator dataDescriptorGenerator = this.getDataDescriptorGenerator();
            for (int i = 0; i < numberOfIndexes; ++i) {
                this.initSystemIndexVariables(dataDescriptorGenerator, tabInfoImpl, i);
            }
        }
    }
    
    private void clearNoncoreTable(final int n) {
        this.noncoreInfo[n] = null;
    }
    
    public void faultInTabInfo(final TabInfoImpl tabInfoImpl) throws StandardException {
        if (tabInfoImpl.isComplete()) {
            return;
        }
        synchronized (tabInfoImpl) {
            if (tabInfoImpl.isComplete()) {
                return;
            }
            final TableDescriptor tableDescriptor = this.getTableDescriptor(tabInfoImpl.getTableName(), this.getSystemSchemaDescriptor(), null);
            if (tableDescriptor == null) {
                return;
            }
            final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
            for (int i = 0; i < conglomerateDescriptors.length; ++i) {
                final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[i];
                if (!conglomerateDescriptor.isIndex()) {
                    tabInfoImpl.setHeapConglomerate(conglomerateDescriptor.getConglomerateNumber());
                    break;
                }
            }
            if (tabInfoImpl.getCatalogRowFactory().getNumIndexes() == 0) {
                return;
            }
            int n = 0;
            for (int j = 0; j < conglomerateDescriptors.length; ++j) {
                final ConglomerateDescriptor indexConglomerate = conglomerateDescriptors[j];
                if (indexConglomerate.isIndex()) {
                    tabInfoImpl.setIndexConglomerate(indexConglomerate);
                    ++n;
                }
            }
        }
    }
    
    public static ExecIndexRow getIndexRowFromHeapRow(final IndexRowGenerator indexRowGenerator, final RowLocation rowLocation, final ExecRow execRow) throws StandardException {
        final ExecIndexRow indexRowTemplate = indexRowGenerator.getIndexRowTemplate();
        indexRowGenerator.getIndexRow(execRow, rowLocation, indexRowTemplate, null);
        return indexRowTemplate;
    }
    
    public int getEngineType() {
        return this.engineType;
    }
    
    public long getSYSCOLUMNSHeapConglomerateNumber() {
        return this.coreInfo[2].getHeapConglomerate();
    }
    
    void addSYSCOLUMNSIndex2Property(final TransactionController transactionController, final long i) {
        this.startupParameters.put("SyscolumnsIndex2Identifier", Long.toString(i));
    }
    
    private long getBootParameter(final Properties properties, final String key, final boolean b) throws StandardException {
        final String property = properties.getProperty(key);
        if (property == null) {
            if (!b) {
                return -1L;
            }
            throw StandardException.newException("XCY03.S", key);
        }
        else {
            try {
                return Long.parseLong(property);
            }
            catch (NumberFormatException ex) {
                throw StandardException.newException("XCY00.S", key, property);
            }
        }
    }
    
    public String getSystemSQLName() {
        final StringBuffer sb = new StringBuffer("SQL");
        synchronized (this) {
            final long n = System.currentTimeMillis() / 10L * 10L;
            if (n > this.timeForLastSystemSQLName) {
                this.systemSQLNameNumber = 0;
                this.calendarForLastSystemSQLName.setTimeInMillis(n);
                this.timeForLastSystemSQLName = n;
            }
            else {
                ++this.systemSQLNameNumber;
                if (this.systemSQLNameNumber == 10) {
                    this.systemSQLNameNumber = 0;
                    this.timeForLastSystemSQLName += 10L;
                    this.calendarForLastSystemSQLName.setTimeInMillis(this.timeForLastSystemSQLName);
                }
            }
            sb.append(twoDigits(this.calendarForLastSystemSQLName.get(1)));
            sb.append(twoDigits(this.calendarForLastSystemSQLName.get(2) + 1));
            sb.append(twoDigits(this.calendarForLastSystemSQLName.get(5)));
            sb.append(twoDigits(this.calendarForLastSystemSQLName.get(11)));
            sb.append(twoDigits(this.calendarForLastSystemSQLName.get(12)));
            sb.append(twoDigits(this.calendarForLastSystemSQLName.get(13)));
            sb.append(twoDigits(this.calendarForLastSystemSQLName.get(14) / 10));
            sb.append(this.systemSQLNameNumber);
        }
        return sb.toString();
    }
    
    private static String twoDigits(final int i) {
        String s;
        if (i < 10) {
            s = "0" + i;
        }
        else {
            s = Integer.toString(i).substring(Integer.toString(i).length() - 2);
        }
        return s;
    }
    
    public void setAutoincrementValue(final TransactionController transactionController, final UUID uuid, final String s, long n, final boolean b) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[2];
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, getIDValueAsCHAR(uuid));
        indexableRow.setColumn(2, new SQLChar(s));
        final ExecRow emptyRow = ((SYSCOLUMNSRowFactory)tabInfoImpl.getCatalogRowFactory()).makeEmptyRow();
        final boolean[] array = new boolean[2];
        for (int i = 0; i < 2; ++i) {
            array[i] = false;
        }
        final int[] array2 = { 7 };
        if (b) {
            n += ((NumberDataValue)tabInfoImpl.getRow(transactionController, indexableRow, 0).getColumn(9)).getLong();
        }
        emptyRow.setColumn(7, new SQLLongint(n));
        tabInfoImpl.updateRow(indexableRow, emptyRow, 0, array, array2, transactionController);
    }
    
    private RowLocation computeRowLocation(final TransactionController transactionController, final TableDescriptor tableDescriptor, final String s) throws StandardException {
        final TabInfoImpl tabInfoImpl = this.coreInfo[2];
        final UUID uuid = tableDescriptor.getUUID();
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, getIDValueAsCHAR(uuid));
        indexableRow.setColumn(2, new SQLChar(s));
        return tabInfoImpl.getRowLocation(transactionController, indexableRow, 0);
    }
    
    void computeSequenceRowLocation(final TransactionController transactionController, final String s, final RowLocation[] array, final SequenceDescriptor[] array2) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(20);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, new SQLChar(s));
        array[0] = nonCoreTI.getRowLocation(transactionController, indexableRow, 0);
        array2[0] = (SequenceDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false, 4, transactionController);
    }
    
    boolean updateCurrentSequenceValue(final TransactionController transactionController, final RowLocation rowLocation, final boolean b, final Long n, final Long n2) throws StandardException {
        final int n3 = 5;
        final FormatableBitSet set = new FormatableBitSet(10);
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(20);
        ConglomerateController openConglomerate = null;
        final ExecRow emptyRow = ((SYSSEQUENCESRowFactory)nonCoreTI.getCatalogRowFactory()).makeEmptyRow();
        set.set(n3 - 1);
        try {
            openConglomerate = transactionController.openConglomerate(nonCoreTI.getHeapConglomerate(), false, 0x4 | (b ? 0 : 128), 6, 4);
            openConglomerate.fetch(rowLocation, emptyRow.getRowArray(), set, b);
            final NumberDataValue numberDataValue = (NumberDataValue)emptyRow.getColumn(n3);
            SQLLongint sqlLongint;
            if (n == null) {
                sqlLongint = new SQLLongint();
            }
            else {
                sqlLongint = new SQLLongint((long)n);
            }
            if (n == null || sqlLongint.compare(numberDataValue) == 0) {
                SQLLongint sqlLongint2;
                if (n2 == null) {
                    sqlLongint2 = new SQLLongint();
                }
                else {
                    sqlLongint2 = new SQLLongint((long)n2);
                }
                emptyRow.setColumn(n3, sqlLongint2);
                openConglomerate.replace(rowLocation, emptyRow.getRowArray(), set);
                return true;
            }
            return false;
        }
        finally {
            if (openConglomerate != null) {
                openConglomerate.close();
            }
        }
    }
    
    public void getCurrentValueAndAdvance(final String s, final NumberDataValue numberDataValue) throws StandardException {
        SequenceUpdater sequenceUpdater = null;
        try {
            sequenceUpdater = (SequenceUpdater)this.sequenceGeneratorCache.find(s);
            sequenceUpdater.getCurrentValueAndAdvance(numberDataValue);
        }
        finally {
            if (sequenceUpdater != null) {
                this.sequenceGeneratorCache.release(sequenceUpdater);
            }
        }
    }
    
    public Long peekAtSequence(final String str, final String str2) throws StandardException {
        final String sequenceID = this.getSequenceID(str, str2);
        if (sequenceID == null) {
            throw StandardException.newException("X0X81.S", "SEQUENCE", str + "." + str2);
        }
        SequenceUpdater sequenceUpdater = null;
        try {
            sequenceUpdater = (SequenceUpdater)this.sequenceGeneratorCache.find(sequenceID);
            return sequenceUpdater.peekAtCurrentValue();
        }
        finally {
            if (sequenceUpdater != null) {
                this.sequenceGeneratorCache.release(sequenceUpdater);
            }
        }
    }
    
    public RowLocation getRowLocationTemplate(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor) throws StandardException {
        final ConglomerateController openConglomerate = languageConnectionContext.getTransactionCompile().openConglomerate(tableDescriptor.getHeapConglomerateId(), false, 0, 6, 2);
        RowLocation rowLocationTemplate;
        try {
            rowLocationTemplate = openConglomerate.newRowLocationTemplate();
        }
        finally {
            openConglomerate.close();
        }
        return rowLocationTemplate;
    }
    
    public void addTableDescriptorToOtherCache(final TableDescriptor tableDescriptor, final Cacheable cacheable) throws StandardException {
        final CacheManager cacheManager = (cacheable instanceof OIDTDCacheable) ? this.nameTdCache : this.OIDTdCache;
        Cacheable cacheable2 = null;
        Object uuid;
        if (cacheManager == this.nameTdCache) {
            uuid = new TableKey(tableDescriptor.getSchemaDescriptor().getUUID(), tableDescriptor.getName());
        }
        else {
            uuid = tableDescriptor.getUUID();
        }
        try {
            cacheable2 = cacheManager.create(uuid, tableDescriptor);
        }
        catch (StandardException ex) {
            if (!ex.getMessageId().equals("XBCA0.S")) {
                throw ex;
            }
        }
        finally {
            if (cacheable2 != null) {
                cacheManager.release(cacheable2);
            }
        }
    }
    
    public void dropStatisticsDescriptors(final UUID uuid, final UUID uuid2, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(14);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        ExecIndexRow execIndexRow;
        if (uuid2 != null) {
            execIndexRow = this.exFactory.getIndexableRow(2);
            execIndexRow.setColumn(2, getIDValueAsCHAR(uuid2));
        }
        else {
            execIndexRow = this.exFactory.getIndexableRow(1);
        }
        execIndexRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, execIndexRow, 0);
    }
    
    private static LanguageConnectionContext getLCC() {
        return (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
    }
    
    private SchemaDescriptor newSystemSchemaDesc(final String s, final String s2) {
        return new SchemaDescriptor(this, s, this.authorizationDatabaseOwner, this.uuidFactory.recreateUUID(s2), true);
    }
    
    private SchemaDescriptor newDeclaredGlobalTemporaryTablesSchemaDesc(final String s) {
        return new SchemaDescriptor(this, s, this.authorizationDatabaseOwner, null, false);
    }
    
    public boolean checkVersion(int majorVersionNumber, final String s) throws StandardException {
        if (majorVersionNumber == -1) {
            majorVersionNumber = this.softwareVersion.majorVersionNumber;
        }
        return this.dictionaryVersion.checkVersion(majorVersionNumber, s);
    }
    
    public boolean isReadOnlyUpgrade() {
        return this.readOnlyUpgrade;
    }
    
    void setReadOnlyUpgrade() {
        this.readOnlyUpgrade = true;
    }
    
    void createSystemSps(final TransactionController transactionController) throws StandardException {
        this.createSPSSet(transactionController, false, this.getSystemSchemaDescriptor().getUUID());
        this.createSPSSet(transactionController, true, this.getSysIBMSchemaDescriptor().getUUID());
    }
    
    protected void createSPSSet(final TransactionController transactionController, final boolean b, final UUID uuid) throws StandardException {
        final Properties queryDescriptions = this.getQueryDescriptions(b);
        final Enumeration<Object> keys = queryDescriptions.keys();
        final boolean b2 = true;
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            this.addSPSDescriptor(new SPSDescriptor(this, key, this.getUUIDFactory().createUUID(), uuid, uuid, 'S', !b2, queryDescriptions.getProperty(key), !b2), transactionController);
        }
    }
    
    private final UUID createSystemProcedureOrFunction(final String e, final UUID uuid, final String[] array, final TypeDescriptor[] array2, final int n, final int n2, final short n3, final boolean b, final boolean b2, final TypeDescriptor typeDescriptor, final HashSet set, final TransactionController transactionController, final String s) throws StandardException {
        int length = 0;
        if (array != null) {
            length = array.length;
        }
        int[] array3 = null;
        if (length != 0) {
            array3 = new int[length];
            final int n4 = length - n;
            for (int i = 0; i < n4; ++i) {
                array3[i] = 1;
            }
            for (int j = 0; j < n; ++j) {
                array3[n4 + j] = 4;
            }
        }
        final RoutineAliasInfo routineAliasInfo = new RoutineAliasInfo(e, length, array, array2, array3, n2, (short)0, n3, b, b2, false, true, typeDescriptor);
        final UUID uuid2 = this.getUUIDFactory().createUUID();
        this.addDescriptor(new AliasDescriptor(this, uuid2, e, uuid, s, (typeDescriptor == null) ? 'P' : 'F', (typeDescriptor == null) ? 'P' : 'F', false, routineAliasInfo, null), null, 7, false, transactionController);
        set.add(e);
        return uuid2;
    }
    
    private final UUID createSystemProcedureOrFunction(final String s, final UUID uuid, final String[] array, final TypeDescriptor[] array2, final int n, final int n2, final short n3, final boolean b, final boolean b2, final TypeDescriptor typeDescriptor, final HashSet set, final TransactionController transactionController) throws StandardException {
        return this.createSystemProcedureOrFunction(s, uuid, array, array2, n, n2, n3, b, b2, typeDescriptor, set, transactionController, "org.apache.derby.catalog.SystemProcedures");
    }
    
    private final void create_SYSCS_procedures(final TransactionController transactionController, final HashSet set) throws StandardException {
        final TypeDescriptor catalogType = DataTypeDescriptor.getCatalogType(12, 32672);
        final UUID uuid = this.getSystemUtilSchemaDescriptor().getUUID();
        this.createSystemProcedureOrFunction("SYSCS_SET_DATABASE_PROPERTY", uuid, new String[] { "KEY", "VALUE" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 32672) }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_COMPRESS_TABLE", uuid, new String[] { "SCHEMANAME", "TABLENAME", "SEQUENTIAL" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_CHECKPOINT_DATABASE", uuid, null, null, 0, 0, (short)2, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_FREEZE_DATABASE", uuid, null, null, 0, 0, (short)2, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_UNFREEZE_DATABASE", uuid, null, null, 0, 0, (short)2, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_BACKUP_DATABASE", uuid, new String[] { "BACKUPDIR" }, new TypeDescriptor[] { DataTypeDescriptor.getCatalogType(12, 32672) }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_BACKUP_DATABASE_AND_ENABLE_LOG_ARCHIVE_MODE", uuid, new String[] { "BACKUPDIR", "DELETE_ARCHIVED_LOG_FILES" }, new TypeDescriptor[] { DataTypeDescriptor.getCatalogType(12, 32672), TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_DISABLE_LOG_ARCHIVE_MODE", uuid, new String[] { "DELETE_ARCHIVED_LOG_FILES" }, new TypeDescriptor[] { TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_SET_RUNTIMESTATISTICS", uuid, new String[] { "ENABLE" }, new TypeDescriptor[] { TypeDescriptor.SMALLINT }, 0, 0, (short)2, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_SET_STATISTICS_TIMING", uuid, new String[] { "ENABLE" }, new TypeDescriptor[] { TypeDescriptor.SMALLINT }, 0, 0, (short)2, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_GET_DATABASE_PROPERTY", uuid, new String[] { "KEY" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)1, false, false, DataTypeDescriptor.getCatalogType(12, 32672), set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_CHECK_TABLE", uuid, new String[] { "SCHEMANAME", "TABLENAME" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)1, false, false, TypeDescriptor.INTEGER, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_GET_RUNTIMESTATISTICS", uuid, null, null, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(12, 32672), set, transactionController);
        final UUID uuid2 = this.getSchemaDescriptor("SQLJ", transactionController, true).getUUID();
        this.createSystemProcedureOrFunction("INSTALL_JAR", uuid2, new String[] { "URL", "JAR", "DEPLOY" }, new TypeDescriptor[] { DataTypeDescriptor.getCatalogType(12, 256), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.INTEGER }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("REPLACE_JAR", uuid2, new String[] { "URL", "JAR" }, new TypeDescriptor[] { DataTypeDescriptor.getCatalogType(12, 256), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("REMOVE_JAR", uuid2, new String[] { "JAR", "UNDEPLOY" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.INTEGER }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_EXPORT_TABLE", uuid, new String[] { "schemaName", "tableName", "fileName", " columnDelimiter", "characterDelimiter", "codeset" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, catalogType, DataTypeDescriptor.getCatalogType(1, 1), DataTypeDescriptor.getCatalogType(1, 1), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_EXPORT_QUERY", uuid, new String[] { "selectStatement", "fileName", " columnDelimiter", "characterDelimiter", "codeset" }, new TypeDescriptor[] { catalogType, catalogType, DataTypeDescriptor.getCatalogType(1, 1), DataTypeDescriptor.getCatalogType(1, 1), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_IMPORT_TABLE", uuid, new String[] { "schemaName", "tableName", "fileName", " columnDelimiter", "characterDelimiter", "codeset", "replace" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, catalogType, DataTypeDescriptor.getCatalogType(1, 1), DataTypeDescriptor.getCatalogType(1, 1), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_IMPORT_DATA", uuid, new String[] { "schemaName", "tableName", "insertColumnList", "columnIndexes", "fileName", " columnDelimiter", "characterDelimiter", "codeset", "replace" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, catalogType, catalogType, catalogType, DataTypeDescriptor.getCatalogType(1, 1), DataTypeDescriptor.getCatalogType(1, 1), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_BULK_INSERT", uuid, new String[] { "schemaName", "tableName", "vtiName", "vtiArg" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, catalogType, catalogType }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.create_10_1_system_procedures(transactionController, set, uuid);
        this.create_10_2_system_procedures(transactionController, set, uuid);
        this.create_10_3_system_procedures(transactionController, set);
        this.create_10_5_system_procedures(transactionController, set);
        this.create_10_6_system_procedures(transactionController, set);
        this.create_10_9_system_procedures(transactionController, set);
        this.create_10_10_system_procedures(transactionController, set);
    }
    
    protected final void create_SYSIBM_procedures(final TransactionController transactionController, final HashSet set) throws StandardException {
        final UUID uuid = this.getSysIBMSchemaDescriptor().getUUID();
        this.createSystemProcedureOrFunction("SQLCAMESSAGE", uuid, new String[] { "SQLCODE", "SQLERRML", "SQLERRMC", "SQLERRP", "SQLERRD0", "SQLERRD1", "SQLERRD2", "SQLERRD3", "SQLERRD4", "SQLERRD5", "SQLWARN", "SQLSTATE", "FILE", "LOCALE", "MESSAGE", "RETURNCODE" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, TypeDescriptor.SMALLINT, DataTypeDescriptor.getCatalogType(12, 2400), DataTypeDescriptor.getCatalogType(1, 8), TypeDescriptor.INTEGER, TypeDescriptor.INTEGER, TypeDescriptor.INTEGER, TypeDescriptor.INTEGER, TypeDescriptor.INTEGER, TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(1, 11), DataTypeDescriptor.getCatalogType(1, 5), DataTypeDescriptor.getCatalogType(12, 50), DataTypeDescriptor.getCatalogType(1, 5), DataTypeDescriptor.getCatalogType(12, 2400), TypeDescriptor.INTEGER }, 2, 0, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLPROCEDURES", uuid, new String[] { "CATALOGNAME", "SCHEMANAME", "PROCNAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLTABLEPRIVILEGES", uuid, new String[] { "CATALOGNAME", "SCHEMANAME", "TABLENAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLPRIMARYKEYS", uuid, new String[] { "CATALOGNAME", "SCHEMANAME", "TABLENAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLTABLES", uuid, new String[] { "CATALOGNAME", "SCHEMANAME", "TABLENAME", "TABLETYPE", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000), DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLPROCEDURECOLS", uuid, new String[] { "CATALOGNAME", "SCHEMANAME", "PROCNAME", "PARAMNAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLCOLUMNS", uuid, new String[] { "CATALOGNAME", "SCHEMANAME", "TABLENAME", "COLUMNNAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLCOLPRIVILEGES", uuid, new String[] { "CATALOGNAME", "SCHEMANAME", "TABLENAME", "COLUMNNAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLUDTS", uuid, new String[] { "CATALOGNAME", "SCHEMAPATTERN", "TYPENAMEPATTERN", "UDTTYPES", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLFOREIGNKEYS", uuid, new String[] { "PKCATALOGNAME", "PKSCHEMANAME", "PKTABLENAME", "FKCATALOGNAME", "FKSCHEMANAME", "FKTABLENAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLSPECIALCOLUMNS", uuid, new String[] { "COLTYPE", "CATALOGNAME", "SCHEMANAME", "TABLENAME", "SCOPE", "NULLABLE", "OPTIONS" }, new TypeDescriptor[] { TypeDescriptor.SMALLINT, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.SMALLINT, TypeDescriptor.SMALLINT, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLGETTYPEINFO", uuid, new String[] { "DATATYPE", "OPTIONS" }, new TypeDescriptor[] { TypeDescriptor.SMALLINT, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLSTATISTICS", uuid, new String[] { "CATALOGNAME", "SCHEMANAME", "TABLENAME", "UNIQUE", "RESERVED", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.SMALLINT, TypeDescriptor.SMALLINT, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("METADATA", uuid, null, null, 0, 1, (short)1, false, false, null, set, transactionController);
    }
    
    public void grantPublicAccessToSystemRoutines(final HashSet set, final TransactionController transactionController, final String s) throws StandardException {
        final String string = this.getSystemUtilSchemaDescriptor().getUUID().toString();
        for (int i = 0; i < DataDictionaryImpl.sysUtilProceduresWithPublicAccess.length; ++i) {
            final String o = DataDictionaryImpl.sysUtilProceduresWithPublicAccess[i];
            if (set.contains(o)) {
                this.grantPublicAccessToSystemRoutine(string, o, 'P', transactionController, s);
            }
        }
        for (int j = 0; j < DataDictionaryImpl.sysUtilFunctionsWithPublicAccess.length; ++j) {
            final String o2 = DataDictionaryImpl.sysUtilFunctionsWithPublicAccess[j];
            if (set.contains(o2)) {
                this.grantPublicAccessToSystemRoutine(string, o2, 'F', transactionController, s);
            }
        }
    }
    
    private void grantPublicAccessToSystemRoutine(final String s, final String s2, final char c, final TransactionController transactionController, final String s3) throws StandardException {
        final AliasDescriptor aliasDescriptor = this.getAliasDescriptor(s, s2, c);
        if (aliasDescriptor == null) {
            return;
        }
        this.createRoutinePermPublicDescriptor(aliasDescriptor.getUUID(), transactionController, s3);
    }
    
    void createRoutinePermPublicDescriptor(final UUID uuid, final TransactionController transactionController, final String s) throws StandardException {
        this.addDescriptor(new RoutinePermsDescriptor(this, "PUBLIC", s, uuid), null, 18, false, transactionController);
    }
    
    void create_10_1_system_procedures(final TransactionController transactionController, final HashSet set, final UUID uuid) throws StandardException {
        this.createSystemProcedureOrFunction("SYSCS_INPLACE_COMPRESS_TABLE", uuid, new String[] { "SCHEMANAME", "TABLENAME", "PURGE_ROWS", "DEFRAGMENT_ROWS", "TRUNCATE_END" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.SMALLINT, TypeDescriptor.SMALLINT, TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
    }
    
    void create_10_2_system_procedures(final TransactionController transactionController, final HashSet set, final UUID uuid) throws StandardException {
        this.createSystemProcedureOrFunction("SYSCS_BACKUP_DATABASE_NOWAIT", uuid, new String[] { "BACKUPDIR" }, new TypeDescriptor[] { DataTypeDescriptor.getCatalogType(12, 32672) }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_BACKUP_DATABASE_AND_ENABLE_LOG_ARCHIVE_MODE_NOWAIT", uuid, new String[] { "BACKUPDIR", "DELETE_ARCHIVED_LOG_FILES" }, new TypeDescriptor[] { DataTypeDescriptor.getCatalogType(12, 32672), TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLFUNCTIONS", this.getSysIBMSchemaDescriptor().getUUID(), new String[] { "CATALOGNAME", "SCHEMANAME", "FUNCNAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SQLFUNCTIONPARAMS", this.getSysIBMSchemaDescriptor().getUUID(), new String[] { "CATALOGNAME", "SCHEMANAME", "FUNCNAME", "PARAMNAME", "OPTIONS" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 4000) }, 0, 1, (short)1, false, false, null, set, transactionController);
    }
    
    private void create_10_3_system_procedures_SYSIBM(final TransactionController transactionController, final HashSet set) throws StandardException {
        final UUID uuid = this.getSysIBMSchemaDescriptor().getUUID();
        this.createSystemProcedureOrFunction("CLOBCREATELOCATOR", uuid, null, null, 0, 0, (short)2, false, false, TypeDescriptor.INTEGER, set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("CLOBRELEASELOCATOR", uuid, new String[] { "LOCATOR" }, new TypeDescriptor[] { TypeDescriptor.INTEGER }, 0, 0, (short)2, false, false, null, set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("CLOBGETPOSITIONFROMSTRING", uuid, new String[] { "LOCATOR", "SEARCHSTR", "POS" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(12), DataTypeDescriptor.getCatalogType(-5) }, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(-5), set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("CLOBGETPOSITIONFROMLOCATOR", uuid, new String[] { "LOCATOR", "SEARCHLOCATOR", "POS" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-5) }, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(-5), set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("CLOBGETLENGTH", uuid, new String[] { "LOCATOR" }, new TypeDescriptor[] { TypeDescriptor.INTEGER }, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(-5), set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("CLOBGETSUBSTRING", uuid, new String[] { "LOCATOR", "POS", "LEN" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-5), TypeDescriptor.INTEGER }, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(12, 10890), set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("CLOBSETSTRING", uuid, new String[] { "LOCATOR", "POS", "LEN", "REPLACESTR" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-5), TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(12) }, 0, 0, (short)2, false, false, null, set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("CLOBTRUNCATE", uuid, new String[] { "LOCATOR", "LEN" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-5) }, 0, 0, (short)2, false, false, null, set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("BLOBCREATELOCATOR", uuid, null, null, 0, 0, (short)2, false, false, TypeDescriptor.INTEGER, set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("BLOBRELEASELOCATOR", uuid, new String[] { "LOCATOR" }, new TypeDescriptor[] { TypeDescriptor.INTEGER }, 0, 0, (short)2, false, false, null, set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("BLOBGETPOSITIONFROMBYTES", uuid, new String[] { "LOCATOR", "SEARCHBYTES", "POS" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-3), DataTypeDescriptor.getCatalogType(-5) }, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(-5), set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("BLOBGETPOSITIONFROMLOCATOR", uuid, new String[] { "LOCATOR", "SEARCHLOCATOR", "POS" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-5) }, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(-5), set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("BLOBGETLENGTH", uuid, new String[] { "LOCATOR" }, new TypeDescriptor[] { TypeDescriptor.INTEGER }, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(-5), set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("BLOBGETBYTES", uuid, new String[] { "LOCATOR", "POS", "LEN" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-5), TypeDescriptor.INTEGER }, 0, 0, (short)2, false, false, DataTypeDescriptor.getCatalogType(-3, 32672), set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("BLOBSETBYTES", uuid, new String[] { "LOCATOR", "POS", "LEN", "REPLACEBYTES" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-5), TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-3) }, 0, 0, (short)2, false, false, null, set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
        this.createSystemProcedureOrFunction("BLOBTRUNCATE", uuid, new String[] { "LOCATOR", "LEN" }, new TypeDescriptor[] { TypeDescriptor.INTEGER, DataTypeDescriptor.getCatalogType(-5) }, 0, 0, (short)2, false, false, null, set, transactionController, "org.apache.derby.impl.jdbc.LOBStoredProcedure");
    }
    
    void create_10_5_system_procedures(final TransactionController transactionController, final HashSet set) throws StandardException {
        this.createSystemProcedureOrFunction("SYSCS_UPDATE_STATISTICS", this.getSystemUtilSchemaDescriptor().getUUID(), new String[] { "SCHEMANAME", "TABLENAME", "INDEXNAME" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)0, false, false, null, set, transactionController);
    }
    
    void create_10_6_system_procedures(final TransactionController transactionController, final HashSet set) throws StandardException {
        final UUID uuid = this.getSystemUtilSchemaDescriptor().getUUID();
        this.createSystemProcedureOrFunction("SYSCS_SET_XPLAIN_MODE", uuid, new String[] { "ENABLE" }, new TypeDescriptor[] { TypeDescriptor.INTEGER }, 0, 0, (short)2, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_GET_XPLAIN_MODE", uuid, null, null, 0, 0, (short)1, false, false, TypeDescriptor.INTEGER, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_SET_XPLAIN_SCHEMA", uuid, new String[] { "SCHEMANAME" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_GET_XPLAIN_SCHEMA", uuid, null, null, 0, 0, (short)1, false, false, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, set, transactionController);
    }
    
    void create_10_3_system_procedures(final TransactionController transactionController, final HashSet set) throws StandardException {
        this.create_10_3_system_procedures_SYSCS_UTIL(transactionController, set);
        this.create_10_3_system_procedures_SYSIBM(transactionController, set);
    }
    
    void create_10_3_system_procedures_SYSCS_UTIL(final TransactionController transactionController, final HashSet set) throws StandardException {
        final UUID uuid = this.getSystemUtilSchemaDescriptor().getUUID();
        this.createSystemProcedureOrFunction("SYSCS_EXPORT_TABLE_LOBS_TO_EXTFILE", uuid, new String[] { "schemaName", "tableName", "fileName", " columnDelimiter", "characterDelimiter", "codeset", "lobsFileName" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 32672), DataTypeDescriptor.getCatalogType(1, 1), DataTypeDescriptor.getCatalogType(1, 1), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 32672) }, 0, 0, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_EXPORT_QUERY_LOBS_TO_EXTFILE", uuid, new String[] { "selectStatement", "fileName", " columnDelimiter", "characterDelimiter", "codeset", "lobsFileName" }, new TypeDescriptor[] { DataTypeDescriptor.getCatalogType(12, 32672), DataTypeDescriptor.getCatalogType(12, 32672), DataTypeDescriptor.getCatalogType(1, 1), DataTypeDescriptor.getCatalogType(1, 1), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 32672) }, 0, 0, (short)1, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_IMPORT_TABLE_LOBS_FROM_EXTFILE", uuid, new String[] { "schemaName", "tableName", "fileName", " columnDelimiter", "characterDelimiter", "codeset", "replace" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 32672), DataTypeDescriptor.getCatalogType(1, 1), DataTypeDescriptor.getCatalogType(1, 1), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_IMPORT_DATA_LOBS_FROM_EXTFILE", uuid, new String[] { "schemaName", "tableName", "insertColumnList", "columnIndexes", "fileName", " columnDelimiter", "characterDelimiter", "codeset", "replace" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 32672), DataTypeDescriptor.getCatalogType(12, 32672), DataTypeDescriptor.getCatalogType(12, 32672), DataTypeDescriptor.getCatalogType(1, 1), DataTypeDescriptor.getCatalogType(1, 1), DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, TypeDescriptor.SMALLINT }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_RELOAD_SECURITY_POLICY", uuid, null, null, 0, 0, (short)3, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_SET_USER_ACCESS", uuid, new String[] { "USERNAME", "CONNECTIONPERMISSION" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_GET_USER_ACCESS", uuid, new String[] { "USERNAME" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)1, false, false, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_EMPTY_STATEMENT_CACHE", uuid, null, null, 0, 0, (short)3, false, false, null, set, transactionController);
    }
    
    void create_10_9_system_procedures(final TransactionController transactionController, final HashSet set) throws StandardException {
        final UUID uuid = this.getSystemUtilSchemaDescriptor().getUUID();
        this.createSystemProcedureOrFunction("SYSCS_CREATE_USER", uuid, new String[] { "userName", "password" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 32672) }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_RESET_PASSWORD", uuid, new String[] { "userName", "password" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataTypeDescriptor.getCatalogType(12, 32672) }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_MODIFY_PASSWORD", uuid, new String[] { "password" }, new TypeDescriptor[] { DataTypeDescriptor.getCatalogType(12, 32672) }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_DROP_USER", uuid, new String[] { "userName" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)0, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_PEEK_AT_SEQUENCE", uuid, new String[] { "schemaName", "sequenceName" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)1, false, false, DataTypeDescriptor.getCatalogType(-5), set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_DROP_STATISTICS", uuid, new String[] { "SCHEMANAME", "TABLENAME", "INDEXNAME" }, new TypeDescriptor[] { DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER, DataDictionaryImpl.CATALOG_TYPE_SYSTEM_IDENTIFIER }, 0, 0, (short)0, false, false, null, set, transactionController);
    }
    
    void create_10_10_system_procedures(final TransactionController transactionController, final HashSet set) throws StandardException {
        final UUID uuid = this.getSystemUtilSchemaDescriptor().getUUID();
        final TypeDescriptor catalogType = DataTypeDescriptor.getCatalogType(12, 32672);
        this.createSystemProcedureOrFunction("SYSCS_INVALIDATE_STORED_STATEMENTS", uuid, null, null, 0, 0, (short)3, false, false, null, set, transactionController);
        this.createSystemProcedureOrFunction("SYSCS_REGISTER_TOOL", uuid, new String[] { "toolName", "register", "optionalArgs" }, new TypeDescriptor[] { catalogType, DataTypeDescriptor.getCatalogType(16), catalogType }, 0, 0, (short)0, false, true, null, set, transactionController, "org.apache.derby.catalog.Java5SystemProcedures");
    }
    
    private final synchronized Properties getQueryDescriptions(final boolean b) {
        this.spsSet = (b ? "metadata_net.properties" : "/org/apache/derby/impl/jdbc/metadata.properties");
        return AccessController.doPrivileged((PrivilegedAction<Properties>)this);
    }
    
    public final Object run() {
        final Properties properties = new Properties();
        try {
            final InputStream resourceAsStream = this.getClass().getResourceAsStream(this.spsSet);
            properties.load(resourceAsStream);
            resourceAsStream.close();
        }
        catch (IOException ex) {}
        return properties;
    }
    
    private static List newSList() {
        return Collections.synchronizedList(new LinkedList<Object>());
    }
    
    public TablePermsDescriptor getTablePermissions(final UUID uuid, final String s) throws StandardException {
        return (TablePermsDescriptor)this.getPermissions(new TablePermsDescriptor(this, s, null, uuid));
    }
    
    public TablePermsDescriptor getTablePermissions(final UUID uuid) throws StandardException {
        return this.getUncachedTablePermsDescriptor(new TablePermsDescriptor(this, uuid));
    }
    
    private Object getPermissions(final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        final Cacheable find = this.getPermissionsCache().find(permissionsDescriptor);
        if (find == null) {
            return null;
        }
        final Object identity = find.getIdentity();
        this.getPermissionsCache().release(find);
        return identity;
    }
    
    public ColPermsDescriptor getColumnPermissions(final UUID uuid) throws StandardException {
        return this.getUncachedColPermsDescriptor(new ColPermsDescriptor(this, uuid));
    }
    
    public ColPermsDescriptor getColumnPermissions(final UUID uuid, final int n, final boolean b, final String s) throws StandardException {
        return (ColPermsDescriptor)this.getPermissions(new ColPermsDescriptor(this, s, null, uuid, b ? DataDictionaryImpl.colPrivTypeMapForGrant[n] : DataDictionaryImpl.colPrivTypeMap[n]));
    }
    
    public ColPermsDescriptor getColumnPermissions(final UUID uuid, final String s, final boolean b, final String s2) throws StandardException {
        return (ColPermsDescriptor)this.getPermissions(new ColPermsDescriptor(this, s2, null, uuid, s));
    }
    
    public RoutinePermsDescriptor getRoutinePermissions(final UUID uuid, final String s) throws StandardException {
        return (RoutinePermsDescriptor)this.getPermissions(new RoutinePermsDescriptor(this, s, null, uuid));
    }
    
    public RoutinePermsDescriptor getRoutinePermissions(final UUID uuid) throws StandardException {
        return this.getUncachedRoutinePermsDescriptor(new RoutinePermsDescriptor(this, uuid));
    }
    
    public boolean addRemovePermissionsDescriptor(final boolean b, final PermissionsDescriptor permissionsDescriptor, final String grantee, final TransactionController transactionController) throws StandardException {
        final int catalogNumber = permissionsDescriptor.getCatalogNumber();
        permissionsDescriptor.setUUID(null);
        permissionsDescriptor.setGrantee(grantee);
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(catalogNumber);
        final PermissionsCatalogRowFactory permissionsCatalogRowFactory = (PermissionsCatalogRowFactory)nonCoreTI.getCatalogRowFactory();
        final int primaryKeyIndexNumber = permissionsCatalogRowFactory.getPrimaryKeyIndexNumber();
        final ConglomerateController openConglomerate = transactionController.openConglomerate(nonCoreTI.getHeapConglomerate(), false, 0, 6, 4);
        try {
            openConglomerate.newRowLocationTemplate();
        }
        finally {
            openConglomerate.close();
        }
        final ExecIndexRow buildIndexKeyRow = permissionsCatalogRowFactory.buildIndexKeyRow(primaryKeyIndexNumber, permissionsDescriptor);
        final ExecRow row = nonCoreTI.getRow(transactionController, buildIndexKeyRow, primaryKeyIndexNumber);
        if (row == null) {
            if (!b) {
                return false;
            }
            nonCoreTI.insertRow(nonCoreTI.getCatalogRowFactory().makeRow(permissionsDescriptor, null), transactionController);
        }
        else {
            final boolean[] array = new boolean[row.nColumns()];
            final boolean[] array2 = new boolean[permissionsCatalogRowFactory.getNumIndexes()];
            int n;
            if (b) {
                n = permissionsCatalogRowFactory.orPermissions(row, permissionsDescriptor, array);
            }
            else {
                n = permissionsCatalogRowFactory.removePermissions(row, permissionsDescriptor, array);
            }
            if (n == 0) {
                return false;
            }
            if (!b) {
                permissionsCatalogRowFactory.setUUIDOfThePassedDescriptor(row, permissionsDescriptor);
            }
            if (n < 0) {
                nonCoreTI.deleteRow(transactionController, buildIndexKeyRow, primaryKeyIndexNumber);
            }
            else if (n > 0) {
                final int[] array3 = new int[n];
                int n2 = 0;
                for (int i = 0; i < array.length; ++i) {
                    if (array[i]) {
                        array3[n2++] = i + 1;
                    }
                }
                nonCoreTI.updateRow(buildIndexKeyRow, row, primaryKeyIndexNumber, array2, array3, transactionController);
            }
        }
        this.removePermEntryInCache(permissionsDescriptor);
        return !b;
    }
    
    TablePermsDescriptor getUncachedTablePermsDescriptor(final TablePermsDescriptor tablePermsDescriptor) throws StandardException {
        if (tablePermsDescriptor.getObjectID() == null) {
            return (TablePermsDescriptor)this.getUncachedPermissionsDescriptor(16, 0, tablePermsDescriptor);
        }
        return (TablePermsDescriptor)this.getUncachedPermissionsDescriptor(16, 1, tablePermsDescriptor);
    }
    
    ColPermsDescriptor getUncachedColPermsDescriptor(final ColPermsDescriptor colPermsDescriptor) throws StandardException {
        if (colPermsDescriptor.getObjectID() == null) {
            return (ColPermsDescriptor)this.getUncachedPermissionsDescriptor(17, 0, colPermsDescriptor);
        }
        return (ColPermsDescriptor)this.getUncachedPermissionsDescriptor(17, 1, colPermsDescriptor);
    }
    
    private TupleDescriptor getUncachedPermissionsDescriptor(final int n, final int n2, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(n);
        return this.getDescriptorViaIndex(n2, ((PermissionsCatalogRowFactory)nonCoreTI.getCatalogRowFactory()).buildIndexKeyRow(n2, permissionsDescriptor), null, nonCoreTI, null, null, false);
    }
    
    RoutinePermsDescriptor getUncachedRoutinePermsDescriptor(final RoutinePermsDescriptor routinePermsDescriptor) throws StandardException {
        if (routinePermsDescriptor.getObjectID() == null) {
            return (RoutinePermsDescriptor)this.getUncachedPermissionsDescriptor(18, 0, routinePermsDescriptor);
        }
        return (RoutinePermsDescriptor)this.getUncachedPermissionsDescriptor(18, 1, routinePermsDescriptor);
    }
    
    public String getVTIClass(final TableDescriptor tableDescriptor, final boolean b) throws StandardException {
        if ("SYSCS_DIAG".equals(tableDescriptor.getSchemaName())) {
            return this.getBuiltinVTIClass(tableDescriptor, b);
        }
        final String schemaName = tableDescriptor.getSchemaName();
        final String descriptorName = tableDescriptor.getDescriptorName();
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(tableDescriptor.getSchemaName(), null, true);
        if (schemaDescriptor == null) {
            return null;
        }
        final AliasDescriptor aliasDescriptor = this.getAliasDescriptor(schemaDescriptor.getUUID().toString(), descriptorName, 'F');
        if (aliasDescriptor != null && aliasDescriptor.isTableFunction()) {
            return aliasDescriptor.getJavaClassName();
        }
        throw StandardException.newException("42ZB4", schemaName, descriptorName);
    }
    
    public String getBuiltinVTIClass(final TableDescriptor tableDescriptor, final boolean b) throws StandardException {
        if ("SYSCS_DIAG".equals(tableDescriptor.getSchemaName())) {
            final String[][] array = b ? this.DIAG_VTI_TABLE_FUNCTION_CLASSES : this.DIAG_VTI_TABLE_CLASSES;
            for (int i = 0; i < array.length; ++i) {
                final String[] array2 = array[i];
                if (array2[0].equals(tableDescriptor.getDescriptorName())) {
                    return array2[1];
                }
            }
        }
        return null;
    }
    
    public RoleGrantDescriptor getRoleGrantDescriptor(final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(19);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        return (RoleGrantDescriptor)this.getDescriptorViaIndex(2, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public RoleGrantDescriptor getRoleDefinitionDescriptor(final String s) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(19);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLVarchar sqlVarchar2 = new SQLVarchar("Y");
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, sqlVarchar2);
        return (RoleGrantDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public RoleGrantDescriptor getRoleGrantDescriptor(final String s, final String s2, final String s3) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(19);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLVarchar sqlVarchar2 = new SQLVarchar(s2);
        final SQLVarchar sqlVarchar3 = new SQLVarchar(s3);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(3);
        indexableRow.setColumn(1, sqlVarchar);
        indexableRow.setColumn(2, sqlVarchar2);
        indexableRow.setColumn(3, sqlVarchar3);
        return (RoleGrantDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
    }
    
    public boolean existsGrantToAuthid(final String s, final TransactionController transactionController) throws StandardException {
        return this.existsPermByGrantee(s, transactionController, 16, 0, 1) || this.existsPermByGrantee(s, transactionController, 17, 0, 1) || this.existsPermByGrantee(s, transactionController, 18, 0, 1) || this.existsRoleGrantByGrantee(s, transactionController);
    }
    
    private void dropJDBCMetadataSPSes(final TransactionController transactionController) throws StandardException {
        for (final SPSDescriptor spsDescriptor : this.getAllSPSDescriptors()) {
            if (!spsDescriptor.getSchemaDescriptor().isSystemSchema()) {
                continue;
            }
            this.dropSPSDescriptor(spsDescriptor, transactionController);
            this.dropDependentsStoredDependencies(spsDescriptor.getUUID(), transactionController);
        }
    }
    
    public void updateMetadataSPSes(final TransactionController transactionController) throws StandardException {
        this.dropJDBCMetadataSPSes(transactionController);
        this.createSystemSps(transactionController);
    }
    
    public void dropSequenceDescriptor(final SequenceDescriptor sequenceDescriptor, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(20);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(sequenceDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        nonCoreTI.deleteRow(transactionController, indexableRow, 0);
        this.dropSequenceID(sequenceDescriptor);
    }
    
    public SequenceDescriptor getSequenceDescriptor(final UUID uuid) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(20);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        final SequenceDescriptor sequenceDescriptor = (SequenceDescriptor)this.getDescriptorViaIndex(0, indexableRow, null, nonCoreTI, null, null, false);
        this.putSequenceID(sequenceDescriptor);
        return sequenceDescriptor;
    }
    
    public SequenceDescriptor getSequenceDescriptor(final SchemaDescriptor schemaDescriptor, final String s) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(20);
        final SQLVarchar sqlVarchar = new SQLVarchar(s);
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(schemaDescriptor.getUUID());
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(2);
        indexableRow.setColumn(1, idValueAsCHAR);
        indexableRow.setColumn(2, sqlVarchar);
        final SequenceDescriptor sequenceDescriptor = (SequenceDescriptor)this.getDescriptorViaIndex(1, indexableRow, null, nonCoreTI, null, null, false);
        this.putSequenceID(sequenceDescriptor);
        return sequenceDescriptor;
    }
    
    private void putSequenceID(final SequenceDescriptor sequenceDescriptor) throws StandardException {
        if (sequenceDescriptor == null) {
            return;
        }
        final String schemaName = sequenceDescriptor.getSchemaDescriptor().getSchemaName();
        final String sequenceName = sequenceDescriptor.getSequenceName();
        final String string = sequenceDescriptor.getUUID().toString();
        HashMap<String, String> value = this.sequenceIDs.get(schemaName);
        if (value == null) {
            value = new HashMap<String, String>();
            this.sequenceIDs.put(schemaName, value);
        }
        if (value.get(sequenceName) == null) {
            value.put(sequenceName, string);
        }
    }
    
    private void dropSequenceID(final SequenceDescriptor sequenceDescriptor) throws StandardException {
        if (sequenceDescriptor == null) {
            return;
        }
        final String schemaName = sequenceDescriptor.getSchemaDescriptor().getSchemaName();
        final String sequenceName = sequenceDescriptor.getSequenceName();
        final HashMap hashMap = this.sequenceIDs.get(schemaName);
        if (hashMap == null) {
            return;
        }
        if (hashMap.get(sequenceName) == null) {
            return;
        }
        hashMap.remove(sequenceName);
    }
    
    private String getSequenceID(final String key, final String key2) throws StandardException {
        final HashMap<Object, String> hashMap = this.sequenceIDs.get(key);
        if (hashMap != null) {
            final String s = hashMap.get(key2);
            if (s != null) {
                return s;
            }
        }
        final SequenceDescriptor sequenceDescriptor = this.getSequenceDescriptor(this.getSchemaDescriptor(key, this.getTransactionCompile(), true), key2);
        if (sequenceDescriptor == null) {
            return null;
        }
        return sequenceDescriptor.getUUID().toString();
    }
    
    PermDescriptor getUncachedGenericPermDescriptor(final PermDescriptor permDescriptor) throws StandardException {
        if (permDescriptor.getObjectID() == null) {
            return (PermDescriptor)this.getUncachedPermissionsDescriptor(21, 2, permDescriptor);
        }
        return (PermDescriptor)this.getUncachedPermissionsDescriptor(21, 0, permDescriptor);
    }
    
    public PermDescriptor getGenericPermissions(final UUID uuid, final String s, final String s2, final String s3) throws StandardException {
        return (PermDescriptor)this.getPermissions(new PermDescriptor(this, null, s, uuid, s2, null, s3, false));
    }
    
    public PermDescriptor getGenericPermissions(final UUID uuid) throws StandardException {
        return this.getUncachedGenericPermDescriptor(new PermDescriptor(this, uuid));
    }
    
    public void dropAllPermDescriptors(final UUID uuid, final TransactionController transactionController) throws StandardException {
        final TabInfoImpl nonCoreTI = this.getNonCoreTI(21);
        final SYSPERMSRowFactory syspermsRowFactory = (SYSPERMSRowFactory)nonCoreTI.getCatalogRowFactory();
        if (!this.usesSqlAuthorization) {
            return;
        }
        final SQLChar idValueAsCHAR = getIDValueAsCHAR(uuid);
        final ExecIndexRow indexableRow = this.exFactory.getIndexableRow(1);
        indexableRow.setColumn(1, idValueAsCHAR);
        ExecRow row;
        while ((row = nonCoreTI.getRow(transactionController, indexableRow, 1)) != null) {
            final PermissionsDescriptor permissionsDescriptor = (PermissionsDescriptor)syspermsRowFactory.buildDescriptor(row, null, this);
            this.removePermEntryInCache(permissionsDescriptor);
            nonCoreTI.deleteRow(transactionController, syspermsRowFactory.buildIndexKeyRow(0, permissionsDescriptor), 0);
        }
    }
    
    public IndexStatisticsDaemon getIndexStatsRefresher(final boolean b) {
        if (this.indexStatsUpdateDisabled && b) {
            return null;
        }
        return this.indexRefresher;
    }
    
    public void disableIndexStatsRefresher() {
        if (!this.indexStatsUpdateDisabled) {
            this.indexStatsUpdateDisabled = true;
            this.indexRefresher.stop();
        }
    }
    
    public boolean doCreateIndexStatsRefresher() {
        return this.indexRefresher == null;
    }
    
    public void createIndexStatsRefresher(final Database database, final String s) {
        if (this.af.isReadOnly()) {
            this.indexStatsUpdateDisabled = true;
            return;
        }
        this.indexRefresher = new IndexStatisticsDaemonImpl(Monitor.getStream(), this.indexStatsUpdateLogging, this.indexStatsUpdateTracing, database, this.authorizationDatabaseOwner, s);
    }
    
    public DependableFinder getDependableFinder(final int n) {
        return new DDdependableFinder(n);
    }
    
    public DependableFinder getColumnDependableFinder(final int n, final byte[] array) {
        return new DDColumnDependableFinder(n, array);
    }
    
    static {
        SYSFUN_FUNCTIONS = new String[][] { { "ACOS", "DOUBLE", "java.lang.StrictMath", "acos(double)", "true", "false", "DOUBLE" }, { "ASIN", "DOUBLE", "java.lang.StrictMath", "asin(double)", "true", "false", "DOUBLE" }, { "ATAN", "DOUBLE", "java.lang.StrictMath", "atan(double)", "true", "false", "DOUBLE" }, { "ATAN2", "DOUBLE", "java.lang.StrictMath", "atan2(double,double)", "true", "false", "DOUBLE", "DOUBLE" }, { "COS", "DOUBLE", "java.lang.StrictMath", "cos(double)", "true", "false", "DOUBLE" }, { "SIN", "DOUBLE", "java.lang.StrictMath", "sin(double)", "true", "false", "DOUBLE" }, { "TAN", "DOUBLE", "java.lang.StrictMath", "tan(double)", "true", "false", "DOUBLE" }, { "PI", "DOUBLE", "org.apache.derby.catalog.SystemProcedures", "PI()", "true", "false" }, { "DEGREES", "DOUBLE", "java.lang.StrictMath", "toDegrees(double)", "true", "false", "DOUBLE" }, { "RADIANS", "DOUBLE", "java.lang.StrictMath", "toRadians(double)", "true", "false", "DOUBLE" }, { "LN", "DOUBLE", "java.lang.StrictMath", "log(double)", "true", "false", "DOUBLE" }, { "LOG", "DOUBLE", "java.lang.StrictMath", "log(double)", "true", "false", "DOUBLE" }, { "LOG10", "DOUBLE", "org.apache.derby.catalog.SystemProcedures", "LOG10(double)", "true", "false", "DOUBLE" }, { "EXP", "DOUBLE", "java.lang.StrictMath", "exp(double)", "true", "false", "DOUBLE" }, { "CEIL", "DOUBLE", "java.lang.StrictMath", "ceil(double)", "true", "false", "DOUBLE" }, { "CEILING", "DOUBLE", "java.lang.StrictMath", "ceil(double)", "true", "false", "DOUBLE" }, { "FLOOR", "DOUBLE", "java.lang.StrictMath", "floor(double)", "true", "false", "DOUBLE" }, { "SIGN", "INTEGER", "org.apache.derby.catalog.SystemProcedures", "SIGN(double)", "true", "false", "DOUBLE" }, { "RANDOM", "DOUBLE", "java.lang.StrictMath", "random()", "false", "false" }, { "RAND", "DOUBLE", "org.apache.derby.catalog.SystemProcedures", "RAND(int)", "false", "false", "INTEGER" }, { "COT", "DOUBLE", "org.apache.derby.catalog.SystemProcedures", "COT(double)", "true", "false", "DOUBLE" }, { "COSH", "DOUBLE", "org.apache.derby.catalog.SystemProcedures", "COSH(double)", "true", "false", "DOUBLE" }, { "SINH", "DOUBLE", "org.apache.derby.catalog.SystemProcedures", "SINH(double)", "true", "false", "DOUBLE" }, { "TANH", "DOUBLE", "org.apache.derby.catalog.SystemProcedures", "TANH(double)", "true", "false", "DOUBLE" } };
        nonCoreNames = new String[] { "SYSCONSTRAINTS", "SYSKEYS", "SYSDEPENDS", "SYSALIASES", "SYSVIEWS", "SYSCHECKS", "SYSFOREIGNKEYS", "SYSSTATEMENTS", "SYSFILES", "SYSTRIGGERS", "SYSSTATISTICS", "SYSDUMMY1", "SYSTABLEPERMS", "SYSCOLPERMS", "SYSROUTINEPERMS", "SYSROLES", "SYSSEQUENCES", "SYSPERMS", "SYSUSERS" };
        NUM_NONCORE = DataDictionaryImpl.nonCoreNames.length;
        systemSchemaNames = new String[] { "SYSCAT", "SYSFUN", "SYSPROC", "SYSSTAT", "NULLID", "SYSCS_DIAG", "SYSCS_UTIL", "SYSIBM", "SQLJ", "SYS" };
        sysUtilProceduresWithPublicAccess = new String[] { "SYSCS_SET_RUNTIMESTATISTICS", "SYSCS_SET_STATISTICS_TIMING", "SYSCS_INPLACE_COMPRESS_TABLE", "SYSCS_COMPRESS_TABLE", "SYSCS_UPDATE_STATISTICS", "SYSCS_MODIFY_PASSWORD", "SYSCS_DROP_STATISTICS" };
        sysUtilFunctionsWithPublicAccess = new String[] { "SYSCS_GET_RUNTIMESTATISTICS", "SYSCS_PEEK_AT_SEQUENCE" };
        OFFSET_COMPARATOR = new Comparator() {
            public int compare(final Object o, final Object o2) {
                return ((ColumnReference)o).getBeginOffset() - ((ColumnReference)o2).getBeginOffset();
            }
        };
        colPrivTypeMap = new String[9];
        colPrivTypeMapForGrant = new String[9];
        DataDictionaryImpl.colPrivTypeMap[8] = "s";
        DataDictionaryImpl.colPrivTypeMapForGrant[8] = "S";
        DataDictionaryImpl.colPrivTypeMap[0] = "s";
        DataDictionaryImpl.colPrivTypeMapForGrant[0] = "S";
        DataDictionaryImpl.colPrivTypeMap[1] = "u";
        DataDictionaryImpl.colPrivTypeMapForGrant[1] = "U";
        DataDictionaryImpl.colPrivTypeMap[2] = "r";
        DataDictionaryImpl.colPrivTypeMapForGrant[2] = "R";
    }
}
