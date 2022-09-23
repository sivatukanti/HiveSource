// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.binder.AnnotatedElementBuilder;

public interface PrivateBinder extends Binder
{
    void expose(final Key<?> p0);
    
    AnnotatedElementBuilder expose(final Class<?> p0);
    
    AnnotatedElementBuilder expose(final TypeLiteral<?> p0);
    
    PrivateBinder withSource(final Object p0);
    
    PrivateBinder skipSources(final Class... p0);
}
