// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.util.Iterator;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiator;

public final class PropertyBasedCreator
{
    protected final ValueInstantiator _valueInstantiator;
    protected final HashMap<String, SettableBeanProperty> _properties;
    protected final int _propertyCount;
    protected final Object[] _defaultValues;
    protected final SettableBeanProperty[] _propertiesWithInjectables;
    
    protected PropertyBasedCreator(final ValueInstantiator valueInstantiator, final SettableBeanProperty[] creatorProps, final Object[] defaultValues) {
        this._valueInstantiator = valueInstantiator;
        this._properties = new HashMap<String, SettableBeanProperty>();
        SettableBeanProperty[] propertiesWithInjectables = null;
        final int len = creatorProps.length;
        this._propertyCount = len;
        for (int i = 0; i < len; ++i) {
            final SettableBeanProperty prop = creatorProps[i];
            this._properties.put(prop.getName(), prop);
            final Object injectableValueId = prop.getInjectableValueId();
            if (injectableValueId != null) {
                if (propertiesWithInjectables == null) {
                    propertiesWithInjectables = new SettableBeanProperty[len];
                }
                propertiesWithInjectables[i] = prop;
            }
        }
        this._defaultValues = defaultValues;
        this._propertiesWithInjectables = propertiesWithInjectables;
    }
    
    public static PropertyBasedCreator construct(final DeserializationContext ctxt, final ValueInstantiator valueInstantiator, final SettableBeanProperty[] srcProps) throws JsonMappingException {
        final int len = srcProps.length;
        final SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];
        Object[] defaultValues = null;
        for (int i = 0; i < len; ++i) {
            SettableBeanProperty prop = srcProps[i];
            if (!prop.hasValueDeserializer()) {
                prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
            }
            creatorProps[i] = prop;
            final JsonDeserializer<?> deser = prop.getValueDeserializer();
            Object nullValue = (deser == null) ? null : deser.getNullValue();
            if (nullValue == null && prop.getType().isPrimitive()) {
                nullValue = ClassUtil.defaultValue(prop.getType().getRawClass());
            }
            if (nullValue != null) {
                if (defaultValues == null) {
                    defaultValues = new Object[len];
                }
                defaultValues[i] = nullValue;
            }
        }
        return new PropertyBasedCreator(valueInstantiator, creatorProps, defaultValues);
    }
    
    public void assignDeserializer(SettableBeanProperty prop, final JsonDeserializer<Object> deser) {
        prop = prop.withValueDeserializer(deser);
        this._properties.put(prop.getName(), prop);
    }
    
    public Collection<SettableBeanProperty> properties() {
        return this._properties.values();
    }
    
    public SettableBeanProperty findCreatorProperty(final String name) {
        return this._properties.get(name);
    }
    
    public SettableBeanProperty findCreatorProperty(final int propertyIndex) {
        for (final SettableBeanProperty prop : this._properties.values()) {
            if (prop.getPropertyIndex() == propertyIndex) {
                return prop;
            }
        }
        return null;
    }
    
    public PropertyValueBuffer startBuilding(final JsonParser jp, final DeserializationContext ctxt, final ObjectIdReader oir) {
        final PropertyValueBuffer buffer = new PropertyValueBuffer(jp, ctxt, this._propertyCount, oir);
        if (this._propertiesWithInjectables != null) {
            buffer.inject(this._propertiesWithInjectables);
        }
        return buffer;
    }
    
    public Object build(final DeserializationContext ctxt, final PropertyValueBuffer buffer) throws IOException {
        Object bean = this._valueInstantiator.createFromObjectWith(ctxt, buffer.getParameters(this._defaultValues));
        bean = buffer.handleIdValue(ctxt, bean);
        for (PropertyValue pv = buffer.buffered(); pv != null; pv = pv.next) {
            pv.assign(bean);
        }
        return bean;
    }
}
