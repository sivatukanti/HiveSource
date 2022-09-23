// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import java.util.Collections;
import java.util.Iterator;
import org.codehaus.jackson.node.TextNode;
import java.util.LinkedHashMap;
import java.util.Set;
import org.codehaus.jackson.JsonNode;
import java.util.Map;

public abstract class JsonProperties
{
    Map<String, JsonNode> props;
    private Set<String> reserved;
    
    JsonProperties(final Set<String> reserved) {
        this.props = new LinkedHashMap<String, JsonNode>(1);
        this.reserved = reserved;
    }
    
    public String getProp(final String name) {
        final JsonNode value = this.getJsonProp(name);
        return (value != null && value.isTextual()) ? value.getTextValue() : null;
    }
    
    public synchronized JsonNode getJsonProp(final String name) {
        return this.props.get(name);
    }
    
    public void addProp(final String name, final String value) {
        this.addProp(name, TextNode.valueOf(value));
    }
    
    public synchronized void addProp(final String name, final JsonNode value) {
        if (this.reserved.contains(name)) {
            throw new AvroRuntimeException("Can't set reserved property: " + name);
        }
        if (value == null) {
            throw new AvroRuntimeException("Can't set a property to null: " + name);
        }
        final JsonNode old = this.props.get(name);
        if (old == null) {
            this.props.put(name, value);
        }
        else if (!old.equals(value)) {
            throw new AvroRuntimeException("Can't overwrite property: " + name);
        }
    }
    
    @Deprecated
    public Map<String, String> getProps() {
        final Map<String, String> result = new LinkedHashMap<String, String>();
        for (final Map.Entry<String, JsonNode> e : this.props.entrySet()) {
            if (e.getValue().isTextual()) {
                result.put(e.getKey(), e.getValue().getTextValue());
            }
        }
        return result;
    }
    
    Map<String, JsonNode> jsonProps(final Map<String, String> stringProps) {
        final Map<String, JsonNode> result = new LinkedHashMap<String, JsonNode>();
        for (final Map.Entry<String, String> e : stringProps.entrySet()) {
            result.put(e.getKey(), TextNode.valueOf(e.getValue()));
        }
        return result;
    }
    
    public Map<String, JsonNode> getJsonProps() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends JsonNode>)this.props);
    }
    
    void writeProps(final JsonGenerator gen) throws IOException {
        for (final Map.Entry<String, JsonNode> e : this.props.entrySet()) {
            gen.writeObjectField(e.getKey(), e.getValue());
        }
    }
}
