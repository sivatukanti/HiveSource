// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.scanning;

import java.lang.annotation.Annotation;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Path;

public final class PathProviderScannerListener extends AnnotationScannerListener
{
    public PathProviderScannerListener() {
        super((Class<? extends Annotation>[])new Class[] { Path.class, Provider.class });
    }
    
    public PathProviderScannerListener(final ClassLoader classloader) {
        super(classloader, (Class<? extends Annotation>[])new Class[] { Path.class, Provider.class });
    }
}
