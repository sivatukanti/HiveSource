// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.context.Dependent;
import java.util.Collections;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.HashSet;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.enterprise.inject.spi.Bean;

public abstract class AbstractBean<T> implements Bean<T>
{
    private Class<?> klass;
    private Set<Annotation> qualifiers;
    private Set<Type> types;
    
    public AbstractBean(final Class<?> klass, final Annotation qualifier) {
        this(klass, klass, qualifier);
    }
    
    public AbstractBean(final Class<?> klass, final Set<Annotation> qualifiers) {
        this(klass, klass, qualifiers);
    }
    
    public AbstractBean(final Class<?> klass, final Type type, final Annotation qualifier) {
        this.klass = klass;
        (this.qualifiers = new HashSet<Annotation>()).add(qualifier);
        (this.types = new HashSet<Type>()).add(type);
    }
    
    public AbstractBean(final Class<?> klass, final Type type, final Set<Annotation> qualifiers) {
        this.klass = klass;
        this.qualifiers = qualifiers;
        (this.types = new HashSet<Type>()).add(type);
    }
    
    public Class<?> getBeanClass() {
        return this.klass;
    }
    
    public Set<InjectionPoint> getInjectionPoints() {
        return (Set<InjectionPoint>)Collections.EMPTY_SET;
    }
    
    public String getName() {
        return null;
    }
    
    public Set<Annotation> getQualifiers() {
        return this.qualifiers;
    }
    
    public Class<? extends Annotation> getScope() {
        return (Class<? extends Annotation>)Dependent.class;
    }
    
    public Set<Class<? extends Annotation>> getStereotypes() {
        return (Set<Class<? extends Annotation>>)Collections.EMPTY_SET;
    }
    
    public Set<Type> getTypes() {
        return this.types;
    }
    
    public boolean isAlternative() {
        return false;
    }
    
    public boolean isNullable() {
        return false;
    }
    
    public abstract T create(final CreationalContext<T> p0);
    
    public void destroy(final T instance, final CreationalContext<T> creationalContext) {
    }
}
