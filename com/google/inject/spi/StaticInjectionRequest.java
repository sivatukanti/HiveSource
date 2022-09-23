// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import java.util.Set;
import com.google.inject.internal.util.$Preconditions;

public final class StaticInjectionRequest implements Element
{
    private final Object source;
    private final Class<?> type;
    
    StaticInjectionRequest(final Object source, final Class<?> type) {
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
        this.type = $Preconditions.checkNotNull(type, (Object)"type");
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public Set<InjectionPoint> getInjectionPoints() throws ConfigurationException {
        return InjectionPoint.forStaticMethodsAndFields(this.type);
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).requestStaticInjection(this.type);
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
