// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.node;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;

public abstract class BaseJsonNode extends JsonNode implements JsonSerializable
{
    protected BaseJsonNode() {
    }
    
    @Override
    public final JsonNode findPath(final String fieldName) {
        final JsonNode value = this.findValue(fieldName);
        if (value == null) {
            return MissingNode.getInstance();
        }
        return value;
    }
    
    @Override
    public JsonParser traverse() {
        return new TreeTraversingParser(this);
    }
    
    @Override
    public JsonParser traverse(final ObjectCodec codec) {
        return new TreeTraversingParser(this, codec);
    }
    
    @Override
    public abstract JsonToken asToken();
    
    @Override
    public JsonParser.NumberType numberType() {
        return null;
    }
    
    @Override
    public abstract void serialize(final JsonGenerator p0, final SerializerProvider p1) throws IOException, JsonProcessingException;
    
    @Override
    public abstract void serializeWithType(final JsonGenerator p0, final SerializerProvider p1, final TypeSerializer p2) throws IOException, JsonProcessingException;
}
