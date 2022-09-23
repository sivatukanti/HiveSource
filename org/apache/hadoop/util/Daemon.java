// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.concurrent.ThreadFactory;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class Daemon extends Thread
{
    Runnable runnable;
    
    public Daemon() {
        this.setDaemon(true);
        this.runnable = null;
    }
    
    public Daemon(final Runnable runnable) {
        super(runnable);
        this.setDaemon(true);
        this.runnable = null;
        this.runnable = runnable;
        this.setName(runnable.toString());
    }
    
    public Daemon(final ThreadGroup group, final Runnable runnable) {
        super(group, runnable);
        this.setDaemon(true);
        this.runnable = null;
        this.runnable = runnable;
        this.setName(runnable.toString());
    }
    
    public Runnable getRunnable() {
        return this.runnable;
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
    public static class DaemonFactory extends Daemon implements ThreadFactory
    {
        @Override
        public Thread newThread(final Runnable runnable) {
            return new Daemon(runnable);
        }
    }
}
