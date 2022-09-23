// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContainerSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.ArraySerializerBase;

@JacksonStdImpl
public class StringArraySerializer extends ArraySerializerBase<String[]> implements ContextualSerializer
{
    private static final JavaType VALUE_TYPE;
    public static final StringArraySerializer instance;
    protected final JsonSerializer<Object> _elementSerializer;
    
    protected StringArraySerializer() {
        super(String[].class, null);
        this._elementSerializer = null;
    }
    
    public StringArraySerializer(final StringArraySerializer src, final BeanProperty prop, final JsonSerializer<?> ser) {
        super(src, prop);
        this._elementSerializer = (JsonSerializer<Object>)ser;
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return this;
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        if (property != null) {
            final AnnotatedMember m = property.getMember();
            if (m != null) {
                final Object serDef = provider.getAnnotationIntrospector().findContentSerializer(m);
                if (serDef != null) {
                    ser = provider.serializerInstance(m, serDef);
                }
            }
        }
        if (ser == null) {
            ser = this._elementSerializer;
        }
        ser = this.findConvertingContentSerializer(provider, property, ser);
        if (ser == null) {
            ser = provider.findValueSerializer(String.class, property);
        }
        else {
            ser = provider.handleSecondaryContextualization(ser, property);
        }
        if (this.isDefaultSerializer(ser)) {
            ser = null;
        }
        if (ser == this._elementSerializer) {
            return this;
        }
        return new StringArraySerializer(this, property, ser);
    }
    
    @Override
    public JavaType getContentType() {
        return StringArraySerializer.VALUE_TYPE;
    }
    
    @Override
    public JsonSerializer<?> getContentSerializer() {
        return this._elementSerializer;
    }
    
    @Override
    public boolean isEmpty(final String[] value) {
        return value == null || value.length == 0;
    }
    
    @Override
    public boolean hasSingleElement(final String[] value) {
        return value.length == 1;
    }
    
    public void serializeContents(final String[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        final int len = value.length;
        if (len == 0) {
            return;
        }
        if (this._elementSerializer != null) {
            this.serializeContentsSlow(value, jgen, provider, this._elementSerializer);
            return;
        }
        for (int i = 0; i < len; ++i) {
            final String str = value[i];
            if (str == null) {
                jgen.writeNull();
            }
            else {
                jgen.writeString(value[i]);
            }
        }
    }
    
    private void serializeContentsSlow(final String[] value, final JsonGenerator jgen, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
        for (int i = 0, len = value.length; i < len; ++i) {
            final String str = value[i];
            if (str == null) {
                provider.defaultSerializeNull(jgen);
            }
            else {
                ser.serialize(value[i], jgen, provider);
            }
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("array", true).set("items", this.createSchemaNode("string"));
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (visitor != null) {
            final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
            if (v2 != null) {
                v2.itemsFormat(JsonFormatTypes.STRING);
            }
        }
    }
    
    static {
        VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(String.class);
        instance = new StringArraySerializer();
    }
}
