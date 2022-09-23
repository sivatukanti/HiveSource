// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.annotation.Annotation;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.Binder;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.TypeListenerBinding;
import com.google.inject.spi.Elements;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$Stopwatch;
import java.util.Iterator;
import java.util.Collection;
import com.google.inject.spi.PrivateElements;
import com.google.inject.internal.util.$Lists;
import com.google.inject.Stage;
import com.google.inject.Module;
import java.util.logging.Logger;
import com.google.inject.spi.InjectionPoint;
import java.util.Set;
import com.google.inject.Provider;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$SourceProvider;
import com.google.inject.Key;
import com.google.inject.Injector;
import com.google.inject.spi.Element;
import java.util.List;

final class InjectorShell
{
    private final List<Element> elements;
    private final InjectorImpl injector;
    
    private InjectorShell(final Builder builder, final List<Element> elements, final InjectorImpl injector) {
        this.elements = elements;
        this.injector = injector;
    }
    
    InjectorImpl getInjector() {
        return this.injector;
    }
    
    List<Element> getElements() {
        return this.elements;
    }
    
    private static void bindInjector(final InjectorImpl injector) {
        final Key<Injector> key = Key.get(Injector.class);
        final InjectorFactory injectorFactory = new InjectorFactory((Injector)injector);
        injector.state.putBinding(key, new ProviderInstanceBindingImpl<Object>(injector, key, $SourceProvider.UNKNOWN_SOURCE, injectorFactory, Scoping.UNSCOPED, injectorFactory, (Set<InjectionPoint>)$ImmutableSet.of()));
    }
    
    private static void bindLogger(final InjectorImpl injector) {
        final Key<Logger> key = Key.get(Logger.class);
        final LoggerFactory loggerFactory = new LoggerFactory();
        injector.state.putBinding(key, new ProviderInstanceBindingImpl<Object>(injector, key, $SourceProvider.UNKNOWN_SOURCE, loggerFactory, Scoping.UNSCOPED, loggerFactory, (Set<InjectionPoint>)$ImmutableSet.of()));
    }
    
    static class Builder
    {
        private final List<Element> elements;
        private final List<Module> modules;
        private State state;
        private InjectorImpl parent;
        private InjectorImpl.InjectorOptions options;
        private Stage stage;
        private PrivateElementsImpl privateElements;
        
        Builder() {
            this.elements = (List<Element>)$Lists.newArrayList();
            this.modules = (List<Module>)$Lists.newArrayList();
        }
        
        Builder stage(final Stage stage) {
            this.stage = stage;
            return this;
        }
        
        Builder parent(final InjectorImpl parent) {
            this.parent = parent;
            this.state = new InheritingState(parent.state);
            this.options = parent.options;
            this.stage = this.options.stage;
            return this;
        }
        
        Builder privateElements(final PrivateElements privateElements) {
            this.privateElements = (PrivateElementsImpl)privateElements;
            this.elements.addAll(privateElements.getElements());
            return this;
        }
        
        void addModules(final Iterable<? extends Module> modules) {
            for (final Module module : modules) {
                this.modules.add(module);
            }
        }
        
        Stage getStage() {
            return this.options.stage;
        }
        
        Object lock() {
            return this.getState().lock();
        }
        
        List<InjectorShell> build(final Initializer initializer, final ProcessedBindingData bindingData, final $Stopwatch stopwatch, final Errors errors) {
            $Preconditions.checkState(this.stage != null, (Object)"Stage not initialized");
            $Preconditions.checkState(this.privateElements == null || this.parent != null, (Object)"PrivateElements with no parent");
            $Preconditions.checkState(this.state != null, (Object)"no state. Did you remember to lock() ?");
            if (this.parent == null) {
                this.modules.add(0, new RootModule(this.stage));
            }
            this.elements.addAll(Elements.getElements(this.stage, this.modules));
            final InjectorOptionsProcessor optionsProcessor = new InjectorOptionsProcessor(errors);
            optionsProcessor.process(null, this.elements);
            this.options = optionsProcessor.getOptions(this.stage, this.options);
            final InjectorImpl injector = new InjectorImpl(this.parent, this.state, this.options);
            if (this.privateElements != null) {
                this.privateElements.initInjector(injector);
            }
            if (this.parent == null) {
                new TypeConverterBindingProcessor(errors).prepareBuiltInConverters(injector);
            }
            stopwatch.resetAndLog("Module execution");
            new MessageProcessor(errors).process(injector, this.elements);
            final InterceptorBindingProcessor interceptors = new InterceptorBindingProcessor(errors);
            interceptors.process(injector, this.elements);
            stopwatch.resetAndLog("Interceptors creation");
            new TypeListenerBindingProcessor(errors).process(injector, this.elements);
            final List<TypeListenerBinding> listenerBindings = injector.state.getTypeListenerBindings();
            injector.membersInjectorStore = new MembersInjectorStore(injector, listenerBindings);
            stopwatch.resetAndLog("TypeListeners creation");
            new ScopeBindingProcessor(errors).process(injector, this.elements);
            stopwatch.resetAndLog("Scopes creation");
            new TypeConverterBindingProcessor(errors).process(injector, this.elements);
            stopwatch.resetAndLog("Converters creation");
            bindInjector(injector);
            bindLogger(injector);
            new BindingProcessor(errors, initializer, bindingData).process(injector, this.elements);
            new UntargettedBindingProcessor(errors, bindingData).process(injector, this.elements);
            stopwatch.resetAndLog("Binding creation");
            final List<InjectorShell> injectorShells = (List<InjectorShell>)$Lists.newArrayList();
            injectorShells.add(new InjectorShell(this, this.elements, injector, null));
            final PrivateElementProcessor processor = new PrivateElementProcessor(errors);
            processor.process(injector, this.elements);
            for (final Builder builder : processor.getInjectorShellBuilders()) {
                injectorShells.addAll(builder.build(initializer, bindingData, stopwatch, errors));
            }
            stopwatch.resetAndLog("Private environment creation");
            return injectorShells;
        }
        
        private State getState() {
            if (this.state == null) {
                this.state = new InheritingState(State.NONE);
            }
            return this.state;
        }
    }
    
    private static class InjectorFactory implements InternalFactory<Injector>, Provider<Injector>
    {
        private final Injector injector;
        
        private InjectorFactory(final Injector injector) {
            this.injector = injector;
        }
        
        public Injector get(final Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) throws ErrorsException {
            return this.injector;
        }
        
        public Injector get() {
            return this.injector;
        }
        
        @Override
        public String toString() {
            return "Provider<Injector>";
        }
    }
    
    private static class LoggerFactory implements InternalFactory<Logger>, Provider<Logger>
    {
        public Logger get(final Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) {
            final InjectionPoint injectionPoint = dependency.getInjectionPoint();
            return (injectionPoint == null) ? Logger.getAnonymousLogger() : Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
        }
        
        public Logger get() {
            return Logger.getAnonymousLogger();
        }
        
        @Override
        public String toString() {
            return "Provider<Logger>";
        }
    }
    
    private static class RootModule implements Module
    {
        final Stage stage;
        
        private RootModule(final Stage stage) {
            this.stage = $Preconditions.checkNotNull(stage, (Object)"stage");
        }
        
        public void configure(Binder binder) {
            binder = binder.withSource($SourceProvider.UNKNOWN_SOURCE);
            binder.bind(Stage.class).toInstance(this.stage);
            binder.bindScope(Singleton.class, Scopes.SINGLETON);
            binder.bindScope(javax.inject.Singleton.class, Scopes.SINGLETON);
        }
    }
}
