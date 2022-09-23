// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.TypeLiteral;
import com.google.inject.Scope;
import com.google.inject.Provider;
import com.google.inject.Module;
import com.google.inject.MembersInjector;
import com.google.inject.Injector;
import com.google.inject.Binding;
import com.google.inject.Binder;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import java.util.Set;

abstract class AbstractBindingProcessor extends AbstractProcessor
{
    private static final Set<Class<?>> FORBIDDEN_TYPES;
    protected final ProcessedBindingData bindingData;
    
    AbstractBindingProcessor(final Errors errors, final ProcessedBindingData bindingData) {
        super(errors);
        this.bindingData = bindingData;
    }
    
    protected <T> UntargettedBindingImpl<T> invalidBinding(final InjectorImpl injector, final Key<T> key, final Object source) {
        return new UntargettedBindingImpl<T>(injector, key, source);
    }
    
    protected void putBinding(final BindingImpl<?> binding) {
        final Key<?> key = binding.getKey();
        final Class<?> rawType = key.getTypeLiteral().getRawType();
        if (AbstractBindingProcessor.FORBIDDEN_TYPES.contains(rawType)) {
            this.errors.cannotBindToGuiceType(rawType.getSimpleName());
            return;
        }
        final BindingImpl<?> original = this.injector.getExistingBinding(key);
        Label_0133: {
            if (original != null) {
                if (this.injector.state.getExplicitBinding(key) != null) {
                    try {
                        if (!this.isOkayDuplicate(original, binding, this.injector.state)) {
                            this.errors.bindingAlreadySet(key, original.getSource());
                            return;
                        }
                        break Label_0133;
                    }
                    catch (Throwable t) {
                        this.errors.errorCheckingDuplicateBinding(key, original.getSource(), t);
                        return;
                    }
                }
                this.errors.jitBindingAlreadySet(key);
                return;
            }
        }
        this.injector.state.parent().blacklist(key, binding.getSource());
        this.injector.state.putBinding(key, binding);
    }
    
    private boolean isOkayDuplicate(BindingImpl<?> original, final BindingImpl<?> binding, final State state) {
        if (original instanceof ExposedBindingImpl) {
            final ExposedBindingImpl exposed = (ExposedBindingImpl)original;
            final InjectorImpl exposedFrom = (InjectorImpl)exposed.getPrivateElements().getInjector();
            return exposedFrom == binding.getInjector();
        }
        original = state.getExplicitBindingsThisLevel().get(binding.getKey());
        return original != null && original.equals(binding);
    }
    
    private <T> void validateKey(final Object source, final Key<T> key) {
        Annotations.checkForMisplacedScopeAnnotations(key.getTypeLiteral().getRawType(), source, this.errors);
    }
    
    static {
        FORBIDDEN_TYPES = $ImmutableSet.of((Class[])new Class[] { AbstractModule.class, Binder.class, Binding.class, Injector.class, Key.class, MembersInjector.class, Module.class, Provider.class, Scope.class, TypeLiteral.class });
    }
    
    abstract class Processor<T, V> extends DefaultBindingTargetVisitor<T, V>
    {
        final Object source;
        final Key<T> key;
        final Class<? super T> rawType;
        Scoping scoping;
        
        Processor(final BindingImpl<T> binding) {
            this.source = binding.getSource();
            this.key = binding.getKey();
            this.rawType = this.key.getTypeLiteral().getRawType();
            this.scoping = binding.getScoping();
        }
        
        protected void prepareBinding() {
            AbstractBindingProcessor.this.validateKey(this.source, (Key<Object>)this.key);
            this.scoping = Scoping.makeInjectable(this.scoping, AbstractBindingProcessor.this.injector, AbstractBindingProcessor.this.errors);
        }
        
        protected void scheduleInitialization(final BindingImpl<?> binding) {
            AbstractBindingProcessor.this.bindingData.addUninitializedBinding(new Runnable() {
                public void run() {
                    try {
                        binding.getInjector().initializeBinding(binding, AbstractBindingProcessor.this.errors.withSource(Processor.this.source));
                    }
                    catch (ErrorsException e) {
                        AbstractBindingProcessor.this.errors.merge(e.getErrors());
                    }
                }
            });
        }
    }
}
