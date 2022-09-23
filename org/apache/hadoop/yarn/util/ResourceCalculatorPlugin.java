// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.hadoop.util.Shell;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.LimitedPrivate({ "YARN", "MAPREDUCE" })
@InterfaceStability.Unstable
public abstract class ResourceCalculatorPlugin extends Configured
{
    protected String processPid;
    
    public ResourceCalculatorPlugin() {
        this.processPid = null;
    }
    
    public void setProcessPid(final String pid) {
        this.processPid = pid;
    }
    
    public abstract long getVirtualMemorySize();
    
    public abstract long getPhysicalMemorySize();
    
    public abstract long getAvailableVirtualMemorySize();
    
    public abstract long getAvailablePhysicalMemorySize();
    
    public abstract int getNumProcessors();
    
    public abstract long getCpuFrequency();
    
    public abstract long getCumulativeCpuTime();
    
    public abstract float getCpuUsage();
    
    public static ResourceCalculatorPlugin getResourceCalculatorPlugin(final Class<? extends ResourceCalculatorPlugin> clazz, final Configuration conf) {
        if (clazz != null) {
            return ReflectionUtils.newInstance(clazz, conf);
        }
        try {
            if (Shell.LINUX) {
                return new LinuxResourceCalculatorPlugin();
            }
            if (Shell.WINDOWS) {
                return new WindowsResourceCalculatorPlugin();
            }
        }
        catch (SecurityException se) {
            return null;
        }
        return null;
    }
}
