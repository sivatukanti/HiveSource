// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.reference;

public interface Property
{
    public static final String PROPERTIES_FILE = "derby.properties";
    public static final String PROPERTY_RUNTIME_PREFIX = "derby.__rt.";
    public static final String LOG_SEVERITY_LEVEL = "derby.stream.error.logSeverityLevel";
    public static final String EXT_DIAG_SEVERITY_LEVEL = "derby.stream.error.extendedDiagSeverityLevel";
    public static final String LOG_BOOT_TRACE = "derby.stream.error.logBootTrace";
    public static final String ERRORLOG_FILE_PROPERTY = "derby.stream.error.file";
    public static final String ERRORLOG_METHOD_PROPERTY = "derby.stream.error.method";
    public static final String ERRORLOG_FIELD_PROPERTY = "derby.stream.error.field";
    public static final String LOG_FILE_APPEND = "derby.infolog.append";
    public static final String SYSTEM_HOME_PROPERTY = "derby.system.home";
    public static final String BOOT_ALL = "derby.system.bootAll";
    public static final String NO_AUTO_BOOT = "derby.database.noAutoBoot";
    public static final String DELETE_ON_CREATE = "derby.__deleteOnCreate";
    public static final String FORCE_DATABASE_LOCK = "derby.database.forceDatabaseLock";
    public static final String LOCKS_INTRO = "derby.locks.";
    public static final String LOCKS_ESCALATION_THRESHOLD = "derby.locks.escalationThreshold";
    public static final int DEFAULT_LOCKS_ESCALATION_THRESHOLD = 5000;
    public static final int MIN_LOCKS_ESCALATION_THRESHOLD = 100;
    public static final String DEADLOCK_TIMEOUT = "derby.locks.deadlockTimeout";
    public static final int DEADLOCK_TIMEOUT_DEFAULT = 20;
    public static final int WAIT_TIMEOUT_DEFAULT = 60;
    public static final String DEADLOCK_MONITOR = "derby.locks.monitor";
    public static final String DEADLOCK_TRACE = "derby.locks.deadlockTrace";
    public static final String LOCKWAIT_TIMEOUT = "derby.locks.waitTimeout";
    public static final String DATABASE_CLASSPATH = "derby.database.classpath";
    public static final String BOOT_DB_CLASSPATH = "derby.__rt.database.classpath";
    public static final String DATABASE_PROPERTIES_ONLY = "derby.database.propertiesOnly";
    public static final String DERBY_INSTALL_URL = "derby.install.url";
    public static final String DERBY_SECURITY_HOST = "derby.security.host";
    public static final String DERBY_SECURITY_PORT = "derby.security.port";
    public static final String CREATE_WITH_NO_LOG = "derby.__rt.storage.createWithNoLog";
    public static final String PAGE_SIZE_PARAMETER = "derby.storage.pageSize";
    public static final String PAGE_SIZE_DEFAULT_LONG = "32768";
    public static final int TBL_PAGE_SIZE_BUMP_THRESHOLD = 4096;
    public static final int IDX_PAGE_SIZE_BUMP_THRESHOLD = 1024;
    public static final String ROW_LOCKING = "derby.storage.rowLocking";
    public static final String PROPERTIES_CONGLOM_ID = "derby.storage.propertiesId";
    public static final String STORAGE_TEMP_DIRECTORY = "derby.storage.tempDirectory";
    public static final String DURABILITY_PROPERTY = "derby.system.durability";
    public static final String DURABILITY_TESTMODE_NO_SYNC = "test";
    public static final String FILESYNC_TRANSACTION_LOG = "derby.storage.fileSyncTransactionLog";
    public static final String LOG_ARCHIVE_MODE = "derby.storage.logArchiveMode";
    public static final String LOG_DEVICE_AT_BACKUP = "derby.storage.logDeviceWhenBackedUp";
    public static final String MODULE_PREFIX = "derby.module.";
    public static final String SUB_SUB_PROTOCOL_PREFIX = "derby.subSubProtocol.";
    public static final String MODULE_ENV_JDK_PREFIX = "derby.env.jdk.";
    public static final String MODULE_ENV_CLASSES_PREFIX = "derby.env.classes.";
    public static final String LANG_TD_CACHE_SIZE = "derby.language.tableDescriptorCacheSize";
    public static final int LANG_TD_CACHE_SIZE_DEFAULT = 64;
    public static final String LANG_PERMISSIONS_CACHE_SIZE = "derby.language.permissionsCacheSize";
    public static final int LANG_PERMISSIONS_CACHE_SIZE_DEFAULT = 64;
    public static final String LANG_SPS_CACHE_SIZE = "derby.language.spsCacheSize";
    public static final int LANG_SPS_CACHE_SIZE_DEFAULT = 32;
    public static final String LANG_SEQGEN_CACHE_SIZE = "derby.language.sequenceGeneratorCacheSize";
    public static final int LANG_SEQGEN_CACHE_SIZE_DEFAULT = 32;
    public static final String LANG_SEQUENCE_PREALLOCATOR = "derby.language.sequence.preallocator";
    public static final String LANGUAGE_STALE_PLAN_CHECK_INTERVAL = "derby.language.stalePlanCheckInterval";
    public static final int DEFAULT_LANGUAGE_STALE_PLAN_CHECK_INTERVAL = 100;
    public static final int MIN_LANGUAGE_STALE_PLAN_CHECK_INTERVAL = 5;
    public static final String STATEMENT_CACHE_SIZE = "derby.language.statementCacheSize";
    public static final int STATEMENT_CACHE_SIZE_DEFAULT = 100;
    public static final String STORAGE_AUTO_INDEX_STATS = "derby.storage.indexStats.auto";
    public static final String STORAGE_AUTO_INDEX_STATS_LOGGING = "derby.storage.indexStats.log";
    public static final String STORAGE_AUTO_INDEX_STATS_TRACING = "derby.storage.indexStats.trace";
    public static final String STORAGE_AUTO_INDEX_STATS_DEBUG_CREATE_THRESHOLD = "derby.storage.indexStats.debug.createThreshold";
    public static final int STORAGE_AUTO_INDEX_STATS_DEBUG_CREATE_THRESHOLD_DEFAULT = 100;
    public static final String STORAGE_AUTO_INDEX_STATS_DEBUG_ABSDIFF_THRESHOLD = "derby.storage.indexStats.debug.absdiffThreshold";
    public static final int STORAGE_AUTO_INDEX_STATS_DEBUG_ABSDIFF_THRESHOLD_DEFAULT = 1000;
    public static final String STORAGE_AUTO_INDEX_STATS_DEBUG_LNDIFF_THRESHOLD = "derby.storage.indexStats.debug.lndiffThreshold";
    public static final double STORAGE_AUTO_INDEX_STATS_DEBUG_LNDIFF_THRESHOLD_DEFAULT = 1.0;
    public static final String STORAGE_AUTO_INDEX_STATS_DEBUG_QUEUE_SIZE = "derby.storage.indexStats.debug.queueSize";
    public static final int STORAGE_AUTO_INDEX_STATS_DEBUG_QUEUE_SIZE_DEFAULT = 20;
    public static final String STORAGE_AUTO_INDEX_STATS_DEBUG_KEEP_DISPOSABLE_STATS = "derby.storage.indexStats.debug.keepDisposableStats";
    public static final String PROP_XA_TRANSACTION_TIMEOUT = "derby.jdbc.xaTransactionTimeout";
    public static final int DEFAULT_XA_TRANSACTION_TIMEOUT = 0;
    public static final String DEFAULT_USER_NAME = "APP";
    public static final String DATABASE_MODULE = "org.apache.derby.database.Database";
    public static final String SQL_AUTHORIZATION_PROPERTY = "derby.database.sqlAuthorization";
    public static final String DEFAULT_CONNECTION_MODE_PROPERTY = "derby.database.defaultConnectionMode";
    public static final String NO_ACCESS = "NOACCESS";
    public static final String READ_ONLY_ACCESS = "READONLYACCESS";
    public static final String FULL_ACCESS = "FULLACCESS";
    public static final String READ_ONLY_ACCESS_USERS_PROPERTY = "derby.database.readOnlyAccessUsers";
    public static final String FULL_ACCESS_USERS_PROPERTY = "derby.database.fullAccessUsers";
    public static final String REQUIRE_AUTHENTICATION_PARAMETER = "derby.connection.requireAuthentication";
    public static final String AUTHENTICATION_PROVIDER_PARAMETER = "derby.authentication.provider";
    public static final String USER_PROPERTY_PREFIX = "derby.user.";
    public static final String AUTHENTICATION_PROVIDER_NATIVE = "NATIVE:";
    public static final String AUTHENTICATION_PROVIDER_BUILTIN = "BUILTIN";
    public static final String AUTHENTICATION_PROVIDER_LDAP = "LDAP";
    public static final String AUTHENTICATION_SERVER_PARAMETER = "derby.authentication.server";
    public static final String AUTHENTICATION_PROVIDER_LOCAL_SUFFIX = ":LOCAL";
    public static final String AUTHENTICATION_PROVIDER_NATIVE_LOCAL = "NATIVE::LOCAL";
    public static final String AUTHENTICATION_NATIVE_PASSWORD_LIFETIME = "derby.authentication.native.passwordLifetimeMillis";
    public static final long MILLISECONDS_IN_DAY = 86400000L;
    public static final long AUTHENTICATION_NATIVE_PASSWORD_LIFETIME_DEFAULT = 2678400000L;
    public static final String AUTHENTICATION_PASSWORD_EXPIRATION_THRESHOLD = "derby.authentication.native.passwordLifetimeThreshold";
    public static final double AUTHENTICATION_PASSWORD_EXPIRATION_THRESHOLD_DEFAULT = 0.125;
    public static final String AUTHENTICATION_BUILTIN_ALGORITHM = "derby.authentication.builtin.algorithm";
    public static final String AUTHENTICATION_BUILTIN_ALGORITHM_DEFAULT = "SHA-256";
    public static final String AUTHENTICATION_BUILTIN_ALGORITHM_FALLBACK = "SHA-1";
    public static final String AUTHENTICATION_BUILTIN_SALT_LENGTH = "derby.authentication.builtin.saltLength";
    public static final int AUTHENTICATION_BUILTIN_SALT_LENGTH_DEFAULT = 16;
    public static final String AUTHENTICATION_BUILTIN_ITERATIONS = "derby.authentication.builtin.iterations";
    public static final int AUTHENTICATION_BUILTIN_ITERATIONS_DEFAULT = 1000;
    public static final String LOG_SWITCH_INTERVAL = "derby.storage.logSwitchInterval";
    public static final String CHECKPOINT_INTERVAL = "derby.storage.checkpointInterval";
    public static final String LOG_ARCHIVAL_DIRECTORY = "derby.storage.logArchive";
    public static final String LOG_BUFFER_SIZE = "derby.storage.logBufferSize";
    public static final String REPLICATION_LOG_BUFFER_SIZE = "derby.replication.logBufferSize";
    public static final String REPLICATION_MIN_SHIPPING_INTERVAL = "derby.replication.minLogShippingInterval";
    public static final String REPLICATION_MAX_SHIPPING_INTERVAL = "derby.replication.maxLogShippingInterval";
    public static final String REPLICATION_VERBOSE = "derby.replication.verbose";
    public static final String ALPHA_BETA_ALLOW_UPGRADE = "derby.database.allowPreReleaseUpgrade";
    public static final String IN_RESTORE_FROM_BACKUP = "derby.__rt.inRestore";
    public static final String DELETE_ROOT_ON_ERROR = "derby.__rt.deleteRootOnError";
    public static final String HTTP_DB_FILE_OFFSET = "db2j.http.file.offset";
    public static final String HTTP_DB_FILE_LENGTH = "db2j.http.file.length";
    public static final String HTTP_DB_FILE_NAME = "db2j.http.file.name";
    public static final String START_DRDA = "derby.drda.startNetworkServer";
    public static final String DRDA_PROP_LOGCONNECTIONS = "derby.drda.logConnections";
    public static final String DRDA_PROP_TRACEALL = "derby.drda.traceAll";
    public static final String DRDA_PROP_TRACE = "derby.drda.trace";
    public static final String DRDA_PROP_TRACEDIRECTORY = "derby.drda.traceDirectory";
    public static final String DRDA_PROP_MINTHREADS = "derby.drda.minThreads";
    public static final String DRDA_PROP_MAXTHREADS = "derby.drda.maxThreads";
    public static final String DRDA_PROP_TIMESLICE = "derby.drda.timeSlice";
    public static final String DRDA_PROP_SSL_MODE = "derby.drda.sslMode";
    public static final String DRDA_PROP_SECURITYMECHANISM = "derby.drda.securityMechanism";
    public static final String DRDA_PROP_PORTNUMBER = "derby.drda.portNumber";
    public static final String DRDA_PROP_HOSTNAME = "derby.drda.host";
    public static final String DRDA_PROP_KEEPALIVE = "derby.drda.keepAlive";
    public static final String DRDA_PROP_STREAMOUTBUFFERSIZE = "derby.drda.streamOutBufferSize";
    public static final String SERVICE_PROTOCOL = "derby.serviceProtocol";
    public static final String SERVICE_LOCALE = "derby.serviceLocale";
    public static final String COLLATION = "derby.database.collation";
    public static final String UCS_BASIC_COLLATION = "UCS_BASIC";
    public static final String TERRITORY_BASED_COLLATION = "TERRITORY_BASED";
    public static final String TERRITORY_BASED_PRIMARY_COLLATION = "TERRITORY_BASED:PRIMARY";
    public static final String TERRITORY_BASED_SECONDARY_COLLATION = "TERRITORY_BASED:SECONDARY";
    public static final String TERRITORY_BASED_TERTIARY_COLLATION = "TERRITORY_BASED:TERTIARY";
    public static final String TERRITORY_BASED_IDENTICAL_COLLATION = "TERRITORY_BASED:IDENTICAL";
    public static final String COLLATION_NONE = "NONE";
    public static final String STORAGE_DATA_NOT_SYNCED_AT_CHECKPOINT = "db2j.storage.dataNotSyncedAtCheckPoint";
    public static final String STORAGE_DATA_NOT_SYNCED_AT_ALLOCATION = "db2j.storage.dataNotSyncedAtAllocation";
    public static final String STORAGE_LOG_NOT_SYNCED = "db2j.storage.logNotSynced";
    public static final String STORAGE_USE_DEFAULT_FILE_PERMISSIONS = "derby.storage.useDefaultFilePermissions";
    public static final String SERVER_STARTED_FROM_CMD_LINE = "derby.__serverStartedFromCmdLine";
}
