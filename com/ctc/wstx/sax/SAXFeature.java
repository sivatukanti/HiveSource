// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sax;

import java.util.HashMap;

public final class SAXFeature
{
    public static final String STD_FEATURE_PREFIX = "http://xml.org/sax/features/";
    static final HashMap<String, SAXFeature> sInstances;
    static final SAXFeature EXTERNAL_GENERAL_ENTITIES;
    static final SAXFeature EXTERNAL_PARAMETER_ENTITIES;
    static final SAXFeature IS_STANDALONE;
    static final SAXFeature LEXICAL_HANDLER_PARAMETER_ENTITIES;
    static final SAXFeature NAMESPACES;
    static final SAXFeature NAMESPACE_PREFIXES;
    static final SAXFeature RESOLVE_DTD_URIS;
    static final SAXFeature STRING_INTERNING;
    static final SAXFeature UNICODE_NORMALIZATION_CHECKING;
    static final SAXFeature USE_ATTRIBUTES2;
    static final SAXFeature USE_LOCATOR2;
    static final SAXFeature USE_ENTITY_RESOLVER2;
    static final SAXFeature VALIDATION;
    static final SAXFeature XMLNS_URIS;
    static final SAXFeature XML_1_1;
    private final String mSuffix;
    
    private SAXFeature(final String suffix) {
        this.mSuffix = suffix;
        SAXFeature.sInstances.put(suffix, this);
    }
    
    public static SAXFeature findByUri(final String uri) {
        if (uri.startsWith("http://xml.org/sax/features/")) {
            return findBySuffix(uri.substring("http://xml.org/sax/features/".length()));
        }
        return null;
    }
    
    public static SAXFeature findBySuffix(final String suffix) {
        return SAXFeature.sInstances.get(suffix);
    }
    
    public String getSuffix() {
        return this.mSuffix;
    }
    
    @Override
    public String toString() {
        return "http://xml.org/sax/features/" + this.mSuffix;
    }
    
    static {
        sInstances = new HashMap<String, SAXFeature>();
        EXTERNAL_GENERAL_ENTITIES = new SAXFeature("external-general-entities");
        EXTERNAL_PARAMETER_ENTITIES = new SAXFeature("external-parameter-entities");
        IS_STANDALONE = new SAXFeature("is-standalone");
        LEXICAL_HANDLER_PARAMETER_ENTITIES = new SAXFeature("lexical-handler/parameter-entities");
        NAMESPACES = new SAXFeature("namespaces");
        NAMESPACE_PREFIXES = new SAXFeature("namespace-prefixes");
        RESOLVE_DTD_URIS = new SAXFeature("resolve-dtd-uris");
        STRING_INTERNING = new SAXFeature("string-interning");
        UNICODE_NORMALIZATION_CHECKING = new SAXFeature("unicode-normalization-checking");
        USE_ATTRIBUTES2 = new SAXFeature("use-attributes2");
        USE_LOCATOR2 = new SAXFeature("use-locator2");
        USE_ENTITY_RESOLVER2 = new SAXFeature("use-entity-resolver2");
        VALIDATION = new SAXFeature("validation");
        XMLNS_URIS = new SAXFeature("xmlns-uris");
        XML_1_1 = new SAXFeature("xml-1.1");
    }
}
