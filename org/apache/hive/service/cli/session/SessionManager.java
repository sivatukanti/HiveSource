// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.session;

import org.apache.commons.logging.LogFactory;
import java.util.concurrent.Future;
import java.util.List;
import org.apache.hadoop.hive.ql.hooks.HookUtils;
import org.apache.hive.service.cli.thrift.TProtocolVersion;
import java.util.Iterator;
import org.apache.hive.service.cli.HiveSQLException;
import java.util.Date;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import org.apache.hive.service.server.ThreadFactoryWithGarbageCleanup;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.TimeUnit;
import org.apache.hive.service.Service;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hive.service.server.HiveServer2;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hive.service.cli.operation.OperationManager;
import org.apache.hive.service.cli.SessionHandle;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;
import org.apache.hive.service.CompositeService;

public class SessionManager extends CompositeService
{
    private static final Log LOG;
    public static final String HIVERCFILE = ".hiverc";
    private HiveConf hiveConf;
    private final Map<SessionHandle, HiveSession> handleToSession;
    private final OperationManager operationManager;
    private ThreadPoolExecutor backgroundOperationPool;
    private boolean isOperationLogEnabled;
    private File operationLogRootDir;
    private long checkInterval;
    private long sessionTimeout;
    private boolean checkOperation;
    private volatile boolean shutdown;
    private final HiveServer2 hiveServer2;
    private static ThreadLocal<String> threadLocalIpAddress;
    private static ThreadLocal<String> threadLocalUserName;
    private static ThreadLocal<String> threadLocalProxyUserName;
    
    public SessionManager(final HiveServer2 hiveServer2) {
        super(SessionManager.class.getSimpleName());
        this.handleToSession = new ConcurrentHashMap<SessionHandle, HiveSession>();
        this.operationManager = new OperationManager();
        this.hiveServer2 = hiveServer2;
    }
    
    @Override
    public synchronized void init(final HiveConf hiveConf) {
        this.hiveConf = hiveConf;
        if (hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_LOGGING_OPERATION_ENABLED)) {
            this.initOperationLogRootDir();
        }
        this.createBackgroundOperationPool();
        this.addService(this.operationManager);
        super.init(hiveConf);
    }
    
    private void createBackgroundOperationPool() {
        final int poolSize = this.hiveConf.getIntVar(HiveConf.ConfVars.HIVE_SERVER2_ASYNC_EXEC_THREADS);
        SessionManager.LOG.info("HiveServer2: Background operation thread pool size: " + poolSize);
        final int poolQueueSize = this.hiveConf.getIntVar(HiveConf.ConfVars.HIVE_SERVER2_ASYNC_EXEC_WAIT_QUEUE_SIZE);
        SessionManager.LOG.info("HiveServer2: Background operation thread wait queue size: " + poolQueueSize);
        final long keepAliveTime = HiveConf.getTimeVar(this.hiveConf, HiveConf.ConfVars.HIVE_SERVER2_ASYNC_EXEC_KEEPALIVE_TIME, TimeUnit.SECONDS);
        SessionManager.LOG.info("HiveServer2: Background operation thread keepalive time: " + keepAliveTime + " seconds");
        final String threadPoolName = "HiveServer2-Background-Pool";
        (this.backgroundOperationPool = new ThreadPoolExecutor(poolSize, poolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(poolQueueSize), new ThreadFactoryWithGarbageCleanup(threadPoolName))).allowCoreThreadTimeOut(true);
        this.checkInterval = HiveConf.getTimeVar(this.hiveConf, HiveConf.ConfVars.HIVE_SERVER2_SESSION_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
        this.sessionTimeout = HiveConf.getTimeVar(this.hiveConf, HiveConf.ConfVars.HIVE_SERVER2_IDLE_SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
        this.checkOperation = HiveConf.getBoolVar(this.hiveConf, HiveConf.ConfVars.HIVE_SERVER2_IDLE_SESSION_CHECK_OPERATION);
    }
    
    private void initOperationLogRootDir() {
        this.operationLogRootDir = new File(this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_LOGGING_OPERATION_LOG_LOCATION));
        this.isOperationLogEnabled = true;
        if (this.operationLogRootDir.exists() && !this.operationLogRootDir.isDirectory()) {
            SessionManager.LOG.warn("The operation log root directory exists, but it is not a directory: " + this.operationLogRootDir.getAbsolutePath());
            this.isOperationLogEnabled = false;
        }
        if (!this.operationLogRootDir.exists() && !this.operationLogRootDir.mkdirs()) {
            SessionManager.LOG.warn("Unable to create operation log root directory: " + this.operationLogRootDir.getAbsolutePath());
            this.isOperationLogEnabled = false;
        }
        if (this.isOperationLogEnabled) {
            SessionManager.LOG.info("Operation log root directory is created: " + this.operationLogRootDir.getAbsolutePath());
            try {
                FileUtils.forceDeleteOnExit(this.operationLogRootDir);
            }
            catch (IOException e) {
                SessionManager.LOG.warn("Failed to schedule cleanup HS2 operation logging root dir: " + this.operationLogRootDir.getAbsolutePath(), e);
            }
        }
    }
    
    @Override
    public synchronized void start() {
        super.start();
        if (this.checkInterval > 0L) {
            this.startTimeoutChecker();
        }
    }
    
    private void startTimeoutChecker() {
        final long interval = Math.max(this.checkInterval, 3000L);
        final Runnable timeoutChecker = new Runnable() {
            @Override
            public void run() {
                this.sleepInterval(interval);
                while (!SessionManager.this.shutdown) {
                    final long current = System.currentTimeMillis();
                    for (final HiveSession session : new ArrayList<HiveSession>(SessionManager.this.handleToSession.values())) {
                        if (SessionManager.this.sessionTimeout > 0L && session.getLastAccessTime() + SessionManager.this.sessionTimeout <= current && (!SessionManager.this.checkOperation || session.getNoOperationTime() > SessionManager.this.sessionTimeout)) {
                            final SessionHandle handle = session.getSessionHandle();
                            SessionManager.LOG.warn("Session " + handle + " is Timed-out (last access : " + new Date(session.getLastAccessTime()) + ") and will be closed");
                            try {
                                SessionManager.this.closeSession(handle);
                            }
                            catch (HiveSQLException e) {
                                SessionManager.LOG.warn("Exception is thrown closing session " + handle, e);
                            }
                        }
                        else {
                            session.closeExpiredOperations();
                        }
                    }
                    this.sleepInterval(interval);
                }
            }
            
            private void sleepInterval(final long interval) {
                try {
                    Thread.sleep(interval);
                }
                catch (InterruptedException ex) {}
            }
        };
        this.backgroundOperationPool.execute(timeoutChecker);
    }
    
    @Override
    public synchronized void stop() {
        super.stop();
        this.shutdown = true;
        if (this.backgroundOperationPool != null) {
            this.backgroundOperationPool.shutdown();
            final long timeout = this.hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_SERVER2_ASYNC_EXEC_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
            try {
                this.backgroundOperationPool.awaitTermination(timeout, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                SessionManager.LOG.warn("HIVE_SERVER2_ASYNC_EXEC_SHUTDOWN_TIMEOUT = " + timeout + " seconds has been exceeded. RUNNING background operations will be shut down", e);
            }
            this.backgroundOperationPool = null;
        }
        this.cleanupLoggingRootDir();
    }
    
    private void cleanupLoggingRootDir() {
        if (this.isOperationLogEnabled) {
            try {
                FileUtils.forceDelete(this.operationLogRootDir);
            }
            catch (Exception e) {
                SessionManager.LOG.warn("Failed to cleanup root dir of HS2 logging: " + this.operationLogRootDir.getAbsolutePath(), e);
            }
        }
    }
    
    public SessionHandle openSession(final TProtocolVersion protocol, final String username, final String password, final String ipAddress, final Map<String, String> sessionConf) throws HiveSQLException {
        return this.openSession(protocol, username, password, ipAddress, sessionConf, false, null);
    }
    
    public SessionHandle openSession(final TProtocolVersion protocol, final String username, final String password, final String ipAddress, final Map<String, String> sessionConf, final boolean withImpersonation, final String delegationToken) throws HiveSQLException {
        HiveSession session;
        if (withImpersonation) {
            final HiveSessionImplwithUGI sessionWithUGI = new HiveSessionImplwithUGI(protocol, username, password, this.hiveConf, ipAddress, delegationToken);
            session = HiveSessionProxy.getProxy(sessionWithUGI, sessionWithUGI.getSessionUgi());
            sessionWithUGI.setProxySession(session);
        }
        else {
            session = new HiveSessionImpl(protocol, username, password, this.hiveConf, ipAddress);
        }
        session.setSessionManager(this);
        session.setOperationManager(this.operationManager);
        try {
            session.open(sessionConf);
        }
        catch (Exception e) {
            try {
                session.close();
            }
            catch (Throwable t) {
                SessionManager.LOG.warn("Error closing session", t);
            }
            session = null;
            throw new HiveSQLException("Failed to open new session: " + e, e);
        }
        if (this.isOperationLogEnabled) {
            session.setOperationLogSessionDir(this.operationLogRootDir);
        }
        try {
            this.executeSessionHooks(session);
        }
        catch (Exception e) {
            try {
                session.close();
            }
            catch (Throwable t) {
                SessionManager.LOG.warn("Error closing session", t);
            }
            session = null;
            throw new HiveSQLException("Failed to execute session hooks", e);
        }
        this.handleToSession.put(session.getSessionHandle(), session);
        return session.getSessionHandle();
    }
    
    public void closeSession(final SessionHandle sessionHandle) throws HiveSQLException {
        final HiveSession session = this.handleToSession.remove(sessionHandle);
        if (session == null) {
            throw new HiveSQLException("Session does not exist!");
        }
        try {
            session.close();
        }
        finally {
            if (this.hiveServer2 != null && this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_SUPPORT_DYNAMIC_SERVICE_DISCOVERY) && !this.hiveServer2.isRegisteredWithZooKeeper() && this.getOpenSessionCount() == 0) {
                SessionManager.LOG.info("This instance of HiveServer2 has been removed from the list of server instances available for dynamic service discovery. The last client session has ended - will shutdown now.");
                final Thread shutdownThread = new Thread() {
                    @Override
                    public void run() {
                        SessionManager.this.hiveServer2.stop();
                    }
                };
                shutdownThread.start();
            }
        }
    }
    
    public HiveSession getSession(final SessionHandle sessionHandle) throws HiveSQLException {
        final HiveSession session = this.handleToSession.get(sessionHandle);
        if (session == null) {
            throw new HiveSQLException("Invalid SessionHandle: " + sessionHandle);
        }
        return session;
    }
    
    public OperationManager getOperationManager() {
        return this.operationManager;
    }
    
    public static void setIpAddress(final String ipAddress) {
        SessionManager.threadLocalIpAddress.set(ipAddress);
    }
    
    public static void clearIpAddress() {
        SessionManager.threadLocalIpAddress.remove();
    }
    
    public static String getIpAddress() {
        return SessionManager.threadLocalIpAddress.get();
    }
    
    public static void setUserName(final String userName) {
        SessionManager.threadLocalUserName.set(userName);
    }
    
    public static void clearUserName() {
        SessionManager.threadLocalUserName.remove();
    }
    
    public static String getUserName() {
        return SessionManager.threadLocalUserName.get();
    }
    
    public static void setProxyUserName(final String userName) {
        SessionManager.LOG.debug("setting proxy user name based on query param to: " + userName);
        SessionManager.threadLocalProxyUserName.set(userName);
    }
    
    public static String getProxyUserName() {
        return SessionManager.threadLocalProxyUserName.get();
    }
    
    public static void clearProxyUserName() {
        SessionManager.threadLocalProxyUserName.remove();
    }
    
    private void executeSessionHooks(final HiveSession session) throws Exception {
        final List<HiveSessionHook> sessionHooks = (List<HiveSessionHook>)HookUtils.getHooks(this.hiveConf, HiveConf.ConfVars.HIVE_SERVER2_SESSION_HOOK, (Class)HiveSessionHook.class);
        for (final HiveSessionHook sessionHook : sessionHooks) {
            sessionHook.run(new HiveSessionHookContextImpl(session));
        }
    }
    
    public Future<?> submitBackgroundOperation(final Runnable r) {
        return this.backgroundOperationPool.submit(r);
    }
    
    public int getOpenSessionCount() {
        return this.handleToSession.size();
    }
    
    static {
        LOG = LogFactory.getLog(CompositeService.class);
        SessionManager.threadLocalIpAddress = new ThreadLocal<String>() {
            @Override
            protected synchronized String initialValue() {
                return null;
            }
        };
        SessionManager.threadLocalUserName = new ThreadLocal<String>() {
            @Override
            protected synchronized String initialValue() {
                return null;
            }
        };
        SessionManager.threadLocalProxyUserName = new ThreadLocal<String>() {
            @Override
            protected synchronized String initialValue() {
                return null;
            }
        };
    }
}
