// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.SchemaAware;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ResolvableSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;

public class StdDelegatingSerializer extends StdSerializer<Object> implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable, SchemaAware
{
    protected final Converter<Object, ?> _converter;
    protected final JavaType _delegateType;
    protected final JsonSerializer<Object> _delegateSerializer;
    
    public StdDelegatingSerializer(final Converter<?, ?> converter) {
        super(Object.class);
        this._converter = (Converter<Object, ?>)converter;
        this._delegateType = null;
        this._delegateSerializer = null;
    }
    
    public <T> StdDelegatingSerializer(final Class<T> cls, final Converter<T, ?> converter) {
        super(cls, false);
        this._converter = (Converter<Object, ?>)converter;
        this._delegateType = null;
        this._delegateSerializer = null;
    }
    
    public StdDelegatingSerializer(final Converter<Object, ?> converter, final JavaType delegateType, final JsonSerializer<?> delegateSerializer) {
        super(delegateType);
        this._converter = converter;
        this._delegateType = delegateType;
        this._delegateSerializer = (JsonSerializer<Object>)delegateSerializer;
    }
    
    protected StdDelegatingSerializer withDelegate(final Converter<Object, ?> converter, final JavaType delegateType, final JsonSerializer<?> delegateSerializer) {
        if (this.getClass() != StdDelegatingSerializer.class) {
            throw new IllegalStateException("Sub-class " + this.getClass().getName() + " must override 'withDelegate'");
        }
        return new StdDelegatingSerializer(converter, delegateType, delegateSerializer);
    }
    
    @Override
    public void resolve(final SerializerProvider provider) throws JsonMappingException {
        if (this._delegateSerializer != null && this._delegateSerializer instanceof ResolvableSerializer) {
            ((ResolvableSerializer)this._delegateSerializer).resolve(provider);
        }
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        if (this._delegateSerializer == null) {
            JavaType delegateType = this._delegateType;
            if (delegateType == null) {
                delegateType = this._converter.getOutputType(provider.getTypeFactory());
            }
            return this.withDelegate(this._converter, delegateType, provider.findValueSerializer(delegateType, property));
        }
        if (!(this._delegateSerializer instanceof ContextualSerializer)) {
            return this;
        }
        final JsonSerializer<?> ser = provider.handleSecondaryContextualization(this._delegateSerializer, property);
        if (ser == this._delegateSerializer) {
            return this;
        }
        return this.withDelegate(this._converter, this._delegateType, ser);
    }
    
    protected Converter<Object, ?> getConverter() {
        return this._converter;
    }
    
    @Override
    public JsonSerializer<?> getDelegatee() {
        return this._delegateSerializer;
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        final Object delegateValue = this.convertValue(value);
        if (delegateValue == null) {
            provider.defaultSerializeNull(jgen);
            return;
        }
        this._delegateSerializer.serialize(delegateValue, jgen, provider);
    }
    
    @Override
    public void serializeWithType(final Object value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        final Object delegateValue = this.convertValue(value);
        this._delegateSerializer.serializeWithType(delegateValue, jgen, provider, typeSer);
    }
    
    @Override
    public boolean isEmpty(final Object value) {
        final Object delegateValue = this.convertValue(value);
        return this._delegateSerializer.isEmpty(delegateValue);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        if (this._delegateSerializer instanceof SchemaAware) {
            return ((SchemaAware)this._delegateSerializer).getSchema(provider, typeHint);
        }
        return super.getSchema(provider, typeHint);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint, final boolean isOptional) throws JsonMappingException {
        if (this._delegateSerializer instanceof SchemaAware) {
            return ((SchemaAware)this._delegateSerializer).getSchema(provider, typeHint, isOptional);
        }
        return super.getSchema(provider, typeHint);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        this._delegateSerializer.acceptJsonFormatVisitor(visitor, typeHint);
    }
    
    protected Object convertValue(final Object value) {
        return this._converter.convert(value);
    }
}
