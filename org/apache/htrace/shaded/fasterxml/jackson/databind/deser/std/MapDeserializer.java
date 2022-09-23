// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import java.lang.reflect.InvocationTargetException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ArrayBuilders;
import java.util.HashSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.Map;

@JacksonStdImpl
public class MapDeserializer extends ContainerDeserializerBase<Map<Object, Object>> implements ContextualDeserializer, ResolvableDeserializer
{
    private static final long serialVersionUID = -3378654289961736240L;
    protected final JavaType _mapType;
    protected final KeyDeserializer _keyDeserializer;
    protected boolean _standardStringKey;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final boolean _hasDefaultCreator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected PropertyBasedCreator _propertyBasedCreator;
    protected HashSet<String> _ignorableProperties;
    
    public MapDeserializer(final JavaType mapType, final ValueInstantiator valueInstantiator, final KeyDeserializer keyDeser, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser) {
        super(mapType);
        this._mapType = mapType;
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = valueInstantiator;
        this._hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
        this._delegateDeserializer = null;
        this._propertyBasedCreator = null;
        this._standardStringKey = this._isStdKeyDeser(mapType, keyDeser);
    }
    
    protected MapDeserializer(final MapDeserializer src) {
        super(src._mapType);
        this._mapType = src._mapType;
        this._keyDeserializer = src._keyDeserializer;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._hasDefaultCreator = src._hasDefaultCreator;
        this._ignorableProperties = src._ignorableProperties;
        this._standardStringKey = src._standardStringKey;
    }
    
    protected MapDeserializer(final MapDeserializer src, final KeyDeserializer keyDeser, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final HashSet<String> ignorable) {
        super(src._mapType);
        this._mapType = src._mapType;
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = src._valueInstantiator;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._hasDefaultCreator = src._hasDefaultCreator;
        this._ignorableProperties = ignorable;
        this._standardStringKey = this._isStdKeyDeser(this._mapType, keyDeser);
    }
    
    protected MapDeserializer withResolved(final KeyDeserializer keyDeser, final TypeDeserializer valueTypeDeser, final JsonDeserializer<?> valueDeser, final HashSet<String> ignorable) {
        if (this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser && this._ignorableProperties == ignorable) {
            return this;
        }
        return new MapDeserializer(this, keyDeser, (JsonDeserializer<Object>)valueDeser, valueTypeDeser, ignorable);
    }
    
    protected final boolean _isStdKeyDeser(final JavaType mapType, final KeyDeserializer keyDeser) {
        if (keyDeser == null) {
            return true;
        }
        final JavaType keyType = mapType.getKeyType();
        if (keyType == null) {
            return true;
        }
        final Class<?> rawKeyType = keyType.getRawClass();
        return (rawKeyType == String.class || rawKeyType == Object.class) && this.isDefaultKeyDeserializer(keyDeser);
    }
    
    public void setIgnorableProperties(final String[] ignorable) {
        this._ignorableProperties = ((ignorable == null || ignorable.length == 0) ? null : ArrayBuilders.arrayToSet(ignorable));
    }
    
    @Override
    public void resolve(final DeserializationContext ctxt) throws JsonMappingException {
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._mapType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
            }
            this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, null);
        }
        if (this._valueInstantiator.canCreateFromObjectWith()) {
            final SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps);
        }
        this._standardStringKey = this._isStdKeyDeser(this._mapType, this._keyDeserializer);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        KeyDeserializer kd = this._keyDeserializer;
        if (kd == null) {
            kd = ctxt.findKeyDeserializer(this._mapType.getKeyType(), property);
        }
        else if (kd instanceof ContextualKeyDeserializer) {
            kd = ((ContextualKeyDeserializer)kd).createContextual(ctxt, property);
        }
        JsonDeserializer<?> vd = this._valueDeserializer;
        vd = this.findConvertingContentDeserializer(ctxt, property, vd);
        if (vd == null) {
            vd = ctxt.findContextualValueDeserializer(this._mapType.getContentType(), property);
        }
        else {
            vd = ctxt.handleSecondaryContextualization(vd, property);
        }
        TypeDeserializer vtd = this._valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        HashSet<String> ignored = this._ignorableProperties;
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && property != null) {
            final String[] moreToIgnore = intr.findPropertiesToIgnore(property.getMember());
            if (moreToIgnore != null) {
                ignored = ((ignored == null) ? new HashSet<String>() : new HashSet<String>(ignored));
                for (final String str : moreToIgnore) {
                    ignored.add(str);
                }
            }
        }
        return this.withResolved(kd, vtd, vd, ignored);
    }
    
    @Override
    public JavaType getContentType() {
        return this._mapType.getContentType();
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }
    
    @Override
    public Map<Object, Object> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingCreator(jp, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return (Map<Object, Object>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (!this._hasDefaultCreator) {
            throw ctxt.instantiationException(this.getMapClass(), "No default constructor found");
        }
        final JsonToken t = jp.getCurrentToken();
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            if (t == JsonToken.VALUE_STRING) {
                return (Map<Object, Object>)this._valueInstantiator.createFromString(ctxt, jp.getText());
            }
            throw ctxt.mappingException(this.getMapClass());
        }
        else {
            final Map<Object, Object> result = (Map<Object, Object>)this._valueInstantiator.createUsingDefault(ctxt);
            if (this._standardStringKey) {
                this._readAndBindStringMap(jp, ctxt, result);
                return result;
            }
            this._readAndBind(jp, ctxt, result);
            return result;
        }
    }
    
    @Override
    public Map<Object, Object> deserialize(final JsonParser jp, final DeserializationContext ctxt, final Map<Object, Object> result) throws IOException, JsonProcessingException {
        final JsonToken t = jp.getCurrentToken();
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
            throw ctxt.mappingException(this.getMapClass());
        }
        if (this._standardStringKey) {
            this._readAndBindStringMap(jp, ctxt, result);
            return result;
        }
        this._readAndBind(jp, ctxt, result);
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }
    
    public final Class<?> getMapClass() {
        return this._mapType.getRawClass();
    }
    
    @Override
    public JavaType getValueType() {
        return this._mapType;
    }
    
    protected final void _readAndBind(final JsonParser jp, final DeserializationContext ctxt, final Map<Object, Object> result) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        final KeyDeserializer keyDes = this._keyDeserializer;
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        MapReferringAccumulator referringAccumulator = null;
        final boolean useObjectId = valueDes.getObjectIdReader() != null;
        if (useObjectId) {
            referringAccumulator = new MapReferringAccumulator(this._mapType.getContentType().getRawClass(), result);
        }
        while (t == JsonToken.FIELD_NAME) {
            final String fieldName = jp.getCurrentName();
            final Object key = keyDes.deserializeKey(fieldName, ctxt);
            t = jp.nextToken();
            if (this._ignorableProperties != null && this._ignorableProperties.contains(fieldName)) {
                jp.skipChildren();
            }
            else {
                try {
                    Object value;
                    if (t == JsonToken.VALUE_NULL) {
                        value = valueDes.getNullValue();
                    }
                    else if (typeDeser == null) {
                        value = valueDes.deserialize(jp, ctxt);
                    }
                    else {
                        value = valueDes.deserializeWithType(jp, ctxt, typeDeser);
                    }
                    if (useObjectId) {
                        referringAccumulator.put(key, value);
                    }
                    else {
                        result.put(key, value);
                    }
                }
                catch (UnresolvedForwardReference reference) {
                    this.handleUnresolvedReference(jp, referringAccumulator, key, reference);
                }
            }
            t = jp.nextToken();
        }
    }
    
    protected final void _readAndBindStringMap(final JsonParser jp, final DeserializationContext ctxt, final Map<Object, Object> result) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        MapReferringAccumulator referringAccumulator = null;
        final boolean useObjectId = valueDes.getObjectIdReader() != null;
        if (useObjectId) {
            referringAccumulator = new MapReferringAccumulator(this._mapType.getContentType().getRawClass(), result);
        }
        while (t == JsonToken.FIELD_NAME) {
            final String fieldName = jp.getCurrentName();
            t = jp.nextToken();
            if (this._ignorableProperties != null && this._ignorableProperties.contains(fieldName)) {
                jp.skipChildren();
            }
            else {
                try {
                    Object value;
                    if (t == JsonToken.VALUE_NULL) {
                        value = valueDes.getNullValue();
                    }
                    else if (typeDeser == null) {
                        value = valueDes.deserialize(jp, ctxt);
                    }
                    else {
                        value = valueDes.deserializeWithType(jp, ctxt, typeDeser);
                    }
                    if (useObjectId) {
                        referringAccumulator.put(fieldName, value);
                    }
                    else {
                        result.put(fieldName, value);
                    }
                }
                catch (UnresolvedForwardReference reference) {
                    this.handleUnresolvedReference(jp, referringAccumulator, fieldName, reference);
                }
            }
            t = jp.nextToken();
        }
    }
    
    public Map<Object, Object> _deserializeUsingCreator(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt, null);
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        while (t == JsonToken.FIELD_NAME) {
            final String propName = jp.getCurrentName();
            t = jp.nextToken();
            if (this._ignorableProperties != null && this._ignorableProperties.contains(propName)) {
                jp.skipChildren();
            }
            else {
                final SettableBeanProperty prop = creator.findCreatorProperty(propName);
                if (prop != null) {
                    final Object value = prop.deserialize(jp, ctxt);
                    if (buffer.assignParameter(prop.getCreatorIndex(), value)) {
                        jp.nextToken();
                        Map<Object, Object> result;
                        try {
                            result = (Map<Object, Object>)creator.build(ctxt, buffer);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow(e, this._mapType.getRawClass());
                            return null;
                        }
                        this._readAndBind(jp, ctxt, result);
                        return result;
                    }
                }
                else {
                    final String fieldName = jp.getCurrentName();
                    final Object key = this._keyDeserializer.deserializeKey(fieldName, ctxt);
                    Object value2;
                    if (t == JsonToken.VALUE_NULL) {
                        value2 = valueDes.getNullValue();
                    }
                    else if (typeDeser == null) {
                        value2 = valueDes.deserialize(jp, ctxt);
                    }
                    else {
                        value2 = valueDes.deserializeWithType(jp, ctxt, typeDeser);
                    }
                    buffer.bufferMapProperty(key, value2);
                }
            }
            t = jp.nextToken();
        }
        try {
            return (Map<Object, Object>)creator.build(ctxt, buffer);
        }
        catch (Exception e2) {
            this.wrapAndThrow(e2, this._mapType.getRawClass());
            return null;
        }
    }
    
    protected void wrapAndThrow(Throwable t, final Object ref) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof IOException && !(t instanceof JsonMappingException)) {
            throw (IOException)t;
        }
        throw JsonMappingException.wrapWithPath(t, ref, null);
    }
    
    private void handleUnresolvedReference(final JsonParser jp, final MapReferringAccumulator accumulator, final Object key, final UnresolvedForwardReference reference) throws JsonMappingException {
        if (accumulator == null) {
            throw JsonMappingException.from(jp, "Unresolved forward reference but no identity info.", reference);
        }
        final ReadableObjectId.Referring referring = accumulator.handleUnresolvedReference(reference, key);
        reference.getRoid().appendReferring(referring);
    }
    
    private static final class MapReferringAccumulator
    {
        private final Class<?> _valueType;
        private Map<Object, Object> _result;
        private List<MapReferring> _accumulator;
        
        public MapReferringAccumulator(final Class<?> valueType, final Map<Object, Object> result) {
            this._accumulator = new ArrayList<MapReferring>();
            this._valueType = valueType;
            this._result = result;
        }
        
        public void put(final Object key, final Object value) {
            if (this._accumulator.isEmpty()) {
                this._result.put(key, value);
            }
            else {
                final MapReferring ref = this._accumulator.get(this._accumulator.size() - 1);
                ref.next.put(key, value);
            }
        }
        
        public ReadableObjectId.Referring handleUnresolvedReference(final UnresolvedForwardReference reference, final Object key) {
            final MapReferring id = new MapReferring(this, reference, (Class)this._valueType, key);
            this._accumulator.add(id);
            return id;
        }
        
        public void resolveForwardReference(final Object id, final Object value) throws IOException {
            final Iterator<MapReferring> iterator = this._accumulator.iterator();
            Map<Object, Object> previous = this._result;
            while (iterator.hasNext()) {
                final MapReferring ref = iterator.next();
                if (ref.hasId(id)) {
                    iterator.remove();
                    previous.put(ref.key, value);
                    previous.putAll(ref.next);
                    return;
                }
                previous = ref.next;
            }
            throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
        }
    }
    
    private static final class MapReferring extends ReadableObjectId.Referring
    {
        private final MapReferringAccumulator _parent;
        public final Map<Object, Object> next;
        public final Object key;
        
        private MapReferring(final MapReferringAccumulator parent, final UnresolvedForwardReference ref, final Class<?> valueType, final Object key) {
            super(ref, valueType);
            this.next = new LinkedHashMap<Object, Object>();
            this._parent = parent;
            this.key = key;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            this._parent.resolveForwardReference(id, value);
        }
    }
}
