// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.ShutdownHookManager;
import org.apache.hadoop.service.Service;
import java.lang.ref.WeakReference;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ServiceShutdownHook implements Runnable
{
    private static final Logger LOG;
    private final WeakReference<Service> serviceRef;
    
    public ServiceShutdownHook(final Service service) {
        this.serviceRef = new WeakReference<Service>(service);
    }
    
    public synchronized void register(final int priority) {
        this.unregister();
        ShutdownHookManager.get().addShutdownHook(this, priority);
    }
    
    public synchronized void unregister() {
        try {
            ShutdownHookManager.get().removeShutdownHook(this);
        }
        catch (IllegalStateException e) {
            ServiceShutdownHook.LOG.info("Failed to unregister shutdown hook: {}", e, e);
        }
    }
    
    @Override
    public void run() {
        this.shutdown();
    }
    
    protected boolean shutdown() {
        boolean result = false;
        final Service service;
        synchronized (this) {
            service = this.serviceRef.get();
            this.serviceRef.clear();
        }
        if (service != null) {
            try {
                service.stop();
                result = true;
            }
            catch (Throwable t) {
                ServiceShutdownHook.LOG.info("Error stopping {}", service.getName(), t);
            }
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ServiceShutdownHook.class);
    }
}
