// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.InjectionPoint;
import java.util.Set;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import com.google.inject.spi.Element;
import java.util.List;
import com.google.inject.Binder;
import com.google.inject.binder.ConstantBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;

public final class ConstantBindingBuilderImpl<T> extends AbstractBindingBuilder<T> implements AnnotatedConstantBindingBuilder, ConstantBindingBuilder
{
    public ConstantBindingBuilderImpl(final Binder binder, final List<Element> elements, final Object source) {
        super(binder, elements, source, ConstantBindingBuilderImpl.NULL_KEY);
    }
    
    public ConstantBindingBuilder annotatedWith(final Class<? extends Annotation> annotationType) {
        this.annotatedWithInternal(annotationType);
        return this;
    }
    
    public ConstantBindingBuilder annotatedWith(final Annotation annotation) {
        this.annotatedWithInternal(annotation);
        return this;
    }
    
    public void to(final String value) {
        this.toConstant(String.class, value);
    }
    
    public void to(final int value) {
        this.toConstant(Integer.class, value);
    }
    
    public void to(final long value) {
        this.toConstant(Long.class, value);
    }
    
    public void to(final boolean value) {
        this.toConstant(Boolean.class, value);
    }
    
    public void to(final double value) {
        this.toConstant(Double.class, value);
    }
    
    public void to(final float value) {
        this.toConstant(Float.class, value);
    }
    
    public void to(final short value) {
        this.toConstant(Short.class, value);
    }
    
    public void to(final char value) {
        this.toConstant(Character.class, value);
    }
    
    public void to(final byte value) {
        this.toConstant(Byte.class, value);
    }
    
    public void to(final Class<?> value) {
        this.toConstant(Class.class, value);
    }
    
    public <E extends Enum<E>> void to(final E value) {
        this.toConstant(value.getDeclaringClass(), value);
    }
    
    private void toConstant(final Class<?> type, final Object instance) {
        final Class<T> typeAsClassT = (Class<T>)type;
        final T instanceAsT = (T)instance;
        if (this.keyTypeIsSet()) {
            this.binder.addError("Constant value is set more than once.", new Object[0]);
            return;
        }
        final BindingImpl<T> base = this.getBinding();
        Key<T> key;
        if (base.getKey().getAnnotation() != null) {
            key = Key.get(typeAsClassT, base.getKey().getAnnotation());
        }
        else if (base.getKey().getAnnotationType() != null) {
            key = Key.get(typeAsClassT, base.getKey().getAnnotationType());
        }
        else {
            key = Key.get(typeAsClassT);
        }
        if (instanceAsT == null) {
            this.binder.addError("Binding to null instances is not allowed. Use toProvider(Providers.of(null)) if this is your intended behaviour.", new Object[0]);
        }
        this.setBinding(new InstanceBindingImpl<T>(base.getSource(), key, base.getScoping(), (Set<InjectionPoint>)$ImmutableSet.of(), instanceAsT));
    }
    
    @Override
    public String toString() {
        return "ConstantBindingBuilder";
    }
}
