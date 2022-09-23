// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.Provider;
import com.google.inject.Key;
import com.google.inject.internal.util.$Objects;
import com.google.inject.Stage;
import com.google.inject.Scopes;
import com.google.inject.Scope;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.Singleton;
import java.lang.annotation.Annotation;

public abstract class Scoping
{
    public static final Scoping UNSCOPED;
    public static final Scoping SINGLETON_ANNOTATION;
    public static final Scoping SINGLETON_INSTANCE;
    public static final Scoping EAGER_SINGLETON;
    
    public static Scoping forAnnotation(final Class<? extends Annotation> scopingAnnotation) {
        if (scopingAnnotation == Singleton.class || scopingAnnotation == javax.inject.Singleton.class) {
            return Scoping.SINGLETON_ANNOTATION;
        }
        return new Scoping() {
            @Override
            public <V> V acceptVisitor(final BindingScopingVisitor<V> visitor) {
                return visitor.visitScopeAnnotation(scopingAnnotation);
            }
            
            @Override
            public Class<? extends Annotation> getScopeAnnotation() {
                return scopingAnnotation;
            }
            
            @Override
            public String toString() {
                return scopingAnnotation.getName();
            }
            
            @Override
            public void applyTo(final ScopedBindingBuilder scopedBindingBuilder) {
                scopedBindingBuilder.in(scopingAnnotation);
            }
        };
    }
    
    public static Scoping forInstance(final Scope scope) {
        if (scope == Scopes.SINGLETON) {
            return Scoping.SINGLETON_INSTANCE;
        }
        return new Scoping() {
            @Override
            public <V> V acceptVisitor(final BindingScopingVisitor<V> visitor) {
                return visitor.visitScope(scope);
            }
            
            @Override
            public Scope getScopeInstance() {
                return scope;
            }
            
            @Override
            public String toString() {
                return scope.toString();
            }
            
            @Override
            public void applyTo(final ScopedBindingBuilder scopedBindingBuilder) {
                scopedBindingBuilder.in(scope);
            }
        };
    }
    
    public boolean isExplicitlyScoped() {
        return this != Scoping.UNSCOPED;
    }
    
    public boolean isNoScope() {
        return this.getScopeInstance() == Scopes.NO_SCOPE;
    }
    
    public boolean isEagerSingleton(final Stage stage) {
        return this == Scoping.EAGER_SINGLETON || (stage == Stage.PRODUCTION && (this == Scoping.SINGLETON_ANNOTATION || this == Scoping.SINGLETON_INSTANCE));
    }
    
    public Scope getScopeInstance() {
        return null;
    }
    
    public Class<? extends Annotation> getScopeAnnotation() {
        return null;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Scoping) {
            final Scoping o = (Scoping)obj;
            return $Objects.equal(this.getScopeAnnotation(), o.getScopeAnnotation()) && $Objects.equal(this.getScopeInstance(), o.getScopeInstance());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.getScopeAnnotation(), this.getScopeInstance());
    }
    
    public abstract <V> V acceptVisitor(final BindingScopingVisitor<V> p0);
    
    public abstract void applyTo(final ScopedBindingBuilder p0);
    
    private Scoping() {
    }
    
    static <T> InternalFactory<? extends T> scope(final Key<T> key, final InjectorImpl injector, final InternalFactory<? extends T> creator, final Object source, final Scoping scoping) {
        if (scoping.isNoScope()) {
            return creator;
        }
        final Scope scope = scoping.getScopeInstance();
        final Provider<T> scoped = scope.scope(key, new ProviderToInternalFactoryAdapter<T>(injector, creator));
        return (InternalFactory<? extends T>)new InternalFactoryToProviderAdapter<T>((Initializable<Provider<? extends T>>)Initializables.of((Provider<? extends T>)scoped), source);
    }
    
    static Scoping makeInjectable(final Scoping scoping, final InjectorImpl injector, final Errors errors) {
        final Class<? extends Annotation> scopeAnnotation = scoping.getScopeAnnotation();
        if (scopeAnnotation == null) {
            return scoping;
        }
        final Scope scope = injector.state.getScope(scopeAnnotation);
        if (scope != null) {
            return forInstance(scope);
        }
        errors.scopeNotFound(scopeAnnotation);
        return Scoping.UNSCOPED;
    }
    
    static {
        UNSCOPED = new Scoping() {
            @Override
            public <V> V acceptVisitor(final BindingScopingVisitor<V> visitor) {
                return visitor.visitNoScoping();
            }
            
            @Override
            public Scope getScopeInstance() {
                return Scopes.NO_SCOPE;
            }
            
            @Override
            public String toString() {
                return Scopes.NO_SCOPE.toString();
            }
            
            @Override
            public void applyTo(final ScopedBindingBuilder scopedBindingBuilder) {
            }
        };
        SINGLETON_ANNOTATION = new Scoping() {
            @Override
            public <V> V acceptVisitor(final BindingScopingVisitor<V> visitor) {
                return visitor.visitScopeAnnotation(Singleton.class);
            }
            
            @Override
            public Class<? extends Annotation> getScopeAnnotation() {
                return Singleton.class;
            }
            
            @Override
            public String toString() {
                return Singleton.class.getName();
            }
            
            @Override
            public void applyTo(final ScopedBindingBuilder scopedBindingBuilder) {
                scopedBindingBuilder.in(Singleton.class);
            }
        };
        SINGLETON_INSTANCE = new Scoping() {
            @Override
            public <V> V acceptVisitor(final BindingScopingVisitor<V> visitor) {
                return visitor.visitScope(Scopes.SINGLETON);
            }
            
            @Override
            public Scope getScopeInstance() {
                return Scopes.SINGLETON;
            }
            
            @Override
            public String toString() {
                return Scopes.SINGLETON.toString();
            }
            
            @Override
            public void applyTo(final ScopedBindingBuilder scopedBindingBuilder) {
                scopedBindingBuilder.in(Scopes.SINGLETON);
            }
        };
        EAGER_SINGLETON = new Scoping() {
            @Override
            public <V> V acceptVisitor(final BindingScopingVisitor<V> visitor) {
                return visitor.visitEagerSingleton();
            }
            
            @Override
            public Scope getScopeInstance() {
                return Scopes.SINGLETON;
            }
            
            @Override
            public String toString() {
                return "eager singleton";
            }
            
            @Override
            public void applyTo(final ScopedBindingBuilder scopedBindingBuilder) {
                scopedBindingBuilder.asEagerSingleton();
            }
        };
    }
}
