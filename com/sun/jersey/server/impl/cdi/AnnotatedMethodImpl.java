// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.lang.reflect.Member;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import javax.enterprise.inject.spi.AnnotatedMethod;

public class AnnotatedMethodImpl<T> extends AnnotatedCallableImpl<T> implements AnnotatedMethod<T>
{
    private Method javaMember;
    
    public AnnotatedMethodImpl(final Type baseType, final Set<Type> typeClosure, final Set<Annotation> annotations, final AnnotatedType<T> declaringType, final Method javaMember, final boolean isStatic) {
        super(baseType, typeClosure, annotations, declaringType, javaMember, isStatic);
        this.javaMember = javaMember;
    }
    
    public AnnotatedMethodImpl(final AnnotatedMethod<? super T> method, final AnnotatedType<T> declaringType) {
        this(method.getBaseType(), method.getTypeClosure(), method.getAnnotations(), declaringType, method.getJavaMember(), method.isStatic());
    }
    
    public AnnotatedMethodImpl(final AnnotatedMethod<? super T> method, final Set<Annotation> annotations, final AnnotatedType<T> declaringType) {
        this(method.getBaseType(), method.getTypeClosure(), annotations, declaringType, method.getJavaMember(), method.isStatic());
    }
    
    public Method getJavaMember() {
        return this.javaMember;
    }
}
