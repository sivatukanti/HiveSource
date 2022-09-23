// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.SchemaAware;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.JsonSchema;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import java.util.Map;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;

public abstract class DefaultSerializerProvider extends SerializerProvider implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected transient Map<Object, WritableObjectId> _seenObjectIds;
    protected transient ArrayList<ObjectIdGenerator<?>> _objectIdGenerators;
    
    protected DefaultSerializerProvider() {
    }
    
    protected DefaultSerializerProvider(final SerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
        super(src, config, f);
    }
    
    public abstract DefaultSerializerProvider createInstance(final SerializationConfig p0, final SerializerFactory p1);
    
    public void serializeValue(final JsonGenerator jgen, final Object value) throws IOException {
        if (value == null) {
            this._serializeNull(jgen);
            return;
        }
        final Class<?> cls = value.getClass();
        final JsonSerializer<Object> ser = this.findTypedValueSerializer(cls, true, null);
        final String rootName = this._config.getRootName();
        boolean wrap;
        if (rootName == null) {
            wrap = this._config.isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
            if (wrap) {
                final PropertyName pname = this._rootNames.findRootName(value.getClass(), this._config);
                jgen.writeStartObject();
                jgen.writeFieldName(pname.simpleAsEncoded(this._config));
            }
        }
        else if (rootName.length() == 0) {
            wrap = false;
        }
        else {
            wrap = true;
            jgen.writeStartObject();
            jgen.writeFieldName(rootName);
        }
        try {
            ser.serialize(value, jgen, this);
            if (wrap) {
                jgen.writeEndObject();
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "[no message for " + e.getClass().getName() + "]";
            }
            throw new JsonMappingException(msg, e);
        }
    }
    
    public void serializeValue(final JsonGenerator jgen, final Object value, final JavaType rootType) throws IOException {
        if (value == null) {
            this._serializeNull(jgen);
            return;
        }
        if (!rootType.getRawClass().isAssignableFrom(value.getClass())) {
            this._reportIncompatibleRootType(value, rootType);
        }
        final JsonSerializer<Object> ser = this.findTypedValueSerializer(rootType, true, null);
        final String rootName = this._config.getRootName();
        boolean wrap;
        if (rootName == null) {
            wrap = this._config.isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
            if (wrap) {
                jgen.writeStartObject();
                final PropertyName pname = this._rootNames.findRootName(value.getClass(), this._config);
                jgen.writeFieldName(pname.simpleAsEncoded(this._config));
            }
        }
        else if (rootName.length() == 0) {
            wrap = false;
        }
        else {
            wrap = true;
            jgen.writeStartObject();
            jgen.writeFieldName(rootName);
        }
        try {
            ser.serialize(value, jgen, this);
            if (wrap) {
                jgen.writeEndObject();
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "[no message for " + e.getClass().getName() + "]";
            }
            throw new JsonMappingException(msg, e);
        }
    }
    
    public void serializeValue(final JsonGenerator jgen, final Object value, final JavaType rootType, JsonSerializer<Object> ser) throws IOException {
        if (value == null) {
            this._serializeNull(jgen);
            return;
        }
        if (rootType != null && !rootType.getRawClass().isAssignableFrom(value.getClass())) {
            this._reportIncompatibleRootType(value, rootType);
        }
        if (ser == null) {
            ser = this.findTypedValueSerializer(rootType, true, null);
        }
        final String rootName = this._config.getRootName();
        boolean wrap;
        if (rootName == null) {
            wrap = this._config.isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
            if (wrap) {
                jgen.writeStartObject();
                final PropertyName pname = (rootType == null) ? this._rootNames.findRootName(value.getClass(), this._config) : this._rootNames.findRootName(rootType, this._config);
                jgen.writeFieldName(pname.simpleAsEncoded(this._config));
            }
        }
        else if (rootName.length() == 0) {
            wrap = false;
        }
        else {
            wrap = true;
            jgen.writeStartObject();
            jgen.writeFieldName(rootName);
        }
        try {
            ser.serialize(value, jgen, this);
            if (wrap) {
                jgen.writeEndObject();
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "[no message for " + e.getClass().getName() + "]";
            }
            throw new JsonMappingException(msg, e);
        }
    }
    
    protected void _serializeNull(final JsonGenerator jgen) throws IOException {
        final JsonSerializer<Object> ser = this.getDefaultNullValueSerializer();
        try {
            ser.serialize(null, jgen, this);
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "[no message for " + e.getClass().getName() + "]";
            }
            throw new JsonMappingException(msg, e);
        }
    }
    
    public JsonSchema generateJsonSchema(final Class<?> type) throws JsonMappingException {
        if (type == null) {
            throw new IllegalArgumentException("A class must be provided");
        }
        final JsonSerializer<Object> ser = this.findValueSerializer(type, null);
        final JsonNode schemaNode = (ser instanceof SchemaAware) ? ((SchemaAware)ser).getSchema(this, null) : JsonSchema.getDefaultSchemaNode();
        if (!(schemaNode instanceof ObjectNode)) {
            throw new IllegalArgumentException("Class " + type.getName() + " would not be serialized as a JSON object and therefore has no schema");
        }
        return new JsonSchema((ObjectNode)schemaNode);
    }
    
    public void acceptJsonFormatVisitor(final JavaType javaType, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        if (javaType == null) {
            throw new IllegalArgumentException("A class must be provided");
        }
        visitor.setProvider(this);
        this.findValueSerializer(javaType, null).acceptJsonFormatVisitor(visitor, javaType);
    }
    
    @Deprecated
    public boolean hasSerializerFor(final Class<?> cls) {
        return this.hasSerializerFor(cls, null);
    }
    
    public boolean hasSerializerFor(final Class<?> cls, final AtomicReference<Throwable> cause) {
        try {
            final JsonSerializer<?> ser = this._findExplicitUntypedSerializer(cls);
            return ser != null;
        }
        catch (JsonMappingException e) {
            if (cause != null) {
                cause.set(e);
            }
        }
        catch (RuntimeException e2) {
            if (cause == null) {
                throw e2;
            }
            cause.set(e2);
        }
        return false;
    }
    
    public int cachedSerializersCount() {
        return this._serializerCache.size();
    }
    
    public void flushCachedSerializers() {
        this._serializerCache.flush();
    }
    
    @Override
    public WritableObjectId findObjectId(final Object forPojo, final ObjectIdGenerator<?> generatorType) {
        if (this._seenObjectIds == null) {
            this._seenObjectIds = this._createObjectIdMap();
        }
        else {
            final WritableObjectId oid = this._seenObjectIds.get(forPojo);
            if (oid != null) {
                return oid;
            }
        }
        ObjectIdGenerator<?> generator = null;
        if (this._objectIdGenerators == null) {
            this._objectIdGenerators = new ArrayList<ObjectIdGenerator<?>>(8);
        }
        else {
            for (int i = 0, len = this._objectIdGenerators.size(); i < len; ++i) {
                final ObjectIdGenerator<?> gen = this._objectIdGenerators.get(i);
                if (gen.canUseFor(generatorType)) {
                    generator = gen;
                    break;
                }
            }
        }
        if (generator == null) {
            generator = generatorType.newForSerialization(this);
            this._objectIdGenerators.add(generator);
        }
        final WritableObjectId oid2 = new WritableObjectId(generator);
        this._seenObjectIds.put(forPojo, oid2);
        return oid2;
    }
    
    protected Map<Object, WritableObjectId> _createObjectIdMap() {
        if (this.isEnabled(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID)) {
            return new HashMap<Object, WritableObjectId>();
        }
        return new IdentityHashMap<Object, WritableObjectId>();
    }
    
    @Override
    public JsonSerializer<Object> serializerInstance(final Annotated annotated, final Object serDef) throws JsonMappingException {
        if (serDef == null) {
            return null;
        }
        JsonSerializer<?> ser;
        if (serDef instanceof JsonSerializer) {
            ser = (JsonSerializer<?>)serDef;
        }
        else {
            if (!(serDef instanceof Class)) {
                throw new IllegalStateException("AnnotationIntrospector returned serializer definition of type " + serDef.getClass().getName() + "; expected type JsonSerializer or Class<JsonSerializer> instead");
            }
            final Class<?> serClass = (Class<?>)serDef;
            if (serClass == JsonSerializer.None.class || ClassUtil.isBogusClass(serClass)) {
                return null;
            }
            if (!JsonSerializer.class.isAssignableFrom(serClass)) {
                throw new IllegalStateException("AnnotationIntrospector returned Class " + serClass.getName() + "; expected Class<JsonSerializer>");
            }
            final HandlerInstantiator hi = this._config.getHandlerInstantiator();
            ser = ((hi == null) ? null : hi.serializerInstance(this._config, annotated, serClass));
            if (ser == null) {
                ser = ClassUtil.createInstance(serClass, this._config.canOverrideAccessModifiers());
            }
        }
        return this._handleResolvable(ser);
    }
    
    public static final class Impl extends DefaultSerializerProvider
    {
        private static final long serialVersionUID = 1L;
        
        public Impl() {
        }
        
        protected Impl(final SerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
            super(src, config, f);
        }
        
        @Override
        public Impl createInstance(final SerializationConfig config, final SerializerFactory jsf) {
            return new Impl(this, config, jsf);
        }
    }
}
