// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.json;

import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializableWithType;

public class JSONWithPadding implements JsonSerializableWithType
{
    public static final String DEFAULT_CALLBACK_NAME = "callback";
    private final String callbackName;
    private final Object jsonSource;
    
    public JSONWithPadding(final Object jsonSource) {
        this(jsonSource, "callback");
    }
    
    public JSONWithPadding(final Object jsonSource, final String callbackName) {
        if (jsonSource == null) {
            throw new IllegalArgumentException("JSON source MUST not be null");
        }
        this.jsonSource = jsonSource;
        this.callbackName = ((callbackName == null) ? "callback" : callbackName);
    }
    
    public String getCallbackName() {
        return this.callbackName;
    }
    
    public Object getJsonSource() {
        return this.jsonSource;
    }
    
    @Override
    public void serialize(final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        if (this.jsonSource == null) {
            provider.getNullValueSerializer().serialize(null, jgen, provider);
        }
        else {
            final Class<?> cls = this.jsonSource.getClass();
            provider.findTypedValueSerializer(cls, true).serialize(this.jsonSource, jgen, provider);
        }
    }
    
    @Override
    public void serializeWithType(final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        this.serialize(jgen, provider);
    }
}
