// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import com.google.inject.internal.util.$ImmutableList;
import java.util.Iterator;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$Lists;
import java.util.Collections;
import com.google.inject.internal.util.$Maps;
import com.google.inject.spi.TypeListenerBinding;
import com.google.inject.spi.TypeConverterBinding;
import java.util.List;
import com.google.inject.Scope;
import java.lang.annotation.Annotation;
import com.google.inject.Binding;
import com.google.inject.Key;
import java.util.Map;

final class InheritingState implements State
{
    private final State parent;
    private final Map<Key<?>, Binding<?>> explicitBindingsMutable;
    private final Map<Key<?>, Binding<?>> explicitBindings;
    private final Map<Class<? extends Annotation>, Scope> scopes;
    private final List<TypeConverterBinding> converters;
    private final List<MethodAspect> methodAspects;
    private final List<TypeListenerBinding> listenerBindings;
    private final WeakKeySet blacklistedKeys;
    private final Object lock;
    
    InheritingState(final State parent) {
        this.explicitBindingsMutable = (Map<Key<?>, Binding<?>>)$Maps.newLinkedHashMap();
        this.explicitBindings = Collections.unmodifiableMap((Map<? extends Key<?>, ? extends Binding<?>>)this.explicitBindingsMutable);
        this.scopes = (Map<Class<? extends Annotation>, Scope>)$Maps.newHashMap();
        this.converters = (List<TypeConverterBinding>)$Lists.newArrayList();
        this.methodAspects = (List<MethodAspect>)$Lists.newArrayList();
        this.listenerBindings = (List<TypeListenerBinding>)$Lists.newArrayList();
        this.blacklistedKeys = new WeakKeySet();
        this.parent = $Preconditions.checkNotNull(parent, (Object)"parent");
        this.lock = ((parent == State.NONE) ? this : parent.lock());
    }
    
    public State parent() {
        return this.parent;
    }
    
    public <T> BindingImpl<T> getExplicitBinding(final Key<T> key) {
        final Binding<?> binding = this.explicitBindings.get(key);
        return (binding != null) ? ((BindingImpl)binding) : this.parent.getExplicitBinding(key);
    }
    
    public Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel() {
        return this.explicitBindings;
    }
    
    public void putBinding(final Key<?> key, final BindingImpl<?> binding) {
        this.explicitBindingsMutable.put(key, binding);
    }
    
    public Scope getScope(final Class<? extends Annotation> annotationType) {
        final Scope scope = this.scopes.get(annotationType);
        return (scope != null) ? scope : this.parent.getScope(annotationType);
    }
    
    public void putAnnotation(final Class<? extends Annotation> annotationType, final Scope scope) {
        this.scopes.put(annotationType, scope);
    }
    
    public Iterable<TypeConverterBinding> getConvertersThisLevel() {
        return this.converters;
    }
    
    public void addConverter(final TypeConverterBinding typeConverterBinding) {
        this.converters.add(typeConverterBinding);
    }
    
    public TypeConverterBinding getConverter(final String stringValue, final TypeLiteral<?> type, final Errors errors, final Object source) {
        TypeConverterBinding matchingConverter = null;
        for (State s = this; s != State.NONE; s = s.parent()) {
            for (final TypeConverterBinding converter : s.getConvertersThisLevel()) {
                if (converter.getTypeMatcher().matches(type)) {
                    if (matchingConverter != null) {
                        errors.ambiguousTypeConversion(stringValue, source, type, matchingConverter, converter);
                    }
                    matchingConverter = converter;
                }
            }
        }
        return matchingConverter;
    }
    
    public void addMethodAspect(final MethodAspect methodAspect) {
        this.methodAspects.add(methodAspect);
    }
    
    public $ImmutableList<MethodAspect> getMethodAspects() {
        return new $ImmutableList.Builder<MethodAspect>().addAll(this.parent.getMethodAspects()).addAll(this.methodAspects).build();
    }
    
    public void addTypeListener(final TypeListenerBinding listenerBinding) {
        this.listenerBindings.add(listenerBinding);
    }
    
    public List<TypeListenerBinding> getTypeListenerBindings() {
        final List<TypeListenerBinding> parentBindings = this.parent.getTypeListenerBindings();
        final List<TypeListenerBinding> result = new ArrayList<TypeListenerBinding>(parentBindings.size() + 1);
        result.addAll(parentBindings);
        result.addAll(this.listenerBindings);
        return result;
    }
    
    public void blacklist(final Key<?> key, final Object source) {
        this.parent.blacklist(key, source);
        this.blacklistedKeys.add(key, source);
    }
    
    public boolean isBlacklisted(final Key<?> key) {
        return this.blacklistedKeys.contains(key);
    }
    
    public Set<Object> getSourcesForBlacklistedKey(final Key<?> key) {
        return this.blacklistedKeys.getSources(key);
    }
    
    public Object lock() {
        return this.lock;
    }
    
    public Map<Class<? extends Annotation>, Scope> getScopes() {
        return this.scopes;
    }
}
