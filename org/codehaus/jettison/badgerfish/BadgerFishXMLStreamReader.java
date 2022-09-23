// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.badgerfish;

import javax.xml.namespace.NamespaceContext;
import org.codehaus.jettison.json.JSONArray;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.Convention;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.util.FastStack;
import org.codehaus.jettison.AbstractXMLStreamReader;

public class BadgerFishXMLStreamReader extends AbstractXMLStreamReader
{
    private static final BadgerFishConvention CONVENTION;
    private FastStack nodes;
    private String currentText;
    
    public BadgerFishXMLStreamReader(final JSONObject obj) throws JSONException, XMLStreamException {
        final String rootName = obj.keys().next();
        super.node = new Node(rootName, obj.getJSONObject(rootName), BadgerFishXMLStreamReader.CONVENTION);
        (this.nodes = new FastStack()).push(super.node);
        super.event = 7;
    }
    
    public int next() throws XMLStreamException {
        if (super.event == 7) {
            super.event = 1;
        }
        else {
            if (super.event == 2 && this.nodes.size() != 0) {
                super.node = (Node)this.nodes.peek();
            }
            if (super.node.getArray() != null && super.node.getArray().length() > super.node.getArrayIndex()) {
                final Node arrayNode = super.node;
                int idx = arrayNode.getArrayIndex();
                try {
                    final Object o = arrayNode.getArray().get(idx);
                    this.processKey(super.node.getCurrentKey(), o);
                }
                catch (JSONException e) {
                    throw new XMLStreamException(e);
                }
                ++idx;
                arrayNode.setArrayIndex(idx);
            }
            else if (super.node.getKeys() != null && super.node.getKeys().hasNext()) {
                this.processElement();
            }
            else if (this.nodes.size() != 0) {
                super.event = 2;
                super.node = (Node)this.nodes.pop();
            }
            else {
                super.event = 8;
            }
        }
        return super.event;
    }
    
    private void processElement() throws XMLStreamException {
        try {
            final String nextKey = super.node.getKeys().next();
            final Object newObj = super.node.getObject().get(nextKey);
            this.processKey(nextKey, newObj);
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void processKey(final String nextKey, final Object newObj) throws JSONException, XMLStreamException {
        if (nextKey.equals("$")) {
            super.event = 4;
            this.currentText = (String)newObj;
            return;
        }
        if (newObj instanceof JSONObject) {
            super.node = new Node(nextKey, (JSONObject)newObj, BadgerFishXMLStreamReader.CONVENTION);
            this.nodes.push(super.node);
            super.event = 1;
            return;
        }
        if (newObj instanceof JSONArray) {
            final JSONArray arr = (JSONArray)newObj;
            if (arr.length() == 0) {
                this.next();
                return;
            }
            super.node.setArray(arr);
            super.node.setArrayIndex(1);
            super.node.setCurrentKey(nextKey);
            this.processKey(nextKey, arr.get(0));
        }
    }
    
    public void close() throws XMLStreamException {
    }
    
    public String getAttributeType(final int arg0) {
        return null;
    }
    
    public String getCharacterEncodingScheme() {
        return null;
    }
    
    public String getElementText() throws XMLStreamException {
        return this.currentText;
    }
    
    public NamespaceContext getNamespaceContext() {
        return null;
    }
    
    public String getText() {
        return this.currentText;
    }
    
    static {
        CONVENTION = new BadgerFishConvention();
    }
}
