// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class JavaMethodInvokerFactory
{
    static JavaMethodInvoker defaultInstance;
    
    public static JavaMethodInvoker getDefault() {
        return JavaMethodInvokerFactory.defaultInstance;
    }
    
    static {
        JavaMethodInvokerFactory.defaultInstance = new JavaMethodInvoker() {
            @Override
            public Object invoke(final Method m, final Object o, final Object... parameters) throws InvocationTargetException, IllegalAccessException {
                return m.invoke(o, parameters);
            }
        };
    }
}
