// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.zookeeper.server.util.OSMXBean;
import org.apache.zookeeper.server.quorum.ProposalStats;
import org.apache.zookeeper.server.quorum.Leader;
import java.util.HashSet;
import org.apache.zookeeper.server.quorum.ReadOnlyZooKeeperServer;
import org.apache.zookeeper.Version;
import org.apache.zookeeper.server.quorum.LeaderZooKeeperServer;
import java.util.List;
import org.apache.zookeeper.Environment;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.proto.WatcherEvent;
import org.apache.zookeeper.WatchedEvent;
import java.io.OutputStream;
import org.apache.jute.BinaryOutputArchive;
import java.io.ByteArrayOutputStream;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.jute.BinaryInputArchive;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import org.apache.zookeeper.proto.RequestHeader;
import java.util.Iterator;
import java.nio.channels.CancelledKeyException;
import java.io.IOException;
import java.net.InetAddress;
import org.apache.zookeeper.data.Id;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;

public class NIOServerCnxn extends ServerCnxn
{
    static final Logger LOG;
    NIOServerCnxnFactory factory;
    final SocketChannel sock;
    protected final SelectionKey sk;
    boolean initialized;
    ByteBuffer lenBuffer;
    ByteBuffer incomingBuffer;
    LinkedBlockingQueue<ByteBuffer> outgoingBuffers;
    int sessionTimeout;
    protected final ZooKeeperServer zkServer;
    int outstandingRequests;
    long sessionId;
    static long nextSessionId;
    int outstandingLimit;
    private static final String ZK_NOT_SERVING = "This ZooKeeper instance is not currently serving requests";
    private static final byte[] fourBytes;
    
    public NIOServerCnxn(final ZooKeeperServer zk, final SocketChannel sock, final SelectionKey sk, final NIOServerCnxnFactory factory) throws IOException {
        this.lenBuffer = ByteBuffer.allocate(4);
        this.incomingBuffer = this.lenBuffer;
        this.outgoingBuffers = new LinkedBlockingQueue<ByteBuffer>();
        this.outstandingLimit = 1;
        this.zkServer = zk;
        this.sock = sock;
        this.sk = sk;
        this.factory = factory;
        if (this.factory.login != null) {
            this.zooKeeperSaslServer = new ZooKeeperSaslServer(factory.login);
        }
        if (zk != null) {
            this.outstandingLimit = zk.getGlobalOutstandingLimit();
        }
        sock.socket().setTcpNoDelay(true);
        sock.socket().setSoLinger(false, -1);
        final InetAddress addr = ((InetSocketAddress)sock.socket().getRemoteSocketAddress()).getAddress();
        this.authInfo.add(new Id("ip", addr.getHostAddress()));
        sk.interestOps(1);
    }
    
    public void sendCloseSession() {
        this.sendBuffer(ServerCnxnFactory.closeConn);
    }
    
    void sendBufferSync(final ByteBuffer bb) {
        try {
            this.sock.configureBlocking(true);
            if (bb != ServerCnxnFactory.closeConn) {
                if (this.sock.isOpen()) {
                    this.sock.write(bb);
                }
                this.packetSent();
            }
        }
        catch (IOException ie) {
            NIOServerCnxn.LOG.error("Error sending data synchronously ", ie);
        }
    }
    
    public void sendBuffer(final ByteBuffer bb) {
        try {
            this.internalSendBuffer(bb);
        }
        catch (Exception e) {
            NIOServerCnxn.LOG.error("Unexpected Exception: ", e);
        }
    }
    
    protected void internalSendBuffer(final ByteBuffer bb) {
        if (bb != ServerCnxnFactory.closeConn) {
            if (this.sk.isValid() && (this.sk.interestOps() & 0x4) == 0x0) {
                try {
                    this.sock.write(bb);
                }
                catch (IOException ex) {}
            }
            if (bb.remaining() == 0) {
                this.packetSent();
                return;
            }
        }
        synchronized (this.factory) {
            this.sk.selector().wakeup();
            if (NIOServerCnxn.LOG.isTraceEnabled()) {
                NIOServerCnxn.LOG.trace("Add a buffer to outgoingBuffers, sk " + this.sk + " is valid: " + this.sk.isValid());
            }
            this.outgoingBuffers.add(bb);
            if (this.sk.isValid()) {
                this.sk.interestOps(this.sk.interestOps() | 0x4);
            }
        }
    }
    
    private void readPayload() throws IOException, InterruptedException {
        if (this.incomingBuffer.remaining() != 0) {
            final int rc = this.sock.read(this.incomingBuffer);
            if (rc < 0) {
                throw new EndOfStreamException("Unable to read additional data from client sessionid 0x" + Long.toHexString(this.sessionId) + ", likely client has closed socket");
            }
        }
        if (this.incomingBuffer.remaining() == 0) {
            this.packetReceived();
            this.incomingBuffer.flip();
            if (!this.initialized) {
                this.readConnectRequest();
            }
            else {
                this.readRequest();
            }
            this.lenBuffer.clear();
            this.incomingBuffer = this.lenBuffer;
        }
    }
    
    protected boolean isSocketOpen() {
        return this.sock.isOpen();
    }
    
    @Override
    public InetAddress getSocketAddress() {
        if (this.sock == null) {
            return null;
        }
        return this.sock.socket().getInetAddress();
    }
    
    void doIO(final SelectionKey k) throws InterruptedException {
        try {
            if (!this.isSocketOpen()) {
                NIOServerCnxn.LOG.warn("trying to do i/o on a null socket for session:0x" + Long.toHexString(this.sessionId));
                return;
            }
            if (k.isReadable()) {
                final int rc = this.sock.read(this.incomingBuffer);
                if (rc < 0) {
                    throw new EndOfStreamException("Unable to read additional data from client sessionid 0x" + Long.toHexString(this.sessionId) + ", likely client has closed socket");
                }
                if (this.incomingBuffer.remaining() == 0) {
                    boolean isPayload;
                    if (this.incomingBuffer == this.lenBuffer) {
                        this.incomingBuffer.flip();
                        isPayload = this.readLength(k);
                        this.incomingBuffer.clear();
                    }
                    else {
                        isPayload = true;
                    }
                    if (!isPayload) {
                        return;
                    }
                    this.readPayload();
                }
            }
            if (k.isWritable()) {
                if (this.outgoingBuffers.size() > 0) {
                    final ByteBuffer directBuffer = this.factory.directBuffer;
                    directBuffer.clear();
                    for (ByteBuffer b : this.outgoingBuffers) {
                        if (directBuffer.remaining() < b.remaining()) {
                            b = (ByteBuffer)b.slice().limit(directBuffer.remaining());
                        }
                        final int p = b.position();
                        directBuffer.put(b);
                        b.position(p);
                        if (directBuffer.remaining() == 0) {
                            break;
                        }
                    }
                    directBuffer.flip();
                    int sent = this.sock.write(directBuffer);
                    while (this.outgoingBuffers.size() > 0) {
                        final ByteBuffer bb = this.outgoingBuffers.peek();
                        if (bb == ServerCnxnFactory.closeConn) {
                            throw new CloseRequestException("close requested");
                        }
                        final int left = bb.remaining() - sent;
                        if (left > 0) {
                            bb.position(bb.position() + sent);
                            break;
                        }
                        this.packetSent();
                        sent -= bb.remaining();
                        this.outgoingBuffers.remove();
                    }
                }
                synchronized (this.factory) {
                    if (this.outgoingBuffers.size() == 0) {
                        if (!this.initialized && (this.sk.interestOps() & 0x1) == 0x0) {
                            throw new CloseRequestException("responded to info probe");
                        }
                        this.sk.interestOps(this.sk.interestOps() & 0xFFFFFFFB);
                    }
                    else {
                        this.sk.interestOps(this.sk.interestOps() | 0x4);
                    }
                }
            }
        }
        catch (CancelledKeyException e) {
            NIOServerCnxn.LOG.warn("CancelledKeyException causing close of session 0x" + Long.toHexString(this.sessionId));
            if (NIOServerCnxn.LOG.isDebugEnabled()) {
                NIOServerCnxn.LOG.debug("CancelledKeyException stack trace", e);
            }
            this.close();
        }
        catch (CloseRequestException e4) {
            this.close();
        }
        catch (EndOfStreamException e2) {
            NIOServerCnxn.LOG.warn(e2.getMessage());
            if (NIOServerCnxn.LOG.isDebugEnabled()) {
                NIOServerCnxn.LOG.debug("EndOfStreamException stack trace", e2);
            }
            this.close();
        }
        catch (IOException e3) {
            NIOServerCnxn.LOG.warn("Exception causing close of session 0x" + Long.toHexString(this.sessionId) + ": " + e3.getMessage());
            if (NIOServerCnxn.LOG.isDebugEnabled()) {
                NIOServerCnxn.LOG.debug("IOException stack trace", e3);
            }
            this.close();
        }
    }
    
    private void readRequest() throws IOException {
        this.zkServer.processPacket(this, this.incomingBuffer);
    }
    
    @Override
    protected void incrOutstandingRequests(final RequestHeader h) {
        if (h.getXid() >= 0) {
            synchronized (this) {
                ++this.outstandingRequests;
            }
            synchronized (this.factory) {
                if (this.zkServer.getInProcess() > this.outstandingLimit) {
                    if (NIOServerCnxn.LOG.isDebugEnabled()) {
                        NIOServerCnxn.LOG.debug("Throttling recv " + this.zkServer.getInProcess());
                    }
                    this.disableRecv();
                }
            }
        }
    }
    
    public void disableRecv() {
        this.sk.interestOps(this.sk.interestOps() & 0xFFFFFFFE);
    }
    
    public void enableRecv() {
        synchronized (this.factory) {
            this.sk.selector().wakeup();
            if (this.sk.isValid()) {
                final int interest = this.sk.interestOps();
                if ((interest & 0x1) == 0x0) {
                    this.sk.interestOps(interest | 0x1);
                }
            }
        }
    }
    
    private void readConnectRequest() throws IOException, InterruptedException {
        if (!this.isZKServerRunning()) {
            throw new IOException("ZooKeeperServer not running");
        }
        this.zkServer.processConnectRequest(this, this.incomingBuffer);
        this.initialized = true;
    }
    
    private void cleanupWriterSocket(final PrintWriter pwriter) {
        try {
            if (pwriter != null) {
                pwriter.flush();
                pwriter.close();
            }
        }
        catch (Exception e) {
            NIOServerCnxn.LOG.info("Error closing PrintWriter ", e);
            try {
                this.close();
            }
            catch (Exception e) {
                NIOServerCnxn.LOG.error("Error closing a command socket ", e);
            }
        }
        finally {
            try {
                this.close();
            }
            catch (Exception e2) {
                NIOServerCnxn.LOG.error("Error closing a command socket ", e2);
            }
        }
    }
    
    private boolean checkFourLetterWord(final SelectionKey k, final int len) throws IOException {
        if (!ServerCnxn.isKnown(len)) {
            return false;
        }
        this.packetReceived();
        if (k != null) {
            try {
                k.cancel();
            }
            catch (Exception e) {
                NIOServerCnxn.LOG.error("Error cancelling command selection key ", e);
            }
        }
        final PrintWriter pwriter = new PrintWriter(new BufferedWriter(new SendBufferWriter()));
        final String cmd = ServerCnxn.getCommandString(len);
        if (!ServerCnxn.isEnabled(cmd)) {
            NIOServerCnxn.LOG.debug("Command {} is not executed because it is not in the whitelist.", cmd);
            final NopCommand nopCmd = new NopCommand(pwriter, cmd + " is not executed because it is not in the whitelist.");
            nopCmd.start();
            return true;
        }
        NIOServerCnxn.LOG.info("Processing " + cmd + " command from " + this.sock.socket().getRemoteSocketAddress());
        if (len == NIOServerCnxn.ruokCmd) {
            final RuokCommand ruok = new RuokCommand(pwriter);
            ruok.start();
            return true;
        }
        if (len == NIOServerCnxn.getTraceMaskCmd) {
            final TraceMaskCommand tmask = new TraceMaskCommand(pwriter);
            tmask.start();
            return true;
        }
        if (len == NIOServerCnxn.setTraceMaskCmd) {
            this.incomingBuffer = ByteBuffer.allocate(8);
            final int rc = this.sock.read(this.incomingBuffer);
            if (rc < 0) {
                throw new IOException("Read error");
            }
            this.incomingBuffer.flip();
            final long traceMask = this.incomingBuffer.getLong();
            ZooTrace.setTextTraceLevel(traceMask);
            final SetTraceMaskCommand setMask = new SetTraceMaskCommand(pwriter, traceMask);
            setMask.start();
            return true;
        }
        else {
            if (len == NIOServerCnxn.enviCmd) {
                final EnvCommand env = new EnvCommand(pwriter);
                env.start();
                return true;
            }
            if (len == NIOServerCnxn.confCmd) {
                final ConfCommand ccmd = new ConfCommand(pwriter);
                ccmd.start();
                return true;
            }
            if (len == NIOServerCnxn.srstCmd) {
                final StatResetCommand strst = new StatResetCommand(pwriter);
                strst.start();
                return true;
            }
            if (len == NIOServerCnxn.crstCmd) {
                final CnxnStatResetCommand crst = new CnxnStatResetCommand(pwriter);
                crst.start();
                return true;
            }
            if (len == NIOServerCnxn.dumpCmd) {
                final DumpCommand dump = new DumpCommand(pwriter);
                dump.start();
                return true;
            }
            if (len == NIOServerCnxn.statCmd || len == NIOServerCnxn.srvrCmd) {
                final StatCommand stat = new StatCommand(pwriter, len);
                stat.start();
                return true;
            }
            if (len == NIOServerCnxn.consCmd) {
                final ConsCommand cons = new ConsCommand(pwriter);
                cons.start();
                return true;
            }
            if (len == NIOServerCnxn.wchpCmd || len == NIOServerCnxn.wchcCmd || len == NIOServerCnxn.wchsCmd) {
                final WatchCommand wcmd = new WatchCommand(pwriter, len);
                wcmd.start();
                return true;
            }
            if (len == NIOServerCnxn.mntrCmd) {
                final MonitorCommand mntr = new MonitorCommand(pwriter);
                mntr.start();
                return true;
            }
            if (len == NIOServerCnxn.isroCmd) {
                final IsroCommand isro = new IsroCommand(pwriter);
                isro.start();
                return true;
            }
            return false;
        }
    }
    
    private boolean readLength(final SelectionKey k) throws IOException {
        final int len = this.lenBuffer.getInt();
        if (!this.initialized && this.checkFourLetterWord(this.sk, len)) {
            return false;
        }
        if (len < 0 || len > BinaryInputArchive.maxBuffer) {
            throw new IOException("Len error " + len);
        }
        if (!this.isZKServerRunning()) {
            throw new IOException("ZooKeeperServer not running");
        }
        this.incomingBuffer = ByteBuffer.allocate(len);
        return true;
    }
    
    @Override
    public long getOutstandingRequests() {
        synchronized (this) {
            synchronized (this.factory) {
                return this.outstandingRequests;
            }
        }
    }
    
    public int getSessionTimeout() {
        return this.sessionTimeout;
    }
    
    @Override
    public String toString() {
        return "NIOServerCnxn object with sock = " + this.sock + " and sk = " + this.sk;
    }
    
    public void close() {
        this.factory.removeCnxn(this);
        if (this.zkServer != null) {
            this.zkServer.removeCnxn(this);
        }
        this.closeSock();
        if (this.sk != null) {
            try {
                this.sk.cancel();
            }
            catch (Exception e) {
                if (NIOServerCnxn.LOG.isDebugEnabled()) {
                    NIOServerCnxn.LOG.debug("ignoring exception during selectionkey cancel", e);
                }
            }
        }
    }
    
    private void closeSock() {
        if (!this.sock.isOpen()) {
            return;
        }
        NIOServerCnxn.LOG.info("Closed socket connection for client " + this.sock.socket().getRemoteSocketAddress() + ((this.sessionId != 0L) ? (" which had sessionid 0x" + Long.toHexString(this.sessionId)) : " (no session established for client)"));
        try {
            this.sock.socket().shutdownOutput();
        }
        catch (IOException e) {
            if (NIOServerCnxn.LOG.isDebugEnabled()) {
                NIOServerCnxn.LOG.debug("ignoring exception during output shutdown", e);
            }
        }
        try {
            this.sock.socket().shutdownInput();
        }
        catch (IOException e) {
            if (NIOServerCnxn.LOG.isDebugEnabled()) {
                NIOServerCnxn.LOG.debug("ignoring exception during input shutdown", e);
            }
        }
        try {
            this.sock.socket().close();
        }
        catch (IOException e) {
            if (NIOServerCnxn.LOG.isDebugEnabled()) {
                NIOServerCnxn.LOG.debug("ignoring exception during socket close", e);
            }
        }
        try {
            this.sock.close();
        }
        catch (IOException e) {
            if (NIOServerCnxn.LOG.isDebugEnabled()) {
                NIOServerCnxn.LOG.debug("ignoring exception during socketchannel close", e);
            }
        }
    }
    
    @Override
    public synchronized void sendResponse(final ReplyHeader h, final Record r, final String tag) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final BinaryOutputArchive bos = BinaryOutputArchive.getArchive(baos);
            try {
                baos.write(NIOServerCnxn.fourBytes);
                bos.writeRecord(h, "header");
                if (r != null) {
                    bos.writeRecord(r, tag);
                }
                baos.close();
            }
            catch (IOException e2) {
                NIOServerCnxn.LOG.error("Error serializing response");
            }
            final byte[] b = baos.toByteArray();
            final ByteBuffer bb = ByteBuffer.wrap(b);
            bb.putInt(b.length - 4).rewind();
            this.sendBuffer(bb);
            if (h.getXid() > 0) {
                synchronized (this) {
                    --this.outstandingRequests;
                }
                synchronized (this.factory) {
                    if (this.zkServer.getInProcess() < this.outstandingLimit || this.outstandingRequests < 1) {
                        this.sk.selector().wakeup();
                        this.enableRecv();
                    }
                }
            }
        }
        catch (Exception e) {
            NIOServerCnxn.LOG.warn("Unexpected exception. Destruction averted.", e);
        }
    }
    
    @Override
    public synchronized void process(final WatchedEvent event) {
        final ReplyHeader h = new ReplyHeader(-1, -1L, 0);
        if (NIOServerCnxn.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(NIOServerCnxn.LOG, 64L, "Deliver event " + event + " to 0x" + Long.toHexString(this.sessionId) + " through " + this);
        }
        final WatcherEvent e = event.getWrapper();
        this.sendResponse(h, e, "notification");
    }
    
    public long getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final long sessionId) {
        this.sessionId = sessionId;
        this.factory.addSession(sessionId, this);
    }
    
    public void setSessionTimeout(final int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    @Override
    public int getInterestOps() {
        return this.sk.isValid() ? this.sk.interestOps() : 0;
    }
    
    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        if (!this.sock.isOpen()) {
            return null;
        }
        return (InetSocketAddress)this.sock.socket().getRemoteSocketAddress();
    }
    
    @Override
    protected ServerStats serverStats() {
        if (!this.isZKServerRunning()) {
            return null;
        }
        return this.zkServer.serverStats();
    }
    
    boolean isZKServerRunning() {
        return this.zkServer != null && this.zkServer.isRunning();
    }
    
    static {
        LOG = LoggerFactory.getLogger(NIOServerCnxn.class);
        NIOServerCnxn.nextSessionId = 1L;
        fourBytes = new byte[4];
    }
    
    private class SendBufferWriter extends Writer
    {
        private StringBuffer sb;
        
        private SendBufferWriter() {
            this.sb = new StringBuffer();
        }
        
        private void checkFlush(final boolean force) {
            if ((force && this.sb.length() > 0) || this.sb.length() > 2048) {
                NIOServerCnxn.this.sendBufferSync(ByteBuffer.wrap(this.sb.toString().getBytes()));
                this.sb.setLength(0);
            }
        }
        
        @Override
        public void close() throws IOException {
            if (this.sb == null) {
                return;
            }
            this.checkFlush(true);
            this.sb = null;
        }
        
        @Override
        public void flush() throws IOException {
            this.checkFlush(true);
        }
        
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            this.sb.append(cbuf, off, len);
            this.checkFlush(false);
        }
    }
    
    private abstract class CommandThread extends Thread
    {
        PrintWriter pw;
        
        CommandThread(final PrintWriter pw) {
            this.pw = pw;
        }
        
        @Override
        public void run() {
            try {
                this.commandRun();
            }
            catch (IOException ie) {
                NIOServerCnxn.LOG.error("Error in running command ", ie);
            }
            finally {
                NIOServerCnxn.this.cleanupWriterSocket(this.pw);
            }
        }
        
        public abstract void commandRun() throws IOException;
    }
    
    private class RuokCommand extends CommandThread
    {
        public RuokCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            this.pw.print("imok");
        }
    }
    
    private class TraceMaskCommand extends CommandThread
    {
        TraceMaskCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            final long traceMask = ZooTrace.getTextTraceLevel();
            this.pw.print(traceMask);
        }
    }
    
    private class SetTraceMaskCommand extends CommandThread
    {
        long trace;
        
        SetTraceMaskCommand(final PrintWriter pw, final long trace) {
            super(pw);
            this.trace = 0L;
            this.trace = trace;
        }
        
        @Override
        public void commandRun() {
            this.pw.print(this.trace);
        }
    }
    
    private class EnvCommand extends CommandThread
    {
        EnvCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            final List<Environment.Entry> env = Environment.list();
            this.pw.println("Environment:");
            for (final Environment.Entry e : env) {
                this.pw.print(e.getKey());
                this.pw.print("=");
                this.pw.println(e.getValue());
            }
        }
    }
    
    private class ConfCommand extends CommandThread
    {
        ConfCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                NIOServerCnxn.this.zkServer.dumpConf(this.pw);
            }
        }
    }
    
    private class StatResetCommand extends CommandThread
    {
        public StatResetCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                final ServerStats serverStats = NIOServerCnxn.this.zkServer.serverStats();
                serverStats.reset();
                if (serverStats.getServerState().equals("leader")) {
                    ((LeaderZooKeeperServer)NIOServerCnxn.this.zkServer).getLeader().getProposalStats().reset();
                }
                this.pw.println("Server stats reset.");
            }
        }
    }
    
    private class CnxnStatResetCommand extends CommandThread
    {
        public CnxnStatResetCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                synchronized (NIOServerCnxn.this.factory.cnxns) {
                    for (final ServerCnxn c : NIOServerCnxn.this.factory.cnxns) {
                        c.resetStats();
                    }
                }
                this.pw.println("Connection stats reset.");
            }
        }
    }
    
    private class DumpCommand extends CommandThread
    {
        public DumpCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                this.pw.println("SessionTracker dump:");
                NIOServerCnxn.this.zkServer.sessionTracker.dumpSessions(this.pw);
                this.pw.println("ephemeral nodes dump:");
                NIOServerCnxn.this.zkServer.dumpEphemerals(this.pw);
            }
        }
    }
    
    private class StatCommand extends CommandThread
    {
        int len;
        
        public StatCommand(final PrintWriter pw, final int len) {
            super(pw);
            this.len = len;
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                this.pw.print("Zookeeper version: ");
                this.pw.println(Version.getFullVersion());
                if (NIOServerCnxn.this.zkServer instanceof ReadOnlyZooKeeperServer) {
                    this.pw.println("READ-ONLY mode; serving only read-only clients");
                }
                if (this.len == ServerCnxn.statCmd) {
                    NIOServerCnxn.LOG.info("Stat command output");
                    this.pw.println("Clients:");
                    final HashSet<NIOServerCnxn> cnxnset;
                    synchronized (NIOServerCnxn.this.factory.cnxns) {
                        cnxnset = (HashSet<NIOServerCnxn>)NIOServerCnxn.this.factory.cnxns.clone();
                    }
                    for (final NIOServerCnxn c : cnxnset) {
                        c.dumpConnectionInfo(this.pw, true);
                        this.pw.println();
                    }
                    this.pw.println();
                }
                final ServerStats serverStats = NIOServerCnxn.this.zkServer.serverStats();
                this.pw.print(serverStats.toString());
                this.pw.print("Node count: ");
                this.pw.println(NIOServerCnxn.this.zkServer.getZKDatabase().getNodeCount());
                if (serverStats.getServerState().equals("leader")) {
                    final Leader leader = ((LeaderZooKeeperServer)NIOServerCnxn.this.zkServer).getLeader();
                    final ProposalStats proposalStats = leader.getProposalStats();
                    this.pw.printf("Proposal sizes last/min/max: %s%n", proposalStats.toString());
                }
            }
        }
    }
    
    private class ConsCommand extends CommandThread
    {
        public ConsCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                final HashSet<NIOServerCnxn> cnxns;
                synchronized (NIOServerCnxn.this.factory.cnxns) {
                    cnxns = (HashSet<NIOServerCnxn>)NIOServerCnxn.this.factory.cnxns.clone();
                }
                for (final NIOServerCnxn c : cnxns) {
                    c.dumpConnectionInfo(this.pw, false);
                    this.pw.println();
                }
                this.pw.println();
            }
        }
    }
    
    private class WatchCommand extends CommandThread
    {
        int len;
        
        public WatchCommand(final PrintWriter pw, final int len) {
            super(pw);
            this.len = 0;
            this.len = len;
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                final DataTree dt = NIOServerCnxn.this.zkServer.getZKDatabase().getDataTree();
                if (this.len == ServerCnxn.wchsCmd) {
                    dt.dumpWatchesSummary(this.pw);
                }
                else if (this.len == ServerCnxn.wchpCmd) {
                    dt.dumpWatches(this.pw, true);
                }
                else {
                    dt.dumpWatches(this.pw, false);
                }
                this.pw.println();
            }
        }
    }
    
    private class MonitorCommand extends CommandThread
    {
        MonitorCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
                return;
            }
            final ZKDatabase zkdb = NIOServerCnxn.this.zkServer.getZKDatabase();
            final ServerStats stats = NIOServerCnxn.this.zkServer.serverStats();
            this.print("version", Version.getFullVersion());
            this.print("avg_latency", stats.getAvgLatency());
            this.print("max_latency", stats.getMaxLatency());
            this.print("min_latency", stats.getMinLatency());
            this.print("packets_received", stats.getPacketsReceived());
            this.print("packets_sent", stats.getPacketsSent());
            this.print("num_alive_connections", stats.getNumAliveClientConnections());
            this.print("outstanding_requests", stats.getOutstandingRequests());
            this.print("server_state", stats.getServerState());
            this.print("znode_count", zkdb.getNodeCount());
            this.print("watch_count", zkdb.getDataTree().getWatchCount());
            this.print("ephemerals_count", zkdb.getDataTree().getEphemeralsCount());
            this.print("approximate_data_size", zkdb.getDataTree().approximateDataSize());
            final OSMXBean osMbean = new OSMXBean();
            if (osMbean != null && osMbean.getUnix()) {
                this.print("open_file_descriptor_count", osMbean.getOpenFileDescriptorCount());
                this.print("max_file_descriptor_count", osMbean.getMaxFileDescriptorCount());
            }
            this.print("fsync_threshold_exceed_count", stats.getFsyncThresholdExceedCount());
            if (stats.getServerState().equals("leader")) {
                final Leader leader = ((LeaderZooKeeperServer)NIOServerCnxn.this.zkServer).getLeader();
                this.print("followers", leader.getLearners().size());
                this.print("synced_followers", leader.getForwardingFollowers().size());
                this.print("pending_syncs", leader.getNumPendingSyncs());
                this.print("last_proposal_size", leader.getProposalStats().getLastProposalSize());
                this.print("max_proposal_size", leader.getProposalStats().getMaxProposalSize());
                this.print("min_proposal_size", leader.getProposalStats().getMinProposalSize());
            }
        }
        
        private void print(final String key, final long number) {
            this.print(key, "" + number);
        }
        
        private void print(final String key, final String value) {
            this.pw.print("zk_");
            this.pw.print(key);
            this.pw.print("\t");
            this.pw.println(value);
        }
    }
    
    private class IsroCommand extends CommandThread
    {
        public IsroCommand(final PrintWriter pw) {
            super(pw);
        }
        
        @Override
        public void commandRun() {
            if (!NIOServerCnxn.this.isZKServerRunning()) {
                this.pw.print("null");
            }
            else if (NIOServerCnxn.this.zkServer instanceof ReadOnlyZooKeeperServer) {
                this.pw.print("ro");
            }
            else {
                this.pw.print("rw");
            }
        }
    }
    
    private class NopCommand extends CommandThread
    {
        private String msg;
        
        public NopCommand(final PrintWriter pw, final String msg) {
            super(pw);
            this.msg = msg;
        }
        
        @Override
        public void commandRun() {
            this.pw.println(this.msg);
        }
    }
}
