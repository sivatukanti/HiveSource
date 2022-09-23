// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.binder.LinkedBindingBuilder;
import java.util.Iterator;
import com.google.inject.spi.Message;
import com.google.inject.binder.ScopedBindingBuilder;
import java.lang.reflect.Constructor;
import com.google.inject.Provider;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.ConfigurationException;
import java.util.Set;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import com.google.inject.Key;
import com.google.inject.spi.Element;
import java.util.List;
import com.google.inject.Binder;
import com.google.inject.binder.AnnotatedBindingBuilder;

public class BindingBuilder<T> extends AbstractBindingBuilder<T> implements AnnotatedBindingBuilder<T>
{
    public BindingBuilder(final Binder binder, final List<Element> elements, final Object source, final Key<T> key) {
        super(binder, elements, source, key);
    }
    
    public BindingBuilder<T> annotatedWith(final Class<? extends Annotation> annotationType) {
        this.annotatedWithInternal(annotationType);
        return this;
    }
    
    public BindingBuilder<T> annotatedWith(final Annotation annotation) {
        this.annotatedWithInternal(annotation);
        return this;
    }
    
    public BindingBuilder<T> to(final Class<? extends T> implementation) {
        return this.to(Key.get(implementation));
    }
    
    public BindingBuilder<T> to(final TypeLiteral<? extends T> implementation) {
        return this.to(Key.get(implementation));
    }
    
    public BindingBuilder<T> to(final Key<? extends T> linkedKey) {
        $Preconditions.checkNotNull(linkedKey, (Object)"linkedKey");
        this.checkNotTargetted();
        final BindingImpl<T> base = this.getBinding();
        this.setBinding(new LinkedBindingImpl<T>(base.getSource(), base.getKey(), base.getScoping(), linkedKey));
        return this;
    }
    
    public void toInstance(final T instance) {
        this.checkNotTargetted();
        Set<InjectionPoint> injectionPoints;
        if (instance != null) {
            try {
                injectionPoints = InjectionPoint.forInstanceMethodsAndFields(instance.getClass());
            }
            catch (ConfigurationException e) {
                this.copyErrorsToBinder(e);
                injectionPoints = e.getPartialValue();
            }
        }
        else {
            this.binder.addError("Binding to null instances is not allowed. Use toProvider(Providers.of(null)) if this is your intended behaviour.", new Object[0]);
            injectionPoints = (Set<InjectionPoint>)$ImmutableSet.of();
        }
        final BindingImpl<T> base = this.getBinding();
        this.setBinding(new InstanceBindingImpl<T>(base.getSource(), base.getKey(), Scoping.EAGER_SINGLETON, injectionPoints, instance));
    }
    
    public BindingBuilder<T> toProvider(final Provider<? extends T> provider) {
        $Preconditions.checkNotNull(provider, (Object)"provider");
        this.checkNotTargetted();
        Set<InjectionPoint> injectionPoints;
        try {
            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(provider.getClass());
        }
        catch (ConfigurationException e) {
            this.copyErrorsToBinder(e);
            injectionPoints = e.getPartialValue();
        }
        final BindingImpl<T> base = this.getBinding();
        this.setBinding(new ProviderInstanceBindingImpl<T>(base.getSource(), base.getKey(), base.getScoping(), injectionPoints, provider));
        return this;
    }
    
    public BindingBuilder<T> toProvider(final Class<? extends javax.inject.Provider<? extends T>> providerType) {
        return this.toProvider(Key.get(providerType));
    }
    
    public BindingBuilder<T> toProvider(final TypeLiteral<? extends javax.inject.Provider<? extends T>> providerType) {
        return this.toProvider(Key.get(providerType));
    }
    
    public BindingBuilder<T> toProvider(final Key<? extends javax.inject.Provider<? extends T>> providerKey) {
        $Preconditions.checkNotNull(providerKey, (Object)"providerKey");
        this.checkNotTargetted();
        final BindingImpl<T> base = this.getBinding();
        this.setBinding(new LinkedProviderBindingImpl<T>(base.getSource(), base.getKey(), base.getScoping(), providerKey));
        return this;
    }
    
    public <S extends T> ScopedBindingBuilder toConstructor(final Constructor<S> constructor) {
        return this.toConstructor(constructor, (TypeLiteral<? extends S>)TypeLiteral.get((Class<? extends S>)constructor.getDeclaringClass()));
    }
    
    public <S extends T> ScopedBindingBuilder toConstructor(final Constructor<S> constructor, final TypeLiteral<? extends S> type) {
        $Preconditions.checkNotNull(constructor, (Object)"constructor");
        $Preconditions.checkNotNull(type, (Object)"type");
        this.checkNotTargetted();
        final BindingImpl<T> base = this.getBinding();
        Set<InjectionPoint> injectionPoints;
        try {
            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(type);
        }
        catch (ConfigurationException e) {
            this.copyErrorsToBinder(e);
            injectionPoints = e.getPartialValue();
        }
        try {
            final InjectionPoint constructorPoint = InjectionPoint.forConstructor(constructor, type);
            this.setBinding(new ConstructorBindingImpl<T>(base.getKey(), base.getSource(), base.getScoping(), constructorPoint, injectionPoints));
        }
        catch (ConfigurationException e) {
            this.copyErrorsToBinder(e);
        }
        return this;
    }
    
    @Override
    public String toString() {
        return "BindingBuilder<" + this.getBinding().getKey().getTypeLiteral() + ">";
    }
    
    private void copyErrorsToBinder(final ConfigurationException e) {
        for (final Message message : e.getErrorMessages()) {
            this.binder.addError(message);
        }
    }
}
