// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.Collection;

@JacksonStdImpl
public class CollectionDeserializer extends ContainerDeserializerBase<Collection<Object>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 3917273725180652224L;
    protected final JavaType _collectionType;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final JsonDeserializer<Object> _delegateDeserializer;
    
    public CollectionDeserializer(final JavaType collectionType, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final ValueInstantiator valueInstantiator) {
        this(collectionType, valueDeser, valueTypeDeser, valueInstantiator, null);
    }
    
    protected CollectionDeserializer(final JavaType collectionType, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final ValueInstantiator valueInstantiator, final JsonDeserializer<Object> delegateDeser) {
        super(collectionType);
        this._collectionType = collectionType;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = valueInstantiator;
        this._delegateDeserializer = delegateDeser;
    }
    
    protected CollectionDeserializer(final CollectionDeserializer src) {
        super(src._collectionType);
        this._collectionType = src._collectionType;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
    }
    
    protected CollectionDeserializer withResolved(final JsonDeserializer<?> dd, final JsonDeserializer<?> vd, final TypeDeserializer vtd) {
        if (dd == this._delegateDeserializer && vd == this._valueDeserializer && vtd == this._valueTypeDeserializer) {
            return this;
        }
        return new CollectionDeserializer(this._collectionType, (JsonDeserializer<Object>)vd, vtd, this._valueInstantiator, (JsonDeserializer<Object>)dd);
    }
    
    @Override
    public CollectionDeserializer createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> delegateDeser = null;
        if (this._valueInstantiator != null && this._valueInstantiator.canCreateUsingDelegate()) {
            final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._collectionType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
            }
            delegateDeser = this.findDeserializer(ctxt, delegateType, property);
        }
        JsonDeserializer<?> valueDeser = this._valueDeserializer;
        valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(this._collectionType.getContentType(), property);
        }
        else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property);
        }
        TypeDeserializer valueTypeDeser = this._valueTypeDeserializer;
        if (valueTypeDeser != null) {
            valueTypeDeser = valueTypeDeser.forProperty(property);
        }
        return this.withResolved(delegateDeser, valueDeser, valueTypeDeser);
    }
    
    @Override
    public JavaType getContentType() {
        return this._collectionType.getContentType();
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }
    
    @Override
    public Collection<Object> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            return (Collection<Object>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (jp.getCurrentToken() == JsonToken.VALUE_STRING) {
            final String str = jp.getText();
            if (str.length() == 0) {
                return (Collection<Object>)this._valueInstantiator.createFromString(ctxt, str);
            }
        }
        return this.deserialize(jp, ctxt, (Collection<Object>)this._valueInstantiator.createUsingDefault(ctxt));
    }
    
    @Override
    public Collection<Object> deserialize(final JsonParser jp, final DeserializationContext ctxt, final Collection<Object> result) throws IOException, JsonProcessingException {
        if (!jp.isExpectedStartArrayToken()) {
            return this.handleNonArray(jp, ctxt, result);
        }
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        final CollectionReferringAccumulator referringAccumulator = (valueDes.getObjectIdReader() == null) ? null : new CollectionReferringAccumulator(this._collectionType.getContentType().getRawClass(), result);
        JsonToken t;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
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
                if (referringAccumulator != null) {
                    referringAccumulator.add(value);
                }
                else {
                    result.add(value);
                }
            }
            catch (UnresolvedForwardReference reference) {
                if (referringAccumulator == null) {
                    throw JsonMappingException.from(jp, "Unresolved forward reference but no identity info.", reference);
                }
                final ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(reference);
                reference.getRoid().appendReferring(ref);
            }
        }
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }
    
    protected final Collection<Object> handleNonArray(final JsonParser jp, final DeserializationContext ctxt, final Collection<Object> result) throws IOException, JsonProcessingException {
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            throw ctxt.mappingException(this._collectionType.getRawClass());
        }
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        final JsonToken t = jp.getCurrentToken();
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
        result.add(value);
        return result;
    }
    
    public static final class CollectionReferringAccumulator
    {
        private final Class<?> _elementType;
        private final Collection<Object> _result;
        private List<CollectionReferring> _accumulator;
        
        public CollectionReferringAccumulator(final Class<?> elementType, final Collection<Object> result) {
            this._accumulator = new ArrayList<CollectionReferring>();
            this._elementType = elementType;
            this._result = result;
        }
        
        public void add(final Object value) {
            if (this._accumulator.isEmpty()) {
                this._result.add(value);
            }
            else {
                final CollectionReferring ref = this._accumulator.get(this._accumulator.size() - 1);
                ref.next.add(value);
            }
        }
        
        public ReadableObjectId.Referring handleUnresolvedReference(final UnresolvedForwardReference reference) {
            final CollectionReferring id = new CollectionReferring(this, reference, (Class)this._elementType);
            this._accumulator.add(id);
            return id;
        }
        
        public void resolveForwardReference(final Object id, final Object value) throws IOException {
            final Iterator<CollectionReferring> iterator = this._accumulator.iterator();
            Collection<Object> previous = this._result;
            while (iterator.hasNext()) {
                final CollectionReferring ref = iterator.next();
                if (ref.hasId(id)) {
                    iterator.remove();
                    previous.add(value);
                    previous.addAll(ref.next);
                    return;
                }
                previous = ref.next;
            }
            throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
        }
    }
    
    private static final class CollectionReferring extends ReadableObjectId.Referring
    {
        private final CollectionReferringAccumulator _parent;
        public final List<Object> next;
        
        private CollectionReferring(final CollectionReferringAccumulator parent, final UnresolvedForwardReference reference, final Class<?> contentType) {
            super(reference, contentType);
            this.next = new ArrayList<Object>();
            this._parent = parent;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            this._parent.resolveForwardReference(id, value);
        }
    }
}
