// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.sql.conn.StatementContext;
import java.sql.Savepoint;
import java.util.Iterator;
import org.apache.derby.iapi.jdbc.EngineLOB;
import java.sql.Blob;
import java.sql.Clob;
import org.apache.derby.iapi.jdbc.ExceptionFactory;
import org.apache.derby.iapi.store.access.XATransactionController;
import java.util.Enumeration;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.io.IOException;
import java.security.AccessControlException;
import java.security.Permission;
import org.apache.derby.security.DatabasePermission;
import java.util.Collections;
import java.util.Map;
import org.apache.derby.iapi.sql.execute.ExecutionContext;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.jdbc.AuthenticationService;
import org.apache.derby.impl.jdbc.authentication.NoneAuthenticationServiceImpl;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.impl.db.SlaveDatabase;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.error.SQLWarningFactory;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.db.Database;
import java.util.Properties;
import java.sql.Connection;
import org.apache.derby.jdbc.InternalDriver;
import java.sql.SQLWarning;
import java.util.HashSet;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.sql.DatabaseMetaData;
import org.apache.derby.iapi.services.memory.LowMemory;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.jdbc.EngineConnection;

public class EmbedConnection implements EngineConnection
{
    protected static final StandardException exceptionClose;
    public static final SQLException NO_MEM;
    public static final LowMemory memoryState;
    DatabaseMetaData dbMetadata;
    TransactionResourceImpl tr;
    private HashMap lobHashMap;
    private int lobHMKey;
    private WeakHashMap lobReferences;
    private HashSet lobFiles;
    private boolean active;
    private boolean aborting;
    boolean autoCommit;
    boolean needCommit;
    private boolean usingNoneAuth;
    private int connectionHoldAbility;
    final EmbedConnection rootConnection;
    private SQLWarning topWarning;
    private InternalDriver factory;
    private Connection applicationConnection;
    private int resultSetId;
    private String connString;
    private static final int OP_ENCRYPT = 0;
    private static final int OP_SHUTDOWN = 1;
    private static final int OP_HARD_UPGRADE = 2;
    private static final int OP_REPLICATION = 3;
    private static final int OP_DECRYPT = 4;
    
    public EmbedConnection(final InternalDriver factory, final String s, Properties removePhaseTwoProps) throws SQLException {
        this.lobHashMap = null;
        this.lobHMKey = 0;
        this.lobReferences = null;
        this.aborting = false;
        this.autoCommit = true;
        this.connectionHoldAbility = 1;
        this.rootConnection = this;
        this.applicationConnection = this;
        this.factory = factory;
        this.tr = new TransactionResourceImpl(factory, s, removePhaseTwoProps);
        this.active = true;
        this.setupContextStack();
        try {
            this.pushConnectionContext(this.tr.getContextManager());
            final boolean true = isTrue(removePhaseTwoProps, "shutdown");
            final Database database = (Database)Monitor.findService("org.apache.derby.database.Database", this.tr.getDBName());
            if (database != null && this.isCryptoBoot(removePhaseTwoProps)) {
                this.addWarning(SQLWarningFactory.newSQLWarning("01J17"));
            }
            final boolean boot = this.createBoot(removePhaseTwoProps);
            int n = (!boot && this.isCryptoBoot(removePhaseTwoProps)) ? 1 : 0;
            boolean b = !boot && this.isHardUpgradeBoot(removePhaseTwoProps);
            final boolean startReplicationSlaveBoot = this.isStartReplicationSlaveBoot(removePhaseTwoProps);
            boolean b2 = false;
            boolean b3 = false;
            boolean b4 = false;
            final boolean dropDatabase = this.isDropDatabase(removePhaseTwoProps);
            if (true && dropDatabase) {
                throw newSQLException("XJ048.C", "shutdown, drop");
            }
            if (n != 0) {
                this.checkConflictingCryptoAttributes(removePhaseTwoProps);
            }
            final String replicationOperation = this.getReplicationOperation(removePhaseTwoProps);
            if (replicationOperation != null && (boot || true || dropDatabase || n != 0 || b)) {
                throw StandardException.newException("XRE10", replicationOperation);
            }
            if (this.isReplicationFailover(removePhaseTwoProps)) {
                this.checkDatabaseBooted(database, "failover", this.tr.getDBName());
                if (database.isInSlaveMode()) {
                    b4 = true;
                }
                else {
                    b3 = true;
                }
            }
            Properties properties = null;
            if (startReplicationSlaveBoot) {
                if (database != null) {
                    b2 = true;
                }
                else {
                    removePhaseTwoProps.setProperty("replication.slave.mode", "slavepremode");
                }
            }
            if (this.isStopReplicationSlaveBoot(removePhaseTwoProps)) {
                this.handleStopReplicationSlave(database, removePhaseTwoProps);
            }
            else {
                if (this.isInternalShutdownSlaveDatabase(removePhaseTwoProps)) {
                    this.internalStopReplicationSlave(database, removePhaseTwoProps);
                    return;
                }
                if (b4) {
                    this.handleFailoverSlave(database);
                }
            }
            if (database != null) {
                this.tr.setDatabase(database);
                n = 0;
                b = false;
            }
            else if (!true) {
                if (n != 0 || b) {
                    properties = removePhaseTwoProps;
                    removePhaseTwoProps = this.removePhaseTwoProps((Properties)removePhaseTwoProps.clone());
                }
                if (!this.bootDatabase(removePhaseTwoProps, b)) {
                    this.tr.clearContextInError();
                    this.setInactive();
                    return;
                }
            }
            if (boot && !true && !dropDatabase) {
                if (this.tr.getDatabase() != null) {
                    this.addWarning(SQLWarningFactory.newSQLWarning("01J01", this.getDBName()));
                }
                else {
                    this.checkUserCredentials(true, null, removePhaseTwoProps);
                    this.tr.setDatabase(this.createDatabase(this.tr.getDBName(), removePhaseTwoProps));
                }
            }
            if (this.tr.getDatabase() == null) {
                this.handleDBNotFound();
            }
            try {
                this.checkUserCredentials(false, this.tr.getDBName(), removePhaseTwoProps);
            }
            catch (SQLException ex) {
                if (startReplicationSlaveBoot && !b2) {
                    this.tr.startTransaction();
                    this.handleException(this.tr.shutdownDatabaseException());
                }
                throw ex;
            }
            this.tr.startTransaction();
            if (this.isStartReplicationMasterBoot(removePhaseTwoProps) || this.isStopReplicationMasterBoot(removePhaseTwoProps) || b3) {
                if (!this.usingNoneAuth && this.getLanguageConnection().usesSqlAuthorization()) {
                    this.checkIsDBOwner(3);
                }
                if (this.isStartReplicationMasterBoot(removePhaseTwoProps)) {
                    this.handleStartReplicationMaster(this.tr, removePhaseTwoProps);
                }
                else if (this.isStopReplicationMasterBoot(removePhaseTwoProps)) {
                    this.handleStopReplicationMaster(this.tr, removePhaseTwoProps);
                }
                else if (b3) {
                    this.handleFailoverMaster(this.tr);
                }
            }
            if (n != 0 || b || startReplicationSlaveBoot) {
                if (!this.usingNoneAuth && this.getLanguageConnection().usesSqlAuthorization()) {
                    int n2;
                    if (n != 0) {
                        if (isTrue(properties, "decryptDatabase")) {
                            n2 = 4;
                        }
                        else {
                            n2 = 0;
                        }
                    }
                    else if (b) {
                        n2 = 2;
                    }
                    else {
                        n2 = 3;
                    }
                    try {
                        this.checkIsDBOwner(n2);
                    }
                    catch (SQLException ex2) {
                        if (startReplicationSlaveBoot) {
                            this.handleException(this.tr.shutdownDatabaseException());
                        }
                        throw ex2;
                    }
                }
                if (startReplicationSlaveBoot) {
                    if (b2) {
                        throw StandardException.newException("XRE09.C", this.getTR().getDBName());
                    }
                    removePhaseTwoProps.setProperty("replication.slave.mode", "slavemode");
                    removePhaseTwoProps.setProperty("replication.slave.dbname", this.getTR().getDBName());
                }
                else {
                    removePhaseTwoProps = properties;
                }
                this.handleException(this.tr.shutdownDatabaseException());
                this.restoreContextStack();
                this.tr = new TransactionResourceImpl(factory, s, removePhaseTwoProps);
                this.active = true;
                this.setupContextStack();
                this.pushConnectionContext(this.tr.getContextManager());
                if (!this.bootDatabase(removePhaseTwoProps, false)) {
                    this.tr.clearContextInError();
                    this.setInactive();
                    return;
                }
                if (startReplicationSlaveBoot) {
                    throw StandardException.newException("XRE08", this.getTR().getDBName());
                }
                this.tr.startTransaction();
            }
            if (true) {
                if (!this.usingNoneAuth && this.getLanguageConnection().usesSqlAuthorization()) {
                    this.checkIsDBOwner(1);
                }
                throw this.tr.shutdownDatabaseException();
            }
            if (dropDatabase) {
                if (!this.usingNoneAuth && this.getLanguageConnection().usesSqlAuthorization()) {
                    this.checkIsDBOwner(1);
                }
                final String dbName = this.tr.getDBName();
                this.handleException(this.tr.shutdownDatabaseException());
                sleep(500L);
                Monitor.removePersistentService(dbName);
                final StandardException exception = StandardException.newException("08006.D.1", dbName);
                exception.setReport(1);
                throw exception;
            }
            if (this.usingNoneAuth && this.getLanguageConnection().usesSqlAuthorization()) {
                this.addWarning(SQLWarningFactory.newSQLWarning("01J14"));
            }
            InterruptStatus.restoreIntrFlagIfSeen(this.getLanguageConnection());
        }
        catch (OutOfMemoryError outOfMemoryError) {
            InterruptStatus.restoreIntrFlagIfSeen();
            this.restoreContextStack();
            this.tr.lcc = null;
            this.tr.cm = null;
            EmbedConnection.memoryState.setLowMemory();
            throw EmbedConnection.NO_MEM;
        }
        catch (Throwable t) {
            InterruptStatus.restoreIntrFlagIfSeen();
            if (t instanceof StandardException) {
                final StandardException ex3 = (StandardException)t;
                if (ex3.getSeverity() < 40000) {
                    ex3.setSeverity(40000);
                }
            }
            this.tr.cleanupOnError(t, false);
            throw this.handleException(t);
        }
        finally {
            this.restoreContextStack();
        }
    }
    
    private void checkDatabaseBooted(final Database database, final String s, final String s2) throws SQLException {
        if (database == null) {
            this.setInactive();
            throw newSQLException("XRE11.C", s, s2);
        }
    }
    
    private boolean createBoot(final Properties properties) throws SQLException {
        int n = 0;
        if (isTrue(properties, "create")) {
            ++n;
        }
        int n2 = 0;
        if (isSet(properties, "createFrom")) {
            ++n2;
        }
        if (isSet(properties, "restoreFrom")) {
            ++n2;
        }
        if (isSet(properties, "rollForwardRecoveryFrom")) {
            ++n2;
        }
        if (n2 > 1) {
            throw newSQLException("XJ081.C");
        }
        if (n2 != 0 && this.isCryptoBoot(properties)) {
            throw newSQLException("XJ081.C");
        }
        final int n3 = n + n2;
        if (n3 > 1) {
            throw newSQLException("XJ049.C");
        }
        if (n3 == 1 && this.isDropDatabase(properties)) {
            String s = "XJ049.C";
            if (n2 > 0) {
                s = "XJ081.C";
            }
            throw newSQLException(s);
        }
        return n3 - n2 == 1;
    }
    
    private void handleDBNotFound() throws SQLException {
        final String dbName = this.tr.getDBName();
        this.setInactive();
        throw newSQLException("XJ004.C", dbName);
    }
    
    private boolean isDropDatabase(final Properties properties) {
        return isTrue(properties, "drop");
    }
    
    private boolean isCryptoBoot(final Properties properties) throws SQLException {
        return vetTrue(properties, "dataEncryption") || vetTrue(properties, "decryptDatabase") || isSet(properties, "newBootPassword") || isSet(properties, "newEncryptionKey");
    }
    
    private boolean isHardUpgradeBoot(final Properties properties) {
        return isTrue(properties, "upgrade");
    }
    
    private boolean isStartReplicationSlaveBoot(final Properties properties) {
        return isTrue(properties, "startSlave");
    }
    
    private boolean isStartReplicationMasterBoot(final Properties properties) {
        return isTrue(properties, "startMaster");
    }
    
    private boolean isReplicationFailover(final Properties properties) {
        return isTrue(properties, "failover");
    }
    
    private boolean isStopReplicationMasterBoot(final Properties properties) {
        return isTrue(properties, "stopMaster");
    }
    
    private boolean isStopReplicationSlaveBoot(final Properties properties) {
        return isTrue(properties, "stopSlave");
    }
    
    private boolean isInternalShutdownSlaveDatabase(final Properties properties) {
        return isTrue(properties, "internal_stopslave");
    }
    
    private static boolean isSet(final Properties properties, final String key) {
        return properties.getProperty(key) != null;
    }
    
    private static boolean isTrue(final Properties properties, final String key) {
        return Boolean.valueOf(properties.getProperty(key));
    }
    
    private static boolean vetTrue(final Properties properties, final String key) throws SQLException {
        final String property = properties.getProperty(key);
        if (property == null) {
            return false;
        }
        if (Boolean.valueOf(property)) {
            return true;
        }
        throw newSQLException("XJ05B.C", key, property, Boolean.TRUE.toString());
    }
    
    private String getReplicationOperation(final Properties properties) throws StandardException {
        String s = null;
        int n = 0;
        if (this.isStartReplicationSlaveBoot(properties)) {
            s = "startSlave";
            ++n;
        }
        if (this.isStartReplicationMasterBoot(properties)) {
            s = "startMaster";
            ++n;
        }
        if (this.isStopReplicationSlaveBoot(properties)) {
            s = "stopSlave";
            ++n;
        }
        if (this.isInternalShutdownSlaveDatabase(properties)) {
            s = "internal_stopslave";
            ++n;
        }
        if (this.isStopReplicationMasterBoot(properties)) {
            s = "stopMaster";
            ++n;
        }
        if (this.isReplicationFailover(properties)) {
            s = "failover";
            ++n;
        }
        if (n > 1) {
            throw StandardException.newException("XRE10", s);
        }
        return s;
    }
    
    private void handleStartReplicationMaster(final TransactionResourceImpl transactionResourceImpl, final Properties properties) throws SQLException {
        if (!this.usingNoneAuth && this.getLanguageConnection().usesSqlAuthorization()) {
            this.checkIsDBOwner(3);
        }
        final String property = properties.getProperty("slaveHost");
        if (property == null) {
            throw newSQLException("08004", newSQLException("XCY03.S", "slaveHost"));
        }
        final String property2 = properties.getProperty("slavePort");
        int int1 = -1;
        if (property2 != null) {
            int1 = Integer.parseInt(property2);
        }
        transactionResourceImpl.getDatabase().startReplicationMaster(this.getTR().getDBName(), property, int1, "derby.__rt.asynch");
    }
    
    private void handleStopReplicationMaster(final TransactionResourceImpl transactionResourceImpl, final Properties properties) throws SQLException {
        if (!this.usingNoneAuth && this.getLanguageConnection().usesSqlAuthorization()) {
            this.checkIsDBOwner(3);
        }
        transactionResourceImpl.getDatabase().stopReplicationMaster();
    }
    
    private void handleStopReplicationSlave(final Database database, final Properties properties) throws StandardException, SQLException {
        this.checkDatabaseBooted(database, "stopSlave", this.tr.getDBName());
        database.stopReplicationSlave();
        throw newSQLException("XRE42.C", this.getTR().getDBName());
    }
    
    private void internalStopReplicationSlave(final Database database, final Properties properties) throws StandardException, SQLException {
        this.checkDatabaseBooted(database, "internal_stopslave", this.tr.getDBName());
        if (!(database instanceof SlaveDatabase)) {
            throw newSQLException("XRE40");
        }
        ((SlaveDatabase)database).verifyShutdownSlave();
        this.handleException(this.tr.shutdownDatabaseException());
    }
    
    private void handleFailoverMaster(final TransactionResourceImpl transactionResourceImpl) throws SQLException, StandardException {
        if (!this.usingNoneAuth && this.getLanguageConnection().usesSqlAuthorization()) {
            this.checkIsDBOwner(3);
        }
        transactionResourceImpl.getDatabase().failover(transactionResourceImpl.getDBName());
    }
    
    private void handleFailoverSlave(final Database database) throws SQLException {
        try {
            database.failover(this.getTR().getDBName());
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    private Properties removePhaseTwoProps(final Properties properties) {
        properties.remove("dataEncryption");
        properties.remove("decryptDatabase");
        properties.remove("newBootPassword");
        properties.remove("newEncryptionKey");
        properties.remove("upgrade");
        return properties;
    }
    
    public EmbedConnection(final EmbedConnection embedConnection) {
        this.lobHashMap = null;
        this.lobHMKey = 0;
        this.lobReferences = null;
        this.aborting = false;
        this.autoCommit = true;
        this.connectionHoldAbility = 1;
        this.autoCommit = false;
        this.tr = null;
        this.active = true;
        this.rootConnection = embedConnection.rootConnection;
        this.applicationConnection = this;
        this.factory = embedConnection.factory;
        this.connectionHoldAbility = embedConnection.connectionHoldAbility;
    }
    
    private void checkUserCredentials(final boolean b, final String s, final Properties properties) throws SQLException {
        AuthenticationService authenticationService;
        try {
            if (s == null) {
                authenticationService = this.getLocalDriver().getAuthenticationService();
            }
            else {
                authenticationService = this.getTR().getDatabase().getAuthenticationService();
            }
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
        if (authenticationService == null) {
            throw newSQLException("08004", MessageService.getTextMessage((s == null) ? "A001" : "A002"));
        }
        if (b && this.compareDatabaseNames(this.getDBName(), authenticationService.getSystemCredentialsDatabaseName())) {
            final String property = properties.getProperty("user");
            final String property2 = properties.getProperty("password");
            if (this.emptyCredential(property) || this.emptyCredential(property2)) {
                throw newSQLException("08004.C.13");
            }
        }
        else {
            if (s != null) {
                this.checkUserIsNotARole();
            }
            boolean authenticate = true;
            try {
                authenticate = authenticationService.authenticate(s, properties);
            }
            catch (SQLWarning sqlWarning) {
                this.addWarning(sqlWarning);
            }
            if (!authenticate) {
                throw newSQLException("08004.C.1", MessageService.getTextMessage("A020"));
            }
            if (authenticationService instanceof NoneAuthenticationServiceImpl) {
                this.usingNoneAuth = true;
            }
        }
    }
    
    private boolean emptyCredential(final String s) {
        return s == null || s.length() == 0;
    }
    
    private boolean compareDatabaseNames(final String s, final String s2) throws SQLException {
        try {
            final String canonicalServiceName = Monitor.getMonitor().getCanonicalServiceName(s);
            final String canonicalServiceName2 = Monitor.getMonitor().getCanonicalServiceName(s2);
            return canonicalServiceName != null && canonicalServiceName.equals(canonicalServiceName2);
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    private void checkUserIsNotARole() throws SQLException {
        final TransactionResourceImpl tr = this.getTR();
        try {
            tr.startTransaction();
            final LanguageConnectionContext lcc = tr.getLcc();
            final String sessionUserId = lcc.getSessionUserId();
            final DataDictionary dataDictionary = lcc.getDataDictionary();
            if (lcc.usesSqlAuthorization() && dataDictionary.checkVersion(160, null)) {
                lcc.getTransactionExecute();
                final String textMessage = MessageService.getTextMessage("A020");
                if (dataDictionary.getRoleDefinitionDescriptor(sessionUserId) != null) {
                    throw newSQLException("08004.C.1", textMessage);
                }
            }
            tr.rollback();
            InterruptStatus.restoreIntrFlagIfSeen(lcc);
        }
        catch (StandardException ex) {
            try {
                tr.rollback();
            }
            catch (StandardException ex2) {}
            throw this.handleException(ex);
        }
    }
    
    private void checkIsDBOwner(final int n) throws SQLException {
        final LanguageConnectionContext languageConnection = this.getLanguageConnection();
        final String sessionUserId = languageConnection.getSessionUserId();
        if (sessionUserId.equals(languageConnection.getDataDictionary().getAuthorizationDatabaseOwner())) {
            return;
        }
        switch (n) {
            case 0: {
                throw newSQLException("08004.C.5", sessionUserId, this.tr.getDBName());
            }
            case 4: {
                throw newSQLException("08004.C.14", sessionUserId, this.tr.getDBName());
            }
            case 1: {
                throw newSQLException("08004.C.4", sessionUserId, this.tr.getDBName());
            }
            case 2: {
                throw newSQLException("08004.C.6", sessionUserId, this.tr.getDBName());
            }
            case 3: {
                throw newSQLException("08004.C.8", sessionUserId, this.tr.getDBName());
            }
            default: {
                throw newSQLException("08004.C.3");
            }
        }
    }
    
    public int getEngineType() {
        final Database database = this.getDatabase();
        if (null == database) {
            return 0;
        }
        return database.getEngineType();
    }
    
    public final Statement createStatement() throws SQLException {
        return this.createStatement(1003, 1007, this.connectionHoldAbility);
    }
    
    public final Statement createStatement(final int n, final int n2) throws SQLException {
        return this.createStatement(n, n2, this.connectionHoldAbility);
    }
    
    public final Statement createStatement(final int resultSetType, final int n, final int n2) throws SQLException {
        this.checkIfClosed();
        return this.factory.newEmbedStatement(this, false, this.setResultSetType(resultSetType), n, n2);
    }
    
    public final PreparedStatement prepareStatement(final String s) throws SQLException {
        return this.prepareStatement(s, 1003, 1007, this.connectionHoldAbility, 2, null, null);
    }
    
    public final PreparedStatement prepareStatement(final String s, final int n, final int n2) throws SQLException {
        return this.prepareStatement(s, n, n2, this.connectionHoldAbility, 2, null, null);
    }
    
    public final PreparedStatement prepareStatement(final String s, final int n, final int n2, final int n3) throws SQLException {
        return this.prepareStatement(s, n, n2, n3, 2, null, null);
    }
    
    public final PreparedStatement prepareStatement(final String s, final int[] array) throws SQLException {
        return this.prepareStatement(s, 1003, 1007, this.connectionHoldAbility, (array == null || array.length == 0) ? 2 : 1, array, null);
    }
    
    public final PreparedStatement prepareStatement(final String s, final String[] array) throws SQLException {
        return this.prepareStatement(s, 1003, 1007, this.connectionHoldAbility, (array == null || array.length == 0) ? 2 : 1, null, array);
    }
    
    public final PreparedStatement prepareStatement(final String s, final int n) throws SQLException {
        return this.prepareStatement(s, 1003, 1007, this.connectionHoldAbility, n, null, null);
    }
    
    private PreparedStatement prepareStatement(final String s, final int resultSetType, final int n, final int n2, final int n3, final int[] array, final String[] array2) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                return this.factory.newEmbedPreparedStatement(this, s, false, this.setResultSetType(resultSetType), n, n2, n3, array, array2);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public final CallableStatement prepareCall(final String s) throws SQLException {
        return this.prepareCall(s, 1003, 1007, this.connectionHoldAbility);
    }
    
    public final CallableStatement prepareCall(final String s, final int n, final int n2) throws SQLException {
        return this.prepareCall(s, n, n2, this.connectionHoldAbility);
    }
    
    public final CallableStatement prepareCall(final String s, final int resultSetType, final int n, final int n2) throws SQLException {
        this.checkIfClosed();
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                return this.factory.newEmbedCallableStatement(this, s, this.setResultSetType(resultSetType), n, n2);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public String nativeSQL(final String s) throws SQLException {
        this.checkIfClosed();
        return s;
    }
    
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.checkIfClosed();
        if (this.rootConnection != this && autoCommit) {
            throw newSQLException("XJ030.S");
        }
        if (this.autoCommit != autoCommit) {
            this.commit();
        }
        this.autoCommit = autoCommit;
    }
    
    public boolean getAutoCommit() throws SQLException {
        this.checkIfClosed();
        return this.autoCommit;
    }
    
    public void commit() throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                this.getTR().commit();
                this.clearLOBMapping();
                InterruptStatus.restoreIntrFlagIfSeen(this.getLanguageConnection());
            }
            catch (Throwable t) {
                throw this.handleException(t);
            }
            finally {
                this.restoreContextStack();
            }
            this.needCommit = false;
        }
    }
    
    public void rollback() throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                this.getTR().rollback();
                this.clearLOBMapping();
                InterruptStatus.restoreIntrFlagIfSeen(this.getLanguageConnection());
            }
            catch (Throwable t) {
                throw this.handleException(t);
            }
            finally {
                this.restoreContextStack();
            }
            this.needCommit = false;
        }
    }
    
    public void close() throws SQLException {
        this.checkForTransactionInProgress();
        this.close(EmbedConnection.exceptionClose);
    }
    
    public void checkForTransactionInProgress() throws SQLException {
        if (!this.isClosed() && this.rootConnection == this && !this.autoCommit && !this.transactionIsIdle()) {
            Util.logAndThrowSQLException(newSQLException("25001"));
        }
    }
    
    protected void close(final StandardException ex) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            if (this.rootConnection == this && (this.active || this.isAborting())) {
                if (this.tr.isActive()) {
                    this.setupContextStack();
                    try {
                        this.tr.rollback();
                        InterruptStatus.restoreIntrFlagIfSeen(this.tr.getLcc());
                        this.tr.clearLcc();
                        this.tr.cleanupOnError(ex, false);
                    }
                    catch (Throwable t) {
                        throw this.handleException(t);
                    }
                    finally {
                        this.restoreContextStack();
                    }
                }
                else {
                    InterruptStatus.restoreIntrFlagIfSeen();
                    this.tr.clearLcc();
                    this.tr.cleanupOnError(ex, false);
                }
            }
            this.aborting = false;
            if (!this.isClosed()) {
                this.setInactive();
            }
        }
    }
    
    public final boolean isClosed() {
        return !this.active || !this.getTR().isActive();
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkIfClosed();
        if (this.dbMetadata == null) {
            this.dbMetadata = this.factory.newEmbedDatabaseMetaData(this, this.getTR().getUrl());
        }
        return this.dbMetadata;
    }
    
    public final int getHoldability() throws SQLException {
        this.checkIfClosed();
        return this.connectionHoldAbility;
    }
    
    public final void setHoldability(final int connectionHoldAbility) throws SQLException {
        this.checkIfClosed();
        this.connectionHoldAbility = connectionHoldAbility;
    }
    
    public final void setReadOnly(final boolean readOnly) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                final LanguageConnectionContext languageConnection = this.getLanguageConnection();
                languageConnection.setReadOnly(readOnly);
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public final boolean isReadOnly() throws SQLException {
        this.checkIfClosed();
        return this.getLanguageConnection().isReadOnly();
    }
    
    public void setCatalog(final String s) throws SQLException {
        this.checkIfClosed();
    }
    
    public String getCatalog() throws SQLException {
        this.checkIfClosed();
        return null;
    }
    
    public void setTransactionIsolation(final int value) throws SQLException {
        if (value == this.getTransactionIsolation()) {
            return;
        }
        int isolationLevel = 0;
        switch (value) {
            case 1: {
                isolationLevel = 1;
                break;
            }
            case 2: {
                isolationLevel = 2;
                break;
            }
            case 4: {
                isolationLevel = 3;
                break;
            }
            case 8: {
                isolationLevel = 4;
                break;
            }
            default: {
                throw newSQLException("XJ045.S", new Integer(value));
            }
        }
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                final LanguageConnectionContext languageConnection = this.getLanguageConnection();
                languageConnection.setIsolationLevel(isolationLevel);
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public final int getTransactionIsolation() throws SQLException {
        this.checkIfClosed();
        return ExecutionContext.CS_TO_JDBC_ISOLATION_LEVEL_MAP[this.getLanguageConnection().getCurrentIsolationLevel()];
    }
    
    public final synchronized SQLWarning getWarnings() throws SQLException {
        this.checkIfClosed();
        return this.topWarning;
    }
    
    public final synchronized void clearWarnings() throws SQLException {
        this.checkIfClosed();
        this.topWarning = null;
    }
    
    public Map getTypeMap() throws SQLException {
        this.checkIfClosed();
        return Collections.EMPTY_MAP;
    }
    
    public final void setTypeMap(final Map map) throws SQLException {
        this.checkIfClosed();
        if (map == null) {
            throw Util.generateCsSQLException("XJ081.S", map, "map", "java.sql.Connection.setTypeMap");
        }
        if (!map.isEmpty()) {
            throw Util.notImplemented();
        }
    }
    
    public final synchronized void addWarning(final SQLWarning sqlWarning) {
        if (this.topWarning == null) {
            this.topWarning = sqlWarning;
            return;
        }
        this.topWarning.setNextWarning(sqlWarning);
    }
    
    public String getDBName() {
        return this.getTR().getDBName();
    }
    
    public final LanguageConnectionContext getLanguageConnection() {
        return this.getTR().getLcc();
    }
    
    protected final void checkIfClosed() throws SQLException {
        if (this.isClosed()) {
            throw Util.noCurrentConnection();
        }
    }
    
    SQLException handleException(final Throwable t) throws SQLException {
        if (t instanceof StandardException && ((StandardException)t).getSeverity() >= 30000) {
            this.clearLOBMapping();
        }
        return this.getTR().handleException(t, this.autoCommit, true);
    }
    
    final SQLException handleException(final Throwable t, final boolean b) throws SQLException {
        if (t instanceof StandardException && ((StandardException)t).getSeverity() >= 30000) {
            this.clearLOBMapping();
        }
        return this.getTR().handleException(t, this.autoCommit, b);
    }
    
    public final void setInactive() {
        if (!this.active) {
            return;
        }
        synchronized (this.getConnectionSynchronization()) {
            this.active = false;
            this.dbMetadata = null;
        }
    }
    
    protected void finalize() throws Throwable {
        try {
            if (this.rootConnection == this) {
                this.close(EmbedConnection.exceptionClose);
            }
        }
        finally {
            super.finalize();
        }
    }
    
    protected void needCommit() {
        if (!this.needCommit) {
            this.needCommit = true;
        }
    }
    
    protected void commitIfNeeded() throws SQLException {
        if (this.autoCommit && this.needCommit) {
            try {
                this.getTR().commit();
                this.clearLOBMapping();
                InterruptStatus.restoreIntrFlagIfSeen(this.getLanguageConnection());
            }
            catch (Throwable t) {
                throw this.handleException(t);
            }
            this.needCommit = false;
        }
    }
    
    protected void commitIfAutoCommit() throws SQLException {
        if (this.autoCommit) {
            try {
                this.getTR().commit();
                this.clearLOBMapping();
                InterruptStatus.restoreIntrFlagIfSeen(this.getLanguageConnection());
            }
            catch (Throwable t) {
                throw this.handleException(t);
            }
            this.needCommit = false;
        }
    }
    
    protected final Object getConnectionSynchronization() {
        return this.rootConnection;
    }
    
    protected final void setupContextStack() throws SQLException {
        if (!this.isAborting()) {
            this.checkIfClosed();
        }
        this.getTR().setupContextStack();
    }
    
    protected final void restoreContextStack() throws SQLException {
        this.getTR().restoreContextStack();
    }
    
    private Database createDatabase(final String s, Properties filterProperties) throws SQLException {
        filterProperties = this.filterProperties(filterProperties);
        try {
            if (Monitor.createPersistentService("org.apache.derby.database.Database", s, filterProperties) == null) {
                this.addWarning(SQLWarningFactory.newSQLWarning("01J01", s));
            }
        }
        catch (StandardException ex) {
            throw Util.seeNextException("XJ041.C", new Object[] { s }, this.handleException(ex));
        }
        filterProperties.clear();
        return (Database)Monitor.findService("org.apache.derby.database.Database", s);
    }
    
    private void checkDatabaseCreatePrivileges(final String s, final String s2) throws SQLException {
        if (System.getSecurityManager() == null) {
            return;
        }
        if (s2 == null) {
            throw new NullPointerException("dbname can't be null");
        }
        try {
            this.factory.checkSystemPrivileges(s, new DatabasePermission("directory:" + stripSubSubProtocolPrefix(s2), "create"));
        }
        catch (AccessControlException ex) {
            throw Util.generateCsSQLException("08004.C.11", s, s2, ex);
        }
        catch (IOException ex2) {
            throw Util.generateCsSQLException("08004.C.10", s2, (Object)ex2);
        }
        catch (Exception ex3) {
            throw Util.generateCsSQLException("08004.C.10", s2, (Object)ex3);
        }
    }
    
    private static void sleep(final long n) {
        final long currentTimeMillis = System.currentTimeMillis();
        long n2 = 0L;
        while (n2 < n) {
            try {
                Thread.sleep(n - n2);
            }
            catch (InterruptedException ex) {
                InterruptStatus.setInterrupted();
                n2 = System.currentTimeMillis() - currentTimeMillis;
                continue;
            }
            break;
        }
    }
    
    public static String stripSubSubProtocolPrefix(final String s) {
        final int index = s.indexOf(58);
        if (index > 0 && PropertyUtil.getSystemProperty("derby.subSubProtocol." + s.substring(0, index), null) != null) {
            return s.substring(index + 1);
        }
        return s;
    }
    
    private boolean bootDatabase(Properties filterProperties, final boolean b) throws Throwable {
        final String dbName = this.tr.getDBName();
        try {
            filterProperties = this.filterProperties(filterProperties);
            if (b) {
                filterProperties.setProperty("softUpgradeNoFeatureCheck", "true");
            }
            else {
                filterProperties.remove("softUpgradeNoFeatureCheck");
            }
            if (!Monitor.startPersistentService(dbName, filterProperties)) {
                return false;
            }
            filterProperties.clear();
            this.tr.setDatabase((Database)Monitor.findService("org.apache.derby.database.Database", dbName));
        }
        catch (StandardException ex) {
            final Throwable cause = ex.getCause();
            SQLException ex2;
            if (cause instanceof StandardException) {
                ex2 = Util.generateCsSQLException((StandardException)cause);
            }
            else if (cause != null) {
                ex2 = Util.javaException(cause);
            }
            else {
                ex2 = Util.generateCsSQLException(ex);
            }
            throw Util.seeNextException("XJ040.C", new Object[] { dbName, this.getClass().getClassLoader() }, ex2);
        }
        return true;
    }
    
    PreparedStatement prepareMetaDataStatement(final String s) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            PreparedStatement embedPreparedStatement = null;
            try {
                embedPreparedStatement = this.factory.newEmbedPreparedStatement(this, s, true, 1003, 1007, this.connectionHoldAbility, 2, null, null);
            }
            finally {
                InterruptStatus.restoreIntrFlagIfSeen(this.getLanguageConnection());
                this.restoreContextStack();
            }
            return embedPreparedStatement;
        }
    }
    
    public final InternalDriver getLocalDriver() {
        return this.getTR().getDriver();
    }
    
    public final ContextManager getContextManager() {
        return this.getTR().getContextManager();
    }
    
    private Properties filterProperties(final Properties properties) {
        final Properties properties2 = new Properties();
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String s = (String)propertyNames.nextElement();
            if (s.startsWith("derby.")) {
                continue;
            }
            properties2.put(s, properties.getProperty(s));
        }
        return properties2;
    }
    
    protected Database getDatabase() {
        return this.getTR().getDatabase();
    }
    
    protected final TransactionResourceImpl getTR() {
        return this.rootConnection.tr;
    }
    
    private EmbedConnectionContext pushConnectionContext(final ContextManager contextManager) {
        return new EmbedConnectionContext(contextManager, this);
    }
    
    public final void setApplicationConnection(final Connection applicationConnection) {
        this.applicationConnection = applicationConnection;
    }
    
    public final Connection getApplicationConnection() {
        return this.applicationConnection;
    }
    
    public void setDrdaID(final String drdaID) {
        this.getLanguageConnection().setDrdaID(drdaID);
    }
    
    public boolean isInGlobalTransaction() {
        return false;
    }
    
    public void resetFromPool() throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                final LanguageConnectionContext languageConnection = this.getLanguageConnection();
                languageConnection.resetFromPool();
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public final int xa_prepare() throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                final LanguageConnectionContext languageConnection = this.getLanguageConnection();
                final int xa_prepare = ((XATransactionController)languageConnection.getTransactionExecute()).xa_prepare();
                if (xa_prepare == 1) {
                    languageConnection.internalCommit(false);
                }
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
                return xa_prepare;
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public final void xa_commit(final boolean b) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                final LanguageConnectionContext languageConnection = this.getLanguageConnection();
                languageConnection.xaCommit(b);
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public final void xa_rollback() throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                final LanguageConnectionContext languageConnection = this.getLanguageConnection();
                languageConnection.xaRollback();
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public final boolean transactionIsIdle() {
        return this.getTR().isIdle();
    }
    
    private int setResultSetType(int n) {
        if (n == 1005) {
            this.addWarning(SQLWarningFactory.newSQLWarning("01J02"));
            n = 1004;
        }
        return n;
    }
    
    public void setPrepareIsolation(final int n) throws SQLException {
        if (n == this.getPrepareIsolation()) {
            return;
        }
        switch (n) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4: {
                synchronized (this.getConnectionSynchronization()) {
                    this.getLanguageConnection().setPrepareIsolationLevel(n);
                }
            }
            default: {
                throw Util.generateCsSQLException("XJ045.S", new Integer(n));
            }
        }
    }
    
    public int getPrepareIsolation() {
        return this.getLanguageConnection().getPrepareIsolationLevel();
    }
    
    final int getResultSetOrderId() {
        if (this == this.rootConnection) {
            return 0;
        }
        return this.rootConnection.resultSetId++;
    }
    
    public ExceptionFactory getExceptionFactory() {
        return Util.getExceptionFactory();
    }
    
    protected static SQLException newSQLException(final String s) {
        return Util.generateCsSQLException(s);
    }
    
    protected static SQLException newSQLException(final String s, final Object o) {
        return Util.generateCsSQLException(s, o);
    }
    
    protected static SQLException newSQLException(final String s, final Object o, final Object o2) {
        return Util.generateCsSQLException(s, o, o2);
    }
    
    protected static SQLException newSQLException(final String s, final Object o, final Object o2, final Object o3) {
        return Util.generateCsSQLException(s, o, o2, o3);
    }
    
    public String toString() {
        if (this.connString == null) {
            final LanguageConnectionContext languageConnection = this.getLanguageConnection();
            this.connString = this.getClass().getName() + "@" + this.hashCode() + " " + "(XID = " + languageConnection.getTransactionExecute().getTransactionIdString() + "), " + "(SESSIONID = " + Integer.toString(languageConnection.getInstanceNumber()) + "), " + "(DATABASE = " + languageConnection.getDbname() + "), " + "(DRDAID = " + languageConnection.getDrdaID() + ") ";
        }
        return this.connString;
    }
    
    public Clob createClob() throws SQLException {
        this.checkIfClosed();
        return new EmbedClob(this);
    }
    
    public Blob createBlob() throws SQLException {
        this.checkIfClosed();
        return new EmbedBlob(new byte[0], this);
    }
    
    public int addLOBMapping(final Object value) {
        final int incLOBKey = this.getIncLOBKey();
        this.getlobHMObj().put(new Integer(incLOBKey), value);
        return incLOBKey;
    }
    
    public void removeLOBMapping(final int value) {
        this.getlobHMObj().remove(new Integer(value));
    }
    
    public Object getLOBMapping(final int value) {
        return this.getlobHMObj().get(new Integer(value));
    }
    
    public void clearLOBMapping() throws SQLException {
        final WeakHashMap lobReferences = this.rootConnection.lobReferences;
        if (lobReferences != null) {
            final Iterator<EngineLOB> iterator = lobReferences.keySet().iterator();
            while (iterator.hasNext()) {
                iterator.next().free();
            }
            lobReferences.clear();
        }
        if (this.rootConnection.lobHashMap != null) {
            this.rootConnection.lobHashMap.clear();
        }
        synchronized (this) {
            if (this.lobFiles != null) {
                SQLException javaException = null;
                final Iterator<LOBFile> iterator2 = this.lobFiles.iterator();
                while (iterator2.hasNext()) {
                    try {
                        iterator2.next().close();
                    }
                    catch (IOException ex) {
                        if (javaException != null) {
                            continue;
                        }
                        javaException = Util.javaException(ex);
                    }
                }
                this.lobFiles.clear();
                if (javaException != null) {
                    throw javaException;
                }
            }
        }
    }
    
    private int getIncLOBKey() {
        int n = ++this.rootConnection.lobHMKey;
        if (n == 32768 || n == 32770 || n == 32772 || n == 32774 || n == 32776) {
            n = ++this.rootConnection.lobHMKey;
        }
        if (n == Integer.MIN_VALUE || n == 0) {
            final EmbedConnection rootConnection = this.rootConnection;
            final int lobHMKey = 1;
            rootConnection.lobHMKey = lobHMKey;
            n = lobHMKey;
        }
        return n;
    }
    
    void addLOBReference(final Object key) {
        if (this.rootConnection.lobReferences == null) {
            this.rootConnection.lobReferences = new WeakHashMap();
        }
        this.rootConnection.lobReferences.put(key, null);
    }
    
    private HashMap getlobHMObj() {
        if (this.rootConnection.lobHashMap == null) {
            this.rootConnection.lobHashMap = new HashMap();
        }
        return this.rootConnection.lobHashMap;
    }
    
    public void cancelRunningStatement() {
        this.getLanguageConnection().getStatementContext().cancel();
    }
    
    public String getCurrentSchemaName() {
        return this.getLanguageConnection().getCurrentSchemaName();
    }
    
    void addLobFile(final LOBFile e) {
        synchronized (this) {
            if (this.lobFiles == null) {
                this.lobFiles = new HashSet();
            }
            this.lobFiles.add(e);
        }
    }
    
    void removeLobFile(final LOBFile o) {
        synchronized (this) {
            this.lobFiles.remove(o);
        }
    }
    
    public boolean isAborting() {
        return this.aborting;
    }
    
    protected void beginAborting() {
        this.aborting = true;
        this.setInactive();
    }
    
    public Savepoint setSavepoint() throws SQLException {
        return this.commonSetSavepointCode(null, false);
    }
    
    public Savepoint setSavepoint(final String s) throws SQLException {
        return this.commonSetSavepointCode(s, true);
    }
    
    private Savepoint commonSetSavepointCode(final String s, final boolean b) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                this.verifySavepointCommandIsAllowed();
                if (b && s == null) {
                    throw newSQLException("XJ011.S");
                }
                if (b && s.length() > 128) {
                    throw newSQLException("42622", s, String.valueOf(128));
                }
                if (b && s.startsWith("SYS")) {
                    throw newSQLException("42939", "SYS");
                }
                return new EmbedSavepoint(this, s);
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public void rollback(final Savepoint savepoint) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                this.verifySavepointCommandIsAllowed();
                this.verifySavepointArg(savepoint);
                this.getLanguageConnection().internalRollbackToSavepoint(((EmbedSavepoint)savepoint).getInternalName(), true, savepoint);
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                this.verifySavepointCommandIsAllowed();
                this.verifySavepointArg(savepoint);
                this.getLanguageConnection().releaseSavePoint(((EmbedSavepoint)savepoint).getInternalName(), savepoint);
            }
            catch (StandardException ex) {
                throw this.handleException(ex);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    private void verifySavepointCommandIsAllowed() throws SQLException {
        if (this.autoCommit) {
            throw newSQLException("XJ010.S");
        }
        final StatementContext statementContext = this.getLanguageConnection().getStatementContext();
        if (statementContext != null && statementContext.inTrigger()) {
            throw newSQLException("XJ017.S");
        }
    }
    
    private void verifySavepointArg(final Savepoint savepoint) throws SQLException {
        final EmbedSavepoint embedSavepoint = (EmbedSavepoint)savepoint;
        if (embedSavepoint == null) {
            throw Util.generateCsSQLException("3B001.S", "null");
        }
        if (!embedSavepoint.sameConnection(this)) {
            throw newSQLException("3B502.S");
        }
    }
    
    public String getSchema() throws SQLException {
        this.checkIfClosed();
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                return this.getLanguageConnection().getCurrentSchemaName();
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public void setSchema(final String s) throws SQLException {
        this.checkIfClosed();
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = this.prepareStatement("set schema ?");
            prepareStatement.setString(1, s);
            prepareStatement.execute();
        }
        finally {
            if (prepareStatement != null) {
                prepareStatement.close();
            }
        }
    }
    
    private void checkConflictingCryptoAttributes(final Properties properties) throws SQLException {
        if ((isSet(properties, "encryptionKey") || isSet(properties, "bootPassword")) && isTrue(properties, "decryptDatabase")) {
            if (isSet(properties, "newBootPassword")) {
                throw newSQLException("XJ048.C", "decryptDatabase, newBootPassword");
            }
            if (isSet(properties, "newEncryptionKey")) {
                throw newSQLException("XJ048.C", "decryptDatabase, newEncryptionKey");
            }
        }
    }
    
    static {
        exceptionClose = StandardException.closeException();
        NO_MEM = Util.generateCsSQLException("08004", "java.lang.OutOfMemoryError");
        memoryState = new LowMemory();
    }
}
