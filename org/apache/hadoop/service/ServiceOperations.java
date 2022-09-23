// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class ServiceOperations
{
    private static final Logger LOG;
    
    private ServiceOperations() {
    }
    
    public static void stop(final Service service) {
        if (service != null) {
            service.stop();
        }
    }
    
    public static Exception stopQuietly(final Service service) {
        return stopQuietly(ServiceOperations.LOG, service);
    }
    
    public static Exception stopQuietly(final Log log, final Service service) {
        try {
            stop(service);
        }
        catch (Exception e) {
            log.warn("When stopping the service " + service.getName(), e);
            return e;
        }
        return null;
    }
    
    public static Exception stopQuietly(final Logger log, final Service service) {
        try {
            stop(service);
        }
        catch (Exception e) {
            log.warn("When stopping the service {}", service.getName(), e);
            return e;
        }
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractService.class);
    }
    
    public static class ServiceListeners
    {
        private final List<ServiceStateChangeListener> listeners;
        
        public ServiceListeners() {
            this.listeners = new ArrayList<ServiceStateChangeListener>();
        }
        
        public synchronized void add(final ServiceStateChangeListener l) {
            if (!this.listeners.contains(l)) {
                this.listeners.add(l);
            }
        }
        
        public synchronized boolean remove(final ServiceStateChangeListener l) {
            return this.listeners.remove(l);
        }
        
        public synchronized void reset() {
            this.listeners.clear();
        }
        
        public void notifyListeners(final Service service) {
            final ServiceStateChangeListener[] callbacks;
            synchronized (this) {
                callbacks = this.listeners.toArray(new ServiceStateChangeListener[this.listeners.size()]);
            }
            for (final ServiceStateChangeListener l : callbacks) {
                l.stateChanged(service);
            }
        }
    }
}
