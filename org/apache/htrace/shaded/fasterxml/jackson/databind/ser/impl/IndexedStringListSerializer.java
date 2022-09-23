// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
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
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;

@JacksonStdImpl
public final class IndexedStringListSerializer extends StaticListSerializerBase<List<String>> implements ContextualSerializer
{
    public static final IndexedStringListSerializer instance;
    protected final JsonSerializer<String> _serializer;
    
    protected IndexedStringListSerializer() {
        this((JsonSerializer<?>)null);
    }
    
    public IndexedStringListSerializer(final JsonSerializer<?> ser) {
        super(List.class);
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
        return new IndexedStringListSerializer(ser);
    }
    
    @Override
    public void serialize(final List<String> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        final int len = value.size();
        if (len == 1 && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) {
            this._serializeUnwrapped(value, jgen, provider);
            return;
        }
        jgen.writeStartArray();
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider, len);
        }
        else {
            this.serializeUsingCustom(value, jgen, provider, len);
        }
        jgen.writeEndArray();
    }
    
    private final void _serializeUnwrapped(final List<String> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider, 1);
        }
        else {
            this.serializeUsingCustom(value, jgen, provider, 1);
        }
    }
    
    @Override
    public void serializeWithType(final List<String> value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final int len = value.size();
        typeSer.writeTypePrefixForArray(value, jgen);
        if (this._serializer == null) {
            this.serializeContents(value, jgen, provider, len);
        }
        else {
            this.serializeUsingCustom(value, jgen, provider, len);
        }
        typeSer.writeTypeSuffixForArray(value, jgen);
    }
    
    private final void serializeContents(final List<String> value, final JsonGenerator jgen, final SerializerProvider provider, final int len) throws IOException {
        int i = 0;
        try {
            while (i < len) {
                final String str = value.get(i);
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else {
                    jgen.writeString(str);
                }
                ++i;
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, i);
        }
    }
    
    private final void serializeUsingCustom(final List<String> value, final JsonGenerator jgen, final SerializerProvider provider, final int len) throws IOException {
        int i = 0;
        try {
            final JsonSerializer<String> ser = this._serializer;
            for (i = 0; i < len; ++i) {
                final String str = value.get(i);
                if (str == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else {
                    ser.serialize(str, jgen, provider);
                }
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, i);
        }
    }
    
    static {
        instance = new IndexedStringListSerializer();
    }
}
