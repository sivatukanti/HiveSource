// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Preconditions;
import java.lang.annotation.Annotation;
import com.google.inject.Key;
import com.google.inject.Binder;
import com.google.inject.binder.AnnotatedElementBuilder;

public class ExposureBuilder<T> implements AnnotatedElementBuilder
{
    private final Binder binder;
    private final Object source;
    private Key<T> key;
    
    public ExposureBuilder(final Binder binder, final Object source, final Key<T> key) {
        this.binder = binder;
        this.source = source;
        this.key = key;
    }
    
    protected void checkNotAnnotated() {
        if (this.key.getAnnotationType() != null) {
            this.binder.addError("More than one annotation is specified for this binding.", new Object[0]);
        }
    }
    
    public void annotatedWith(final Class<? extends Annotation> annotationType) {
        $Preconditions.checkNotNull(annotationType, (Object)"annotationType");
        this.checkNotAnnotated();
        this.key = Key.get(this.key.getTypeLiteral(), annotationType);
    }
    
    public void annotatedWith(final Annotation annotation) {
        $Preconditions.checkNotNull(annotation, (Object)"annotation");
        this.checkNotAnnotated();
        this.key = Key.get(this.key.getTypeLiteral(), annotation);
    }
    
    public Key<?> getKey() {
        return this.key;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    @Override
    public String toString() {
        return "AnnotatedElementBuilder";
    }
}
