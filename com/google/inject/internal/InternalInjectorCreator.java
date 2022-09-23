// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.spi.TypeConverterBinding;
import java.util.Set;
import com.google.inject.Scope;
import java.lang.annotation.Annotation;
import com.google.inject.TypeLiteral;
import com.google.inject.Binding;
import java.util.Map;
import com.google.inject.Key;
import com.google.inject.spi.Dependency;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Iterables;
import java.util.Iterator;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import java.util.List;
import com.google.inject.internal.util.$Stopwatch;

public final class InternalInjectorCreator
{
    private final $Stopwatch stopwatch;
    private final Errors errors;
    private final Initializer initializer;
    private final ProcessedBindingData bindingData;
    private final InjectionRequestProcessor injectionRequestProcessor;
    private final InjectorShell.Builder shellBuilder;
    private List<InjectorShell> shells;
    
    public InternalInjectorCreator() {
        this.stopwatch = new $Stopwatch();
        this.errors = new Errors();
        this.initializer = new Initializer();
        this.shellBuilder = new InjectorShell.Builder();
        this.injectionRequestProcessor = new InjectionRequestProcessor(this.errors, this.initializer);
        this.bindingData = new ProcessedBindingData();
    }
    
    public InternalInjectorCreator stage(final Stage stage) {
        this.shellBuilder.stage(stage);
        return this;
    }
    
    public InternalInjectorCreator parentInjector(final InjectorImpl parent) {
        this.shellBuilder.parent(parent);
        return this;
    }
    
    public InternalInjectorCreator addModules(final Iterable<? extends Module> modules) {
        this.shellBuilder.addModules(modules);
        return this;
    }
    
    public Injector build() {
        if (this.shellBuilder == null) {
            throw new AssertionError((Object)"Already built, builders are not reusable.");
        }
        synchronized (this.shellBuilder.lock()) {
            this.shells = this.shellBuilder.build(this.initializer, this.bindingData, this.stopwatch, this.errors);
            this.stopwatch.resetAndLog("Injector construction");
            this.initializeStatically();
        }
        this.injectDynamically();
        if (this.shellBuilder.getStage() == Stage.TOOL) {
            return new ToolStageInjector(this.primaryInjector());
        }
        return this.primaryInjector();
    }
    
    private void initializeStatically() {
        this.bindingData.initializeBindings();
        this.stopwatch.resetAndLog("Binding initialization");
        for (final InjectorShell shell : this.shells) {
            shell.getInjector().index();
        }
        this.stopwatch.resetAndLog("Binding indexing");
        this.injectionRequestProcessor.process(this.shells);
        this.stopwatch.resetAndLog("Collecting injection requests");
        this.bindingData.runCreationListeners(this.errors);
        this.stopwatch.resetAndLog("Binding validation");
        this.injectionRequestProcessor.validate();
        this.stopwatch.resetAndLog("Static validation");
        this.initializer.validateOustandingInjections(this.errors);
        this.stopwatch.resetAndLog("Instance member validation");
        new LookupProcessor(this.errors).process(this.shells);
        for (final InjectorShell shell : this.shells) {
            ((DeferredLookups)shell.getInjector().lookups).initialize(this.errors);
        }
        this.stopwatch.resetAndLog("Provider verification");
        for (final InjectorShell shell : this.shells) {
            if (!shell.getElements().isEmpty()) {
                throw new AssertionError((Object)("Failed to execute " + shell.getElements()));
            }
        }
        this.errors.throwCreationExceptionIfErrorsExist();
    }
    
    private Injector primaryInjector() {
        return this.shells.get(0).getInjector();
    }
    
    private void injectDynamically() {
        this.injectionRequestProcessor.injectMembers();
        this.stopwatch.resetAndLog("Static member injection");
        this.initializer.injectAll(this.errors);
        this.stopwatch.resetAndLog("Instance injection");
        this.errors.throwCreationExceptionIfErrorsExist();
        if (this.shellBuilder.getStage() != Stage.TOOL) {
            for (final InjectorShell shell : this.shells) {
                this.loadEagerSingletons(shell.getInjector(), this.shellBuilder.getStage(), this.errors);
            }
            this.stopwatch.resetAndLog("Preloading singletons");
        }
        this.errors.throwCreationExceptionIfErrorsExist();
    }
    
    void loadEagerSingletons(final InjectorImpl injector, final Stage stage, final Errors errors) {
        final Iterable<BindingImpl<?>> candidateBindings = (Iterable<BindingImpl<?>>)$ImmutableList.copyOf($Iterables.concat((Iterable<?>)injector.state.getExplicitBindingsThisLevel().values(), (Iterable<?>)injector.jitBindings.values()));
        for (final BindingImpl<?> binding : candidateBindings) {
            if (this.isEagerSingleton(injector, binding, stage)) {
                try {
                    injector.callInContext((ContextualCallable<Object>)new ContextualCallable<Void>() {
                        Dependency<?> dependency = Dependency.get(binding.getKey());
                        
                        public Void call(final InternalContext context) {
                            final Dependency previous = context.setDependency(this.dependency);
                            final Errors errorsForBinding = errors.withSource(this.dependency);
                            try {
                                binding.getInternalFactory().get(errorsForBinding, context, this.dependency, false);
                            }
                            catch (ErrorsException e) {
                                errorsForBinding.merge(e.getErrors());
                            }
                            finally {
                                context.setDependency(previous);
                            }
                            return null;
                        }
                    });
                }
                catch (ErrorsException e) {
                    throw new AssertionError();
                }
            }
        }
    }
    
    private boolean isEagerSingleton(final InjectorImpl injector, final BindingImpl<?> binding, final Stage stage) {
        if (binding.getScoping().isEagerSingleton(stage)) {
            return true;
        }
        if (binding instanceof LinkedBindingImpl) {
            final Key<?> linkedBinding = (Key<?>)((LinkedBindingImpl)binding).getLinkedKey();
            return this.isEagerSingleton(injector, injector.getBinding(linkedBinding), stage);
        }
        return false;
    }
    
    static class ToolStageInjector implements Injector
    {
        private final Injector delegateInjector;
        
        ToolStageInjector(final Injector delegateInjector) {
            this.delegateInjector = delegateInjector;
        }
        
        public void injectMembers(final Object o) {
            throw new UnsupportedOperationException("Injector.injectMembers(Object) is not supported in Stage.TOOL");
        }
        
        public Map<Key<?>, Binding<?>> getBindings() {
            return this.delegateInjector.getBindings();
        }
        
        public Map<Key<?>, Binding<?>> getAllBindings() {
            return this.delegateInjector.getAllBindings();
        }
        
        public <T> Binding<T> getBinding(final Key<T> key) {
            return this.delegateInjector.getBinding(key);
        }
        
        public <T> Binding<T> getBinding(final Class<T> type) {
            return this.delegateInjector.getBinding(type);
        }
        
        public <T> Binding<T> getExistingBinding(final Key<T> key) {
            return this.delegateInjector.getExistingBinding(key);
        }
        
        public <T> List<Binding<T>> findBindingsByType(final TypeLiteral<T> type) {
            return this.delegateInjector.findBindingsByType(type);
        }
        
        public Injector getParent() {
            return this.delegateInjector.getParent();
        }
        
        public Injector createChildInjector(final Iterable<? extends Module> modules) {
            return this.delegateInjector.createChildInjector(modules);
        }
        
        public Injector createChildInjector(final Module... modules) {
            return this.delegateInjector.createChildInjector(modules);
        }
        
        public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
            return this.delegateInjector.getScopeBindings();
        }
        
        public Set<TypeConverterBinding> getTypeConverterBindings() {
            return this.delegateInjector.getTypeConverterBindings();
        }
        
        public <T> Provider<T> getProvider(final Key<T> key) {
            throw new UnsupportedOperationException("Injector.getProvider(Key<T>) is not supported in Stage.TOOL");
        }
        
        public <T> Provider<T> getProvider(final Class<T> type) {
            throw new UnsupportedOperationException("Injector.getProvider(Class<T>) is not supported in Stage.TOOL");
        }
        
        public <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> typeLiteral) {
            throw new UnsupportedOperationException("Injector.getMembersInjector(TypeLiteral<T>) is not supported in Stage.TOOL");
        }
        
        public <T> MembersInjector<T> getMembersInjector(final Class<T> type) {
            throw new UnsupportedOperationException("Injector.getMembersInjector(Class<T>) is not supported in Stage.TOOL");
        }
        
        public <T> T getInstance(final Key<T> key) {
            throw new UnsupportedOperationException("Injector.getInstance(Key<T>) is not supported in Stage.TOOL");
        }
        
        public <T> T getInstance(final Class<T> type) {
            throw new UnsupportedOperationException("Injector.getInstance(Class<T>) is not supported in Stage.TOOL");
        }
    }
}
