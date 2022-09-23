// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.ExposureBuilder;
import com.google.inject.binder.AnnotatedElementBuilder;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.Key;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.ProviderMethodsModule;
import java.util.Collection;
import com.google.inject.internal.Errors;
import com.google.inject.PrivateModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.Scope;
import java.lang.annotation.Annotation;
import org.aopalliance.intercept.MethodInterceptor;
import java.lang.reflect.Method;
import com.google.inject.matcher.Matcher;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.BindingBuilder;
import com.google.inject.internal.AbstractBindingBuilder;
import com.google.inject.internal.ConstantBindingBuilderImpl;
import com.google.inject.AbstractModule;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Sets;
import com.google.inject.internal.PrivateElementsImpl;
import com.google.inject.internal.util.$SourceProvider;
import java.util.Set;
import com.google.inject.PrivateBinder;
import com.google.inject.Binding;
import com.google.inject.Binder;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;
import com.google.inject.Stage;
import java.util.List;
import com.google.inject.Module;

public final class Elements
{
    private static final BindingTargetVisitor<Object, Object> GET_INSTANCE_VISITOR;
    
    public static List<Element> getElements(final Module... modules) {
        return getElements(Stage.DEVELOPMENT, Arrays.asList(modules));
    }
    
    public static List<Element> getElements(final Stage stage, final Module... modules) {
        return getElements(stage, Arrays.asList(modules));
    }
    
    public static List<Element> getElements(final Iterable<? extends Module> modules) {
        return getElements(Stage.DEVELOPMENT, modules);
    }
    
    public static List<Element> getElements(final Stage stage, final Iterable<? extends Module> modules) {
        final RecordingBinder binder = new RecordingBinder(stage);
        for (final Module module : modules) {
            binder.install(module);
        }
        return Collections.unmodifiableList((List<? extends Element>)binder.elements);
    }
    
    public static Module getModule(final Iterable<? extends Element> elements) {
        return new Module() {
            public void configure(final Binder binder) {
                for (final Element element : elements) {
                    element.applyTo(binder);
                }
            }
        };
    }
    
    static <T> BindingTargetVisitor<T, T> getInstanceVisitor() {
        return (BindingTargetVisitor<T, T>)Elements.GET_INSTANCE_VISITOR;
    }
    
    static {
        GET_INSTANCE_VISITOR = new DefaultBindingTargetVisitor<Object, Object>() {
            @Override
            public Object visit(final InstanceBinding<?> binding) {
                return binding.getInstance();
            }
            
            @Override
            protected Object visitOther(final Binding<?> binding) {
                throw new IllegalArgumentException();
            }
        };
    }
    
    private static class RecordingBinder implements Binder, PrivateBinder
    {
        private final Stage stage;
        private final Set<Module> modules;
        private final List<Element> elements;
        private final Object source;
        private final $SourceProvider sourceProvider;
        private final RecordingBinder parent;
        private final PrivateElementsImpl privateElements;
        
        private RecordingBinder(final Stage stage) {
            this.stage = stage;
            this.modules = (Set<Module>)$Sets.newHashSet();
            this.elements = (List<Element>)$Lists.newArrayList();
            this.source = null;
            this.sourceProvider = $SourceProvider.DEFAULT_INSTANCE.plusSkippedClasses(Elements.class, RecordingBinder.class, AbstractModule.class, ConstantBindingBuilderImpl.class, AbstractBindingBuilder.class, BindingBuilder.class);
            this.parent = null;
            this.privateElements = null;
        }
        
        private RecordingBinder(final RecordingBinder prototype, final Object source, final $SourceProvider sourceProvider) {
            $Preconditions.checkArgument(source == null ^ sourceProvider == null);
            this.stage = prototype.stage;
            this.modules = prototype.modules;
            this.elements = prototype.elements;
            this.source = source;
            this.sourceProvider = sourceProvider;
            this.parent = prototype.parent;
            this.privateElements = prototype.privateElements;
        }
        
        private RecordingBinder(final RecordingBinder parent, final PrivateElementsImpl privateElements) {
            this.stage = parent.stage;
            this.modules = (Set<Module>)$Sets.newHashSet();
            this.elements = privateElements.getElementsMutable();
            this.source = parent.source;
            this.sourceProvider = parent.sourceProvider;
            this.parent = parent;
            this.privateElements = privateElements;
        }
        
        public void bindInterceptor(final Matcher<? super Class<?>> classMatcher, final Matcher<? super Method> methodMatcher, final MethodInterceptor... interceptors) {
            this.elements.add(new InterceptorBinding(this.getSource(), classMatcher, methodMatcher, interceptors));
        }
        
        public void bindScope(final Class<? extends Annotation> annotationType, final Scope scope) {
            this.elements.add(new ScopeBinding(this.getSource(), annotationType, scope));
        }
        
        public void requestInjection(final Object instance) {
            this.requestInjection((TypeLiteral<Object>)TypeLiteral.get(instance.getClass()), instance);
        }
        
        public <T> void requestInjection(final TypeLiteral<T> type, final T instance) {
            this.elements.add(new InjectionRequest<Object>(this.getSource(), (TypeLiteral<Object>)type, instance));
        }
        
        public <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> typeLiteral) {
            final MembersInjectorLookup<T> element = new MembersInjectorLookup<T>(this.getSource(), typeLiteral);
            this.elements.add(element);
            return element.getMembersInjector();
        }
        
        public <T> MembersInjector<T> getMembersInjector(final Class<T> type) {
            return this.getMembersInjector((TypeLiteral<T>)TypeLiteral.get((Class<T>)type));
        }
        
        public void bindListener(final Matcher<? super TypeLiteral<?>> typeMatcher, final TypeListener listener) {
            this.elements.add(new TypeListenerBinding(this.getSource(), listener, typeMatcher));
        }
        
        public void requestStaticInjection(final Class<?>... types) {
            for (final Class<?> type : types) {
                this.elements.add(new StaticInjectionRequest(this.getSource(), type));
            }
        }
        
        public void install(final Module module) {
            if (this.modules.add(module)) {
                Binder binder = this;
                if (module instanceof PrivateModule) {
                    binder = binder.newPrivateBinder();
                }
                try {
                    module.configure(binder);
                }
                catch (RuntimeException e) {
                    final Collection<Message> messages = Errors.getMessagesFromThrowable(e);
                    if (!messages.isEmpty()) {
                        this.elements.addAll(messages);
                    }
                    else {
                        this.addError(e);
                    }
                }
                binder.install(ProviderMethodsModule.forModule(module));
            }
        }
        
        public Stage currentStage() {
            return this.stage;
        }
        
        public void addError(final String message, final Object... arguments) {
            this.elements.add(new Message(this.getSource(), Errors.format(message, arguments)));
        }
        
        public void addError(final Throwable t) {
            final String message = "An exception was caught and reported. Message: " + t.getMessage();
            this.elements.add(new Message($ImmutableList.of(this.getSource()), message, t));
        }
        
        public void addError(final Message message) {
            this.elements.add(message);
        }
        
        public <T> AnnotatedBindingBuilder<T> bind(final Key<T> key) {
            return new BindingBuilder<T>(this, this.elements, this.getSource(), key);
        }
        
        public <T> AnnotatedBindingBuilder<T> bind(final TypeLiteral<T> typeLiteral) {
            return this.bind((Key<T>)Key.get((TypeLiteral<T>)typeLiteral));
        }
        
        public <T> AnnotatedBindingBuilder<T> bind(final Class<T> type) {
            return this.bind((Key<T>)Key.get((Class<T>)type));
        }
        
        public AnnotatedConstantBindingBuilder bindConstant() {
            return new ConstantBindingBuilderImpl<Object>(this, this.elements, this.getSource());
        }
        
        public <T> Provider<T> getProvider(final Key<T> key) {
            final ProviderLookup<T> element = new ProviderLookup<T>(this.getSource(), key);
            this.elements.add(element);
            return element.getProvider();
        }
        
        public <T> Provider<T> getProvider(final Class<T> type) {
            return this.getProvider((Key<T>)Key.get((Class<T>)type));
        }
        
        public void convertToTypes(final Matcher<? super TypeLiteral<?>> typeMatcher, final TypeConverter converter) {
            this.elements.add(new TypeConverterBinding(this.getSource(), typeMatcher, converter));
        }
        
        public RecordingBinder withSource(final Object source) {
            return new RecordingBinder(this, source, null);
        }
        
        public RecordingBinder skipSources(final Class... classesToSkip) {
            if (this.source != null) {
                return this;
            }
            final $SourceProvider newSourceProvider = this.sourceProvider.plusSkippedClasses(classesToSkip);
            return new RecordingBinder(this, null, newSourceProvider);
        }
        
        public PrivateBinder newPrivateBinder() {
            final PrivateElementsImpl privateElements = new PrivateElementsImpl(this.getSource());
            this.elements.add(privateElements);
            return new RecordingBinder(this, privateElements);
        }
        
        public void disableCircularProxies() {
            this.elements.add(new DisableCircularProxiesOption(this.getSource()));
        }
        
        public void requireExplicitBindings() {
            this.elements.add(new RequireExplicitBindingsOption(this.getSource()));
        }
        
        public void expose(final Key<?> key) {
            this.exposeInternal(key);
        }
        
        public AnnotatedElementBuilder expose(final Class<?> type) {
            return this.exposeInternal((Key<Object>)Key.get(type));
        }
        
        public AnnotatedElementBuilder expose(final TypeLiteral<?> type) {
            return this.exposeInternal((Key<Object>)Key.get(type));
        }
        
        private <T> AnnotatedElementBuilder exposeInternal(final Key<T> key) {
            if (this.privateElements == null) {
                this.addError("Cannot expose %s on a standard binder. Exposed bindings are only applicable to private binders.", key);
                return new AnnotatedElementBuilder() {
                    public void annotatedWith(final Class<? extends Annotation> annotationType) {
                    }
                    
                    public void annotatedWith(final Annotation annotation) {
                    }
                };
            }
            final ExposureBuilder<T> builder = new ExposureBuilder<T>(this, this.getSource(), key);
            this.privateElements.addExposureBuilder(builder);
            return builder;
        }
        
        protected Object getSource() {
            return (this.sourceProvider != null) ? this.sourceProvider.get() : this.source;
        }
        
        @Override
        public String toString() {
            return "Binder";
        }
    }
}
