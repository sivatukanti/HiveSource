// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.InstanceBinding;
import com.google.inject.Scope;
import com.google.inject.internal.util.$Preconditions;
import java.lang.annotation.Annotation;
import com.google.inject.Binder;
import com.google.inject.spi.Element;
import java.util.List;
import com.google.inject.Key;

public abstract class AbstractBindingBuilder<T>
{
    public static final String IMPLEMENTATION_ALREADY_SET = "Implementation is set more than once.";
    public static final String SINGLE_INSTANCE_AND_SCOPE = "Setting the scope is not permitted when binding to a single instance.";
    public static final String SCOPE_ALREADY_SET = "Scope is set more than once.";
    public static final String BINDING_TO_NULL = "Binding to null instances is not allowed. Use toProvider(Providers.of(null)) if this is your intended behaviour.";
    public static final String CONSTANT_VALUE_ALREADY_SET = "Constant value is set more than once.";
    public static final String ANNOTATION_ALREADY_SPECIFIED = "More than one annotation is specified for this binding.";
    protected static final Key<?> NULL_KEY;
    protected List<Element> elements;
    protected int position;
    protected final Binder binder;
    private BindingImpl<T> binding;
    
    public AbstractBindingBuilder(final Binder binder, final List<Element> elements, final Object source, final Key<T> key) {
        this.binder = binder;
        this.elements = elements;
        this.position = elements.size();
        this.binding = new UntargettedBindingImpl<T>(source, key, Scoping.UNSCOPED);
        elements.add(this.position, this.binding);
    }
    
    protected BindingImpl<T> getBinding() {
        return this.binding;
    }
    
    protected BindingImpl<T> setBinding(final BindingImpl<T> binding) {
        this.binding = binding;
        this.elements.set(this.position, binding);
        return binding;
    }
    
    protected BindingImpl<T> annotatedWithInternal(final Class<? extends Annotation> annotationType) {
        $Preconditions.checkNotNull(annotationType, (Object)"annotationType");
        this.checkNotAnnotated();
        return this.setBinding(this.binding.withKey(Key.get(this.binding.getKey().getTypeLiteral(), annotationType)));
    }
    
    protected BindingImpl<T> annotatedWithInternal(final Annotation annotation) {
        $Preconditions.checkNotNull(annotation, (Object)"annotation");
        this.checkNotAnnotated();
        return this.setBinding(this.binding.withKey(Key.get(this.binding.getKey().getTypeLiteral(), annotation)));
    }
    
    public void in(final Class<? extends Annotation> scopeAnnotation) {
        $Preconditions.checkNotNull(scopeAnnotation, (Object)"scopeAnnotation");
        this.checkNotScoped();
        this.setBinding(this.getBinding().withScoping(Scoping.forAnnotation(scopeAnnotation)));
    }
    
    public void in(final Scope scope) {
        $Preconditions.checkNotNull(scope, (Object)"scope");
        this.checkNotScoped();
        this.setBinding(this.getBinding().withScoping(Scoping.forInstance(scope)));
    }
    
    public void asEagerSingleton() {
        this.checkNotScoped();
        this.setBinding(this.getBinding().withScoping(Scoping.EAGER_SINGLETON));
    }
    
    protected boolean keyTypeIsSet() {
        return !Void.class.equals(this.binding.getKey().getTypeLiteral().getType());
    }
    
    protected void checkNotTargetted() {
        if (!(this.binding instanceof UntargettedBindingImpl)) {
            this.binder.addError("Implementation is set more than once.", new Object[0]);
        }
    }
    
    protected void checkNotAnnotated() {
        if (this.binding.getKey().getAnnotationType() != null) {
            this.binder.addError("More than one annotation is specified for this binding.", new Object[0]);
        }
    }
    
    protected void checkNotScoped() {
        if (this.binding instanceof InstanceBinding) {
            this.binder.addError("Setting the scope is not permitted when binding to a single instance.", new Object[0]);
            return;
        }
        if (this.binding.getScoping().isExplicitlyScoped()) {
            this.binder.addError("Scope is set more than once.", new Object[0]);
        }
    }
    
    static {
        NULL_KEY = Key.get((Class<?>)Void.class);
    }
}
