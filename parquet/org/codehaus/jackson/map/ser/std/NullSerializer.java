// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser.std;

import parquet.org.codehaus.jackson.map.JsonMappingException;
import parquet.org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;

@JacksonStdImpl
public class NullSerializer extends SerializerBase<Object>
{
    public static final NullSerializer instance;
    
    private NullSerializer() {
        super(Object.class);
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeNull();
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        return this.createSchemaNode("null");
    }
    
    static {
        instance = new NullSerializer();
    }
}
