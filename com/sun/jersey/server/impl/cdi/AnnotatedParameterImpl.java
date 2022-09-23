// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

public class AnnotatedParameterImpl<T> extends AnnotatedImpl implements AnnotatedParameter<T>
{
    private AnnotatedCallable<T> declaringCallable;
    private int position;
    
    public AnnotatedParameterImpl(final Type baseType, final Set<Type> typeClosure, final Set<Annotation> annotations, final AnnotatedCallable<T> declaringCallable, final int position) {
        super(baseType, typeClosure, annotations);
        this.declaringCallable = declaringCallable;
        this.position = position;
    }
    
    public AnnotatedParameterImpl(final AnnotatedParameter<? super T> param, final AnnotatedCallable<T> declaringCallable) {
        this(param.getBaseType(), param.getTypeClosure(), param.getAnnotations(), declaringCallable, param.getPosition());
    }
    
    public AnnotatedParameterImpl(final AnnotatedParameter<? super T> param, final Set<Annotation> annotations, final AnnotatedCallable<T> declaringCallable) {
        this(param.getBaseType(), param.getTypeClosure(), annotations, declaringCallable, param.getPosition());
    }
    
    public AnnotatedCallable<T> getDeclaringCallable() {
        return this.declaringCallable;
    }
    
    public int getPosition() {
        return this.position;
    }
}
