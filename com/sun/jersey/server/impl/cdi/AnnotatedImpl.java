// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.util.Iterator;
import java.util.Collections;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import javax.enterprise.inject.spi.Annotated;

public class AnnotatedImpl implements Annotated
{
    private Type baseType;
    private Set<Type> typeClosure;
    private Set<Annotation> annotations;
    
    public AnnotatedImpl(final Type baseType, final Set<Type> typeClosure, final Set<Annotation> annotations) {
        this.baseType = baseType;
        this.typeClosure = Collections.unmodifiableSet((Set<? extends Type>)typeClosure);
        this.annotations = Collections.unmodifiableSet((Set<? extends Annotation>)annotations);
    }
    
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        for (final Annotation a : this.annotations) {
            if (annotationType.isInstance(a)) {
                return annotationType.cast(a);
            }
        }
        return null;
    }
    
    public Set<Annotation> getAnnotations() {
        return this.annotations;
    }
    
    public Type getBaseType() {
        return this.baseType;
    }
    
    public Set<Type> getTypeClosure() {
        return this.typeClosure;
    }
    
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return this.getAnnotation(annotationType) != null;
    }
}
