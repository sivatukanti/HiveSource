// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binding;

public abstract class DefaultElementVisitor<V> implements ElementVisitor<V>
{
    protected V visitOther(final Element element) {
        return null;
    }
    
    public V visit(final Message message) {
        return this.visitOther(message);
    }
    
    public <T> V visit(final Binding<T> binding) {
        return this.visitOther(binding);
    }
    
    public V visit(final InterceptorBinding interceptorBinding) {
        return this.visitOther(interceptorBinding);
    }
    
    public V visit(final ScopeBinding scopeBinding) {
        return this.visitOther(scopeBinding);
    }
    
    public V visit(final TypeConverterBinding typeConverterBinding) {
        return this.visitOther(typeConverterBinding);
    }
    
    public <T> V visit(final ProviderLookup<T> providerLookup) {
        return this.visitOther(providerLookup);
    }
    
    public V visit(final InjectionRequest<?> injectionRequest) {
        return this.visitOther(injectionRequest);
    }
    
    public V visit(final StaticInjectionRequest staticInjectionRequest) {
        return this.visitOther(staticInjectionRequest);
    }
    
    public V visit(final PrivateElements privateElements) {
        return this.visitOther(privateElements);
    }
    
    public <T> V visit(final MembersInjectorLookup<T> lookup) {
        return this.visitOther(lookup);
    }
    
    public V visit(final TypeListenerBinding binding) {
        return this.visitOther(binding);
    }
    
    public V visit(final DisableCircularProxiesOption option) {
        return this.visitOther(option);
    }
    
    public V visit(final RequireExplicitBindingsOption option) {
        return this.visitOther(option);
    }
}
