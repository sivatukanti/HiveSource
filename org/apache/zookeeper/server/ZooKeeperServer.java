// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.util.Collection;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.StatPersisted;
import org.apache.zookeeper.Environment;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.txn.CreateSessionTxn;
import org.apache.zookeeper.txn.TxnHeader;
import org.apache.zookeeper.proto.SetSASLResponse;
import javax.security.sasl.SaslException;
import org.apache.zookeeper.proto.GetSASLRequest;
import org.apache.zookeeper.server.auth.AuthenticationProvider;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.server.auth.ProviderRegistry;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.AuthPacket;
import org.apache.jute.InputArchive;
import org.apache.zookeeper.proto.ConnectRequest;
import java.io.InputStream;
import org.apache.jute.BinaryInputArchive;
import org.apache.zookeeper.proto.RequestHeader;
import org.apache.zookeeper.server.quorum.ReadOnlyZooKeeperServer;
import org.apache.jute.OutputArchive;
import java.io.OutputStream;
import org.apache.jute.BinaryOutputArchive;
import java.io.ByteArrayOutputStream;
import org.apache.zookeeper.proto.ConnectResponse;
import org.apache.zookeeper.KeeperException;
import java.util.Arrays;
import java.util.Random;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import org.apache.zookeeper.data.Id;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.slf4j.Logger;

public class ZooKeeperServer implements SessionTracker.SessionExpirer, ServerStats.Provider
{
    protected static final Logger LOG;
    protected ZooKeeperServerBean jmxServerBean;
    protected DataTreeBean jmxDataTreeBean;
    public static final int DEFAULT_TICK_TIME = 3000;
    protected int tickTime;
    protected int minSessionTimeout;
    protected int maxSessionTimeout;
    protected SessionTracker sessionTracker;
    private FileTxnSnapLog txnLogFactory;
    private ZKDatabase zkDb;
    private final AtomicLong hzxid;
    public static final Exception ok;
    protected RequestProcessor firstProcessor;
    protected volatile State state;
    private static final long superSecret = 3007405056L;
    private final AtomicInteger requestsInProcess;
    final List<ChangeRecord> outstandingChanges;
    final HashMap<String, ChangeRecord> outstandingChangesForPath;
    private ServerCnxnFactory serverCnxnFactory;
    private final ServerStats serverStats;
    private final ZooKeeperServerListener listener;
    private ZooKeeperServerShutdownHandler zkShutdownHandler;
    
    void removeCnxn(final ServerCnxn cnxn) {
        this.zkDb.removeCnxn(cnxn);
    }
    
    public ZooKeeperServer() {
        this.tickTime = 3000;
        this.minSessionTimeout = -1;
        this.maxSessionTimeout = -1;
        this.txnLogFactory = null;
        this.hzxid = new AtomicLong(0L);
        this.state = State.INITIAL;
        this.requestsInProcess = new AtomicInteger(0);
        this.outstandingChanges = new ArrayList<ChangeRecord>();
        this.outstandingChangesForPath = new HashMap<String, ChangeRecord>();
        this.serverStats = new ServerStats(this);
        this.listener = new ZooKeeperServerListenerImpl(this);
    }
    
    public ZooKeeperServer(final FileTxnSnapLog txnLogFactory, final int tickTime, final int minSessionTimeout, final int maxSessionTimeout, final DataTreeBuilder treeBuilder, final ZKDatabase zkDb) {
        this.tickTime = 3000;
        this.minSessionTimeout = -1;
        this.maxSessionTimeout = -1;
        this.txnLogFactory = null;
        this.hzxid = new AtomicLong(0L);
        this.state = State.INITIAL;
        this.requestsInProcess = new AtomicInteger(0);
        this.outstandingChanges = new ArrayList<ChangeRecord>();
        this.outstandingChangesForPath = new HashMap<String, ChangeRecord>();
        this.serverStats = new ServerStats(this);
        (this.txnLogFactory = txnLogFactory).setServerStats(this.serverStats);
        this.zkDb = zkDb;
        this.tickTime = tickTime;
        this.minSessionTimeout = minSessionTimeout;
        this.maxSessionTimeout = maxSessionTimeout;
        this.listener = new ZooKeeperServerListenerImpl(this);
        ZooKeeperServer.LOG.info("Created server with tickTime " + tickTime + " minSessionTimeout " + this.getMinSessionTimeout() + " maxSessionTimeout " + this.getMaxSessionTimeout() + " datadir " + txnLogFactory.getDataDir() + " snapdir " + txnLogFactory.getSnapDir());
    }
    
    public ZooKeeperServer(final FileTxnSnapLog txnLogFactory, final int tickTime, final DataTreeBuilder treeBuilder) throws IOException {
        this(txnLogFactory, tickTime, -1, -1, treeBuilder, new ZKDatabase(txnLogFactory));
    }
    
    public ServerStats serverStats() {
        return this.serverStats;
    }
    
    public void dumpConf(final PrintWriter pwriter) {
        pwriter.print("clientPort=");
        pwriter.println(this.getClientPort());
        pwriter.print("dataDir=");
        pwriter.println(this.zkDb.snapLog.getSnapDir().getAbsolutePath());
        pwriter.print("dataLogDir=");
        pwriter.println(this.zkDb.snapLog.getDataDir().getAbsolutePath());
        pwriter.print("tickTime=");
        pwriter.println(this.getTickTime());
        pwriter.print("maxClientCnxns=");
        pwriter.println(this.serverCnxnFactory.getMaxClientCnxnsPerHost());
        pwriter.print("minSessionTimeout=");
        pwriter.println(this.getMinSessionTimeout());
        pwriter.print("maxSessionTimeout=");
        pwriter.println(this.getMaxSessionTimeout());
        pwriter.print("serverId=");
        pwriter.println(this.getServerId());
    }
    
    public ZooKeeperServer(final File snapDir, final File logDir, final int tickTime) throws IOException {
        this(new FileTxnSnapLog(snapDir, logDir), tickTime, new BasicDataTreeBuilder());
    }
    
    public ZooKeeperServer(final FileTxnSnapLog txnLogFactory, final DataTreeBuilder treeBuilder) throws IOException {
        this(txnLogFactory, 3000, -1, -1, treeBuilder, new ZKDatabase(txnLogFactory));
    }
    
    public ZKDatabase getZKDatabase() {
        return this.zkDb;
    }
    
    public void setZKDatabase(final ZKDatabase zkDb) {
        this.zkDb = zkDb;
    }
    
    public void loadData() throws IOException, InterruptedException {
        if (this.zkDb.isInitialized()) {
            this.setZxid(this.zkDb.getDataTreeLastProcessedZxid());
        }
        else {
            this.setZxid(this.zkDb.loadDataBase());
        }
        final LinkedList<Long> deadSessions = new LinkedList<Long>();
        for (final Long session : this.zkDb.getSessions()) {
            if (this.zkDb.getSessionWithTimeOuts().get(session) == null) {
                deadSessions.add(session);
            }
        }
        this.zkDb.setDataTreeInit(true);
        for (final long session2 : deadSessions) {
            this.killSession(session2, this.zkDb.getDataTreeLastProcessedZxid());
        }
    }
    
    public void takeSnapshot() {
        try {
            this.txnLogFactory.save(this.zkDb.getDataTree(), this.zkDb.getSessionWithTimeOuts());
        }
        catch (IOException e) {
            ZooKeeperServer.LOG.error("Severe unrecoverable error, exiting", e);
            System.exit(10);
        }
    }
    
    public long getZxid() {
        return this.hzxid.get();
    }
    
    long getNextZxid() {
        return this.hzxid.incrementAndGet();
    }
    
    public void setZxid(final long zxid) {
        this.hzxid.set(zxid);
    }
    
    private void close(final long sessionId) {
        this.submitRequest(null, sessionId, -11, 0, null, null);
    }
    
    public void closeSession(final long sessionId) {
        ZooKeeperServer.LOG.info("Closing session 0x" + Long.toHexString(sessionId));
        this.close(sessionId);
    }
    
    protected void killSession(final long sessionId, final long zxid) {
        this.zkDb.killSession(sessionId, zxid);
        if (ZooKeeperServer.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(ZooKeeperServer.LOG, 32L, "ZooKeeperServer --- killSession: 0x" + Long.toHexString(sessionId));
        }
        if (this.sessionTracker != null) {
            this.sessionTracker.removeSession(sessionId);
        }
    }
    
    @Override
    public void expire(final SessionTracker.Session session) {
        final long sessionId = session.getSessionId();
        ZooKeeperServer.LOG.info("Expiring session 0x" + Long.toHexString(sessionId) + ", timeout of " + session.getTimeout() + "ms exceeded");
        this.close(sessionId);
    }
    
    void touch(final ServerCnxn cnxn) throws MissingSessionException {
        if (cnxn == null) {
            return;
        }
        final long id = cnxn.getSessionId();
        final int to = cnxn.getSessionTimeout();
        if (!this.sessionTracker.touchSession(id, to)) {
            throw new MissingSessionException("No session with sessionid 0x" + Long.toHexString(id) + " exists, probably expired and removed");
        }
    }
    
    protected void registerJMX() {
        try {
            this.jmxServerBean = new ZooKeeperServerBean(this);
            MBeanRegistry.getInstance().register(this.jmxServerBean, null);
            try {
                this.jmxDataTreeBean = new DataTreeBean(this.zkDb.getDataTree());
                MBeanRegistry.getInstance().register(this.jmxDataTreeBean, this.jmxServerBean);
            }
            catch (Exception e) {
                ZooKeeperServer.LOG.warn("Failed to register with JMX", e);
                this.jmxDataTreeBean = null;
            }
        }
        catch (Exception e) {
            ZooKeeperServer.LOG.warn("Failed to register with JMX", e);
            this.jmxServerBean = null;
        }
    }
    
    public void startdata() throws IOException, InterruptedException {
        if (this.zkDb == null) {
            this.zkDb = new ZKDatabase(this.txnLogFactory);
        }
        if (!this.zkDb.isInitialized()) {
            this.loadData();
        }
    }
    
    public synchronized void startup() {
        if (this.sessionTracker == null) {
            this.createSessionTracker();
        }
        this.startSessionTracker();
        this.setupRequestProcessors();
        this.registerJMX();
        this.setState(State.RUNNING);
        this.notifyAll();
    }
    
    protected void setupRequestProcessors() {
        final RequestProcessor finalProcessor = new FinalRequestProcessor(this);
        final RequestProcessor syncProcessor = new SyncRequestProcessor(this, finalProcessor);
        ((SyncRequestProcessor)syncProcessor).start();
        this.firstProcessor = new PrepRequestProcessor(this, syncProcessor);
        ((PrepRequestProcessor)this.firstProcessor).start();
    }
    
    public ZooKeeperServerListener getZooKeeperServerListener() {
        return this.listener;
    }
    
    protected void createSessionTracker() {
        this.sessionTracker = new SessionTrackerImpl(this, this.zkDb.getSessionWithTimeOuts(), this.tickTime, 1L, this.getZooKeeperServerListener());
    }
    
    protected void startSessionTracker() {
        ((SessionTrackerImpl)this.sessionTracker).start();
    }
    
    protected void setState(final State state) {
        this.state = state;
        if (this.zkShutdownHandler != null) {
            this.zkShutdownHandler.handle(state);
        }
        else {
            ZooKeeperServer.LOG.debug("ZKShutdownHandler is not registered, so ZooKeeper server won't take any action on ERROR or SHUTDOWN server state changes");
        }
    }
    
    protected boolean canShutdown() {
        return this.state == State.RUNNING || this.state == State.ERROR;
    }
    
    public boolean isRunning() {
        return this.state == State.RUNNING;
    }
    
    public void shutdown() {
        this.shutdown(false);
    }
    
    public synchronized void shutdown(final boolean fullyShutDown) {
        if (!this.canShutdown()) {
            ZooKeeperServer.LOG.debug("ZooKeeper server is not running, so not proceeding to shutdown!");
            return;
        }
        ZooKeeperServer.LOG.info("shutting down");
        this.setState(State.SHUTDOWN);
        if (this.sessionTracker != null) {
            this.sessionTracker.shutdown();
        }
        if (this.firstProcessor != null) {
            this.firstProcessor.shutdown();
        }
        if (this.zkDb != null) {
            if (fullyShutDown) {
                this.zkDb.clear();
            }
            else {
                try {
                    this.zkDb.fastForwardDataBase();
                }
                catch (IOException e) {
                    ZooKeeperServer.LOG.error("Error updating DB", e);
                    this.zkDb.clear();
                }
            }
        }
        this.unregisterJMX();
    }
    
    protected void unregisterJMX() {
        try {
            if (this.jmxDataTreeBean != null) {
                MBeanRegistry.getInstance().unregister(this.jmxDataTreeBean);
            }
        }
        catch (Exception e) {
            ZooKeeperServer.LOG.warn("Failed to unregister with JMX", e);
        }
        try {
            if (this.jmxServerBean != null) {
                MBeanRegistry.getInstance().unregister(this.jmxServerBean);
            }
        }
        catch (Exception e) {
            ZooKeeperServer.LOG.warn("Failed to unregister with JMX", e);
        }
        this.jmxServerBean = null;
        this.jmxDataTreeBean = null;
    }
    
    public void incInProcess() {
        this.requestsInProcess.incrementAndGet();
    }
    
    public void decInProcess() {
        this.requestsInProcess.decrementAndGet();
    }
    
    public int getInProcess() {
        return this.requestsInProcess.get();
    }
    
    byte[] generatePasswd(final long id) {
        final Random r = new Random(id ^ 0xB3415C00L);
        final byte[] p = new byte[16];
        r.nextBytes(p);
        return p;
    }
    
    protected boolean checkPasswd(final long sessionId, final byte[] passwd) {
        return sessionId != 0L && Arrays.equals(passwd, this.generatePasswd(sessionId));
    }
    
    long createSession(final ServerCnxn cnxn, final byte[] passwd, final int timeout) {
        final long sessionId = this.sessionTracker.createSession(timeout);
        final Random r = new Random(sessionId ^ 0xB3415C00L);
        r.nextBytes(passwd);
        final ByteBuffer to = ByteBuffer.allocate(4);
        to.putInt(timeout);
        cnxn.setSessionId(sessionId);
        this.submitRequest(cnxn, sessionId, -10, 0, to, null);
        return sessionId;
    }
    
    public void setOwner(final long id, final Object owner) throws KeeperException.SessionExpiredException {
        this.sessionTracker.setOwner(id, owner);
    }
    
    protected void revalidateSession(final ServerCnxn cnxn, final long sessionId, final int sessionTimeout) throws IOException {
        final boolean rc = this.sessionTracker.touchSession(sessionId, sessionTimeout);
        if (ZooKeeperServer.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(ZooKeeperServer.LOG, 32L, "Session 0x" + Long.toHexString(sessionId) + " is valid: " + rc);
        }
        this.finishSessionInit(cnxn, rc);
    }
    
    public void reopenSession(final ServerCnxn cnxn, final long sessionId, final byte[] passwd, final int sessionTimeout) throws IOException {
        if (!this.checkPasswd(sessionId, passwd)) {
            this.finishSessionInit(cnxn, false);
        }
        else {
            this.revalidateSession(cnxn, sessionId, sessionTimeout);
        }
    }
    
    public void finishSessionInit(final ServerCnxn cnxn, final boolean valid) {
        try {
            if (valid) {
                this.serverCnxnFactory.registerConnection(cnxn);
            }
        }
        catch (Exception e) {
            ZooKeeperServer.LOG.warn("Failed to register with JMX", e);
        }
        try {
            final ConnectResponse rsp = new ConnectResponse(0, valid ? cnxn.getSessionTimeout() : 0, valid ? cnxn.getSessionId() : 0L, valid ? this.generatePasswd(cnxn.getSessionId()) : new byte[16]);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final BinaryOutputArchive bos = BinaryOutputArchive.getArchive(baos);
            bos.writeInt(-1, "len");
            rsp.serialize(bos, "connect");
            if (!cnxn.isOldClient) {
                bos.writeBool(this instanceof ReadOnlyZooKeeperServer, "readOnly");
            }
            baos.close();
            final ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
            bb.putInt(bb.remaining() - 4).rewind();
            cnxn.sendBuffer(bb);
            if (!valid) {
                ZooKeeperServer.LOG.info("Invalid session 0x" + Long.toHexString(cnxn.getSessionId()) + " for client " + cnxn.getRemoteSocketAddress() + ", probably expired");
                cnxn.sendBuffer(ServerCnxnFactory.closeConn);
            }
            else {
                ZooKeeperServer.LOG.info("Established session 0x" + Long.toHexString(cnxn.getSessionId()) + " with negotiated timeout " + cnxn.getSessionTimeout() + " for client " + cnxn.getRemoteSocketAddress());
                cnxn.enableRecv();
            }
        }
        catch (Exception e) {
            ZooKeeperServer.LOG.warn("Exception while establishing session, closing", e);
            cnxn.close();
        }
    }
    
    public void closeSession(final ServerCnxn cnxn, final RequestHeader requestHeader) {
        this.closeSession(cnxn.getSessionId());
    }
    
    @Override
    public long getServerId() {
        return 0L;
    }
    
    private void submitRequest(final ServerCnxn cnxn, final long sessionId, final int type, final int xid, final ByteBuffer bb, final List<Id> authInfo) {
        final Request si = new Request(cnxn, sessionId, xid, type, bb, authInfo);
        this.submitRequest(si);
    }
    
    public void submitRequest(final Request si) {
        if (this.firstProcessor == null) {
            synchronized (this) {
                try {
                    while (this.state == State.INITIAL) {
                        this.wait(1000L);
                    }
                }
                catch (InterruptedException e) {
                    ZooKeeperServer.LOG.warn("Unexpected interruption", e);
                }
                if (this.firstProcessor == null || this.state != State.RUNNING) {
                    throw new RuntimeException("Not started");
                }
            }
        }
        try {
            this.touch(si.cnxn);
            final boolean validpacket = Request.isValid(si.type);
            if (validpacket) {
                this.firstProcessor.processRequest(si);
                if (si.cnxn != null) {
                    this.incInProcess();
                }
            }
            else {
                ZooKeeperServer.LOG.warn("Received packet at server of unknown type " + si.type);
                new UnimplementedRequestProcessor().processRequest(si);
            }
        }
        catch (MissingSessionException e2) {
            if (ZooKeeperServer.LOG.isDebugEnabled()) {
                ZooKeeperServer.LOG.debug("Dropping request: " + e2.getMessage());
            }
        }
        catch (RequestProcessor.RequestProcessorException e3) {
            ZooKeeperServer.LOG.error("Unable to process request:" + e3.getMessage(), e3);
        }
    }
    
    public static int getSnapCount() {
        final String sc = System.getProperty("zookeeper.snapCount");
        try {
            int snapCount = Integer.parseInt(sc);
            if (snapCount < 2) {
                ZooKeeperServer.LOG.warn("SnapCount should be 2 or more. Now, snapCount is reset to 2");
                snapCount = 2;
            }
            return snapCount;
        }
        catch (Exception e) {
            return 100000;
        }
    }
    
    public int getGlobalOutstandingLimit() {
        final String sc = System.getProperty("zookeeper.globalOutstandingLimit");
        int limit;
        try {
            limit = Integer.parseInt(sc);
        }
        catch (Exception e) {
            limit = 1000;
        }
        return limit;
    }
    
    public void setServerCnxnFactory(final ServerCnxnFactory factory) {
        this.serverCnxnFactory = factory;
    }
    
    public ServerCnxnFactory getServerCnxnFactory() {
        return this.serverCnxnFactory;
    }
    
    @Override
    public long getLastProcessedZxid() {
        return this.zkDb.getDataTreeLastProcessedZxid();
    }
    
    @Override
    public long getOutstandingRequests() {
        return this.getInProcess();
    }
    
    public void truncateLog(final long zxid) throws IOException {
        this.zkDb.truncateLog(zxid);
    }
    
    public int getTickTime() {
        return this.tickTime;
    }
    
    public void setTickTime(final int tickTime) {
        ZooKeeperServer.LOG.info("tickTime set to " + tickTime);
        this.tickTime = tickTime;
    }
    
    public int getMinSessionTimeout() {
        return (this.minSessionTimeout == -1) ? (this.tickTime * 2) : this.minSessionTimeout;
    }
    
    public void setMinSessionTimeout(final int min) {
        ZooKeeperServer.LOG.info("minSessionTimeout set to " + min);
        this.minSessionTimeout = min;
    }
    
    public int getMaxSessionTimeout() {
        return (this.maxSessionTimeout == -1) ? (this.tickTime * 20) : this.maxSessionTimeout;
    }
    
    public void setMaxSessionTimeout(final int max) {
        ZooKeeperServer.LOG.info("maxSessionTimeout set to " + max);
        this.maxSessionTimeout = max;
    }
    
    public int getClientPort() {
        return (this.serverCnxnFactory != null) ? this.serverCnxnFactory.getLocalPort() : -1;
    }
    
    public void setTxnLogFactory(final FileTxnSnapLog txnLog) {
        this.txnLogFactory = txnLog;
    }
    
    public FileTxnSnapLog getTxnLogFactory() {
        return this.txnLogFactory;
    }
    
    @Override
    public String getState() {
        return "standalone";
    }
    
    public void dumpEphemerals(final PrintWriter pwriter) {
        this.zkDb.dumpEphemerals(pwriter);
    }
    
    @Override
    public int getNumAliveConnections() {
        return this.serverCnxnFactory.getNumAliveConnections();
    }
    
    public void processConnectRequest(final ServerCnxn cnxn, final ByteBuffer incomingBuffer) throws IOException {
        final BinaryInputArchive bia = BinaryInputArchive.getArchive(new ByteBufferInputStream(incomingBuffer));
        final ConnectRequest connReq = new ConnectRequest();
        connReq.deserialize(bia, "connect");
        if (ZooKeeperServer.LOG.isDebugEnabled()) {
            ZooKeeperServer.LOG.debug("Session establishment request from client " + cnxn.getRemoteSocketAddress() + " client's lastZxid is 0x" + Long.toHexString(connReq.getLastZxidSeen()));
        }
        boolean readOnly = false;
        try {
            readOnly = bia.readBool("readOnly");
            cnxn.isOldClient = false;
        }
        catch (IOException e) {
            ZooKeeperServer.LOG.warn("Connection request from old client " + cnxn.getRemoteSocketAddress() + "; will be dropped if server is in r-o mode");
        }
        if (!readOnly && this instanceof ReadOnlyZooKeeperServer) {
            final String msg = "Refusing session request for not-read-only client " + cnxn.getRemoteSocketAddress();
            ZooKeeperServer.LOG.info(msg);
            throw new ServerCnxn.CloseRequestException(msg);
        }
        if (connReq.getLastZxidSeen() > this.zkDb.dataTree.lastProcessedZxid) {
            final String msg = "Refusing session request for client " + cnxn.getRemoteSocketAddress() + " as it has seen zxid 0x" + Long.toHexString(connReq.getLastZxidSeen()) + " our last zxid is 0x" + Long.toHexString(this.getZKDatabase().getDataTreeLastProcessedZxid()) + " client must try another server";
            ZooKeeperServer.LOG.info(msg);
            throw new ServerCnxn.CloseRequestException(msg);
        }
        int sessionTimeout = connReq.getTimeOut();
        final byte[] passwd = connReq.getPasswd();
        final int minSessionTimeout = this.getMinSessionTimeout();
        if (sessionTimeout < minSessionTimeout) {
            sessionTimeout = minSessionTimeout;
        }
        final int maxSessionTimeout = this.getMaxSessionTimeout();
        if (sessionTimeout > maxSessionTimeout) {
            sessionTimeout = maxSessionTimeout;
        }
        cnxn.setSessionTimeout(sessionTimeout);
        cnxn.disableRecv();
        final long sessionId = connReq.getSessionId();
        if (sessionId != 0L) {
            final long clientSessionId = connReq.getSessionId();
            ZooKeeperServer.LOG.info("Client attempting to renew session 0x" + Long.toHexString(clientSessionId) + " at " + cnxn.getRemoteSocketAddress());
            this.serverCnxnFactory.closeSession(sessionId);
            cnxn.setSessionId(sessionId);
            this.reopenSession(cnxn, sessionId, passwd, sessionTimeout);
        }
        else {
            ZooKeeperServer.LOG.info("Client attempting to establish new session at " + cnxn.getRemoteSocketAddress());
            this.createSession(cnxn, passwd, sessionTimeout);
        }
    }
    
    public boolean shouldThrottle(final long outStandingCount) {
        return this.getGlobalOutstandingLimit() < this.getInProcess() && outStandingCount > 0L;
    }
    
    public void processPacket(final ServerCnxn cnxn, ByteBuffer incomingBuffer) throws IOException {
        final InputStream bais = new ByteBufferInputStream(incomingBuffer);
        final BinaryInputArchive bia = BinaryInputArchive.getArchive(bais);
        final RequestHeader h = new RequestHeader();
        h.deserialize(bia, "header");
        incomingBuffer = incomingBuffer.slice();
        if (h.getType() == 100) {
            ZooKeeperServer.LOG.info("got auth packet " + cnxn.getRemoteSocketAddress());
            final AuthPacket authPacket = new AuthPacket();
            ByteBufferInputStream.byteBuffer2Record(incomingBuffer, authPacket);
            final String scheme = authPacket.getScheme();
            final AuthenticationProvider ap = ProviderRegistry.getProvider(scheme);
            KeeperException.Code authReturn = KeeperException.Code.AUTHFAILED;
            if (ap != null) {
                try {
                    authReturn = ap.handleAuthentication(cnxn, authPacket.getAuth());
                }
                catch (RuntimeException e) {
                    ZooKeeperServer.LOG.warn("Caught runtime exception from AuthenticationProvider: " + scheme + " due to " + e);
                    authReturn = KeeperException.Code.AUTHFAILED;
                }
            }
            if (authReturn != KeeperException.Code.OK) {
                if (ap == null) {
                    ZooKeeperServer.LOG.warn("No authentication provider for scheme: " + scheme + " has " + ProviderRegistry.listProviders());
                }
                else {
                    ZooKeeperServer.LOG.warn("Authentication failed for scheme: " + scheme);
                }
                final ReplyHeader rh = new ReplyHeader(h.getXid(), 0L, KeeperException.Code.AUTHFAILED.intValue());
                cnxn.sendResponse(rh, null, null);
                cnxn.sendBuffer(ServerCnxnFactory.closeConn);
                cnxn.disableRecv();
            }
            else {
                if (ZooKeeperServer.LOG.isDebugEnabled()) {
                    ZooKeeperServer.LOG.debug("Authentication succeeded for scheme: " + scheme);
                }
                ZooKeeperServer.LOG.info("auth success " + cnxn.getRemoteSocketAddress());
                final ReplyHeader rh = new ReplyHeader(h.getXid(), 0L, KeeperException.Code.OK.intValue());
                cnxn.sendResponse(rh, null, null);
            }
            return;
        }
        if (h.getType() == 102) {
            final Record rsp = this.processSasl(incomingBuffer, cnxn);
            final ReplyHeader rh2 = new ReplyHeader(h.getXid(), 0L, KeeperException.Code.OK.intValue());
            cnxn.sendResponse(rh2, rsp, "response");
            return;
        }
        final Request si = new Request(cnxn, cnxn.getSessionId(), h.getXid(), h.getType(), incomingBuffer, cnxn.getAuthInfo());
        si.setOwner(ServerCnxn.me);
        this.submitRequest(si);
        cnxn.incrOutstandingRequests(h);
    }
    
    private Record processSasl(final ByteBuffer incomingBuffer, final ServerCnxn cnxn) throws IOException {
        ZooKeeperServer.LOG.debug("Responding to client SASL token.");
        final GetSASLRequest clientTokenRecord = new GetSASLRequest();
        ByteBufferInputStream.byteBuffer2Record(incomingBuffer, clientTokenRecord);
        final byte[] clientToken = clientTokenRecord.getToken();
        ZooKeeperServer.LOG.debug("Size of client SASL token: " + clientToken.length);
        byte[] responseToken = null;
        try {
            final ZooKeeperSaslServer saslServer = cnxn.zooKeeperSaslServer;
            try {
                responseToken = saslServer.evaluateResponse(clientToken);
                if (saslServer.isComplete()) {
                    final String authorizationID = saslServer.getAuthorizationID();
                    ZooKeeperServer.LOG.info("adding SASL authorization for authorizationID: " + authorizationID);
                    cnxn.addAuthInfo(new Id("sasl", authorizationID));
                }
            }
            catch (SaslException e) {
                ZooKeeperServer.LOG.warn("Client failed to SASL authenticate: " + e, e);
                if (System.getProperty("zookeeper.allowSaslFailedClients") != null && System.getProperty("zookeeper.allowSaslFailedClients").equals("true")) {
                    ZooKeeperServer.LOG.warn("Maintaining client connection despite SASL authentication failure.");
                }
                else {
                    ZooKeeperServer.LOG.warn("Closing client connection due to SASL authentication failure.");
                    cnxn.close();
                }
            }
        }
        catch (NullPointerException e2) {
            ZooKeeperServer.LOG.error("cnxn.saslServer is null: cnxn object did not initialize its saslServer properly.");
        }
        if (responseToken != null) {
            ZooKeeperServer.LOG.debug("Size of server SASL response: " + responseToken.length);
        }
        return new SetSASLResponse(responseToken);
    }
    
    public DataTree.ProcessTxnResult processTxn(final TxnHeader hdr, final Record txn) {
        final int opCode = hdr.getType();
        final long sessionId = hdr.getClientId();
        final DataTree.ProcessTxnResult rc = this.getZKDatabase().processTxn(hdr, txn);
        if (opCode == -10) {
            if (txn instanceof CreateSessionTxn) {
                final CreateSessionTxn cst = (CreateSessionTxn)txn;
                this.sessionTracker.addSession(sessionId, cst.getTimeOut());
            }
            else {
                ZooKeeperServer.LOG.warn("*****>>>>> Got " + txn.getClass() + " " + txn.toString());
            }
        }
        else if (opCode == -11) {
            this.sessionTracker.removeSession(sessionId);
        }
        return rc;
    }
    
    void registerServerShutdownHandler(final ZooKeeperServerShutdownHandler zkShutdownHandler) {
        this.zkShutdownHandler = zkShutdownHandler;
    }
    
    static {
        Environment.logEnv("Server environment:", LOG = LoggerFactory.getLogger(ZooKeeperServer.class));
        ok = new Exception("No prob");
    }
    
    public static class BasicDataTreeBuilder implements DataTreeBuilder
    {
        @Override
        public DataTree build() {
            return new DataTree();
        }
    }
    
    protected enum State
    {
        INITIAL, 
        RUNNING, 
        SHUTDOWN, 
        ERROR;
    }
    
    public static class MissingSessionException extends IOException
    {
        private static final long serialVersionUID = 7467414635467261007L;
        
        public MissingSessionException(final String msg) {
            super(msg);
        }
    }
    
    static class ChangeRecord
    {
        long zxid;
        String path;
        StatPersisted stat;
        int childCount;
        List<ACL> acl;
        
        ChangeRecord(final long zxid, final String path, final StatPersisted stat, final int childCount, final List<ACL> acl) {
            this.zxid = zxid;
            this.path = path;
            this.stat = stat;
            this.childCount = childCount;
            this.acl = acl;
        }
        
        ChangeRecord duplicate(final long zxid) {
            final StatPersisted stat = new StatPersisted();
            if (this.stat != null) {
                DataTree.copyStatPersisted(this.stat, stat);
            }
            return new ChangeRecord(zxid, this.path, stat, this.childCount, (this.acl == null) ? new ArrayList<ACL>() : new ArrayList<ACL>(this.acl));
        }
    }
    
    public interface DataTreeBuilder
    {
        DataTree build();
    }
}
