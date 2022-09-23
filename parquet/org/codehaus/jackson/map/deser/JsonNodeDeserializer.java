// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser;

import parquet.org.codehaus.jackson.JsonNode;
import parquet.org.codehaus.jackson.node.ArrayNode;
import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.node.ObjectNode;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;

@Deprecated
public class JsonNodeDeserializer extends parquet.org.codehaus.jackson.map.deser.std.JsonNodeDeserializer
{
    @Deprecated
    public static final JsonNodeDeserializer instance;
    
    @Deprecated
    protected final ObjectNode deserializeObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this.deserializeObject(jp, ctxt, ctxt.getNodeFactory());
    }
    
    @Deprecated
    protected final ArrayNode deserializeArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this.deserializeArray(jp, ctxt, ctxt.getNodeFactory());
    }
    
    @Deprecated
    protected final JsonNode deserializeAny(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this.deserializeAny(jp, ctxt, ctxt.getNodeFactory());
    }
    
    static {
        instance = new JsonNodeDeserializer();
    }
}
