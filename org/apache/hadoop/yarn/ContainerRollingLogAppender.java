// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn;

import java.io.File;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Flushable;
import org.apache.log4j.RollingFileAppender;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ContainerRollingLogAppender extends RollingFileAppender implements Flushable
{
    private String containerLogDir;
    
    @Override
    public void activateOptions() {
        synchronized (this) {
            this.setFile(new File(this.containerLogDir, "syslog").toString());
            this.setAppend(true);
            super.activateOptions();
        }
    }
    
    @Override
    public void flush() {
        if (this.qw != null) {
            this.qw.flush();
        }
    }
    
    public String getContainerLogDir() {
        return this.containerLogDir;
    }
    
    public void setContainerLogDir(final String containerLogDir) {
        this.containerLogDir = containerLogDir;
    }
}
