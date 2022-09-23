// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import java.io.IOException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONArray;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.jettison.util.FastStack;
import java.io.Writer;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.AbstractXMLStreamWriter;

public class MappedXMLStreamWriter extends AbstractXMLStreamWriter
{
    MappedNamespaceConvention convention;
    JSONObject root;
    Object current;
    Writer writer;
    FastStack nodes;
    String currentKey;
    NamespaceContext ctx;
    String valueKey;
    
    public MappedXMLStreamWriter(final MappedNamespaceConvention convention, final Writer writer) {
        this.nodes = new FastStack();
        this.ctx = new NullNamespaceContext();
        this.valueKey = "$";
        this.convention = convention;
        this.writer = writer;
    }
    
    public void close() throws XMLStreamException {
    }
    
    public void flush() throws XMLStreamException {
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.ctx;
    }
    
    public String getPrefix(final String arg0) throws XMLStreamException {
        return null;
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
        if (this.convention.isElement(p, ns, local)) {
            this.writeStartElement(p, local, ns);
            this.writeCharacters(value);
            this.writeEndElement();
            return;
        }
        final String key = this.convention.createAttributeKey(p, ns, local);
        try {
            if (this.current instanceof JSONArray) {
                final JSONArray array = (JSONArray)this.current;
                this.current = array.get(array.length() - 1);
            }
            this.makeCurrentJSONObject();
            if (this.current instanceof JSONObject) {
                final Object o = ((JSONObject)this.current).opt(key);
                if (o == null) {
                    ((JSONObject)this.current).put(key, value);
                }
            }
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void makeCurrentJSONObject() throws JSONException {
        if (this.current.equals("")) {
            final JSONObject newCurrent = new JSONObject();
            this.setNewValue(newCurrent);
            this.current = newCurrent;
            this.nodes.push(newCurrent);
        }
    }
    
    private void setNewValue(final Object newCurrent) throws JSONException {
        if (this.isJsonPrimitive(this.current)) {
            this.setNewValue(newCurrent, this.nodes.peek());
        }
        else {
            this.setNewValue(newCurrent, this.current);
        }
    }
    
    private void setNewValue(final Object newCurrent, final Object node) throws JSONException {
        if (node instanceof JSONObject) {
            ((JSONObject)node).put(this.currentKey, newCurrent);
        }
        else if (node instanceof JSONArray) {
            final JSONArray arr = (JSONArray)node;
            arr.put(arr.length() - 1, newCurrent);
        }
        this.current = newCurrent;
    }
    
    public void writeAttribute(final String ns, final String local, final String value) throws XMLStreamException {
        this.writeAttribute(null, ns, local, value);
    }
    
    public void writeAttribute(final String local, final String value) throws XMLStreamException {
        this.writeAttribute(null, local, value);
    }
    
    public void writeCharacters(final String text) throws XMLStreamException {
        try {
            final Object convertedPrimitive = this.convention.convertToJSONPrimitive(text);
            if (this.isJsonPrimitive(this.current)) {
                if (this.current instanceof String && convertedPrimitive instanceof String) {
                    this.current += convertedPrimitive.toString();
                }
                else {
                    this.current = convertedPrimitive;
                }
                this.setNewValue(this.current);
            }
            else if (this.current instanceof JSONArray) {
                final JSONArray arr = (JSONArray)this.current;
                if (arr.get(arr.length() - 1).equals("")) {
                    arr.put(arr.length() - 1, convertedPrimitive);
                }
                else {
                    arr.put(convertedPrimitive);
                }
                this.current = convertedPrimitive;
            }
            else if (this.current instanceof JSONObject && this.valueKey != null) {
                final JSONObject obj = (JSONObject)this.current;
                obj.put(this.valueKey, text);
            }
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeComment(final String arg0) throws XMLStreamException {
    }
    
    public void writeDefaultNamespace(final String ns) throws XMLStreamException {
    }
    
    public void writeDTD(final String arg0) throws XMLStreamException {
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
    
    public void writeEndElement() throws XMLStreamException {
        if (this.isJsonPrimitive(this.current)) {
            this.current = this.nodes.peek();
        }
        else if (this.nodes.size() > 1) {
            if (this.isEmptyArray(this.current)) {
                this.current = this.nodes.peek();
            }
            else {
                final Object isArray = this.nodes.pop();
                this.current = this.nodes.peek();
                if (this.current instanceof JSONArray || isArray instanceof JSONArray) {
                    this.nodes.pop();
                    if (this.nodes.size() > 0) {
                        this.current = this.nodes.peek();
                    }
                }
            }
        }
    }
    
    public void writeEntityRef(final String arg0) throws XMLStreamException {
    }
    
    public void writeNamespace(final String arg0, final String arg1) throws XMLStreamException {
    }
    
    public void writeProcessingInstruction(final String arg0, final String arg1) throws XMLStreamException {
    }
    
    public void writeProcessingInstruction(final String arg0) throws XMLStreamException {
    }
    
    public void writeStartDocument() throws XMLStreamException {
    }
    
    public void writeStartElement(final String prefix, final String local, final String ns) throws XMLStreamException {
        try {
            if (this.current == null) {
                this.root = new JSONObject();
                this.current = this.root;
                this.nodes.push(this.root);
            }
            else {
                this.makeCurrentJSONObject();
            }
            final String previousKey = this.currentKey;
            this.currentKey = this.convention.createKey(prefix, ns, local);
            if (this.current instanceof JSONArray) {
                final JSONArray array = (JSONArray)this.current;
                if (array.get(array.length() - 1).equals("")) {
                    if (this.getSerializedAsArrays().contains(this.currentKey)) {
                        final JSONObject newNode = new JSONObject();
                        newNode.put(this.currentKey, "");
                        this.setNewValue(newNode);
                        this.nodes.push(newNode);
                        this.current = "";
                        final JSONArray arr = new JSONArray();
                        arr.put("");
                        this.setNewValue(arr);
                    }
                    else {
                        final JSONObject newNode = new JSONObject();
                        newNode.put(this.currentKey, "");
                        this.setNewValue(newNode);
                        this.nodes.push(newNode);
                        this.current = "";
                    }
                }
                else if (array.get(array.length() - 1) instanceof JSONObject) {
                    array.put("");
                    this.nodes.push(array);
                }
                else if (!this.currentKey.equals(previousKey)) {
                    int i = 0;
                    while (!(this.current instanceof JSONObject)) {
                        if (i > 0) {
                            this.nodes.pop();
                        }
                        this.current = this.nodes.peek();
                        ++i;
                    }
                    this.setNewValue("");
                    this.current = "";
                }
            }
            else {
                Object o = ((JSONObject)this.current).opt(this.currentKey);
                if (o == null && this.nodes.size() > 2) {
                    final Object next = this.nodes.get(this.nodes.size() - 2);
                    if (next instanceof JSONObject) {
                        final Object maybe = ((JSONObject)next).opt(this.currentKey);
                        if (maybe != null && maybe instanceof JSONObject) {
                            o = maybe;
                            this.nodes.pop();
                            this.current = this.nodes.pop();
                        }
                    }
                }
                if (o instanceof JSONObject || this.isJsonPrimitive(o)) {
                    final JSONArray arr2 = new JSONArray();
                    arr2.put(o);
                    arr2.put("");
                    this.setNewValue(arr2);
                    this.current = arr2;
                    this.nodes.push(arr2);
                }
                else if (o instanceof JSONArray) {
                    this.current = "";
                    ((JSONArray)o).put("");
                    this.nodes.push(o);
                }
                else if (this.getSerializedAsArrays().contains(this.currentKey)) {
                    final JSONArray arr2 = new JSONArray();
                    arr2.put("");
                    this.setNewValue(arr2);
                }
                else {
                    this.setNewValue("");
                    this.current = "";
                }
            }
        }
        catch (JSONException e) {
            throw new XMLStreamException("Could not write start element!", e);
        }
    }
    
    private boolean isJsonPrimitive(final Object o) {
        return o instanceof String || o instanceof Boolean || o instanceof Number;
    }
    
    private boolean isEmptyArray(final Object o) throws XMLStreamException {
        if (o instanceof JSONArray) {
            final JSONArray arr = (JSONArray)o;
            if (arr.length() == 1) {
                try {
                    return arr.get(0).equals("");
                }
                catch (JSONException e) {
                    throw new XMLStreamException("Could not read array value!", e);
                }
            }
        }
        return false;
    }
    
    public void setValueKey(final String valueKey) {
        this.valueKey = valueKey;
    }
}
