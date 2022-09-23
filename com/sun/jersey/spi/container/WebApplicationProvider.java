// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import com.sun.jersey.api.container.ContainerException;

public interface WebApplicationProvider
{
    WebApplication createWebApplication() throws ContainerException;
}
