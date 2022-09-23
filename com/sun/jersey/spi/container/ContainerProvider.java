// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;

public interface ContainerProvider<T>
{
    T createContainer(final Class<T> p0, final ResourceConfig p1, final WebApplication p2) throws ContainerException;
}
