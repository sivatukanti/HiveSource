// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi;

import javax.ws.rs.ext.RuntimeDelegate;

public interface HeaderDelegateProvider<T> extends RuntimeDelegate.HeaderDelegate<T>
{
    boolean supports(final Class<?> p0);
}
