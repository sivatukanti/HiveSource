// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.binder;

import java.lang.annotation.Annotation;

public interface AnnotatedBindingBuilder<T> extends LinkedBindingBuilder<T>
{
    LinkedBindingBuilder<T> annotatedWith(final Class<? extends Annotation> p0);
    
    LinkedBindingBuilder<T> annotatedWith(final Annotation p0);
}
