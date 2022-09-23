// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.binder;

import java.lang.annotation.Annotation;

public interface AnnotatedConstantBindingBuilder
{
    ConstantBindingBuilder annotatedWith(final Class<? extends Annotation> p0);
    
    ConstantBindingBuilder annotatedWith(final Annotation p0);
}
