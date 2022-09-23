// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.util.TreeMap;
import java.util.SortedMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.PropertyWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.PropertyFilter;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import java.util.HashSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContainerSerializer;

@JacksonStdImpl
public class MapSerializer extends ContainerSerializer<Map<?, ?>> implements ContextualSerializer
{
    protected static final JavaType UNSPECIFIED_TYPE;
    protected final BeanProperty _property;
    protected final HashSet<String> _ignoredEntries;
    protected final boolean _valueTypeIsStatic;
    protected final JavaType _keyType;
    protected final JavaType _valueType;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _valueSerializer;
    protected final TypeSerializer _valueTypeSerializer;
    protected PropertySerializerMap _dynamicValueSerializers;
    protected final Object _filterId;
    protected final boolean _sortKeys;
    
    protected MapSerializer(final HashSet<String> ignoredEntries, final JavaType keyType, final JavaType valueType, final boolean valueTypeIsStatic, final TypeSerializer vts, final JsonSerializer<?> keySerializer, final JsonSerializer<?> valueSerializer) {
        super(Map.class, false);
        this._ignoredEntries = ignoredEntries;
        this._keyType = keyType;
        this._valueType = valueType;
        this._valueTypeIsStatic = valueTypeIsStatic;
        this._valueTypeSerializer = vts;
        this._keySerializer = (JsonSerializer<Object>)keySerializer;
        this._valueSerializer = (JsonSerializer<Object>)valueSerializer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyMap();
        this._property = null;
        this._filterId = null;
        this._sortKeys = false;
    }
    
    protected MapSerializer(final MapSerializer src, final BeanProperty property, final JsonSerializer<?> keySerializer, final JsonSerializer<?> valueSerializer, final HashSet<String> ignored) {
        super(Map.class, false);
        this._ignoredEntries = ignored;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = src._valueTypeSerializer;
        this._keySerializer = (JsonSerializer<Object>)keySerializer;
        this._valueSerializer = (JsonSerializer<Object>)valueSerializer;
        this._dynamicValueSerializers = src._dynamicValueSerializers;
        this._property = property;
        this._filterId = src._filterId;
        this._sortKeys = src._sortKeys;
    }
    
    protected MapSerializer(final MapSerializer src, final TypeSerializer vts) {
        super(Map.class, false);
        this._ignoredEntries = src._ignoredEntries;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = vts;
        this._keySerializer = src._keySerializer;
        this._valueSerializer = src._valueSerializer;
        this._dynamicValueSerializers = src._dynamicValueSerializers;
        this._property = src._property;
        this._filterId = src._filterId;
        this._sortKeys = src._sortKeys;
    }
    
    protected MapSerializer(final MapSerializer src, final Object filterId, final boolean sortKeys) {
        super(Map.class, false);
        this._ignoredEntries = src._ignoredEntries;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = src._valueTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._valueSerializer = src._valueSerializer;
        this._dynamicValueSerializers = src._dynamicValueSerializers;
        this._property = src._property;
        this._filterId = filterId;
        this._sortKeys = sortKeys;
    }
    
    public MapSerializer _withValueTypeSerializer(final TypeSerializer vts) {
        return new MapSerializer(this, vts);
    }
    
    @Deprecated
    public MapSerializer withResolved(final BeanProperty property, final JsonSerializer<?> keySerializer, final JsonSerializer<?> valueSerializer, final HashSet<String> ignored) {
        return this.withResolved(property, keySerializer, valueSerializer, ignored, this._sortKeys);
    }
    
    public MapSerializer withResolved(final BeanProperty property, final JsonSerializer<?> keySerializer, final JsonSerializer<?> valueSerializer, final HashSet<String> ignored, final boolean sortKeys) {
        MapSerializer ser = new MapSerializer(this, property, keySerializer, valueSerializer, ignored);
        if (sortKeys != ser._sortKeys) {
            ser = new MapSerializer(ser, this._filterId, sortKeys);
        }
        return ser;
    }
    
    public MapSerializer withFilterId(final Object filterId) {
        return (this._filterId == filterId) ? this : new MapSerializer(this, filterId, this._sortKeys);
    }
    
    @Deprecated
    public static MapSerializer construct(final String[] ignoredList, final JavaType mapType, final boolean staticValueType, final TypeSerializer vts, final JsonSerializer<Object> keySerializer, final JsonSerializer<Object> valueSerializer) {
        return construct(ignoredList, mapType, staticValueType, vts, keySerializer, valueSerializer, null);
    }
    
    public static MapSerializer construct(final String[] ignoredList, final JavaType mapType, boolean staticValueType, final TypeSerializer vts, final JsonSerializer<Object> keySerializer, final JsonSerializer<Object> valueSerializer, final Object filterId) {
        final HashSet<String> ignoredEntries = toSet(ignoredList);
        JavaType keyType;
        JavaType valueType;
        if (mapType == null) {
            valueType = (keyType = MapSerializer.UNSPECIFIED_TYPE);
        }
        else {
            keyType = mapType.getKeyType();
            valueType = mapType.getContentType();
        }
        if (!staticValueType) {
            staticValueType = (valueType != null && valueType.isFinal());
        }
        else if (valueType.getRawClass() == Object.class) {
            staticValueType = false;
        }
        MapSerializer ser = new MapSerializer(ignoredEntries, keyType, valueType, staticValueType, vts, keySerializer, valueSerializer);
        if (filterId != null) {
            ser = ser.withFilterId(filterId);
        }
        return ser;
    }
    
    private static HashSet<String> toSet(final String[] ignoredEntries) {
        if (ignoredEntries == null || ignoredEntries.length == 0) {
            return null;
        }
        final HashSet<String> result = new HashSet<String>(ignoredEntries.length);
        for (final String prop : ignoredEntries) {
            result.add(prop);
        }
        return result;
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        JsonSerializer<?> keySer = null;
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
        if (propertyAcc != null && intr != null) {
            Object serDef = intr.findKeySerializer(propertyAcc);
            if (serDef != null) {
                keySer = provider.serializerInstance(propertyAcc, serDef);
            }
            serDef = intr.findContentSerializer(propertyAcc);
            if (serDef != null) {
                ser = provider.serializerInstance(propertyAcc, serDef);
            }
        }
        if (ser == null) {
            ser = this._valueSerializer;
        }
        ser = this.findConvertingContentSerializer(provider, property, ser);
        if (ser == null) {
            if ((this._valueTypeIsStatic && this._valueType.getRawClass() != Object.class) || this.hasContentTypeAnnotation(provider, property)) {
                ser = provider.findValueSerializer(this._valueType, property);
            }
        }
        else {
            ser = provider.handleSecondaryContextualization(ser, property);
        }
        if (keySer == null) {
            keySer = this._keySerializer;
        }
        if (keySer == null) {
            keySer = provider.findKeySerializer(this._keyType, property);
        }
        else {
            keySer = provider.handleSecondaryContextualization(keySer, property);
        }
        HashSet<String> ignored = this._ignoredEntries;
        boolean sortKeys = false;
        if (intr != null && propertyAcc != null) {
            final String[] moreToIgnore = intr.findPropertiesToIgnore(propertyAcc);
            if (moreToIgnore != null) {
                ignored = ((ignored == null) ? new HashSet<String>() : new HashSet<String>(ignored));
                for (final String str : moreToIgnore) {
                    ignored.add(str);
                }
            }
            final Boolean b = intr.findSerializationSortAlphabetically(propertyAcc);
            sortKeys = (b != null && b);
        }
        MapSerializer mser = this.withResolved(property, keySer, ser, ignored, sortKeys);
        if (property != null) {
            final Object filterId = intr.findFilterId(property.getMember());
            if (filterId != null) {
                mser = mser.withFilterId(filterId);
            }
        }
        return mser;
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
    public boolean isEmpty(final Map<?, ?> value) {
        return value == null || value.isEmpty();
    }
    
    @Override
    public boolean hasSingleElement(final Map<?, ?> value) {
        return value.size() == 1;
    }
    
    public JsonSerializer<?> getKeySerializer() {
        return this._keySerializer;
    }
    
    @Override
    public void serialize(Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        if (!value.isEmpty()) {
            if (this._filterId != null) {
                this.serializeFilteredFields(value, jgen, provider, this.findPropertyFilter(provider, this._filterId, value));
                jgen.writeEndObject();
                return;
            }
            if (this._sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = this._orderEntries(value);
            }
            if (this._valueSerializer != null) {
                this.serializeFieldsUsing(value, jgen, provider, this._valueSerializer);
            }
            else {
                this.serializeFields(value, jgen, provider);
            }
        }
        jgen.writeEndObject();
    }
    
    @Override
    public void serializeWithType(Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForObject(value, jgen);
        if (!value.isEmpty()) {
            if (this._sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = this._orderEntries(value);
            }
            if (this._valueSerializer != null) {
                this.serializeFieldsUsing(value, jgen, provider, this._valueSerializer);
            }
            else {
                this.serializeFields(value, jgen, provider);
            }
        }
        typeSer.writeTypeSuffixForObject(value, jgen);
    }
    
    public void serializeFields(final Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._valueTypeSerializer != null) {
            this.serializeTypedFields(value, jgen, provider);
            return;
        }
        final JsonSerializer<Object> keySerializer = this._keySerializer;
        final HashSet<String> ignored = this._ignoredEntries;
        final boolean skipNulls = !provider.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES);
        PropertySerializerMap serializers = this._dynamicValueSerializers;
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object valueElem = entry.getValue();
            final Object keyElem = entry.getKey();
            if (keyElem == null) {
                provider.findNullKeySerializer(this._keyType, this._property).serialize(null, jgen, provider);
            }
            else {
                if (skipNulls && valueElem == null) {
                    continue;
                }
                if (ignored != null && ignored.contains(keyElem)) {
                    continue;
                }
                keySerializer.serialize(keyElem, jgen, provider);
            }
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
            }
            else {
                final Class<?> cc = valueElem.getClass();
                JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                if (serializer == null) {
                    if (this._valueType.hasGenericTypes()) {
                        serializer = this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._valueType, cc), provider);
                    }
                    else {
                        serializer = this._findAndAddDynamic(serializers, cc, provider);
                    }
                    serializers = this._dynamicValueSerializers;
                }
                try {
                    serializer.serialize(valueElem, jgen, provider);
                }
                catch (Exception e) {
                    final String keyDesc = "" + keyElem;
                    this.wrapAndThrow(provider, e, value, keyDesc);
                }
            }
        }
    }
    
    protected void serializeFieldsUsing(final Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException, JsonGenerationException {
        final JsonSerializer<Object> keySerializer = this._keySerializer;
        final HashSet<String> ignored = this._ignoredEntries;
        final TypeSerializer typeSer = this._valueTypeSerializer;
        final boolean skipNulls = !provider.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES);
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object valueElem = entry.getValue();
            final Object keyElem = entry.getKey();
            if (keyElem == null) {
                provider.findNullKeySerializer(this._keyType, this._property).serialize(null, jgen, provider);
            }
            else {
                if (skipNulls && valueElem == null) {
                    continue;
                }
                if (ignored != null && ignored.contains(keyElem)) {
                    continue;
                }
                keySerializer.serialize(keyElem, jgen, provider);
            }
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
            }
            else {
                try {
                    if (typeSer == null) {
                        ser.serialize(valueElem, jgen, provider);
                    }
                    else {
                        ser.serializeWithType(valueElem, jgen, provider, typeSer);
                    }
                }
                catch (Exception e) {
                    final String keyDesc = "" + keyElem;
                    this.wrapAndThrow(provider, e, value, keyDesc);
                }
            }
        }
    }
    
    public void serializeFilteredFields(final Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider, final PropertyFilter filter) throws IOException, JsonGenerationException {
        final HashSet<String> ignored = this._ignoredEntries;
        final boolean skipNulls = !provider.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES);
        PropertySerializerMap serializers = this._dynamicValueSerializers;
        final MapProperty prop = new MapProperty(this._valueTypeSerializer);
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object keyElem = entry.getKey();
            final Object valueElem = entry.getValue();
            JsonSerializer<Object> keySer;
            if (keyElem == null) {
                keySer = provider.findNullKeySerializer(this._keyType, this._property);
            }
            else {
                if (skipNulls && valueElem == null) {
                    continue;
                }
                if (ignored != null && ignored.contains(keyElem)) {
                    continue;
                }
                keySer = this._keySerializer;
            }
            JsonSerializer<Object> valueSer;
            if (valueElem == null) {
                valueSer = provider.getDefaultNullValueSerializer();
            }
            else {
                final Class<?> cc = valueElem.getClass();
                valueSer = serializers.serializerFor(cc);
                if (valueSer == null) {
                    if (this._valueType.hasGenericTypes()) {
                        valueSer = this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._valueType, cc), provider);
                    }
                    else {
                        valueSer = this._findAndAddDynamic(serializers, cc, provider);
                    }
                    serializers = this._dynamicValueSerializers;
                }
            }
            prop.reset(keyElem, valueElem, keySer, valueSer);
            try {
                filter.serializeAsField(value, jgen, provider, prop);
            }
            catch (Exception e) {
                final String keyDesc = "" + keyElem;
                this.wrapAndThrow(provider, e, value, keyDesc);
            }
        }
    }
    
    protected void serializeTypedFields(final Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        final JsonSerializer<Object> keySerializer = this._keySerializer;
        JsonSerializer<Object> prevValueSerializer = null;
        Class<?> prevValueClass = null;
        final HashSet<String> ignored = this._ignoredEntries;
        final boolean skipNulls = !provider.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES);
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object valueElem = entry.getValue();
            final Object keyElem = entry.getKey();
            if (keyElem == null) {
                provider.findNullKeySerializer(this._keyType, this._property).serialize(null, jgen, provider);
            }
            else {
                if (skipNulls && valueElem == null) {
                    continue;
                }
                if (ignored != null && ignored.contains(keyElem)) {
                    continue;
                }
                keySerializer.serialize(keyElem, jgen, provider);
            }
            if (valueElem == null) {
                provider.defaultSerializeNull(jgen);
            }
            else {
                final Class<?> cc = valueElem.getClass();
                JsonSerializer<Object> currSerializer;
                if (cc == prevValueClass) {
                    currSerializer = prevValueSerializer;
                }
                else {
                    if (this._valueType.hasGenericTypes()) {
                        currSerializer = provider.findValueSerializer(provider.constructSpecializedType(this._valueType, cc), this._property);
                    }
                    else {
                        currSerializer = provider.findValueSerializer(cc, this._property);
                    }
                    prevValueSerializer = currSerializer;
                    prevValueClass = cc;
                }
                try {
                    currSerializer.serializeWithType(valueElem, jgen, provider, this._valueTypeSerializer);
                }
                catch (Exception e) {
                    final String keyDesc = "" + keyElem;
                    this.wrapAndThrow(provider, e, value, keyDesc);
                }
            }
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        final ObjectNode o = this.createSchemaNode("object", true);
        return o;
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JsonMapFormatVisitor v2 = (visitor == null) ? null : visitor.expectMapFormat(typeHint);
        if (v2 != null) {
            v2.keyFormat(this._keySerializer, this._keyType);
            JsonSerializer<?> valueSer = this._valueSerializer;
            if (valueSer == null) {
                valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, this._valueType, visitor.getProvider());
            }
            v2.valueFormat(valueSer, this._valueType);
        }
    }
    
    protected final JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final Class<?> type, final SerializerProvider provider) throws JsonMappingException {
        final PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
    
    protected final JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final JavaType type, final SerializerProvider provider) throws JsonMappingException {
        final PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
    
    protected Map<?, ?> _orderEntries(final Map<?, ?> input) {
        if (input instanceof SortedMap) {
            return input;
        }
        return new TreeMap<Object, Object>(input);
    }
    
    static {
        UNSPECIFIED_TYPE = TypeFactory.unknownType();
    }
}
