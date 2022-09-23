// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;

public class PostConstructCallback extends LifeCycleCallback
{
    @Override
    public void validate(final Class<?> clazz, final Method method) {
        if (method.getExceptionTypes().length > 0) {
            throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot not throw a checked exception");
        }
        if (!method.getReturnType().equals(Void.TYPE)) {
            throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot not have a return type");
        }
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException(clazz.getName() + "." + method.getName() + " cannot be static");
        }
    }
    
    @Override
    public void callback(final Object instance) throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        super.callback(instance);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PostConstructCallback;
    }
}
