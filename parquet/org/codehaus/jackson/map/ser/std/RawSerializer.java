// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser.std;

import parquet.org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import parquet.org.codehaus.jackson.JsonProcessingException;
import parquet.org.codehaus.jackson.map.TypeSerializer;
import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;

@JacksonStdImpl
public class RawSerializer<T> extends SerializerBase<T>
{
    public RawSerializer(final Class<?> cls) {
        super(cls, false);
    }
    
    @Override
    public void serialize(final T value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeRawValue(value.toString());
    }
    
    @Override
    public void serializeWithType(final T value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForScalar(value, jgen);
        this.serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForScalar(value, jgen);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("string", true);
    }
}
