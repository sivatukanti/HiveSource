// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Method;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import java.io.Serializable;

public class SettableAnyProperty implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final BeanProperty _property;
    protected final transient Method _setter;
    protected final JavaType _type;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    
    @Deprecated
    public SettableAnyProperty(final BeanProperty property, final AnnotatedMethod setter, final JavaType type, final JsonDeserializer<Object> valueDeser) {
        this(property, setter, type, valueDeser, null);
    }
    
    public SettableAnyProperty(final BeanProperty property, final AnnotatedMethod setter, final JavaType type, final JsonDeserializer<Object> valueDeser, final TypeDeserializer typeDeser) {
        this(property, setter.getAnnotated(), type, valueDeser, typeDeser);
    }
    
    @Deprecated
    public SettableAnyProperty(final BeanProperty property, final Method rawSetter, final JavaType type, final JsonDeserializer<Object> valueDeser) {
        this(property, rawSetter, type, valueDeser, null);
    }
    
    public SettableAnyProperty(final BeanProperty property, final Method rawSetter, final JavaType type, final JsonDeserializer<Object> valueDeser, final TypeDeserializer typeDeser) {
        this._property = property;
        this._type = type;
        this._setter = rawSetter;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = typeDeser;
    }
    
    public SettableAnyProperty withValueDeserializer(final JsonDeserializer<Object> deser) {
        return new SettableAnyProperty(this._property, this._setter, this._type, deser, this._valueTypeDeserializer);
    }
    
    public BeanProperty getProperty() {
        return this._property;
    }
    
    public boolean hasValueDeserializer() {
        return this._valueDeserializer != null;
    }
    
    public JavaType getType() {
        return this._type;
    }
    
    public final void deserializeAndSet(final JsonParser jp, final DeserializationContext ctxt, final Object instance, final String propName) throws IOException {
        try {
            this.set(instance, propName, this.deserialize(jp, ctxt));
        }
        catch (UnresolvedForwardReference reference) {
            if (this._valueDeserializer.getObjectIdReader() == null) {
                throw JsonMappingException.from(jp, "Unresolved forward reference but no identity info.", reference);
            }
            final AnySetterReferring referring = new AnySetterReferring(this, reference, this._type.getRawClass(), instance, propName);
            reference.getRoid().appendReferring(referring);
        }
    }
    
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return null;
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(jp, ctxt, this._valueTypeDeserializer);
        }
        return this._valueDeserializer.deserialize(jp, ctxt);
    }
    
    public void set(final Object instance, final String propName, final Object value) throws IOException {
        try {
            this._setter.invoke(instance, propName, value);
        }
        catch (Exception e) {
            this._throwAsIOE(e, propName, value);
        }
    }
    
    protected void _throwAsIOE(final Exception e, final String propName, final Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            final String actType = (value == null) ? "[NULL]" : value.getClass().getName();
            final StringBuilder msg = new StringBuilder("Problem deserializing \"any\" property '").append(propName);
            msg.append("' of class " + this.getClassName() + " (expected type: ").append(this._type);
            msg.append("; actual type: ").append(actType).append(")");
            final String origMsg = e.getMessage();
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            }
            else {
                msg.append(" (no error message provided)");
            }
            throw new JsonMappingException(msg.toString(), null, e);
        }
        if (e instanceof IOException) {
            throw (IOException)e;
        }
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }
        Throwable t;
        for (t = e; t.getCause() != null; t = t.getCause()) {}
        throw new JsonMappingException(t.getMessage(), null, t);
    }
    
    private String getClassName() {
        return this._setter.getDeclaringClass().getName();
    }
    
    @Override
    public String toString() {
        return "[any property on class " + this.getClassName() + "]";
    }
    
    private static class AnySetterReferring extends ReadableObjectId.Referring
    {
        private final SettableAnyProperty _parent;
        private final Object _pojo;
        private final String _propName;
        
        public AnySetterReferring(final SettableAnyProperty parent, final UnresolvedForwardReference reference, final Class<?> type, final Object instance, final String propName) {
            super(reference, type);
            this._parent = parent;
            this._pojo = instance;
            this._propName = propName;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            if (!this.hasId(id)) {
                throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id.toString() + "] that wasn't previously registered.");
            }
            this._parent.set(this._pojo, this._propName, value);
        }
    }
}
