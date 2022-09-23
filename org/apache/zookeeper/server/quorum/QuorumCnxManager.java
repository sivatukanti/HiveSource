// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.nio.BufferUnderflowException;
import java.net.ServerSocket;
import org.apache.zookeeper.server.ZooKeeperThread;
import org.slf4j.LoggerFactory;
import java.util.NoSuchElementException;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Enumeration;
import java.net.InetSocketAddress;
import java.nio.channels.UnresolvedAddressException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.util.Collections;
import java.util.HashSet;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zookeeper.server.quorum.auth.QuorumAuthLearner;
import org.apache.zookeeper.server.quorum.auth.QuorumAuthServer;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;

public class QuorumCnxManager
{
    private static final Logger LOG;
    static final int RECV_CAPACITY = 100;
    static final int SEND_CAPACITY = 1;
    static final int PACKETMAXSIZE = 524288;
    public static final int maxBuffer = 2048;
    private AtomicLong observerCounter;
    private int cnxTO;
    final long mySid;
    final int socketTimeout;
    final Map<Long, QuorumPeer.QuorumServer> view;
    final boolean tcpKeepAlive;
    final boolean listenOnAllIPs;
    private ThreadPoolExecutor connectionExecutor;
    private final Set<Long> inprogressConnections;
    private QuorumAuthServer authServer;
    private QuorumAuthLearner authLearner;
    private boolean quorumSaslAuthEnabled;
    private AtomicInteger connectionThreadCnt;
    final ConcurrentHashMap<Long, SendWorker> senderWorkerMap;
    final ConcurrentHashMap<Long, ArrayBlockingQueue<ByteBuffer>> queueSendMap;
    final ConcurrentHashMap<Long, ByteBuffer> lastMessageSent;
    public final ArrayBlockingQueue<Message> recvQueue;
    private final Object recvQLock;
    volatile boolean shutdown;
    public final Listener listener;
    private AtomicInteger threadCnt;
    
    public QuorumCnxManager(final long mySid, final Map<Long, QuorumPeer.QuorumServer> view, final QuorumAuthServer authServer, final QuorumAuthLearner authLearner, final int socketTimeout, final boolean listenOnAllIPs, final int quorumCnxnThreadsSize, final boolean quorumSaslAuthEnabled) {
        this(mySid, view, authServer, authLearner, socketTimeout, listenOnAllIPs, quorumCnxnThreadsSize, quorumSaslAuthEnabled, new ConcurrentHashMap<Long, SendWorker>());
    }
    
    public QuorumCnxManager(final long mySid, final Map<Long, QuorumPeer.QuorumServer> view, final QuorumAuthServer authServer, final QuorumAuthLearner authLearner, final int socketTimeout, final boolean listenOnAllIPs, final int quorumCnxnThreadsSize, final boolean quorumSaslAuthEnabled, final ConcurrentHashMap<Long, SendWorker> senderWorkerMap) {
        this.observerCounter = new AtomicLong(-1L);
        this.cnxTO = 5000;
        this.tcpKeepAlive = Boolean.getBoolean("zookeeper.tcpKeepAlive");
        this.inprogressConnections = Collections.synchronizedSet(new HashSet<Long>());
        this.connectionThreadCnt = new AtomicInteger(0);
        this.recvQLock = new Object();
        this.shutdown = false;
        this.threadCnt = new AtomicInteger(0);
        this.senderWorkerMap = senderWorkerMap;
        this.recvQueue = new ArrayBlockingQueue<Message>(100);
        this.queueSendMap = new ConcurrentHashMap<Long, ArrayBlockingQueue<ByteBuffer>>();
        this.lastMessageSent = new ConcurrentHashMap<Long, ByteBuffer>();
        final String cnxToValue = System.getProperty("zookeeper.cnxTimeout");
        if (cnxToValue != null) {
            this.cnxTO = Integer.parseInt(cnxToValue);
        }
        this.mySid = mySid;
        this.socketTimeout = socketTimeout;
        this.view = view;
        this.listenOnAllIPs = listenOnAllIPs;
        this.initializeAuth(mySid, authServer, authLearner, quorumCnxnThreadsSize, quorumSaslAuthEnabled);
        this.listener = new Listener();
    }
    
    private void initializeAuth(final long mySid, final QuorumAuthServer authServer, final QuorumAuthLearner authLearner, final int quorumCnxnThreadsSize, final boolean quorumSaslAuthEnabled) {
        this.authServer = authServer;
        this.authLearner = authLearner;
        if (!(this.quorumSaslAuthEnabled = quorumSaslAuthEnabled)) {
            QuorumCnxManager.LOG.debug("Not initializing connection executor as quorum sasl auth is disabled");
            return;
        }
        final AtomicInteger threadIndex = new AtomicInteger(1);
        final SecurityManager s = System.getSecurityManager();
        final ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        final ThreadFactory daemonThFactory = new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                final Thread t = new Thread(group, r, "QuorumConnectionThread-[myid=" + mySid + "]-" + threadIndex.getAndIncrement());
                return t;
            }
        };
        (this.connectionExecutor = new ThreadPoolExecutor(3, quorumCnxnThreadsSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), daemonThFactory)).allowCoreThreadTimeOut(true);
    }
    
    public void testInitiateConnection(final long sid) throws Exception {
        if (QuorumCnxManager.LOG.isDebugEnabled()) {
            QuorumCnxManager.LOG.debug("Opening channel to server " + sid);
        }
        final Socket sock = new Socket();
        this.setSockOpts(sock);
        sock.connect(QuorumPeer.viewToVotingView(this.view).get(sid).electionAddr, this.cnxTO);
        this.initiateConnection(sock, sid);
    }
    
    public void initiateConnection(final Socket sock, final Long sid) {
        try {
            this.startConnection(sock, sid);
        }
        catch (IOException e) {
            QuorumCnxManager.LOG.error("Exception while connecting, id: {}, addr: {}, closing learner connection", (Object)new Object[] { sid, sock.getRemoteSocketAddress() }, e);
            this.closeSocket(sock);
        }
    }
    
    public void initiateConnectionAsync(final Socket sock, final Long sid) {
        if (!this.inprogressConnections.add(sid)) {
            QuorumCnxManager.LOG.debug("Connection request to server id: {} is already in progress, so skipping this request", sid);
            this.closeSocket(sock);
            return;
        }
        try {
            this.connectionExecutor.execute(new QuorumConnectionReqThread(sock, sid));
            this.connectionThreadCnt.incrementAndGet();
        }
        catch (Throwable e) {
            this.inprogressConnections.remove(sid);
            QuorumCnxManager.LOG.error("Exception while submitting quorum connection request", e);
            this.closeSocket(sock);
        }
    }
    
    private boolean startConnection(final Socket sock, final Long sid) throws IOException {
        DataOutputStream dout = null;
        DataInputStream din = null;
        try {
            dout = new DataOutputStream(sock.getOutputStream());
            dout.writeLong(this.mySid);
            dout.flush();
            din = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        }
        catch (IOException e) {
            QuorumCnxManager.LOG.warn("Ignoring exception reading or writing challenge: ", e);
            this.closeSocket(sock);
            return false;
        }
        this.authLearner.authenticate(sock, this.view.get(sid).hostname);
        if (sid > this.mySid) {
            QuorumCnxManager.LOG.info("Have smaller server identifier, so dropping the connection: (" + sid + ", " + this.mySid + ")");
            this.closeSocket(sock);
            return false;
        }
        final SendWorker sw = new SendWorker(sock, sid);
        final RecvWorker rw = new RecvWorker(sock, din, sid, sw);
        sw.setRecv(rw);
        final SendWorker vsw = this.senderWorkerMap.get(sid);
        if (vsw != null) {
            vsw.finish();
        }
        this.senderWorkerMap.put(sid, sw);
        this.queueSendMap.putIfAbsent(sid, new ArrayBlockingQueue<ByteBuffer>(1));
        sw.start();
        rw.start();
        return true;
    }
    
    public void receiveConnection(final Socket sock) {
        DataInputStream din = null;
        try {
            din = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
            this.handleConnection(sock, din);
        }
        catch (IOException e) {
            QuorumCnxManager.LOG.error("Exception handling connection, addr: {}, closing server connection", sock.getRemoteSocketAddress());
            this.closeSocket(sock);
        }
    }
    
    public void receiveConnectionAsync(final Socket sock) {
        try {
            this.connectionExecutor.execute(new QuorumConnectionReceiverThread(sock));
            this.connectionThreadCnt.incrementAndGet();
        }
        catch (Throwable e) {
            QuorumCnxManager.LOG.error("Exception handling connection, addr: {}, closing server connection", sock.getRemoteSocketAddress());
            this.closeSocket(sock);
        }
    }
    
    private void handleConnection(final Socket sock, final DataInputStream din) throws IOException {
        Long sid = null;
        try {
            sid = din.readLong();
            if (sid < 0L) {
                sid = din.readLong();
                final int num_remaining_bytes = din.readInt();
                if (num_remaining_bytes < 0 || num_remaining_bytes > 2048) {
                    QuorumCnxManager.LOG.error("Unreasonable buffer length: {}", (Object)num_remaining_bytes);
                    this.closeSocket(sock);
                    return;
                }
                final byte[] b = new byte[num_remaining_bytes];
                final int num_read = din.read(b);
                if (num_read != num_remaining_bytes) {
                    QuorumCnxManager.LOG.error("Read only " + num_read + " bytes out of " + num_remaining_bytes + " sent by server " + sid);
                }
            }
            if (sid == Long.MAX_VALUE) {
                sid = this.observerCounter.getAndDecrement();
                QuorumCnxManager.LOG.info("Setting arbitrary identifier to observer: " + sid);
            }
        }
        catch (IOException e) {
            this.closeSocket(sock);
            QuorumCnxManager.LOG.warn("Exception reading or writing challenge: " + e.toString());
            return;
        }
        QuorumCnxManager.LOG.debug("Authenticating learner server.id: {}", sid);
        this.authServer.authenticate(sock, din);
        if (sid < this.mySid) {
            final SendWorker sw = this.senderWorkerMap.get(sid);
            if (sw != null) {
                sw.finish();
            }
            QuorumCnxManager.LOG.debug("Create new connection to server: " + sid);
            this.closeSocket(sock);
            this.connectOne(sid);
            return;
        }
        final SendWorker sw = new SendWorker(sock, sid);
        final RecvWorker rw = new RecvWorker(sock, din, sid, sw);
        sw.setRecv(rw);
        final SendWorker vsw = this.senderWorkerMap.get(sid);
        if (vsw != null) {
            vsw.finish();
        }
        this.senderWorkerMap.put(sid, sw);
        this.queueSendMap.putIfAbsent(sid, new ArrayBlockingQueue<ByteBuffer>(1));
        sw.start();
        rw.start();
    }
    
    public void toSend(final Long sid, final ByteBuffer b) {
        if (this.mySid == sid) {
            b.position(0);
            this.addToRecvQueue(new Message(b.duplicate(), sid));
        }
        else {
            final ArrayBlockingQueue<ByteBuffer> bq = new ArrayBlockingQueue<ByteBuffer>(1);
            final ArrayBlockingQueue<ByteBuffer> bqExisting = this.queueSendMap.putIfAbsent(sid, bq);
            if (bqExisting != null) {
                this.addToSendQueue(bqExisting, b);
            }
            else {
                this.addToSendQueue(bq, b);
            }
            this.connectOne(sid);
        }
    }
    
    public synchronized void connectOne(final long sid) {
        if (!this.connectedToPeer(sid)) {
            if (!this.view.containsKey(sid)) {
                QuorumCnxManager.LOG.warn("Invalid server id: " + sid);
                return;
            }
            final InetSocketAddress electionAddr = this.view.get(sid).electionAddr;
            try {
                QuorumCnxManager.LOG.debug("Opening channel to server " + sid);
                final Socket sock = new Socket();
                this.setSockOpts(sock);
                sock.connect(this.view.get(sid).electionAddr, this.cnxTO);
                QuorumCnxManager.LOG.debug("Connected to server " + sid);
                if (this.quorumSaslAuthEnabled) {
                    this.initiateConnectionAsync(sock, sid);
                }
                else {
                    this.initiateConnection(sock, sid);
                }
            }
            catch (UnresolvedAddressException e) {
                QuorumCnxManager.LOG.warn("Cannot open channel to " + sid + " at election address " + electionAddr, e);
                if (this.view.containsKey(sid)) {
                    this.view.get(sid).recreateSocketAddresses();
                }
                throw e;
            }
            catch (IOException e2) {
                QuorumCnxManager.LOG.warn("Cannot open channel to " + sid + " at election address " + electionAddr, e2);
                if (this.view.containsKey(sid)) {
                    this.view.get(sid).recreateSocketAddresses();
                }
            }
        }
        else {
            QuorumCnxManager.LOG.debug("There is a connection already for server " + sid);
        }
    }
    
    public void connectAll() {
        final Enumeration<Long> en = this.queueSendMap.keys();
        while (en.hasMoreElements()) {
            final long sid = en.nextElement();
            this.connectOne(sid);
        }
    }
    
    boolean haveDelivered() {
        for (final ArrayBlockingQueue<ByteBuffer> queue : this.queueSendMap.values()) {
            QuorumCnxManager.LOG.debug("Queue size: " + queue.size());
            if (queue.size() == 0) {
                return true;
            }
        }
        return false;
    }
    
    public void halt() {
        this.shutdown = true;
        QuorumCnxManager.LOG.debug("Halting listener");
        this.listener.halt();
        this.softHalt();
        if (this.connectionExecutor != null) {
            this.connectionExecutor.shutdown();
        }
        this.inprogressConnections.clear();
        this.resetConnectionThreadCount();
    }
    
    public void softHalt() {
        for (final SendWorker sw : this.senderWorkerMap.values()) {
            QuorumCnxManager.LOG.debug("Halting sender: " + sw);
            sw.finish();
        }
    }
    
    private void setSockOpts(final Socket sock) throws SocketException {
        sock.setTcpNoDelay(true);
        sock.setKeepAlive(this.tcpKeepAlive);
        sock.setSoTimeout(this.socketTimeout);
    }
    
    private void closeSocket(final Socket sock) {
        try {
            sock.close();
        }
        catch (IOException ie) {
            QuorumCnxManager.LOG.error("Exception while closing", ie);
        }
    }
    
    public long getThreadCount() {
        return this.threadCnt.get();
    }
    
    public long getConnectionThreadCount() {
        return this.connectionThreadCnt.get();
    }
    
    private void resetConnectionThreadCount() {
        this.connectionThreadCnt.set(0);
    }
    
    private void addToSendQueue(final ArrayBlockingQueue<ByteBuffer> queue, final ByteBuffer buffer) {
        if (queue.remainingCapacity() == 0) {
            try {
                queue.remove();
            }
            catch (NoSuchElementException ne) {
                QuorumCnxManager.LOG.debug("Trying to remove from an empty Queue. Ignoring exception " + ne);
            }
        }
        try {
            queue.add(buffer);
        }
        catch (IllegalStateException ie) {
            QuorumCnxManager.LOG.error("Unable to insert an element in the queue " + ie);
        }
    }
    
    private boolean isSendQueueEmpty(final ArrayBlockingQueue<ByteBuffer> queue) {
        return queue.isEmpty();
    }
    
    private ByteBuffer pollSendQueue(final ArrayBlockingQueue<ByteBuffer> queue, final long timeout, final TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
    
    public void addToRecvQueue(final Message msg) {
        synchronized (this.recvQLock) {
            if (this.recvQueue.remainingCapacity() == 0) {
                try {
                    this.recvQueue.remove();
                }
                catch (NoSuchElementException ne) {
                    QuorumCnxManager.LOG.debug("Trying to remove from an empty recvQueue. Ignoring exception " + ne);
                }
            }
            try {
                this.recvQueue.add(msg);
            }
            catch (IllegalStateException ie) {
                QuorumCnxManager.LOG.error("Unable to insert element in the recvQueue " + ie);
            }
        }
    }
    
    public Message pollRecvQueue(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.recvQueue.poll(timeout, unit);
    }
    
    public boolean connectedToPeer(final long peerSid) {
        return this.senderWorkerMap.get(peerSid) != null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QuorumCnxManager.class);
    }
    
    public static class Message
    {
        ByteBuffer buffer;
        long sid;
        
        Message(final ByteBuffer buffer, final long sid) {
            this.buffer = buffer;
            this.sid = sid;
        }
    }
    
    private class QuorumConnectionReqThread extends ZooKeeperThread
    {
        final Socket sock;
        final Long sid;
        
        QuorumConnectionReqThread(final Socket sock, final Long sid) {
            super("QuorumConnectionReqThread-" + sid);
            this.sock = sock;
            this.sid = sid;
        }
        
        @Override
        public void run() {
            try {
                QuorumCnxManager.this.initiateConnection(this.sock, this.sid);
            }
            finally {
                QuorumCnxManager.this.inprogressConnections.remove(this.sid);
            }
        }
    }
    
    private class QuorumConnectionReceiverThread extends ZooKeeperThread
    {
        private final Socket sock;
        
        QuorumConnectionReceiverThread(final Socket sock) {
            super("QuorumConnectionReceiverThread-" + sock.getRemoteSocketAddress());
            this.sock = sock;
        }
        
        @Override
        public void run() {
            QuorumCnxManager.this.receiveConnection(this.sock);
        }
    }
    
    public class Listener extends ZooKeeperThread
    {
        volatile ServerSocket ss;
        
        public Listener() {
            super("ListenerThread");
            this.ss = null;
        }
        
        @Override
        public void run() {
            int numRetries = 0;
            while (!QuorumCnxManager.this.shutdown && numRetries < 3) {
                try {
                    (this.ss = new ServerSocket()).setReuseAddress(true);
                    InetSocketAddress addr;
                    if (QuorumCnxManager.this.listenOnAllIPs) {
                        final int port = QuorumCnxManager.this.view.get(QuorumCnxManager.this.mySid).electionAddr.getPort();
                        addr = new InetSocketAddress(port);
                    }
                    else {
                        addr = QuorumCnxManager.this.view.get(QuorumCnxManager.this.mySid).electionAddr;
                    }
                    QuorumCnxManager.LOG.info("My election bind port: " + addr.toString());
                    this.setName(QuorumCnxManager.this.view.get(QuorumCnxManager.this.mySid).electionAddr.toString());
                    this.ss.bind(addr);
                    while (!QuorumCnxManager.this.shutdown) {
                        final Socket client = this.ss.accept();
                        QuorumCnxManager.this.setSockOpts(client);
                        QuorumCnxManager.LOG.info("Received connection request " + client.getRemoteSocketAddress());
                        if (QuorumCnxManager.this.quorumSaslAuthEnabled) {
                            QuorumCnxManager.this.receiveConnectionAsync(client);
                        }
                        else {
                            QuorumCnxManager.this.receiveConnection(client);
                        }
                        numRetries = 0;
                    }
                }
                catch (IOException e) {
                    QuorumCnxManager.LOG.error("Exception while listening", e);
                    ++numRetries;
                    try {
                        this.ss.close();
                        Thread.sleep(1000L);
                    }
                    catch (IOException ie) {
                        QuorumCnxManager.LOG.error("Error closing server socket", ie);
                    }
                    catch (InterruptedException ie2) {
                        QuorumCnxManager.LOG.error("Interrupted while sleeping. Ignoring exception", ie2);
                    }
                }
            }
            QuorumCnxManager.LOG.info("Leaving listener");
            if (!QuorumCnxManager.this.shutdown) {
                QuorumCnxManager.LOG.error("As I'm leaving the listener thread, I won't be able to participate in leader election any longer: " + QuorumCnxManager.this.view.get(QuorumCnxManager.this.mySid).electionAddr);
            }
        }
        
        void halt() {
            try {
                QuorumCnxManager.LOG.debug("Trying to close listener: " + this.ss);
                if (this.ss != null) {
                    QuorumCnxManager.LOG.debug("Closing listener: " + QuorumCnxManager.this.mySid);
                    this.ss.close();
                }
            }
            catch (IOException e) {
                QuorumCnxManager.LOG.warn("Exception when shutting down listener: " + e);
            }
        }
    }
    
    class SendWorker extends ZooKeeperThread
    {
        Long sid;
        Socket sock;
        RecvWorker recvWorker;
        volatile boolean running;
        DataOutputStream dout;
        
        SendWorker(final Socket sock, final Long sid) {
            super("SendWorker:" + sid);
            this.running = true;
            this.sid = sid;
            this.sock = sock;
            this.recvWorker = null;
            try {
                this.dout = new DataOutputStream(sock.getOutputStream());
            }
            catch (IOException e) {
                QuorumCnxManager.LOG.error("Unable to access socket output stream", e);
                QuorumCnxManager.this.closeSocket(sock);
                this.running = false;
            }
            QuorumCnxManager.LOG.debug("Address of remote peer: " + this.sid);
        }
        
        synchronized void setRecv(final RecvWorker recvWorker) {
            this.recvWorker = recvWorker;
        }
        
        synchronized RecvWorker getRecvWorker() {
            return this.recvWorker;
        }
        
        synchronized boolean finish() {
            if (QuorumCnxManager.LOG.isDebugEnabled()) {
                QuorumCnxManager.LOG.debug("Calling finish for " + this.sid);
            }
            if (!this.running) {
                return this.running;
            }
            this.running = false;
            QuorumCnxManager.this.closeSocket(this.sock);
            this.interrupt();
            if (this.recvWorker != null) {
                this.recvWorker.finish();
            }
            if (QuorumCnxManager.LOG.isDebugEnabled()) {
                QuorumCnxManager.LOG.debug("Removing entry from senderWorkerMap sid=" + this.sid);
            }
            QuorumCnxManager.this.senderWorkerMap.remove(this.sid, this);
            QuorumCnxManager.this.threadCnt.decrementAndGet();
            return this.running;
        }
        
        synchronized void send(final ByteBuffer b) throws IOException {
            final byte[] msgBytes = new byte[b.capacity()];
            try {
                b.position(0);
                b.get(msgBytes);
            }
            catch (BufferUnderflowException be) {
                QuorumCnxManager.LOG.error("BufferUnderflowException ", be);
                return;
            }
            this.dout.writeInt(b.capacity());
            this.dout.write(b.array());
            this.dout.flush();
        }
        
        @Override
        public void run() {
            QuorumCnxManager.this.threadCnt.incrementAndGet();
            try {
                final ArrayBlockingQueue<ByteBuffer> bq = QuorumCnxManager.this.queueSendMap.get(this.sid);
                if (bq == null || QuorumCnxManager.this.isSendQueueEmpty(bq)) {
                    final ByteBuffer b = QuorumCnxManager.this.lastMessageSent.get(this.sid);
                    if (b != null) {
                        QuorumCnxManager.LOG.debug("Attempting to send lastMessage to sid=" + this.sid);
                        this.send(b);
                    }
                }
            }
            catch (IOException e) {
                QuorumCnxManager.LOG.error("Failed to send last message. Shutting down thread.", e);
                this.finish();
            }
            try {
                while (this.running && !QuorumCnxManager.this.shutdown && this.sock != null) {
                    ByteBuffer b2 = null;
                    try {
                        final ArrayBlockingQueue<ByteBuffer> bq2 = QuorumCnxManager.this.queueSendMap.get(this.sid);
                        if (bq2 == null) {
                            QuorumCnxManager.LOG.error("No queue of incoming messages for server " + this.sid);
                            break;
                        }
                        b2 = QuorumCnxManager.this.pollSendQueue(bq2, 1000L, TimeUnit.MILLISECONDS);
                        if (b2 == null) {
                            continue;
                        }
                        QuorumCnxManager.this.lastMessageSent.put(this.sid, b2);
                        this.send(b2);
                    }
                    catch (InterruptedException e2) {
                        QuorumCnxManager.LOG.warn("Interrupted while waiting for message on queue", e2);
                    }
                }
            }
            catch (Exception e3) {
                QuorumCnxManager.LOG.warn("Exception when using channel: for id " + this.sid + " my id = " + QuorumCnxManager.this.mySid + " error = " + e3);
            }
            this.finish();
            QuorumCnxManager.LOG.warn("Send worker leaving thread");
        }
    }
    
    class RecvWorker extends ZooKeeperThread
    {
        Long sid;
        Socket sock;
        volatile boolean running;
        final DataInputStream din;
        final SendWorker sw;
        
        RecvWorker(final Socket sock, final DataInputStream din, final Long sid, final SendWorker sw) {
            super("RecvWorker:" + sid);
            this.running = true;
            this.sid = sid;
            this.sock = sock;
            this.sw = sw;
            this.din = din;
            try {
                sock.setSoTimeout(0);
            }
            catch (IOException e) {
                QuorumCnxManager.LOG.error("Error while accessing socket for " + sid, e);
                QuorumCnxManager.this.closeSocket(sock);
                this.running = false;
            }
        }
        
        synchronized boolean finish() {
            if (!this.running) {
                return this.running;
            }
            this.running = false;
            this.interrupt();
            QuorumCnxManager.this.threadCnt.decrementAndGet();
            return this.running;
        }
        
        @Override
        public void run() {
            QuorumCnxManager.this.threadCnt.incrementAndGet();
            try {
                while (this.running && !QuorumCnxManager.this.shutdown && this.sock != null) {
                    final int length = this.din.readInt();
                    if (length <= 0 || length > 524288) {
                        throw new IOException("Received packet with invalid packet: " + length);
                    }
                    final byte[] msgArray = new byte[length];
                    this.din.readFully(msgArray, 0, length);
                    final ByteBuffer message = ByteBuffer.wrap(msgArray);
                    QuorumCnxManager.this.addToRecvQueue(new Message(message.duplicate(), this.sid));
                }
            }
            catch (Exception e) {
                QuorumCnxManager.LOG.warn("Connection broken for id " + this.sid + ", my id = " + QuorumCnxManager.this.mySid + ", error = ", e);
            }
            finally {
                QuorumCnxManager.LOG.warn("Interrupting SendWorker");
                this.sw.finish();
                if (this.sock != null) {
                    QuorumCnxManager.this.closeSocket(this.sock);
                }
            }
        }
    }
}
