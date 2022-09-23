// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.LinkedNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.text.ParseException;
import java.util.Date;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.ContextAttributes;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ObjectBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ArrayBuilders;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DeserializerFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DeserializerCache;
import java.io.Serializable;

public abstract class DeserializationContext extends DatabindContext implements Serializable
{
    private static final long serialVersionUID = -4290063686213707727L;
    private static final int MAX_ERROR_STR_LEN = 500;
    protected final DeserializerCache _cache;
    protected final DeserializerFactory _factory;
    protected final DeserializationConfig _config;
    protected final int _featureFlags;
    protected final Class<?> _view;
    protected transient JsonParser _parser;
    protected final InjectableValues _injectableValues;
    protected transient ArrayBuilders _arrayBuilders;
    protected transient ObjectBuffer _objectBuffer;
    protected transient DateFormat _dateFormat;
    protected transient ContextAttributes _attributes;
    
    protected DeserializationContext(final DeserializerFactory df) {
        this(df, null);
    }
    
    protected DeserializationContext(final DeserializerFactory df, final DeserializerCache cache) {
        if (df == null) {
            throw new IllegalArgumentException("Can not pass null DeserializerFactory");
        }
        this._factory = df;
        this._cache = ((cache == null) ? new DeserializerCache() : cache);
        this._featureFlags = 0;
        this._config = null;
        this._injectableValues = null;
        this._view = null;
        this._attributes = null;
    }
    
    protected DeserializationContext(final DeserializationContext src, final DeserializerFactory factory) {
        this._cache = src._cache;
        this._factory = factory;
        this._config = src._config;
        this._featureFlags = src._featureFlags;
        this._view = src._view;
        this._parser = src._parser;
        this._injectableValues = src._injectableValues;
        this._attributes = src._attributes;
    }
    
    protected DeserializationContext(final DeserializationContext src, final DeserializationConfig config, final JsonParser p, final InjectableValues injectableValues) {
        this._cache = src._cache;
        this._factory = src._factory;
        this._config = config;
        this._featureFlags = config.getDeserializationFeatures();
        this._view = config.getActiveView();
        this._parser = p;
        this._injectableValues = injectableValues;
        this._attributes = config.getAttributes();
    }
    
    @Override
    public DeserializationConfig getConfig() {
        return this._config;
    }
    
    @Override
    public final Class<?> getActiveView() {
        return this._view;
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
    public Object getAttribute(final Object key) {
        return this._attributes.getAttribute(key);
    }
    
    @Override
    public DeserializationContext setAttribute(final Object key, final Object value) {
        this._attributes = this._attributes.withPerCallAttribute(key, value);
        return this;
    }
    
    public DeserializerFactory getFactory() {
        return this._factory;
    }
    
    public final boolean isEnabled(final DeserializationFeature feat) {
        return (this._featureFlags & feat.getMask()) != 0x0;
    }
    
    public final boolean hasDeserializationFeatures(final int featureMask) {
        return this._config.hasDeserializationFeatures(featureMask);
    }
    
    public final JsonParser getParser() {
        return this._parser;
    }
    
    public final Object findInjectableValue(final Object valueId, final BeanProperty forProperty, final Object beanInstance) {
        if (this._injectableValues == null) {
            throw new IllegalStateException("No 'injectableValues' configured, can not inject value with id [" + valueId + "]");
        }
        return this._injectableValues.findInjectableValue(valueId, this, forProperty, beanInstance);
    }
    
    public final Base64Variant getBase64Variant() {
        return this._config.getBase64Variant();
    }
    
    public final JsonNodeFactory getNodeFactory() {
        return this._config.getNodeFactory();
    }
    
    public Locale getLocale() {
        return this._config.getLocale();
    }
    
    public TimeZone getTimeZone() {
        return this._config.getTimeZone();
    }
    
    @Deprecated
    public boolean hasValueDeserializerFor(final JavaType type) {
        return this.hasValueDeserializerFor(type, null);
    }
    
    public boolean hasValueDeserializerFor(final JavaType type, final AtomicReference<Throwable> cause) {
        try {
            return this._cache.hasValueDeserializerFor(this, this._factory, type);
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
    
    public final JsonDeserializer<Object> findContextualValueDeserializer(final JavaType type, final BeanProperty prop) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
        if (deser != null) {
            deser = (JsonDeserializer<Object>)this.handleSecondaryContextualization(deser, prop);
        }
        return deser;
    }
    
    public final JsonDeserializer<Object> findRootValueDeserializer(final JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
        if (deser == null) {
            return null;
        }
        deser = (JsonDeserializer<Object>)this.handleSecondaryContextualization(deser, null);
        TypeDeserializer typeDeser = this._factory.findTypeDeserializer(this._config, type);
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(null);
            return new TypeWrappedDeserializer(typeDeser, deser);
        }
        return deser;
    }
    
    public final KeyDeserializer findKeyDeserializer(final JavaType keyType, final BeanProperty prop) throws JsonMappingException {
        KeyDeserializer kd = this._cache.findKeyDeserializer(this, this._factory, keyType);
        if (kd instanceof ContextualKeyDeserializer) {
            kd = ((ContextualKeyDeserializer)kd).createContextual(this, prop);
        }
        return kd;
    }
    
    public abstract ReadableObjectId findObjectId(final Object p0, final ObjectIdGenerator<?> p1, final ObjectIdResolver p2);
    
    @Deprecated
    public abstract ReadableObjectId findObjectId(final Object p0, final ObjectIdGenerator<?> p1);
    
    public abstract void checkUnresolvedObjectId() throws UnresolvedForwardReference;
    
    public final JavaType constructType(final Class<?> cls) {
        return this._config.constructType(cls);
    }
    
    public Class<?> findClass(final String className) throws ClassNotFoundException {
        return ClassUtil.findClass(className);
    }
    
    public final ObjectBuffer leaseObjectBuffer() {
        ObjectBuffer buf = this._objectBuffer;
        if (buf == null) {
            buf = new ObjectBuffer();
        }
        else {
            this._objectBuffer = null;
        }
        return buf;
    }
    
    public final void returnObjectBuffer(final ObjectBuffer buf) {
        if (this._objectBuffer == null || buf.initialCapacity() >= this._objectBuffer.initialCapacity()) {
            this._objectBuffer = buf;
        }
    }
    
    public final ArrayBuilders getArrayBuilders() {
        if (this._arrayBuilders == null) {
            this._arrayBuilders = new ArrayBuilders();
        }
        return this._arrayBuilders;
    }
    
    public abstract JsonDeserializer<Object> deserializerInstance(final Annotated p0, final Object p1) throws JsonMappingException;
    
    public abstract KeyDeserializer keyDeserializerInstance(final Annotated p0, final Object p1) throws JsonMappingException;
    
    public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, final BeanProperty prop) throws JsonMappingException {
        if (deser != null && deser instanceof ContextualDeserializer) {
            deser = ((ContextualDeserializer)deser).createContextual(this, prop);
        }
        return deser;
    }
    
    public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, final BeanProperty prop) throws JsonMappingException {
        if (deser != null && deser instanceof ContextualDeserializer) {
            deser = ((ContextualDeserializer)deser).createContextual(this, prop);
        }
        return deser;
    }
    
    public Date parseDate(final String dateStr) throws IllegalArgumentException {
        try {
            final DateFormat df = this.getDateFormat();
            return df.parse(dateStr);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse Date value '" + dateStr + "': " + e.getMessage());
        }
    }
    
    public Calendar constructCalendar(final Date d) {
        final Calendar c = Calendar.getInstance(this.getTimeZone());
        c.setTime(d);
        return c;
    }
    
    public <T> T readValue(final JsonParser p, final Class<T> type) throws IOException {
        return this.readValue(p, this.getTypeFactory().constructType(type));
    }
    
    public <T> T readValue(final JsonParser p, final JavaType type) throws IOException {
        final JsonDeserializer<Object> deser = this.findRootValueDeserializer(type);
        if (deser == null) {}
        return (T)deser.deserialize(p, this);
    }
    
    public <T> T readPropertyValue(final JsonParser p, final BeanProperty prop, final Class<T> type) throws IOException {
        return this.readPropertyValue(p, prop, this.getTypeFactory().constructType(type));
    }
    
    public <T> T readPropertyValue(final JsonParser p, final BeanProperty prop, final JavaType type) throws IOException {
        final JsonDeserializer<Object> deser = this.findContextualValueDeserializer(type, prop);
        if (deser == null) {}
        return (T)deser.deserialize(p, this);
    }
    
    public boolean handleUnknownProperty(final JsonParser p, final JsonDeserializer<?> deser, final Object instanceOrClass, final String propName) throws IOException, JsonProcessingException {
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        if (h != null) {
            while (h != null) {
                if (h.value().handleUnknownProperty(this, p, deser, instanceOrClass, propName)) {
                    return true;
                }
                h = h.next();
            }
        }
        return false;
    }
    
    public void reportUnknownProperty(final Object instanceOrClass, final String fieldName, final JsonDeserializer<?> deser) throws JsonMappingException {
        if (!this.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            return;
        }
        final Collection<Object> propIds = (deser == null) ? null : deser.getKnownPropertyNames();
        throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, fieldName, propIds);
    }
    
    public JsonMappingException mappingException(final Class<?> targetClass) {
        return this.mappingException(targetClass, this._parser.getCurrentToken());
    }
    
    public JsonMappingException mappingException(final Class<?> targetClass, final JsonToken token) {
        return JsonMappingException.from(this._parser, "Can not deserialize instance of " + this._calcName(targetClass) + " out of " + token + " token");
    }
    
    public JsonMappingException mappingException(final String message) {
        return JsonMappingException.from(this.getParser(), message);
    }
    
    public JsonMappingException instantiationException(final Class<?> instClass, final Throwable t) {
        return JsonMappingException.from(this._parser, "Can not construct instance of " + instClass.getName() + ", problem: " + t.getMessage(), t);
    }
    
    public JsonMappingException instantiationException(final Class<?> instClass, final String msg) {
        return JsonMappingException.from(this._parser, "Can not construct instance of " + instClass.getName() + ", problem: " + msg);
    }
    
    @Deprecated
    public JsonMappingException weirdStringException(final Class<?> instClass, final String msg) {
        return this.weirdStringException(null, instClass, msg);
    }
    
    public JsonMappingException weirdStringException(final String value, final Class<?> instClass, final String msg) {
        return InvalidFormatException.from(this._parser, "Can not construct instance of " + instClass.getName() + " from String value '" + this._valueDesc() + "': " + msg, value, instClass);
    }
    
    @Deprecated
    public JsonMappingException weirdNumberException(final Class<?> instClass, final String msg) {
        return this.weirdStringException(null, instClass, msg);
    }
    
    public JsonMappingException weirdNumberException(final Number value, final Class<?> instClass, final String msg) {
        return InvalidFormatException.from(this._parser, "Can not construct instance of " + instClass.getName() + " from number value (" + this._valueDesc() + "): " + msg, null, instClass);
    }
    
    public JsonMappingException weirdKeyException(final Class<?> keyClass, final String keyValue, final String msg) {
        return InvalidFormatException.from(this._parser, "Can not construct Map key of type " + keyClass.getName() + " from String \"" + this._desc(keyValue) + "\": " + msg, keyValue, keyClass);
    }
    
    public JsonMappingException wrongTokenException(final JsonParser p, final JsonToken expToken, final String msg0) {
        String msg = "Unexpected token (" + p.getCurrentToken() + "), expected " + expToken;
        if (msg0 != null) {
            msg = msg + ": " + msg0;
        }
        return JsonMappingException.from(p, msg);
    }
    
    public JsonMappingException unknownTypeException(final JavaType type, final String id) {
        return JsonMappingException.from(this._parser, "Could not resolve type id '" + id + "' into a subtype of " + type);
    }
    
    public JsonMappingException endOfInputException(final Class<?> instClass) {
        return JsonMappingException.from(this._parser, "Unexpected end-of-input when trying to deserialize a " + instClass.getName());
    }
    
    protected DateFormat getDateFormat() {
        if (this._dateFormat != null) {
            return this._dateFormat;
        }
        DateFormat df = this._config.getDateFormat();
        df = (this._dateFormat = (DateFormat)df.clone());
        return df;
    }
    
    protected String determineClassName(final Object instance) {
        return ClassUtil.getClassDescription(instance);
    }
    
    protected String _calcName(final Class<?> cls) {
        if (cls.isArray()) {
            return this._calcName(cls.getComponentType()) + "[]";
        }
        return cls.getName();
    }
    
    protected String _valueDesc() {
        try {
            return this._desc(this._parser.getText());
        }
        catch (Exception e) {
            return "[N/A]";
        }
    }
    
    protected String _desc(String desc) {
        if (desc.length() > 500) {
            desc = desc.substring(0, 500) + "]...[" + desc.substring(desc.length() - 500);
        }
        return desc;
    }
}
