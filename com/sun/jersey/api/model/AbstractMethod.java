// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.AnnotatedElement;

public abstract class AbstractMethod implements AnnotatedElement
{
    private Method method;
    private Annotation[] annotations;
    private AbstractResource resource;
    
    public AbstractMethod(final AbstractResource resource, final Method method, final Annotation[] annotations) {
        this.method = method;
        this.annotations = annotations;
        this.resource = resource;
    }
    
    public AbstractResource getResource() {
        return this.resource;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        for (final Annotation a : this.annotations) {
            if (annotationType == a.annotationType()) {
                return annotationType.cast(a);
            }
        }
        return null;
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.annotations.clone();
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.annotations.clone();
    }
    
    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return this.getAnnotation(annotationType) != null;
    }
}
