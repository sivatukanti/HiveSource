// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.concurrent.Callable;
import com.google.common.util.concurrent.ListenableFuture;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Executors;
import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import com.jolbox.bonecp.hooks.ConnectionHook;
import java.util.concurrent.atomic.AtomicInteger;
import com.jolbox.bonecp.hooks.AcquireFailConfig;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.common.base.FinalizableReferenceQueue;
import java.lang.ref.Reference;
import java.sql.Connection;
import java.util.Map;
import javax.management.MBeanServer;
import org.slf4j.Logger;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.concurrent.ExecutorService;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.ScheduledExecutorService;
import java.io.Closeable;
import java.io.Serializable;

public class BoneCP implements Serializable, Closeable
{
    private static final String THREAD_CLOSE_CONNECTION_WARNING = "Thread close connection monitoring has been enabled. This will negatively impact on your performance. Only enable this option for debugging purposes!";
    private static final long serialVersionUID = -8386816681977604817L;
    private static final String ERROR_TEST_CONNECTION = "Unable to open a test connection to the given database. JDBC url = %s, username = %s. Terminating connection pool (set lazyInit to true if you expect to start your database after your app). Original Exception: %s";
    private static final String SHUTDOWN_LOCATION_TRACE = "Attempting to obtain a connection from a pool that has already been shutdown. \nStack trace of location where pool was shutdown follows:\n";
    private static final String UNCLOSED_EXCEPTION_MESSAGE = "Connection obtained from thread [%s] was never closed. \nStack trace of location where connection was obtained follows:\n";
    public static final String MBEAN_CONFIG = "com.jolbox.bonecp:type=BoneCPConfig";
    public static final String MBEAN_BONECP = "com.jolbox.bonecp:type=BoneCP";
    private static final String[] METADATATABLE;
    private static final String KEEPALIVEMETADATA = "BONECPKEEPALIVE";
    protected final int poolAvailabilityThreshold;
    protected int partitionCount;
    protected ConnectionPartition[] partitions;
    @VisibleForTesting
    protected ScheduledExecutorService keepAliveScheduler;
    private ScheduledExecutorService maxAliveScheduler;
    private ExecutorService connectionsScheduler;
    @VisibleForTesting
    protected BoneCPConfig config;
    private ListeningExecutorService asyncExecutor;
    private static final Logger logger;
    private MBeanServer mbs;
    protected boolean closeConnectionWatch;
    private ExecutorService closeConnectionExecutor;
    protected volatile boolean poolShuttingDown;
    protected String shutdownStackTrace;
    private final Map<Connection, Reference<ConnectionHandle>> finalizableRefs;
    private transient FinalizableReferenceQueue finalizableRefQueue;
    protected long connectionTimeoutInMs;
    private long closeConnectionWatchTimeoutInMs;
    protected boolean statisticsEnabled;
    protected Statistics statistics;
    @VisibleForTesting
    protected boolean nullOnConnectionTimeout;
    @VisibleForTesting
    protected boolean resetConnectionOnClose;
    protected boolean cachedPoolStrategy;
    protected ConnectionStrategy connectionStrategy;
    private AtomicBoolean dbIsDown;
    @VisibleForTesting
    protected Properties clientInfo;
    @VisibleForTesting
    protected volatile boolean driverInitialized;
    protected int jvmMajorVersion;
    protected static String connectionClass;
    
    public synchronized void shutdown() {
        if (!this.poolShuttingDown) {
            BoneCP.logger.info("Shutting down connection pool...");
            this.poolShuttingDown = true;
            this.shutdownStackTrace = this.captureStackTrace("Attempting to obtain a connection from a pool that has already been shutdown. \nStack trace of location where pool was shutdown follows:\n");
            this.keepAliveScheduler.shutdownNow();
            this.maxAliveScheduler.shutdownNow();
            this.connectionsScheduler.shutdownNow();
            this.asyncExecutor.shutdownNow();
            try {
                this.connectionsScheduler.awaitTermination(5L, TimeUnit.SECONDS);
                this.maxAliveScheduler.awaitTermination(5L, TimeUnit.SECONDS);
                this.keepAliveScheduler.awaitTermination(5L, TimeUnit.SECONDS);
                this.asyncExecutor.awaitTermination(5L, TimeUnit.SECONDS);
                if (this.closeConnectionExecutor != null) {
                    this.closeConnectionExecutor.shutdownNow();
                    this.closeConnectionExecutor.awaitTermination(5L, TimeUnit.SECONDS);
                }
            }
            catch (InterruptedException ex) {}
            this.connectionStrategy.terminateAllConnections();
            this.unregisterDriver();
            this.registerUnregisterJMX(false);
            if (this.finalizableRefQueue != null) {
                this.finalizableRefQueue.close();
            }
            BoneCP.logger.info("Connection pool has been shutdown.");
        }
    }
    
    protected void unregisterDriver() {
        final String jdbcURL = this.config.getJdbcUrl();
        if (jdbcURL != null && this.config.isDeregisterDriverOnClose()) {
            BoneCP.logger.info("Unregistering JDBC driver for : " + jdbcURL);
            try {
                DriverManager.deregisterDriver(DriverManager.getDriver(jdbcURL));
            }
            catch (SQLException e) {
                BoneCP.logger.info("Unregistering driver failed.", e);
            }
        }
    }
    
    public void close() {
        this.shutdown();
    }
    
    protected void destroyConnection(final ConnectionHandle conn) {
        this.postDestroyConnection(conn);
        conn.setInReplayMode(true);
        try {
            conn.internalClose();
        }
        catch (SQLException e) {
            BoneCP.logger.error("Error in attempting to close connection", e);
        }
    }
    
    protected void postDestroyConnection(final ConnectionHandle handle) {
        final ConnectionPartition partition = handle.getOriginatingPartition();
        if (this.finalizableRefQueue != null && handle.getInternalConnection() != null) {
            this.finalizableRefs.remove(handle.getInternalConnection());
        }
        partition.updateCreatedConnections(-1);
        partition.setUnableToCreateMoreTransactions(false);
        if (handle.getConnectionHook() != null) {
            handle.getConnectionHook().onDestroy(handle);
        }
    }
    
    protected Connection obtainInternalConnection(final ConnectionHandle connectionHandle) throws SQLException {
        boolean tryAgain = false;
        Connection result = null;
        final Connection oldRawConnection = connectionHandle.getInternalConnection();
        final String url = this.getConfig().getJdbcUrl();
        int acquireRetryAttempts = this.getConfig().getAcquireRetryAttempts();
        final long acquireRetryDelayInMs = this.getConfig().getAcquireRetryDelayInMs();
        final AcquireFailConfig acquireConfig = new AcquireFailConfig();
        acquireConfig.setAcquireRetryAttempts(new AtomicInteger(acquireRetryAttempts));
        acquireConfig.setAcquireRetryDelayInMs(acquireRetryDelayInMs);
        acquireConfig.setLogMessage("Failed to acquire connection to " + url);
        final ConnectionHook connectionHook = this.getConfig().getConnectionHook();
        do {
            result = null;
            try {
                result = this.obtainRawInternalConnection();
                tryAgain = false;
                if (acquireRetryAttempts != this.getConfig().getAcquireRetryAttempts()) {
                    BoneCP.logger.info("Successfully re-established connection to " + url);
                }
                this.getDbIsDown().set(false);
                connectionHandle.setInternalConnection(result);
                if (connectionHook != null) {
                    connectionHook.onAcquire(connectionHandle);
                }
                ConnectionHandle.sendInitSQL(result, this.getConfig().getInitSQL());
            }
            catch (SQLException e) {
                if (connectionHook != null) {
                    tryAgain = connectionHook.onAcquireFail(e, acquireConfig);
                }
                else {
                    BoneCP.logger.error(String.format("Failed to acquire connection to %s. Sleeping for %d ms. Attempts left: %d", url, acquireRetryDelayInMs, acquireRetryAttempts), e);
                    try {
                        if (acquireRetryAttempts > 0) {
                            Thread.sleep(acquireRetryDelayInMs);
                        }
                        tryAgain = (acquireRetryAttempts-- > 0);
                    }
                    catch (InterruptedException e2) {
                        tryAgain = false;
                    }
                }
                if (!tryAgain) {
                    if (oldRawConnection != null) {
                        oldRawConnection.close();
                    }
                    if (result != null) {
                        result.close();
                    }
                    connectionHandle.setInternalConnection(oldRawConnection);
                    throw e;
                }
                continue;
            }
        } while (tryAgain);
        return result;
    }
    
    protected Connection obtainRawInternalConnection() throws SQLException {
        Connection result = null;
        final DataSource datasourceBean = this.config.getDatasourceBean();
        final String url = this.config.getJdbcUrl();
        final String username = this.config.getUsername();
        final String password = this.config.getPassword();
        Properties props = this.config.getDriverProperties();
        final boolean externalAuth = this.config.isExternalAuth();
        if (externalAuth && props == null) {
            props = new Properties();
        }
        if (datasourceBean != null) {
            return (username == null) ? datasourceBean.getConnection() : datasourceBean.getConnection(username, password);
        }
        if (!this.driverInitialized) {
            try {
                this.driverInitialized = true;
                if (props != null) {
                    result = DriverManager.getConnection(url, props);
                }
                else {
                    result = DriverManager.getConnection(url, username, password);
                }
                result.close();
            }
            catch (SQLException ex) {}
        }
        if (props != null) {
            result = DriverManager.getConnection(url, props);
        }
        else {
            result = DriverManager.getConnection(url, username, password);
        }
        if (this.clientInfo != null) {
            result.setClientInfo(this.clientInfo);
        }
        return result;
    }
    
    public BoneCP(final BoneCPConfig config) throws SQLException {
        this.closeConnectionWatch = false;
        this.finalizableRefs = new ConcurrentHashMap<Connection, Reference<ConnectionHandle>>();
        this.statistics = new Statistics(this);
        this.dbIsDown = new AtomicBoolean();
        this.driverInitialized = false;
        try {
            this.jvmMajorVersion = 5;
            final Class<?> clazz = Class.forName(BoneCP.connectionClass, true, config.getClassLoader());
            clazz.getMethod("createClob", (Class<?>[])new Class[0]);
            this.jvmMajorVersion = 6;
            clazz.getMethod("getNetworkTimeout", (Class<?>[])new Class[0]);
            this.jvmMajorVersion = 7;
        }
        catch (Exception ex) {}
        try {
            this.config = Preconditions.checkNotNull(config).clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new SQLException("Cloning of the config failed");
        }
        this.config.sanitize();
        this.statisticsEnabled = config.isStatisticsEnabled();
        this.closeConnectionWatchTimeoutInMs = config.getCloseConnectionWatchTimeoutInMs();
        this.poolAvailabilityThreshold = config.getPoolAvailabilityThreshold();
        this.connectionTimeoutInMs = config.getConnectionTimeoutInMs();
        if (this.connectionTimeoutInMs == 0L) {
            this.connectionTimeoutInMs = Long.MAX_VALUE;
        }
        this.nullOnConnectionTimeout = config.isNullOnConnectionTimeout();
        this.resetConnectionOnClose = config.isResetConnectionOnClose();
        this.clientInfo = ((this.jvmMajorVersion > 5) ? config.getClientInfo() : null);
        final AcquireFailConfig acquireConfig = new AcquireFailConfig();
        acquireConfig.setAcquireRetryAttempts(new AtomicInteger(0));
        acquireConfig.setAcquireRetryDelayInMs(0L);
        acquireConfig.setLogMessage("Failed to obtain initial connection");
        if (!config.isLazyInit()) {
            try {
                final Connection sanityConnection = this.obtainRawInternalConnection();
                sanityConnection.close();
            }
            catch (Exception e) {
                if (config.getConnectionHook() != null) {
                    config.getConnectionHook().onAcquireFail(e, acquireConfig);
                }
                throw PoolUtil.generateSQLException(String.format("Unable to open a test connection to the given database. JDBC url = %s, username = %s. Terminating connection pool (set lazyInit to true if you expect to start your database after your app). Original Exception: %s", config.getJdbcUrl(), config.getUsername(), PoolUtil.stringifyException(e)), e);
            }
        }
        if (!config.isDisableConnectionTracking()) {
            this.finalizableRefQueue = new FinalizableReferenceQueue();
        }
        this.asyncExecutor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        this.config = config;
        this.partitions = new ConnectionPartition[config.getPartitionCount()];
        String suffix = "";
        if (config.getPoolName() != null) {
            suffix = "-" + config.getPoolName();
        }
        this.keepAliveScheduler = Executors.newScheduledThreadPool(config.getPartitionCount(), new CustomThreadFactory("BoneCP-keep-alive-scheduler" + suffix, true));
        this.maxAliveScheduler = Executors.newScheduledThreadPool(config.getPartitionCount(), new CustomThreadFactory("BoneCP-max-alive-scheduler" + suffix, true));
        this.connectionsScheduler = Executors.newFixedThreadPool(config.getPartitionCount(), new CustomThreadFactory("BoneCP-pool-watch-thread" + suffix, true));
        this.partitionCount = config.getPartitionCount();
        this.closeConnectionWatch = config.isCloseConnectionWatch();
        this.cachedPoolStrategy = (config.getPoolStrategy() != null && config.getPoolStrategy().equalsIgnoreCase("CACHED"));
        if (this.cachedPoolStrategy) {
            this.connectionStrategy = new CachedConnectionStrategy(this, new DefaultConnectionStrategy(this));
        }
        else {
            this.connectionStrategy = new DefaultConnectionStrategy(this);
        }
        final boolean queueLIFO = config.getServiceOrder() != null && config.getServiceOrder().equalsIgnoreCase("LIFO");
        if (this.closeConnectionWatch) {
            BoneCP.logger.warn("Thread close connection monitoring has been enabled. This will negatively impact on your performance. Only enable this option for debugging purposes!");
            this.closeConnectionExecutor = Executors.newCachedThreadPool(new CustomThreadFactory("BoneCP-connection-watch-thread" + suffix, true));
        }
        for (int p = 0; p < config.getPartitionCount(); ++p) {
            final ConnectionPartition connectionPartition = new ConnectionPartition(this);
            this.partitions[p] = connectionPartition;
            final BlockingQueue<ConnectionHandle> connectionHandles = new LinkedBlockingQueue<ConnectionHandle>(this.config.getMaxConnectionsPerPartition());
            this.partitions[p].setFreeConnections(connectionHandles);
            if (!config.isLazyInit()) {
                for (int i = 0; i < config.getMinConnectionsPerPartition(); ++i) {
                    this.partitions[p].addFreeConnection(new ConnectionHandle(null, this.partitions[p], this, false));
                }
            }
            if (config.getIdleConnectionTestPeriod(TimeUnit.SECONDS) > 0L || config.getIdleMaxAge(TimeUnit.SECONDS) > 0L) {
                final Runnable connectionTester = new ConnectionTesterThread(connectionPartition, this.keepAliveScheduler, this, config.getIdleMaxAge(TimeUnit.MILLISECONDS), config.getIdleConnectionTestPeriod(TimeUnit.MILLISECONDS), queueLIFO);
                long delayInSeconds = config.getIdleConnectionTestPeriod(TimeUnit.SECONDS);
                if (delayInSeconds == 0L) {
                    delayInSeconds = config.getIdleMaxAge(TimeUnit.SECONDS);
                }
                if (config.getIdleMaxAge(TimeUnit.SECONDS) < delayInSeconds && config.getIdleConnectionTestPeriod(TimeUnit.SECONDS) != 0L && config.getIdleMaxAge(TimeUnit.SECONDS) != 0L) {
                    delayInSeconds = config.getIdleMaxAge(TimeUnit.SECONDS);
                }
                this.keepAliveScheduler.schedule(connectionTester, delayInSeconds, TimeUnit.SECONDS);
            }
            if (config.getMaxConnectionAgeInSeconds() > 0L) {
                final Runnable connectionMaxAgeTester = new ConnectionMaxAgeThread(connectionPartition, this.maxAliveScheduler, this, config.getMaxConnectionAge(TimeUnit.MILLISECONDS), queueLIFO);
                this.maxAliveScheduler.schedule(connectionMaxAgeTester, config.getMaxConnectionAgeInSeconds(), TimeUnit.SECONDS);
            }
            this.connectionsScheduler.execute(new PoolWatchThread(connectionPartition, this));
        }
        if (!this.config.isDisableJMX()) {
            this.registerUnregisterJMX(true);
        }
    }
    
    protected void registerUnregisterJMX(final boolean doRegister) {
        if (this.mbs == null) {
            this.mbs = ManagementFactory.getPlatformMBeanServer();
        }
        try {
            String suffix = "";
            if (this.config.getPoolName() != null) {
                suffix = "-" + this.config.getPoolName();
            }
            final ObjectName name = new ObjectName("com.jolbox.bonecp:type=BoneCP" + suffix);
            final ObjectName configname = new ObjectName("com.jolbox.bonecp:type=BoneCPConfig" + suffix);
            if (doRegister) {
                if (!this.mbs.isRegistered(name)) {
                    this.mbs.registerMBean(this.statistics, name);
                }
                if (!this.mbs.isRegistered(configname)) {
                    this.mbs.registerMBean(this.config, configname);
                }
            }
            else {
                if (this.mbs.isRegistered(name)) {
                    this.mbs.unregisterMBean(name);
                }
                if (this.mbs.isRegistered(configname)) {
                    this.mbs.unregisterMBean(configname);
                }
            }
        }
        catch (Exception e) {
            BoneCP.logger.error("Unable to start/stop JMX", e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        return this.connectionStrategy.getConnection();
    }
    
    protected void watchConnection(final ConnectionHandle connectionHandle) {
        final String message = this.captureStackTrace("Connection obtained from thread [%s] was never closed. \nStack trace of location where connection was obtained follows:\n");
        this.closeConnectionExecutor.submit(new CloseThreadMonitor(Thread.currentThread(), connectionHandle, message, this.closeConnectionWatchTimeoutInMs));
    }
    
    protected String captureStackTrace(final String message) {
        final StringBuilder stringBuilder = new StringBuilder(String.format(message, Thread.currentThread().getName()));
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < trace.length; ++i) {
            stringBuilder.append(" " + trace[i] + "\r\n");
        }
        stringBuilder.append("");
        return stringBuilder.toString();
    }
    
    public ListenableFuture<Connection> getAsyncConnection() {
        return this.asyncExecutor.submit((Callable<Connection>)new Callable<Connection>() {
            public Connection call() throws Exception {
                return BoneCP.this.getConnection();
            }
        });
    }
    
    protected void maybeSignalForMoreConnections(final ConnectionPartition connectionPartition) {
        if (!connectionPartition.isUnableToCreateMoreTransactions() && !this.poolShuttingDown && connectionPartition.getAvailableConnections() * 100 / connectionPartition.getMaxConnections() <= this.poolAvailabilityThreshold) {
            connectionPartition.getPoolWatchThreadSignalQueue().offer(new Object());
        }
    }
    
    protected void releaseConnection(final Connection connection) throws SQLException {
        final ConnectionHandle handle = (ConnectionHandle)connection;
        if (handle.getConnectionHook() != null) {
            handle.getConnectionHook().onCheckIn(handle);
        }
        if (!this.poolShuttingDown) {
            this.internalReleaseConnection(handle);
        }
    }
    
    protected void internalReleaseConnection(final ConnectionHandle connectionHandle) throws SQLException {
        if (!this.cachedPoolStrategy) {
            connectionHandle.clearStatementCaches(false);
        }
        if (connectionHandle.getReplayLog() != null) {
            connectionHandle.getReplayLog().clear();
            connectionHandle.recoveryResult.getReplaceTarget().clear();
        }
        if (connectionHandle.isExpired() || (!this.poolShuttingDown && connectionHandle.isPossiblyBroken() && !this.isConnectionHandleAlive(connectionHandle))) {
            if (connectionHandle.isExpired()) {
                connectionHandle.internalClose();
            }
            final ConnectionPartition connectionPartition = connectionHandle.getOriginatingPartition();
            this.postDestroyConnection(connectionHandle);
            this.maybeSignalForMoreConnections(connectionPartition);
            connectionHandle.clearStatementCaches(true);
            return;
        }
        connectionHandle.setConnectionLastUsedInMs(System.currentTimeMillis());
        if (!this.poolShuttingDown) {
            this.putConnectionBackInPartition(connectionHandle);
        }
        else {
            connectionHandle.internalClose();
        }
    }
    
    protected void putConnectionBackInPartition(final ConnectionHandle connectionHandle) throws SQLException {
        if (this.cachedPoolStrategy && ((CachedConnectionStrategy)this.connectionStrategy).tlConnections.dumbGet().getValue()) {
            connectionHandle.logicallyClosed.set(true);
            ((CachedConnectionStrategy)this.connectionStrategy).tlConnections.set(new AbstractMap.SimpleEntry<ConnectionHandle, Boolean>(connectionHandle, false));
        }
        else {
            final BlockingQueue<ConnectionHandle> queue = connectionHandle.getOriginatingPartition().getFreeConnections();
            if (!queue.offer(connectionHandle)) {
                connectionHandle.internalClose();
            }
        }
    }
    
    public boolean isConnectionHandleAlive(final ConnectionHandle connection) {
        Statement stmt = null;
        boolean result = false;
        final boolean logicallyClosed = connection.logicallyClosed.get();
        try {
            connection.logicallyClosed.compareAndSet(true, false);
            final String testStatement = this.config.getConnectionTestStatement();
            ResultSet rs = null;
            if (testStatement == null) {
                rs = connection.getMetaData().getTables(null, null, "BONECPKEEPALIVE", BoneCP.METADATATABLE);
            }
            else {
                stmt = connection.createStatement();
                stmt.execute(testStatement);
            }
            if (rs != null) {
                rs.close();
            }
            result = true;
        }
        catch (SQLException e) {
            result = false;
        }
        finally {
            connection.logicallyClosed.set(logicallyClosed);
            connection.setConnectionLastResetInMs(System.currentTimeMillis());
            result = this.closeStatement(stmt, result);
        }
        return result;
    }
    
    private boolean closeStatement(final Statement stmt, final boolean result) {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                return false;
            }
        }
        return result;
    }
    
    public int getTotalLeased() {
        int total = 0;
        for (int i = 0; i < this.partitionCount && this.partitions[i] != null; ++i) {
            total += this.partitions[i].getCreatedConnections() - this.partitions[i].getAvailableConnections();
        }
        return total;
    }
    
    public int getTotalFree() {
        int total = 0;
        for (int i = 0; i < this.partitionCount && this.partitions[i] != null; ++i) {
            total += this.partitions[i].getAvailableConnections();
        }
        return total;
    }
    
    public int getTotalCreatedConnections() {
        int total = 0;
        for (int i = 0; i < this.partitionCount && this.partitions[i] != null; ++i) {
            total += this.partitions[i].getCreatedConnections();
        }
        return total;
    }
    
    public BoneCPConfig getConfig() {
        return this.config;
    }
    
    protected Map<Connection, Reference<ConnectionHandle>> getFinalizableRefs() {
        return this.finalizableRefs;
    }
    
    protected FinalizableReferenceQueue getFinalizableRefQueue() {
        return this.finalizableRefQueue;
    }
    
    public Statistics getStatistics() {
        return this.statistics;
    }
    
    public AtomicBoolean getDbIsDown() {
        return this.dbIsDown;
    }
    
    static {
        METADATATABLE = new String[] { "TABLE" };
        logger = LoggerFactory.getLogger(BoneCP.class);
        BoneCP.connectionClass = "java.sql.Connection";
    }
}
