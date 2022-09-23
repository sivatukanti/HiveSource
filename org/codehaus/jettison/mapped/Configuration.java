// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration
{
    private Map xmlToJsonNamespaces;
    private List attributesAsElements;
    private List ignoredElements;
    private boolean supressAtAttributes;
    private String attributeKey;
    private boolean implicitCollections;
    private TypeConverter typeConverter;
    
    public Configuration() {
        this.attributeKey = "@";
        this.implicitCollections = false;
        this.typeConverter = new DefaultConverter();
        this.xmlToJsonNamespaces = new HashMap();
    }
    
    public Configuration(final Map xmlToJsonNamespaces) {
        this.attributeKey = "@";
        this.implicitCollections = false;
        this.typeConverter = new DefaultConverter();
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
    }
    
    public Configuration(final Map xmlToJsonNamespaces, final List attributesAsElements, final List ignoredElements) {
        this.attributeKey = "@";
        this.implicitCollections = false;
        this.typeConverter = new DefaultConverter();
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
        this.attributesAsElements = attributesAsElements;
        this.ignoredElements = ignoredElements;
    }
    
    public List getAttributesAsElements() {
        return this.attributesAsElements;
    }
    
    public void setAttributesAsElements(final List attributesAsElements) {
        this.attributesAsElements = attributesAsElements;
    }
    
    public List getIgnoredElements() {
        return this.ignoredElements;
    }
    
    public void setIgnoredElements(final List ignoredElements) {
        this.ignoredElements = ignoredElements;
    }
    
    public Map getXmlToJsonNamespaces() {
        return this.xmlToJsonNamespaces;
    }
    
    public void setXmlToJsonNamespaces(final Map xmlToJsonNamespaces) {
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
    }
    
    public TypeConverter getTypeConverter() {
        return this.typeConverter;
    }
    
    public void setTypeConverter(final TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }
    
    public boolean isSupressAtAttributes() {
        return this.supressAtAttributes;
    }
    
    public void setSupressAtAttributes(final boolean supressAtAttributes) {
        this.supressAtAttributes = supressAtAttributes;
    }
    
    public String getAttributeKey() {
        return this.attributeKey;
    }
    
    public void setAttributeKey(final String attributeKey) {
        this.attributeKey = attributeKey;
    }
    
    public boolean isImplicitCollections() {
        return this.implicitCollections;
    }
    
    public void setImplicitCollections(final boolean implicitCollections) {
        this.implicitCollections = implicitCollections;
    }
}
