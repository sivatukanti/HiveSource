// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;

abstract class BaseNodeDeserializer<T extends JsonNode> extends StdDeserializer<T>
{
    public BaseNodeDeserializer(final Class<T> vc) {
        super(vc);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }
    
    protected void _reportProblem(final JsonParser jp, final String msg) throws JsonMappingException {
        throw new JsonMappingException(msg, jp.getTokenLocation());
    }
    
    @Deprecated
    protected void _handleDuplicateField(final String fieldName, final ObjectNode objectNode, final JsonNode oldValue, final JsonNode newValue) throws JsonProcessingException {
    }
    
    protected void _handleDuplicateField(final JsonParser jp, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory, final String fieldName, final ObjectNode objectNode, final JsonNode oldValue, final JsonNode newValue) throws JsonProcessingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
            this._reportProblem(jp, "Duplicate field '" + fieldName + "' for ObjectNode: not allowed when FAIL_ON_READING_DUP_TREE_KEY enabled");
        }
        this._handleDuplicateField(fieldName, objectNode, oldValue, newValue);
    }
    
    protected final ObjectNode deserializeObject(final JsonParser jp, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException, JsonProcessingException {
        final ObjectNode node = nodeFactory.objectNode();
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        while (t == JsonToken.FIELD_NAME) {
            final String fieldName = jp.getCurrentName();
            t = jp.nextToken();
            JsonNode value = null;
            switch (t.id()) {
                case 1: {
                    value = this.deserializeObject(jp, ctxt, nodeFactory);
                    break;
                }
                case 3: {
                    value = this.deserializeArray(jp, ctxt, nodeFactory);
                    break;
                }
                case 6: {
                    value = nodeFactory.textNode(jp.getText());
                    break;
                }
                case 7: {
                    value = this._fromInt(jp, ctxt, nodeFactory);
                    break;
                }
                case 9: {
                    value = nodeFactory.booleanNode(true);
                    break;
                }
                case 10: {
                    value = nodeFactory.booleanNode(false);
                    break;
                }
                case 11: {
                    value = nodeFactory.nullNode();
                    break;
                }
                default: {
                    value = this.deserializeAny(jp, ctxt, nodeFactory);
                    break;
                }
            }
            final JsonNode old = node.replace(fieldName, value);
            if (old != null) {
                this._handleDuplicateField(jp, ctxt, nodeFactory, fieldName, node, old, value);
            }
            t = jp.nextToken();
        }
        return node;
    }
    
    protected final ArrayNode deserializeArray(final JsonParser jp, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException, JsonProcessingException {
        final ArrayNode node = nodeFactory.arrayNode();
        while (true) {
            final JsonToken t = jp.nextToken();
            if (t == null) {
                throw ctxt.mappingException("Unexpected end-of-input when binding data into ArrayNode");
            }
            switch (t.id()) {
                case 1: {
                    node.add(this.deserializeObject(jp, ctxt, nodeFactory));
                    continue;
                }
                case 3: {
                    node.add(this.deserializeArray(jp, ctxt, nodeFactory));
                    continue;
                }
                case 4: {
                    return node;
                }
                case 6: {
                    node.add(nodeFactory.textNode(jp.getText()));
                    continue;
                }
                case 7: {
                    node.add(this._fromInt(jp, ctxt, nodeFactory));
                    continue;
                }
                case 9: {
                    node.add(nodeFactory.booleanNode(true));
                    continue;
                }
                case 10: {
                    node.add(nodeFactory.booleanNode(false));
                    continue;
                }
                case 11: {
                    node.add(nodeFactory.nullNode());
                    continue;
                }
                default: {
                    node.add(this.deserializeAny(jp, ctxt, nodeFactory));
                    continue;
                }
            }
        }
    }
    
    protected final JsonNode deserializeAny(final JsonParser jp, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        switch (jp.getCurrentTokenId()) {
            case 1:
            case 2: {
                return this.deserializeObject(jp, ctxt, nodeFactory);
            }
            case 3: {
                return this.deserializeArray(jp, ctxt, nodeFactory);
            }
            case 5: {
                return this.deserializeObject(jp, ctxt, nodeFactory);
            }
            case 12: {
                return this._fromEmbedded(jp, ctxt, nodeFactory);
            }
            case 6: {
                return nodeFactory.textNode(jp.getText());
            }
            case 7: {
                return this._fromInt(jp, ctxt, nodeFactory);
            }
            case 8: {
                return this._fromFloat(jp, ctxt, nodeFactory);
            }
            case 9: {
                return nodeFactory.booleanNode(true);
            }
            case 10: {
                return nodeFactory.booleanNode(false);
            }
            case 11: {
                return nodeFactory.nullNode();
            }
            default: {
                throw ctxt.mappingException(this.handledType());
            }
        }
    }
    
    protected final JsonNode _fromInt(final JsonParser jp, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final JsonParser.NumberType nt = jp.getNumberType();
        if (nt == JsonParser.NumberType.BIG_INTEGER || ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
            return nodeFactory.numberNode(jp.getBigIntegerValue());
        }
        if (nt == JsonParser.NumberType.INT) {
            return nodeFactory.numberNode(jp.getIntValue());
        }
        return nodeFactory.numberNode(jp.getLongValue());
    }
    
    protected final JsonNode _fromFloat(final JsonParser jp, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final JsonParser.NumberType nt = jp.getNumberType();
        if (nt == JsonParser.NumberType.BIG_DECIMAL || ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            return nodeFactory.numberNode(jp.getDecimalValue());
        }
        return nodeFactory.numberNode(jp.getDoubleValue());
    }
    
    protected final JsonNode _fromEmbedded(final JsonParser jp, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final Object ob = jp.getEmbeddedObject();
        if (ob == null) {
            return nodeFactory.nullNode();
        }
        final Class<?> type = ob.getClass();
        if (type == byte[].class) {
            return nodeFactory.binaryNode((byte[])ob);
        }
        if (JsonNode.class.isAssignableFrom(type)) {
            return (JsonNode)ob;
        }
        return nodeFactory.pojoNode(ob);
    }
}
