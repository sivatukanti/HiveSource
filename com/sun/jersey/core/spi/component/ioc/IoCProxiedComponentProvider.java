// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component.ioc;

public interface IoCProxiedComponentProvider extends IoCComponentProvider
{
    Object getInstance();
    
    Object proxy(final Object p0);
}
