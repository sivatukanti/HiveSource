// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.proxy;

import com.sun.jersey.api.client.Client;

public interface ViewProxyProvider
{
     <T> ViewProxy<T> proxy(final Client p0, final Class<T> p1);
}
