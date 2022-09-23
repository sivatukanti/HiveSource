// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

public interface ResourceFilter
{
    ContainerRequestFilter getRequestFilter();
    
    ContainerResponseFilter getResponseFilter();
}
