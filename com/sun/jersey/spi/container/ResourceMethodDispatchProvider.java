// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;

public interface ResourceMethodDispatchProvider
{
    RequestDispatcher create(final AbstractResourceMethod p0);
}
