// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

import java.lang.reflect.AccessibleObject;
import java.lang.annotation.Annotation;

public class AnnotatedContext implements ComponentContext
{
    private Annotation[] annotations;
    private AccessibleObject ao;
    
    public AnnotatedContext() {
    }
    
    public AnnotatedContext(final Annotation[] annotations) {
        this(null, annotations);
    }
    
    public AnnotatedContext(final AccessibleObject ao) {
        this(ao, null);
    }
    
    public AnnotatedContext(final AccessibleObject ao, final Annotation[] annotations) {
        this.ao = ao;
        this.annotations = annotations;
    }
    
    public void setAnnotations(final Annotation[] annotations) {
        this.annotations = annotations;
    }
    
    public void setAccessibleObject(final AccessibleObject ao) {
        this.ao = ao;
    }
    
    @Override
    public AccessibleObject getAccesibleObject() {
        return this.ao;
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.annotations;
    }
}
