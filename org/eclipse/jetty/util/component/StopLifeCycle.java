// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class StopLifeCycle extends AbstractLifeCycle implements LifeCycle.Listener
{
    private static final Logger LOG;
    private final LifeCycle _lifecycle;
    
    public StopLifeCycle(final LifeCycle lifecycle) {
        this._lifecycle = lifecycle;
        this.addLifeCycleListener(this);
    }
    
    @Override
    public void lifeCycleStarting(final LifeCycle lifecycle) {
    }
    
    @Override
    public void lifeCycleStarted(final LifeCycle lifecycle) {
        try {
            this._lifecycle.stop();
        }
        catch (Exception e) {
            StopLifeCycle.LOG.warn(e);
        }
    }
    
    @Override
    public void lifeCycleFailure(final LifeCycle lifecycle, final Throwable cause) {
    }
    
    @Override
    public void lifeCycleStopping(final LifeCycle lifecycle) {
    }
    
    @Override
    public void lifeCycleStopped(final LifeCycle lifecycle) {
    }
    
    static {
        LOG = Log.getLogger(StopLifeCycle.class);
    }
}
