// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.zookeeper.server.util.OSMXBean;
import java.util.AbstractSet;
import org.apache.zookeeper.server.quorum.ProposalStats;
import org.apache.zookeeper.server.quorum.Leader;
import java.util.Collection;
import java.util.HashSet;
import org.apache.zookeeper.server.quorum.ReadOnlyZooKeeperServer;
import org.apache.zookeeper.Version;
import org.apache.zookeeper.server.quorum.LeaderZooKeeperServer;
import java.util.Iterator;
import java.util.List;
import org.apache.zookeeper.Environment;
import java.net.SocketAddress;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.apache.jute.BinaryInputArchive;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import java.io.OutputStream;
import org.apache.jute.BinaryOutputArchive;
import java.io.ByteArrayOutputStream;
import org.apache.zookeeper.proto.WatcherEvent;
import java.io.IOException;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;

public class NettyServerCnxn extends ServerCnxn
{
    Logger LOG;
    Channel channel;
    ChannelBuffer queuedBuffer;
    volatile boolean throttled;
    ByteBuffer bb;
    ByteBuffer bbLen;
    long sessionId;
    int sessionTimeout;
    AtomicLong outstandingCount;
    private volatile ZooKeeperServer zkServer;
    NettyServerCnxnFactory factory;
    boolean initialized;
    private static final byte[] fourBytes;
    private static final String ZK_NOT_SERVING = "This ZooKeeper instance is not currently serving requests";
    
    NettyServerCnxn(final Channel channel, final ZooKeeperServer zks, final NettyServerCnxnFactory factory) {
        this.LOG = LoggerFactory.getLogger(NettyServerCnxn.class);
        this.bbLen = ByteBuffer.allocate(4);
        this.outstandingCount = new AtomicLong();
        this.channel = channel;
        this.zkServer = zks;
        this.factory = factory;
        if (this.factory.login != null) {
            this.zooKeeperSaslServer = new ZooKeeperSaslServer(factory.login);
        }
    }
    
    public void close() {
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("close called for sessionid:0x" + Long.toHexString(this.sessionId));
        }
        this.factory.unregisterConnection(this);
        this.factory.removeCnxn(this);
        if (this.channel.isOpen()) {
            this.channel.close();
        }
    }
    
    public long getSessionId() {
        return this.sessionId;
    }
    
    public int getSessionTimeout() {
        return this.sessionTimeout;
    }
    
    @Override
    public void process(final WatchedEvent event) {
        final ReplyHeader h = new ReplyHeader(-1, -1L, 0);
        if (this.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(this.LOG, 64L, "Deliver event " + event + " to 0x" + Long.toHexString(this.sessionId) + " through " + this);
        }
        final WatcherEvent e = event.getWrapper();
        try {
            this.sendResponse(h, e, "notification");
        }
        catch (IOException e2) {
            if (this.LOG.isDebugEnabled()) {
                this.LOG.debug("Problem sending to " + this.getRemoteSocketAddress(), e2);
            }
            this.close();
        }
    }
    
    @Override
    public void sendResponse(final ReplyHeader h, final Record r, final String tag) throws IOException {
        if (!this.channel.isOpen()) {
            return;
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final BinaryOutputArchive bos = BinaryOutputArchive.getArchive(baos);
        try {
            baos.write(NettyServerCnxn.fourBytes);
            bos.writeRecord(h, "header");
            if (r != null) {
                bos.writeRecord(r, tag);
            }
            baos.close();
        }
        catch (IOException e) {
            this.LOG.error("Error serializing response");
        }
        final byte[] b = baos.toByteArray();
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.putInt(b.length - 4).rewind();
        this.sendBuffer(bb);
        if (h.getXid() > 0 && !this.zkServer.shouldThrottle(this.outstandingCount.decrementAndGet())) {
            this.enableRecv();
        }
    }
    
    public void setSessionId(final long sessionId) {
        this.sessionId = sessionId;
        this.factory.addSession(sessionId, this);
    }
    
    public void enableRecv() {
        if (this.throttled) {
            this.throttled = false;
            if (this.LOG.isDebugEnabled()) {
                this.LOG.debug("Sending unthrottle event " + this);
            }
            this.channel.getPipeline().sendUpstream(new ResumeMessageEvent(this.channel));
        }
    }
    
    public void sendBuffer(final ByteBuffer sendBuffer) {
        if (sendBuffer == ServerCnxnFactory.closeConn) {
            this.close();
            return;
        }
        this.channel.write(ChannelBuffers.wrappedBuffer(sendBuffer));
        this.packetSent();
    }
    
    @Override
    public InetAddress getSocketAddress() {
        if (this.channel == null) {
            return null;
        }
        return ((InetSocketAddress)this.channel.getRemoteAddress()).getAddress();
    }
    
    private void cleanupWriterSocket(final PrintWriter pwriter) {
        try {
            if (pwriter != null) {
                pwriter.flush();
                pwriter.close();
            }
        }
        catch (Exception e) {
            this.LOG.info("Error closing PrintWriter ", e);
            try {
                this.close();
            }
            catch (Exception e) {
                this.LOG.error("Error closing a command socket ", e);
            }
        }
        finally {
            try {
                this.close();
            }
            catch (Exception e2) {
                this.LOG.error("Error closing a command socket ", e2);
            }
        }
    }
    
    private boolean checkFourLetterWord(final Channel channel, final ChannelBuffer message, final int len) throws IOException {
        if (!ServerCnxn.isKnown(len)) {
            return false;
        }
        channel.setInterestOps(0).awaitUninterruptibly();
        this.packetReceived();
        final PrintWriter pwriter = new PrintWriter(new BufferedWriter(new SendBufferWriter()));
        final String cmd = ServerCnxn.getCommandString(len);
        if (!ServerCnxn.isEnabled(cmd)) {
            this.LOG.debug("Command {} is not executed because it is not in the whitelist.", cmd);
            final NopCommand nopCmd = new NopCommand(pwriter, cmd + " is not executed because it is not in the whitelist.");
            nopCmd.start();
            return true;
        }
        this.LOG.info("Processing " + cmd + " command from " + channel.getRemoteAddress());
        if (len == NettyServerCnxn.ruokCmd) {
            final RuokCommand ruok = new RuokCommand(pwriter);
            ruok.start();
            return true;
        }
        if (len == NettyServerCnxn.getTraceMaskCmd) {
            final TraceMaskCommand tmask = new TraceMaskCommand(pwriter);
            tmask.start();
            return true;
        }
        if (len == NettyServerCnxn.setTraceMaskCmd) {
            final ByteBuffer mask = ByteBuffer.allocate(8);
            message.readBytes(mask);
            mask.flip();
            final long traceMask = mask.getLong();
            ZooTrace.setTextTraceLevel(traceMask);
            final SetTraceMaskCommand setMask = new SetTraceMaskCommand(pwriter, traceMask);
            setMask.start();
            return true;
        }
        if (len == NettyServerCnxn.enviCmd) {
            final EnvCommand env = new EnvCommand(pwriter);
            env.start();
            return true;
        }
        if (len == NettyServerCnxn.confCmd) {
            final ConfCommand ccmd = new ConfCommand(pwriter);
            ccmd.start();
            return true;
        }
        if (len == NettyServerCnxn.srstCmd) {
            final StatResetCommand strst = new StatResetCommand(pwriter);
            strst.start();
            return true;
        }
        if (len == NettyServerCnxn.crstCmd) {
            final CnxnStatResetCommand crst = new CnxnStatResetCommand(pwriter);
            crst.start();
            return true;
        }
        if (len == NettyServerCnxn.dumpCmd) {
            final DumpCommand dump = new DumpCommand(pwriter);
            dump.start();
            return true;
        }
        if (len == NettyServerCnxn.statCmd || len == NettyServerCnxn.srvrCmd) {
            final StatCommand stat = new StatCommand(pwriter, len);
            stat.start();
            return true;
        }
        if (len == NettyServerCnxn.consCmd) {
            final ConsCommand cons = new ConsCommand(pwriter);
            cons.start();
            return true;
        }
        if (len == NettyServerCnxn.wchpCmd || len == NettyServerCnxn.wchcCmd || len == NettyServerCnxn.wchsCmd) {
            final WatchCommand wcmd = new WatchCommand(pwriter, len);
            wcmd.start();
            return true;
        }
        if (len == NettyServerCnxn.mntrCmd) {
            final MonitorCommand mntr = new MonitorCommand(pwriter);
            mntr.start();
            return true;
        }
        if (len == NettyServerCnxn.isroCmd) {
            final IsroCommand isro = new IsroCommand(pwriter);
            isro.start();
            return true;
        }
        return false;
    }
    
    public void receiveMessage(final ChannelBuffer message) {
        try {
            while (message.readable() && !this.throttled) {
                if (this.bb != null) {
                    if (this.LOG.isTraceEnabled()) {
                        this.LOG.trace("message readable " + message.readableBytes() + " bb len " + this.bb.remaining() + " " + this.bb);
                        final ByteBuffer dat = this.bb.duplicate();
                        dat.flip();
                        this.LOG.trace(Long.toHexString(this.sessionId) + " bb 0x" + ChannelBuffers.hexDump(ChannelBuffers.copiedBuffer(dat)));
                    }
                    if (this.bb.remaining() > message.readableBytes()) {
                        final int newLimit = this.bb.position() + message.readableBytes();
                        this.bb.limit(newLimit);
                    }
                    message.readBytes(this.bb);
                    this.bb.limit(this.bb.capacity());
                    if (this.LOG.isTraceEnabled()) {
                        this.LOG.trace("after readBytes message readable " + message.readableBytes() + " bb len " + this.bb.remaining() + " " + this.bb);
                        final ByteBuffer dat = this.bb.duplicate();
                        dat.flip();
                        this.LOG.trace("after readbytes " + Long.toHexString(this.sessionId) + " bb 0x" + ChannelBuffers.hexDump(ChannelBuffers.copiedBuffer(dat)));
                    }
                    if (this.bb.remaining() != 0) {
                        continue;
                    }
                    this.packetReceived();
                    this.bb.flip();
                    final ZooKeeperServer zks = this.zkServer;
                    if (zks == null || !zks.isRunning()) {
                        throw new IOException("ZK down");
                    }
                    if (this.initialized) {
                        zks.processPacket(this, this.bb);
                        if (zks.shouldThrottle(this.outstandingCount.incrementAndGet())) {
                            this.disableRecvNoWait();
                        }
                    }
                    else {
                        this.LOG.debug("got conn req request from " + this.getRemoteSocketAddress());
                        zks.processConnectRequest(this, this.bb);
                        this.initialized = true;
                    }
                    this.bb = null;
                }
                else {
                    if (this.LOG.isTraceEnabled()) {
                        this.LOG.trace("message readable " + message.readableBytes() + " bblenrem " + this.bbLen.remaining());
                        final ByteBuffer dat = this.bbLen.duplicate();
                        dat.flip();
                        this.LOG.trace(Long.toHexString(this.sessionId) + " bbLen 0x" + ChannelBuffers.hexDump(ChannelBuffers.copiedBuffer(dat)));
                    }
                    if (message.readableBytes() < this.bbLen.remaining()) {
                        this.bbLen.limit(this.bbLen.position() + message.readableBytes());
                    }
                    message.readBytes(this.bbLen);
                    this.bbLen.limit(this.bbLen.capacity());
                    if (this.bbLen.remaining() != 0) {
                        continue;
                    }
                    this.bbLen.flip();
                    if (this.LOG.isTraceEnabled()) {
                        this.LOG.trace(Long.toHexString(this.sessionId) + " bbLen 0x" + ChannelBuffers.hexDump(ChannelBuffers.copiedBuffer(this.bbLen)));
                    }
                    final int len = this.bbLen.getInt();
                    if (this.LOG.isTraceEnabled()) {
                        this.LOG.trace(Long.toHexString(this.sessionId) + " bbLen len is " + len);
                    }
                    this.bbLen.clear();
                    if (!this.initialized && this.checkFourLetterWord(this.channel, message, len)) {
                        return;
                    }
                    if (len < 0 || len > BinaryInputArchive.maxBuffer) {
                        throw new IOException("Len error " + len);
                    }
                    this.bb = ByteBuffer.allocate(len);
                }
            }
        }
        catch (IOException e) {
            this.LOG.warn("Closing connection to " + this.getRemoteSocketAddress(), e);
            this.close();
        }
    }
    
    public void disableRecv() {
        this.disableRecvNoWait().awaitUninterruptibly();
    }
    
    private ChannelFuture disableRecvNoWait() {
        this.throttled = true;
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("Throttling - disabling recv " + this);
        }
        return this.channel.setReadable(false);
    }
    
    @Override
    public long getOutstandingRequests() {
        return this.outstandingCount.longValue();
    }
    
    public void setSessionTimeout(final int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    @Override
    public int getInterestOps() {
        return this.channel.getInterestOps();
    }
    
    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress)this.channel.getRemoteAddress();
    }
    
    public void sendCloseSession() {
        this.sendBuffer(ServerCnxnFactory.closeConn);
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
        fourBytes = new byte[4];
    }
    
    static class ResumeMessageEvent implements MessageEvent
    {
        Channel channel;
        
        ResumeMessageEvent(final Channel channel) {
            this.channel = channel;
        }
        
        @Override
        public Object getMessage() {
            return null;
        }
        
        @Override
        public SocketAddress getRemoteAddress() {
            return null;
        }
        
        @Override
        public Channel getChannel() {
            return this.channel;
        }
        
        @Override
        public ChannelFuture getFuture() {
            return null;
        }
    }
    
    private class SendBufferWriter extends Writer
    {
        private StringBuffer sb;
        
        private SendBufferWriter() {
            this.sb = new StringBuffer();
        }
        
        private void checkFlush(final boolean force) {
            if ((force && this.sb.length() > 0) || this.sb.length() > 2048) {
                NettyServerCnxn.this.sendBuffer(ByteBuffer.wrap(this.sb.toString().getBytes()));
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
    
    private abstract class CommandThread
    {
        PrintWriter pw;
        
        CommandThread(final PrintWriter pw) {
            this.pw = pw;
        }
        
        public void start() {
            this.run();
        }
        
        public void run() {
            try {
                this.commandRun();
            }
            catch (IOException ie) {
                NettyServerCnxn.this.LOG.error("Error in running command ", ie);
            }
            finally {
                NettyServerCnxn.this.cleanupWriterSocket(this.pw);
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                NettyServerCnxn.this.zkServer.dumpConf(this.pw);
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                final ServerStats serverStats = NettyServerCnxn.this.zkServer.serverStats();
                serverStats.reset();
                if (serverStats.getServerState().equals("leader")) {
                    ((LeaderZooKeeperServer)NettyServerCnxn.this.zkServer).getLeader().getProposalStats().reset();
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                synchronized (NettyServerCnxn.this.factory.cnxns) {
                    for (final ServerCnxn c : NettyServerCnxn.this.factory.cnxns) {
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                this.pw.println("SessionTracker dump:");
                NettyServerCnxn.this.zkServer.sessionTracker.dumpSessions(this.pw);
                this.pw.println("ephemeral nodes dump:");
                NettyServerCnxn.this.zkServer.dumpEphemerals(this.pw);
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                this.pw.print("Zookeeper version: ");
                this.pw.println(Version.getFullVersion());
                if (NettyServerCnxn.this.zkServer instanceof ReadOnlyZooKeeperServer) {
                    this.pw.println("READ-ONLY mode; serving only read-only clients");
                }
                if (this.len == ServerCnxn.statCmd) {
                    NettyServerCnxn.this.LOG.info("Stat command output");
                    this.pw.println("Clients:");
                    final HashSet<ServerCnxn> cnxns;
                    synchronized (NettyServerCnxn.this.factory.cnxns) {
                        cnxns = new HashSet<ServerCnxn>(NettyServerCnxn.this.factory.cnxns);
                    }
                    for (final ServerCnxn c : cnxns) {
                        c.dumpConnectionInfo(this.pw, true);
                        this.pw.println();
                    }
                    this.pw.println();
                }
                final ServerStats serverStats = NettyServerCnxn.this.zkServer.serverStats();
                this.pw.print(serverStats.toString());
                this.pw.print("Node count: ");
                this.pw.println(NettyServerCnxn.this.zkServer.getZKDatabase().getNodeCount());
                if (serverStats.getServerState().equals("leader")) {
                    final Leader leader = ((LeaderZooKeeperServer)NettyServerCnxn.this.zkServer).getLeader();
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                final AbstractSet<ServerCnxn> cnxns;
                synchronized (NettyServerCnxn.this.factory.cnxns) {
                    cnxns = new HashSet<ServerCnxn>(NettyServerCnxn.this.factory.cnxns);
                }
                for (final ServerCnxn c : cnxns) {
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
            }
            else {
                final DataTree dt = NettyServerCnxn.this.zkServer.getZKDatabase().getDataTree();
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.println("This ZooKeeper instance is not currently serving requests");
                return;
            }
            final ZKDatabase zkdb = NettyServerCnxn.this.zkServer.getZKDatabase();
            final ServerStats stats = NettyServerCnxn.this.zkServer.serverStats();
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
                final Leader leader = ((LeaderZooKeeperServer)NettyServerCnxn.this.zkServer).getLeader();
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
            if (!NettyServerCnxn.this.isZKServerRunning()) {
                this.pw.print("null");
            }
            else if (NettyServerCnxn.this.zkServer instanceof ReadOnlyZooKeeperServer) {
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
