// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component.ioc;

import com.sun.jersey.core.spi.component.ComponentScope;

public interface IoCComponentProcessorFactory
{
    ComponentScope getScope(final Class p0);
    
    IoCComponentProcessor get(final Class p0, final ComponentScope p1);
}
