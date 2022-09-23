// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.FailingSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ResolvableSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Date;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.FilterProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.NullSerializer;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.ContextAttributes;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.RootNameLookup;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.SerializerCache;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.SerializerFactory;

public abstract class SerializerProvider extends DatabindContext
{
    @Deprecated
    protected static final JavaType TYPE_OBJECT;
    protected static final boolean CACHE_UNKNOWN_MAPPINGS = false;
    public static final JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER;
    protected static final JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER;
    protected final SerializationConfig _config;
    protected final Class<?> _serializationView;
    protected final SerializerFactory _serializerFactory;
    protected final SerializerCache _serializerCache;
    protected final RootNameLookup _rootNames;
    protected transient ContextAttributes _attributes;
    protected JsonSerializer<Object> _unknownTypeSerializer;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _nullValueSerializer;
    protected JsonSerializer<Object> _nullKeySerializer;
    protected final ReadOnlyClassToSerializerMap _knownSerializers;
    protected DateFormat _dateFormat;
    protected final boolean _stdNullValueSerializer;
    
    public SerializerProvider() {
        this._unknownTypeSerializer = SerializerProvider.DEFAULT_UNKNOWN_SERIALIZER;
        this._nullValueSerializer = NullSerializer.instance;
        this._nullKeySerializer = SerializerProvider.DEFAULT_NULL_KEY_SERIALIZER;
        this._config = null;
        this._serializerFactory = null;
        this._serializerCache = new SerializerCache();
        this._knownSerializers = null;
        this._rootNames = new RootNameLookup();
        this._serializationView = null;
        this._attributes = null;
        this._stdNullValueSerializer = true;
    }
    
    protected SerializerProvider(final SerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
        this._unknownTypeSerializer = SerializerProvider.DEFAULT_UNKNOWN_SERIALIZER;
        this._nullValueSerializer = NullSerializer.instance;
        this._nullKeySerializer = SerializerProvider.DEFAULT_NULL_KEY_SERIALIZER;
        if (config == null) {
            throw new NullPointerException();
        }
        this._serializerFactory = f;
        this._config = config;
        this._serializerCache = src._serializerCache;
        this._unknownTypeSerializer = src._unknownTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._nullValueSerializer = src._nullValueSerializer;
        this._stdNullValueSerializer = (this._nullValueSerializer == SerializerProvider.DEFAULT_NULL_KEY_SERIALIZER);
        this._nullKeySerializer = src._nullKeySerializer;
        this._rootNames = src._rootNames;
        this._knownSerializers = this._serializerCache.getReadOnlyLookupMap();
        this._serializationView = config.getActiveView();
        this._attributes = config.getAttributes();
    }
    
    public void setDefaultKeySerializer(final JsonSerializer<Object> ks) {
        if (ks == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._keySerializer = ks;
    }
    
    public void setNullValueSerializer(final JsonSerializer<Object> nvs) {
        if (nvs == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._nullValueSerializer = nvs;
    }
    
    public void setNullKeySerializer(final JsonSerializer<Object> nks) {
        if (nks == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._nullKeySerializer = nks;
    }
    
    @Override
    public final SerializationConfig getConfig() {
        return this._config;
    }
    
    @Override
    public final AnnotationIntrospector getAnnotationIntrospector() {
        return this._config.getAnnotationIntrospector();
    }
    
    @Override
    public final TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }
    
    @Override
    public final Class<?> getActiveView() {
        return this._serializationView;
    }
    
    @Deprecated
    public final Class<?> getSerializationView() {
        return this._serializationView;
    }
    
    @Override
    public Object getAttribute(final Object key) {
        return this._attributes.getAttribute(key);
    }
    
    @Override
    public SerializerProvider setAttribute(final Object key, final Object value) {
        this._attributes = this._attributes.withPerCallAttribute(key, value);
        return this;
    }
    
    public final boolean isEnabled(final SerializationFeature feature) {
        return this._config.isEnabled(feature);
    }
    
    public final boolean hasSerializationFeatures(final int featureMask) {
        return this._config.hasSerializationFeatures(featureMask);
    }
    
    public final FilterProvider getFilterProvider() {
        return this._config.getFilterProvider();
    }
    
    public Locale getLocale() {
        return this._config.getLocale();
    }
    
    public TimeZone getTimeZone() {
        return this._config.getTimeZone();
    }
    
    public abstract WritableObjectId findObjectId(final Object p0, final ObjectIdGenerator<?> p1);
    
    public JsonSerializer<Object> findValueSerializer(final Class<?> valueType, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
                if (ser == null) {
                    ser = this._createAndCacheUntypedSerializer(valueType);
                    if (ser == null) {
                        ser = this.getUnknownTypeSerializer(valueType);
                        return ser;
                    }
                }
            }
        }
        return (JsonSerializer<Object>)this.handleSecondaryContextualization(ser, property);
    }
    
    public JsonSerializer<Object> findValueSerializer(final JavaType valueType, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._createAndCacheUntypedSerializer(valueType);
                if (ser == null) {
                    ser = this.getUnknownTypeSerializer(valueType.getRawClass());
                    return ser;
                }
            }
        }
        return (JsonSerializer<Object>)this.handleSecondaryContextualization(ser, property);
    }
    
    public JsonSerializer<Object> findPrimaryPropertySerializer(final JavaType valueType, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._createAndCacheUntypedSerializer(valueType);
                if (ser == null) {
                    ser = this.getUnknownTypeSerializer(valueType.getRawClass());
                    return ser;
                }
            }
        }
        return (JsonSerializer<Object>)this.handlePrimaryContextualization(ser, property);
    }
    
    public JsonSerializer<Object> findPrimaryPropertySerializer(final Class<?> valueType, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
                if (ser == null) {
                    ser = this._createAndCacheUntypedSerializer(valueType);
                    if (ser == null) {
                        ser = this.getUnknownTypeSerializer(valueType);
                        return ser;
                    }
                }
            }
        }
        return (JsonSerializer<Object>)this.handlePrimaryContextualization(ser, property);
    }
    
    public JsonSerializer<Object> findTypedValueSerializer(final Class<?> valueType, final boolean cache, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this.findValueSerializer(valueType, property);
        TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, this._config.constructType(valueType));
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
            ser = new TypeWrappedSerializer(typeSer, ser);
        }
        if (cache) {
            this._serializerCache.addTypedSerializer(valueType, ser);
        }
        return ser;
    }
    
    public JsonSerializer<Object> findTypedValueSerializer(final JavaType valueType, final boolean cache, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this.findValueSerializer(valueType, property);
        TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, valueType);
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
            ser = new TypeWrappedSerializer(typeSer, ser);
        }
        if (cache) {
            this._serializerCache.addTypedSerializer(valueType, ser);
        }
        return ser;
    }
    
    public JsonSerializer<Object> findKeySerializer(final JavaType keyType, final BeanProperty property) throws JsonMappingException {
        final JsonSerializer<Object> ser = this._serializerFactory.createKeySerializer(this._config, keyType, this._keySerializer);
        return this._handleContextualResolvable(ser, property);
    }
    
    public JsonSerializer<Object> getDefaultNullKeySerializer() {
        return this._nullKeySerializer;
    }
    
    public JsonSerializer<Object> getDefaultNullValueSerializer() {
        return this._nullValueSerializer;
    }
    
    public JsonSerializer<Object> findNullKeySerializer(final JavaType serializationType, final BeanProperty property) throws JsonMappingException {
        return this._nullKeySerializer;
    }
    
    public JsonSerializer<Object> findNullValueSerializer(final BeanProperty property) throws JsonMappingException {
        return this._nullValueSerializer;
    }
    
    public JsonSerializer<Object> getUnknownTypeSerializer(final Class<?> unknownType) {
        return this._unknownTypeSerializer;
    }
    
    public abstract JsonSerializer<Object> serializerInstance(final Annotated p0, final Object p1) throws JsonMappingException;
    
    @Deprecated
    public JsonSerializer<?> handleContextualization(final JsonSerializer<?> ser, final BeanProperty property) throws JsonMappingException {
        return this.handleSecondaryContextualization(ser, property);
    }
    
    public JsonSerializer<?> handlePrimaryContextualization(JsonSerializer<?> ser, final BeanProperty property) throws JsonMappingException {
        if (ser != null && ser instanceof ContextualSerializer) {
            ser = ((ContextualSerializer)ser).createContextual(this, property);
        }
        return ser;
    }
    
    public JsonSerializer<?> handleSecondaryContextualization(JsonSerializer<?> ser, final BeanProperty property) throws JsonMappingException {
        if (ser != null && ser instanceof ContextualSerializer) {
            ser = ((ContextualSerializer)ser).createContextual(this, property);
        }
        return ser;
    }
    
    public final void defaultSerializeValue(final Object value, final JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (value == null) {
            if (this._stdNullValueSerializer) {
                jgen.writeNull();
            }
            else {
                this._nullValueSerializer.serialize(null, jgen, this);
            }
        }
        else {
            final Class<?> cls = value.getClass();
            this.findTypedValueSerializer(cls, true, null).serialize(value, jgen, this);
        }
    }
    
    public final void defaultSerializeField(final String fieldName, final Object value, final JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeFieldName(fieldName);
        if (value == null) {
            if (this._stdNullValueSerializer) {
                jgen.writeNull();
            }
            else {
                this._nullValueSerializer.serialize(null, jgen, this);
            }
        }
        else {
            final Class<?> cls = value.getClass();
            this.findTypedValueSerializer(cls, true, null).serialize(value, jgen, this);
        }
    }
    
    public final void defaultSerializeDateValue(final long timestamp, final JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            jgen.writeNumber(timestamp);
        }
        else {
            jgen.writeString(this._dateFormat().format(new Date(timestamp)));
        }
    }
    
    public final void defaultSerializeDateValue(final Date date, final JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            jgen.writeNumber(date.getTime());
        }
        else {
            jgen.writeString(this._dateFormat().format(date));
        }
    }
    
    public void defaultSerializeDateKey(final long timestamp, final JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            jgen.writeFieldName(String.valueOf(timestamp));
        }
        else {
            jgen.writeFieldName(this._dateFormat().format(new Date(timestamp)));
        }
    }
    
    public void defaultSerializeDateKey(final Date date, final JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            jgen.writeFieldName(String.valueOf(date.getTime()));
        }
        else {
            jgen.writeFieldName(this._dateFormat().format(date));
        }
    }
    
    public final void defaultSerializeNull(final JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this._stdNullValueSerializer) {
            jgen.writeNull();
        }
        else {
            this._nullValueSerializer.serialize(null, jgen, this);
        }
    }
    
    protected void _reportIncompatibleRootType(final Object value, final JavaType rootType) throws IOException, JsonProcessingException {
        if (rootType.isPrimitive()) {
            final Class<?> wrapperType = ClassUtil.wrapperType(rootType.getRawClass());
            if (wrapperType.isAssignableFrom(value.getClass())) {
                return;
            }
        }
        throw new JsonMappingException("Incompatible types: declared root type (" + rootType + ") vs " + value.getClass().getName());
    }
    
    protected JsonSerializer<Object> _findExplicitUntypedSerializer(final Class<?> runtimeType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(runtimeType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.untypedValueSerializer(runtimeType);
        if (ser != null) {
            return ser;
        }
        return this._createAndCacheUntypedSerializer(runtimeType);
    }
    
    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(final Class<?> type) throws JsonMappingException {
        JsonSerializer<Object> ser;
        try {
            ser = this._createUntypedSerializer(this._config.constructType(type));
        }
        catch (IllegalArgumentException iae) {
            throw new JsonMappingException(iae.getMessage(), null, iae);
        }
        if (ser != null) {
            this._serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
        }
        return ser;
    }
    
    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(final JavaType type) throws JsonMappingException {
        JsonSerializer<Object> ser;
        try {
            ser = this._createUntypedSerializer(type);
        }
        catch (IllegalArgumentException iae) {
            throw new JsonMappingException(iae.getMessage(), null, iae);
        }
        if (ser != null) {
            this._serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
        }
        return ser;
    }
    
    protected JsonSerializer<Object> _createUntypedSerializer(final JavaType type) throws JsonMappingException {
        return this._serializerFactory.createSerializer(this, type);
    }
    
    protected JsonSerializer<Object> _handleContextualResolvable(final JsonSerializer<?> ser, final BeanProperty property) throws JsonMappingException {
        if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer)ser).resolve(this);
        }
        return (JsonSerializer<Object>)this.handleSecondaryContextualization(ser, property);
    }
    
    protected JsonSerializer<Object> _handleResolvable(final JsonSerializer<?> ser) throws JsonMappingException {
        if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer)ser).resolve(this);
        }
        return (JsonSerializer<Object>)ser;
    }
    
    protected final DateFormat _dateFormat() {
        if (this._dateFormat != null) {
            return this._dateFormat;
        }
        DateFormat df = this._config.getDateFormat();
        df = (this._dateFormat = (DateFormat)df.clone());
        return df;
    }
    
    static {
        TYPE_OBJECT = TypeFactory.defaultInstance().uncheckedSimpleType(Object.class);
        DEFAULT_NULL_KEY_SERIALIZER = new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");
        DEFAULT_UNKNOWN_SERIALIZER = new UnknownSerializer();
    }
}
