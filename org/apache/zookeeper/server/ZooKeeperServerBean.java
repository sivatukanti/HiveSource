// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.jute.BinaryInputArchive;
import org.apache.zookeeper.Version;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Date;
import org.apache.zookeeper.jmx.ZKMBeanInfo;

public class ZooKeeperServerBean implements ZooKeeperServerMXBean, ZKMBeanInfo
{
    private final Date startTime;
    private final String name;
    protected final ZooKeeperServer zks;
    
    public ZooKeeperServerBean(final ZooKeeperServer zks) {
        this.startTime = new Date();
        this.zks = zks;
        this.name = "StandaloneServer_port" + zks.getClientPort();
    }
    
    @Override
    public String getClientPort() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":" + this.zks.getClientPort();
        }
        catch (UnknownHostException e) {
            return "localhost:" + this.zks.getClientPort();
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public String getStartTime() {
        return this.startTime.toString();
    }
    
    @Override
    public String getVersion() {
        return Version.getFullVersion();
    }
    
    @Override
    public long getAvgRequestLatency() {
        return this.zks.serverStats().getAvgLatency();
    }
    
    @Override
    public long getMaxRequestLatency() {
        return this.zks.serverStats().getMaxLatency();
    }
    
    @Override
    public long getMinRequestLatency() {
        return this.zks.serverStats().getMinLatency();
    }
    
    @Override
    public long getOutstandingRequests() {
        return this.zks.serverStats().getOutstandingRequests();
    }
    
    @Override
    public int getTickTime() {
        return this.zks.getTickTime();
    }
    
    @Override
    public void setTickTime(final int tickTime) {
        this.zks.setTickTime(tickTime);
    }
    
    @Override
    public int getMaxClientCnxnsPerHost() {
        final ServerCnxnFactory fac = this.zks.getServerCnxnFactory();
        if (fac == null) {
            return -1;
        }
        return fac.getMaxClientCnxnsPerHost();
    }
    
    @Override
    public void setMaxClientCnxnsPerHost(final int max) {
        this.zks.getServerCnxnFactory().setMaxClientCnxnsPerHost(max);
    }
    
    @Override
    public int getMinSessionTimeout() {
        return this.zks.getMinSessionTimeout();
    }
    
    @Override
    public void setMinSessionTimeout(final int min) {
        this.zks.setMinSessionTimeout(min);
    }
    
    @Override
    public int getMaxSessionTimeout() {
        return this.zks.getMaxSessionTimeout();
    }
    
    @Override
    public void setMaxSessionTimeout(final int max) {
        this.zks.setMaxSessionTimeout(max);
    }
    
    @Override
    public long getPacketsReceived() {
        return this.zks.serverStats().getPacketsReceived();
    }
    
    @Override
    public long getPacketsSent() {
        return this.zks.serverStats().getPacketsSent();
    }
    
    @Override
    public long getFsyncThresholdExceedCount() {
        return this.zks.serverStats().getFsyncThresholdExceedCount();
    }
    
    @Override
    public void resetLatency() {
        this.zks.serverStats().resetLatency();
    }
    
    @Override
    public void resetMaxLatency() {
        this.zks.serverStats().resetMaxLatency();
    }
    
    @Override
    public void resetFsyncThresholdExceedCount() {
        this.zks.serverStats().resetFsyncThresholdExceedCount();
    }
    
    @Override
    public void resetStatistics() {
        final ServerStats serverStats = this.zks.serverStats();
        serverStats.resetRequestCounters();
        serverStats.resetLatency();
        serverStats.resetFsyncThresholdExceedCount();
    }
    
    @Override
    public long getNumAliveConnections() {
        return this.zks.getNumAliveConnections();
    }
    
    @Override
    public int getJuteMaxBufferSize() {
        return BinaryInputArchive.maxBuffer;
    }
}
