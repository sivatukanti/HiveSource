// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;

@JacksonStdImpl
public class StringCollectionSerializer extends StaticListSerializerBase<Collection<String>> implements ContextualSerializer
{
    public static final StringCollectionSerializer instance;
    protected final JsonSerializer<String> _serializer;
    
    protected StringCollectionSerializer() {
        this((JsonSerializer<?>)null);
    }
    
    protected StringCollectionSerializer(final JsonSerializer<?> ser) {
        super(Collection.class);
        this._serializer = (JsonSerializer<String>)ser;
    }
    
    @Override
    protected JsonNode contentSchema() {
        return this.createSchemaNode("string", true);
    }
    
    @Override
    protected void acceptContentVisitor(final JsonArrayFormatVisitor visitor) throws JsonMappingException {
        visitor.itemsFormat(JsonFormatTypes.STRING);
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
            ser = this._serializer;
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
        if (ser == this._serializer) {
            return this;
        }
        return new StringCollectionSerializer(ser);
    }
    
    @Override
    public void serialize(final Collection<String> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (value.size() == 1 && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) {
            this._serializeUnwrapped(value, jgen, provider);
            return;
        }
        jgen.writeStartArray();
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider);
        }
        else {
            this.serializeUsingCustom(value, jgen, provider);
        }
        jgen.writeEndArray();
    }
    
    private final void _serializeUnwrapped(final Collection<String> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider);
        }
        else {
            this.serializeUsingCustom(value, jgen, provider);
        }
    }
    
    @Override
    public void serializeWithType(final Collection<String> value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForArray(value, jgen);
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider);
        }
        else {
            this.serializeUsingCustom(value, jgen, provider);
        }
        typeSer.writeTypeSuffixForArray(value, jgen);
    }
    
    private final void serializeContents(final Collection<String> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._serializer != null) {
            this.serializeUsingCustom(value, jgen, provider);
            return;
        }
        int i = 0;
        for (final String str : value) {
            try {
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else {
                    jgen.writeString(str);
                }
                ++i;
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, e, value, i);
            }
        }
    }
    
    private void serializeUsingCustom(final Collection<String> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        final JsonSerializer<String> ser = this._serializer;
        final int i = 0;
        for (final String str : value) {
            try {
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else {
                    ser.serialize(str, jgen, provider);
                }
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, e, value, i);
            }
        }
    }
    
    static {
        instance = new StringCollectionSerializer();
    }
}
