// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.lang.reflect.Constructor;

public class ReflectWrapper
{
    private Object obj;
    
    public ReflectWrapper(final ClassLoader loader, final String name) {
        try {
            final Class clazz = Class.forName(name, true, loader);
            final Constructor constructor = clazz.getConstructor((Class[])null);
            this.obj = constructor.newInstance((Object[])null);
        }
        catch (Exception t) {
            ReflectUtil.throwBuildException(t);
        }
    }
    
    public ReflectWrapper(final Object obj) {
        this.obj = obj;
    }
    
    public Object getObject() {
        return this.obj;
    }
    
    public Object invoke(final String methodName) {
        return ReflectUtil.invoke(this.obj, methodName);
    }
    
    public Object invoke(final String methodName, final Class argType, final Object arg) {
        return ReflectUtil.invoke(this.obj, methodName, argType, arg);
    }
    
    public Object invoke(final String methodName, final Class argType1, final Object arg1, final Class argType2, final Object arg2) {
        return ReflectUtil.invoke(this.obj, methodName, argType1, arg1, argType2, arg2);
    }
}
