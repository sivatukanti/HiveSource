// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableAnyProperty;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;

public final class PropertyValueBuffer
{
    protected final JsonParser _parser;
    protected final DeserializationContext _context;
    protected final Object[] _creatorParameters;
    protected final ObjectIdReader _objectIdReader;
    private int _paramsNeeded;
    private PropertyValue _buffered;
    private Object _idValue;
    
    public PropertyValueBuffer(final JsonParser jp, final DeserializationContext ctxt, final int paramCount, final ObjectIdReader oir) {
        this._parser = jp;
        this._context = ctxt;
        this._paramsNeeded = paramCount;
        this._objectIdReader = oir;
        this._creatorParameters = new Object[paramCount];
    }
    
    public void inject(final SettableBeanProperty[] injectableProperties) {
        for (int i = 0, len = injectableProperties.length; i < len; ++i) {
            final SettableBeanProperty prop = injectableProperties[i];
            if (prop != null) {
                this._creatorParameters[i] = this._context.findInjectableValue(prop.getInjectableValueId(), prop, null);
            }
        }
    }
    
    protected final Object[] getParameters(final Object[] defaults) {
        if (defaults != null) {
            for (int i = 0, len = this._creatorParameters.length; i < len; ++i) {
                if (this._creatorParameters[i] == null) {
                    final Object value = defaults[i];
                    if (value != null) {
                        this._creatorParameters[i] = value;
                    }
                }
            }
        }
        return this._creatorParameters;
    }
    
    public boolean readIdProperty(final String propName) throws IOException {
        if (this._objectIdReader != null && propName.equals(this._objectIdReader.propertyName.getSimpleName())) {
            this._idValue = this._objectIdReader.readObjectReference(this._parser, this._context);
            return true;
        }
        return false;
    }
    
    public Object handleIdValue(final DeserializationContext ctxt, final Object bean) throws IOException {
        if (this._objectIdReader != null && this._idValue != null) {
            final ReadableObjectId roid = ctxt.findObjectId(this._idValue, this._objectIdReader.generator, this._objectIdReader.resolver);
            roid.bindItem(bean);
            final SettableBeanProperty idProp = this._objectIdReader.idProperty;
            if (idProp != null) {
                return idProp.setAndReturn(bean, this._idValue);
            }
        }
        return bean;
    }
    
    protected PropertyValue buffered() {
        return this._buffered;
    }
    
    public boolean isComplete() {
        return this._paramsNeeded <= 0;
    }
    
    public boolean assignParameter(final int index, final Object value) {
        this._creatorParameters[index] = value;
        final int paramsNeeded = this._paramsNeeded - 1;
        this._paramsNeeded = paramsNeeded;
        return paramsNeeded <= 0;
    }
    
    public void bufferProperty(final SettableBeanProperty prop, final Object value) {
        this._buffered = new PropertyValue.Regular(this._buffered, value, prop);
    }
    
    public void bufferAnyProperty(final SettableAnyProperty prop, final String propName, final Object value) {
        this._buffered = new PropertyValue.Any(this._buffered, value, prop, propName);
    }
    
    public void bufferMapProperty(final Object key, final Object value) {
        this._buffered = new PropertyValue.Map(this._buffered, value, key);
    }
}
