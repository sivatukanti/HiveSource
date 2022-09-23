// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;

public class JsonNamespaceContext implements NamespaceContext
{
    static final String PrefixPREFIX = "ns";
    private final Map<String, String> ns2pMap;
    private final Map<String, String> p2nsMap;
    private int nsCount;
    
    public JsonNamespaceContext() {
        this.ns2pMap = new HashMap<String, String>();
        this.p2nsMap = new HashMap<String, String>();
        this.nsCount = 0;
    }
    
    protected int getNamespaceCount() {
        return this.nsCount;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        if ("".equals(prefix)) {
            return "";
        }
        if (this.p2nsMap.containsKey(prefix)) {
            return this.p2nsMap.get(prefix);
        }
        return null;
    }
    
    @Override
    public String getPrefix(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        if ("".equals(namespaceURI)) {
            return "";
        }
        if (this.ns2pMap.containsKey(namespaceURI)) {
            return this.ns2pMap.get(namespaceURI);
        }
        final String newPrefix = "ns" + ++this.nsCount;
        this.ns2pMap.put(namespaceURI, newPrefix);
        this.p2nsMap.put(newPrefix, namespaceURI);
        return newPrefix;
    }
    
    @Override
    public Iterator getPrefixes(final String namespaceURI) {
        return this.p2nsMap.keySet().iterator();
    }
}
