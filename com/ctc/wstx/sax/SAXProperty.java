// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sax;

import java.util.HashMap;

public final class SAXProperty
{
    public static final String STD_PROPERTY_PREFIX = "http://xml.org/sax/properties/";
    static final HashMap<String, SAXProperty> sInstances;
    public static final SAXProperty DECLARATION_HANDLER;
    public static final SAXProperty DOCUMENT_XML_VERSION;
    public static final SAXProperty DOM_NODE;
    public static final SAXProperty LEXICAL_HANDLER;
    static final SAXProperty XML_STRING;
    private final String mSuffix;
    
    private SAXProperty(final String suffix) {
        this.mSuffix = suffix;
        SAXProperty.sInstances.put(suffix, this);
    }
    
    public static SAXProperty findByUri(final String uri) {
        if (uri.startsWith("http://xml.org/sax/properties/")) {
            return findBySuffix(uri.substring("http://xml.org/sax/properties/".length()));
        }
        return null;
    }
    
    public static SAXProperty findBySuffix(final String suffix) {
        return SAXProperty.sInstances.get(suffix);
    }
    
    public String getSuffix() {
        return this.mSuffix;
    }
    
    @Override
    public String toString() {
        return "http://xml.org/sax/properties/" + this.mSuffix;
    }
    
    static {
        sInstances = new HashMap<String, SAXProperty>();
        DECLARATION_HANDLER = new SAXProperty("declaration-handler");
        DOCUMENT_XML_VERSION = new SAXProperty("document-xml-version");
        DOM_NODE = new SAXProperty("dom-node");
        LEXICAL_HANDLER = new SAXProperty("lexical-handler");
        XML_STRING = new SAXProperty("xml-string");
    }
}
