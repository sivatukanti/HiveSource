// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.TokenBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import java.util.HashSet;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import java.io.Serializable;

public class BeanDeserializer extends BeanDeserializerBase implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public BeanDeserializer(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final BeanPropertyMap properties, final Map<String, SettableBeanProperty> backRefs, final HashSet<String> ignorableProps, final boolean ignoreAllUnknown, final boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
    }
    
    protected BeanDeserializer(final BeanDeserializerBase src) {
        super(src, src._ignoreAllUnknown);
    }
    
    protected BeanDeserializer(final BeanDeserializerBase src, final boolean ignoreAllUnknown) {
        super(src, ignoreAllUnknown);
    }
    
    protected BeanDeserializer(final BeanDeserializerBase src, final NameTransformer unwrapper) {
        super(src, unwrapper);
    }
    
    public BeanDeserializer(final BeanDeserializerBase src, final ObjectIdReader oir) {
        super(src, oir);
    }
    
    public BeanDeserializer(final BeanDeserializerBase src, final HashSet<String> ignorableProps) {
        super(src, ignorableProps);
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer unwrapper) {
        if (this.getClass() != BeanDeserializer.class) {
            return this;
        }
        return new BeanDeserializer(this, unwrapper);
    }
    
    @Override
    public BeanDeserializer withObjectIdReader(final ObjectIdReader oir) {
        return new BeanDeserializer(this, oir);
    }
    
    @Override
    public BeanDeserializer withIgnorableProperties(final HashSet<String> ignorableProps) {
        return new BeanDeserializer(this, ignorableProps);
    }
    
    @Override
    protected BeanDeserializerBase asArrayDeserializer() {
        final SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
        return new BeanAsArrayDeserializer(this, props);
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonToken t = jp.getCurrentToken();
        if (t != JsonToken.START_OBJECT) {
            return this._deserializeOther(jp, ctxt, t);
        }
        if (this._vanillaProcessing) {
            return this.vanillaDeserialize(jp, ctxt, jp.nextToken());
        }
        jp.nextToken();
        if (this._objectIdReader != null) {
            return this.deserializeWithObjectId(jp, ctxt);
        }
        return this.deserializeFromObject(jp, ctxt);
    }
    
    protected final Object _deserializeOther(final JsonParser jp, final DeserializationContext ctxt, final JsonToken t) throws IOException, JsonProcessingException {
        if (t == null) {
            return this._missingToken(jp, ctxt);
        }
        switch (t) {
            case VALUE_STRING: {
                return this.deserializeFromString(jp, ctxt);
            }
            case VALUE_NUMBER_INT: {
                return this.deserializeFromNumber(jp, ctxt);
            }
            case VALUE_NUMBER_FLOAT: {
                return this.deserializeFromDouble(jp, ctxt);
            }
            case VALUE_EMBEDDED_OBJECT: {
                return jp.getEmbeddedObject();
            }
            case VALUE_TRUE:
            case VALUE_FALSE: {
                return this.deserializeFromBoolean(jp, ctxt);
            }
            case START_ARRAY: {
                return this.deserializeFromArray(jp, ctxt);
            }
            case FIELD_NAME:
            case END_OBJECT: {
                if (this._vanillaProcessing) {
                    return this.vanillaDeserialize(jp, ctxt, t);
                }
                if (this._objectIdReader != null) {
                    return this.deserializeWithObjectId(jp, ctxt);
                }
                return this.deserializeFromObject(jp, ctxt);
            }
            default: {
                throw ctxt.mappingException(this.handledType());
            }
        }
    }
    
    protected Object _missingToken(final JsonParser jp, final DeserializationContext ctxt) throws JsonProcessingException {
        throw ctxt.endOfInputException(this.handledType());
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt, final Object bean) throws IOException, JsonProcessingException {
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        if (this._unwrappedPropertyHandler != null) {
            return this.deserializeWithUnwrapped(jp, ctxt, bean);
        }
        if (this._externalTypeIdHandler != null) {
            return this.deserializeWithExternalTypeId(jp, ctxt, bean);
        }
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        if (this._needViewProcesing) {
            final Class<?> view = ctxt.getActiveView();
            if (view != null) {
                return this.deserializeWithView(jp, ctxt, bean, view);
            }
        }
        while (t == JsonToken.FIELD_NAME) {
            final String propName = jp.getCurrentName();
            jp.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    prop.deserializeAndSet(jp, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow(e, bean, propName, ctxt);
                }
            }
            else {
                this.handleUnknownVanilla(jp, ctxt, bean, propName);
            }
            t = jp.nextToken();
        }
        return bean;
    }
    
    private final Object vanillaDeserialize(final JsonParser jp, final DeserializationContext ctxt, JsonToken t) throws IOException, JsonProcessingException {
        final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        while (t == JsonToken.FIELD_NAME) {
            final String propName = jp.getCurrentName();
            jp.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    prop.deserializeAndSet(jp, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow(e, bean, propName, ctxt);
                }
            }
            else {
                this.handleUnknownVanilla(jp, ctxt, bean, propName);
            }
            t = jp.nextToken();
        }
        return bean;
    }
    
    @Override
    public Object deserializeFromObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (!this._nonStandardCreation) {
            final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
            if (jp.canReadObjectId()) {
                final Object id = jp.getObjectId();
                if (id != null) {
                    this._handleTypedObjectId(jp, ctxt, bean, id);
                }
            }
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            if (this._needViewProcesing) {
                final Class<?> view = ctxt.getActiveView();
                if (view != null) {
                    return this.deserializeWithView(jp, ctxt, bean, view);
                }
            }
            for (JsonToken t = jp.getCurrentToken(); t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
                final String propName = jp.getCurrentName();
                jp.nextToken();
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(jp, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
                else {
                    this.handleUnknownVanilla(jp, ctxt, bean, propName);
                }
            }
            return bean;
        }
        if (this._unwrappedPropertyHandler != null) {
            return this.deserializeWithUnwrapped(jp, ctxt);
        }
        if (this._externalTypeIdHandler != null) {
            return this.deserializeWithExternalTypeId(jp, ctxt);
        }
        final Object bean = this.deserializeFromObjectUsingNonDefault(jp, ctxt);
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        return bean;
    }
    
    @Override
    protected Object _deserializeUsingPropertyBased(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt, this._objectIdReader);
        TokenBuffer unknown = null;
        for (JsonToken t = jp.getCurrentToken(); t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            final String propName = jp.getCurrentName();
            jp.nextToken();
            final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                final Object value = creatorProp.deserialize(jp, ctxt);
                if (buffer.assignParameter(creatorProp.getCreatorIndex(), value)) {
                    jp.nextToken();
                    Object bean;
                    try {
                        bean = creator.build(ctxt, buffer);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
                        bean = null;
                    }
                    if (bean.getClass() != this._beanType.getRawClass()) {
                        return this.handlePolymorphic(jp, ctxt, bean, unknown);
                    }
                    if (unknown != null) {
                        bean = this.handleUnknownProperties(ctxt, bean, unknown);
                    }
                    return this.deserialize(jp, ctxt, bean);
                }
            }
            else if (!buffer.readIdProperty(propName)) {
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(jp, ctxt));
                }
                else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    this.handleIgnoredProperty(jp, ctxt, this.handledType(), propName);
                }
                else if (this._anySetter != null) {
                    buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(jp, ctxt));
                }
                else {
                    if (unknown == null) {
                        unknown = new TokenBuffer(jp);
                    }
                    unknown.writeFieldName(propName);
                    unknown.copyCurrentStructure(jp);
                }
            }
        }
        Object bean2;
        try {
            bean2 = creator.build(ctxt, buffer);
        }
        catch (Exception e2) {
            this.wrapInstantiationProblem(e2, ctxt);
            bean2 = null;
        }
        if (unknown == null) {
            return bean2;
        }
        if (bean2.getClass() != this._beanType.getRawClass()) {
            return this.handlePolymorphic(null, ctxt, bean2, unknown);
        }
        return this.handleUnknownProperties(ctxt, bean2, unknown);
    }
    
    protected final Object deserializeWithView(final JsonParser jp, final DeserializationContext ctxt, final Object bean, final Class<?> activeView) throws IOException, JsonProcessingException {
        for (JsonToken t = jp.getCurrentToken(); t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            final String propName = jp.getCurrentName();
            jp.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (!prop.visibleInView(activeView)) {
                    jp.skipChildren();
                }
                else {
                    try {
                        prop.deserializeAndSet(jp, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else {
                this.handleUnknownVanilla(jp, ctxt, bean, propName);
            }
        }
        return bean;
    }
    
    protected Object deserializeWithUnwrapped(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return this.deserializeUsingPropertyBasedWithUnwrapped(jp, ctxt);
        }
        final TokenBuffer tokens = new TokenBuffer(jp);
        tokens.writeStartObject();
        final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (jp.getCurrentToken() != JsonToken.END_OBJECT) {
            final String propName = jp.getCurrentName();
            jp.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (activeView != null && !prop.visibleInView(activeView)) {
                    jp.skipChildren();
                }
                else {
                    try {
                        prop.deserializeAndSet(jp, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                this.handleIgnoredProperty(jp, ctxt, bean, propName);
            }
            else {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(jp);
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            jp.nextToken();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(jp, ctxt, bean, tokens);
        return bean;
    }
    
    protected Object deserializeWithUnwrapped(final JsonParser jp, final DeserializationContext ctxt, final Object bean) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        final TokenBuffer tokens = new TokenBuffer(jp);
        tokens.writeStartObject();
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (t == JsonToken.FIELD_NAME) {
            final String propName = jp.getCurrentName();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            jp.nextToken();
            if (prop != null) {
                if (activeView != null && !prop.visibleInView(activeView)) {
                    jp.skipChildren();
                }
                else {
                    try {
                        prop.deserializeAndSet(jp, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                this.handleIgnoredProperty(jp, ctxt, bean, propName);
            }
            else {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(jp);
                if (this._anySetter != null) {
                    this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
                }
            }
            t = jp.nextToken();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(jp, ctxt, bean, tokens);
        return bean;
    }
    
    protected Object deserializeUsingPropertyBasedWithUnwrapped(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt, this._objectIdReader);
        final TokenBuffer tokens = new TokenBuffer(jp);
        tokens.writeStartObject();
        for (JsonToken t = jp.getCurrentToken(); t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            final String propName = jp.getCurrentName();
            jp.nextToken();
            final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                final Object value = creatorProp.deserialize(jp, ctxt);
                if (buffer.assignParameter(creatorProp.getCreatorIndex(), value)) {
                    t = jp.nextToken();
                    Object bean;
                    try {
                        bean = creator.build(ctxt, buffer);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
                        continue;
                    }
                    while (t == JsonToken.FIELD_NAME) {
                        jp.nextToken();
                        tokens.copyCurrentStructure(jp);
                        t = jp.nextToken();
                    }
                    tokens.writeEndObject();
                    if (bean.getClass() != this._beanType.getRawClass()) {
                        tokens.close();
                        throw ctxt.mappingException("Can not create polymorphic instances with unwrapped values");
                    }
                    return this._unwrappedPropertyHandler.processUnwrapped(jp, ctxt, bean, tokens);
                }
            }
            else if (!buffer.readIdProperty(propName)) {
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(jp, ctxt));
                }
                else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    this.handleIgnoredProperty(jp, ctxt, this.handledType(), propName);
                }
                else {
                    tokens.writeFieldName(propName);
                    tokens.copyCurrentStructure(jp);
                    if (this._anySetter != null) {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(jp, ctxt));
                    }
                }
            }
        }
        Object bean2;
        try {
            bean2 = creator.build(ctxt, buffer);
        }
        catch (Exception e2) {
            this.wrapInstantiationProblem(e2, ctxt);
            return null;
        }
        return this._unwrappedPropertyHandler.processUnwrapped(jp, ctxt, bean2, tokens);
    }
    
    protected Object deserializeWithExternalTypeId(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._propertyBasedCreator != null) {
            return this.deserializeUsingPropertyBasedWithExternalTypeId(jp, ctxt);
        }
        return this.deserializeWithExternalTypeId(jp, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
    }
    
    protected Object deserializeWithExternalTypeId(final JsonParser jp, final DeserializationContext ctxt, final Object bean) throws IOException, JsonProcessingException {
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        final ExternalTypeHandler ext = this._externalTypeIdHandler.start();
        for (JsonToken t = jp.getCurrentToken(); t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            final String propName = jp.getCurrentName();
            jp.nextToken();
            final SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (jp.getCurrentToken().isScalarValue()) {
                    ext.handleTypePropertyValue(jp, ctxt, propName, bean);
                }
                if (activeView != null && !prop.visibleInView(activeView)) {
                    jp.skipChildren();
                }
                else {
                    try {
                        prop.deserializeAndSet(jp, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            }
            else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                this.handleIgnoredProperty(jp, ctxt, bean, propName);
            }
            else if (!ext.handlePropertyValue(jp, ctxt, propName, bean)) {
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
                else {
                    this.handleUnknownProperty(jp, ctxt, bean, propName);
                }
            }
        }
        return ext.complete(jp, ctxt, bean);
    }
    
    protected Object deserializeUsingPropertyBasedWithExternalTypeId(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final ExternalTypeHandler ext = this._externalTypeIdHandler.start();
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt, this._objectIdReader);
        final TokenBuffer tokens = new TokenBuffer(jp);
        tokens.writeStartObject();
        for (JsonToken t = jp.getCurrentToken(); t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            final String propName = jp.getCurrentName();
            jp.nextToken();
            final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (!ext.handlePropertyValue(jp, ctxt, propName, buffer)) {
                    final Object value = creatorProp.deserialize(jp, ctxt);
                    if (buffer.assignParameter(creatorProp.getCreatorIndex(), value)) {
                        t = jp.nextToken();
                        Object bean;
                        try {
                            bean = creator.build(ctxt, buffer);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
                            continue;
                        }
                        while (t == JsonToken.FIELD_NAME) {
                            jp.nextToken();
                            tokens.copyCurrentStructure(jp);
                            t = jp.nextToken();
                        }
                        if (bean.getClass() != this._beanType.getRawClass()) {
                            throw ctxt.mappingException("Can not create polymorphic instances with unwrapped values");
                        }
                        return ext.complete(jp, ctxt, bean);
                    }
                }
            }
            else if (!buffer.readIdProperty(propName)) {
                final SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(jp, ctxt));
                }
                else if (!ext.handlePropertyValue(jp, ctxt, propName, null)) {
                    if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                        this.handleIgnoredProperty(jp, ctxt, this.handledType(), propName);
                    }
                    else if (this._anySetter != null) {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(jp, ctxt));
                    }
                }
            }
        }
        try {
            return ext.complete(jp, ctxt, buffer, creator);
        }
        catch (Exception e2) {
            this.wrapInstantiationProblem(e2, ctxt);
            return null;
        }
    }
}
