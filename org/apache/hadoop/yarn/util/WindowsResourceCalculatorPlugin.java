// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Shell;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class WindowsResourceCalculatorPlugin extends ResourceCalculatorPlugin
{
    static final Log LOG;
    long vmemSize;
    long memSize;
    long vmemAvailable;
    long memAvailable;
    int numProcessors;
    long cpuFrequencyKhz;
    long cumulativeCpuTimeMs;
    float cpuUsage;
    long lastRefreshTime;
    private final int refreshIntervalMs = 1000;
    WindowsBasedProcessTree pTree;
    
    public WindowsResourceCalculatorPlugin() {
        this.pTree = null;
        this.lastRefreshTime = 0L;
        this.reset();
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
    }
    
    String getSystemInfoInfoFromShell() {
        final Shell.ShellCommandExecutor shellExecutor = new Shell.ShellCommandExecutor(new String[] { Shell.WINUTILS, "systeminfo" });
        try {
            shellExecutor.execute();
            return shellExecutor.getOutput();
        }
        catch (IOException e) {
            WindowsResourceCalculatorPlugin.LOG.error(StringUtils.stringifyException(e));
            return null;
        }
    }
    
    void refreshIfNeeded() {
        final long now = System.currentTimeMillis();
        if (now - this.lastRefreshTime > 1000L) {
            final long refreshInterval = now - this.lastRefreshTime;
            this.lastRefreshTime = now;
            final long lastCumCpuTimeMs = this.cumulativeCpuTimeMs;
            this.reset();
            final String sysInfoStr = this.getSystemInfoInfoFromShell();
            if (sysInfoStr != null) {
                final int sysInfoSplitCount = 7;
                final String[] sysInfo = sysInfoStr.substring(0, sysInfoStr.indexOf("\r\n")).split(",");
                if (sysInfo.length == 7) {
                    try {
                        this.vmemSize = Long.parseLong(sysInfo[0]);
                        this.memSize = Long.parseLong(sysInfo[1]);
                        this.vmemAvailable = Long.parseLong(sysInfo[2]);
                        this.memAvailable = Long.parseLong(sysInfo[3]);
                        this.numProcessors = Integer.parseInt(sysInfo[4]);
                        this.cpuFrequencyKhz = Long.parseLong(sysInfo[5]);
                        this.cumulativeCpuTimeMs = Long.parseLong(sysInfo[6]);
                        if (lastCumCpuTimeMs != -1L) {
                            this.cpuUsage = (this.cumulativeCpuTimeMs - lastCumCpuTimeMs) / (refreshInterval * 1.0f);
                        }
                    }
                    catch (NumberFormatException nfe) {
                        WindowsResourceCalculatorPlugin.LOG.warn("Error parsing sysInfo." + nfe);
                    }
                }
                else {
                    WindowsResourceCalculatorPlugin.LOG.warn("Expected split length of sysInfo to be 7. Got " + sysInfo.length);
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
    public int getNumProcessors() {
        this.refreshIfNeeded();
        return this.numProcessors;
    }
    
    @Override
    public long getCpuFrequency() {
        this.refreshIfNeeded();
        return -1L;
    }
    
    @Override
    public long getCumulativeCpuTime() {
        this.refreshIfNeeded();
        return this.cumulativeCpuTimeMs;
    }
    
    @Override
    public float getCpuUsage() {
        this.refreshIfNeeded();
        return this.cpuUsage;
    }
    
    static {
        LOG = LogFactory.getLog(WindowsResourceCalculatorPlugin.class);
    }
}
