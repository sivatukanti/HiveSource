// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.Provider;
import com.google.inject.Key;

public final class ProviderLookup<T> implements Element
{
    private final Object source;
    private final Key<T> key;
    private Provider<T> delegate;
    
    public ProviderLookup(final Object source, final Key<T> key) {
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
        this.key = $Preconditions.checkNotNull(key, (Object)"key");
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public Key<T> getKey() {
        return this.key;
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit((ProviderLookup<Object>)this);
    }
    
    public void initializeDelegate(final Provider<T> delegate) {
        $Preconditions.checkState(this.delegate == null, (Object)"delegate already initialized");
        this.delegate = $Preconditions.checkNotNull(delegate, (Object)"delegate");
    }
    
    public void applyTo(final Binder binder) {
        this.initializeDelegate(binder.withSource(this.getSource()).getProvider(this.key));
    }
    
    public Provider<T> getDelegate() {
        return this.delegate;
    }
    
    public Provider<T> getProvider() {
        return new Provider<T>() {
            public T get() {
                $Preconditions.checkState(ProviderLookup.this.delegate != null, (Object)"This Provider cannot be used until the Injector has been created.");
                return ProviderLookup.this.delegate.get();
            }
            
            @Override
            public String toString() {
                return "Provider<" + ProviderLookup.this.key.getTypeLiteral() + ">";
            }
        };
    }
}
