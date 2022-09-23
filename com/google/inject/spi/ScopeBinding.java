// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.Scope;
import java.lang.annotation.Annotation;

public final class ScopeBinding implements Element
{
    private final Object source;
    private final Class<? extends Annotation> annotationType;
    private final Scope scope;
    
    ScopeBinding(final Object source, final Class<? extends Annotation> annotationType, final Scope scope) {
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
        this.annotationType = $Preconditions.checkNotNull(annotationType, (Object)"annotationType");
        this.scope = $Preconditions.checkNotNull(scope, (Object)"scope");
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }
    
    public Scope getScope() {
        return this.scope;
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).bindScope(this.annotationType, this.scope);
    }
}
