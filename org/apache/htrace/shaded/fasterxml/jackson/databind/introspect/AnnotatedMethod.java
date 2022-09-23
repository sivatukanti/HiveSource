// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.TypeVariable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeBindings;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.io.Serializable;

public final class AnnotatedMethod extends AnnotatedWithParams implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final transient Method _method;
    protected Class<?>[] _paramClasses;
    protected Serialization _serialization;
    
    public AnnotatedMethod(final Method method, final AnnotationMap classAnn, final AnnotationMap[] paramAnnotations) {
        super(classAnn, paramAnnotations);
        if (method == null) {
            throw new IllegalArgumentException("Can not construct AnnotatedMethod with null Method");
        }
        this._method = method;
    }
    
    protected AnnotatedMethod(final Serialization ser) {
        super(null, null);
        this._method = null;
        this._serialization = ser;
    }
    
    public AnnotatedMethod withMethod(final Method m) {
        return new AnnotatedMethod(m, this._annotations, this._paramAnnotations);
    }
    
    @Override
    public AnnotatedMethod withAnnotations(final AnnotationMap ann) {
        return new AnnotatedMethod(this._method, ann, this._paramAnnotations);
    }
    
    @Override
    public Method getAnnotated() {
        return this._method;
    }
    
    public int getModifiers() {
        return this._method.getModifiers();
    }
    
    @Override
    public String getName() {
        return this._method.getName();
    }
    
    @Override
    public Type getGenericType() {
        return this._method.getGenericReturnType();
    }
    
    @Override
    public Class<?> getRawType() {
        return this._method.getReturnType();
    }
    
    @Override
    public JavaType getType(final TypeBindings bindings) {
        return this.getType(bindings, this._method.getTypeParameters());
    }
    
    @Override
    public final Object call() throws Exception {
        return this._method.invoke(null, new Object[0]);
    }
    
    @Override
    public final Object call(final Object[] args) throws Exception {
        return this._method.invoke(null, args);
    }
    
    @Override
    public final Object call1(final Object arg) throws Exception {
        return this._method.invoke(null, arg);
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return this._method.getDeclaringClass();
    }
    
    @Override
    public Method getMember() {
        return this._method;
    }
    
    @Override
    public void setValue(final Object pojo, final Object value) throws IllegalArgumentException {
        try {
            this._method.invoke(pojo, value);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to setValue() with method " + this.getFullName() + ": " + e.getMessage(), e);
        }
        catch (InvocationTargetException e2) {
            throw new IllegalArgumentException("Failed to setValue() with method " + this.getFullName() + ": " + e2.getMessage(), e2);
        }
    }
    
    @Override
    public Object getValue(final Object pojo) throws IllegalArgumentException {
        try {
            return this._method.invoke(pojo, new Object[0]);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to getValue() with method " + this.getFullName() + ": " + e.getMessage(), e);
        }
        catch (InvocationTargetException e2) {
            throw new IllegalArgumentException("Failed to getValue() with method " + this.getFullName() + ": " + e2.getMessage(), e2);
        }
    }
    
    @Override
    public int getParameterCount() {
        return this.getRawParameterTypes().length;
    }
    
    public String getFullName() {
        return this.getDeclaringClass().getName() + "#" + this.getName() + "(" + this.getParameterCount() + " params)";
    }
    
    public Class<?>[] getRawParameterTypes() {
        if (this._paramClasses == null) {
            this._paramClasses = this._method.getParameterTypes();
        }
        return this._paramClasses;
    }
    
    public Type[] getGenericParameterTypes() {
        return this._method.getGenericParameterTypes();
    }
    
    @Override
    public Class<?> getRawParameterType(final int index) {
        final Class<?>[] types = this.getRawParameterTypes();
        return (index >= types.length) ? null : types[index];
    }
    
    @Override
    public Type getGenericParameterType(final int index) {
        final Type[] types = this._method.getGenericParameterTypes();
        return (index >= types.length) ? null : types[index];
    }
    
    public Class<?> getRawReturnType() {
        return this._method.getReturnType();
    }
    
    public Type getGenericReturnType() {
        return this._method.getGenericReturnType();
    }
    
    public boolean hasReturnType() {
        final Class<?> rt = this.getRawReturnType();
        return rt != Void.TYPE && rt != Void.class;
    }
    
    @Override
    public String toString() {
        return "[method " + this.getFullName() + "]";
    }
    
    Object writeReplace() {
        return new AnnotatedMethod(new Serialization(this._method));
    }
    
    Object readResolve() {
        final Class<?> clazz = this._serialization.clazz;
        try {
            final Method m = clazz.getDeclaredMethod(this._serialization.name, this._serialization.args);
            if (!m.isAccessible()) {
                ClassUtil.checkAndFixAccess(m);
            }
            return new AnnotatedMethod(m, null, null);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz.getName());
        }
    }
    
    private static final class Serialization implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected Class<?> clazz;
        protected String name;
        protected Class<?>[] args;
        
        public Serialization(final Method setter) {
            this.clazz = setter.getDeclaringClass();
            this.name = setter.getName();
            this.args = setter.getParameterTypes();
        }
    }
}
