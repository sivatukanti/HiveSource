// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.util.HashSet;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.net.InetSocketAddress;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.zookeeper.proto.RequestHeader;
import java.util.Iterator;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import org.apache.zookeeper.WatchedEvent;
import java.io.IOException;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.ReplyHeader;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Date;
import java.util.Set;
import org.slf4j.Logger;
import java.util.Map;
import org.apache.zookeeper.data.Id;
import java.util.ArrayList;
import org.apache.zookeeper.Watcher;

public abstract class ServerCnxn implements Stats, Watcher
{
    public static final Object me;
    protected ArrayList<Id> authInfo;
    boolean isOldClient;
    protected ZooKeeperSaslServer zooKeeperSaslServer;
    protected static final int confCmd;
    protected static final int consCmd;
    protected static final int crstCmd;
    protected static final int dumpCmd;
    protected static final int enviCmd;
    protected static final int getTraceMaskCmd;
    protected static final int ruokCmd;
    protected static final int setTraceMaskCmd;
    protected static final int srvrCmd;
    protected static final int srstCmd;
    protected static final int statCmd;
    protected static final int wchcCmd;
    protected static final int wchpCmd;
    protected static final int wchsCmd;
    protected static final int mntrCmd;
    protected static final int isroCmd;
    static final Map<Integer, String> cmd2String;
    private static final String ZOOKEEPER_4LW_COMMANDS_WHITELIST = "zookeeper.4lw.commands.whitelist";
    private static final Logger LOG;
    private static final Set<String> whiteListedCommands;
    private static boolean whiteListInitialized;
    protected final Date established;
    protected final AtomicLong packetsReceived;
    protected final AtomicLong packetsSent;
    protected long minLatency;
    protected long maxLatency;
    protected String lastOp;
    protected long lastCxid;
    protected long lastZxid;
    protected long lastResponseTime;
    protected long lastLatency;
    protected long count;
    protected long totalLatency;
    
    public ServerCnxn() {
        this.authInfo = new ArrayList<Id>();
        this.isOldClient = true;
        this.zooKeeperSaslServer = null;
        this.established = new Date();
        this.packetsReceived = new AtomicLong();
        this.packetsSent = new AtomicLong();
    }
    
    abstract int getSessionTimeout();
    
    abstract void close();
    
    public abstract void sendResponse(final ReplyHeader p0, final Record p1, final String p2) throws IOException;
    
    abstract void sendCloseSession();
    
    @Override
    public abstract void process(final WatchedEvent p0);
    
    abstract long getSessionId();
    
    abstract void setSessionId(final long p0);
    
    public List<Id> getAuthInfo() {
        return Collections.unmodifiableList((List<? extends Id>)this.authInfo);
    }
    
    public void addAuthInfo(final Id id) {
        if (!this.authInfo.contains(id)) {
            this.authInfo.add(id);
        }
    }
    
    public boolean removeAuthInfo(final Id id) {
        return this.authInfo.remove(id);
    }
    
    abstract void sendBuffer(final ByteBuffer p0);
    
    abstract void enableRecv();
    
    abstract void disableRecv();
    
    abstract void setSessionTimeout(final int p0);
    
    public abstract InetAddress getSocketAddress();
    
    public static synchronized void resetWhiteList() {
        ServerCnxn.whiteListInitialized = false;
        ServerCnxn.whiteListedCommands.clear();
    }
    
    public static String getCommandString(final int command) {
        return ServerCnxn.cmd2String.get(command);
    }
    
    public static boolean isKnown(final int command) {
        return ServerCnxn.cmd2String.containsKey(command);
    }
    
    public static synchronized boolean isEnabled(final String command) {
        if (ServerCnxn.whiteListInitialized) {
            return ServerCnxn.whiteListedCommands.contains(command);
        }
        final String commands = System.getProperty("zookeeper.4lw.commands.whitelist");
        if (commands != null) {
            final String[] split;
            final String[] list = split = commands.split(",");
            for (final String cmd : split) {
                if (cmd.trim().equals("*")) {
                    for (final Map.Entry<Integer, String> entry : ServerCnxn.cmd2String.entrySet()) {
                        ServerCnxn.whiteListedCommands.add(entry.getValue());
                    }
                    break;
                }
                if (!cmd.trim().isEmpty()) {
                    ServerCnxn.whiteListedCommands.add(cmd.trim());
                }
            }
        }
        else {
            for (final Map.Entry<Integer, String> entry2 : ServerCnxn.cmd2String.entrySet()) {
                final String cmd2 = entry2.getValue();
                if (!cmd2.equals("wchc")) {
                    if (cmd2.equals("wchp")) {
                        continue;
                    }
                    ServerCnxn.whiteListedCommands.add(cmd2);
                }
            }
        }
        if (System.getProperty("readonlymode.enabled", "false").equals("true")) {
            ServerCnxn.whiteListedCommands.add("isro");
        }
        ServerCnxn.whiteListedCommands.add("srvr");
        ServerCnxn.whiteListInitialized = true;
        ServerCnxn.LOG.info("The list of known four letter word commands is : {}", Collections.singletonList(ServerCnxn.cmd2String));
        ServerCnxn.LOG.info("The list of enabled four letter word commands is : {}", Collections.singletonList(ServerCnxn.whiteListedCommands));
        return ServerCnxn.whiteListedCommands.contains(command);
    }
    
    protected void packetReceived() {
        this.incrPacketsReceived();
        final ServerStats serverStats = this.serverStats();
        if (serverStats != null) {
            this.serverStats().incrementPacketsReceived();
        }
    }
    
    protected void packetSent() {
        this.incrPacketsSent();
        final ServerStats serverStats = this.serverStats();
        if (serverStats != null) {
            this.serverStats().incrementPacketsSent();
        }
    }
    
    protected abstract ServerStats serverStats();
    
    @Override
    public synchronized void resetStats() {
        this.packetsReceived.set(0L);
        this.packetsSent.set(0L);
        this.minLatency = Long.MAX_VALUE;
        this.maxLatency = 0L;
        this.lastOp = "NA";
        this.lastCxid = -1L;
        this.lastZxid = -1L;
        this.lastResponseTime = 0L;
        this.lastLatency = 0L;
        this.count = 0L;
        this.totalLatency = 0L;
    }
    
    protected long incrPacketsReceived() {
        return this.packetsReceived.incrementAndGet();
    }
    
    protected void incrOutstandingRequests(final RequestHeader h) {
    }
    
    protected long incrPacketsSent() {
        return this.packetsSent.incrementAndGet();
    }
    
    protected synchronized void updateStatsForResponse(final long cxid, final long zxid, final String op, final long start, final long end) {
        if (cxid >= 0L) {
            this.lastCxid = cxid;
        }
        this.lastZxid = zxid;
        this.lastOp = op;
        this.lastResponseTime = end;
        final long elapsed = end - start;
        this.lastLatency = elapsed;
        if (elapsed < this.minLatency) {
            this.minLatency = elapsed;
        }
        if (elapsed > this.maxLatency) {
            this.maxLatency = elapsed;
        }
        ++this.count;
        this.totalLatency += elapsed;
    }
    
    @Override
    public Date getEstablished() {
        return (Date)this.established.clone();
    }
    
    @Override
    public abstract long getOutstandingRequests();
    
    @Override
    public long getPacketsReceived() {
        return this.packetsReceived.longValue();
    }
    
    @Override
    public long getPacketsSent() {
        return this.packetsSent.longValue();
    }
    
    @Override
    public synchronized long getMinLatency() {
        return (this.minLatency == Long.MAX_VALUE) ? 0L : this.minLatency;
    }
    
    @Override
    public synchronized long getAvgLatency() {
        return (this.count == 0L) ? 0L : (this.totalLatency / this.count);
    }
    
    @Override
    public synchronized long getMaxLatency() {
        return this.maxLatency;
    }
    
    @Override
    public synchronized String getLastOperation() {
        return this.lastOp;
    }
    
    @Override
    public synchronized long getLastCxid() {
        return this.lastCxid;
    }
    
    @Override
    public synchronized long getLastZxid() {
        return this.lastZxid;
    }
    
    @Override
    public synchronized long getLastResponseTime() {
        return this.lastResponseTime;
    }
    
    @Override
    public synchronized long getLastLatency() {
        return this.lastLatency;
    }
    
    @Override
    public String toString() {
        final StringWriter sw = new StringWriter();
        final PrintWriter pwriter = new PrintWriter(sw);
        this.dumpConnectionInfo(pwriter, false);
        pwriter.flush();
        pwriter.close();
        return sw.toString();
    }
    
    public abstract InetSocketAddress getRemoteSocketAddress();
    
    public abstract int getInterestOps();
    
    protected synchronized void dumpConnectionInfo(final PrintWriter pwriter, final boolean brief) {
        pwriter.print(" ");
        pwriter.print(this.getRemoteSocketAddress());
        pwriter.print("[");
        final int interestOps = this.getInterestOps();
        pwriter.print((interestOps == 0) ? "0" : Integer.toHexString(interestOps));
        pwriter.print("](queued=");
        pwriter.print(this.getOutstandingRequests());
        pwriter.print(",recved=");
        pwriter.print(this.getPacketsReceived());
        pwriter.print(",sent=");
        pwriter.print(this.getPacketsSent());
        if (!brief) {
            final long sessionId = this.getSessionId();
            if (sessionId != 0L) {
                pwriter.print(",sid=0x");
                pwriter.print(Long.toHexString(sessionId));
                pwriter.print(",lop=");
                pwriter.print(this.getLastOperation());
                pwriter.print(",est=");
                pwriter.print(this.getEstablished().getTime());
                pwriter.print(",to=");
                pwriter.print(this.getSessionTimeout());
                final long lastCxid = this.getLastCxid();
                if (lastCxid >= 0L) {
                    pwriter.print(",lcxid=0x");
                    pwriter.print(Long.toHexString(lastCxid));
                }
                pwriter.print(",lzxid=0x");
                pwriter.print(Long.toHexString(this.getLastZxid()));
                pwriter.print(",lresp=");
                pwriter.print(this.getLastResponseTime());
                pwriter.print(",llat=");
                pwriter.print(this.getLastLatency());
                pwriter.print(",minlat=");
                pwriter.print(this.getMinLatency());
                pwriter.print(",avglat=");
                pwriter.print(this.getAvgLatency());
                pwriter.print(",maxlat=");
                pwriter.print(this.getMaxLatency());
            }
        }
        pwriter.print(")");
    }
    
    static {
        me = new Object();
        confCmd = ByteBuffer.wrap("conf".getBytes()).getInt();
        consCmd = ByteBuffer.wrap("cons".getBytes()).getInt();
        crstCmd = ByteBuffer.wrap("crst".getBytes()).getInt();
        dumpCmd = ByteBuffer.wrap("dump".getBytes()).getInt();
        enviCmd = ByteBuffer.wrap("envi".getBytes()).getInt();
        getTraceMaskCmd = ByteBuffer.wrap("gtmk".getBytes()).getInt();
        ruokCmd = ByteBuffer.wrap("ruok".getBytes()).getInt();
        setTraceMaskCmd = ByteBuffer.wrap("stmk".getBytes()).getInt();
        srvrCmd = ByteBuffer.wrap("srvr".getBytes()).getInt();
        srstCmd = ByteBuffer.wrap("srst".getBytes()).getInt();
        statCmd = ByteBuffer.wrap("stat".getBytes()).getInt();
        wchcCmd = ByteBuffer.wrap("wchc".getBytes()).getInt();
        wchpCmd = ByteBuffer.wrap("wchp".getBytes()).getInt();
        wchsCmd = ByteBuffer.wrap("wchs".getBytes()).getInt();
        mntrCmd = ByteBuffer.wrap("mntr".getBytes()).getInt();
        isroCmd = ByteBuffer.wrap("isro".getBytes()).getInt();
        cmd2String = new HashMap<Integer, String>();
        LOG = LoggerFactory.getLogger(ServerCnxn.class);
        whiteListedCommands = new HashSet<String>();
        ServerCnxn.whiteListInitialized = false;
        ServerCnxn.cmd2String.put(ServerCnxn.confCmd, "conf");
        ServerCnxn.cmd2String.put(ServerCnxn.consCmd, "cons");
        ServerCnxn.cmd2String.put(ServerCnxn.crstCmd, "crst");
        ServerCnxn.cmd2String.put(ServerCnxn.dumpCmd, "dump");
        ServerCnxn.cmd2String.put(ServerCnxn.enviCmd, "envi");
        ServerCnxn.cmd2String.put(ServerCnxn.getTraceMaskCmd, "gtmk");
        ServerCnxn.cmd2String.put(ServerCnxn.ruokCmd, "ruok");
        ServerCnxn.cmd2String.put(ServerCnxn.setTraceMaskCmd, "stmk");
        ServerCnxn.cmd2String.put(ServerCnxn.srstCmd, "srst");
        ServerCnxn.cmd2String.put(ServerCnxn.srvrCmd, "srvr");
        ServerCnxn.cmd2String.put(ServerCnxn.statCmd, "stat");
        ServerCnxn.cmd2String.put(ServerCnxn.wchcCmd, "wchc");
        ServerCnxn.cmd2String.put(ServerCnxn.wchpCmd, "wchp");
        ServerCnxn.cmd2String.put(ServerCnxn.wchsCmd, "wchs");
        ServerCnxn.cmd2String.put(ServerCnxn.mntrCmd, "mntr");
        ServerCnxn.cmd2String.put(ServerCnxn.isroCmd, "isro");
    }
    
    protected static class CloseRequestException extends IOException
    {
        private static final long serialVersionUID = -7854505709816442681L;
        
        public CloseRequestException(final String msg) {
            super(msg);
        }
    }
    
    protected static class EndOfStreamException extends IOException
    {
        private static final long serialVersionUID = -8255690282104294178L;
        
        public EndOfStreamException(final String msg) {
            super(msg);
        }
        
        @Override
        public String toString() {
            return "EndOfStreamException: " + this.getMessage();
        }
    }
}
