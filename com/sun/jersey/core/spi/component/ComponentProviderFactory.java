// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

public interface ComponentProviderFactory<C extends ComponentProvider>
{
    C getComponentProvider(final Class<?> p0);
}
