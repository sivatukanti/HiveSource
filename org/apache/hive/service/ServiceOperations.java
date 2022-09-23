// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;

public final class ServiceOperations
{
    private static final Log LOG;
    
    private ServiceOperations() {
    }
    
    public static void ensureCurrentState(final Service.STATE state, final Service.STATE expectedState) {
        if (state != expectedState) {
            throw new IllegalStateException("For this operation, the current service state must be " + expectedState + " instead of " + state);
        }
    }
    
    public static void init(final Service service, final HiveConf configuration) {
        final Service.STATE state = service.getServiceState();
        ensureCurrentState(state, Service.STATE.NOTINITED);
        service.init(configuration);
    }
    
    public static void start(final Service service) {
        final Service.STATE state = service.getServiceState();
        ensureCurrentState(state, Service.STATE.INITED);
        service.start();
    }
    
    public static void deploy(final Service service, final HiveConf configuration) {
        init(service, configuration);
        start(service);
    }
    
    public static void stop(final Service service) {
        if (service != null) {
            final Service.STATE state = service.getServiceState();
            if (state == Service.STATE.STARTED) {
                service.stop();
            }
        }
    }
    
    public static Exception stopQuietly(final Service service) {
        try {
            stop(service);
        }
        catch (Exception e) {
            ServiceOperations.LOG.warn("When stopping the service " + service.getName() + " : " + e, e);
            return e;
        }
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(AbstractService.class);
    }
}
