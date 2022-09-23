// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.annotation;

import org.eclipse.jetty.util.log.Log;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import org.eclipse.jetty.util.log.Logger;

public class PreDestroyCallback extends LifeCycleCallback
{
    private static final Logger LOG;
    
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
    public void callback(final Object instance) {
        try {
            super.callback(instance);
        }
        catch (Exception e) {
            PreDestroyCallback.LOG.warn("Ignoring exception thrown on preDestroy call to " + this.getTargetClass() + "." + this.getTarget().getName(), e);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PreDestroyCallback;
    }
    
    static {
        LOG = Log.getLogger(PreDestroyCallback.class);
    }
}
