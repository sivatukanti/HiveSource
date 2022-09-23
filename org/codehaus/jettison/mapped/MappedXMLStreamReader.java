// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import javax.xml.namespace.NamespaceContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.Convention;
import org.codehaus.jettison.Node;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.util.FastStack;
import org.codehaus.jettison.AbstractXMLStreamReader;

public class MappedXMLStreamReader extends AbstractXMLStreamReader
{
    private FastStack nodes;
    private String currentValue;
    private MappedNamespaceConvention convention;
    private String valueKey;
    
    public MappedXMLStreamReader(final JSONObject obj) throws JSONException, XMLStreamException {
        this(obj, new MappedNamespaceConvention());
    }
    
    public MappedXMLStreamReader(final JSONObject obj, final MappedNamespaceConvention con) throws JSONException, XMLStreamException {
        this.valueKey = "$";
        final String rootName = obj.keys().next();
        this.convention = con;
        this.nodes = new FastStack();
        final Object top = obj.get(rootName);
        if (top instanceof JSONObject) {
            super.node = new Node(rootName, (JSONObject)top, this.convention);
        }
        else if (top instanceof JSONArray && (((JSONArray)top).length() != 1 || !((JSONArray)top).get(0).equals(""))) {
            super.node = new Node(rootName, ((JSONArray)top).getJSONObject(0), this.convention);
        }
        else {
            super.node = new Node(rootName, this.convention);
            this.convention.processAttributesAndNamespaces(super.node, obj);
            this.currentValue = top.toString();
        }
        this.nodes.push(super.node);
        super.event = 7;
    }
    
    public int next() throws XMLStreamException {
        if (super.event == 7) {
            super.event = 1;
        }
        else if (super.event == 4) {
            super.event = 2;
            super.node = (Node)this.nodes.pop();
            this.currentValue = null;
        }
        else if (super.event == 1 || super.event == 2) {
            if (super.event == 2 && this.nodes.size() > 0) {
                super.node = (Node)this.nodes.peek();
            }
            if (this.currentValue != null) {
                super.event = 4;
            }
            else if ((super.node.getKeys() != null && super.node.getKeys().hasNext()) || super.node.getArray() != null) {
                this.processElement();
            }
            else if (this.nodes.size() > 0) {
                super.event = 2;
                super.node = (Node)this.nodes.pop();
            }
            else {
                super.event = 8;
            }
        }
        if (this.nodes.size() > 0) {
            final Node next = (Node)this.nodes.peek();
            if (super.event == 1 && next.getName().getLocalPart().equals(this.valueKey)) {
                super.event = 4;
                super.node = (Node)this.nodes.pop();
            }
        }
        return super.event;
    }
    
    private void processElement() throws XMLStreamException {
        try {
            Object newObj = null;
            String nextKey = null;
            if (super.node.getArray() != null) {
                int index = super.node.getArrayIndex();
                if (index >= super.node.getArray().length()) {
                    this.nodes.pop();
                    super.node = (Node)this.nodes.peek();
                    if (super.node == null) {
                        super.event = 8;
                        return;
                    }
                    if ((super.node.getKeys() != null && super.node.getKeys().hasNext()) || super.node.getArray() != null) {
                        this.processElement();
                    }
                    else {
                        super.event = 2;
                        super.node = (Node)this.nodes.pop();
                    }
                    return;
                }
                else {
                    newObj = super.node.getArray().get(index++);
                    nextKey = super.node.getName().getLocalPart();
                    super.node.setArrayIndex(index);
                }
            }
            else {
                nextKey = super.node.getKeys().next();
                newObj = super.node.getObject().get(nextKey);
            }
            if (newObj instanceof String) {
                super.node = new Node(nextKey, this.convention);
                this.nodes.push(super.node);
                this.currentValue = (String)newObj;
                super.event = 1;
                return;
            }
            if (newObj instanceof JSONArray) {
                final JSONArray array = (JSONArray)newObj;
                (super.node = new Node(nextKey, this.convention)).setArray(array);
                super.node.setArrayIndex(0);
                this.nodes.push(super.node);
                this.processElement();
                return;
            }
            if (newObj instanceof JSONObject) {
                super.node = new Node(nextKey, (JSONObject)newObj, this.convention);
                this.nodes.push(super.node);
                super.event = 1;
                return;
            }
            super.node = new Node(nextKey, this.convention);
            this.nodes.push(super.node);
            this.currentValue = newObj.toString();
            super.event = 1;
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void close() throws XMLStreamException {
    }
    
    public String getElementText() throws XMLStreamException {
        super.event = 4;
        return this.currentValue;
    }
    
    public NamespaceContext getNamespaceContext() {
        return null;
    }
    
    public String getText() {
        return this.currentValue;
    }
    
    public int getTextCharacters(final int arg0, final char[] arg1, final int arg2, final int arg3) throws XMLStreamException {
        return 0;
    }
    
    public void setValueKey(final String valueKey) {
        this.valueKey = valueKey;
    }
}
