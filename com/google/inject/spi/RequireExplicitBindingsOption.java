// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.internal.util.$Preconditions;

public final class RequireExplicitBindingsOption implements Element
{
    private final Object source;
    
    RequireExplicitBindingsOption(final Object source) {
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).requireExplicitBindings();
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
