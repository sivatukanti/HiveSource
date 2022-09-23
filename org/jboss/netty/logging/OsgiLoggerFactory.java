// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class OsgiLoggerFactory extends InternalLoggerFactory
{
    private final ServiceTracker logServiceTracker;
    private final InternalLoggerFactory fallback;
    volatile LogService logService;
    
    public OsgiLoggerFactory(final BundleContext ctx) {
        this(ctx, null);
    }
    
    public OsgiLoggerFactory(final BundleContext ctx, InternalLoggerFactory fallback) {
        if (ctx == null) {
            throw new NullPointerException("ctx");
        }
        if (fallback == null) {
            fallback = InternalLoggerFactory.getDefaultFactory();
            if (fallback instanceof OsgiLoggerFactory) {
                fallback = new JdkLoggerFactory();
            }
        }
        this.fallback = fallback;
        (this.logServiceTracker = new ServiceTracker(ctx, "org.osgi.service.log.LogService", null) {
            public Object addingService(final ServiceReference reference) {
                final LogService service = (LogService)super.addingService(reference);
                return OsgiLoggerFactory.this.logService = service;
            }
            
            public void removedService(final ServiceReference reference, final Object service) {
                OsgiLoggerFactory.this.logService = null;
            }
        }).open();
    }
    
    public InternalLoggerFactory getFallback() {
        return this.fallback;
    }
    
    public LogService getLogService() {
        return this.logService;
    }
    
    public void destroy() {
        this.logService = null;
        this.logServiceTracker.close();
    }
    
    @Override
    public InternalLogger newInstance(final String name) {
        return new OsgiLogger(this, name, this.fallback.newInstance(name));
    }
}
