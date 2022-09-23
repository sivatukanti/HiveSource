// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

public class StateSummary
{
    private long currentEpoch;
    private long lastZxid;
    
    public StateSummary(final long currentEpoch, final long lastZxid) {
        this.currentEpoch = currentEpoch;
        this.lastZxid = lastZxid;
    }
    
    public long getCurrentEpoch() {
        return this.currentEpoch;
    }
    
    public long getLastZxid() {
        return this.lastZxid;
    }
    
    public boolean isMoreRecentThan(final StateSummary ss) {
        return this.currentEpoch > ss.currentEpoch || (this.currentEpoch == ss.currentEpoch && this.lastZxid > ss.lastZxid);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof StateSummary)) {
            return false;
        }
        final StateSummary ss = (StateSummary)obj;
        return this.currentEpoch == ss.currentEpoch && this.lastZxid == ss.lastZxid;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.currentEpoch ^ this.lastZxid);
    }
}
