// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$ImmutableMap;
import com.google.inject.internal.util.$ImmutableSet;
import java.util.Set;
import java.util.List;
import com.google.inject.spi.TypeListenerBinding;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.Scope;
import java.lang.annotation.Annotation;
import com.google.inject.Binding;
import java.util.Map;
import com.google.inject.Key;

interface State
{
    public static final State NONE = new State() {
        public State parent() {
            throw new UnsupportedOperationException();
        }
        
        public <T> BindingImpl<T> getExplicitBinding(final Key<T> key) {
            return null;
        }
        
        public Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel() {
            throw new UnsupportedOperationException();
        }
        
        public void putBinding(final Key<?> key, final BindingImpl<?> binding) {
            throw new UnsupportedOperationException();
        }
        
        public Scope getScope(final Class<? extends Annotation> scopingAnnotation) {
            return null;
        }
        
        public void putAnnotation(final Class<? extends Annotation> annotationType, final Scope scope) {
            throw new UnsupportedOperationException();
        }
        
        public void addConverter(final TypeConverterBinding typeConverterBinding) {
            throw new UnsupportedOperationException();
        }
        
        public TypeConverterBinding getConverter(final String stringValue, final TypeLiteral<?> type, final Errors errors, final Object source) {
            throw new UnsupportedOperationException();
        }
        
        public Iterable<TypeConverterBinding> getConvertersThisLevel() {
            return (Iterable<TypeConverterBinding>)$ImmutableSet.of();
        }
        
        public void addMethodAspect(final MethodAspect methodAspect) {
            throw new UnsupportedOperationException();
        }
        
        public $ImmutableList<MethodAspect> getMethodAspects() {
            return $ImmutableList.of();
        }
        
        public void addTypeListener(final TypeListenerBinding typeListenerBinding) {
            throw new UnsupportedOperationException();
        }
        
        public List<TypeListenerBinding> getTypeListenerBindings() {
            return (List<TypeListenerBinding>)$ImmutableList.of();
        }
        
        public void blacklist(final Key<?> key, final Object source) {
        }
        
        public boolean isBlacklisted(final Key<?> key) {
            return true;
        }
        
        public Set<Object> getSourcesForBlacklistedKey(final Key<?> key) {
            throw new UnsupportedOperationException();
        }
        
        public Object lock() {
            throw new UnsupportedOperationException();
        }
        
        public Map<Class<? extends Annotation>, Scope> getScopes() {
            return (Map<Class<? extends Annotation>, Scope>)$ImmutableMap.of();
        }
    };
    
    State parent();
    
     <T> BindingImpl<T> getExplicitBinding(final Key<T> p0);
    
    Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel();
    
    void putBinding(final Key<?> p0, final BindingImpl<?> p1);
    
    Scope getScope(final Class<? extends Annotation> p0);
    
    void putAnnotation(final Class<? extends Annotation> p0, final Scope p1);
    
    void addConverter(final TypeConverterBinding p0);
    
    TypeConverterBinding getConverter(final String p0, final TypeLiteral<?> p1, final Errors p2, final Object p3);
    
    Iterable<TypeConverterBinding> getConvertersThisLevel();
    
    void addMethodAspect(final MethodAspect p0);
    
    $ImmutableList<MethodAspect> getMethodAspects();
    
    void addTypeListener(final TypeListenerBinding p0);
    
    List<TypeListenerBinding> getTypeListenerBindings();
    
    void blacklist(final Key<?> p0, final Object p1);
    
    boolean isBlacklisted(final Key<?> p0);
    
    Set<Object> getSourcesForBlacklistedKey(final Key<?> p0);
    
    Object lock();
    
    Map<Class<? extends Annotation>, Scope> getScopes();
}
