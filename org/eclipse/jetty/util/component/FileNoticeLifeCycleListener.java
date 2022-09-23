// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import org.eclipse.jetty.util.log.Log;
import java.io.Writer;
import java.io.FileWriter;
import org.eclipse.jetty.util.log.Logger;

public class FileNoticeLifeCycleListener implements LifeCycle.Listener
{
    private static final Logger LOG;
    private final String _filename;
    
    public FileNoticeLifeCycleListener(final String filename) {
        this._filename = filename;
    }
    
    private void writeState(final String action, final LifeCycle lifecycle) {
        try {
            final Writer out = new FileWriter(this._filename, true);
            Throwable t = null;
            try {
                out.append((CharSequence)action).append((CharSequence)" ").append((CharSequence)lifecycle.toString()).append((CharSequence)"\n");
            }
            catch (Throwable t2) {
                t = t2;
                throw t2;
            }
            finally {
                if (t != null) {
                    try {
                        out.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                else {
                    out.close();
                }
            }
        }
        catch (Exception e) {
            FileNoticeLifeCycleListener.LOG.warn(e);
        }
    }
    
    @Override
    public void lifeCycleStarting(final LifeCycle event) {
        this.writeState("STARTING", event);
    }
    
    @Override
    public void lifeCycleStarted(final LifeCycle event) {
        this.writeState("STARTED", event);
    }
    
    @Override
    public void lifeCycleFailure(final LifeCycle event, final Throwable cause) {
        this.writeState("FAILED", event);
    }
    
    @Override
    public void lifeCycleStopping(final LifeCycle event) {
        this.writeState("STOPPING", event);
    }
    
    @Override
    public void lifeCycleStopped(final LifeCycle event) {
        this.writeState("STOPPED", event);
    }
    
    static {
        LOG = Log.getLogger(FileNoticeLifeCycleListener.class);
    }
}
