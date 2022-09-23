// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class SysInfo
{
    public static SysInfo newInstance() {
        if (Shell.LINUX) {
            return new SysInfoLinux();
        }
        if (Shell.WINDOWS) {
            return new SysInfoWindows();
        }
        throw new UnsupportedOperationException("Could not determine OS");
    }
    
    public abstract long getVirtualMemorySize();
    
    public abstract long getPhysicalMemorySize();
    
    public abstract long getAvailableVirtualMemorySize();
    
    public abstract long getAvailablePhysicalMemorySize();
    
    public abstract int getNumProcessors();
    
    public abstract int getNumCores();
    
    public abstract long getCpuFrequency();
    
    public abstract long getCumulativeCpuTime();
    
    public abstract float getCpuUsagePercentage();
    
    public abstract float getNumVCoresUsed();
    
    public abstract long getNetworkBytesRead();
    
    public abstract long getNetworkBytesWritten();
    
    public abstract long getStorageBytesRead();
    
    public abstract long getStorageBytesWritten();
}
