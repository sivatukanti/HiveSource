// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.xc;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.JsonGenerator;
import org.w3c.dom.Element;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class DomElementJsonSerializer extends SerializerBase<Element>
{
    public DomElementJsonSerializer() {
        super(Element.class);
    }
    
    @Override
    public void serialize(final Element value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        jgen.writeStringField("name", value.getTagName());
        if (value.getNamespaceURI() != null) {
            jgen.writeStringField("namespace", value.getNamespaceURI());
        }
        final NamedNodeMap attributes = value.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            jgen.writeArrayFieldStart("attributes");
            for (int i = 0; i < attributes.getLength(); ++i) {
                final Attr attribute = (Attr)attributes.item(i);
                jgen.writeStartObject();
                jgen.writeStringField("$", attribute.getValue());
                jgen.writeStringField("name", attribute.getName());
                final String ns = attribute.getNamespaceURI();
                if (ns != null) {
                    jgen.writeStringField("namespace", ns);
                }
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
        }
        final NodeList children = value.getChildNodes();
        if (children != null && children.getLength() > 0) {
            jgen.writeArrayFieldStart("children");
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                switch (child.getNodeType()) {
                    case 3:
                    case 4: {
                        jgen.writeStartObject();
                        jgen.writeStringField("$", child.getNodeValue());
                        jgen.writeEndObject();
                        break;
                    }
                    case 1: {
                        this.serialize((Element)child, jgen, provider);
                        break;
                    }
                }
            }
            jgen.writeEndArray();
        }
        jgen.writeEndObject();
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        final ObjectNode o = this.createSchemaNode("object", true);
        o.put("name", this.createSchemaNode("string"));
        o.put("namespace", this.createSchemaNode("string", true));
        o.put("attributes", this.createSchemaNode("array", true));
        o.put("children", this.createSchemaNode("array", true));
        return o;
    }
}
