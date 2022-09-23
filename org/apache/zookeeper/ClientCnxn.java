// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import java.net.UnknownHostException;
import java.net.ConnectException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.zookeeper.server.ZooTrace;
import java.net.SocketException;
import javax.security.sasl.SaslException;
import org.apache.zookeeper.common.Time;
import javax.security.auth.login.LoginException;
import org.apache.zookeeper.proto.SetWatches;
import java.util.ArrayList;
import org.apache.zookeeper.proto.GetSASLRequest;
import org.apache.zookeeper.proto.WatcherEvent;
import org.apache.jute.InputArchive;
import java.io.InputStream;
import org.apache.jute.BinaryInputArchive;
import org.apache.zookeeper.server.ByteBufferInputStream;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Iterator;
import org.apache.zookeeper.proto.CreateResponse;
import org.apache.zookeeper.proto.GetChildren2Response;
import org.apache.zookeeper.proto.GetChildrenResponse;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.zookeeper.proto.GetACLResponse;
import org.apache.zookeeper.proto.GetDataResponse;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.SetACLResponse;
import org.apache.zookeeper.proto.SetDataResponse;
import org.apache.zookeeper.proto.ExistsResponse;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.zookeeper.server.ZooKeeperThread;
import java.util.Set;
import org.apache.zookeeper.proto.ConnectRequest;
import org.apache.jute.OutputArchive;
import java.io.OutputStream;
import org.apache.jute.BinaryOutputArchive;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.proto.AuthPacket;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.RequestHeader;
import java.io.IOException;
import java.net.SocketAddress;
import org.apache.zookeeper.client.ZooKeeperSaslClient;
import org.apache.zookeeper.client.HostProvider;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;

public class ClientCnxn
{
    private static final Logger LOG;
    private static final String ZK_SASL_CLIENT_USERNAME = "zookeeper.sasl.client.username";
    private static final int SET_WATCHES_MAX_LENGTH = 131072;
    private static boolean disableAutoWatchReset;
    private final CopyOnWriteArraySet<AuthData> authInfo;
    private final LinkedList<Packet> pendingQueue;
    private final LinkedList<Packet> outgoingQueue;
    private int connectTimeout;
    private volatile int negotiatedSessionTimeout;
    private int readTimeout;
    private final int sessionTimeout;
    private final ZooKeeper zooKeeper;
    private final ClientWatchManager watcher;
    private long sessionId;
    private byte[] sessionPasswd;
    private boolean readOnly;
    final String chrootPath;
    final SendThread sendThread;
    final EventThread eventThread;
    private volatile boolean closing;
    private final HostProvider hostProvider;
    volatile boolean seenRwServerBefore;
    public ZooKeeperSaslClient zooKeeperSaslClient;
    private Object eventOfDeath;
    private volatile long lastZxid;
    public static final int packetLen;
    private int xid;
    volatile ZooKeeper.States state;
    
    public long getSessionId() {
        return this.sessionId;
    }
    
    public byte[] getSessionPasswd() {
        return this.sessionPasswd;
    }
    
    public int getSessionTimeout() {
        return this.negotiatedSessionTimeout;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final SocketAddress local = this.sendThread.getClientCnxnSocket().getLocalSocketAddress();
        final SocketAddress remote = this.sendThread.getClientCnxnSocket().getRemoteSocketAddress();
        sb.append("sessionid:0x").append(Long.toHexString(this.getSessionId())).append(" local:").append(local).append(" remoteserver:").append(remote).append(" lastZxid:").append(this.lastZxid).append(" xid:").append(this.xid).append(" sent:").append(this.sendThread.getClientCnxnSocket().getSentCount()).append(" recv:").append(this.sendThread.getClientCnxnSocket().getRecvCount()).append(" queuedpkts:").append(this.outgoingQueue.size()).append(" pendingresp:").append(this.pendingQueue.size()).append(" queuedevents:").append(this.eventThread.waitingEvents.size());
        return sb.toString();
    }
    
    public ClientCnxn(final String chrootPath, final HostProvider hostProvider, final int sessionTimeout, final ZooKeeper zooKeeper, final ClientWatchManager watcher, final ClientCnxnSocket clientCnxnSocket, final boolean canBeReadOnly) throws IOException {
        this(chrootPath, hostProvider, sessionTimeout, zooKeeper, watcher, clientCnxnSocket, 0L, new byte[16], canBeReadOnly);
    }
    
    public ClientCnxn(final String chrootPath, final HostProvider hostProvider, final int sessionTimeout, final ZooKeeper zooKeeper, final ClientWatchManager watcher, final ClientCnxnSocket clientCnxnSocket, final long sessionId, final byte[] sessionPasswd, final boolean canBeReadOnly) {
        this.authInfo = new CopyOnWriteArraySet<AuthData>();
        this.pendingQueue = new LinkedList<Packet>();
        this.outgoingQueue = new LinkedList<Packet>();
        this.sessionPasswd = new byte[16];
        this.closing = false;
        this.seenRwServerBefore = false;
        this.eventOfDeath = new Object();
        this.xid = 1;
        this.state = ZooKeeper.States.NOT_CONNECTED;
        this.zooKeeper = zooKeeper;
        this.watcher = watcher;
        this.sessionId = sessionId;
        this.sessionPasswd = sessionPasswd;
        this.sessionTimeout = sessionTimeout;
        this.hostProvider = hostProvider;
        this.chrootPath = chrootPath;
        this.connectTimeout = sessionTimeout / hostProvider.size();
        this.readTimeout = sessionTimeout * 2 / 3;
        this.readOnly = canBeReadOnly;
        this.sendThread = new SendThread(clientCnxnSocket);
        this.eventThread = new EventThread();
    }
    
    public static boolean getDisableAutoResetWatch() {
        return ClientCnxn.disableAutoWatchReset;
    }
    
    public static void setDisableAutoResetWatch(final boolean b) {
        ClientCnxn.disableAutoWatchReset = b;
    }
    
    public void start() {
        this.sendThread.start();
        this.eventThread.start();
    }
    
    private static String makeThreadName(final String suffix) {
        final String name = Thread.currentThread().getName().replaceAll("-EventThread", "");
        return name + suffix;
    }
    
    private void finishPacket(final Packet p) {
        if (p.watchRegistration != null) {
            p.watchRegistration.register(p.replyHeader.getErr());
        }
        if (p.cb == null) {
            synchronized (p) {
                p.finished = true;
                p.notifyAll();
            }
        }
        else {
            p.finished = true;
            this.eventThread.queuePacket(p);
        }
    }
    
    private void conLossPacket(final Packet p) {
        if (p.replyHeader == null) {
            return;
        }
        switch (this.state) {
            case AUTH_FAILED: {
                p.replyHeader.setErr(KeeperException.Code.AUTHFAILED.intValue());
                break;
            }
            case CLOSED: {
                p.replyHeader.setErr(KeeperException.Code.SESSIONEXPIRED.intValue());
                break;
            }
            default: {
                p.replyHeader.setErr(KeeperException.Code.CONNECTIONLOSS.intValue());
                break;
            }
        }
        this.finishPacket(p);
    }
    
    public long getLastZxid() {
        return this.lastZxid;
    }
    
    public void disconnect() {
        if (ClientCnxn.LOG.isDebugEnabled()) {
            ClientCnxn.LOG.debug("Disconnecting client for session: 0x" + Long.toHexString(this.getSessionId()));
        }
        this.sendThread.close();
        this.eventThread.queueEventOfDeath();
    }
    
    public void close() throws IOException {
        if (ClientCnxn.LOG.isDebugEnabled()) {
            ClientCnxn.LOG.debug("Closing client for session: 0x" + Long.toHexString(this.getSessionId()));
        }
        try {
            final RequestHeader h = new RequestHeader();
            h.setType(-11);
            this.submitRequest(h, null, null, null);
        }
        catch (InterruptedException ex) {}
        finally {
            this.disconnect();
        }
    }
    
    public synchronized int getXid() {
        return this.xid++;
    }
    
    public ReplyHeader submitRequest(final RequestHeader h, final Record request, final Record response, final ZooKeeper.WatchRegistration watchRegistration) throws InterruptedException {
        final ReplyHeader r = new ReplyHeader();
        final Packet packet = this.queuePacket(h, r, request, response, null, null, null, null, watchRegistration);
        synchronized (packet) {
            while (!packet.finished) {
                packet.wait();
            }
        }
        return r;
    }
    
    public void enableWrite() {
        this.sendThread.getClientCnxnSocket().enableWrite();
    }
    
    public void sendPacket(final Record request, final Record response, final AsyncCallback cb, final int opCode) throws IOException {
        final int xid = this.getXid();
        final RequestHeader h = new RequestHeader();
        h.setXid(xid);
        h.setType(opCode);
        final ReplyHeader r = new ReplyHeader();
        r.setXid(xid);
        final Packet p = new Packet(h, r, request, response, null, false);
        p.cb = cb;
        this.sendThread.sendPacket(p);
    }
    
    Packet queuePacket(final RequestHeader h, final ReplyHeader r, final Record request, final Record response, final AsyncCallback cb, final String clientPath, final String serverPath, final Object ctx, final ZooKeeper.WatchRegistration watchRegistration) {
        Packet packet = null;
        synchronized (this.outgoingQueue) {
            packet = new Packet(h, r, request, response, watchRegistration);
            packet.cb = cb;
            packet.ctx = ctx;
            packet.clientPath = clientPath;
            packet.serverPath = serverPath;
            if (!this.state.isAlive() || this.closing) {
                this.conLossPacket(packet);
            }
            else {
                if (h.getType() == -11) {
                    this.closing = true;
                }
                this.outgoingQueue.add(packet);
            }
        }
        this.sendThread.getClientCnxnSocket().wakeupCnxn();
        return packet;
    }
    
    public void addAuthInfo(final String scheme, final byte[] auth) {
        if (!this.state.isAlive()) {
            return;
        }
        this.authInfo.add(new AuthData(scheme, auth));
        this.queuePacket(new RequestHeader(-4, 100), null, new AuthPacket(0, scheme, auth), null, null, null, null, null, null);
    }
    
    ZooKeeper.States getState() {
        return this.state;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ClientCnxn.class);
        ClientCnxn.disableAutoWatchReset = Boolean.getBoolean("zookeeper.disableAutoWatchReset");
        if (ClientCnxn.LOG.isDebugEnabled()) {
            ClientCnxn.LOG.debug("zookeeper.disableAutoWatchReset is " + ClientCnxn.disableAutoWatchReset);
        }
        packetLen = Integer.getInteger("jute.maxbuffer", 4194304);
    }
    
    static class AuthData
    {
        String scheme;
        byte[] data;
        
        AuthData(final String scheme, final byte[] data) {
            this.scheme = scheme;
            this.data = data;
        }
    }
    
    static class Packet
    {
        RequestHeader requestHeader;
        ReplyHeader replyHeader;
        Record request;
        Record response;
        ByteBuffer bb;
        String clientPath;
        String serverPath;
        boolean finished;
        AsyncCallback cb;
        Object ctx;
        ZooKeeper.WatchRegistration watchRegistration;
        public boolean readOnly;
        
        Packet(final RequestHeader requestHeader, final ReplyHeader replyHeader, final Record request, final Record response, final ZooKeeper.WatchRegistration watchRegistration) {
            this(requestHeader, replyHeader, request, response, watchRegistration, false);
        }
        
        Packet(final RequestHeader requestHeader, final ReplyHeader replyHeader, final Record request, final Record response, final ZooKeeper.WatchRegistration watchRegistration, final boolean readOnly) {
            this.requestHeader = requestHeader;
            this.replyHeader = replyHeader;
            this.request = request;
            this.response = response;
            this.readOnly = readOnly;
            this.watchRegistration = watchRegistration;
        }
        
        public void createBB() {
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
                boa.writeInt(-1, "len");
                if (this.requestHeader != null) {
                    this.requestHeader.serialize(boa, "header");
                }
                if (this.request instanceof ConnectRequest) {
                    this.request.serialize(boa, "connect");
                    boa.writeBool(this.readOnly, "readOnly");
                }
                else if (this.request != null) {
                    this.request.serialize(boa, "request");
                }
                baos.close();
                (this.bb = ByteBuffer.wrap(baos.toByteArray())).putInt(this.bb.capacity() - 4);
                this.bb.rewind();
            }
            catch (IOException e) {
                ClientCnxn.LOG.warn("Ignoring unexpected exception", e);
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("clientPath:" + this.clientPath);
            sb.append(" serverPath:" + this.serverPath);
            sb.append(" finished:" + this.finished);
            sb.append(" header:: " + this.requestHeader);
            sb.append(" replyHeader:: " + this.replyHeader);
            sb.append(" request:: " + this.request);
            sb.append(" response:: " + this.response);
            return sb.toString().replaceAll("\r*\n+", " ");
        }
    }
    
    private static class WatcherSetEventPair
    {
        private final Set<Watcher> watchers;
        private final WatchedEvent event;
        
        public WatcherSetEventPair(final Set<Watcher> watchers, final WatchedEvent event) {
            this.watchers = watchers;
            this.event = event;
        }
    }
    
    class EventThread extends ZooKeeperThread
    {
        private final LinkedBlockingQueue<Object> waitingEvents;
        private volatile Watcher.Event.KeeperState sessionState;
        private volatile boolean wasKilled;
        private volatile boolean isRunning;
        
        EventThread() {
            super(makeThreadName("-EventThread"));
            this.waitingEvents = new LinkedBlockingQueue<Object>();
            this.sessionState = Watcher.Event.KeeperState.Disconnected;
            this.wasKilled = false;
            this.isRunning = false;
            this.setDaemon(true);
        }
        
        public void queueEvent(final WatchedEvent event) {
            if (event.getType() == Watcher.Event.EventType.None && this.sessionState == event.getState()) {
                return;
            }
            this.sessionState = event.getState();
            final WatcherSetEventPair pair = new WatcherSetEventPair(ClientCnxn.this.watcher.materialize(event.getState(), event.getType(), event.getPath()), event);
            this.waitingEvents.add(pair);
        }
        
        public void queuePacket(final Packet packet) {
            if (this.wasKilled) {
                synchronized (this.waitingEvents) {
                    if (this.isRunning) {
                        this.waitingEvents.add(packet);
                    }
                    else {
                        this.processEvent(packet);
                    }
                }
            }
            else {
                this.waitingEvents.add(packet);
            }
        }
        
        public void queueEventOfDeath() {
            this.waitingEvents.add(ClientCnxn.this.eventOfDeath);
        }
        
        @Override
        public void run() {
            try {
                this.isRunning = true;
                while (true) {
                    final Object event = this.waitingEvents.take();
                    if (event == ClientCnxn.this.eventOfDeath) {
                        this.wasKilled = true;
                    }
                    else {
                        this.processEvent(event);
                    }
                    if (this.wasKilled) {
                        synchronized (this.waitingEvents) {
                            if (this.waitingEvents.isEmpty()) {
                                this.isRunning = false;
                                break;
                            }
                            continue;
                        }
                    }
                }
            }
            catch (InterruptedException e) {
                ClientCnxn.LOG.error("Event thread exiting due to interruption", e);
            }
            ClientCnxn.LOG.info("EventThread shut down for session: 0x{}", Long.toHexString(ClientCnxn.this.getSessionId()));
        }
        
        private void processEvent(final Object event) {
            try {
                if (event instanceof WatcherSetEventPair) {
                    final WatcherSetEventPair pair = (WatcherSetEventPair)event;
                    for (final Watcher watcher : pair.watchers) {
                        try {
                            watcher.process(pair.event);
                        }
                        catch (Throwable t) {
                            ClientCnxn.LOG.error("Error while calling watcher ", t);
                        }
                    }
                }
                else {
                    final Packet p = (Packet)event;
                    int rc = 0;
                    final String clientPath = p.clientPath;
                    if (p.replyHeader.getErr() != 0) {
                        rc = p.replyHeader.getErr();
                    }
                    if (p.cb == null) {
                        ClientCnxn.LOG.warn("Somehow a null cb got to EventThread!");
                    }
                    else if (p.response instanceof ExistsResponse || p.response instanceof SetDataResponse || p.response instanceof SetACLResponse) {
                        final AsyncCallback.StatCallback cb = (AsyncCallback.StatCallback)p.cb;
                        if (rc == 0) {
                            if (p.response instanceof ExistsResponse) {
                                cb.processResult(rc, clientPath, p.ctx, ((ExistsResponse)p.response).getStat());
                            }
                            else if (p.response instanceof SetDataResponse) {
                                cb.processResult(rc, clientPath, p.ctx, ((SetDataResponse)p.response).getStat());
                            }
                            else if (p.response instanceof SetACLResponse) {
                                cb.processResult(rc, clientPath, p.ctx, ((SetACLResponse)p.response).getStat());
                            }
                        }
                        else {
                            cb.processResult(rc, clientPath, p.ctx, null);
                        }
                    }
                    else if (p.response instanceof GetDataResponse) {
                        final AsyncCallback.DataCallback cb2 = (AsyncCallback.DataCallback)p.cb;
                        final GetDataResponse rsp = (GetDataResponse)p.response;
                        if (rc == 0) {
                            cb2.processResult(rc, clientPath, p.ctx, rsp.getData(), rsp.getStat());
                        }
                        else {
                            cb2.processResult(rc, clientPath, p.ctx, null, null);
                        }
                    }
                    else if (p.response instanceof GetACLResponse) {
                        final AsyncCallback.ACLCallback cb3 = (AsyncCallback.ACLCallback)p.cb;
                        final GetACLResponse rsp2 = (GetACLResponse)p.response;
                        if (rc == 0) {
                            cb3.processResult(rc, clientPath, p.ctx, rsp2.getAcl(), rsp2.getStat());
                        }
                        else {
                            cb3.processResult(rc, clientPath, p.ctx, null, null);
                        }
                    }
                    else if (p.response instanceof GetChildrenResponse) {
                        final AsyncCallback.ChildrenCallback cb4 = (AsyncCallback.ChildrenCallback)p.cb;
                        final GetChildrenResponse rsp3 = (GetChildrenResponse)p.response;
                        if (rc == 0) {
                            cb4.processResult(rc, clientPath, p.ctx, rsp3.getChildren());
                        }
                        else {
                            cb4.processResult(rc, clientPath, p.ctx, null);
                        }
                    }
                    else if (p.response instanceof GetChildren2Response) {
                        final AsyncCallback.Children2Callback cb5 = (AsyncCallback.Children2Callback)p.cb;
                        final GetChildren2Response rsp4 = (GetChildren2Response)p.response;
                        if (rc == 0) {
                            cb5.processResult(rc, clientPath, p.ctx, rsp4.getChildren(), rsp4.getStat());
                        }
                        else {
                            cb5.processResult(rc, clientPath, p.ctx, null, null);
                        }
                    }
                    else if (p.response instanceof CreateResponse) {
                        final AsyncCallback.StringCallback cb6 = (AsyncCallback.StringCallback)p.cb;
                        final CreateResponse rsp5 = (CreateResponse)p.response;
                        if (rc == 0) {
                            cb6.processResult(rc, clientPath, p.ctx, (ClientCnxn.this.chrootPath == null) ? rsp5.getPath() : rsp5.getPath().substring(ClientCnxn.this.chrootPath.length()));
                        }
                        else {
                            cb6.processResult(rc, clientPath, p.ctx, null);
                        }
                    }
                    else if (p.response instanceof MultiResponse) {
                        final AsyncCallback.MultiCallback cb7 = (AsyncCallback.MultiCallback)p.cb;
                        final MultiResponse rsp6 = (MultiResponse)p.response;
                        if (rc == 0) {
                            final List<OpResult> results = rsp6.getResultList();
                            int newRc = rc;
                            for (final OpResult result : results) {
                                if (result instanceof OpResult.ErrorResult && KeeperException.Code.OK.intValue() != (newRc = ((OpResult.ErrorResult)result).getErr())) {
                                    break;
                                }
                            }
                            cb7.processResult(newRc, clientPath, p.ctx, results);
                        }
                        else {
                            cb7.processResult(rc, clientPath, p.ctx, null);
                        }
                    }
                    else if (p.cb instanceof AsyncCallback.VoidCallback) {
                        final AsyncCallback.VoidCallback cb8 = (AsyncCallback.VoidCallback)p.cb;
                        cb8.processResult(rc, clientPath, p.ctx);
                    }
                }
            }
            catch (Throwable t2) {
                ClientCnxn.LOG.error("Caught unexpected throwable", t2);
            }
        }
    }
    
    static class EndOfStreamException extends IOException
    {
        private static final long serialVersionUID = -5438877188796231422L;
        
        public EndOfStreamException(final String msg) {
            super(msg);
        }
        
        @Override
        public String toString() {
            return "EndOfStreamException: " + this.getMessage();
        }
    }
    
    private static class SessionTimeoutException extends IOException
    {
        private static final long serialVersionUID = 824482094072071178L;
        
        public SessionTimeoutException(final String msg) {
            super(msg);
        }
    }
    
    private static class SessionExpiredException extends IOException
    {
        private static final long serialVersionUID = -1388816932076193249L;
        
        public SessionExpiredException(final String msg) {
            super(msg);
        }
    }
    
    private static class RWServerFoundException extends IOException
    {
        private static final long serialVersionUID = 90431199887158758L;
        
        public RWServerFoundException(final String msg) {
            super(msg);
        }
    }
    
    class SendThread extends ZooKeeperThread
    {
        private long lastPingSentNs;
        private final ClientCnxnSocket clientCnxnSocket;
        private Random r;
        private boolean isFirstConnect;
        private InetSocketAddress rwServerAddress;
        private static final int minPingRwTimeout = 100;
        private static final int maxPingRwTimeout = 60000;
        private int pingRwTimeout;
        private boolean saslLoginFailed;
        private static final String RETRY_CONN_MSG = ", closing socket connection and attempting reconnect";
        
        void readResponse(final ByteBuffer incomingBuffer) throws IOException {
            final ByteBufferInputStream bbis = new ByteBufferInputStream(incomingBuffer);
            final BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
            final ReplyHeader replyHdr = new ReplyHeader();
            replyHdr.deserialize(bbia, "header");
            if (replyHdr.getXid() == -2) {
                if (ClientCnxn.LOG.isDebugEnabled()) {
                    ClientCnxn.LOG.debug("Got ping response for sessionid: 0x" + Long.toHexString(ClientCnxn.this.sessionId) + " after " + (System.nanoTime() - this.lastPingSentNs) / 1000000L + "ms");
                }
                return;
            }
            if (replyHdr.getXid() == -4) {
                if (replyHdr.getErr() == KeeperException.Code.AUTHFAILED.intValue()) {
                    ClientCnxn.this.state = ZooKeeper.States.AUTH_FAILED;
                    ClientCnxn.this.eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.AuthFailed, null));
                }
                if (ClientCnxn.LOG.isDebugEnabled()) {
                    ClientCnxn.LOG.debug("Got auth sessionid:0x" + Long.toHexString(ClientCnxn.this.sessionId));
                }
                return;
            }
            if (replyHdr.getXid() == -1) {
                if (ClientCnxn.LOG.isDebugEnabled()) {
                    ClientCnxn.LOG.debug("Got notification sessionid:0x" + Long.toHexString(ClientCnxn.this.sessionId));
                }
                final WatcherEvent event = new WatcherEvent();
                event.deserialize(bbia, "response");
                if (ClientCnxn.this.chrootPath != null) {
                    final String serverPath = event.getPath();
                    if (serverPath.compareTo(ClientCnxn.this.chrootPath) == 0) {
                        event.setPath("/");
                    }
                    else if (serverPath.length() > ClientCnxn.this.chrootPath.length()) {
                        event.setPath(serverPath.substring(ClientCnxn.this.chrootPath.length()));
                    }
                    else {
                        ClientCnxn.LOG.warn("Got server path " + event.getPath() + " which is too short for chroot path " + ClientCnxn.this.chrootPath);
                    }
                }
                final WatchedEvent we = new WatchedEvent(event);
                if (ClientCnxn.LOG.isDebugEnabled()) {
                    ClientCnxn.LOG.debug("Got " + we + " for sessionid 0x" + Long.toHexString(ClientCnxn.this.sessionId));
                }
                ClientCnxn.this.eventThread.queueEvent(we);
                return;
            }
            if (this.clientTunneledAuthenticationInProgress()) {
                final GetSASLRequest request = new GetSASLRequest();
                request.deserialize(bbia, "token");
                ClientCnxn.this.zooKeeperSaslClient.respondToServer(request.getToken(), ClientCnxn.this);
                return;
            }
            final Packet packet;
            synchronized (ClientCnxn.this.pendingQueue) {
                if (ClientCnxn.this.pendingQueue.size() == 0) {
                    throw new IOException("Nothing in the queue, but got " + replyHdr.getXid());
                }
                packet = ClientCnxn.this.pendingQueue.remove();
            }
            try {
                if (packet.requestHeader.getXid() != replyHdr.getXid()) {
                    packet.replyHeader.setErr(KeeperException.Code.CONNECTIONLOSS.intValue());
                    throw new IOException("Xid out of order. Got Xid " + replyHdr.getXid() + " with err " + replyHdr.getErr() + " expected Xid " + packet.requestHeader.getXid() + " for a packet with details: " + packet);
                }
                packet.replyHeader.setXid(replyHdr.getXid());
                packet.replyHeader.setErr(replyHdr.getErr());
                packet.replyHeader.setZxid(replyHdr.getZxid());
                if (replyHdr.getZxid() > 0L) {
                    ClientCnxn.this.lastZxid = replyHdr.getZxid();
                }
                if (packet.response != null && replyHdr.getErr() == 0) {
                    packet.response.deserialize(bbia, "response");
                }
                if (ClientCnxn.LOG.isDebugEnabled()) {
                    ClientCnxn.LOG.debug("Reading reply sessionid:0x" + Long.toHexString(ClientCnxn.this.sessionId) + ", packet:: " + packet);
                }
            }
            finally {
                ClientCnxn.this.finishPacket(packet);
            }
        }
        
        SendThread(final ClientCnxnSocket clientCnxnSocket) {
            super(makeThreadName("-SendThread()"));
            this.r = new Random(System.nanoTime());
            this.isFirstConnect = true;
            this.rwServerAddress = null;
            this.pingRwTimeout = 100;
            this.saslLoginFailed = false;
            ClientCnxn.this.state = ZooKeeper.States.CONNECTING;
            this.clientCnxnSocket = clientCnxnSocket;
            this.setDaemon(true);
        }
        
        ZooKeeper.States getZkState() {
            return ClientCnxn.this.state;
        }
        
        ClientCnxnSocket getClientCnxnSocket() {
            return this.clientCnxnSocket;
        }
        
        void primeConnection() throws IOException {
            ClientCnxn.LOG.info("Socket connection established to " + this.clientCnxnSocket.getRemoteSocketAddress() + ", initiating session");
            this.isFirstConnect = false;
            final long sessId = ClientCnxn.this.seenRwServerBefore ? ClientCnxn.this.sessionId : 0L;
            final ConnectRequest conReq = new ConnectRequest(0, ClientCnxn.this.lastZxid, ClientCnxn.this.sessionTimeout, sessId, ClientCnxn.this.sessionPasswd);
            synchronized (ClientCnxn.this.outgoingQueue) {
                if (!ClientCnxn.disableAutoWatchReset) {
                    final List<String> dataWatches = ClientCnxn.this.zooKeeper.getDataWatches();
                    final List<String> existWatches = ClientCnxn.this.zooKeeper.getExistWatches();
                    final List<String> childWatches = ClientCnxn.this.zooKeeper.getChildWatches();
                    if (!dataWatches.isEmpty() || !existWatches.isEmpty() || !childWatches.isEmpty()) {
                        final Iterator<String> dataWatchesIter = this.prependChroot(dataWatches).iterator();
                        final Iterator<String> existWatchesIter = this.prependChroot(existWatches).iterator();
                        final Iterator<String> childWatchesIter = this.prependChroot(childWatches).iterator();
                        final long setWatchesLastZxid = ClientCnxn.this.lastZxid;
                        while (dataWatchesIter.hasNext() || existWatchesIter.hasNext() || childWatchesIter.hasNext()) {
                            final List<String> dataWatchesBatch = new ArrayList<String>();
                            final List<String> existWatchesBatch = new ArrayList<String>();
                            final List<String> childWatchesBatch = new ArrayList<String>();
                            String watch;
                            for (int batchLength = 0; batchLength < 131072; batchLength += watch.length()) {
                                if (dataWatchesIter.hasNext()) {
                                    watch = dataWatchesIter.next();
                                    dataWatchesBatch.add(watch);
                                }
                                else if (existWatchesIter.hasNext()) {
                                    watch = existWatchesIter.next();
                                    existWatchesBatch.add(watch);
                                }
                                else {
                                    if (!childWatchesIter.hasNext()) {
                                        break;
                                    }
                                    watch = childWatchesIter.next();
                                    childWatchesBatch.add(watch);
                                }
                            }
                            final SetWatches sw = new SetWatches(setWatchesLastZxid, dataWatchesBatch, existWatchesBatch, childWatchesBatch);
                            final RequestHeader h = new RequestHeader();
                            h.setType(101);
                            h.setXid(-8);
                            final Packet packet = new Packet(h, new ReplyHeader(), sw, null, null);
                            ClientCnxn.this.outgoingQueue.addFirst(packet);
                        }
                    }
                }
                for (final AuthData id : ClientCnxn.this.authInfo) {
                    ClientCnxn.this.outgoingQueue.addFirst(new Packet(new RequestHeader(-4, 100), null, new AuthPacket(0, id.scheme, id.data), null, null));
                }
                ClientCnxn.this.outgoingQueue.addFirst(new Packet(null, null, conReq, null, null, ClientCnxn.this.readOnly));
            }
            this.clientCnxnSocket.enableReadWriteOnly();
            if (ClientCnxn.LOG.isDebugEnabled()) {
                ClientCnxn.LOG.debug("Session establishment request sent on " + this.clientCnxnSocket.getRemoteSocketAddress());
            }
        }
        
        private List<String> prependChroot(final List<String> paths) {
            if (ClientCnxn.this.chrootPath != null && !paths.isEmpty()) {
                for (int i = 0; i < paths.size(); ++i) {
                    final String clientPath = paths.get(i);
                    String serverPath;
                    if (clientPath.length() == 1) {
                        serverPath = ClientCnxn.this.chrootPath;
                    }
                    else {
                        serverPath = ClientCnxn.this.chrootPath + clientPath;
                    }
                    paths.set(i, serverPath);
                }
            }
            return paths;
        }
        
        private void sendPing() {
            this.lastPingSentNs = System.nanoTime();
            final RequestHeader h = new RequestHeader(-2, 11);
            ClientCnxn.this.queuePacket(h, null, null, null, null, null, null, null, null);
        }
        
        private void startConnect(final InetSocketAddress addr) throws IOException {
            this.saslLoginFailed = false;
            ClientCnxn.this.state = ZooKeeper.States.CONNECTING;
            this.setName(this.getName().replaceAll("\\(.*\\)", "(" + addr.getHostName() + ":" + addr.getPort() + ")"));
            if (ZooKeeperSaslClient.isEnabled()) {
                try {
                    final String principalUserName = System.getProperty("zookeeper.sasl.client.username", "zookeeper");
                    ClientCnxn.this.zooKeeperSaslClient = new ZooKeeperSaslClient(principalUserName + "/" + addr.getHostName());
                }
                catch (LoginException e) {
                    ClientCnxn.LOG.warn("SASL configuration failed: " + e + " Will continue connection to Zookeeper server without SASL authentication, if Zookeeper server allows it.");
                    ClientCnxn.this.eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.AuthFailed, null));
                    this.saslLoginFailed = true;
                }
            }
            this.logStartConnect(addr);
            this.clientCnxnSocket.connect(addr);
        }
        
        private void logStartConnect(final InetSocketAddress addr) {
            String msg = "Opening socket connection to server " + addr;
            if (ClientCnxn.this.zooKeeperSaslClient != null) {
                msg = msg + ". " + ClientCnxn.this.zooKeeperSaslClient.getConfigStatus();
            }
            ClientCnxn.LOG.info(msg);
        }
        
        @Override
        public void run() {
            this.clientCnxnSocket.introduce(this, ClientCnxn.this.sessionId);
            this.clientCnxnSocket.updateNow();
            this.clientCnxnSocket.updateLastSendAndHeard();
            long lastPingRwServer = Time.currentElapsedTime();
            final int MAX_SEND_PING_INTERVAL = 10000;
            InetSocketAddress serverAddress = null;
            while (ClientCnxn.this.state.isAlive()) {
                try {
                    if (!this.clientCnxnSocket.isConnected()) {
                        if (!this.isFirstConnect) {
                            try {
                                Thread.sleep(this.r.nextInt(1000));
                            }
                            catch (InterruptedException e) {
                                ClientCnxn.LOG.warn("Unexpected exception", e);
                            }
                        }
                        if (ClientCnxn.this.closing || !ClientCnxn.this.state.isAlive()) {
                            break;
                        }
                        if (this.rwServerAddress != null) {
                            serverAddress = this.rwServerAddress;
                            this.rwServerAddress = null;
                        }
                        else {
                            serverAddress = ClientCnxn.this.hostProvider.next(1000L);
                        }
                        this.startConnect(serverAddress);
                        this.clientCnxnSocket.updateLastSendAndHeard();
                    }
                    int to;
                    if (ClientCnxn.this.state.isConnected()) {
                        if (ClientCnxn.this.zooKeeperSaslClient != null) {
                            boolean sendAuthEvent = false;
                            if (ClientCnxn.this.zooKeeperSaslClient.getSaslState() == ZooKeeperSaslClient.SaslState.INITIAL) {
                                try {
                                    ClientCnxn.this.zooKeeperSaslClient.initialize(ClientCnxn.this);
                                }
                                catch (SaslException e2) {
                                    ClientCnxn.LOG.error("SASL authentication with Zookeeper Quorum member failed: " + e2);
                                    ClientCnxn.this.state = ZooKeeper.States.AUTH_FAILED;
                                    sendAuthEvent = true;
                                }
                            }
                            final Watcher.Event.KeeperState authState = ClientCnxn.this.zooKeeperSaslClient.getKeeperState();
                            if (authState != null) {
                                if (authState == Watcher.Event.KeeperState.AuthFailed) {
                                    ClientCnxn.this.state = ZooKeeper.States.AUTH_FAILED;
                                    sendAuthEvent = true;
                                }
                                else if (authState == Watcher.Event.KeeperState.SaslAuthenticated) {
                                    sendAuthEvent = true;
                                }
                            }
                            if (sendAuthEvent) {
                                ClientCnxn.this.eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, authState, null));
                            }
                        }
                        to = ClientCnxn.this.readTimeout - this.clientCnxnSocket.getIdleRecv();
                    }
                    else {
                        to = ClientCnxn.this.connectTimeout - this.clientCnxnSocket.getIdleRecv();
                    }
                    if (to <= 0) {
                        final String warnInfo = "Client session timed out, have not heard from server in " + this.clientCnxnSocket.getIdleRecv() + "ms for sessionid 0x" + Long.toHexString(ClientCnxn.this.sessionId);
                        ClientCnxn.LOG.warn(warnInfo);
                        throw new SessionTimeoutException(warnInfo);
                    }
                    if (ClientCnxn.this.state.isConnected()) {
                        final int timeToNextPing = ClientCnxn.this.readTimeout / 2 - this.clientCnxnSocket.getIdleSend() - ((this.clientCnxnSocket.getIdleSend() > 1000) ? 1000 : 0);
                        if (timeToNextPing <= 0 || this.clientCnxnSocket.getIdleSend() > 10000) {
                            this.sendPing();
                            this.clientCnxnSocket.updateLastSend();
                        }
                        else if (timeToNextPing < to) {
                            to = timeToNextPing;
                        }
                    }
                    if (ClientCnxn.this.state == ZooKeeper.States.CONNECTEDREADONLY) {
                        final long now = Time.currentElapsedTime();
                        int idlePingRwServer = (int)(now - lastPingRwServer);
                        if (idlePingRwServer >= this.pingRwTimeout) {
                            lastPingRwServer = now;
                            idlePingRwServer = 0;
                            this.pingRwTimeout = Math.min(2 * this.pingRwTimeout, 60000);
                            this.pingRwServer();
                        }
                        to = Math.min(to, this.pingRwTimeout - idlePingRwServer);
                    }
                    this.clientCnxnSocket.doTransport(to, ClientCnxn.this.pendingQueue, ClientCnxn.this.outgoingQueue, ClientCnxn.this);
                }
                catch (Throwable e3) {
                    if (ClientCnxn.this.closing) {
                        if (ClientCnxn.LOG.isDebugEnabled()) {
                            ClientCnxn.LOG.debug("An exception was thrown while closing send thread for session 0x" + Long.toHexString(ClientCnxn.this.getSessionId()) + " : " + e3.getMessage());
                        }
                        break;
                    }
                    if (e3 instanceof SessionExpiredException) {
                        ClientCnxn.LOG.info(e3.getMessage() + ", closing socket connection");
                    }
                    else if (e3 instanceof SessionTimeoutException) {
                        ClientCnxn.LOG.info(e3.getMessage() + ", closing socket connection and attempting reconnect");
                    }
                    else if (e3 instanceof EndOfStreamException) {
                        ClientCnxn.LOG.info(e3.getMessage() + ", closing socket connection and attempting reconnect");
                    }
                    else if (e3 instanceof RWServerFoundException) {
                        ClientCnxn.LOG.info(e3.getMessage());
                    }
                    else if (e3 instanceof SocketException) {
                        ClientCnxn.LOG.info("Socket error occurred: {}: {}", serverAddress, e3.getMessage());
                    }
                    else {
                        ClientCnxn.LOG.warn("Session 0x{} for server {}, unexpected error{}", Long.toHexString(ClientCnxn.this.getSessionId()), serverAddress, ", closing socket connection and attempting reconnect", e3);
                    }
                    this.cleanup();
                    if (ClientCnxn.this.state.isAlive()) {
                        ClientCnxn.this.eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.Disconnected, null));
                    }
                    this.clientCnxnSocket.updateNow();
                    this.clientCnxnSocket.updateLastSendAndHeard();
                }
            }
            this.cleanup();
            this.clientCnxnSocket.close();
            if (ClientCnxn.this.state.isAlive()) {
                ClientCnxn.this.eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.Disconnected, null));
            }
            ZooTrace.logTraceMessage(ClientCnxn.LOG, ZooTrace.getTextTraceLevel(), "SendThread exited loop for session: 0x" + Long.toHexString(ClientCnxn.this.getSessionId()));
        }
        
        private void pingRwServer() throws RWServerFoundException, UnknownHostException {
            String result = null;
            final InetSocketAddress addr = ClientCnxn.this.hostProvider.next(0L);
            ClientCnxn.LOG.info("Checking server " + addr + " for being r/w. Timeout " + this.pingRwTimeout);
            Socket sock = null;
            BufferedReader br = null;
            try {
                sock = new Socket(addr.getHostName(), addr.getPort());
                sock.setSoLinger(false, -1);
                sock.setSoTimeout(1000);
                sock.setTcpNoDelay(true);
                sock.getOutputStream().write("isro".getBytes());
                sock.getOutputStream().flush();
                sock.shutdownOutput();
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                result = br.readLine();
            }
            catch (ConnectException ex) {}
            catch (IOException e) {
                ClientCnxn.LOG.warn("Exception while seeking for r/w server " + e.getMessage(), e);
            }
            finally {
                if (sock != null) {
                    try {
                        sock.close();
                    }
                    catch (IOException e2) {
                        ClientCnxn.LOG.warn("Unexpected exception", e2);
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    }
                    catch (IOException e2) {
                        ClientCnxn.LOG.warn("Unexpected exception", e2);
                    }
                }
            }
            if ("rw".equals(result)) {
                this.pingRwTimeout = 100;
                this.rwServerAddress = addr;
                throw new RWServerFoundException("Majority server found at " + addr.getHostName() + ":" + addr.getPort());
            }
        }
        
        private void cleanup() {
            this.clientCnxnSocket.cleanup();
            synchronized (ClientCnxn.this.pendingQueue) {
                for (final Packet p : ClientCnxn.this.pendingQueue) {
                    ClientCnxn.this.conLossPacket(p);
                }
                ClientCnxn.this.pendingQueue.clear();
            }
            synchronized (ClientCnxn.this.outgoingQueue) {
                for (final Packet p : ClientCnxn.this.outgoingQueue) {
                    ClientCnxn.this.conLossPacket(p);
                }
                ClientCnxn.this.outgoingQueue.clear();
            }
        }
        
        void onConnected(final int _negotiatedSessionTimeout, final long _sessionId, final byte[] _sessionPasswd, final boolean isRO) throws IOException {
            ClientCnxn.this.negotiatedSessionTimeout = _negotiatedSessionTimeout;
            if (ClientCnxn.this.negotiatedSessionTimeout <= 0) {
                ClientCnxn.this.state = ZooKeeper.States.CLOSED;
                ClientCnxn.this.eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.Expired, null));
                ClientCnxn.this.eventThread.queueEventOfDeath();
                final String warnInfo = "Unable to reconnect to ZooKeeper service, session 0x" + Long.toHexString(ClientCnxn.this.sessionId) + " has expired";
                ClientCnxn.LOG.warn(warnInfo);
                throw new SessionExpiredException(warnInfo);
            }
            if (!ClientCnxn.this.readOnly && isRO) {
                ClientCnxn.LOG.error("Read/write client got connected to read-only server");
            }
            ClientCnxn.this.readTimeout = ClientCnxn.this.negotiatedSessionTimeout * 2 / 3;
            ClientCnxn.this.connectTimeout = ClientCnxn.this.negotiatedSessionTimeout / ClientCnxn.this.hostProvider.size();
            ClientCnxn.this.hostProvider.onConnected();
            ClientCnxn.this.sessionId = _sessionId;
            ClientCnxn.this.sessionPasswd = _sessionPasswd;
            ClientCnxn.this.state = (isRO ? ZooKeeper.States.CONNECTEDREADONLY : ZooKeeper.States.CONNECTED);
            final ClientCnxn this$0 = ClientCnxn.this;
            this$0.seenRwServerBefore |= !isRO;
            ClientCnxn.LOG.info("Session establishment complete on server " + this.clientCnxnSocket.getRemoteSocketAddress() + ", sessionid = 0x" + Long.toHexString(ClientCnxn.this.sessionId) + ", negotiated timeout = " + ClientCnxn.this.negotiatedSessionTimeout + (isRO ? " (READ-ONLY mode)" : ""));
            final Watcher.Event.KeeperState eventState = isRO ? Watcher.Event.KeeperState.ConnectedReadOnly : Watcher.Event.KeeperState.SyncConnected;
            ClientCnxn.this.eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, eventState, null));
        }
        
        void close() {
            ClientCnxn.this.state = ZooKeeper.States.CLOSED;
            this.clientCnxnSocket.wakeupCnxn();
        }
        
        void testableCloseSocket() throws IOException {
            this.clientCnxnSocket.testableCloseSocket();
        }
        
        public boolean clientTunneledAuthenticationInProgress() {
            return ZooKeeperSaslClient.isEnabled() && !this.saslLoginFailed && (ClientCnxn.this.zooKeeperSaslClient == null || ClientCnxn.this.zooKeeperSaslClient.clientTunneledAuthenticationInProgress());
        }
        
        public void sendPacket(final Packet p) throws IOException {
            this.clientCnxnSocket.sendPacket(p);
        }
    }
}
