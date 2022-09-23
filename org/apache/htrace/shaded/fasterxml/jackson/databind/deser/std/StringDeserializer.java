// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variants;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;

@JacksonStdImpl
public final class StringDeserializer extends StdScalarDeserializer<String>
{
    private static final long serialVersionUID = 1L;
    public static final StringDeserializer instance;
    
    public StringDeserializer() {
        super(String.class);
    }
    
    @Override
    public String deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING) {
            return jp.getText();
        }
        if (curr == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            jp.nextToken();
            final String parsed = this._parseString(jp, ctxt);
            if (jp.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'String' value but there was more than a single value in the array");
            }
            return parsed;
        }
        else if (curr == JsonToken.VALUE_EMBEDDED_OBJECT) {
            final Object ob = jp.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (ob instanceof byte[]) {
                return Base64Variants.getDefaultVariant().encode((byte[])ob, false);
            }
            return ob.toString();
        }
        else {
            final String text = jp.getValueAsString();
            if (text != null) {
                return text;
            }
            throw ctxt.mappingException(this._valueClass, curr);
        }
    }
    
    @Override
    public String deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return this.deserialize(jp, ctxt);
    }
    
    static {
        instance = new StringDeserializer();
    }
}
