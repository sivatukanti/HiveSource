// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.monitoring;

import javax.ws.rs.ext.ExceptionMapper;
import com.sun.jersey.spi.container.ContainerResponse;

public interface ResponseListener
{
    void onError(final long p0, final Throwable p1);
    
    void onResponse(final long p0, final ContainerResponse p1);
    
    void onMappedException(final long p0, final Throwable p1, final ExceptionMapper p2);
}
