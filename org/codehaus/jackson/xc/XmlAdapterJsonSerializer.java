// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.xc;

import java.lang.reflect.ParameterizedType;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import java.io.IOException;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.JsonGenerator;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.codehaus.jackson.schema.SchemaAware;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class XmlAdapterJsonSerializer extends SerializerBase<Object> implements SchemaAware
{
    private final XmlAdapter<Object, Object> xmlAdapter;
    
    public XmlAdapterJsonSerializer(final XmlAdapter<Object, Object> xmlAdapter) {
        super(Object.class);
        this.xmlAdapter = xmlAdapter;
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        Object adapted;
        try {
            adapted = this.xmlAdapter.marshal(value);
        }
        catch (Exception e) {
            throw new JsonMappingException("Unable to marshal: " + e.getMessage(), e);
        }
        if (adapted == null) {
            provider.getNullValueSerializer().serialize(null, jgen, provider);
        }
        else {
            final Class<?> c = adapted.getClass();
            provider.findTypedValueSerializer(c, true, null).serialize(adapted, jgen, provider);
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        final JsonSerializer<Object> ser = provider.findValueSerializer(this.findValueClass(), null);
        final JsonNode schemaNode = (ser instanceof SchemaAware) ? ((SchemaAware)ser).getSchema(provider, null) : JsonSchema.getDefaultSchemaNode();
        return schemaNode;
    }
    
    private Class<?> findValueClass() {
        Type superClass;
        for (superClass = this.xmlAdapter.getClass().getGenericSuperclass(); superClass instanceof ParameterizedType && XmlAdapter.class != ((ParameterizedType)superClass).getRawType(); superClass = ((Class)((ParameterizedType)superClass).getRawType()).getGenericSuperclass()) {}
        return (Class<?>)((ParameterizedType)superClass).getActualTypeArguments()[0];
    }
}
