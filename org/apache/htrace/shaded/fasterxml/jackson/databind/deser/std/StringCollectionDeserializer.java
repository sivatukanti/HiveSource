// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.Collection;

@JacksonStdImpl
public final class StringCollectionDeserializer extends ContainerDeserializerBase<Collection<String>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _collectionType;
    protected final JsonDeserializer<String> _valueDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final JsonDeserializer<Object> _delegateDeserializer;
    
    public StringCollectionDeserializer(final JavaType collectionType, final JsonDeserializer<?> valueDeser, final ValueInstantiator valueInstantiator) {
        this(collectionType, valueInstantiator, null, valueDeser);
    }
    
    protected StringCollectionDeserializer(final JavaType collectionType, final ValueInstantiator valueInstantiator, final JsonDeserializer<?> delegateDeser, final JsonDeserializer<?> valueDeser) {
        super(collectionType);
        this._collectionType = collectionType;
        this._valueDeserializer = (JsonDeserializer<String>)valueDeser;
        this._valueInstantiator = valueInstantiator;
        this._delegateDeserializer = (JsonDeserializer<Object>)delegateDeser;
    }
    
    protected StringCollectionDeserializer withResolved(final JsonDeserializer<?> delegateDeser, final JsonDeserializer<?> valueDeser) {
        if (this._valueDeserializer == valueDeser && this._delegateDeserializer == delegateDeser) {
            return this;
        }
        return new StringCollectionDeserializer(this._collectionType, this._valueInstantiator, delegateDeser, valueDeser);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> delegate = null;
        if (this._valueInstantiator != null) {
            final AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
            if (delegateCreator != null) {
                final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
                delegate = this.findDeserializer(ctxt, delegateType, property);
            }
        }
        JsonDeserializer<?> valueDeser = this._valueDeserializer;
        if (valueDeser == null) {
            valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
            if (valueDeser == null) {
                valueDeser = ctxt.findContextualValueDeserializer(this._collectionType.getContentType(), property);
            }
        }
        else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property);
        }
        if (this.isDefaultDeserializer(valueDeser)) {
            valueDeser = null;
        }
        return this.withResolved(delegate, valueDeser);
    }
    
    @Override
    public JavaType getContentType() {
        return this._collectionType.getContentType();
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        final JsonDeserializer<?> deser = this._valueDeserializer;
        return (JsonDeserializer<Object>)deser;
    }
    
    @Override
    public Collection<String> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return (Collection<String>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
        }
        final Collection<String> result = (Collection<String>)this._valueInstantiator.createUsingDefault(ctxt);
        return this.deserialize(jp, ctxt, result);
    }
    
    @Override
    public Collection<String> deserialize(final JsonParser jp, final DeserializationContext ctxt, final Collection<String> result) throws IOException {
        if (!jp.isExpectedStartArrayToken()) {
            return this.handleNonArray(jp, ctxt, result);
        }
        if (this._valueDeserializer != null) {
            return this.deserializeUsingCustom(jp, ctxt, result, this._valueDeserializer);
        }
        JsonToken t;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            String value;
            if (t == JsonToken.VALUE_STRING) {
                value = jp.getText();
            }
            else if (t == JsonToken.VALUE_NULL) {
                value = null;
            }
            else {
                value = this._parseString(jp, ctxt);
            }
            result.add(value);
        }
        return result;
    }
    
    private Collection<String> deserializeUsingCustom(final JsonParser jp, final DeserializationContext ctxt, final Collection<String> result, final JsonDeserializer<String> deser) throws IOException {
        JsonToken t;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            String value;
            if (t == JsonToken.VALUE_NULL) {
                value = deser.getNullValue();
            }
            else {
                value = deser.deserialize(jp, ctxt);
            }
            result.add(value);
        }
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }
    
    private final Collection<String> handleNonArray(final JsonParser jp, final DeserializationContext ctxt, final Collection<String> result) throws IOException {
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            throw ctxt.mappingException(this._collectionType.getRawClass());
        }
        final JsonDeserializer<String> valueDes = this._valueDeserializer;
        final JsonToken t = jp.getCurrentToken();
        String value;
        if (t == JsonToken.VALUE_NULL) {
            value = ((valueDes == null) ? null : valueDes.getNullValue());
        }
        else {
            value = ((valueDes == null) ? this._parseString(jp, ctxt) : valueDes.deserialize(jp, ctxt));
        }
        result.add(value);
        return result;
    }
}
