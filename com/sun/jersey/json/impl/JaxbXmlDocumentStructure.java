// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import java.util.Map;
import java.util.Collection;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

public interface JaxbXmlDocumentStructure
{
    void startElement(final QName p0);
    
    void endElement(final QName p0);
    
    boolean canHandleAttributes();
    
    void handleAttribute(final QName p0, final String p1);
    
    Type getEntityType(final QName p0, final boolean p1);
    
    Type getIndividualType();
    
    Collection<QName> getExpectedAttributes();
    
    Map<String, QName> getExpectedAttributesMap();
    
    Collection<QName> getExpectedElements();
    
    Map<String, QName> getExpectedElementsMap();
    
    boolean isArrayCollection();
    
    boolean isSameArrayCollection();
    
    boolean hasSubElements();
}
