// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.monitoring;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;

public interface DispatchingListener
{
    void onSubResource(final long p0, final Class p1);
    
    void onSubResourceLocator(final long p0, final AbstractSubResourceLocator p1);
    
    void onResourceMethod(final long p0, final AbstractResourceMethod p1);
}
