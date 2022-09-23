// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.ipc.Server;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.util.StringUtils;
import org.apache.zookeeper.data.ACL;
import com.google.common.base.Preconditions;
import org.apache.zookeeper.ZooDefs;
import org.apache.hadoop.util.ZKUtil;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.zookeeper.KeeperException;
import org.apache.hadoop.security.SecurityUtil;
import java.security.PrivilegedAction;
import java.util.List;
import org.apache.hadoop.security.authorize.PolicyProvider;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.AccessControlException;
import java.io.IOException;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
public abstract class ZKFailoverController
{
    static final Logger LOG;
    public static final String ZK_QUORUM_KEY = "ha.zookeeper.quorum";
    private static final String ZK_SESSION_TIMEOUT_KEY = "ha.zookeeper.session-timeout.ms";
    private static final int ZK_SESSION_TIMEOUT_DEFAULT = 10000;
    private static final String ZK_PARENT_ZNODE_KEY = "ha.zookeeper.parent-znode";
    public static final String ZK_ACL_KEY = "ha.zookeeper.acl";
    private static final String ZK_ACL_DEFAULT = "world:anyone:rwcda";
    public static final String ZK_AUTH_KEY = "ha.zookeeper.auth";
    static final String ZK_PARENT_ZNODE_DEFAULT = "/hadoop-ha";
    protected static final String[] ZKFC_CONF_KEYS;
    protected static final String USAGE = "Usage: hdfs zkfc [ -formatZK [-force] [-nonInteractive] ]\n\t-force: formats the znode if the znode exists.\n\t-nonInteractive: formats the znode aborts if the znode exists,\n\tunless -force option is specified.";
    static final int ERR_CODE_FORMAT_DENIED = 2;
    static final int ERR_CODE_NO_PARENT_ZNODE = 3;
    static final int ERR_CODE_NO_FENCER = 4;
    static final int ERR_CODE_AUTO_FAILOVER_NOT_ENABLED = 5;
    static final int ERR_CODE_NO_ZK = 6;
    protected Configuration conf;
    private String zkQuorum;
    protected final HAServiceTarget localTarget;
    private HealthMonitor healthMonitor;
    private ActiveStandbyElector elector;
    protected ZKFCRpcServer rpcServer;
    private HealthMonitor.State lastHealthState;
    private volatile HAServiceProtocol.HAServiceState serviceState;
    private String fatalError;
    private long delayJoiningUntilNanotime;
    private ScheduledExecutorService delayExecutor;
    private ActiveAttemptRecord lastActiveAttemptRecord;
    private Object activeAttemptRecordLock;
    int serviceStateMismatchCount;
    boolean quitElectionOnBadState;
    
    protected ZKFailoverController(final Configuration conf, final HAServiceTarget localTarget) {
        this.lastHealthState = HealthMonitor.State.INITIALIZING;
        this.serviceState = HAServiceProtocol.HAServiceState.INITIALIZING;
        this.fatalError = null;
        this.delayJoiningUntilNanotime = 0L;
        this.delayExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ZKFC Delay timer #%d").build());
        this.activeAttemptRecordLock = new Object();
        this.serviceStateMismatchCount = 0;
        this.quitElectionOnBadState = false;
        this.localTarget = localTarget;
        this.conf = conf;
    }
    
    protected abstract byte[] targetToData(final HAServiceTarget p0);
    
    protected abstract HAServiceTarget dataToTarget(final byte[] p0);
    
    protected abstract void loginAsFCUser() throws IOException;
    
    protected abstract void checkRpcAdminAccess() throws AccessControlException, IOException;
    
    protected abstract InetSocketAddress getRpcAddressToBindTo();
    
    protected abstract PolicyProvider getPolicyProvider();
    
    protected abstract List<HAServiceTarget> getAllOtherNodes();
    
    protected abstract String getScopeInsideParentNode();
    
    public HAServiceTarget getLocalTarget() {
        return this.localTarget;
    }
    
    HAServiceProtocol.HAServiceState getServiceState() {
        return this.serviceState;
    }
    
    public int run(final String[] args) throws Exception {
        if (!this.localTarget.isAutoFailoverEnabled()) {
            ZKFailoverController.LOG.error("Automatic failover is not enabled for " + this.localTarget + ". Please ensure that automatic failover is enabled in the configuration before running the ZK failover controller.");
            return 5;
        }
        this.loginAsFCUser();
        try {
            return SecurityUtil.doAsLoginUserOrFatal((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
                @Override
                public Integer run() {
                    try {
                        return ZKFailoverController.this.doRun(args);
                    }
                    catch (Exception t) {
                        throw new RuntimeException(t);
                    }
                    finally {
                        if (ZKFailoverController.this.elector != null) {
                            ZKFailoverController.this.elector.terminateConnection();
                        }
                    }
                }
            });
        }
        catch (RuntimeException rte) {
            throw (Exception)rte.getCause();
        }
    }
    
    private int doRun(final String[] args) throws Exception {
        try {
            this.initZK();
        }
        catch (KeeperException ke) {
            ZKFailoverController.LOG.error("Unable to start failover controller. Unable to connect to ZooKeeper quorum at " + this.zkQuorum + ". Please check the configured value for " + "ha.zookeeper.quorum" + " and ensure that ZooKeeper is running.", ke);
            return 6;
        }
        try {
            if (args.length > 0) {
                if ("-formatZK".equals(args[0])) {
                    boolean force = false;
                    boolean interactive = true;
                    for (int i = 1; i < args.length; ++i) {
                        if ("-force".equals(args[i])) {
                            force = true;
                        }
                        else if ("-nonInteractive".equals(args[i])) {
                            interactive = false;
                        }
                        else {
                            this.badArg(args[i]);
                        }
                    }
                    return this.formatZK(force, interactive);
                }
                this.badArg(args[0]);
            }
        }
        catch (Exception e) {
            ZKFailoverController.LOG.error("The failover controller encounters runtime error", e);
            throw e;
        }
        if (!this.elector.parentZNodeExists()) {
            ZKFailoverController.LOG.error("Unable to start failover controller. Parent znode does not exist.\nRun with -formatZK flag to initialize ZooKeeper.");
            return 3;
        }
        try {
            this.localTarget.checkFencingConfigured();
        }
        catch (BadFencingConfigurationException e2) {
            ZKFailoverController.LOG.error("Fencing is not configured for " + this.localTarget + ".\nYou must configure a fencing method before using automatic failover.", e2);
            return 4;
        }
        try {
            this.initRPC();
            this.initHM();
            this.startRPC();
            this.mainLoop();
        }
        catch (Exception e) {
            ZKFailoverController.LOG.error("The failover controller encounters runtime error: ", e);
            throw e;
        }
        finally {
            this.rpcServer.stopAndJoin();
            this.elector.quitElection(true);
            this.healthMonitor.shutdown();
            this.healthMonitor.join();
        }
        return 0;
    }
    
    private void badArg(final String arg) {
        this.printUsage();
        throw new HadoopIllegalArgumentException("Bad argument: " + arg);
    }
    
    private void printUsage() {
        System.err.println("Usage: hdfs zkfc [ -formatZK [-force] [-nonInteractive] ]\n\t-force: formats the znode if the znode exists.\n\t-nonInteractive: formats the znode aborts if the znode exists,\n\tunless -force option is specified.\n");
    }
    
    private int formatZK(final boolean force, final boolean interactive) throws IOException, InterruptedException, KeeperException {
        if (this.elector.parentZNodeExists()) {
            if (!force && (!interactive || !this.confirmFormat())) {
                return 2;
            }
            try {
                this.elector.clearParentZNode();
            }
            catch (IOException e) {
                ZKFailoverController.LOG.error("Unable to clear zk parent znode", e);
                return 1;
            }
        }
        this.elector.ensureParentZNode();
        return 0;
    }
    
    private boolean confirmFormat() {
        final String parentZnode = this.getParentZnode();
        System.err.println("===============================================\nThe configured parent znode " + parentZnode + " already exists.\nAre you sure you want to clear all failover information from\nZooKeeper?\nWARNING: Before proceeding, ensure that all HDFS services and\nfailover controllers are stopped!\n===============================================");
        try {
            return ToolRunner.confirmPrompt("Proceed formatting " + parentZnode + "?");
        }
        catch (IOException e) {
            ZKFailoverController.LOG.debug("Failed to confirm", e);
            return false;
        }
    }
    
    private void initHM() {
        (this.healthMonitor = new HealthMonitor(this.conf, this.localTarget)).addCallback(new HealthCallbacks());
        this.healthMonitor.addServiceStateCallback(new ServiceStateCallBacks());
        this.healthMonitor.start();
    }
    
    protected void initRPC() throws IOException {
        final InetSocketAddress bindAddr = this.getRpcAddressToBindTo();
        this.rpcServer = new ZKFCRpcServer(this.conf, bindAddr, this, this.getPolicyProvider());
    }
    
    protected void startRPC() throws IOException {
        this.rpcServer.start();
    }
    
    private void initZK() throws HadoopIllegalArgumentException, IOException, KeeperException {
        this.zkQuorum = this.conf.get("ha.zookeeper.quorum");
        final int zkTimeout = this.conf.getInt("ha.zookeeper.session-timeout.ms", 10000);
        String zkAclConf = this.conf.get("ha.zookeeper.acl", "world:anyone:rwcda");
        zkAclConf = ZKUtil.resolveConfIndirection(zkAclConf);
        List<ACL> zkAcls = ZKUtil.parseACLs(zkAclConf);
        if (zkAcls.isEmpty()) {
            zkAcls = ZooDefs.Ids.CREATOR_ALL_ACL;
        }
        final List<ZKUtil.ZKAuthInfo> zkAuths = SecurityUtil.getZKAuthInfos(this.conf, "ha.zookeeper.auth");
        Preconditions.checkArgument(this.zkQuorum != null, "Missing required configuration '%s' for ZooKeeper quorum", "ha.zookeeper.quorum");
        Preconditions.checkArgument(zkTimeout > 0, "Invalid ZK session timeout %s", zkTimeout);
        final int maxRetryNum = this.conf.getInt("ha.failover-controller.active-standby-elector.zk.op.retries", 3);
        this.elector = new ActiveStandbyElector(this.zkQuorum, zkTimeout, this.getParentZnode(), zkAcls, zkAuths, new ElectorCallbacks(), maxRetryNum);
    }
    
    private String getParentZnode() {
        String znode = this.conf.get("ha.zookeeper.parent-znode", "/hadoop-ha");
        if (!znode.endsWith("/")) {
            znode += "/";
        }
        return znode + this.getScopeInsideParentNode();
    }
    
    private synchronized void mainLoop() throws InterruptedException {
        while (this.fatalError == null) {
            this.wait();
        }
        assert this.fatalError != null;
        throw new RuntimeException("ZK Failover Controller failed: " + this.fatalError);
    }
    
    private synchronized void fatalError(final String err) {
        ZKFailoverController.LOG.error("Fatal error occurred:" + err);
        this.fatalError = err;
        this.notifyAll();
    }
    
    private synchronized void becomeActive() throws ServiceFailedException {
        ZKFailoverController.LOG.info("Trying to make " + this.localTarget + " active...");
        try {
            HAServiceProtocolHelper.transitionToActive(this.localTarget.getProxy(this.conf, FailoverController.getRpcTimeoutToNewActive(this.conf)), this.createReqInfo());
            final String msg = "Successfully transitioned " + this.localTarget + " to active state";
            ZKFailoverController.LOG.info(msg);
            this.serviceState = HAServiceProtocol.HAServiceState.ACTIVE;
            this.recordActiveAttempt(new ActiveAttemptRecord(true, msg));
        }
        catch (Throwable t) {
            final String msg2 = "Couldn't make " + this.localTarget + " active";
            ZKFailoverController.LOG.error(msg2, t);
            this.recordActiveAttempt(new ActiveAttemptRecord(false, msg2 + "\n" + StringUtils.stringifyException(t)));
            if (t instanceof ServiceFailedException) {
                throw (ServiceFailedException)t;
            }
            throw new ServiceFailedException("Couldn't transition to active", t);
        }
    }
    
    private void recordActiveAttempt(final ActiveAttemptRecord record) {
        synchronized (this.activeAttemptRecordLock) {
            this.lastActiveAttemptRecord = record;
            this.activeAttemptRecordLock.notifyAll();
        }
    }
    
    private ActiveAttemptRecord waitForActiveAttempt(final int timeoutMillis) throws InterruptedException {
        final long st = System.nanoTime();
        final long waitUntil = st + TimeUnit.NANOSECONDS.convert(timeoutMillis, TimeUnit.MILLISECONDS);
        do {
            synchronized (this) {
                if (this.lastHealthState != HealthMonitor.State.SERVICE_HEALTHY) {
                    return null;
                }
            }
            synchronized (this.activeAttemptRecordLock) {
                if (this.lastActiveAttemptRecord != null && this.lastActiveAttemptRecord.nanoTime >= st) {
                    return this.lastActiveAttemptRecord;
                }
                this.activeAttemptRecordLock.wait(1000L);
            }
        } while (System.nanoTime() < waitUntil);
        ZKFailoverController.LOG.warn(timeoutMillis + "ms timeout elapsed waiting for an attempt to become active");
        return null;
    }
    
    private HAServiceProtocol.StateChangeRequestInfo createReqInfo() {
        return new HAServiceProtocol.StateChangeRequestInfo(HAServiceProtocol.RequestSource.REQUEST_BY_ZKFC);
    }
    
    private synchronized void becomeStandby() {
        ZKFailoverController.LOG.info("ZK Election indicated that " + this.localTarget + " should become standby");
        try {
            final int timeout = FailoverController.getGracefulFenceTimeout(this.conf);
            this.localTarget.getProxy(this.conf, timeout).transitionToStandby(this.createReqInfo());
            ZKFailoverController.LOG.info("Successfully transitioned " + this.localTarget + " to standby state");
        }
        catch (Exception e) {
            ZKFailoverController.LOG.error("Couldn't transition " + this.localTarget + " to standby state", e);
        }
        this.serviceState = HAServiceProtocol.HAServiceState.STANDBY;
    }
    
    private synchronized void fenceOldActive(final byte[] data) {
        final HAServiceTarget target = this.dataToTarget(data);
        try {
            this.doFence(target);
        }
        catch (Throwable t) {
            this.recordActiveAttempt(new ActiveAttemptRecord(false, "Unable to fence old active: " + StringUtils.stringifyException(t)));
            throw t;
        }
    }
    
    private void doFence(final HAServiceTarget target) {
        ZKFailoverController.LOG.info("Should fence: " + target);
        final boolean gracefulWorked = new FailoverController(this.conf, HAServiceProtocol.RequestSource.REQUEST_BY_ZKFC).tryGracefulFence(target);
        if (gracefulWorked) {
            ZKFailoverController.LOG.info("Successfully transitioned " + target + " to standby state without fencing");
            return;
        }
        try {
            target.checkFencingConfigured();
        }
        catch (BadFencingConfigurationException e) {
            ZKFailoverController.LOG.error("Couldn't fence old active " + target, e);
            this.recordActiveAttempt(new ActiveAttemptRecord(false, "Unable to fence old active"));
            throw new RuntimeException(e);
        }
        if (!target.getFencer().fence(target)) {
            throw new RuntimeException("Unable to fence " + target);
        }
    }
    
    void cedeActive(final int millisToCede) throws AccessControlException, ServiceFailedException, IOException {
        try {
            UserGroupInformation.getLoginUser().doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    ZKFailoverController.this.doCedeActive(millisToCede);
                    return null;
                }
            });
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
    
    private void doCedeActive(final int millisToCede) throws AccessControlException, ServiceFailedException, IOException {
        final int timeout = FailoverController.getGracefulFenceTimeout(this.conf);
        synchronized (this.elector) {
            synchronized (this) {
                if (millisToCede <= 0) {
                    this.delayJoiningUntilNanotime = 0L;
                    this.recheckElectability();
                    return;
                }
                ZKFailoverController.LOG.info("Requested by " + UserGroupInformation.getCurrentUser() + " at " + Server.getRemoteAddress() + " to cede active role.");
                boolean needFence = false;
                try {
                    this.localTarget.getProxy(this.conf, timeout).transitionToStandby(this.createReqInfo());
                    ZKFailoverController.LOG.info("Successfully ensured local node is in standby mode");
                }
                catch (IOException ioe) {
                    ZKFailoverController.LOG.warn("Unable to transition local node to standby: " + ioe.getLocalizedMessage());
                    ZKFailoverController.LOG.warn("Quitting election but indicating that fencing is necessary");
                    needFence = true;
                }
                this.delayJoiningUntilNanotime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(millisToCede);
                this.elector.quitElection(needFence);
                this.serviceState = HAServiceProtocol.HAServiceState.INITIALIZING;
            }
        }
        this.recheckElectability();
    }
    
    void gracefulFailoverToYou() throws ServiceFailedException, IOException {
        try {
            UserGroupInformation.getLoginUser().doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    ZKFailoverController.this.doGracefulFailover();
                    return null;
                }
            });
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
    
    private void doGracefulFailover() throws ServiceFailedException, IOException, InterruptedException {
        final int timeout = FailoverController.getGracefulFenceTimeout(this.conf) * 2;
        this.checkEligibleForFailover();
        final HAServiceTarget oldActive = this.getCurrentActive();
        if (oldActive == null) {
            throw new ServiceFailedException("No other node is currently active.");
        }
        if (oldActive.getAddress().equals(this.localTarget.getAddress())) {
            ZKFailoverController.LOG.info("Local node " + this.localTarget + " is already active. No need to failover. Returning success.");
            return;
        }
        final List<HAServiceTarget> otherNodes = this.getAllOtherNodes();
        final List<ZKFCProtocol> otherZkfcs = new ArrayList<ZKFCProtocol>(otherNodes.size());
        HAServiceTarget activeNode = null;
        for (final HAServiceTarget remote : otherNodes) {
            if (remote.getAddress().equals(oldActive.getAddress())) {
                activeNode = remote;
            }
            else {
                otherZkfcs.add(this.cedeRemoteActive(remote, timeout));
            }
        }
        assert activeNode != null : "Active node does not match any known remote node";
        otherZkfcs.add(this.cedeRemoteActive(activeNode, timeout));
        final ActiveAttemptRecord attempt = this.waitForActiveAttempt(timeout + 60000);
        if (attempt == null) {
            synchronized (this) {
                if (this.lastHealthState != HealthMonitor.State.SERVICE_HEALTHY) {
                    throw new ServiceFailedException("Unable to become active. Service became unhealthy while trying to failover.");
                }
            }
            throw new ServiceFailedException("Unable to become active. Local node did not get an opportunity to do so from ZooKeeper, or the local node took too long to transition to active.");
        }
        for (final ZKFCProtocol zkfc : otherZkfcs) {
            zkfc.cedeActive(-1);
        }
        if (attempt.succeeded) {
            ZKFailoverController.LOG.info("Successfully became active. " + attempt.status);
            return;
        }
        final String msg = "Failed to become active. " + attempt.status;
        throw new ServiceFailedException(msg);
    }
    
    private ZKFCProtocol cedeRemoteActive(final HAServiceTarget remote, final int timeout) throws IOException {
        ZKFailoverController.LOG.info("Asking " + remote + " to cede its active state for " + timeout + "ms");
        final ZKFCProtocol oldZkfc = remote.getZKFCProxy(this.conf, timeout);
        oldZkfc.cedeActive(timeout);
        return oldZkfc;
    }
    
    private synchronized void checkEligibleForFailover() throws ServiceFailedException {
        if (this.getLastHealthState() != HealthMonitor.State.SERVICE_HEALTHY) {
            throw new ServiceFailedException(this.localTarget + " is not currently healthy. Cannot be failover target");
        }
    }
    
    private HAServiceTarget getCurrentActive() throws IOException, InterruptedException {
        synchronized (this.elector) {
            synchronized (this) {
                byte[] activeData;
                try {
                    activeData = this.elector.getActiveData();
                }
                catch (ActiveStandbyElector.ActiveNotFoundException e) {
                    return null;
                }
                catch (KeeperException ke) {
                    throw new IOException("Unexpected ZooKeeper issue fetching active node info", ke);
                }
                final HAServiceTarget oldActive = this.dataToTarget(activeData);
                return oldActive;
            }
        }
    }
    
    private void recheckElectability() {
        synchronized (this.elector) {
            synchronized (this) {
                final boolean healthy = this.lastHealthState == HealthMonitor.State.SERVICE_HEALTHY;
                final long remainingDelay = this.delayJoiningUntilNanotime - System.nanoTime();
                if (remainingDelay > 0L) {
                    if (healthy) {
                        ZKFailoverController.LOG.info("Would have joined master election, but this node is prohibited from doing so for " + TimeUnit.NANOSECONDS.toMillis(remainingDelay) + " more ms");
                    }
                    this.scheduleRecheck(remainingDelay);
                    return;
                }
                switch (this.lastHealthState) {
                    case SERVICE_HEALTHY: {
                        this.elector.joinElection(this.targetToData(this.localTarget));
                        if (this.quitElectionOnBadState) {
                            this.quitElectionOnBadState = false;
                            break;
                        }
                        break;
                    }
                    case INITIALIZING: {
                        ZKFailoverController.LOG.info("Ensuring that " + this.localTarget + " does not participate in active master election");
                        this.elector.quitElection(false);
                        this.serviceState = HAServiceProtocol.HAServiceState.INITIALIZING;
                        break;
                    }
                    case SERVICE_UNHEALTHY:
                    case SERVICE_NOT_RESPONDING: {
                        ZKFailoverController.LOG.info("Quitting master election for " + this.localTarget + " and marking that fencing is necessary");
                        this.elector.quitElection(true);
                        this.serviceState = HAServiceProtocol.HAServiceState.INITIALIZING;
                        break;
                    }
                    case HEALTH_MONITOR_FAILED: {
                        this.fatalError("Health monitor failed!");
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unhandled state:" + this.lastHealthState);
                    }
                }
            }
        }
    }
    
    private void scheduleRecheck(final long whenNanos) {
        this.delayExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    ZKFailoverController.this.recheckElectability();
                }
                catch (Throwable t) {
                    ZKFailoverController.this.fatalError("Failed to recheck electability: " + StringUtils.stringifyException(t));
                }
            }
        }, whenNanos, TimeUnit.NANOSECONDS);
    }
    
    void verifyChangedServiceState(final HAServiceProtocol.HAServiceState changedState) {
        synchronized (this.elector) {
            synchronized (this) {
                if (this.serviceState == HAServiceProtocol.HAServiceState.INITIALIZING) {
                    if (this.quitElectionOnBadState) {
                        ZKFailoverController.LOG.debug("rechecking for electability from bad state");
                        this.recheckElectability();
                    }
                    return;
                }
                if (changedState == this.serviceState) {
                    this.serviceStateMismatchCount = 0;
                    return;
                }
                if (this.serviceStateMismatchCount == 0) {
                    ++this.serviceStateMismatchCount;
                    return;
                }
                ZKFailoverController.LOG.error("Local service " + this.localTarget + " has changed the serviceState to " + changedState + ". Expected was " + this.serviceState + ". Quitting election marking fencing necessary.");
                this.delayJoiningUntilNanotime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(1000L);
                this.elector.quitElection(true);
                this.quitElectionOnBadState = true;
                this.serviceStateMismatchCount = 0;
                this.serviceState = HAServiceProtocol.HAServiceState.INITIALIZING;
            }
        }
    }
    
    protected synchronized HealthMonitor.State getLastHealthState() {
        return this.lastHealthState;
    }
    
    protected synchronized void setLastHealthState(final HealthMonitor.State newState) {
        ZKFailoverController.LOG.info("Local service " + this.localTarget + " entered state: " + newState);
        this.lastHealthState = newState;
    }
    
    @VisibleForTesting
    ActiveStandbyElector getElectorForTests() {
        return this.elector;
    }
    
    @VisibleForTesting
    ZKFCRpcServer getRpcServerForTests() {
        return this.rpcServer;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZKFailoverController.class);
        ZKFC_CONF_KEYS = new String[] { "ha.zookeeper.quorum", "ha.zookeeper.session-timeout.ms", "ha.zookeeper.parent-znode", "ha.zookeeper.acl", "ha.zookeeper.auth" };
    }
    
    class ElectorCallbacks implements ActiveStandbyElector.ActiveStandbyElectorCallback
    {
        @Override
        public void becomeActive() throws ServiceFailedException {
            ZKFailoverController.this.becomeActive();
        }
        
        @Override
        public void becomeStandby() {
            ZKFailoverController.this.becomeStandby();
        }
        
        @Override
        public void enterNeutralMode() {
        }
        
        @Override
        public void notifyFatalError(final String errorMessage) {
            ZKFailoverController.this.fatalError(errorMessage);
        }
        
        @Override
        public void fenceOldActive(final byte[] data) {
            ZKFailoverController.this.fenceOldActive(data);
        }
        
        @Override
        public String toString() {
            synchronized (ZKFailoverController.this) {
                return "Elector callbacks for " + ZKFailoverController.this.localTarget;
            }
        }
    }
    
    class HealthCallbacks implements HealthMonitor.Callback
    {
        @Override
        public void enteredState(final HealthMonitor.State newState) {
            ZKFailoverController.this.setLastHealthState(newState);
            ZKFailoverController.this.recheckElectability();
        }
    }
    
    class ServiceStateCallBacks implements HealthMonitor.ServiceStateCallback
    {
        @Override
        public void reportServiceStatus(final HAServiceStatus status) {
            ZKFailoverController.this.verifyChangedServiceState(status.getState());
        }
    }
    
    private static class ActiveAttemptRecord
    {
        private final boolean succeeded;
        private final String status;
        private final long nanoTime;
        
        public ActiveAttemptRecord(final boolean succeeded, final String status) {
            this.succeeded = succeeded;
            this.status = status;
            this.nanoTime = System.nanoTime();
        }
    }
}
