// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import com.sun.jersey.api.container.ContainerException;
import java.net.URI;

public interface ResourceContext
{
    ExtendedUriInfo matchUriInfo(final URI p0) throws ContainerException;
    
    Object matchResource(final URI p0) throws ContainerException;
    
     <T> T matchResource(final URI p0, final Class<T> p1) throws ContainerException, ClassCastException;
    
     <T> T getResource(final Class<T> p0) throws ContainerException;
}
