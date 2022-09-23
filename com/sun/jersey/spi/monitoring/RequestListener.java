// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.monitoring;

import com.sun.jersey.spi.container.ContainerRequest;

public interface RequestListener
{
    void onRequest(final long p0, final ContainerRequest p1);
}
