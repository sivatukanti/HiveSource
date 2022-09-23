// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Constructor;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Private
public abstract class ResourceCalculatorProcessTree extends Configured
{
    static final Log LOG;
    
    public ResourceCalculatorProcessTree(final String root) {
    }
    
    public abstract void updateProcessTree();
    
    public abstract String getProcessTreeDump();
    
    public long getCumulativeVmem() {
        return this.getCumulativeVmem(0);
    }
    
    public long getCumulativeRssmem() {
        return this.getCumulativeRssmem(0);
    }
    
    public abstract long getCumulativeVmem(final int p0);
    
    public abstract long getCumulativeRssmem(final int p0);
    
    public abstract long getCumulativeCpuTime();
    
    public abstract boolean checkPidPgrpidForMatch();
    
    public static ResourceCalculatorProcessTree getResourceCalculatorProcessTree(final String pid, final Class<? extends ResourceCalculatorProcessTree> clazz, final Configuration conf) {
        if (clazz != null) {
            try {
                final Constructor<? extends ResourceCalculatorProcessTree> c = clazz.getConstructor(String.class);
                final ResourceCalculatorProcessTree rctree = (ResourceCalculatorProcessTree)c.newInstance(pid);
                rctree.setConf(conf);
                return rctree;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (ProcfsBasedProcessTree.isAvailable()) {
            return new ProcfsBasedProcessTree(pid);
        }
        if (WindowsBasedProcessTree.isAvailable()) {
            return new WindowsBasedProcessTree(pid);
        }
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(ResourceCalculatorProcessTree.class);
    }
}
