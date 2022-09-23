// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedTypeImpl<T> extends AnnotatedImpl implements AnnotatedType<T>
{
    private Set<AnnotatedConstructor<T>> constructors;
    private Set<AnnotatedField<? super T>> fields;
    private Class<T> javaClass;
    private Set<AnnotatedMethod<? super T>> methods;
    
    public AnnotatedTypeImpl(final Type baseType, final Set<Type> typeClosure, final Set<Annotation> annotations, final Class<T> javaClass) {
        super(baseType, typeClosure, annotations);
        this.javaClass = javaClass;
    }
    
    public AnnotatedTypeImpl(final AnnotatedType type) {
        this(type.getBaseType(), type.getTypeClosure(), type.getAnnotations(), type.getJavaClass());
    }
    
    public Set<AnnotatedConstructor<T>> getConstructors() {
        return this.constructors;
    }
    
    public void setConstructors(final Set<AnnotatedConstructor<T>> constructors) {
        this.constructors = constructors;
    }
    
    public Set<AnnotatedField<? super T>> getFields() {
        return this.fields;
    }
    
    public void setFields(final Set<AnnotatedField<? super T>> fields) {
        this.fields = fields;
    }
    
    public Class<T> getJavaClass() {
        return this.javaClass;
    }
    
    public Set<AnnotatedMethod<? super T>> getMethods() {
        return this.methods;
    }
    
    public void setMethods(final Set<AnnotatedMethod<? super T>> methods) {
        this.methods = methods;
    }
}
