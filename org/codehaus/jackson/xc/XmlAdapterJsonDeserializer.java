// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.xc;

import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.JsonParser;
import java.lang.reflect.Type;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.JsonDeserializer;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

public class XmlAdapterJsonDeserializer extends StdDeserializer<Object>
{
    protected static final JavaType ADAPTER_TYPE;
    protected final XmlAdapter<Object, Object> _xmlAdapter;
    protected final JavaType _valueType;
    protected JsonDeserializer<?> _deserializer;
    
    public XmlAdapterJsonDeserializer(final XmlAdapter<Object, Object> xmlAdapter) {
        super(Object.class);
        this._xmlAdapter = xmlAdapter;
        final TypeFactory typeFactory = TypeFactory.defaultInstance();
        final JavaType type = typeFactory.constructType(xmlAdapter.getClass());
        final JavaType[] rawTypes = typeFactory.findTypeParameters(type, XmlAdapter.class);
        this._valueType = ((rawTypes == null || rawTypes.length == 0) ? TypeFactory.unknownType() : rawTypes[0]);
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonDeserializer<?> deser = this._deserializer;
        if (deser == null) {
            final DeserializationConfig config = ctxt.getConfig();
            deser = (this._deserializer = ctxt.getDeserializerProvider().findValueDeserializer(config, this._valueType, null));
        }
        final Object boundObject = deser.deserialize(jp, ctxt);
        try {
            return this._xmlAdapter.unmarshal(boundObject);
        }
        catch (Exception e) {
            throw new JsonMappingException("Unable to unmarshal (to type " + this._valueType + "): " + e.getMessage(), e);
        }
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }
    
    static {
        ADAPTER_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(XmlAdapter.class);
    }
}
