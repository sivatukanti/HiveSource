// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser;

import parquet.org.codehaus.jackson.JsonLocation;
import parquet.org.codehaus.jackson.map.JsonMappingException;
import parquet.org.codehaus.jackson.JsonToken;
import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.map.introspect.AnnotatedMethod;
import parquet.org.codehaus.jackson.map.JsonDeserializer;
import parquet.org.codehaus.jackson.type.JavaType;
import java.lang.reflect.Method;
import parquet.org.codehaus.jackson.map.BeanProperty;

public final class SettableAnyProperty
{
    protected final BeanProperty _property;
    protected final Method _setter;
    protected final JavaType _type;
    protected JsonDeserializer<Object> _valueDeserializer;
    
    @Deprecated
    public SettableAnyProperty(final BeanProperty property, final AnnotatedMethod setter, final JavaType type) {
        this(property, setter, type, null);
    }
    
    public SettableAnyProperty(final BeanProperty property, final AnnotatedMethod setter, final JavaType type, final JsonDeserializer<Object> valueDeser) {
        this(property, setter.getAnnotated(), type, valueDeser);
    }
    
    public SettableAnyProperty(final BeanProperty property, final Method rawSetter, final JavaType type, final JsonDeserializer<Object> valueDeser) {
        this._property = property;
        this._type = type;
        this._setter = rawSetter;
        this._valueDeserializer = valueDeser;
    }
    
    public SettableAnyProperty withValueDeserializer(final JsonDeserializer<Object> deser) {
        return new SettableAnyProperty(this._property, this._setter, this._type, deser);
    }
    
    @Deprecated
    public void setValueDeserializer(final JsonDeserializer<Object> deser) {
        if (this._valueDeserializer != null) {
            throw new IllegalStateException("Already had assigned deserializer for SettableAnyProperty");
        }
        this._valueDeserializer = deser;
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
    
    public final void deserializeAndSet(final JsonParser jp, final DeserializationContext ctxt, final Object instance, final String propName) throws IOException, JsonProcessingException {
        this.set(instance, propName, this.deserialize(jp, ctxt));
    }
    
    public final Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return null;
        }
        return this._valueDeserializer.deserialize(jp, ctxt);
    }
    
    public final void set(final Object instance, final String propName, final Object value) throws IOException {
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
}
