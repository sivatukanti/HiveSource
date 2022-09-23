// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface JavaMethodInvoker
{
    Object invoke(final Method p0, final Object p1, final Object... p2) throws InvocationTargetException, IllegalAccessException;
}
