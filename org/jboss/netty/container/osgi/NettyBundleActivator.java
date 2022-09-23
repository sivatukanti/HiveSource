// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.container.osgi;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.osgi.framework.BundleContext;
import org.jboss.netty.logging.OsgiLoggerFactory;
import org.osgi.framework.BundleActivator;

public class NettyBundleActivator implements BundleActivator
{
    private OsgiLoggerFactory loggerFactory;
    
    public void start(final BundleContext ctx) throws Exception {
        InternalLoggerFactory.setDefaultFactory(this.loggerFactory = new OsgiLoggerFactory(ctx));
    }
    
    public void stop(final BundleContext ctx) throws Exception {
        if (this.loggerFactory != null) {
            InternalLoggerFactory.setDefaultFactory(this.loggerFactory.getFallback());
            this.loggerFactory.destroy();
            this.loggerFactory = null;
        }
    }
}
