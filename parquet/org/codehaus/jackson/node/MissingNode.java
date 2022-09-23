// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.node;

import parquet.org.codehaus.jackson.map.TypeSerializer;
import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.JsonNode;
import parquet.org.codehaus.jackson.JsonToken;

public final class MissingNode extends BaseJsonNode
{
    private static final MissingNode instance;
    
    private MissingNode() {
    }
    
    public static MissingNode getInstance() {
        return MissingNode.instance;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.NOT_AVAILABLE;
    }
    
    @Override
    public boolean isMissingNode() {
        return true;
    }
    
    @Override
    public String asText() {
        return "";
    }
    
    @Override
    public boolean asBoolean(final boolean defaultValue) {
        return defaultValue;
    }
    
    @Override
    public int asInt(final int defaultValue) {
        return defaultValue;
    }
    
    @Override
    public long asLong(final long defaultValue) {
        return defaultValue;
    }
    
    @Override
    public double asDouble(final double defaultValue) {
        return defaultValue;
    }
    
    @Override
    public JsonNode path(final String fieldName) {
        return this;
    }
    
    @Override
    public JsonNode path(final int index) {
        return this;
    }
    
    @Override
    public final void serialize(final JsonGenerator jg, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNull();
    }
    
    @Override
    public void serializeWithType(final JsonGenerator jg, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        jg.writeNull();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this;
    }
    
    @Override
    public String toString() {
        return "";
    }
    
    static {
        instance = new MissingNode();
    }
}
