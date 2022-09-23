// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.NullNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;

public class JsonNodeDeserializer extends BaseNodeDeserializer<JsonNode>
{
    private static final JsonNodeDeserializer instance;
    
    protected JsonNodeDeserializer() {
        super(JsonNode.class);
    }
    
    public static JsonDeserializer<? extends JsonNode> getDeserializer(final Class<?> nodeClass) {
        if (nodeClass == ObjectNode.class) {
            return ObjectDeserializer.getInstance();
        }
        if (nodeClass == ArrayNode.class) {
            return ArrayDeserializer.getInstance();
        }
        return JsonNodeDeserializer.instance;
    }
    
    @Override
    public JsonNode getNullValue() {
        return NullNode.getInstance();
    }
    
    @Override
    public JsonNode deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        switch (jp.getCurrentTokenId()) {
            case 1: {
                return this.deserializeObject(jp, ctxt, ctxt.getNodeFactory());
            }
            case 3: {
                return this.deserializeArray(jp, ctxt, ctxt.getNodeFactory());
            }
            default: {
                return this.deserializeAny(jp, ctxt, ctxt.getNodeFactory());
            }
        }
    }
    
    static {
        instance = new JsonNodeDeserializer();
    }
    
    static final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode>
    {
        private static final long serialVersionUID = 1L;
        protected static final ObjectDeserializer _instance;
        
        protected ObjectDeserializer() {
            super(ObjectNode.class);
        }
        
        public static ObjectDeserializer getInstance() {
            return ObjectDeserializer._instance;
        }
        
        @Override
        public ObjectNode deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
                jp.nextToken();
                return this.deserializeObject(jp, ctxt, ctxt.getNodeFactory());
            }
            if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {
                return this.deserializeObject(jp, ctxt, ctxt.getNodeFactory());
            }
            throw ctxt.mappingException(ObjectNode.class);
        }
        
        static {
            _instance = new ObjectDeserializer();
        }
    }
    
    static final class ArrayDeserializer extends BaseNodeDeserializer<ArrayNode>
    {
        private static final long serialVersionUID = 1L;
        protected static final ArrayDeserializer _instance;
        
        protected ArrayDeserializer() {
            super(ArrayNode.class);
        }
        
        public static ArrayDeserializer getInstance() {
            return ArrayDeserializer._instance;
        }
        
        @Override
        public ArrayNode deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.isExpectedStartArrayToken()) {
                return this.deserializeArray(jp, ctxt, ctxt.getNodeFactory());
            }
            throw ctxt.mappingException(ArrayNode.class);
        }
        
        static {
            _instance = new ArrayDeserializer();
        }
    }
}
