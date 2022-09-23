// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.spi.component;

import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ComponentProviderFactory;

public interface ResourceComponentProviderFactory extends ComponentProviderFactory<ResourceComponentProvider>
{
    ComponentScope getScope(final Class p0);
    
    ResourceComponentProvider getComponentProvider(final IoCComponentProvider p0, final Class<?> p1);
}
