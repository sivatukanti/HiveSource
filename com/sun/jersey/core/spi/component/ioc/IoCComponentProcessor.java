// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component.ioc;

public interface IoCComponentProcessor
{
    void preConstruct();
    
    void postConstruct(final Object p0);
}
