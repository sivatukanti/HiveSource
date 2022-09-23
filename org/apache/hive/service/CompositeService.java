// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;

public class CompositeService extends AbstractService
{
    private static final Log LOG;
    private final List<Service> serviceList;
    
    public CompositeService(final String name) {
        super(name);
        this.serviceList = new ArrayList<Service>();
    }
    
    public Collection<Service> getServices() {
        return (Collection<Service>)Collections.unmodifiableList((List<?>)this.serviceList);
    }
    
    protected synchronized void addService(final Service service) {
        this.serviceList.add(service);
    }
    
    protected synchronized boolean removeService(final Service service) {
        return this.serviceList.remove(service);
    }
    
    @Override
    public synchronized void init(final HiveConf hiveConf) {
        for (final Service service : this.serviceList) {
            service.init(hiveConf);
        }
        super.init(hiveConf);
    }
    
    @Override
    public synchronized void start() {
        int i = 0;
        try {
            for (int n = this.serviceList.size(); i < n; ++i) {
                final Service service = this.serviceList.get(i);
                service.start();
            }
            super.start();
        }
        catch (Throwable e) {
            CompositeService.LOG.error("Error starting services " + this.getName(), e);
            this.stop(i);
            throw new ServiceException("Failed to Start " + this.getName(), e);
        }
    }
    
    @Override
    public synchronized void stop() {
        if (this.getServiceState() == Service.STATE.STOPPED) {
            return;
        }
        if (this.serviceList.size() > 0) {
            this.stop(this.serviceList.size() - 1);
        }
        super.stop();
    }
    
    private synchronized void stop(final int numOfServicesStarted) {
        for (int i = numOfServicesStarted; i >= 0; --i) {
            final Service service = this.serviceList.get(i);
            try {
                service.stop();
            }
            catch (Throwable t) {
                CompositeService.LOG.info("Error stopping " + service.getName(), t);
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(CompositeService.class);
    }
    
    public static class CompositeServiceShutdownHook implements Runnable
    {
        private final CompositeService compositeService;
        
        public CompositeServiceShutdownHook(final CompositeService compositeService) {
            this.compositeService = compositeService;
        }
        
        @Override
        public void run() {
            try {
                this.compositeService.stop();
            }
            catch (Throwable t) {
                CompositeService.LOG.info("Error stopping " + this.compositeService.getName(), t);
            }
        }
    }
}
