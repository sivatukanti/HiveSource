// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;

public final class MembersInjectorLookup<T> implements Element
{
    private final Object source;
    private final TypeLiteral<T> type;
    private MembersInjector<T> delegate;
    
    public MembersInjectorLookup(final Object source, final TypeLiteral<T> type) {
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
        this.type = $Preconditions.checkNotNull(type, (Object)"type");
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public TypeLiteral<T> getType() {
        return this.type;
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit((MembersInjectorLookup<Object>)this);
    }
    
    public void initializeDelegate(final MembersInjector<T> delegate) {
        $Preconditions.checkState(this.delegate == null, (Object)"delegate already initialized");
        this.delegate = $Preconditions.checkNotNull(delegate, (Object)"delegate");
    }
    
    public void applyTo(final Binder binder) {
        this.initializeDelegate(binder.withSource(this.getSource()).getMembersInjector(this.type));
    }
    
    public MembersInjector<T> getDelegate() {
        return this.delegate;
    }
    
    public MembersInjector<T> getMembersInjector() {
        return new MembersInjector<T>() {
            public void injectMembers(final T instance) {
                $Preconditions.checkState(MembersInjectorLookup.this.delegate != null, (Object)"This MembersInjector cannot be used until the Injector has been created.");
                MembersInjectorLookup.this.delegate.injectMembers(instance);
            }
            
            @Override
            public String toString() {
                return "MembersInjector<" + MembersInjectorLookup.this.type + ">";
            }
        };
    }
}
