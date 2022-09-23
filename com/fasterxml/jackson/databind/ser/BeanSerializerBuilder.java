// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import java.util.Collections;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.util.List;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.BeanDescription;

public class BeanSerializerBuilder
{
    private static final BeanPropertyWriter[] NO_PROPERTIES;
    protected final BeanDescription _beanDesc;
    protected SerializationConfig _config;
    protected List<BeanPropertyWriter> _properties;
    protected BeanPropertyWriter[] _filteredProperties;
    protected AnyGetterWriter _anyGetter;
    protected Object _filterId;
    protected AnnotatedMember _typeId;
    protected ObjectIdWriter _objectIdWriter;
    
    public BeanSerializerBuilder(final BeanDescription beanDesc) {
        this._properties = Collections.emptyList();
        this._beanDesc = beanDesc;
    }
    
    protected BeanSerializerBuilder(final BeanSerializerBuilder src) {
        this._properties = Collections.emptyList();
        this._beanDesc = src._beanDesc;
        this._properties = src._properties;
        this._filteredProperties = src._filteredProperties;
        this._anyGetter = src._anyGetter;
        this._filterId = src._filterId;
    }
    
    protected void setConfig(final SerializationConfig config) {
        this._config = config;
    }
    
    public void setProperties(final List<BeanPropertyWriter> properties) {
        this._properties = properties;
    }
    
    public void setFilteredProperties(final BeanPropertyWriter[] properties) {
        if (properties != null && properties.length != this._properties.size()) {
            throw new IllegalArgumentException(String.format("Trying to set %d filtered properties; must match length of non-filtered `properties` (%d)", properties.length, this._properties.size()));
        }
        this._filteredProperties = properties;
    }
    
    public void setAnyGetter(final AnyGetterWriter anyGetter) {
        this._anyGetter = anyGetter;
    }
    
    public void setFilterId(final Object filterId) {
        this._filterId = filterId;
    }
    
    public void setTypeId(final AnnotatedMember idProp) {
        if (this._typeId != null) {
            throw new IllegalArgumentException("Multiple type ids specified with " + this._typeId + " and " + idProp);
        }
        this._typeId = idProp;
    }
    
    public void setObjectIdWriter(final ObjectIdWriter w) {
        this._objectIdWriter = w;
    }
    
    public AnnotatedClass getClassInfo() {
        return this._beanDesc.getClassInfo();
    }
    
    public BeanDescription getBeanDescription() {
        return this._beanDesc;
    }
    
    public List<BeanPropertyWriter> getProperties() {
        return this._properties;
    }
    
    public boolean hasProperties() {
        return this._properties != null && this._properties.size() > 0;
    }
    
    public BeanPropertyWriter[] getFilteredProperties() {
        return this._filteredProperties;
    }
    
    public AnyGetterWriter getAnyGetter() {
        return this._anyGetter;
    }
    
    public Object getFilterId() {
        return this._filterId;
    }
    
    public AnnotatedMember getTypeId() {
        return this._typeId;
    }
    
    public ObjectIdWriter getObjectIdWriter() {
        return this._objectIdWriter;
    }
    
    public JsonSerializer<?> build() {
        BeanPropertyWriter[] properties;
        if (this._properties == null || this._properties.isEmpty()) {
            if (this._anyGetter == null && this._objectIdWriter == null) {
                return null;
            }
            properties = BeanSerializerBuilder.NO_PROPERTIES;
        }
        else {
            properties = this._properties.toArray(new BeanPropertyWriter[this._properties.size()]);
            if (this._config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
                for (int i = 0, end = properties.length; i < end; ++i) {
                    properties[i].fixAccess(this._config);
                }
            }
        }
        if (this._filteredProperties != null && this._filteredProperties.length != this._properties.size()) {
            throw new IllegalStateException(String.format("Mismatch between `properties` size (%d), `filteredProperties` (%s): should have as many (or `null` for latter)", this._properties.size(), this._filteredProperties.length));
        }
        if (this._anyGetter != null) {
            this._anyGetter.fixAccess(this._config);
        }
        if (this._typeId != null && this._config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
            this._typeId.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        return new BeanSerializer(this._beanDesc.getType(), this, properties, this._filteredProperties);
    }
    
    public BeanSerializer createDummy() {
        return BeanSerializer.createDummy(this._beanDesc.getType());
    }
    
    static {
        NO_PROPERTIES = new BeanPropertyWriter[0];
    }
}
