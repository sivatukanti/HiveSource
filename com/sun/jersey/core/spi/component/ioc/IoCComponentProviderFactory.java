// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component.ioc;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentProviderFactory;

public interface IoCComponentProviderFactory extends ComponentProviderFactory<IoCComponentProvider>
{
    IoCComponentProvider getComponentProvider(final Class<?> p0);
    
    IoCComponentProvider getComponentProvider(final ComponentContext p0, final Class<?> p1);
}
