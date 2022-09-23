// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import java.util.HashSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.BeanDeserializerBase;

public class BeanAsArrayBuilderDeserializer extends BeanDeserializerBase
{
    private static final long serialVersionUID = 1L;
    protected final BeanDeserializerBase _delegate;
    protected final SettableBeanProperty[] _orderedProperties;
    protected final AnnotatedMethod _buildMethod;
    
    public BeanAsArrayBuilderDeserializer(final BeanDeserializerBase delegate, final SettableBeanProperty[] ordered, final AnnotatedMethod buildMethod) {
        super(delegate);
        this._delegate = delegate;
        this._orderedProperties = ordered;
        this._buildMethod = buildMethod;
    }
    
    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer unwrapper) {
        return this._delegate.unwrappingDeserializer(unwrapper);
    }
    
    @Override
    public BeanAsArrayBuilderDeserializer withObjectIdReader(final ObjectIdReader oir) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withObjectIdReader(oir), this._orderedProperties, this._buildMethod);
    }
    
    @Override
    public BeanAsArrayBuilderDeserializer withIgnorableProperties(final HashSet<String> ignorableProps) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withIgnorableProperties(ignorableProps), this._orderedProperties, this._buildMethod);
    }
    
    @Override
    protected BeanAsArrayBuilderDeserializer asArrayDeserializer() {
        return this;
    }
    
    protected final Object finishBuild(final DeserializationContext ctxt, final Object builder) throws IOException {
        try {
            return this._buildMethod.getMember().invoke(builder, new Object[0]);
        }
        catch (Exception e) {
            this.wrapInstantiationProblem(e, ctxt);
            return null;
        }
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
            return this.finishBuild(ctxt, this._deserializeFromNonArray(jp, ctxt));
        }
        if (!this._vanillaProcessing) {
            return this.finishBuild(ctxt, this._deserializeNonVanilla(jp, ctxt));
        }
        Object builder = this._valueInstantiator.createUsingDefault(ctxt);
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
                return this.finishBuild(ctxt, builder);
            }
            else {
                final SettableBeanProperty prop = props[i];
                if (prop != null) {
                    try {
                        builder = prop.deserializeSetAndReturn(jp, ctxt, builder);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, builder, prop.getName(), ctxt);
                    }
                }
                else {
                    jp.skipChildren();
                }
                ++i;
            }
        }
        return this.finishBuild(ctxt, builder);
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt, Object builder) throws IOException, JsonProcessingException {
        if (this._injectables != null) {
            this.injectValues(ctxt, builder);
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
                return this.finishBuild(ctxt, builder);
            }
            else {
                final SettableBeanProperty prop = props[i];
                if (prop != null) {
                    try {
                        builder = prop.deserializeSetAndReturn(jp, ctxt, builder);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, builder, prop.getName(), ctxt);
                    }
                }
                else {
                    jp.skipChildren();
                }
                ++i;
            }
        }
        return this.finishBuild(ctxt, builder);
    }
    
    @Override
    public Object deserializeFromObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this._deserializeFromNonArray(jp, ctxt);
    }
    
    protected Object _deserializeNonVanilla(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._nonStandardCreation) {
            return this._deserializeWithCreator(jp, ctxt);
        }
        final Object builder = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            this.injectValues(ctxt, builder);
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
                return builder;
            }
            else {
                final SettableBeanProperty prop = props[i];
                ++i;
                Label_0149: {
                    if (prop != null) {
                        if (activeView != null) {
                            if (!prop.visibleInView(activeView)) {
                                break Label_0149;
                            }
                        }
                        try {
                            prop.deserializeSetAndReturn(jp, ctxt, builder);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow(e, builder, prop.getName(), ctxt);
                        }
                        continue;
                    }
                }
                jp.skipChildren();
            }
        }
        return builder;
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
        Object builder = null;
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            final SettableBeanProperty prop = (i < propCount) ? props[i] : null;
            Label_0283: {
                if (prop == null) {
                    jp.skipChildren();
                }
                else if (builder != null) {
                    try {
                        builder = prop.deserializeSetAndReturn(jp, ctxt, builder);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow(e, builder, prop.getName(), ctxt);
                    }
                }
                else {
                    final String propName = prop.getName();
                    final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                    if (creatorProp != null) {
                        final Object value = creatorProp.deserialize(jp, ctxt);
                        if (buffer.assignParameter(creatorProp.getCreatorIndex(), value)) {
                            try {
                                builder = creator.build(ctxt, buffer);
                            }
                            catch (Exception e2) {
                                this.wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                                break Label_0283;
                            }
                            if (builder.getClass() != this._beanType.getRawClass()) {
                                throw ctxt.mappingException("Can not support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type " + this._beanType.getRawClass().getName() + ", actual type " + builder.getClass().getName());
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
        if (builder == null) {
            try {
                builder = creator.build(ctxt, buffer);
            }
            catch (Exception e3) {
                this.wrapInstantiationProblem(e3, ctxt);
                return null;
            }
        }
        return builder;
    }
    
    protected Object _deserializeFromNonArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        throw ctxt.mappingException("Can not deserialize a POJO (of type " + this._beanType.getRawClass().getName() + ") from non-Array representation (token: " + jp.getCurrentToken() + "): type/property designed to be serialized as JSON Array");
    }
}
