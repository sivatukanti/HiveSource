// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.JsonSchema;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.SchemaAware;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.lang.reflect.ParameterizedType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.EnumValues;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.util.EnumMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContainerSerializer;

@JacksonStdImpl
public class EnumMapSerializer extends ContainerSerializer<EnumMap<? extends Enum<?>, ?>> implements ContextualSerializer
{
    protected final boolean _staticTyping;
    protected final BeanProperty _property;
    protected final EnumValues _keyEnums;
    protected final JavaType _valueType;
    protected final JsonSerializer<Object> _valueSerializer;
    protected final TypeSerializer _valueTypeSerializer;
    
    public EnumMapSerializer(final JavaType valueType, final boolean staticTyping, final EnumValues keyEnums, final TypeSerializer vts, final JsonSerializer<Object> valueSerializer) {
        super(EnumMap.class, false);
        this._property = null;
        this._staticTyping = (staticTyping || (valueType != null && valueType.isFinal()));
        this._valueType = valueType;
        this._keyEnums = keyEnums;
        this._valueTypeSerializer = vts;
        this._valueSerializer = valueSerializer;
    }
    
    public EnumMapSerializer(final EnumMapSerializer src, final BeanProperty property, final JsonSerializer<?> ser) {
        super(src);
        this._property = property;
        this._staticTyping = src._staticTyping;
        this._valueType = src._valueType;
        this._keyEnums = src._keyEnums;
        this._valueTypeSerializer = src._valueTypeSerializer;
        this._valueSerializer = (JsonSerializer<Object>)ser;
    }
    
    public EnumMapSerializer _withValueTypeSerializer(final TypeSerializer vts) {
        return new EnumMapSerializer(this._valueType, this._staticTyping, this._keyEnums, vts, this._valueSerializer);
    }
    
    public EnumMapSerializer withValueSerializer(final BeanProperty prop, final JsonSerializer<?> ser) {
        if (this._property == prop && ser == this._valueSerializer) {
            return this;
        }
        return new EnumMapSerializer(this, prop, ser);
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
            ser = this._valueSerializer;
        }
        ser = this.findConvertingContentSerializer(provider, property, ser);
        if (ser == null) {
            if (this._staticTyping) {
                return this.withValueSerializer(property, provider.findValueSerializer(this._valueType, property));
            }
        }
        else {
            ser = provider.handleSecondaryContextualization(ser, property);
        }
        if (ser != this._valueSerializer) {
            return this.withValueSerializer(property, ser);
        }
        return this;
    }
    
    @Override
    public JavaType getContentType() {
        return this._valueType;
    }
    
    @Override
    public JsonSerializer<?> getContentSerializer() {
        return this._valueSerializer;
    }
    
    @Override
    public boolean isEmpty(final EnumMap<? extends Enum<?>, ?> value) {
        return value == null || value.isEmpty();
    }
    
    @Override
    public boolean hasSingleElement(final EnumMap<? extends Enum<?>, ?> value) {
        return value.size() == 1;
    }
    
    @Override
    public void serialize(final EnumMap<? extends Enum<?>, ?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        if (!value.isEmpty()) {
            this.serializeContents(value, jgen, provider);
        }
        jgen.writeEndObject();
    }
    
    @Override
    public void serializeWithType(final EnumMap<? extends Enum<?>, ?> value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForObject(value, jgen);
        if (!value.isEmpty()) {
            this.serializeContents(value, jgen, provider);
        }
        typeSer.writeTypeSuffixForObject(value, jgen);
    }
    
    protected void serializeContents(final EnumMap<? extends Enum<?>, ?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._valueSerializer != null) {
            this.serializeContentsUsing(value, jgen, provider, this._valueSerializer);
            return;
        }
        JsonSerializer<Object> prevSerializer = null;
        Class<?> prevClass = null;
        EnumValues keyEnums = this._keyEnums;
        final boolean skipNulls = !provider.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES);
        final TypeSerializer vts = this._valueTypeSerializer;
        for (final Map.Entry<? extends Enum<?>, ?> entry : value.entrySet()) {
            final Object valueElem = entry.getValue();
            if (skipNulls && valueElem == null) {
                continue;
            }
            final Enum<?> key = (Enum<?>)entry.getKey();
            if (keyEnums == null) {
                final StdSerializer<?> ser = (StdSerializer<?>)(StdSerializer)provider.findValueSerializer(key.getDeclaringClass(), this._property);
                keyEnums = ((EnumSerializer)ser).getEnumValues();
            }
            jgen.writeFieldName(keyEnums.serializedValueFor(key));
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
            }
            else {
                final Class<?> cc = valueElem.getClass();
                JsonSerializer<Object> currSerializer;
                if (cc == prevClass) {
                    currSerializer = prevSerializer;
                }
                else {
                    currSerializer = (prevSerializer = provider.findValueSerializer(cc, this._property));
                    prevClass = cc;
                }
                try {
                    if (vts == null) {
                        currSerializer.serialize(valueElem, jgen, provider);
                    }
                    else {
                        currSerializer.serializeWithType(valueElem, jgen, provider, vts);
                    }
                }
                catch (Exception e) {
                    this.wrapAndThrow(provider, e, value, ((Enum)entry.getKey()).name());
                }
            }
        }
    }
    
    protected void serializeContentsUsing(final EnumMap<? extends Enum<?>, ?> value, final JsonGenerator jgen, final SerializerProvider provider, final JsonSerializer<Object> valueSer) throws IOException, JsonGenerationException {
        EnumValues keyEnums = this._keyEnums;
        final boolean skipNulls = !provider.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES);
        final TypeSerializer vts = this._valueTypeSerializer;
        for (final Map.Entry<? extends Enum<?>, ?> entry : value.entrySet()) {
            final Object valueElem = entry.getValue();
            if (skipNulls && valueElem == null) {
                continue;
            }
            final Enum<?> key = (Enum<?>)entry.getKey();
            if (keyEnums == null) {
                final StdSerializer<?> ser = (StdSerializer<?>)(StdSerializer)provider.findValueSerializer(key.getDeclaringClass(), this._property);
                keyEnums = ((EnumSerializer)ser).getEnumValues();
            }
            jgen.writeFieldName(keyEnums.serializedValueFor(key));
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
            }
            else {
                try {
                    if (vts == null) {
                        valueSer.serialize(valueElem, jgen, provider);
                    }
                    else {
                        valueSer.serializeWithType(valueElem, jgen, provider, vts);
                    }
                }
                catch (Exception e) {
                    this.wrapAndThrow(provider, e, value, ((Enum)entry.getKey()).name());
                }
            }
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        final ObjectNode o = this.createSchemaNode("object", true);
        if (typeHint instanceof ParameterizedType) {
            final Type[] typeArgs = ((ParameterizedType)typeHint).getActualTypeArguments();
            if (typeArgs.length == 2) {
                final JavaType enumType = provider.constructType(typeArgs[0]);
                final JavaType valueType = provider.constructType(typeArgs[1]);
                final ObjectNode propsNode = JsonNodeFactory.instance.objectNode();
                final Class<Enum<?>> enumClass = (Class<Enum<?>>)enumType.getRawClass();
                for (final Enum<?> enumValue : enumClass.getEnumConstants()) {
                    final JsonSerializer<Object> ser = provider.findValueSerializer(valueType.getRawClass(), this._property);
                    final JsonNode schemaNode = (ser instanceof SchemaAware) ? ((SchemaAware)ser).getSchema(provider, null) : JsonSchema.getDefaultSchemaNode();
                    propsNode.put(provider.getConfig().getAnnotationIntrospector().findEnumValue(enumValue), schemaNode);
                }
                o.put("properties", propsNode);
            }
        }
        return o;
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (visitor == null) {
            return;
        }
        final JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
        if (objectVisitor == null) {
            return;
        }
        JavaType valueType = typeHint.containedType(1);
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null && valueType != null) {
            ser = visitor.getProvider().findValueSerializer(valueType, this._property);
        }
        if (valueType == null) {
            valueType = visitor.getProvider().constructType(Object.class);
        }
        EnumValues keyEnums = this._keyEnums;
        if (keyEnums == null) {
            final JavaType enumType = typeHint.containedType(0);
            if (enumType == null) {
                throw new IllegalStateException("Can not resolve Enum type of EnumMap: " + typeHint);
            }
            final JsonSerializer<?> enumSer = visitor.getProvider().findValueSerializer(enumType, this._property);
            if (!(enumSer instanceof EnumSerializer)) {
                throw new IllegalStateException("Can not resolve Enum type of EnumMap: " + typeHint);
            }
            keyEnums = ((EnumSerializer)enumSer).getEnumValues();
        }
        for (final Map.Entry<?, SerializableString> entry : keyEnums.internalMap().entrySet()) {
            final String name = entry.getValue().getValue();
            if (ser == null) {
                ser = visitor.getProvider().findValueSerializer(entry.getKey().getClass(), this._property);
            }
            objectVisitor.property(name, ser, valueType);
        }
    }
}
