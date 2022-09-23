// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import java.lang.annotation.Annotation;
import com.google.inject.Scope;

public interface BindingScopingVisitor<V>
{
    V visitEagerSingleton();
    
    V visitScope(final Scope p0);
    
    V visitScopeAnnotation(final Class<? extends Annotation> p0);
    
    V visitNoScoping();
}
