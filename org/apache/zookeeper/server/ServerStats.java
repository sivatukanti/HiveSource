// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.zookeeper.common.Time;
import java.util.concurrent.atomic.AtomicLong;

public class ServerStats
{
    private long packetsSent;
    private long packetsReceived;
    private long maxLatency;
    private long minLatency;
    private long totalLatency;
    private long count;
    private AtomicLong fsyncThresholdExceedCount;
    private final Provider provider;
    
    public ServerStats(final Provider provider) {
        this.minLatency = Long.MAX_VALUE;
        this.totalLatency = 0L;
        this.count = 0L;
        this.fsyncThresholdExceedCount = new AtomicLong(0L);
        this.provider = provider;
    }
    
    public synchronized long getMinLatency() {
        return (this.minLatency == Long.MAX_VALUE) ? 0L : this.minLatency;
    }
    
    public synchronized long getAvgLatency() {
        if (this.count != 0L) {
            return this.totalLatency / this.count;
        }
        return 0L;
    }
    
    public synchronized long getMaxLatency() {
        return this.maxLatency;
    }
    
    public long getOutstandingRequests() {
        return this.provider.getOutstandingRequests();
    }
    
    public long getLastProcessedZxid() {
        return this.provider.getLastProcessedZxid();
    }
    
    public synchronized long getPacketsReceived() {
        return this.packetsReceived;
    }
    
    public synchronized long getPacketsSent() {
        return this.packetsSent;
    }
    
    public String getServerState() {
        return this.provider.getState();
    }
    
    public int getNumAliveClientConnections() {
        return this.provider.getNumAliveConnections();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Latency min/avg/max: " + this.getMinLatency() + "/" + this.getAvgLatency() + "/" + this.getMaxLatency() + "\n");
        sb.append("Received: " + this.getPacketsReceived() + "\n");
        sb.append("Sent: " + this.getPacketsSent() + "\n");
        sb.append("Connections: " + this.getNumAliveClientConnections() + "\n");
        if (this.provider != null) {
            sb.append("Outstanding: " + this.getOutstandingRequests() + "\n");
            sb.append("Zxid: 0x" + Long.toHexString(this.getLastProcessedZxid()) + "\n");
        }
        sb.append("Mode: " + this.getServerState() + "\n");
        return sb.toString();
    }
    
    public long getFsyncThresholdExceedCount() {
        return this.fsyncThresholdExceedCount.get();
    }
    
    public void incrementFsyncThresholdExceedCount() {
        this.fsyncThresholdExceedCount.incrementAndGet();
    }
    
    public void resetFsyncThresholdExceedCount() {
        this.fsyncThresholdExceedCount.set(0L);
    }
    
    synchronized void updateLatency(final long requestCreateTime) {
        final long latency = Time.currentElapsedTime() - requestCreateTime;
        this.totalLatency += latency;
        ++this.count;
        if (latency < this.minLatency) {
            this.minLatency = latency;
        }
        if (latency > this.maxLatency) {
            this.maxLatency = latency;
        }
    }
    
    public synchronized void resetLatency() {
        this.totalLatency = 0L;
        this.count = 0L;
        this.maxLatency = 0L;
        this.minLatency = Long.MAX_VALUE;
    }
    
    public synchronized void resetMaxLatency() {
        this.maxLatency = this.getMinLatency();
    }
    
    public synchronized void incrementPacketsReceived() {
        ++this.packetsReceived;
    }
    
    public synchronized void incrementPacketsSent() {
        ++this.packetsSent;
    }
    
    public synchronized void resetRequestCounters() {
        this.packetsReceived = 0L;
        this.packetsSent = 0L;
    }
    
    public synchronized void reset() {
        this.resetLatency();
        this.resetRequestCounters();
    }
    
    public interface Provider
    {
        long getOutstandingRequests();
        
        long getLastProcessedZxid();
        
        String getState();
        
        int getNumAliveConnections();
    }
}
