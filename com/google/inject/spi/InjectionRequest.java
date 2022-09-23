// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import java.util.Set;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.TypeLiteral;

public final class InjectionRequest<T> implements Element
{
    private final Object source;
    private final TypeLiteral<T> type;
    private final T instance;
    
    public InjectionRequest(final Object source, final TypeLiteral<T> type, final T instance) {
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
        this.type = $Preconditions.checkNotNull(type, (Object)"type");
        this.instance = $Preconditions.checkNotNull(instance, (Object)"instance");
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public T getInstance() {
        return this.instance;
    }
    
    public TypeLiteral<T> getType() {
        return this.type;
    }
    
    public Set<InjectionPoint> getInjectionPoints() throws ConfigurationException {
        return InjectionPoint.forInstanceMethodsAndFields(this.instance.getClass());
    }
    
    public <R> R acceptVisitor(final ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).requestInjection(this.type, this.instance);
    }
}
