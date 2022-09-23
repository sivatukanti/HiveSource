// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.annotation;

import org.eclipse.jetty.util.log.Log;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import org.eclipse.jetty.util.IntrospectionUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import org.eclipse.jetty.util.log.Logger;

public class Injection
{
    private static final Logger LOG;
    private Class<?> _targetClass;
    private String _jndiName;
    private String _mappingName;
    private Member _target;
    private Class<?> _paramClass;
    private Class<?> _resourceClass;
    
    public Class<?> getTargetClass() {
        return this._targetClass;
    }
    
    public Class<?> getParamClass() {
        return this._paramClass;
    }
    
    public Class<?> getResourceClass() {
        return this._resourceClass;
    }
    
    public boolean isField() {
        return this._target != null && this._target instanceof Field;
    }
    
    public boolean isMethod() {
        return this._target != null && this._target instanceof Method;
    }
    
    public String getJndiName() {
        return this._jndiName;
    }
    
    public void setJndiName(final String jndiName) {
        this._jndiName = jndiName;
    }
    
    public String getMappingName() {
        return this._mappingName;
    }
    
    public void setMappingName(final String mappingName) {
        this._mappingName = mappingName;
    }
    
    public Member getTarget() {
        return this._target;
    }
    
    public void setTarget(final Class<?> clazz, final Field field, final Class<?> resourceType) {
        this._targetClass = clazz;
        this._target = field;
        this._resourceClass = resourceType;
    }
    
    public void setTarget(final Class<?> clazz, final Method method, final Class<?> arg, final Class<?> resourceType) {
        this._targetClass = clazz;
        this._target = method;
        this._resourceClass = resourceType;
        this._paramClass = arg;
    }
    
    public void setTarget(final Class<?> clazz, final String target, final Class<?> resourceType) {
        this._targetClass = clazz;
        this._resourceClass = resourceType;
        final String setter = "set" + target.substring(0, 1).toUpperCase() + target.substring(1);
        try {
            Injection.LOG.debug("Looking for method for setter: " + setter + " with arg " + this._resourceClass, new Object[0]);
            this._target = IntrospectionUtil.findMethod(clazz, setter, new Class[] { this._resourceClass }, true, false);
            this._targetClass = clazz;
            this._paramClass = this._resourceClass;
        }
        catch (NoSuchMethodException me) {
            try {
                this._target = IntrospectionUtil.findField(clazz, target, resourceType, true, false);
                this._targetClass = clazz;
            }
            catch (NoSuchFieldException fe) {
                throw new IllegalArgumentException("No such field or method " + target + " on class " + this._targetClass);
            }
        }
    }
    
    public void inject(final Object injectable) {
        if (this._target != null) {
            if (this._target instanceof Field) {
                this.injectField((Field)this._target, injectable);
            }
            else {
                this.injectMethod((Method)this._target, injectable);
            }
            return;
        }
        throw new IllegalStateException("No method or field to inject with " + this.getJndiName());
    }
    
    public Object lookupInjectedValue() throws NamingException {
        final InitialContext context = new InitialContext();
        return context.lookup("java:comp/env/" + this.getJndiName());
    }
    
    protected void injectField(final Field field, final Object injectable) {
        try {
            final boolean accessibility = field.isAccessible();
            field.setAccessible(true);
            field.set(injectable, this.lookupInjectedValue());
            field.setAccessible(accessibility);
        }
        catch (Exception e) {
            Injection.LOG.warn(e);
            throw new IllegalStateException("Inject failed for field " + field.getName());
        }
    }
    
    protected void injectMethod(final Method method, final Object injectable) {
        try {
            final boolean accessibility = method.isAccessible();
            method.setAccessible(true);
            method.invoke(injectable, this.lookupInjectedValue());
            method.setAccessible(accessibility);
        }
        catch (Exception e) {
            Injection.LOG.warn(e);
            throw new IllegalStateException("Inject failed for method " + method.getName());
        }
    }
    
    static {
        LOG = Log.getLogger(Injection.class);
    }
}
