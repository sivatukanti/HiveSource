// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import com.google.inject.internal.util.$Lists;
import com.google.inject.util.Providers;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.internal.util.$Objects;
import com.google.inject.Binder;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.Stage;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import java.lang.annotation.Annotation;
import com.google.inject.internal.util.$ImmutableMap;
import java.lang.reflect.GenericArrayType;
import com.google.inject.ProvidedBy;
import com.google.inject.ImplementedBy;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.Dependency;
import java.util.HashSet;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.InjectionPoint;
import java.util.Set;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$SourceProvider;
import com.google.inject.MembersInjector;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.spi.Message;
import com.google.inject.ConfigurationException;
import java.util.List;
import java.util.Iterator;
import com.google.inject.Binding;
import com.google.inject.internal.util.$Maps;
import com.google.inject.internal.util.$Nullable;
import com.google.inject.Key;
import java.util.Map;
import com.google.inject.TypeLiteral;
import com.google.inject.Injector;

final class InjectorImpl implements Injector, Lookups
{
    public static final TypeLiteral<String> STRING_TYPE;
    final State state;
    final InjectorImpl parent;
    final BindingsMultimap bindingsMultimap;
    final InjectorOptions options;
    final Map<Key<?>, BindingImpl<?>> jitBindings;
    Lookups lookups;
    final ConstructorInjectorStore constructors;
    MembersInjectorStore membersInjectorStore;
    final ThreadLocal<Object[]> localContext;
    
    InjectorImpl(@$Nullable final InjectorImpl parent, final State state, final InjectorOptions injectorOptions) {
        this.bindingsMultimap = new BindingsMultimap();
        this.jitBindings = (Map<Key<?>, BindingImpl<?>>)$Maps.newHashMap();
        this.lookups = new DeferredLookups(this);
        this.constructors = new ConstructorInjectorStore(this);
        this.parent = parent;
        this.state = state;
        this.options = injectorOptions;
        if (parent != null) {
            this.localContext = parent.localContext;
        }
        else {
            this.localContext = new ThreadLocal<Object[]>() {
                @Override
                protected Object[] initialValue() {
                    return new Object[1];
                }
            };
        }
    }
    
    void index() {
        for (final Binding<?> binding : this.state.getExplicitBindingsThisLevel().values()) {
            this.index(binding);
        }
    }
    
     <T> void index(final Binding<T> binding) {
        this.bindingsMultimap.put(binding.getKey().getTypeLiteral(), binding);
    }
    
    public <T> List<Binding<T>> findBindingsByType(final TypeLiteral<T> type) {
        return this.bindingsMultimap.getAll(type);
    }
    
    public <T> BindingImpl<T> getBinding(final Key<T> key) {
        final Errors errors = new Errors(key);
        try {
            final BindingImpl<T> result = this.getBindingOrThrow(key, errors, JitLimitation.EXISTING_JIT);
            errors.throwConfigurationExceptionIfErrorsExist();
            return result;
        }
        catch (ErrorsException e) {
            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
        }
    }
    
    public <T> BindingImpl<T> getExistingBinding(final Key<T> key) {
        final BindingImpl<T> explicitBinding = this.state.getExplicitBinding(key);
        if (explicitBinding != null) {
            return explicitBinding;
        }
        synchronized (this.state.lock()) {
            for (InjectorImpl injector = this; injector != null; injector = injector.parent) {
                final BindingImpl<T> jitBinding = (BindingImpl<T>)injector.jitBindings.get(key);
                if (jitBinding != null) {
                    return jitBinding;
                }
            }
        }
        if (isProvider(key)) {
            try {
                final Key<?> providedKey = getProvidedKey((Key<Provider<?>>)key, new Errors());
                if (this.getExistingBinding(providedKey) != null) {
                    return (BindingImpl<T>)this.getBinding((Key<Object>)key);
                }
            }
            catch (ErrorsException e) {
                throw new ConfigurationException(e.getErrors().getMessages());
            }
        }
        return null;
    }
    
     <T> BindingImpl<T> getBindingOrThrow(final Key<T> key, final Errors errors, final JitLimitation jitType) throws ErrorsException {
        final BindingImpl<T> binding = this.state.getExplicitBinding(key);
        if (binding != null) {
            return binding;
        }
        return (BindingImpl<T>)this.getJustInTimeBinding((Key<Object>)key, errors, jitType);
    }
    
    public <T> Binding<T> getBinding(final Class<T> type) {
        return (Binding<T>)this.getBinding((Key<Object>)Key.get((Class<T>)type));
    }
    
    public Injector getParent() {
        return this.parent;
    }
    
    public Injector createChildInjector(final Iterable<? extends Module> modules) {
        return new InternalInjectorCreator().parentInjector(this).addModules(modules).build();
    }
    
    public Injector createChildInjector(final Module... modules) {
        return this.createChildInjector($ImmutableList.of(modules));
    }
    
    private <T> BindingImpl<T> getJustInTimeBinding(final Key<T> key, final Errors errors, final JitLimitation jitType) throws ErrorsException {
        final boolean jitOverride = isProvider(key) || isTypeLiteral(key) || isMembersInjector(key);
        synchronized (this.state.lock()) {
            InjectorImpl injector = this;
            while (injector != null) {
                final BindingImpl<T> binding = (BindingImpl<T>)injector.jitBindings.get(key);
                if (binding != null) {
                    if (this.options.jitDisabled && jitType == JitLimitation.NO_JIT && !jitOverride && !(binding instanceof ConvertedConstantBindingImpl)) {
                        throw errors.jitDisabled(key).toException();
                    }
                    return binding;
                }
                else {
                    injector = injector.parent;
                }
            }
            return this.createJustInTimeBindingRecursive(key, errors, this.options.jitDisabled, jitType);
        }
    }
    
    private static boolean isProvider(final Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(Provider.class);
    }
    
    private static boolean isTypeLiteral(final Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(TypeLiteral.class);
    }
    
    private static <T> Key<T> getProvidedKey(final Key<Provider<T>> key, final Errors errors) throws ErrorsException {
        final Type providerType = key.getTypeLiteral().getType();
        if (!(providerType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawProvider().toException();
        }
        final Type entryType = ((ParameterizedType)providerType).getActualTypeArguments()[0];
        final Key<T> providedKey = (Key<T>)key.ofType(entryType);
        return providedKey;
    }
    
    private static boolean isMembersInjector(final Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(MembersInjector.class) && key.getAnnotationType() == null;
    }
    
    private <T> BindingImpl<MembersInjector<T>> createMembersInjectorBinding(final Key<MembersInjector<T>> key, final Errors errors) throws ErrorsException {
        final Type membersInjectorType = key.getTypeLiteral().getType();
        if (!(membersInjectorType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawMembersInjector().toException();
        }
        final TypeLiteral<T> instanceType = (TypeLiteral<T>)TypeLiteral.get(((ParameterizedType)membersInjectorType).getActualTypeArguments()[0]);
        final MembersInjector<T> membersInjector = this.membersInjectorStore.get(instanceType, errors);
        final InternalFactory<MembersInjector<T>> factory = new ConstantFactory<MembersInjector<T>>(Initializables.of(membersInjector));
        return new InstanceBindingImpl<MembersInjector<T>>(this, key, $SourceProvider.UNKNOWN_SOURCE, factory, (Set<InjectionPoint>)$ImmutableSet.of(), membersInjector);
    }
    
    private <T> BindingImpl<Provider<T>> createProviderBinding(final Key<Provider<T>> key, final Errors errors) throws ErrorsException {
        final Key<T> providedKey = getProvidedKey(key, errors);
        final BindingImpl<T> delegate = this.getBindingOrThrow(providedKey, errors, JitLimitation.NO_JIT);
        return (BindingImpl<Provider<T>>)new ProviderBindingImpl(this, (Key<Provider<Object>>)key, (Binding<Object>)delegate);
    }
    
    private <T> BindingImpl<T> convertConstantStringBinding(final Key<T> key, final Errors errors) throws ErrorsException {
        final Key<String> stringKey = key.ofType(InjectorImpl.STRING_TYPE);
        final BindingImpl<String> stringBinding = this.state.getExplicitBinding(stringKey);
        if (stringBinding == null || !stringBinding.isConstant()) {
            return null;
        }
        final String stringValue = stringBinding.getProvider().get();
        final Object source = stringBinding.getSource();
        final TypeLiteral<T> type = key.getTypeLiteral();
        final TypeConverterBinding typeConverterBinding = this.state.getConverter(stringValue, type, errors, source);
        if (typeConverterBinding == null) {
            return null;
        }
        try {
            final T converted = (T)typeConverterBinding.getTypeConverter().convert(stringValue, type);
            if (converted == null) {
                throw errors.converterReturnedNull(stringValue, source, type, typeConverterBinding).toException();
            }
            if (!type.getRawType().isInstance(converted)) {
                throw errors.conversionTypeError(stringValue, source, type, typeConverterBinding, converted).toException();
            }
            return new ConvertedConstantBindingImpl<T>(this, key, converted, stringBinding, typeConverterBinding);
        }
        catch (ErrorsException e) {
            throw e;
        }
        catch (RuntimeException e2) {
            throw errors.conversionError(stringValue, source, type, typeConverterBinding, e2).toException();
        }
    }
    
     <T> void initializeBinding(final BindingImpl<T> binding, final Errors errors) throws ErrorsException {
        if (binding instanceof ConstructorBindingImpl) {
            ((ConstructorBindingImpl)binding).initialize(this, errors);
        }
    }
    
     <T> void initializeJitBinding(final BindingImpl<T> binding, final Errors errors) throws ErrorsException {
        if (binding instanceof ConstructorBindingImpl) {
            final Key<T> key = binding.getKey();
            this.jitBindings.put(key, binding);
            boolean successful = false;
            final ConstructorBindingImpl cb = (ConstructorBindingImpl)binding;
            try {
                cb.initialize(this, errors);
                successful = true;
            }
            finally {
                if (!successful) {
                    this.removeFailedJitBinding(key, null);
                    this.cleanup(binding, new HashSet<Key>());
                }
            }
        }
    }
    
    private boolean cleanup(final BindingImpl<?> binding, final Set<Key> encountered) {
        boolean bindingFailed = false;
        final Set<Dependency<?>> deps = this.getInternalDependencies(binding);
        for (final Dependency dep : deps) {
            final Key<?> depKey = dep.getKey();
            InjectionPoint ip = dep.getInjectionPoint();
            if (encountered.add(depKey)) {
                final BindingImpl depBinding = this.jitBindings.get(depKey);
                if (depBinding != null) {
                    boolean failed = this.cleanup(depBinding, encountered);
                    if (depBinding instanceof ConstructorBindingImpl) {
                        final ConstructorBindingImpl ctorBinding = (ConstructorBindingImpl)depBinding;
                        ip = ctorBinding.getInternalConstructor();
                        if (!ctorBinding.isInitialized()) {
                            failed = true;
                        }
                    }
                    if (!failed) {
                        continue;
                    }
                    this.removeFailedJitBinding(depKey, ip);
                    bindingFailed = true;
                }
                else {
                    if (this.state.getExplicitBinding(depKey) != null) {
                        continue;
                    }
                    bindingFailed = true;
                }
            }
        }
        return bindingFailed;
    }
    
    private void removeFailedJitBinding(final Key<?> key, final InjectionPoint ip) {
        this.jitBindings.remove(key);
        this.membersInjectorStore.remove(key.getTypeLiteral());
        if (ip != null) {
            this.constructors.remove(ip);
        }
    }
    
    private Set<Dependency<?>> getInternalDependencies(final BindingImpl<?> binding) {
        if (binding instanceof ConstructorBindingImpl) {
            return (Set<Dependency<?>>)((ConstructorBindingImpl)binding).getInternalDependencies();
        }
        if (binding instanceof HasDependencies) {
            return ((HasDependencies)binding).getDependencies();
        }
        return (Set<Dependency<?>>)$ImmutableSet.of();
    }
    
     <T> BindingImpl<T> createUninitializedBinding(final Key<T> key, final Scoping scoping, final Object source, final Errors errors, final boolean jitBinding) throws ErrorsException {
        final Class<?> rawType = key.getTypeLiteral().getRawType();
        if (rawType.isArray() || rawType.isEnum()) {
            throw errors.missingImplementation(key).toException();
        }
        if (rawType == TypeLiteral.class) {
            final BindingImpl<T> binding = (BindingImpl<T>)this.createTypeLiteralBinding((Key<TypeLiteral<Object>>)key, errors);
            return binding;
        }
        final ImplementedBy implementedBy = rawType.getAnnotation(ImplementedBy.class);
        if (implementedBy != null) {
            Annotations.checkForMisplacedScopeAnnotations(rawType, source, errors);
            return this.createImplementedByBinding(key, scoping, implementedBy, errors);
        }
        final ProvidedBy providedBy = rawType.getAnnotation(ProvidedBy.class);
        if (providedBy != null) {
            Annotations.checkForMisplacedScopeAnnotations(rawType, source, errors);
            return this.createProvidedByBinding(key, scoping, providedBy, errors);
        }
        return ConstructorBindingImpl.create(this, key, null, source, scoping, errors, jitBinding && this.options.jitDisabled);
    }
    
    private <T> BindingImpl<TypeLiteral<T>> createTypeLiteralBinding(final Key<TypeLiteral<T>> key, final Errors errors) throws ErrorsException {
        final Type typeLiteralType = key.getTypeLiteral().getType();
        if (!(typeLiteralType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawTypeLiteral().toException();
        }
        final ParameterizedType parameterizedType = (ParameterizedType)typeLiteralType;
        final Type innerType = parameterizedType.getActualTypeArguments()[0];
        if (!(innerType instanceof Class) && !(innerType instanceof GenericArrayType) && !(innerType instanceof ParameterizedType)) {
            throw errors.cannotInjectTypeLiteralOf(innerType).toException();
        }
        final TypeLiteral<T> value = (TypeLiteral<T>)TypeLiteral.get(innerType);
        final InternalFactory<TypeLiteral<T>> factory = new ConstantFactory<TypeLiteral<T>>(Initializables.of(value));
        return new InstanceBindingImpl<TypeLiteral<T>>(this, key, $SourceProvider.UNKNOWN_SOURCE, factory, (Set<InjectionPoint>)$ImmutableSet.of(), value);
    }
    
     <T> BindingImpl<T> createProvidedByBinding(final Key<T> key, final Scoping scoping, final ProvidedBy providedBy, final Errors errors) throws ErrorsException {
        final Class<?> rawType = key.getTypeLiteral().getRawType();
        final Class<? extends Provider<?>> providerType = providedBy.value();
        if (providerType == rawType) {
            throw errors.recursiveProviderType().toException();
        }
        final Key<? extends Provider<T>> providerKey = Key.get(providerType);
        final BindingImpl<? extends Provider<?>> providerBinding = this.getBindingOrThrow((Key<? extends Provider<?>>)providerKey, errors, JitLimitation.NEW_OR_EXISTING_JIT);
        final InternalFactory<T> internalFactory = new InternalFactory<T>() {
            public T get(Errors errors, final InternalContext context, final Dependency dependency, final boolean linked) throws ErrorsException {
                errors = errors.withSource(providerKey);
                final Provider<?> provider = (Provider<?>)providerBinding.getInternalFactory().get(errors, context, dependency, true);
                try {
                    final Object o = provider.get();
                    if (o != null && !rawType.isInstance(o)) {
                        throw errors.subtypeNotProvided(providerType, rawType).toException();
                    }
                    final T t = (T)o;
                    return t;
                }
                catch (RuntimeException e) {
                    throw errors.errorInProvider(e).toException();
                }
            }
        };
        final Object source = rawType;
        return new LinkedProviderBindingImpl<T>(this, key, source, Scoping.scope(key, this, (InternalFactory<? extends T>)internalFactory, source, scoping), scoping, (Key<? extends javax.inject.Provider<? extends T>>)providerKey);
    }
    
    private <T> BindingImpl<T> createImplementedByBinding(final Key<T> key, final Scoping scoping, final ImplementedBy implementedBy, final Errors errors) throws ErrorsException {
        final Class<?> rawType = key.getTypeLiteral().getRawType();
        final Class<?> implementationType = implementedBy.value();
        if (implementationType == rawType) {
            throw errors.recursiveImplementationType().toException();
        }
        if (!rawType.isAssignableFrom(implementationType)) {
            throw errors.notASubtype(implementationType, rawType).toException();
        }
        final Class<? extends T> subclass = (Class<? extends T>)implementationType;
        final Key<? extends T> targetKey = Key.get(subclass);
        final BindingImpl<? extends T> targetBinding = this.getBindingOrThrow(targetKey, errors, JitLimitation.NEW_OR_EXISTING_JIT);
        final InternalFactory<T> internalFactory = new InternalFactory<T>() {
            public T get(final Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) throws ErrorsException {
                return (T)targetBinding.getInternalFactory().get(errors.withSource(targetKey), context, dependency, true);
            }
        };
        final Object source = rawType;
        return new LinkedBindingImpl<T>(this, key, source, Scoping.scope(key, this, (InternalFactory<? extends T>)internalFactory, source, scoping), scoping, targetKey);
    }
    
    private <T> BindingImpl<T> createJustInTimeBindingRecursive(final Key<T> key, final Errors errors, final boolean jitDisabled, final JitLimitation jitType) throws ErrorsException {
        if (this.parent != null) {
            try {
                return (BindingImpl<T>)this.parent.createJustInTimeBindingRecursive((Key<Object>)key, new Errors(), jitDisabled, this.parent.options.jitDisabled ? JitLimitation.NO_JIT : jitType);
            }
            catch (ErrorsException ex) {}
        }
        if (this.state.isBlacklisted(key)) {
            final Set<Object> sources = this.state.getSourcesForBlacklistedKey(key);
            throw errors.childBindingAlreadySet(key, sources).toException();
        }
        final BindingImpl<T> binding = (BindingImpl<T>)this.createJustInTimeBinding((Key<Object>)key, errors, jitDisabled, jitType);
        this.state.parent().blacklist(key, binding.getSource());
        this.jitBindings.put(key, binding);
        return binding;
    }
    
    private <T> BindingImpl<T> createJustInTimeBinding(final Key<T> key, final Errors errors, final boolean jitDisabled, final JitLimitation jitType) throws ErrorsException {
        final int numErrorsBefore = errors.size();
        if (this.state.isBlacklisted(key)) {
            final Set<Object> sources = this.state.getSourcesForBlacklistedKey(key);
            throw errors.childBindingAlreadySet(key, sources).toException();
        }
        if (isProvider(key)) {
            final BindingImpl<T> binding = (BindingImpl<T>)this.createProviderBinding((Key<Provider<Object>>)key, errors);
            return binding;
        }
        if (isMembersInjector(key)) {
            final BindingImpl<T> binding = (BindingImpl<T>)this.createMembersInjectorBinding((Key<MembersInjector<Object>>)key, errors);
            return binding;
        }
        final BindingImpl<T> convertedBinding = this.convertConstantStringBinding(key, errors);
        if (convertedBinding != null) {
            return convertedBinding;
        }
        if (!isTypeLiteral(key) && jitDisabled && jitType != JitLimitation.NEW_OR_EXISTING_JIT) {
            throw errors.jitDisabled(key).toException();
        }
        if (key.getAnnotationType() != null) {
            if (key.hasAttributes()) {
                try {
                    final Errors ignored = new Errors();
                    return this.getBindingOrThrow(key.withoutAttributes(), ignored, JitLimitation.NO_JIT);
                }
                catch (ErrorsException ex) {}
            }
            throw errors.missingImplementation(key).toException();
        }
        final Object source = key.getTypeLiteral().getRawType();
        final BindingImpl<T> binding2 = this.createUninitializedBinding(key, Scoping.UNSCOPED, source, errors, true);
        errors.throwIfNewErrors(numErrorsBefore);
        this.initializeJitBinding(binding2, errors);
        return binding2;
    }
    
     <T> InternalFactory<? extends T> getInternalFactory(final Key<T> key, final Errors errors, final JitLimitation jitType) throws ErrorsException {
        return this.getBindingOrThrow(key, errors, jitType).getInternalFactory();
    }
    
    public Map<Key<?>, Binding<?>> getBindings() {
        return this.state.getExplicitBindingsThisLevel();
    }
    
    public Map<Key<?>, Binding<?>> getAllBindings() {
        synchronized (this.state.lock()) {
            return (Map<Key<?>, Binding<?>>)new $ImmutableMap.Builder().putAll((Map)this.state.getExplicitBindingsThisLevel()).putAll(this.jitBindings).build();
        }
    }
    
    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        return (Map<Class<? extends Annotation>, Scope>)$ImmutableMap.copyOf((Map<?, ?>)this.state.getScopes());
    }
    
    public Set<TypeConverterBinding> getTypeConverterBindings() {
        return (Set<TypeConverterBinding>)$ImmutableSet.copyOf((Iterable<?>)this.state.getConvertersThisLevel());
    }
    
    SingleParameterInjector<?>[] getParametersInjectors(final List<Dependency<?>> parameters, final Errors errors) throws ErrorsException {
        if (parameters.isEmpty()) {
            return null;
        }
        final int numErrorsBefore = errors.size();
        final SingleParameterInjector<?>[] result = (SingleParameterInjector<?>[])new SingleParameterInjector[parameters.size()];
        int i = 0;
        for (final Dependency<?> parameter : parameters) {
            try {
                result[i++] = this.createParameterInjector(parameter, errors.withSource(parameter));
            }
            catch (ErrorsException ex) {}
        }
        errors.throwIfNewErrors(numErrorsBefore);
        return result;
    }
    
     <T> SingleParameterInjector<T> createParameterInjector(final Dependency<T> dependency, final Errors errors) throws ErrorsException {
        final InternalFactory<? extends T> factory = this.getInternalFactory(dependency.getKey(), errors, JitLimitation.NO_JIT);
        return new SingleParameterInjector<T>(dependency, factory);
    }
    
    public void injectMembers(final Object instance) {
        final MembersInjector membersInjector = this.getMembersInjector(instance.getClass());
        membersInjector.injectMembers(instance);
    }
    
    public <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> typeLiteral) {
        final Errors errors = new Errors(typeLiteral);
        try {
            return this.membersInjectorStore.get(typeLiteral, errors);
        }
        catch (ErrorsException e) {
            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
        }
    }
    
    public <T> MembersInjector<T> getMembersInjector(final Class<T> type) {
        return this.getMembersInjector((TypeLiteral<T>)TypeLiteral.get((Class<T>)type));
    }
    
    public <T> Provider<T> getProvider(final Class<T> type) {
        return this.getProvider((Key<T>)Key.get((Class<T>)type));
    }
    
     <T> Provider<T> getProviderOrThrow(final Key<T> key, final Errors errors) throws ErrorsException {
        final InternalFactory<? extends T> factory = this.getInternalFactory(key, errors, JitLimitation.NO_JIT);
        final Dependency<T> dependency = Dependency.get(key);
        return new Provider<T>() {
            public T get() {
                final Errors errors = new Errors(dependency);
                try {
                    final T t = InjectorImpl.this.callInContext((ContextualCallable<T>)new ContextualCallable<T>() {
                        public T call(final InternalContext context) throws ErrorsException {
                            final Dependency previous = context.setDependency(dependency);
                            try {
                                return factory.get(errors, context, dependency, false);
                            }
                            finally {
                                context.setDependency(previous);
                            }
                        }
                    });
                    errors.throwIfNewErrors(0);
                    return t;
                }
                catch (ErrorsException e) {
                    throw new ProvisionException(errors.merge(e.getErrors()).getMessages());
                }
            }
            
            @Override
            public String toString() {
                return factory.toString();
            }
        };
    }
    
    public <T> Provider<T> getProvider(final Key<T> key) {
        final Errors errors = new Errors(key);
        try {
            final Provider<T> result = this.getProviderOrThrow(key, errors);
            errors.throwIfNewErrors(0);
            return result;
        }
        catch (ErrorsException e) {
            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
        }
    }
    
    public <T> T getInstance(final Key<T> key) {
        return this.getProvider(key).get();
    }
    
    public <T> T getInstance(final Class<T> type) {
        return this.getProvider(type).get();
    }
    
     <T> T callInContext(final ContextualCallable<T> callable) throws ErrorsException {
        final Object[] reference = this.localContext.get();
        if (reference[0] == null) {
            reference[0] = new InternalContext();
            try {
                return callable.call((InternalContext)reference[0]);
            }
            finally {
                reference[0] = null;
            }
        }
        return callable.call((InternalContext)reference[0]);
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(Injector.class).add("bindings", this.state.getExplicitBindingsThisLevel().values()).toString();
    }
    
    static {
        STRING_TYPE = TypeLiteral.get(String.class);
    }
    
    static class InjectorOptions
    {
        final Stage stage;
        final boolean jitDisabled;
        final boolean disableCircularProxies;
        
        InjectorOptions(final Stage stage, final boolean jitDisabled, final boolean disableCircularProxies) {
            this.stage = stage;
            this.jitDisabled = jitDisabled;
            this.disableCircularProxies = disableCircularProxies;
        }
        
        @Override
        public String toString() {
            return new $ToStringBuilder(this.getClass()).add("stage", this.stage).add("jitDisabled", this.jitDisabled).add("disableCircularProxies", this.disableCircularProxies).toString();
        }
    }
    
    enum JitLimitation
    {
        NO_JIT, 
        EXISTING_JIT, 
        NEW_OR_EXISTING_JIT;
    }
    
    private static class ProviderBindingImpl<T> extends BindingImpl<Provider<T>> implements ProviderBinding<Provider<T>>, HasDependencies
    {
        final BindingImpl<T> providedBinding;
        
        ProviderBindingImpl(final InjectorImpl injector, final Key<Provider<T>> key, final Binding<T> providedBinding) {
            super(injector, key, providedBinding.getSource(), createInternalFactory(providedBinding), Scoping.UNSCOPED);
            this.providedBinding = (BindingImpl<T>)(BindingImpl)providedBinding;
        }
        
        static <T> InternalFactory<Provider<T>> createInternalFactory(final Binding<T> providedBinding) {
            final Provider<T> provider = providedBinding.getProvider();
            return new InternalFactory<Provider<T>>() {
                public Provider<T> get(final Errors errors, final InternalContext context, final Dependency dependency, final boolean linked) {
                    return provider;
                }
            };
        }
        
        public Key<? extends T> getProvidedKey() {
            return (Key<? extends T>)this.providedBinding.getKey();
        }
        
        public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super Provider<T>, V> visitor) {
            return visitor.visit(this);
        }
        
        public void applyTo(final Binder binder) {
            throw new UnsupportedOperationException("This element represents a synthetic binding.");
        }
        
        @Override
        public String toString() {
            return new $ToStringBuilder(ProviderBinding.class).add("key", this.getKey()).add("providedKey", this.getProvidedKey()).toString();
        }
        
        public Set<Dependency<?>> getDependencies() {
            return (Set<Dependency<?>>)$ImmutableSet.of(Dependency.get(this.getProvidedKey()));
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof ProviderBindingImpl) {
                final ProviderBindingImpl<?> o = (ProviderBindingImpl<?>)obj;
                return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.providedBinding, o.providedBinding);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return $Objects.hashCode(this.getKey(), this.getScoping(), this.providedBinding);
        }
    }
    
    private static class ConvertedConstantBindingImpl<T> extends BindingImpl<T> implements ConvertedConstantBinding<T>
    {
        final T value;
        final Provider<T> provider;
        final Binding<String> originalBinding;
        final TypeConverterBinding typeConverterBinding;
        
        ConvertedConstantBindingImpl(final InjectorImpl injector, final Key<T> key, final T value, final Binding<String> originalBinding, final TypeConverterBinding typeConverterBinding) {
            super(injector, key, originalBinding.getSource(), (InternalFactory<? extends T>)new ConstantFactory<T>((Initializable<? extends T>)Initializables.of(value)), Scoping.UNSCOPED);
            this.value = value;
            this.provider = Providers.of(value);
            this.originalBinding = originalBinding;
            this.typeConverterBinding = typeConverterBinding;
        }
        
        @Override
        public Provider<T> getProvider() {
            return this.provider;
        }
        
        public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> visitor) {
            return visitor.visit((ConvertedConstantBinding<? extends T>)this);
        }
        
        public T getValue() {
            return this.value;
        }
        
        public TypeConverterBinding getTypeConverterBinding() {
            return this.typeConverterBinding;
        }
        
        public Key<String> getSourceKey() {
            return this.originalBinding.getKey();
        }
        
        public Set<Dependency<?>> getDependencies() {
            return (Set<Dependency<?>>)$ImmutableSet.of(Dependency.get(this.getSourceKey()));
        }
        
        public void applyTo(final Binder binder) {
            throw new UnsupportedOperationException("This element represents a synthetic binding.");
        }
        
        @Override
        public String toString() {
            return new $ToStringBuilder(ConvertedConstantBinding.class).add("key", this.getKey()).add("sourceKey", this.getSourceKey()).add("value", this.value).toString();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof ConvertedConstantBindingImpl) {
                final ConvertedConstantBindingImpl<?> o = (ConvertedConstantBindingImpl<?>)obj;
                return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.value, o.value);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return $Objects.hashCode(this.getKey(), this.getScoping(), this.value);
        }
    }
    
    private static class BindingsMultimap
    {
        final Map<TypeLiteral<?>, List<Binding<?>>> multimap;
        
        private BindingsMultimap() {
            this.multimap = (Map<TypeLiteral<?>, List<Binding<?>>>)$Maps.newHashMap();
        }
        
         <T> void put(final TypeLiteral<T> type, final Binding<T> binding) {
            List<Binding<?>> bindingsForType = this.multimap.get(type);
            if (bindingsForType == null) {
                bindingsForType = (List<Binding<?>>)$Lists.newArrayList();
                this.multimap.put(type, bindingsForType);
            }
            bindingsForType.add(binding);
        }
        
         <T> List<Binding<T>> getAll(final TypeLiteral<T> type) {
            final List<Binding<?>> bindings = this.multimap.get(type);
            return (List<Binding<T>>)((bindings != null) ? Collections.unmodifiableList((List<?>)this.multimap.get(type)) : $ImmutableList.of());
        }
    }
    
    interface MethodInvoker
    {
        Object invoke(final Object p0, final Object... p1) throws IllegalAccessException, InvocationTargetException;
    }
}
