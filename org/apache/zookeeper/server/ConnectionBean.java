// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.common.Time;
import java.util.Arrays;
import org.apache.zookeeper.jmx.MBeanRegistry;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import javax.management.ObjectName;
import java.net.Inet6Address;
import org.slf4j.Logger;
import org.apache.zookeeper.jmx.ZKMBeanInfo;

public class ConnectionBean implements ConnectionMXBean, ZKMBeanInfo
{
    private static final Logger LOG;
    private final ServerCnxn connection;
    private final Stats stats;
    private final ZooKeeperServer zk;
    private final String remoteIP;
    private final long sessionId;
    
    public ConnectionBean(final ServerCnxn connection, final ZooKeeperServer zk) {
        this.connection = connection;
        this.stats = connection;
        this.zk = zk;
        final InetSocketAddress sockAddr = connection.getRemoteSocketAddress();
        if (sockAddr == null) {
            this.remoteIP = "Unknown";
        }
        else {
            final InetAddress addr = sockAddr.getAddress();
            if (addr instanceof Inet6Address) {
                this.remoteIP = ObjectName.quote(addr.getHostAddress());
            }
            else {
                this.remoteIP = addr.getHostAddress();
            }
        }
        this.sessionId = connection.getSessionId();
    }
    
    @Override
    public String getSessionId() {
        return "0x" + Long.toHexString(this.sessionId);
    }
    
    @Override
    public String getSourceIP() {
        final InetSocketAddress sockAddr = this.connection.getRemoteSocketAddress();
        if (sockAddr == null) {
            return null;
        }
        return sockAddr.getAddress().getHostAddress() + ":" + sockAddr.getPort();
    }
    
    @Override
    public String getName() {
        return MBeanRegistry.getInstance().makeFullPath("Connections", this.remoteIP, this.getSessionId());
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public String[] getEphemeralNodes() {
        if (this.zk.getZKDatabase() != null) {
            final String[] res = this.zk.getZKDatabase().getEphemerals(this.sessionId).toArray(new String[0]);
            Arrays.sort(res);
            return res;
        }
        return null;
    }
    
    @Override
    public String getStartedTime() {
        return this.stats.getEstablished().toString();
    }
    
    @Override
    public void terminateSession() {
        try {
            this.zk.closeSession(this.sessionId);
        }
        catch (Exception e) {
            ConnectionBean.LOG.warn("Unable to closeSession() for session: 0x" + this.getSessionId(), e);
        }
    }
    
    @Override
    public void terminateConnection() {
        this.connection.sendCloseSession();
    }
    
    @Override
    public void resetCounters() {
        this.stats.resetStats();
    }
    
    @Override
    public String toString() {
        return "ConnectionBean{ClientIP=" + ObjectName.quote(this.getSourceIP()) + ",SessionId=0x" + this.getSessionId() + "}";
    }
    
    @Override
    public long getOutstandingRequests() {
        return this.stats.getOutstandingRequests();
    }
    
    @Override
    public long getPacketsReceived() {
        return this.stats.getPacketsReceived();
    }
    
    @Override
    public long getPacketsSent() {
        return this.stats.getPacketsSent();
    }
    
    @Override
    public int getSessionTimeout() {
        return this.connection.getSessionTimeout();
    }
    
    @Override
    public long getMinLatency() {
        return this.stats.getMinLatency();
    }
    
    @Override
    public long getAvgLatency() {
        return this.stats.getAvgLatency();
    }
    
    @Override
    public long getMaxLatency() {
        return this.stats.getMaxLatency();
    }
    
    @Override
    public String getLastOperation() {
        return this.stats.getLastOperation();
    }
    
    @Override
    public String getLastCxid() {
        return "0x" + Long.toHexString(this.stats.getLastCxid());
    }
    
    @Override
    public String getLastZxid() {
        return "0x" + Long.toHexString(this.stats.getLastZxid());
    }
    
    @Override
    public String getLastResponseTime() {
        return Time.elapsedTimeToDate(this.stats.getLastResponseTime()).toString();
    }
    
    @Override
    public long getLastLatency() {
        return this.stats.getLastLatency();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConnectionBean.class);
    }
}
