// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;

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
        this._beanDesc = beanDesc;
    }
    
    protected BeanSerializerBuilder(final BeanSerializerBuilder src) {
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
