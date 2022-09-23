// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

public final class TypeListenerBinding implements Element
{
    private final Object source;
    private final Matcher<? super TypeLiteral<?>> typeMatcher;
    private final TypeListener listener;
    
    TypeListenerBinding(final Object source, final TypeListener listener, final Matcher<? super TypeLiteral<?>> typeMatcher) {
        this.source = source;
        this.listener = listener;
        this.typeMatcher = typeMatcher;
    }
    
    public TypeListener getListener() {
        return this.listener;
    }
    
    public Matcher<? super TypeLiteral<?>> getTypeMatcher() {
        return this.typeMatcher;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).bindListener(this.typeMatcher, this.listener);
    }
}
