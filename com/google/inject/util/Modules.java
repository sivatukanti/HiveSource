// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.util;

import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import java.lang.annotation.Annotation;
import com.google.inject.PrivateBinder;
import com.google.inject.Scope;
import java.util.List;
import java.util.Map;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Maps;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.Binding;
import com.google.inject.internal.util.$Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import com.google.inject.Key;
import com.google.inject.spi.PrivateElements;
import com.google.inject.internal.util.$Iterables;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.AbstractModule;
import java.util.Iterator;
import com.google.inject.Binder;
import java.util.Set;
import com.google.inject.internal.util.$ImmutableSet;
import java.util.Arrays;
import com.google.inject.Module;

public final class Modules
{
    public static final Module EMPTY_MODULE;
    
    private Modules() {
    }
    
    public static OverriddenModuleBuilder override(final Module... modules) {
        return new RealOverriddenModuleBuilder((Iterable)Arrays.asList(modules));
    }
    
    public static OverriddenModuleBuilder override(final Iterable<? extends Module> modules) {
        return new RealOverriddenModuleBuilder((Iterable)modules);
    }
    
    public static Module combine(final Module... modules) {
        return combine($ImmutableSet.of(modules));
    }
    
    public static Module combine(final Iterable<? extends Module> modules) {
        final Set<Module> modulesSet = (Set<Module>)$ImmutableSet.copyOf((Iterable<?>)modules);
        return new Module() {
            public void configure(Binder binder) {
                binder = binder.skipSources(this.getClass());
                for (final Module module : modulesSet) {
                    binder.install(module);
                }
            }
        };
    }
    
    static {
        EMPTY_MODULE = new Module() {
            public void configure(final Binder binder) {
            }
        };
    }
    
    private static final class RealOverriddenModuleBuilder implements OverriddenModuleBuilder
    {
        private final $ImmutableSet<Module> baseModules;
        
        private RealOverriddenModuleBuilder(final Iterable<? extends Module> baseModules) {
            this.baseModules = $ImmutableSet.copyOf(baseModules);
        }
        
        public Module with(final Module... overrides) {
            return this.with(Arrays.asList(overrides));
        }
        
        public Module with(final Iterable<? extends Module> overrides) {
            return new AbstractModule() {
                public void configure() {
                    Binder baseBinder = this.binder();
                    List<Element> baseElements = Elements.getElements(RealOverriddenModuleBuilder.this.baseModules);
                    if (baseElements.size() == 1) {
                        final Element element = $Iterables.getOnlyElement(baseElements);
                        if (element instanceof PrivateElements) {
                            final PrivateElements privateElements = (PrivateElements)element;
                            final PrivateBinder privateBinder = baseBinder.newPrivateBinder().withSource(privateElements.getSource());
                            for (final Key exposed : privateElements.getExposedKeys()) {
                                privateBinder.withSource(privateElements.getExposedSource(exposed)).expose(exposed);
                            }
                            baseBinder = privateBinder;
                            baseElements = privateElements.getElements();
                        }
                    }
                    final Binder binder = baseBinder;
                    final LinkedHashSet<Element> elements = new LinkedHashSet<Element>(baseElements);
                    final List<Element> overrideElements = Elements.getElements(overrides);
                    final Set<Key<?>> overriddenKeys = (Set<Key<?>>)$Sets.newHashSet();
                    final Set<Class<? extends Annotation>> overridesScopeAnnotations = (Set<Class<? extends Annotation>>)$Sets.newHashSet();
                    new ModuleWriter(binder) {
                        @Override
                        public <T> Void visit(final Binding<T> binding) {
                            overriddenKeys.add(binding.getKey());
                            return super.visit(binding);
                        }
                        
                        @Override
                        public Void visit(final ScopeBinding scopeBinding) {
                            overridesScopeAnnotations.add(scopeBinding.getAnnotationType());
                            return super.visit(scopeBinding);
                        }
                        
                        @Override
                        public Void visit(final PrivateElements privateElements) {
                            overriddenKeys.addAll(privateElements.getExposedKeys());
                            return super.visit(privateElements);
                        }
                    }.writeAll(overrideElements);
                    final Map<Scope, Object> scopeInstancesInUse = (Map<Scope, Object>)$Maps.newHashMap();
                    final List<ScopeBinding> scopeBindings = (List<ScopeBinding>)$Lists.newArrayList();
                    new ModuleWriter(binder) {
                        @Override
                        public <T> Void visit(final Binding<T> binding) {
                            if (!overriddenKeys.remove(binding.getKey())) {
                                super.visit(binding);
                                final Scope scope = Modules$RealOverriddenModuleBuilder$1.this.getScopeInstanceOrNull(binding);
                                if (scope != null) {
                                    scopeInstancesInUse.put(scope, binding.getSource());
                                }
                            }
                            return null;
                        }
                        
                        void rewrite(final Binder binder, final PrivateElements privateElements, final Set<Key<?>> keysToSkip) {
                            final PrivateBinder privateBinder = binder.withSource(privateElements.getSource()).newPrivateBinder();
                            final Set<Key<?>> skippedExposes = (Set<Key<?>>)$Sets.newHashSet();
                            for (final Key<?> key : privateElements.getExposedKeys()) {
                                if (keysToSkip.remove(key)) {
                                    skippedExposes.add(key);
                                }
                                else {
                                    privateBinder.withSource(privateElements.getExposedSource(key)).expose(key);
                                }
                            }
                            for (final Element element : privateElements.getElements()) {
                                if (element instanceof Binding && skippedExposes.remove(((Binding)element).getKey())) {
                                    continue;
                                }
                                if (element instanceof PrivateElements) {
                                    this.rewrite(privateBinder, (PrivateElements)element, skippedExposes);
                                }
                                else {
                                    element.applyTo(privateBinder);
                                }
                            }
                        }
                        
                        @Override
                        public Void visit(final PrivateElements privateElements) {
                            this.rewrite(this.binder, privateElements, overriddenKeys);
                            return null;
                        }
                        
                        @Override
                        public Void visit(final ScopeBinding scopeBinding) {
                            scopeBindings.add(scopeBinding);
                            return null;
                        }
                    }.writeAll(elements);
                    new ModuleWriter(binder) {
                        @Override
                        public Void visit(final ScopeBinding scopeBinding) {
                            if (!overridesScopeAnnotations.remove(scopeBinding.getAnnotationType())) {
                                super.visit(scopeBinding);
                            }
                            else {
                                final Object source = scopeInstancesInUse.get(scopeBinding.getScope());
                                if (source != null) {
                                    this.binder.withSource(source).addError("The scope for @%s is bound directly and cannot be overridden.", scopeBinding.getAnnotationType().getSimpleName());
                                }
                            }
                            return null;
                        }
                    }.writeAll(scopeBindings);
                }
                
                private Scope getScopeInstanceOrNull(final Binding<?> binding) {
                    return binding.acceptScopingVisitor((BindingScopingVisitor<Scope>)new DefaultBindingScopingVisitor<Scope>() {
                        @Override
                        public Scope visitScope(final Scope scope) {
                            return scope;
                        }
                    });
                }
            };
        }
    }
    
    private static class ModuleWriter extends DefaultElementVisitor<Void>
    {
        protected final Binder binder;
        
        ModuleWriter(final Binder binder) {
            this.binder = binder;
        }
        
        @Override
        protected Void visitOther(final Element element) {
            element.applyTo(this.binder);
            return null;
        }
        
        void writeAll(final Iterable<? extends Element> elements) {
            for (final Element element : elements) {
                element.acceptVisitor((ElementVisitor<Object>)this);
            }
        }
    }
    
    public interface OverriddenModuleBuilder
    {
        Module with(final Module... p0);
        
        Module with(final Iterable<? extends Module> p0);
    }
}
