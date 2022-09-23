// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.retry;

import java.util.Map;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class RetryProxy
{
    public static <T> Object create(final Class<T> iface, final T implementation, final RetryPolicy retryPolicy) {
        return create(iface, new DefaultFailoverProxyProvider<T>(iface, implementation), retryPolicy);
    }
    
    public static <T> Object create(final Class<T> iface, final FailoverProxyProvider<T> proxyProvider, final RetryPolicy retryPolicy) {
        return Proxy.newProxyInstance(proxyProvider.getInterface().getClassLoader(), new Class[] { iface }, new RetryInvocationHandler<Object>(proxyProvider, retryPolicy));
    }
    
    public static <T> Object create(final Class<T> iface, final T implementation, final Map<String, RetryPolicy> methodNameToPolicyMap) {
        return create(iface, new DefaultFailoverProxyProvider<T>(iface, implementation), methodNameToPolicyMap, RetryPolicies.TRY_ONCE_THEN_FAIL);
    }
    
    public static <T> Object create(final Class<T> iface, final FailoverProxyProvider<T> proxyProvider, final Map<String, RetryPolicy> methodNameToPolicyMap, final RetryPolicy defaultPolicy) {
        return Proxy.newProxyInstance(proxyProvider.getInterface().getClassLoader(), new Class[] { iface }, new RetryInvocationHandler<Object>(proxyProvider, defaultPolicy, methodNameToPolicyMap));
    }
}
