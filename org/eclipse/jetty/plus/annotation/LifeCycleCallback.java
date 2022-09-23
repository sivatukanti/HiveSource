// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.annotation;

import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.IntrospectionUtil;
import java.lang.reflect.Method;

public abstract class LifeCycleCallback
{
    public static final Object[] __EMPTY_ARGS;
    private Method _target;
    private Class<?> _targetClass;
    private String _className;
    private String _methodName;
    
    public Class<?> getTargetClass() {
        return this._targetClass;
    }
    
    public String getTargetClassName() {
        return this._className;
    }
    
    public String getMethodName() {
        return this._methodName;
    }
    
    public Method getTarget() {
        return this._target;
    }
    
    public void setTarget(final String className, final String methodName) {
        this._className = className;
        this._methodName = methodName;
    }
    
    public void setTarget(final Class<?> clazz, final String methodName) {
        try {
            final Method method = IntrospectionUtil.findMethod(clazz, methodName, null, true, true);
            this.validate(clazz, method);
            this._target = method;
            this._targetClass = clazz;
            this._className = clazz.getCanonicalName();
            this._methodName = methodName;
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method " + methodName + " not found on class " + clazz.getName());
        }
    }
    
    public void callback(final Object instance) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (this._target == null) {
            if (this._targetClass == null) {
                this._targetClass = (Class<?>)Loader.loadClass(null, this._className);
            }
            this._target = this._targetClass.getDeclaredMethod(this._methodName, (Class<?>[])new Class[0]);
        }
        if (this._target != null) {
            final boolean accessibility = this.getTarget().isAccessible();
            this.getTarget().setAccessible(true);
            this.getTarget().invoke(instance, LifeCycleCallback.__EMPTY_ARGS);
            this.getTarget().setAccessible(accessibility);
        }
    }
    
    public Method findMethod(final Package pack, final Class<?> clazz, final String methodName, final boolean checkInheritance) {
        if (clazz == null) {
            return null;
        }
        try {
            final Method method = clazz.getDeclaredMethod(methodName, (Class<?>[])null);
            if (!checkInheritance) {
                return method;
            }
            final int modifiers = method.getModifiers();
            if (Modifier.isProtected(modifiers) || Modifier.isPublic(modifiers) || (!Modifier.isPrivate(modifiers) && pack.equals(clazz.getPackage()))) {
                return method;
            }
            return this.findMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, true);
        }
        catch (NoSuchMethodException e) {
            return this.findMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, true);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof LifeCycleCallback)) {
            return false;
        }
        final LifeCycleCallback callback = (LifeCycleCallback)o;
        if (callback.getTargetClass() == null) {
            if (this.getTargetClass() != null) {
                return false;
            }
        }
        else if (!callback.getTargetClass().equals(this.getTargetClass())) {
            return false;
        }
        if (callback.getTarget() == null) {
            if (this.getTarget() != null) {
                return false;
            }
        }
        else if (!callback.getTarget().equals(this.getTarget())) {
            return false;
        }
        return true;
    }
    
    public abstract void validate(final Class<?> p0, final Method p1);
    
    static {
        __EMPTY_ARGS = new Object[0];
    }
}
