// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.std;

import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.JsonToken;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.type.JavaType;

public class JavaTypeDeserializer extends StdScalarDeserializer<JavaType>
{
    public JavaTypeDeserializer() {
        super(JavaType.class);
    }
    
    @Override
    public JavaType deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING) {
            final String str = jp.getText().trim();
            if (str.length() == 0) {
                return this.getEmptyValue();
            }
            return ctxt.getTypeFactory().constructFromCanonical(str);
        }
        else {
            if (curr == JsonToken.VALUE_EMBEDDED_OBJECT) {
                return (JavaType)jp.getEmbeddedObject();
            }
            throw ctxt.mappingException(this._valueClass);
        }
    }
}
