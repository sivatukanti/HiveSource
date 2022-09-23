// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binding;

public abstract class DefaultBindingTargetVisitor<T, V> implements BindingTargetVisitor<T, V>
{
    protected V visitOther(final Binding<? extends T> binding) {
        return null;
    }
    
    public V visit(final InstanceBinding<? extends T> instanceBinding) {
        return this.visitOther(instanceBinding);
    }
    
    public V visit(final ProviderInstanceBinding<? extends T> providerInstanceBinding) {
        return this.visitOther(providerInstanceBinding);
    }
    
    public V visit(final ProviderKeyBinding<? extends T> providerKeyBinding) {
        return this.visitOther(providerKeyBinding);
    }
    
    public V visit(final LinkedKeyBinding<? extends T> linkedKeyBinding) {
        return this.visitOther(linkedKeyBinding);
    }
    
    public V visit(final ExposedBinding<? extends T> exposedBinding) {
        return this.visitOther(exposedBinding);
    }
    
    public V visit(final UntargettedBinding<? extends T> untargettedBinding) {
        return this.visitOther(untargettedBinding);
    }
    
    public V visit(final ConstructorBinding<? extends T> constructorBinding) {
        return this.visitOther(constructorBinding);
    }
    
    public V visit(final ConvertedConstantBinding<? extends T> convertedConstantBinding) {
        return this.visitOther(convertedConstantBinding);
    }
    
    public V visit(final ProviderBinding<? extends T> providerBinding) {
        return this.visitOther(providerBinding);
    }
}
