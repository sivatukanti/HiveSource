// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.json.JSONException;
import java.util.HashMap;
import org.codehaus.jettison.json.JSONArray;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.jettison.json.JSONObject;

public class Node
{
    JSONObject object;
    Map attributes;
    Map namespaces;
    Iterator keys;
    QName name;
    JSONArray array;
    int arrayIndex;
    String currentKey;
    
    public Node(final String name, final JSONObject object, final Convention con) throws JSONException, XMLStreamException {
        this.object = object;
        this.namespaces = new HashMap();
        this.attributes = new HashMap();
        con.processAttributesAndNamespaces(this, object);
        this.keys = object.keys();
        this.name = con.createQName(name, this);
    }
    
    public Node(final String name, final Convention con) throws XMLStreamException {
        this.name = con.createQName(name, this);
        this.namespaces = new HashMap();
        this.attributes = new HashMap();
    }
    
    public Node(final JSONObject object) {
        this.object = object;
        this.namespaces = new HashMap();
        this.attributes = new HashMap();
    }
    
    public int getNamespaceCount() {
        return this.namespaces.size();
    }
    
    public String getNamespaceURI(final String prefix) {
        return this.namespaces.get(prefix);
    }
    
    public String getNamespaceURI(int index) {
        if (index < 0 || index >= this.getNamespaceCount()) {
            throw new IllegalArgumentException("Illegal index: element has " + this.getNamespaceCount() + " namespace declarations");
        }
        final Iterator itr = this.namespaces.values().iterator();
        while (--index >= 0) {
            itr.next();
        }
        return itr.next().toString();
    }
    
    public String getNamespacePrefix(final String URI) {
        for (final Map.Entry e : this.namespaces.entrySet()) {
            if (e.getValue().equals(URI)) {
                return e.getKey();
            }
        }
        return null;
    }
    
    public String getNamespacePrefix(int index) {
        if (index < 0 || index >= this.getNamespaceCount()) {
            throw new IllegalArgumentException("Illegal index: element has " + this.getNamespaceCount() + " namespace declarations");
        }
        final Iterator itr = this.namespaces.keySet().iterator();
        while (--index >= 0) {
            itr.next();
        }
        return itr.next().toString();
    }
    
    public void setNamespaces(final Map namespaces) {
        this.namespaces = namespaces;
    }
    
    public void setNamespace(final String prefix, final String uri) {
        this.namespaces.put(prefix, uri);
    }
    
    public Map getAttributes() {
        return this.attributes;
    }
    
    public void setAttribute(final QName name, final String value) {
        this.attributes.put(name, value);
    }
    
    public Iterator getKeys() {
        return this.keys;
    }
    
    public QName getName() {
        return this.name;
    }
    
    public JSONObject getObject() {
        return this.object;
    }
    
    public void setObject(final JSONObject object) {
        this.object = object;
    }
    
    public JSONArray getArray() {
        return this.array;
    }
    
    public void setArray(final JSONArray array) {
        this.array = array;
    }
    
    public int getArrayIndex() {
        return this.arrayIndex;
    }
    
    public void setArrayIndex(final int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }
    
    public String getCurrentKey() {
        return this.currentKey;
    }
    
    public void setCurrentKey(final String currentKey) {
        this.currentKey = currentKey;
    }
}
