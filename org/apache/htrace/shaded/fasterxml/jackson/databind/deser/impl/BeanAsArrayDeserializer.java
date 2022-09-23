// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.util.HashSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.BeanDeserializerBase;

public class BeanAsArrayDeserializer extends BeanDeserializerBase
{
    private static final long serialVersionUID = 1L;
    protected final BeanDeserializerBase _delegate;
    protected final SettableBeanProperty[] _orderedProperties;
    
    public BeanAsArrayDeserializer(final BeanDeserializerBase delegate, final SettableBeanProperty[] ordered) {
        super(delegate);
        this._delegate = delegate;
        this._orderedProperties = ordered;
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer unwrapper) {
        return this._delegate.unwrappingDeserializer(unwrapper);
    }
    
    @Override
    public BeanAsArrayDeserializer withObjectIdReader(final ObjectIdReader oir) {
        return new BeanAsArrayDeserializer(this._delegate.withObjectIdReader(oir), this._orderedProperties);
    }
    
    @Override
    public BeanAsArrayDeserializer withIgnorableProperties(final HashSet<String> ignorableProps) {
        return new BeanAsArrayDeserializer(this._delegate.withIgnorableProperties(ignorableProps), this._orderedProperties);
    }
    
    @Override
    protected BeanDeserializerBase asArrayDeserializer() {
        return this;
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
            return this._deserializeFromNonArray(jp, ctxt);
        }
        if (!this._vanillaProcessing) {
            return this._deserializeNonVanilla(jp, ctxt);
        }
        final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        final SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        final int propCount = props.length;
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            if (i == propCount) {
                if (!this._ignoreAllUnknown) {
                    throw ctxt.mappingException("Unexpected JSON values; expected at most " + propCount + " properties (in JSON Array)");
                }
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    jp.skipChildren();
                }
                return bean;
            }
            else {
                final SettableBeanProperty prop = props[i];
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(jp, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, prop.getName(), ctxt);
                    }
                }
                else {
                    jp.skipChildren();
                }
                ++i;
            }
        }
        return bean;
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt, final Object bean) throws IOException, JsonProcessingException {
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        final SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        final int propCount = props.length;
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            if (i == propCount) {
                if (!this._ignoreAllUnknown) {
                    throw ctxt.mappingException("Unexpected JSON values; expected at most " + propCount + " properties (in JSON Array)");
                }
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    jp.skipChildren();
                }
                return bean;
            }
            else {
                final SettableBeanProperty prop = props[i];
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(jp, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, prop.getName(), ctxt);
                    }
                }
                else {
                    jp.skipChildren();
                }
                ++i;
            }
        }
        return bean;
    }
    
    @Override
    public Object deserializeFromObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this._deserializeFromNonArray(jp, ctxt);
    }
    
    protected Object _deserializeNonVanilla(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._nonStandardCreation) {
            return this._deserializeWithCreator(jp, ctxt);
        }
        final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        final Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        final SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        final int propCount = props.length;
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            if (i == propCount) {
                if (!this._ignoreAllUnknown) {
                    throw ctxt.mappingException("Unexpected JSON values; expected at most " + propCount + " properties (in JSON Array)");
                }
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    jp.skipChildren();
                }
                return bean;
            }
            else {
                final SettableBeanProperty prop = props[i];
                ++i;
                Label_0148: {
                    if (prop != null) {
                        if (activeView != null) {
                            if (!prop.visibleInView(activeView)) {
                                break Label_0148;
                            }
                        }
                        try {
                            prop.deserializeAndSet(jp, ctxt, bean);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow(e, bean, prop.getName(), ctxt);
                        }
                        continue;
                    }
                }
                jp.skipChildren();
            }
        }
        return bean;
    }
    
    protected Object _deserializeWithCreator(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingPropertyBased(jp, ctxt);
        }
        if (this._beanType.isAbstract()) {
            throw JsonMappingException.from(jp, "Can not instantiate abstract type " + this._beanType + " (need to add/enable type information?)");
        }
        throw JsonMappingException.from(jp, "No suitable constructor found for type " + this._beanType + ": can not instantiate from JSON object (need to add/enable type information?)");
    }
    
    @Override
    protected final Object _deserializeUsingPropertyBased(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt, this._objectIdReader);
        final SettableBeanProperty[] props = this._orderedProperties;
        final int propCount = props.length;
        int i = 0;
        Object bean = null;
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            final SettableBeanProperty prop = (i < propCount) ? props[i] : null;
            Label_0281: {
                if (prop == null) {
                    jp.skipChildren();
                }
                else if (bean != null) {
                    try {
                        prop.deserializeAndSet(jp, ctxt, bean);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, bean, prop.getName(), ctxt);
                    }
                }
                else {
                    final String propName = prop.getName();
                    final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                    if (creatorProp != null) {
                        final Object value = creatorProp.deserialize(jp, ctxt);
                        if (buffer.assignParameter(creatorProp.getCreatorIndex(), value)) {
                            try {
                                bean = creator.build(ctxt, buffer);
                            }
                            catch (Exception e2) {
                                this.wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                                break Label_0281;
                            }
                            if (bean.getClass() != this._beanType.getRawClass()) {
                                throw ctxt.mappingException("Can not support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type " + this._beanType.getRawClass().getName() + ", actual type " + bean.getClass().getName());
                            }
                        }
                    }
                    else if (!buffer.readIdProperty(propName)) {
                        buffer.bufferProperty(prop, prop.deserialize(jp, ctxt));
                    }
                }
            }
            ++i;
        }
        if (bean == null) {
            try {
                bean = creator.build(ctxt, buffer);
            }
            catch (Exception e3) {
                this.wrapInstantiationProblem(e3, ctxt);
                return null;
            }
        }
        return bean;
    }
    
    protected Object _deserializeFromNonArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        throw ctxt.mappingException("Can not deserialize a POJO (of type " + this._beanType.getRawClass().getName() + ") from non-Array representation (token: " + jp.getCurrentToken() + "): type/property designed to be serialized as JSON Array");
    }
}
