// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.util.Iterator;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.UntargettedBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.InjectionPoint;
import java.util.Set;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.Key;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.Provider;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.Binding;

final class BindingProcessor extends AbstractBindingProcessor
{
    private final Initializer initializer;
    
    BindingProcessor(final Errors errors, final Initializer initializer, final ProcessedBindingData bindingData) {
        super(errors, bindingData);
        this.initializer = initializer;
    }
    
    @Override
    public <T> Boolean visit(final Binding<T> command) {
        final Class<?> rawType = command.getKey().getTypeLiteral().getRawType();
        if (Void.class.equals(rawType)) {
            if (command instanceof ProviderInstanceBinding && ((ProviderInstanceBinding)command).getProviderInstance() instanceof ProviderMethod) {
                this.errors.voidProviderMethod();
            }
            else {
                this.errors.missingConstantValues();
            }
            return true;
        }
        if (rawType == Provider.class) {
            this.errors.bindingToProvider();
            return true;
        }
        return command.acceptTargetVisitor((BindingTargetVisitor<? super T, Boolean>)new Processor<T, Boolean>((BindingImpl)command) {
            @Override
            public Boolean visit(final ConstructorBinding<? extends T> binding) {
                this.prepareBinding();
                try {
                    final ConstructorBindingImpl<T> onInjector = ConstructorBindingImpl.create(BindingProcessor.this.injector, (Key<T>)this.key, binding.getConstructor(), this.source, this.scoping, BindingProcessor.this.errors, false);
                    this.scheduleInitialization(onInjector);
                    BindingProcessor.this.putBinding(onInjector);
                }
                catch (ErrorsException e) {
                    BindingProcessor.this.errors.merge(e.getErrors());
                    BindingProcessor.this.putBinding(BindingProcessor.this.invalidBinding(BindingProcessor.this.injector, this.key, this.source));
                }
                return true;
            }
            
            @Override
            public Boolean visit(final InstanceBinding<? extends T> binding) {
                this.prepareBinding();
                final Set<InjectionPoint> injectionPoints = binding.getInjectionPoints();
                final T instance = (T)binding.getInstance();
                final Initializable<T> ref = BindingProcessor.this.initializer.requestInjection(BindingProcessor.this.injector, instance, this.source, injectionPoints);
                final ConstantFactory<? extends T> factory = (ConstantFactory<? extends T>)new ConstantFactory<T>((Initializable<? extends T>)ref);
                final InternalFactory<? extends T> scopedFactory = (InternalFactory<? extends T>)Scoping.scope(this.key, BindingProcessor.this.injector, (InternalFactory<? extends T>)factory, this.source, this.scoping);
                BindingProcessor.this.putBinding(new InstanceBindingImpl<Object>(BindingProcessor.this.injector, this.key, this.source, scopedFactory, injectionPoints, instance));
                return true;
            }
            
            @Override
            public Boolean visit(final ProviderInstanceBinding<? extends T> binding) {
                this.prepareBinding();
                final Provider<? extends T> provider = binding.getProviderInstance();
                final Set<InjectionPoint> injectionPoints = binding.getInjectionPoints();
                final Initializable<Provider<? extends T>> initializable = BindingProcessor.this.initializer.requestInjection(BindingProcessor.this.injector, provider, this.source, injectionPoints);
                final InternalFactory<T> factory = new InternalFactoryToProviderAdapter<T>(initializable, this.source);
                final InternalFactory<? extends T> scopedFactory = (InternalFactory<? extends T>)Scoping.scope(this.key, BindingProcessor.this.injector, (InternalFactory<? extends T>)factory, this.source, this.scoping);
                BindingProcessor.this.putBinding(new ProviderInstanceBindingImpl<Object>(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, provider, injectionPoints));
                return true;
            }
            
            @Override
            public Boolean visit(final ProviderKeyBinding<? extends T> binding) {
                this.prepareBinding();
                final Key<? extends javax.inject.Provider<? extends T>> providerKey = binding.getProviderKey();
                final BoundProviderFactory<T> boundProviderFactory = new BoundProviderFactory<T>(BindingProcessor.this.injector, providerKey, this.source);
                BindingProcessor.this.bindingData.addCreationListener(boundProviderFactory);
                final InternalFactory<? extends T> scopedFactory = (InternalFactory<? extends T>)Scoping.scope(this.key, BindingProcessor.this.injector, (InternalFactory<? extends T>)boundProviderFactory, this.source, this.scoping);
                BindingProcessor.this.putBinding(new LinkedProviderBindingImpl<Object>(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, providerKey));
                return true;
            }
            
            @Override
            public Boolean visit(final LinkedKeyBinding<? extends T> binding) {
                this.prepareBinding();
                final Key<? extends T> linkedKey = binding.getLinkedKey();
                if (this.key.equals(linkedKey)) {
                    BindingProcessor.this.errors.recursiveBinding();
                }
                final FactoryProxy<T> factory = new FactoryProxy<T>(BindingProcessor.this.injector, (Key<T>)this.key, linkedKey, this.source);
                BindingProcessor.this.bindingData.addCreationListener(factory);
                final InternalFactory<? extends T> scopedFactory = (InternalFactory<? extends T>)Scoping.scope(this.key, BindingProcessor.this.injector, (InternalFactory<? extends T>)factory, this.source, this.scoping);
                BindingProcessor.this.putBinding(new LinkedBindingImpl<Object>(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, linkedKey));
                return true;
            }
            
            @Override
            public Boolean visit(final UntargettedBinding<? extends T> untargetted) {
                return false;
            }
            
            @Override
            public Boolean visit(final ExposedBinding<? extends T> binding) {
                throw new IllegalArgumentException("Cannot apply a non-module element");
            }
            
            @Override
            public Boolean visit(final ConvertedConstantBinding<? extends T> binding) {
                throw new IllegalArgumentException("Cannot apply a non-module element");
            }
            
            @Override
            public Boolean visit(final ProviderBinding<? extends T> binding) {
                throw new IllegalArgumentException("Cannot apply a non-module element");
            }
            
            @Override
            protected Boolean visitOther(final Binding<? extends T> binding) {
                throw new IllegalStateException("BindingProcessor should override all visitations");
            }
        });
    }
    
    @Override
    public Boolean visit(final PrivateElements privateElements) {
        for (final Key<?> key : privateElements.getExposedKeys()) {
            this.bindExposed(privateElements, key);
        }
        return false;
    }
    
    private <T> void bindExposed(final PrivateElements privateElements, final Key<T> key) {
        final ExposedKeyFactory<T> exposedKeyFactory = new ExposedKeyFactory<T>(key, privateElements);
        this.bindingData.addCreationListener(exposedKeyFactory);
        this.putBinding(new ExposedBindingImpl<Object>(this.injector, privateElements.getExposedSource(key), key, exposedKeyFactory, privateElements));
    }
}
