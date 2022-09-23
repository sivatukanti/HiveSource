// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.lang.reflect.Member;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import java.lang.reflect.Constructor;
import javax.enterprise.inject.spi.AnnotatedConstructor;

public class AnnotatedConstructorImpl<T> extends AnnotatedCallableImpl<T> implements AnnotatedConstructor<T>
{
    private Constructor<T> javaMember;
    
    public AnnotatedConstructorImpl(final Type baseType, final Set<Type> typeClosure, final Set<Annotation> annotations, final AnnotatedType<T> declaringType, final Constructor javaMember, final boolean isStatic) {
        super(baseType, typeClosure, annotations, declaringType, javaMember, isStatic);
        this.javaMember = (Constructor<T>)javaMember;
    }
    
    public AnnotatedConstructorImpl(final AnnotatedConstructor<T> constructor, final AnnotatedType<T> declaringType) {
        this(constructor.getBaseType(), constructor.getTypeClosure(), constructor.getAnnotations(), declaringType, constructor.getJavaMember(), constructor.isStatic());
    }
    
    public Constructor<T> getJavaMember() {
        return this.javaMember;
    }
}
