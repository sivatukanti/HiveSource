// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class CompositeService extends AbstractService
{
    private static final Logger LOG;
    protected static final boolean STOP_ONLY_STARTED_SERVICES = false;
    private final List<Service> serviceList;
    
    public CompositeService(final String name) {
        super(name);
        this.serviceList = new ArrayList<Service>();
    }
    
    public List<Service> getServices() {
        synchronized (this.serviceList) {
            return new ArrayList<Service>(this.serviceList);
        }
    }
    
    protected void addService(final Service service) {
        if (CompositeService.LOG.isDebugEnabled()) {
            CompositeService.LOG.debug("Adding service " + service.getName());
        }
        synchronized (this.serviceList) {
            this.serviceList.add(service);
        }
    }
    
    protected boolean addIfService(final Object object) {
        if (object instanceof Service) {
            this.addService((Service)object);
            return true;
        }
        return false;
    }
    
    protected synchronized boolean removeService(final Service service) {
        synchronized (this.serviceList) {
            return this.serviceList.remove(service);
        }
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        final List<Service> services = this.getServices();
        if (CompositeService.LOG.isDebugEnabled()) {
            CompositeService.LOG.debug(this.getName() + ": initing services, size=" + services.size());
        }
        for (final Service service : services) {
            service.init(conf);
        }
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        final List<Service> services = this.getServices();
        if (CompositeService.LOG.isDebugEnabled()) {
            CompositeService.LOG.debug(this.getName() + ": starting services, size=" + services.size());
        }
        for (final Service service : services) {
            service.start();
        }
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        final int numOfServicesToStop = this.serviceList.size();
        if (CompositeService.LOG.isDebugEnabled()) {
            CompositeService.LOG.debug(this.getName() + ": stopping services, size=" + numOfServicesToStop);
        }
        this.stop(numOfServicesToStop, false);
        super.serviceStop();
    }
    
    private void stop(final int numOfServicesStarted, final boolean stopOnlyStartedServices) {
        Exception firstException = null;
        final List<Service> services = this.getServices();
        for (int i = numOfServicesStarted - 1; i >= 0; --i) {
            final Service service = services.get(i);
            if (CompositeService.LOG.isDebugEnabled()) {
                CompositeService.LOG.debug("Stopping service #" + i + ": " + service);
            }
            final Service.STATE state = service.getServiceState();
            if (state == Service.STATE.STARTED || (!stopOnlyStartedServices && state == Service.STATE.INITED)) {
                final Exception ex = ServiceOperations.stopQuietly(CompositeService.LOG, service);
                if (ex != null && firstException == null) {
                    firstException = ex;
                }
            }
        }
        if (firstException != null) {
            throw ServiceStateException.convert(firstException);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CompositeService.class);
    }
    
    public static class CompositeServiceShutdownHook implements Runnable
    {
        private CompositeService compositeService;
        
        public CompositeServiceShutdownHook(final CompositeService compositeService) {
            this.compositeService = compositeService;
        }
        
        @Override
        public void run() {
            ServiceOperations.stopQuietly(this.compositeService);
        }
    }
}
