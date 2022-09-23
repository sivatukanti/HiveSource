// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public class BeanAsArraySerializer extends BeanSerializerBase
{
    protected final BeanSerializerBase _defaultSerializer;
    
    public BeanAsArraySerializer(final BeanSerializerBase src) {
        super(src, (ObjectIdWriter)null);
        this._defaultSerializer = src;
    }
    
    protected BeanAsArraySerializer(final BeanSerializerBase src, final String[] toIgnore) {
        super(src, toIgnore);
        this._defaultSerializer = src;
    }
    
    protected BeanAsArraySerializer(final BeanSerializerBase src, final ObjectIdWriter oiw, final Object filterId) {
        super(src, oiw, filterId);
        this._defaultSerializer = src;
    }
    
    @Override
    public JsonSerializer<Object> unwrappingSerializer(final NameTransformer transformer) {
        return this._defaultSerializer.unwrappingSerializer(transformer);
    }
    
    @Override
    public boolean isUnwrappingSerializer() {
        return false;
    }
    
    @Override
    public BeanSerializerBase withObjectIdWriter(final ObjectIdWriter objectIdWriter) {
        return this._defaultSerializer.withObjectIdWriter(objectIdWriter);
    }
    
    @Override
    protected BeanSerializerBase withFilterId(final Object filterId) {
        return new BeanAsArraySerializer(this, this._objectIdWriter, filterId);
    }
    
    @Override
    protected BeanAsArraySerializer withIgnorals(final String[] toIgnore) {
        return new BeanAsArraySerializer(this, toIgnore);
    }
    
    @Override
    protected BeanSerializerBase asArraySerializer() {
        return this;
    }
    
    @Override
    public void serializeWithType(final Object bean, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        this._defaultSerializer.serializeWithType(bean, jgen, provider, typeSer);
    }
    
    @Override
    public final void serialize(final Object bean, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) && this.hasSingleElement(provider)) {
            this.serializeAsArray(bean, jgen, provider);
            return;
        }
        jgen.writeStartArray();
        this.serializeAsArray(bean, jgen, provider);
        jgen.writeEndArray();
    }
    
    private boolean hasSingleElement(final SerializerProvider provider) {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        return props.length == 1;
    }
    
    protected final void serializeAsArray(final Object bean, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        int i = 0;
        try {
            for (int len = props.length; i < len; ++i) {
                final BeanPropertyWriter prop = props[i];
                if (prop == null) {
                    jgen.writeNull();
                }
                else {
                    prop.serializeAsElement(bean, jgen, provider);
                }
            }
        }
        catch (Exception e) {
            final String name = (i == props.length) ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, e, bean, name);
        }
        catch (StackOverflowError e2) {
            final JsonMappingException mapE = new JsonMappingException("Infinite recursion (StackOverflowError)", e2);
            final String name2 = (i == props.length) ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name2));
            throw mapE;
        }
    }
    
    @Override
    public String toString() {
        return "BeanAsArraySerializer for " + this.handledType().getName();
    }
}
