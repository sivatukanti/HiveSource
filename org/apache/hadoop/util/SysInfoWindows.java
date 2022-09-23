// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class SysInfoWindows extends SysInfo
{
    private static final Logger LOG;
    private long vmemSize;
    private long memSize;
    private long vmemAvailable;
    private long memAvailable;
    private int numProcessors;
    private long cpuFrequencyKhz;
    private long cumulativeCpuTimeMs;
    private float cpuUsage;
    private long storageBytesRead;
    private long storageBytesWritten;
    private long netBytesRead;
    private long netBytesWritten;
    private long lastRefreshTime;
    static final int REFRESH_INTERVAL_MS = 1000;
    
    public SysInfoWindows() {
        this.lastRefreshTime = 0L;
        this.reset();
    }
    
    @VisibleForTesting
    long now() {
        return Time.monotonicNow();
    }
    
    void reset() {
        this.vmemSize = -1L;
        this.memSize = -1L;
        this.vmemAvailable = -1L;
        this.memAvailable = -1L;
        this.numProcessors = -1;
        this.cpuFrequencyKhz = -1L;
        this.cumulativeCpuTimeMs = -1L;
        this.cpuUsage = -1.0f;
        this.storageBytesRead = -1L;
        this.storageBytesWritten = -1L;
        this.netBytesRead = -1L;
        this.netBytesWritten = -1L;
    }
    
    String getSystemInfoInfoFromShell() {
        try {
            final Shell.ShellCommandExecutor shellExecutor = new Shell.ShellCommandExecutor(new String[] { Shell.getWinUtilsFile().getCanonicalPath(), "systeminfo" });
            shellExecutor.execute();
            return shellExecutor.getOutput();
        }
        catch (IOException e) {
            SysInfoWindows.LOG.error(StringUtils.stringifyException(e));
            return null;
        }
    }
    
    synchronized void refreshIfNeeded() {
        final long now = this.now();
        if (now - this.lastRefreshTime > 1000L) {
            final long refreshInterval = now - this.lastRefreshTime;
            this.lastRefreshTime = now;
            final long lastCumCpuTimeMs = this.cumulativeCpuTimeMs;
            this.reset();
            final String sysInfoStr = this.getSystemInfoInfoFromShell();
            if (sysInfoStr != null) {
                final int sysInfoSplitCount = 11;
                final int index = sysInfoStr.indexOf("\r\n");
                if (index >= 0) {
                    final String[] sysInfo = sysInfoStr.substring(0, index).split(",");
                    if (sysInfo.length == 11) {
                        try {
                            this.vmemSize = Long.parseLong(sysInfo[0]);
                            this.memSize = Long.parseLong(sysInfo[1]);
                            this.vmemAvailable = Long.parseLong(sysInfo[2]);
                            this.memAvailable = Long.parseLong(sysInfo[3]);
                            this.numProcessors = Integer.parseInt(sysInfo[4]);
                            this.cpuFrequencyKhz = Long.parseLong(sysInfo[5]);
                            this.cumulativeCpuTimeMs = Long.parseLong(sysInfo[6]);
                            this.storageBytesRead = Long.parseLong(sysInfo[7]);
                            this.storageBytesWritten = Long.parseLong(sysInfo[8]);
                            this.netBytesRead = Long.parseLong(sysInfo[9]);
                            this.netBytesWritten = Long.parseLong(sysInfo[10]);
                            if (lastCumCpuTimeMs != -1L) {
                                this.cpuUsage = (this.cumulativeCpuTimeMs - lastCumCpuTimeMs) * 100.0f / refreshInterval;
                            }
                        }
                        catch (NumberFormatException nfe) {
                            SysInfoWindows.LOG.warn("Error parsing sysInfo", nfe);
                        }
                    }
                    else {
                        SysInfoWindows.LOG.warn("Expected split length of sysInfo to be 11. Got " + sysInfo.length);
                    }
                }
                else {
                    SysInfoWindows.LOG.warn("Wrong output from sysInfo: " + sysInfoStr);
                }
            }
        }
    }
    
    @Override
    public long getVirtualMemorySize() {
        this.refreshIfNeeded();
        return this.vmemSize;
    }
    
    @Override
    public long getPhysicalMemorySize() {
        this.refreshIfNeeded();
        return this.memSize;
    }
    
    @Override
    public long getAvailableVirtualMemorySize() {
        this.refreshIfNeeded();
        return this.vmemAvailable;
    }
    
    @Override
    public long getAvailablePhysicalMemorySize() {
        this.refreshIfNeeded();
        return this.memAvailable;
    }
    
    @Override
    public synchronized int getNumProcessors() {
        this.refreshIfNeeded();
        return this.numProcessors;
    }
    
    @Override
    public int getNumCores() {
        return this.getNumProcessors();
    }
    
    @Override
    public long getCpuFrequency() {
        this.refreshIfNeeded();
        return this.cpuFrequencyKhz;
    }
    
    @Override
    public long getCumulativeCpuTime() {
        this.refreshIfNeeded();
        return this.cumulativeCpuTimeMs;
    }
    
    @Override
    public synchronized float getCpuUsagePercentage() {
        this.refreshIfNeeded();
        float ret = this.cpuUsage;
        if (ret != -1.0f) {
            ret /= this.numProcessors;
        }
        return ret;
    }
    
    @Override
    public synchronized float getNumVCoresUsed() {
        this.refreshIfNeeded();
        float ret = this.cpuUsage;
        if (ret != -1.0f) {
            ret /= 100.0f;
        }
        return ret;
    }
    
    @Override
    public long getNetworkBytesRead() {
        this.refreshIfNeeded();
        return this.netBytesRead;
    }
    
    @Override
    public long getNetworkBytesWritten() {
        this.refreshIfNeeded();
        return this.netBytesWritten;
    }
    
    @Override
    public long getStorageBytesRead() {
        this.refreshIfNeeded();
        return this.storageBytesRead;
    }
    
    @Override
    public long getStorageBytesWritten() {
        this.refreshIfNeeded();
        return this.storageBytesWritten;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SysInfoWindows.class);
    }
}
