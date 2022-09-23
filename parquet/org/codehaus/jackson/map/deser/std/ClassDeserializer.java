// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.std;

import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.JsonToken;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;

@JacksonStdImpl
public class ClassDeserializer extends StdScalarDeserializer<Class<?>>
{
    public ClassDeserializer() {
        super(Class.class);
    }
    
    @Override
    public Class<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING) {
            final String className = jp.getText();
            if (className.indexOf(46) < 0) {
                if ("int".equals(className)) {
                    return Integer.TYPE;
                }
                if ("long".equals(className)) {
                    return Long.TYPE;
                }
                if ("float".equals(className)) {
                    return Float.TYPE;
                }
                if ("double".equals(className)) {
                    return Double.TYPE;
                }
                if ("boolean".equals(className)) {
                    return Boolean.TYPE;
                }
                if ("byte".equals(className)) {
                    return Byte.TYPE;
                }
                if ("char".equals(className)) {
                    return Character.TYPE;
                }
                if ("short".equals(className)) {
                    return Short.TYPE;
                }
                if ("void".equals(className)) {
                    return Void.TYPE;
                }
            }
            try {
                return Class.forName(jp.getText());
            }
            catch (ClassNotFoundException e) {
                throw ctxt.instantiationException(this._valueClass, e);
            }
        }
        throw ctxt.mappingException(this._valueClass, curr);
    }
}
