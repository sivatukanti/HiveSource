// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.lang.reflect.Member;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import javax.enterprise.inject.spi.AnnotatedParameter;
import java.util.List;
import javax.enterprise.inject.spi.AnnotatedCallable;

public class AnnotatedCallableImpl<T> extends AnnotatedMemberImpl<T> implements AnnotatedCallable<T>
{
    private List<AnnotatedParameter<T>> parameters;
    
    public AnnotatedCallableImpl(final Type baseType, final Set<Type> typeClosure, final Set<Annotation> annotations, final AnnotatedType<T> declaringType, final Member javaMember, final boolean isStatic) {
        super(baseType, typeClosure, annotations, declaringType, javaMember, isStatic);
    }
    
    public List<AnnotatedParameter<T>> getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final List<AnnotatedParameter<T>> parameters) {
        this.parameters = parameters;
    }
}
