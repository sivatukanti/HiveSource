// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.ReflectWrapper;
import java.io.File;
import org.apache.tools.ant.util.JavaEnvUtils;

public class HasFreeSpace implements Condition
{
    private String partition;
    private String needed;
    
    public boolean eval() throws BuildException {
        this.validate();
        try {
            if (JavaEnvUtils.isAtLeastJavaVersion("1.6")) {
                final File fs = new File(this.partition);
                final ReflectWrapper w = new ReflectWrapper(fs);
                final long free = (long)w.invoke("getFreeSpace");
                return free >= StringUtils.parseHumanSizes(this.needed);
            }
            throw new BuildException("HasFreeSpace condition not supported on Java5 or less.");
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
    
    private void validate() throws BuildException {
        if (null == this.partition) {
            throw new BuildException("Please set the partition attribute.");
        }
        if (null == this.needed) {
            throw new BuildException("Please set the needed attribute.");
        }
    }
    
    public String getPartition() {
        return this.partition;
    }
    
    public void setPartition(final String partition) {
        this.partition = partition;
    }
    
    public String getNeeded() {
        return this.needed;
    }
    
    public void setNeeded(final String needed) {
        this.needed = needed;
    }
}
