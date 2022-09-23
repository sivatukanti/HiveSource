// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.UnwrappingBeanSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public class BeanSerializer extends BeanSerializerBase
{
    public BeanSerializer(final JavaType type, final BeanSerializerBuilder builder, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(type, builder, properties, filteredProperties);
    }
    
    protected BeanSerializer(final BeanSerializerBase src) {
        super(src);
    }
    
    protected BeanSerializer(final BeanSerializerBase src, final ObjectIdWriter objectIdWriter) {
        super(src, objectIdWriter);
    }
    
    protected BeanSerializer(final BeanSerializerBase src, final ObjectIdWriter objectIdWriter, final Object filterId) {
        super(src, objectIdWriter, filterId);
    }
    
    protected BeanSerializer(final BeanSerializerBase src, final String[] toIgnore) {
        super(src, toIgnore);
    }
    
    public static BeanSerializer createDummy(final JavaType forType) {
        return new BeanSerializer(forType, null, BeanSerializer.NO_PROPS, null);
    }
    
    @Override
    public JsonSerializer<Object> unwrappingSerializer(final NameTransformer unwrapper) {
        return new UnwrappingBeanSerializer(this, unwrapper);
    }
    
    @Override
    public BeanSerializerBase withObjectIdWriter(final ObjectIdWriter objectIdWriter) {
        return new BeanSerializer(this, objectIdWriter, this._propertyFilterId);
    }
    
    @Override
    protected BeanSerializerBase withFilterId(final Object filterId) {
        return new BeanSerializer(this, this._objectIdWriter, filterId);
    }
    
    @Override
    protected BeanSerializerBase withIgnorals(final String[] toIgnore) {
        return new BeanSerializer(this, toIgnore);
    }
    
    @Override
    protected BeanSerializerBase asArraySerializer() {
        if (this._objectIdWriter == null && this._anyGetterWriter == null && this._propertyFilterId == null) {
            return new BeanAsArraySerializer(this);
        }
        return this;
    }
    
    @Override
    public final void serialize(final Object bean, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._objectIdWriter != null) {
            this._serializeWithObjectId(bean, jgen, provider, true);
            return;
        }
        jgen.writeStartObject();
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        }
        else {
            this.serializeFields(bean, jgen, provider);
        }
        jgen.writeEndObject();
    }
    
    @Override
    public String toString() {
        return "BeanSerializer for " + this.handledType().getName();
    }
}
