// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.MapSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;

public class AnyGetterWriter
{
    protected final BeanProperty _property;
    protected final AnnotatedMember _accessor;
    protected MapSerializer _serializer;
    
    public AnyGetterWriter(final BeanProperty property, final AnnotatedMember accessor, final MapSerializer serializer) {
        this._accessor = accessor;
        this._property = property;
        this._serializer = serializer;
    }
    
    public void getAndSerialize(final Object bean, final JsonGenerator jgen, final SerializerProvider provider) throws Exception {
        final Object value = this._accessor.getValue(bean);
        if (value == null) {
            return;
        }
        if (!(value instanceof Map)) {
            throw new JsonMappingException("Value returned by 'any-getter' (" + this._accessor.getName() + "()) not java.util.Map but " + value.getClass().getName());
        }
        this._serializer.serializeFields((Map<?, ?>)value, jgen, provider);
    }
    
    public void getAndFilter(final Object bean, final JsonGenerator jgen, final SerializerProvider provider, final PropertyFilter filter) throws Exception {
        final Object value = this._accessor.getValue(bean);
        if (value == null) {
            return;
        }
        if (!(value instanceof Map)) {
            throw new JsonMappingException("Value returned by 'any-getter' (" + this._accessor.getName() + "()) not java.util.Map but " + value.getClass().getName());
        }
        this._serializer.serializeFilteredFields((Map<?, ?>)value, jgen, provider, filter);
    }
    
    public void resolve(final SerializerProvider provider) throws JsonMappingException {
        this._serializer = (MapSerializer)provider.handlePrimaryContextualization(this._serializer, this._property);
    }
}
