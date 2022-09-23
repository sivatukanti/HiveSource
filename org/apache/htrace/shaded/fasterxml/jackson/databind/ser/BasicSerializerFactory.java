// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.TokenBufferSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.TokenBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StdJdkSerializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.SqlTimeSerializer;
import java.sql.Time;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.SqlDateSerializer;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.NumberSerializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.MapperFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.EnumSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.IterableSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.IteratorSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.MapSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.EnumMapSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.EnumValues;
import java.util.EnumMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.EnumSetSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.IndexedListSerializer;
import java.util.RandomAccess;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer;
import java.util.EnumSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ArrayType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.NumberSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.nio.charset.Charset;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.TimeZoneSerializer;
import java.util.TimeZone;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.InetSocketAddressSerializer;
import java.net.InetSocketAddress;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.InetAddressSerializer;
import java.net.InetAddress;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.ByteBufferSerializer;
import java.nio.ByteBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.DateSerializer;
import java.util.Date;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import java.util.Calendar;
import java.lang.reflect.Method;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import java.lang.reflect.Member;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import java.util.HashMap;
import java.io.Serializable;

public abstract class BasicSerializerFactory extends SerializerFactory implements Serializable
{
    protected static final HashMap<String, JsonSerializer<?>> _concrete;
    protected static final HashMap<String, Class<? extends JsonSerializer<?>>> _concreteLazy;
    protected final SerializerFactoryConfig _factoryConfig;
    
    protected BasicSerializerFactory(final SerializerFactoryConfig config) {
        this._factoryConfig = ((config == null) ? new SerializerFactoryConfig() : config);
    }
    
    public SerializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }
    
    public abstract SerializerFactory withConfig(final SerializerFactoryConfig p0);
    
    @Override
    public final SerializerFactory withAdditionalSerializers(final Serializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalSerializers(additional));
    }
    
    @Override
    public final SerializerFactory withAdditionalKeySerializers(final Serializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalKeySerializers(additional));
    }
    
    @Override
    public final SerializerFactory withSerializerModifier(final BeanSerializerModifier modifier) {
        return this.withConfig(this._factoryConfig.withSerializerModifier(modifier));
    }
    
    @Override
    public abstract JsonSerializer<Object> createSerializer(final SerializerProvider p0, final JavaType p1) throws JsonMappingException;
    
    @Override
    public JsonSerializer<Object> createKeySerializer(final SerializationConfig config, final JavaType keyType, final JsonSerializer<Object> defaultImpl) {
        final BeanDescription beanDesc = config.introspectClassAnnotations(keyType.getRawClass());
        JsonSerializer<?> ser = null;
        if (this._factoryConfig.hasKeySerializers()) {
            for (final Serializers serializers : this._factoryConfig.keySerializers()) {
                ser = serializers.findSerializer(config, keyType, beanDesc);
                if (ser != null) {
                    break;
                }
            }
        }
        if (ser == null) {
            ser = defaultImpl;
            if (ser == null) {
                ser = StdKeySerializers.getStdKeySerializer(keyType);
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyKeySerializer(config, keyType, beanDesc, ser);
            }
        }
        return (JsonSerializer<Object>)ser;
    }
    
    @Override
    public TypeSerializer createTypeSerializer(final SerializationConfig config, final JavaType baseType) {
        final BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
        final AnnotatedClass ac = bean.getClassInfo();
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
        }
        else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(ac, config, ai);
        }
        if (b == null) {
            return null;
        }
        return b.buildTypeSerializer(config, baseType, subtypes);
    }
    
    protected abstract Iterable<Serializers> customSerializers();
    
    protected final JsonSerializer<?> findSerializerByLookup(final JavaType type, final SerializationConfig config, final BeanDescription beanDesc, final boolean staticTyping) {
        final Class<?> raw = type.getRawClass();
        final String clsName = raw.getName();
        final JsonSerializer<?> ser = BasicSerializerFactory._concrete.get(clsName);
        if (ser == null) {
            final Class<? extends JsonSerializer<?>> serClass = BasicSerializerFactory._concreteLazy.get(clsName);
            if (serClass != null) {
                try {
                    return (JsonSerializer<?>)serClass.newInstance();
                }
                catch (Exception e) {
                    throw new IllegalStateException("Failed to instantiate standard serializer (of type " + serClass.getName() + "): " + e.getMessage(), e);
                }
            }
        }
        return ser;
    }
    
    protected final JsonSerializer<?> findSerializerByAnnotations(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> raw = type.getRawClass();
        if (JsonSerializable.class.isAssignableFrom(raw)) {
            return SerializableSerializer.instance;
        }
        final AnnotatedMethod valueMethod = beanDesc.findJsonValueMethod();
        if (valueMethod != null) {
            final Method m = valueMethod.getAnnotated();
            if (prov.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(m);
            }
            final JsonSerializer<Object> ser = this.findSerializerFromAnnotation(prov, valueMethod);
            return new JsonValueSerializer(m, ser);
        }
        return null;
    }
    
    protected final JsonSerializer<?> findSerializerByPrimaryType(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        final Class<?> raw = type.getRawClass();
        final JsonSerializer<?> ser = this.findOptionalStdSerializer(prov, type, beanDesc, staticTyping);
        if (ser != null) {
            return ser;
        }
        if (Calendar.class.isAssignableFrom(raw)) {
            return CalendarSerializer.instance;
        }
        if (Date.class.isAssignableFrom(raw)) {
            return DateSerializer.instance;
        }
        if (ByteBuffer.class.isAssignableFrom(raw)) {
            return new ByteBufferSerializer();
        }
        if (InetAddress.class.isAssignableFrom(raw)) {
            return new InetAddressSerializer();
        }
        if (InetSocketAddress.class.isAssignableFrom(raw)) {
            return new InetSocketAddressSerializer();
        }
        if (TimeZone.class.isAssignableFrom(raw)) {
            return new TimeZoneSerializer();
        }
        if (Charset.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        if (Number.class.isAssignableFrom(raw)) {
            final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
            if (format != null) {
                switch (format.getShape()) {
                    case STRING: {
                        return ToStringSerializer.instance;
                    }
                    case OBJECT:
                    case ARRAY: {
                        return null;
                    }
                }
            }
            return NumberSerializer.instance;
        }
        if (Enum.class.isAssignableFrom(raw)) {
            return this.buildEnumSerializer(prov.getConfig(), type, beanDesc);
        }
        return null;
    }
    
    protected JsonSerializer<?> findOptionalStdSerializer(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findSerializer(prov.getConfig(), type, beanDesc);
    }
    
    protected final JsonSerializer<?> findSerializerByAddonType(final SerializationConfig config, final JavaType javaType, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        final Class<?> type = javaType.getRawClass();
        if (Iterator.class.isAssignableFrom(type)) {
            return this.buildIteratorSerializer(config, javaType, beanDesc, staticTyping);
        }
        if (Iterable.class.isAssignableFrom(type)) {
            return this.buildIterableSerializer(config, javaType, beanDesc, staticTyping);
        }
        if (CharSequence.class.isAssignableFrom(type)) {
            return ToStringSerializer.instance;
        }
        return null;
    }
    
    protected JsonSerializer<Object> findSerializerFromAnnotation(final SerializerProvider prov, final Annotated a) throws JsonMappingException {
        final Object serDef = prov.getAnnotationIntrospector().findSerializer(a);
        if (serDef == null) {
            return null;
        }
        final JsonSerializer<Object> ser = prov.serializerInstance(a, serDef);
        return (JsonSerializer<Object>)this.findConvertingSerializer(prov, a, ser);
    }
    
    protected JsonSerializer<?> findConvertingSerializer(final SerializerProvider prov, final Annotated a, final JsonSerializer<?> ser) throws JsonMappingException {
        final Converter<Object, Object> conv = this.findConverter(prov, a);
        if (conv == null) {
            return ser;
        }
        final JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
        return new StdDelegatingSerializer(conv, delegateType, ser);
    }
    
    protected Converter<Object, Object> findConverter(final SerializerProvider prov, final Annotated a) throws JsonMappingException {
        final Object convDef = prov.getAnnotationIntrospector().findSerializationConverter(a);
        if (convDef == null) {
            return null;
        }
        return prov.converterInstance(a, convDef);
    }
    
    protected JsonSerializer<?> buildContainerSerializer(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        final SerializationConfig config = prov.getConfig();
        if (!staticTyping && type.useStaticType() && (!type.isContainerType() || type.getContentType().getRawClass() != Object.class)) {
            staticTyping = true;
        }
        final JavaType elementType = type.getContentType();
        final TypeSerializer elementTypeSerializer = this.createTypeSerializer(config, elementType);
        if (elementTypeSerializer != null) {
            staticTyping = false;
        }
        final JsonSerializer<Object> elementValueSerializer = this._findContentSerializer(prov, beanDesc.getClassInfo());
        if (type.isMapLikeType()) {
            final MapLikeType mlt = (MapLikeType)type;
            final JsonSerializer<Object> keySerializer = this._findKeySerializer(prov, beanDesc.getClassInfo());
            if (mlt.isTrueMapType()) {
                return this.buildMapSerializer(config, (MapType)mlt, beanDesc, staticTyping, keySerializer, elementTypeSerializer, elementValueSerializer);
            }
            for (final Serializers serializers : this.customSerializers()) {
                final MapLikeType mlType = (MapLikeType)type;
                JsonSerializer<?> ser = serializers.findMapLikeSerializer(config, mlType, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
                if (ser != null) {
                    if (this._factoryConfig.hasSerializerModifiers()) {
                        for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                            ser = mod.modifyMapLikeSerializer(config, mlType, beanDesc, ser);
                        }
                    }
                    return ser;
                }
            }
            return null;
        }
        else if (type.isCollectionLikeType()) {
            final CollectionLikeType clt = (CollectionLikeType)type;
            if (clt.isTrueCollectionType()) {
                return this.buildCollectionSerializer(config, (CollectionType)clt, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
            }
            final CollectionLikeType clType = (CollectionLikeType)type;
            for (final Serializers serializers : this.customSerializers()) {
                JsonSerializer<?> ser2 = serializers.findCollectionLikeSerializer(config, clType, beanDesc, elementTypeSerializer, elementValueSerializer);
                if (ser2 != null) {
                    if (this._factoryConfig.hasSerializerModifiers()) {
                        for (final BeanSerializerModifier mod2 : this._factoryConfig.serializerModifiers()) {
                            ser2 = mod2.modifyCollectionLikeSerializer(config, clType, beanDesc, ser2);
                        }
                    }
                    return ser2;
                }
            }
            return null;
        }
        else {
            if (type.isArrayType()) {
                return this.buildArraySerializer(config, (ArrayType)type, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
            }
            return null;
        }
    }
    
    protected JsonSerializer<?> buildCollectionSerializer(final SerializationConfig config, final CollectionType type, final BeanDescription beanDesc, final boolean staticTyping, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        for (final Serializers serializers : this.customSerializers()) {
            ser = serializers.findCollectionSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
            if (format != null && format.getShape() == JsonFormat.Shape.OBJECT) {
                return null;
            }
            final Class<?> raw = type.getRawClass();
            if (EnumSet.class.isAssignableFrom(raw)) {
                JavaType enumType = type.getContentType();
                if (!enumType.isEnumType()) {
                    enumType = null;
                }
                ser = this.buildEnumSetSerializer(enumType);
            }
            else {
                final Class<?> elementRaw = type.getContentType().getRawClass();
                if (this.isIndexedList(raw)) {
                    if (elementRaw == String.class) {
                        if (elementValueSerializer == null || ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
                            ser = IndexedStringListSerializer.instance;
                        }
                    }
                    else {
                        ser = this.buildIndexedListSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
                    }
                }
                else if (elementRaw == String.class && (elementValueSerializer == null || ClassUtil.isJacksonStdImpl(elementValueSerializer))) {
                    ser = StringCollectionSerializer.instance;
                }
                if (ser == null) {
                    ser = this.buildCollectionSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
                }
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyCollectionSerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }
    
    protected boolean isIndexedList(final Class<?> cls) {
        return RandomAccess.class.isAssignableFrom(cls);
    }
    
    public ContainerSerializer<?> buildIndexedListSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> valueSerializer) {
        return new IndexedListSerializer(elemType, staticTyping, vts, null, valueSerializer);
    }
    
    public ContainerSerializer<?> buildCollectionSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> valueSerializer) {
        return new CollectionSerializer(elemType, staticTyping, vts, null, valueSerializer);
    }
    
    public JsonSerializer<?> buildEnumSetSerializer(final JavaType enumType) {
        return new EnumSetSerializer(enumType, null);
    }
    
    protected JsonSerializer<?> buildMapSerializer(final SerializationConfig config, final MapType type, final BeanDescription beanDesc, final boolean staticTyping, final JsonSerializer<Object> keySerializer, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        for (final Serializers serializers : this.customSerializers()) {
            ser = serializers.findMapSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            if (EnumMap.class.isAssignableFrom(type.getRawClass())) {
                final JavaType keyType = type.getKeyType();
                EnumValues enums = null;
                if (keyType.isEnumType()) {
                    final Class<Enum<?>> enumClass = (Class<Enum<?>>)keyType.getRawClass();
                    enums = EnumValues.construct(config, enumClass);
                }
                ser = new EnumMapSerializer(type.getContentType(), staticTyping, enums, elementTypeSerializer, elementValueSerializer);
            }
            else {
                final Object filterId = this.findFilterId(config, beanDesc);
                ser = MapSerializer.construct(config.getAnnotationIntrospector().findPropertiesToIgnore(beanDesc.getClassInfo()), type, staticTyping, elementTypeSerializer, keySerializer, elementValueSerializer, filterId);
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyMapSerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }
    
    protected JsonSerializer<?> buildArraySerializer(final SerializationConfig config, final ArrayType type, final BeanDescription beanDesc, final boolean staticTyping, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        for (final Serializers serializers : this.customSerializers()) {
            ser = serializers.findArraySerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            final Class<?> raw = type.getRawClass();
            if (elementValueSerializer == null || ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
                if (String[].class == raw) {
                    ser = StringArraySerializer.instance;
                }
                else {
                    ser = StdArraySerializers.findStandardImpl(raw);
                }
            }
            if (ser == null) {
                ser = new ObjectArraySerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyArraySerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }
    
    protected JsonSerializer<?> buildIteratorSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        JavaType valueType = type.containedType(0);
        if (valueType == null) {
            valueType = TypeFactory.unknownType();
        }
        final TypeSerializer vts = this.createTypeSerializer(config, valueType);
        return new IteratorSerializer(valueType, staticTyping, vts, null);
    }
    
    protected JsonSerializer<?> buildIterableSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        JavaType valueType = type.containedType(0);
        if (valueType == null) {
            valueType = TypeFactory.unknownType();
        }
        final TypeSerializer vts = this.createTypeSerializer(config, valueType);
        return new IterableSerializer(valueType, staticTyping, vts, null);
    }
    
    protected JsonSerializer<?> buildEnumSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
        if (format != null && format.getShape() == JsonFormat.Shape.OBJECT) {
            ((BasicBeanDescription)beanDesc).removeProperty("declaringClass");
            return null;
        }
        final Class<Enum<?>> enumClass = (Class<Enum<?>>)type.getRawClass();
        JsonSerializer<?> ser = EnumSerializer.construct(enumClass, config, beanDesc, format);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyEnumSerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }
    
    protected <T extends JavaType> T modifyTypeByAnnotation(final SerializationConfig config, final Annotated a, T type) {
        final Class<?> superclass = config.getAnnotationIntrospector().findSerializationType(a);
        if (superclass != null) {
            try {
                type = (T)type.widenBy(superclass);
            }
            catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Failed to widen type " + type + " with concrete-type annotation (value " + superclass.getName() + "), method '" + a.getName() + "': " + iae.getMessage());
            }
        }
        return (T)modifySecondaryTypesByAnnotation(config, a, (JavaType)type);
    }
    
    protected static <T extends JavaType> T modifySecondaryTypesByAnnotation(final SerializationConfig config, final Annotated a, T type) {
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        if (type.isContainerType()) {
            final Class<?> keyClass = intr.findSerializationKeyType(a, type.getKeyType());
            if (keyClass != null) {
                if (!(type instanceof MapType)) {
                    throw new IllegalArgumentException("Illegal key-type annotation: type " + type + " is not a Map type");
                }
                try {
                    type = (T)((MapType)type).widenKey(keyClass);
                }
                catch (IllegalArgumentException iae) {
                    throw new IllegalArgumentException("Failed to narrow key type " + type + " with key-type annotation (" + keyClass.getName() + "): " + iae.getMessage());
                }
            }
            final Class<?> cc = intr.findSerializationContentType(a, type.getContentType());
            if (cc != null) {
                try {
                    type = (T)type.widenContentsBy(cc);
                }
                catch (IllegalArgumentException iae2) {
                    throw new IllegalArgumentException("Failed to narrow content type " + type + " with content-type annotation (" + cc.getName() + "): " + iae2.getMessage());
                }
            }
        }
        return type;
    }
    
    protected JsonSerializer<Object> _findKeySerializer(final SerializerProvider prov, final Annotated a) throws JsonMappingException {
        final AnnotationIntrospector intr = prov.getAnnotationIntrospector();
        final Object serDef = intr.findKeySerializer(a);
        if (serDef != null) {
            return prov.serializerInstance(a, serDef);
        }
        return null;
    }
    
    protected JsonSerializer<Object> _findContentSerializer(final SerializerProvider prov, final Annotated a) throws JsonMappingException {
        final AnnotationIntrospector intr = prov.getAnnotationIntrospector();
        final Object serDef = intr.findContentSerializer(a);
        if (serDef != null) {
            return prov.serializerInstance(a, serDef);
        }
        return null;
    }
    
    protected Object findFilterId(final SerializationConfig config, final BeanDescription beanDesc) {
        return config.getAnnotationIntrospector().findFilterId((Annotated)beanDesc.getClassInfo());
    }
    
    protected boolean usesStaticTyping(final SerializationConfig config, final BeanDescription beanDesc, final TypeSerializer typeSer) {
        if (typeSer != null) {
            return false;
        }
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final JsonSerialize.Typing t = intr.findSerializationTyping(beanDesc.getClassInfo());
        if (t != null && t != JsonSerialize.Typing.DEFAULT_TYPING) {
            return t == JsonSerialize.Typing.STATIC;
        }
        return config.isEnabled(MapperFeature.USE_STATIC_TYPING);
    }
    
    protected Class<?> _verifyAsClass(final Object src, final String methodName, final Class<?> noneClass) {
        if (src == null) {
            return null;
        }
        if (!(src instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
        }
        final Class<?> cls = (Class<?>)src;
        if (cls == noneClass || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }
    
    static {
        _concrete = new HashMap<String, JsonSerializer<?>>();
        _concreteLazy = new HashMap<String, Class<? extends JsonSerializer<?>>>();
        BasicSerializerFactory._concrete.put(String.class.getName(), new StringSerializer());
        final ToStringSerializer sls = ToStringSerializer.instance;
        BasicSerializerFactory._concrete.put(StringBuffer.class.getName(), sls);
        BasicSerializerFactory._concrete.put(StringBuilder.class.getName(), sls);
        BasicSerializerFactory._concrete.put(Character.class.getName(), sls);
        BasicSerializerFactory._concrete.put(Character.TYPE.getName(), sls);
        NumberSerializers.addAll(BasicSerializerFactory._concrete);
        BasicSerializerFactory._concrete.put(Boolean.TYPE.getName(), new BooleanSerializer(true));
        BasicSerializerFactory._concrete.put(Boolean.class.getName(), new BooleanSerializer(false));
        final JsonSerializer<?> ns = NumberSerializer.instance;
        BasicSerializerFactory._concrete.put(BigInteger.class.getName(), ns);
        BasicSerializerFactory._concrete.put(BigDecimal.class.getName(), ns);
        BasicSerializerFactory._concrete.put(Calendar.class.getName(), CalendarSerializer.instance);
        final DateSerializer dateSer = DateSerializer.instance;
        BasicSerializerFactory._concrete.put(Date.class.getName(), dateSer);
        BasicSerializerFactory._concrete.put(Timestamp.class.getName(), dateSer);
        BasicSerializerFactory._concreteLazy.put(java.sql.Date.class.getName(), SqlDateSerializer.class);
        BasicSerializerFactory._concreteLazy.put(Time.class.getName(), SqlTimeSerializer.class);
        for (final Map.Entry<Class<?>, Object> en : StdJdkSerializers.all()) {
            final Object value = en.getValue();
            if (value instanceof JsonSerializer) {
                BasicSerializerFactory._concrete.put(en.getKey().getName(), (JsonSerializer<?>)value);
            }
            else {
                if (!(value instanceof Class)) {
                    throw new IllegalStateException("Internal error: unrecognized value of type " + en.getClass().getName());
                }
                final Class<? extends JsonSerializer<?>> cls = (Class<? extends JsonSerializer<?>>)value;
                BasicSerializerFactory._concreteLazy.put(en.getKey().getName(), cls);
            }
        }
        BasicSerializerFactory._concreteLazy.put(TokenBuffer.class.getName(), TokenBufferSerializer.class);
    }
}
