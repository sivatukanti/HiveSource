// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import java.lang.reflect.Constructor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class InnerClassProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final SettableBeanProperty _delegate;
    protected final Constructor<?> _creator;
    
    public InnerClassProperty(final SettableBeanProperty delegate, final Constructor<?> ctor) {
        super(delegate);
        this._delegate = delegate;
        this._creator = ctor;
    }
    
    protected InnerClassProperty(final InnerClassProperty src, final JsonDeserializer<?> deser) {
        super(src, deser);
        this._delegate = src._delegate.withValueDeserializer(deser);
        this._creator = src._creator;
    }
    
    protected InnerClassProperty(final InnerClassProperty src, final PropertyName newName) {
        super(src, newName);
        this._delegate = src._delegate.withName(newName);
        this._creator = src._creator;
    }
    
    @Override
    public InnerClassProperty withName(final PropertyName newName) {
        return new InnerClassProperty(this, newName);
    }
    
    @Override
    public InnerClassProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        return new InnerClassProperty(this, deser);
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return this._delegate.getAnnotation(acls);
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._delegate.getMember();
    }
    
    @Override
    public void deserializeAndSet(final JsonParser jp, final DeserializationContext ctxt, final Object bean) throws IOException, JsonProcessingException {
        final JsonToken t = jp.getCurrentToken();
        Object value;
        if (t == JsonToken.VALUE_NULL) {
            value = ((this._nullProvider == null) ? null : this._nullProvider.nullValue(ctxt));
        }
        else if (this._valueTypeDeserializer != null) {
            value = this._valueDeserializer.deserializeWithType(jp, ctxt, this._valueTypeDeserializer);
        }
        else {
            try {
                value = this._creator.newInstance(bean);
            }
            catch (Exception e) {
                ClassUtil.unwrapAndThrowAsIAE(e, "Failed to instantiate class " + this._creator.getDeclaringClass().getName() + ", problem: " + e.getMessage());
                value = null;
            }
            this._valueDeserializer.deserialize(jp, ctxt, value);
        }
        this.set(bean, value);
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        return this.setAndReturn(instance, this.deserialize(jp, ctxt));
    }
    
    @Override
    public final void set(final Object instance, final Object value) throws IOException {
        this._delegate.set(instance, value);
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        return this._delegate.setAndReturn(instance, value);
    }
}
