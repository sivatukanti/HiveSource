// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.binder;

import com.google.inject.Scope;
import java.lang.annotation.Annotation;

public interface ScopedBindingBuilder
{
    void in(final Class<? extends Annotation> p0);
    
    void in(final Scope p0);
    
    void asEagerSingleton();
}
