// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.xc;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import org.w3c.dom.Document;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.JsonParser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

public class DomElementJsonDeserializer extends StdDeserializer<Element>
{
    private final DocumentBuilder builder;
    
    public DomElementJsonDeserializer() {
        super(Element.class);
        try {
            final DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
            bf.setNamespaceAware(true);
            this.builder = bf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException();
        }
    }
    
    public DomElementJsonDeserializer(final DocumentBuilder builder) {
        super(Element.class);
        this.builder = builder;
    }
    
    @Override
    public Element deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final Document document = this.builder.newDocument();
        return this.fromNode(document, jp.readValueAsTree());
    }
    
    protected Element fromNode(final Document document, final JsonNode jsonNode) throws IOException {
        String ns = (jsonNode.get("namespace") != null) ? jsonNode.get("namespace").asText() : null;
        String name = (jsonNode.get("name") != null) ? jsonNode.get("name").asText() : null;
        if (name == null) {
            throw new JsonMappingException("No name for DOM element was provided in the JSON object.");
        }
        final Element element = document.createElementNS(ns, name);
        final JsonNode attributesNode = jsonNode.get("attributes");
        if (attributesNode != null && attributesNode instanceof ArrayNode) {
            final Iterator<JsonNode> atts = attributesNode.getElements();
            while (atts.hasNext()) {
                final JsonNode node = atts.next();
                ns = ((node.get("namespace") != null) ? node.get("namespace").asText() : null);
                name = ((node.get("name") != null) ? node.get("name").asText() : null);
                final String value = (node.get("$") != null) ? node.get("$").asText() : null;
                if (name != null) {
                    element.setAttributeNS(ns, name, value);
                }
            }
        }
        final JsonNode childsNode = jsonNode.get("children");
        if (childsNode != null && childsNode instanceof ArrayNode) {
            final Iterator<JsonNode> els = childsNode.getElements();
            while (els.hasNext()) {
                final JsonNode node2 = els.next();
                name = ((node2.get("name") != null) ? node2.get("name").asText() : null);
                final String value2 = (node2.get("$") != null) ? node2.get("$").asText() : null;
                if (value2 != null) {
                    element.appendChild(document.createTextNode(value2));
                }
                else {
                    if (name == null) {
                        continue;
                    }
                    element.appendChild(this.fromNode(document, node2));
                }
            }
        }
        return element;
    }
}
