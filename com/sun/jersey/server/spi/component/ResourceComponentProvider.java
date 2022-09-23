// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.spi.component;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.core.spi.component.ComponentProvider;

public interface ResourceComponentProvider extends ComponentProvider
{
    void init(final AbstractResource p0);
    
    ComponentScope getScope();
    
    Object getInstance(final HttpContext p0);
    
    void destroy();
}
