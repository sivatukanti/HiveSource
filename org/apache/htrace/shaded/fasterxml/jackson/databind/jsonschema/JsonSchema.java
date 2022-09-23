// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema;

import org.apache.htrace.shaded.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonValue;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;

@Deprecated
public class JsonSchema
{
    private final ObjectNode schema;
    
    @JsonCreator
    public JsonSchema(final ObjectNode schema) {
        this.schema = schema;
    }
    
    @JsonValue
    public ObjectNode getSchemaNode() {
        return this.schema;
    }
    
    @Override
    public String toString() {
        return this.schema.toString();
    }
    
    @Override
    public int hashCode() {
        return this.schema.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof JsonSchema)) {
            return false;
        }
        final JsonSchema other = (JsonSchema)o;
        if (this.schema == null) {
            return other.schema == null;
        }
        return this.schema.equals(other.schema);
    }
    
    public static JsonNode getDefaultSchemaNode() {
        final ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("type", "any");
        return objectNode;
    }
}
