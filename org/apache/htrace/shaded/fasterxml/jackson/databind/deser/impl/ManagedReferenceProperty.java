// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class ManagedReferenceProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final String _referenceName;
    protected final boolean _isContainer;
    protected final SettableBeanProperty _managedProperty;
    protected final SettableBeanProperty _backProperty;
    
    public ManagedReferenceProperty(final SettableBeanProperty forward, final String refName, final SettableBeanProperty backward, final Annotations contextAnnotations, final boolean isContainer) {
        super(forward.getFullName(), forward.getType(), forward.getWrapperName(), forward.getValueTypeDeserializer(), contextAnnotations, forward.getMetadata());
        this._referenceName = refName;
        this._managedProperty = forward;
        this._backProperty = backward;
        this._isContainer = isContainer;
    }
    
    protected ManagedReferenceProperty(final ManagedReferenceProperty src, final JsonDeserializer<?> deser) {
        super(src, deser);
        this._referenceName = src._referenceName;
        this._isContainer = src._isContainer;
        this._managedProperty = src._managedProperty;
        this._backProperty = src._backProperty;
    }
    
    protected ManagedReferenceProperty(final ManagedReferenceProperty src, final PropertyName newName) {
        super(src, newName);
        this._referenceName = src._referenceName;
        this._isContainer = src._isContainer;
        this._managedProperty = src._managedProperty;
        this._backProperty = src._backProperty;
    }
    
    @Override
    public ManagedReferenceProperty withName(final PropertyName newName) {
        return new ManagedReferenceProperty(this, newName);
    }
    
    @Override
    public ManagedReferenceProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        return new ManagedReferenceProperty(this, deser);
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return this._managedProperty.getAnnotation(acls);
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._managedProperty.getMember();
    }
    
    @Override
    public void deserializeAndSet(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        this.set(instance, this._managedProperty.deserialize(jp, ctxt));
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        return this.setAndReturn(instance, this.deserialize(jp, ctxt));
    }
    
    @Override
    public final void set(final Object instance, final Object value) throws IOException {
        this.setAndReturn(instance, value);
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        if (value != null) {
            if (this._isContainer) {
                if (value instanceof Object[]) {
                    for (final Object ob : (Object[])value) {
                        if (ob != null) {
                            this._backProperty.set(ob, instance);
                        }
                    }
                }
                else if (value instanceof Collection) {
                    for (final Object ob2 : (Collection)value) {
                        if (ob2 != null) {
                            this._backProperty.set(ob2, instance);
                        }
                    }
                }
                else {
                    if (!(value instanceof Map)) {
                        throw new IllegalStateException("Unsupported container type (" + value.getClass().getName() + ") when resolving reference '" + this._referenceName + "'");
                    }
                    for (final Object ob2 : ((Map)value).values()) {
                        if (ob2 != null) {
                            this._backProperty.set(ob2, instance);
                        }
                    }
                }
            }
            else {
                this._backProperty.set(value, instance);
            }
        }
        return this._managedProperty.setAndReturn(instance, value);
    }
}
