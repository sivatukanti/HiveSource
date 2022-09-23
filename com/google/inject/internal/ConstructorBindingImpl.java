// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$ToStringBuilder;
import java.lang.reflect.Constructor;
import com.google.inject.Binder;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import java.util.Map;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.spi.Dependency;
import java.lang.annotation.Annotation;
import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.$Classes;
import java.lang.reflect.Modifier;
import java.util.Set;
import com.google.inject.Key;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ConstructorBinding;

final class ConstructorBindingImpl<T> extends BindingImpl<T> implements ConstructorBinding<T>
{
    private final Factory<T> factory;
    private final InjectionPoint constructorInjectionPoint;
    
    private ConstructorBindingImpl(final InjectorImpl injector, final Key<T> key, final Object source, final InternalFactory<? extends T> scopedFactory, final Scoping scoping, final Factory<T> factory, final InjectionPoint constructorInjectionPoint) {
        super(injector, key, source, scopedFactory, scoping);
        this.factory = factory;
        this.constructorInjectionPoint = constructorInjectionPoint;
    }
    
    public ConstructorBindingImpl(final Key<T> key, final Object source, final Scoping scoping, final InjectionPoint constructorInjectionPoint, final Set<InjectionPoint> injectionPoints) {
        super(source, key, scoping);
        this.factory = new Factory<T>(false, key);
        final ConstructionProxy<T> constructionProxy = new DefaultConstructionProxyFactory<T>(constructorInjectionPoint).create();
        this.constructorInjectionPoint = constructorInjectionPoint;
        ((Factory<Object>)this.factory).constructorInjector = (ConstructorInjector<Object>)new ConstructorInjector(injectionPoints, (ConstructionProxy<T>)constructionProxy, null, null);
    }
    
    static <T> ConstructorBindingImpl<T> create(final InjectorImpl injector, final Key<T> key, InjectionPoint constructorInjector, final Object source, Scoping scoping, final Errors errors, final boolean failIfNotLinked) throws ErrorsException {
        final int numErrors = errors.size();
        final Class<? super T> rawType = (Class<? super T>)((constructorInjector == null) ? key.getTypeLiteral().getRawType() : constructorInjector.getDeclaringType().getRawType());
        if (Modifier.isAbstract(rawType.getModifiers())) {
            errors.missingImplementation(key);
        }
        if ($Classes.isInnerClass(rawType)) {
            errors.cannotInjectInnerClass(rawType);
        }
        errors.throwIfNewErrors(numErrors);
        if (constructorInjector == null) {
            try {
                constructorInjector = InjectionPoint.forConstructorOf(key.getTypeLiteral());
            }
            catch (ConfigurationException e) {
                throw errors.merge(e.getErrorMessages()).toException();
            }
        }
        if (!scoping.isExplicitlyScoped()) {
            final Class<?> annotatedType = constructorInjector.getMember().getDeclaringClass();
            final Class<? extends Annotation> scopeAnnotation = Annotations.findScopeAnnotation(errors, annotatedType);
            if (scopeAnnotation != null) {
                scoping = Scoping.makeInjectable(Scoping.forAnnotation(scopeAnnotation), injector, errors.withSource(rawType));
            }
        }
        errors.throwIfNewErrors(numErrors);
        final Factory<T> factoryFactory = new Factory<T>(failIfNotLinked, key);
        final InternalFactory<? extends T> scopedFactory = Scoping.scope(key, injector, (InternalFactory<? extends T>)factoryFactory, source, scoping);
        return new ConstructorBindingImpl<T>(injector, key, source, scopedFactory, scoping, factoryFactory, constructorInjector);
    }
    
    public void initialize(final InjectorImpl injector, final Errors errors) throws ErrorsException {
        ((Factory<Object>)this.factory).allowCircularProxy = !injector.options.disableCircularProxies;
        ((Factory<Object>)this.factory).constructorInjector = (ConstructorInjector<Object>)injector.constructors.get(this.constructorInjectionPoint, errors);
    }
    
    boolean isInitialized() {
        return ((Factory<Object>)this.factory).constructorInjector != null;
    }
    
    InjectionPoint getInternalConstructor() {
        if (((Factory<Object>)this.factory).constructorInjector != null) {
            return ((Factory<Object>)this.factory).constructorInjector.getConstructionProxy().getInjectionPoint();
        }
        return this.constructorInjectionPoint;
    }
    
    Set<Dependency<?>> getInternalDependencies() {
        final $ImmutableSet.Builder<InjectionPoint> builder = $ImmutableSet.builder();
        if (((Factory<Object>)this.factory).constructorInjector == null) {
            builder.add(this.constructorInjectionPoint);
            try {
                builder.addAll(InjectionPoint.forInstanceMethodsAndFields(this.constructorInjectionPoint.getDeclaringType()));
            }
            catch (ConfigurationException ignored) {}
        }
        else {
            builder.add(this.getConstructor()).addAll(this.getInjectableMembers());
        }
        return Dependency.forInjectionPoints(builder.build());
    }
    
    public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> visitor) {
        $Preconditions.checkState(((Factory<Object>)this.factory).constructorInjector != null, (Object)"not initialized");
        return visitor.visit((ConstructorBinding<? extends T>)this);
    }
    
    public InjectionPoint getConstructor() {
        $Preconditions.checkState(((Factory<Object>)this.factory).constructorInjector != null, (Object)"Binding is not ready");
        return ((Factory<Object>)this.factory).constructorInjector.getConstructionProxy().getInjectionPoint();
    }
    
    public Set<InjectionPoint> getInjectableMembers() {
        $Preconditions.checkState(((Factory<Object>)this.factory).constructorInjector != null, (Object)"Binding is not ready");
        return (Set<InjectionPoint>)((Factory<Object>)this.factory).constructorInjector.getInjectableMembers();
    }
    
    public Map<Method, List<MethodInterceptor>> getMethodInterceptors() {
        $Preconditions.checkState(((Factory<Object>)this.factory).constructorInjector != null, (Object)"Binding is not ready");
        return (Map<Method, List<MethodInterceptor>>)((Factory<Object>)this.factory).constructorInjector.getConstructionProxy().getMethodInterceptors();
    }
    
    public Set<Dependency<?>> getDependencies() {
        return Dependency.forInjectionPoints(new $ImmutableSet.Builder<InjectionPoint>().add(this.getConstructor()).addAll(this.getInjectableMembers()).build());
    }
    
    @Override
    protected BindingImpl<T> withScoping(final Scoping scoping) {
        return new ConstructorBindingImpl(null, this.getKey(), this.getSource(), this.factory, scoping, (Factory<Object>)this.factory, this.constructorInjectionPoint);
    }
    
    @Override
    protected BindingImpl<T> withKey(final Key<T> key) {
        return new ConstructorBindingImpl(null, (Key<Object>)key, this.getSource(), this.factory, this.getScoping(), (Factory<Object>)this.factory, this.constructorInjectionPoint);
    }
    
    public void applyTo(final Binder binder) {
        final InjectionPoint constructor = this.getConstructor();
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).toConstructor((Constructor<Object>)this.getConstructor().getMember(), constructor.getDeclaringType()));
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(ConstructorBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", this.getScoping()).toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ConstructorBindingImpl) {
            final ConstructorBindingImpl<?> o = (ConstructorBindingImpl<?>)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.constructorInjectionPoint, o.constructorInjectionPoint);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.getKey(), this.getScoping(), this.constructorInjectionPoint);
    }
    
    private static class Factory<T> implements InternalFactory<T>
    {
        private final boolean failIfNotLinked;
        private final Key<?> key;
        private boolean allowCircularProxy;
        private ConstructorInjector<T> constructorInjector;
        
        Factory(final boolean failIfNotLinked, final Key<?> key) {
            this.failIfNotLinked = failIfNotLinked;
            this.key = key;
        }
        
        public T get(final Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) throws ErrorsException {
            $Preconditions.checkState(this.constructorInjector != null, (Object)"Constructor not ready");
            if (this.failIfNotLinked && !linked) {
                throw errors.jitDisabled(this.key).toException();
            }
            return (T)this.constructorInjector.construct(errors, context, dependency.getKey().getTypeLiteral().getRawType(), this.allowCircularProxy);
        }
    }
}
