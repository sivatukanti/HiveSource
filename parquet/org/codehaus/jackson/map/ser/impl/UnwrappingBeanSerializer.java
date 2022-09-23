// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser.impl;

import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.JsonSerializer;
import parquet.org.codehaus.jackson.map.ser.std.BeanSerializerBase;

public class UnwrappingBeanSerializer extends BeanSerializerBase
{
    public UnwrappingBeanSerializer(final BeanSerializerBase src) {
        super(src);
    }
    
    @Override
    public JsonSerializer<Object> unwrappingSerializer() {
        return this;
    }
    
    @Override
    public boolean isUnwrappingSerializer() {
        return true;
    }
    
    @Override
    public final void serialize(final Object bean, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        }
        else {
            this.serializeFields(bean, jgen, provider);
        }
    }
    
    @Override
    public String toString() {
        return "UnwrappingBeanSerializer for " + this.handledType().getName();
    }
}
