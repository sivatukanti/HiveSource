// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import java.lang.annotation.Annotation;
import com.google.inject.Scope;

public class DefaultBindingScopingVisitor<V> implements BindingScopingVisitor<V>
{
    protected V visitOther() {
        return null;
    }
    
    public V visitEagerSingleton() {
        return this.visitOther();
    }
    
    public V visitScope(final Scope scope) {
        return this.visitOther();
    }
    
    public V visitScopeAnnotation(final Class<? extends Annotation> scopeAnnotation) {
        return this.visitOther();
    }
    
    public V visitNoScoping() {
        return this.visitOther();
    }
}
