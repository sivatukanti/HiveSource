// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import java.lang.reflect.Method;

public interface InvocationHandler extends Callback
{
    Object invoke(final Object p0, final Method p1, final Object[] p2) throws Throwable;
}
