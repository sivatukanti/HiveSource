// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import org.apache.hadoop.util.StringUtils;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.CreateMode;
import com.google.common.base.Preconditions;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.zookeeper.KeeperException;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import org.apache.hadoop.util.ZKUtil;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.zookeeper.AsyncCallback;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class ActiveStandbyElector implements AsyncCallback.StatCallback, AsyncCallback.StringCallback
{
    @VisibleForTesting
    protected static final String LOCK_FILENAME = "ActiveStandbyElectorLock";
    @VisibleForTesting
    protected static final String BREADCRUMB_FILENAME = "ActiveBreadCrumb";
    public static final Logger LOG;
    private static final int SLEEP_AFTER_FAILURE_TO_BECOME_ACTIVE = 1000;
    private State state;
    private int createRetryCount;
    private int statRetryCount;
    private ZooKeeper zkClient;
    private WatcherWithClientRef watcher;
    private ConnectionState zkConnectionState;
    private final ActiveStandbyElectorCallback appClient;
    private final String zkHostPort;
    private final int zkSessionTimeout;
    private final List<ACL> zkAcl;
    private final List<ZKUtil.ZKAuthInfo> zkAuthInfo;
    private byte[] appData;
    private final String zkLockFilePath;
    private final String zkBreadCrumbPath;
    private final String znodeWorkingDir;
    private final int maxRetryNum;
    private Lock sessionReestablishLockForTests;
    private boolean wantToBeInElection;
    private boolean monitorLockNodePending;
    private ZooKeeper monitorLockNodeClient;
    
    public ActiveStandbyElector(final String zookeeperHostPorts, final int zookeeperSessionTimeout, final String parentZnodeName, final List<ACL> acl, final List<ZKUtil.ZKAuthInfo> authInfo, final ActiveStandbyElectorCallback app, final int maxRetryNum) throws IOException, HadoopIllegalArgumentException, KeeperException {
        this(zookeeperHostPorts, zookeeperSessionTimeout, parentZnodeName, acl, authInfo, app, maxRetryNum, true);
    }
    
    public ActiveStandbyElector(final String zookeeperHostPorts, final int zookeeperSessionTimeout, final String parentZnodeName, final List<ACL> acl, final List<ZKUtil.ZKAuthInfo> authInfo, final ActiveStandbyElectorCallback app, final int maxRetryNum, final boolean failFast) throws IOException, HadoopIllegalArgumentException, KeeperException {
        this.state = State.INIT;
        this.createRetryCount = 0;
        this.statRetryCount = 0;
        this.zkConnectionState = ConnectionState.TERMINATED;
        this.sessionReestablishLockForTests = new ReentrantLock();
        this.monitorLockNodePending = false;
        if (app == null || acl == null || parentZnodeName == null || zookeeperHostPorts == null || zookeeperSessionTimeout <= 0) {
            throw new HadoopIllegalArgumentException("Invalid argument");
        }
        this.zkHostPort = zookeeperHostPorts;
        this.zkSessionTimeout = zookeeperSessionTimeout;
        this.zkAcl = acl;
        this.zkAuthInfo = authInfo;
        this.appClient = app;
        this.znodeWorkingDir = parentZnodeName;
        this.zkLockFilePath = this.znodeWorkingDir + "/" + "ActiveStandbyElectorLock";
        this.zkBreadCrumbPath = this.znodeWorkingDir + "/" + "ActiveBreadCrumb";
        this.maxRetryNum = maxRetryNum;
        if (failFast) {
            this.createConnection();
        }
        else {
            this.reEstablishSession();
        }
    }
    
    public synchronized void joinElection(final byte[] data) throws HadoopIllegalArgumentException {
        if (data == null) {
            throw new HadoopIllegalArgumentException("data cannot be null");
        }
        if (this.wantToBeInElection) {
            ActiveStandbyElector.LOG.info("Already in election. Not re-connecting.");
            return;
        }
        System.arraycopy(data, 0, this.appData = new byte[data.length], 0, data.length);
        if (ActiveStandbyElector.LOG.isDebugEnabled()) {
            ActiveStandbyElector.LOG.debug("Attempting active election for " + this);
        }
        this.joinElectionInternal();
    }
    
    public synchronized boolean parentZNodeExists() throws IOException, InterruptedException {
        Preconditions.checkState(this.zkClient != null);
        try {
            return this.zkClient.exists(this.znodeWorkingDir, false) != null;
        }
        catch (KeeperException e) {
            throw new IOException("Couldn't determine existence of znode '" + this.znodeWorkingDir + "'", e);
        }
    }
    
    public synchronized void ensureParentZNode() throws IOException, InterruptedException, KeeperException {
        Preconditions.checkState(!this.wantToBeInElection, (Object)"ensureParentZNode() may not be called while in the election");
        if (this.zkClient == null) {
            this.createConnection();
        }
        final String[] pathParts = this.znodeWorkingDir.split("/");
        Preconditions.checkArgument(pathParts.length >= 1 && pathParts[0].isEmpty(), "Invalid path: %s", this.znodeWorkingDir);
        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i < pathParts.length; ++i) {
            sb.append("/").append(pathParts[i]);
            final String prefixPath = sb.toString();
            ActiveStandbyElector.LOG.debug("Ensuring existence of " + prefixPath);
            try {
                this.createWithRetries(prefixPath, new byte[0], this.zkAcl, CreateMode.PERSISTENT);
            }
            catch (KeeperException e) {
                if (isNodeExists(e.code())) {
                    try {
                        this.setAclsWithRetries(prefixPath);
                        continue;
                    }
                    catch (KeeperException e2) {
                        throw new IOException("Couldn't set ACLs on parent ZNode: " + prefixPath, e2);
                    }
                }
                throw new IOException("Couldn't create " + prefixPath, e);
            }
        }
        ActiveStandbyElector.LOG.info("Successfully created " + this.znodeWorkingDir + " in ZK.");
    }
    
    public synchronized void clearParentZNode() throws IOException, InterruptedException {
        Preconditions.checkState(!this.wantToBeInElection, (Object)"clearParentZNode() may not be called while in the election");
        try {
            ActiveStandbyElector.LOG.info("Recursively deleting " + this.znodeWorkingDir + " from ZK...");
            this.zkDoWithRetries((ZKAction<Object>)new ZKAction<Void>() {
                @Override
                public Void run() throws KeeperException, InterruptedException {
                    org.apache.zookeeper.ZKUtil.deleteRecursive(ActiveStandbyElector.this.zkClient, ActiveStandbyElector.this.znodeWorkingDir);
                    return null;
                }
            });
        }
        catch (KeeperException e) {
            throw new IOException("Couldn't clear parent znode " + this.znodeWorkingDir, e);
        }
        ActiveStandbyElector.LOG.info("Successfully deleted " + this.znodeWorkingDir + " from ZK.");
    }
    
    public synchronized void quitElection(final boolean needFence) {
        ActiveStandbyElector.LOG.info("Yielding from election");
        if (!needFence && this.state == State.ACTIVE) {
            this.tryDeleteOwnBreadCrumbNode();
        }
        this.reset();
        this.wantToBeInElection = false;
    }
    
    public synchronized byte[] getActiveData() throws ActiveNotFoundException, KeeperException, InterruptedException, IOException {
        try {
            if (this.zkClient == null) {
                this.createConnection();
            }
            final Stat stat = new Stat();
            return this.getDataWithRetries(this.zkLockFilePath, false, stat);
        }
        catch (KeeperException e) {
            final KeeperException.Code code = e.code();
            if (isNodeDoesNotExist(code)) {
                throw new ActiveNotFoundException();
            }
            throw e;
        }
    }
    
    @Override
    public synchronized void processResult(final int rc, final String path, final Object ctx, final String name) {
        if (this.isStaleClient(ctx)) {
            return;
        }
        if (ActiveStandbyElector.LOG.isDebugEnabled()) {
            ActiveStandbyElector.LOG.debug("CreateNode result: " + rc + " for path: " + path + " connectionState: " + this.zkConnectionState + "  for " + this);
        }
        final KeeperException.Code code = KeeperException.Code.get(rc);
        if (isSuccess(code)) {
            if (this.becomeActive()) {
                this.monitorActiveStatus();
            }
            else {
                this.reJoinElectionAfterFailureToBecomeActive();
            }
            return;
        }
        if (isNodeExists(code)) {
            if (this.createRetryCount == 0) {
                this.becomeStandby();
            }
            this.monitorActiveStatus();
            return;
        }
        String errorMessage = "Received create error from Zookeeper. code:" + code.toString() + " for path " + path;
        ActiveStandbyElector.LOG.debug(errorMessage);
        if (shouldRetry(code)) {
            if (this.createRetryCount < this.maxRetryNum) {
                ActiveStandbyElector.LOG.debug("Retrying createNode createRetryCount: " + this.createRetryCount);
                ++this.createRetryCount;
                this.createLockNodeAsync();
                return;
            }
            errorMessage += ". Not retrying further znode create connection errors.";
        }
        else if (isSessionExpired(code)) {
            ActiveStandbyElector.LOG.warn("Lock acquisition failed because session was lost");
            return;
        }
        this.fatalError(errorMessage);
    }
    
    @Override
    public synchronized void processResult(final int rc, final String path, final Object ctx, final Stat stat) {
        if (this.isStaleClient(ctx)) {
            return;
        }
        this.monitorLockNodePending = false;
        assert this.wantToBeInElection : "Got a StatNode result after quitting election";
        if (ActiveStandbyElector.LOG.isDebugEnabled()) {
            ActiveStandbyElector.LOG.debug("StatNode result: " + rc + " for path: " + path + " connectionState: " + this.zkConnectionState + " for " + this);
        }
        final KeeperException.Code code = KeeperException.Code.get(rc);
        if (isSuccess(code)) {
            if (stat.getEphemeralOwner() == this.zkClient.getSessionId()) {
                if (!this.becomeActive()) {
                    this.reJoinElectionAfterFailureToBecomeActive();
                }
            }
            else {
                this.becomeStandby();
            }
            return;
        }
        if (isNodeDoesNotExist(code)) {
            this.enterNeutralMode();
            this.joinElectionInternal();
            return;
        }
        String errorMessage = "Received stat error from Zookeeper. code:" + code.toString();
        ActiveStandbyElector.LOG.debug(errorMessage);
        if (shouldRetry(code)) {
            if (this.statRetryCount < this.maxRetryNum) {
                ++this.statRetryCount;
                this.monitorLockNodeAsync();
                return;
            }
            errorMessage += ". Not retrying further znode monitoring connection errors.";
        }
        else if (isSessionExpired(code)) {
            ActiveStandbyElector.LOG.warn("Lock monitoring failed because session was lost");
            return;
        }
        this.fatalError(errorMessage);
    }
    
    private void reJoinElectionAfterFailureToBecomeActive() {
        this.reJoinElection(1000);
    }
    
    synchronized void processWatchEvent(final ZooKeeper zk, final WatchedEvent event) {
        final Watcher.Event.EventType eventType = event.getType();
        if (this.isStaleClient(zk)) {
            return;
        }
        if (ActiveStandbyElector.LOG.isDebugEnabled()) {
            ActiveStandbyElector.LOG.debug("Watcher event type: " + eventType + " with state:" + event.getState() + " for path:" + event.getPath() + " connectionState: " + this.zkConnectionState + " for " + this);
        }
        if (eventType == Watcher.Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected: {
                    ActiveStandbyElector.LOG.info("Session connected.");
                    final ConnectionState prevConnectionState = this.zkConnectionState;
                    this.zkConnectionState = ConnectionState.CONNECTED;
                    if (prevConnectionState == ConnectionState.DISCONNECTED && this.wantToBeInElection) {
                        this.monitorActiveStatus();
                        break;
                    }
                    break;
                }
                case Disconnected: {
                    ActiveStandbyElector.LOG.info("Session disconnected. Entering neutral mode...");
                    this.zkConnectionState = ConnectionState.DISCONNECTED;
                    this.enterNeutralMode();
                    break;
                }
                case Expired: {
                    ActiveStandbyElector.LOG.info("Session expired. Entering neutral mode and rejoining...");
                    this.enterNeutralMode();
                    this.reJoinElection(0);
                    break;
                }
                case SaslAuthenticated: {
                    ActiveStandbyElector.LOG.info("Successfully authenticated to ZooKeeper using SASL.");
                    break;
                }
                default: {
                    this.fatalError("Unexpected Zookeeper watch event state: " + event.getState());
                    break;
                }
            }
            return;
        }
        final String path = event.getPath();
        if (path != null) {
            switch (eventType) {
                case NodeDeleted: {
                    if (this.state == State.ACTIVE) {
                        this.enterNeutralMode();
                    }
                    this.joinElectionInternal();
                    break;
                }
                case NodeDataChanged: {
                    this.monitorActiveStatus();
                    break;
                }
                default: {
                    if (ActiveStandbyElector.LOG.isDebugEnabled()) {
                        ActiveStandbyElector.LOG.debug("Unexpected node event: " + eventType + " for path: " + path);
                    }
                    this.monitorActiveStatus();
                    break;
                }
            }
            return;
        }
        this.fatalError("Unexpected watch error from Zookeeper");
    }
    
    protected synchronized ZooKeeper connectToZooKeeper() throws IOException, KeeperException {
        this.watcher = new WatcherWithClientRef();
        final ZooKeeper zk = this.createZooKeeper();
        this.watcher.setZooKeeperRef(zk);
        this.watcher.waitForZKConnectionEvent(this.zkSessionTimeout);
        for (final ZKUtil.ZKAuthInfo auth : this.zkAuthInfo) {
            zk.addAuthInfo(auth.getScheme(), auth.getAuth());
        }
        return zk;
    }
    
    protected ZooKeeper createZooKeeper() throws IOException {
        return new ZooKeeper(this.zkHostPort, this.zkSessionTimeout, this.watcher);
    }
    
    private void fatalError(final String errorMessage) {
        ActiveStandbyElector.LOG.error(errorMessage);
        this.reset();
        this.appClient.notifyFatalError(errorMessage);
    }
    
    private void monitorActiveStatus() {
        assert this.wantToBeInElection;
        if (ActiveStandbyElector.LOG.isDebugEnabled()) {
            ActiveStandbyElector.LOG.debug("Monitoring active leader for " + this);
        }
        this.statRetryCount = 0;
        this.monitorLockNodeAsync();
    }
    
    private void joinElectionInternal() {
        Preconditions.checkState(this.appData != null, (Object)"trying to join election without any app data");
        if (this.zkClient == null && !this.reEstablishSession()) {
            this.fatalError("Failed to reEstablish connection with ZooKeeper");
            return;
        }
        this.createRetryCount = 0;
        this.wantToBeInElection = true;
        this.createLockNodeAsync();
    }
    
    private void reJoinElection(final int sleepTime) {
        ActiveStandbyElector.LOG.info("Trying to re-establish ZK session");
        this.sessionReestablishLockForTests.lock();
        try {
            this.terminateConnection();
            this.sleepFor(sleepTime);
            if (this.appData != null) {
                this.joinElectionInternal();
            }
            else {
                ActiveStandbyElector.LOG.info("Not joining election since service has not yet been reported as healthy.");
            }
        }
        finally {
            this.sessionReestablishLockForTests.unlock();
        }
    }
    
    @VisibleForTesting
    protected void sleepFor(final int sleepMs) {
        if (sleepMs > 0) {
            try {
                Thread.sleep(sleepMs);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @VisibleForTesting
    void preventSessionReestablishmentForTests() {
        this.sessionReestablishLockForTests.lock();
    }
    
    @VisibleForTesting
    void allowSessionReestablishmentForTests() {
        this.sessionReestablishLockForTests.unlock();
    }
    
    @VisibleForTesting
    synchronized long getZKSessionIdForTests() {
        if (this.zkClient != null) {
            return this.zkClient.getSessionId();
        }
        return -1L;
    }
    
    @VisibleForTesting
    synchronized State getStateForTests() {
        return this.state;
    }
    
    @VisibleForTesting
    synchronized boolean isMonitorLockNodePending() {
        return this.monitorLockNodePending;
    }
    
    private boolean reEstablishSession() {
        int connectionRetryCount;
        boolean success;
        for (connectionRetryCount = 0, success = false; !success && connectionRetryCount < this.maxRetryNum; ++connectionRetryCount) {
            if (ActiveStandbyElector.LOG.isDebugEnabled()) {
                ActiveStandbyElector.LOG.debug("Establishing zookeeper connection for " + this);
            }
            try {
                this.createConnection();
                success = true;
            }
            catch (IOException e) {
                ActiveStandbyElector.LOG.warn(e.toString());
                this.sleepFor(5000);
            }
            catch (KeeperException e2) {
                ActiveStandbyElector.LOG.warn(e2.toString());
                this.sleepFor(5000);
            }
        }
        return success;
    }
    
    private void createConnection() throws IOException, KeeperException {
        if (this.zkClient != null) {
            try {
                this.zkClient.close();
            }
            catch (InterruptedException e) {
                throw new IOException("Interrupted while closing ZK", e);
            }
            this.zkClient = null;
            this.watcher = null;
        }
        this.zkClient = this.connectToZooKeeper();
        if (ActiveStandbyElector.LOG.isDebugEnabled()) {
            ActiveStandbyElector.LOG.debug("Created new connection for " + this);
        }
    }
    
    @InterfaceAudience.Private
    public synchronized void terminateConnection() {
        if (this.zkClient == null) {
            return;
        }
        if (ActiveStandbyElector.LOG.isDebugEnabled()) {
            ActiveStandbyElector.LOG.debug("Terminating ZK connection for " + this);
        }
        final ZooKeeper tempZk = this.zkClient;
        this.zkClient = null;
        this.watcher = null;
        try {
            tempZk.close();
        }
        catch (InterruptedException e) {
            ActiveStandbyElector.LOG.warn(e.toString());
        }
        this.zkConnectionState = ConnectionState.TERMINATED;
        this.wantToBeInElection = false;
    }
    
    private void reset() {
        this.state = State.INIT;
        this.terminateConnection();
    }
    
    private boolean becomeActive() {
        assert this.wantToBeInElection;
        if (this.state == State.ACTIVE) {
            return true;
        }
        try {
            final Stat oldBreadcrumbStat = this.fenceOldActive();
            this.writeBreadCrumbNode(oldBreadcrumbStat);
            ActiveStandbyElector.LOG.debug("Becoming active for {}", this);
            this.appClient.becomeActive();
            this.state = State.ACTIVE;
            return true;
        }
        catch (Exception e) {
            ActiveStandbyElector.LOG.warn("Exception handling the winning of election", e);
            return false;
        }
    }
    
    private void writeBreadCrumbNode(final Stat oldBreadcrumbStat) throws KeeperException, InterruptedException {
        Preconditions.checkState(this.appData != null, (Object)"no appdata");
        ActiveStandbyElector.LOG.info("Writing znode {} to indicate that the local node is the most recent active...", this.zkBreadCrumbPath);
        if (oldBreadcrumbStat == null) {
            this.createWithRetries(this.zkBreadCrumbPath, this.appData, this.zkAcl, CreateMode.PERSISTENT);
        }
        else {
            this.setDataWithRetries(this.zkBreadCrumbPath, this.appData, oldBreadcrumbStat.getVersion());
        }
    }
    
    private void tryDeleteOwnBreadCrumbNode() {
        assert this.state == State.ACTIVE;
        ActiveStandbyElector.LOG.info("Deleting bread-crumb of active node...");
        final Stat stat = new Stat();
        byte[] data = null;
        try {
            data = this.zkClient.getData(this.zkBreadCrumbPath, false, stat);
            if (!Arrays.equals(data, this.appData)) {
                throw new IllegalStateException("We thought we were active, but in fact the active znode had the wrong data: " + StringUtils.byteToHexString(data) + " (stat=" + stat + ")");
            }
            this.deleteWithRetries(this.zkBreadCrumbPath, stat.getVersion());
        }
        catch (Exception e) {
            ActiveStandbyElector.LOG.warn("Unable to delete our own bread-crumb of being active at {}.. Expecting to be fenced by the next active.", this.zkBreadCrumbPath, e);
        }
    }
    
    private Stat fenceOldActive() throws InterruptedException, KeeperException {
        final Stat stat = new Stat();
        ActiveStandbyElector.LOG.info("Checking for any old active which needs to be fenced...");
        byte[] data;
        try {
            data = this.zkDoWithRetries((ZKAction<byte[]>)new ZKAction<byte[]>() {
                @Override
                public byte[] run() throws KeeperException, InterruptedException {
                    return ActiveStandbyElector.this.zkClient.getData(ActiveStandbyElector.this.zkBreadCrumbPath, false, stat);
                }
            });
        }
        catch (KeeperException ke) {
            if (isNodeDoesNotExist(ke.code())) {
                ActiveStandbyElector.LOG.info("No old node to fence");
                return null;
            }
            throw ke;
        }
        ActiveStandbyElector.LOG.info("Old node exists: {}", StringUtils.byteToHexString(data));
        if (Arrays.equals(data, this.appData)) {
            ActiveStandbyElector.LOG.info("But old node has our own data, so don't need to fence it.");
        }
        else {
            this.appClient.fenceOldActive(data);
        }
        return stat;
    }
    
    private void becomeStandby() {
        if (this.state != State.STANDBY) {
            ActiveStandbyElector.LOG.debug("Becoming standby for {}", this);
            this.state = State.STANDBY;
            this.appClient.becomeStandby();
        }
    }
    
    private void enterNeutralMode() {
        if (this.state != State.NEUTRAL) {
            ActiveStandbyElector.LOG.debug("Entering neutral mode for {}", this);
            this.state = State.NEUTRAL;
            this.appClient.enterNeutralMode();
        }
    }
    
    private void createLockNodeAsync() {
        this.zkClient.create(this.zkLockFilePath, this.appData, this.zkAcl, CreateMode.EPHEMERAL, this, this.zkClient);
    }
    
    private void monitorLockNodeAsync() {
        if (this.monitorLockNodePending && this.monitorLockNodeClient == this.zkClient) {
            ActiveStandbyElector.LOG.info("Ignore duplicate monitor lock-node request.");
            return;
        }
        this.monitorLockNodePending = true;
        this.monitorLockNodeClient = this.zkClient;
        this.zkClient.exists(this.zkLockFilePath, this.watcher, this, this.zkClient);
    }
    
    private String createWithRetries(final String path, final byte[] data, final List<ACL> acl, final CreateMode mode) throws InterruptedException, KeeperException {
        return this.zkDoWithRetries((ZKAction<String>)new ZKAction<String>() {
            @Override
            public String run() throws KeeperException, InterruptedException {
                return ActiveStandbyElector.this.zkClient.create(path, data, acl, mode);
            }
        });
    }
    
    private byte[] getDataWithRetries(final String path, final boolean watch, final Stat stat) throws InterruptedException, KeeperException {
        return this.zkDoWithRetries((ZKAction<byte[]>)new ZKAction<byte[]>() {
            @Override
            public byte[] run() throws KeeperException, InterruptedException {
                return ActiveStandbyElector.this.zkClient.getData(path, watch, stat);
            }
        });
    }
    
    private Stat setDataWithRetries(final String path, final byte[] data, final int version) throws InterruptedException, KeeperException {
        return this.zkDoWithRetries((ZKAction<Stat>)new ZKAction<Stat>() {
            @Override
            public Stat run() throws KeeperException, InterruptedException {
                return ActiveStandbyElector.this.zkClient.setData(path, data, version);
            }
        });
    }
    
    private void deleteWithRetries(final String path, final int version) throws KeeperException, InterruptedException {
        this.zkDoWithRetries((ZKAction<Object>)new ZKAction<Void>() {
            @Override
            public Void run() throws KeeperException, InterruptedException {
                ActiveStandbyElector.this.zkClient.delete(path, version);
                return null;
            }
        });
    }
    
    private void setAclsWithRetries(final String path) throws KeeperException, InterruptedException {
        final Stat stat = new Stat();
        this.zkDoWithRetries((ZKAction<Object>)new ZKAction<Void>() {
            @Override
            public Void run() throws KeeperException, InterruptedException {
                final List<ACL> acl = ActiveStandbyElector.this.zkClient.getACL(path, stat);
                if (acl == null || !acl.containsAll(ActiveStandbyElector.this.zkAcl) || !ActiveStandbyElector.this.zkAcl.containsAll(acl)) {
                    ActiveStandbyElector.this.zkClient.setACL(path, ActiveStandbyElector.this.zkAcl, stat.getAversion());
                }
                return null;
            }
        }, KeeperException.Code.BADVERSION);
    }
    
    private <T> T zkDoWithRetries(final ZKAction<T> action) throws KeeperException, InterruptedException {
        return this.zkDoWithRetries(action, null);
    }
    
    private <T> T zkDoWithRetries(final ZKAction<T> action, final KeeperException.Code retryCode) throws KeeperException, InterruptedException {
        int retry = 0;
        try {
            return action.run();
        }
        catch (KeeperException ke) {
            if ((shouldRetry(ke.code()) || shouldRetry(ke.code(), retryCode)) && ++retry < this.maxRetryNum) {
                return action.run();
            }
            throw ke;
        }
    }
    
    private synchronized boolean isStaleClient(final Object ctx) {
        Preconditions.checkNotNull(ctx);
        if (this.zkClient != ctx) {
            ActiveStandbyElector.LOG.warn("Ignoring stale result from old client with sessionId {}", String.format("0x%08x", ((ZooKeeper)ctx).getSessionId()));
            return true;
        }
        return false;
    }
    
    private static boolean isSuccess(final KeeperException.Code code) {
        return code == KeeperException.Code.OK;
    }
    
    private static boolean isNodeExists(final KeeperException.Code code) {
        return code == KeeperException.Code.NODEEXISTS;
    }
    
    private static boolean isNodeDoesNotExist(final KeeperException.Code code) {
        return code == KeeperException.Code.NONODE;
    }
    
    private static boolean isSessionExpired(final KeeperException.Code code) {
        return code == KeeperException.Code.SESSIONEXPIRED;
    }
    
    private static boolean shouldRetry(final KeeperException.Code code) {
        return code == KeeperException.Code.CONNECTIONLOSS || code == KeeperException.Code.OPERATIONTIMEOUT;
    }
    
    private static boolean shouldRetry(final KeeperException.Code code, final KeeperException.Code retryIfCode) {
        return retryIfCode != null && retryIfCode == code;
    }
    
    @Override
    public String toString() {
        return "elector id=" + System.identityHashCode(this) + " appData=" + ((this.appData == null) ? "null" : StringUtils.byteToHexString(this.appData)) + " cb=" + this.appClient;
    }
    
    public String getHAZookeeperConnectionState() {
        return this.zkConnectionState.name();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ActiveStandbyElector.class);
    }
    
    private enum ConnectionState
    {
        DISCONNECTED, 
        CONNECTED, 
        TERMINATED;
    }
    
    enum State
    {
        INIT, 
        ACTIVE, 
        STANDBY, 
        NEUTRAL;
    }
    
    public static class ActiveNotFoundException extends Exception
    {
        private static final long serialVersionUID = 3505396722342846462L;
    }
    
    private final class WatcherWithClientRef implements Watcher
    {
        private ZooKeeper zk;
        private CountDownLatch hasReceivedEvent;
        private CountDownLatch hasSetZooKeeper;
        
        private WatcherWithClientRef() {
            this.hasReceivedEvent = new CountDownLatch(1);
            this.hasSetZooKeeper = new CountDownLatch(1);
        }
        
        private void waitForZKConnectionEvent(final int connectionTimeoutMs) throws KeeperException, IOException {
            try {
                if (!this.hasReceivedEvent.await(connectionTimeoutMs, TimeUnit.MILLISECONDS)) {
                    ActiveStandbyElector.LOG.error("Connection timed out: couldn't connect to ZooKeeper in {} milliseconds", (Object)connectionTimeoutMs);
                    this.zk.close();
                    throw KeeperException.create(KeeperException.Code.CONNECTIONLOSS);
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted when connecting to zookeeper server", e);
            }
        }
        
        private void setZooKeeperRef(final ZooKeeper zk) {
            Preconditions.checkState(this.zk == null, (Object)"zk already set -- must be set exactly once");
            this.zk = zk;
            this.hasSetZooKeeper.countDown();
        }
        
        @Override
        public void process(final WatchedEvent event) {
            this.hasReceivedEvent.countDown();
            try {
                if (!this.hasSetZooKeeper.await(ActiveStandbyElector.this.zkSessionTimeout, TimeUnit.MILLISECONDS)) {
                    ActiveStandbyElector.LOG.debug("Event received with stale zk");
                }
                ActiveStandbyElector.this.processWatchEvent(this.zk, event);
            }
            catch (Throwable t) {
                ActiveStandbyElector.this.fatalError("Failed to process watcher event " + event + ": " + StringUtils.stringifyException(t));
            }
        }
    }
    
    private interface ZKAction<T>
    {
        T run() throws KeeperException, InterruptedException;
    }
    
    public interface ActiveStandbyElectorCallback
    {
        void becomeActive() throws ServiceFailedException;
        
        void becomeStandby();
        
        void enterNeutralMode();
        
        void notifyFatalError(final String p0);
        
        void fenceOldActive(final byte[] p0);
    }
}
