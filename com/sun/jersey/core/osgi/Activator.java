// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;

public class Activator implements BundleActivator
{
    public void start(final BundleContext context) throws Exception {
        OsgiRegistry.getInstance();
    }
    
    public void stop(final BundleContext context) throws Exception {
    }
}
