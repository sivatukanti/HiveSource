// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.math.BigInteger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class CpuTimeTracker
{
    public static final int UNAVAILABLE = -1;
    private final long minimumTimeInterval;
    private BigInteger cumulativeCpuTime;
    private BigInteger lastCumulativeCpuTime;
    private long sampleTime;
    private long lastSampleTime;
    private float cpuUsage;
    private BigInteger jiffyLengthInMillis;
    
    public CpuTimeTracker(final long jiffyLengthInMillis) {
        this.cumulativeCpuTime = BigInteger.ZERO;
        this.lastCumulativeCpuTime = BigInteger.ZERO;
        this.jiffyLengthInMillis = BigInteger.valueOf(jiffyLengthInMillis);
        this.cpuUsage = -1.0f;
        this.sampleTime = -1L;
        this.lastSampleTime = -1L;
        this.minimumTimeInterval = 10L * jiffyLengthInMillis;
    }
    
    public float getCpuTrackerUsagePercent() {
        if (this.lastSampleTime == -1L || this.lastSampleTime > this.sampleTime) {
            this.lastSampleTime = this.sampleTime;
            this.lastCumulativeCpuTime = this.cumulativeCpuTime;
            return this.cpuUsage;
        }
        if (this.sampleTime > this.lastSampleTime + this.minimumTimeInterval) {
            this.cpuUsage = this.cumulativeCpuTime.subtract(this.lastCumulativeCpuTime).floatValue() * 100.0f / (this.sampleTime - this.lastSampleTime);
            this.lastSampleTime = this.sampleTime;
            this.lastCumulativeCpuTime = this.cumulativeCpuTime;
        }
        return this.cpuUsage;
    }
    
    public long getCumulativeCpuTime() {
        return this.cumulativeCpuTime.longValue();
    }
    
    public void updateElapsedJiffies(final BigInteger elapsedJiffies, final long newTime) {
        final BigInteger newValue = elapsedJiffies.multiply(this.jiffyLengthInMillis);
        this.cumulativeCpuTime = ((newValue.compareTo(this.cumulativeCpuTime) >= 0) ? newValue : this.cumulativeCpuTime);
        this.sampleTime = newTime;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SampleTime " + this.sampleTime);
        sb.append(" CummulativeCpuTime " + this.cumulativeCpuTime);
        sb.append(" LastSampleTime " + this.lastSampleTime);
        sb.append(" LastCummulativeCpuTime " + this.lastCumulativeCpuTime);
        sb.append(" CpuUsage " + this.cpuUsage);
        sb.append(" JiffyLengthMillisec " + this.jiffyLengthInMillis);
        return sb.toString();
    }
}
