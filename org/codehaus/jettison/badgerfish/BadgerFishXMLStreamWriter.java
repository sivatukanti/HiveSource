// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.badgerfish;

import java.io.IOException;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.XsonNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.jettison.util.FastStack;
import java.io.Writer;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.AbstractXMLStreamWriter;

public class BadgerFishXMLStreamWriter extends AbstractXMLStreamWriter
{
    JSONObject root;
    JSONObject currentNode;
    Writer writer;
    FastStack nodes;
    String currentKey;
    int depth;
    NamespaceContext ctx;
    
    public BadgerFishXMLStreamWriter(final Writer writer) {
        this.nodes = new FastStack();
        this.depth = 0;
        this.ctx = new XsonNamespaceContext(this.nodes);
        this.currentNode = new JSONObject();
        this.root = this.currentNode;
        this.writer = writer;
    }
    
    public void close() throws XMLStreamException {
    }
    
    public void flush() throws XMLStreamException {
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.ctx;
    }
    
    public String getPrefix(final String ns) throws XMLStreamException {
        return this.getNamespaceContext().getPrefix(ns);
    }
    
    public Object getProperty(final String arg0) throws IllegalArgumentException {
        return null;
    }
    
    public void setDefaultNamespace(final String arg0) throws XMLStreamException {
    }
    
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        this.ctx = context;
    }
    
    public void setPrefix(final String arg0, final String arg1) throws XMLStreamException {
    }
    
    public void writeAttribute(final String p, final String ns, final String local, final String value) throws XMLStreamException {
        final String key = this.createAttributeKey(p, ns, local);
        try {
            this.currentNode.put(key, value);
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private String createAttributeKey(final String p, final String ns, final String local) {
        return "@" + this.createKey(p, ns, local);
    }
    
    private String createKey(final String p, final String ns, final String local) {
        if (p == null || p.equals("")) {
            return local;
        }
        return p + ":" + local;
    }
    
    public void writeAttribute(final String ns, final String local, final String value) throws XMLStreamException {
        this.writeAttribute(null, ns, local, value);
    }
    
    public void writeAttribute(final String local, final String value) throws XMLStreamException {
        this.writeAttribute(null, local, value);
    }
    
    public void writeCharacters(final String text) throws XMLStreamException {
        try {
            final Object o = this.currentNode.opt("$");
            if (o instanceof JSONArray) {
                ((JSONArray)o).put(text);
            }
            else if (o instanceof String) {
                final JSONArray arr = new JSONArray();
                arr.put(o);
                arr.put(text);
                this.currentNode.put("$", arr);
            }
            else {
                this.currentNode.put("$", text);
            }
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeDefaultNamespace(final String ns) throws XMLStreamException {
        this.writeNamespace("", ns);
    }
    
    public void writeEndElement() throws XMLStreamException {
        if (this.nodes.size() > 1) {
            this.nodes.pop();
            this.currentNode = ((Node)this.nodes.peek()).getObject();
        }
        --this.depth;
    }
    
    public void writeEntityRef(final String arg0) throws XMLStreamException {
    }
    
    public void writeNamespace(String prefix, final String ns) throws XMLStreamException {
        ((Node)this.nodes.peek()).setNamespace(prefix, ns);
        try {
            JSONObject nsObj = this.currentNode.optJSONObject("@xmlns");
            if (nsObj == null) {
                nsObj = new JSONObject();
                this.currentNode.put("@xmlns", nsObj);
            }
            if (prefix.equals("")) {
                prefix = "$";
            }
            nsObj.put(prefix, ns);
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeProcessingInstruction(final String arg0, final String arg1) throws XMLStreamException {
    }
    
    public void writeProcessingInstruction(final String arg0) throws XMLStreamException {
    }
    
    public void writeStartDocument() throws XMLStreamException {
    }
    
    public void writeEndDocument() throws XMLStreamException {
        try {
            this.root.write(this.writer);
            this.writer.flush();
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
        catch (IOException e2) {
            throw new XMLStreamException(e2);
        }
    }
    
    public void writeStartElement(final String prefix, final String local, final String ns) throws XMLStreamException {
        ++this.depth;
        try {
            this.currentKey = this.createKey(prefix, ns, local);
            final Object existing = this.currentNode.opt(this.currentKey);
            if (existing instanceof JSONObject) {
                final JSONArray array = new JSONArray();
                array.put(existing);
                final JSONObject newCurrent = new JSONObject();
                array.put(newCurrent);
                this.currentNode.put(this.currentKey, array);
                this.currentNode = newCurrent;
                final Node node = new Node(this.currentNode);
                this.nodes.push(node);
            }
            else {
                final JSONObject newCurrent2 = new JSONObject();
                if (existing instanceof JSONArray) {
                    ((JSONArray)existing).put(newCurrent2);
                }
                else {
                    this.currentNode.put(this.currentKey, newCurrent2);
                }
                this.currentNode = newCurrent2;
                final Node node2 = new Node(this.currentNode);
                this.nodes.push(node2);
            }
        }
        catch (JSONException e) {
            throw new XMLStreamException("Could not write start element!", e);
        }
    }
}
