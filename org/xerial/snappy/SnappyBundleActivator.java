// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;

public class SnappyBundleActivator implements BundleActivator
{
    public static final String LIBRARY_NAME = "snappyjava";
    
    public void start(final BundleContext context) throws Exception {
        System.loadLibrary(System.mapLibraryName("snappyjava"));
        SnappyLoader.setApi(new SnappyNative());
    }
    
    public void stop(final BundleContext context) throws Exception {
        SnappyLoader.setApi(null);
    }
}
