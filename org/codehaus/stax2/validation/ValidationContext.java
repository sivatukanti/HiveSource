// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;

public interface ValidationContext
{
    String getXmlVersion();
    
    QName getCurrentElementName();
    
    String getNamespaceURI(final String p0);
    
    int getAttributeCount();
    
    String getAttributeLocalName(final int p0);
    
    String getAttributeNamespace(final int p0);
    
    String getAttributePrefix(final int p0);
    
    String getAttributeValue(final int p0);
    
    String getAttributeValue(final String p0, final String p1);
    
    String getAttributeType(final int p0);
    
    int findAttributeIndex(final String p0, final String p1);
    
    boolean isNotationDeclared(final String p0);
    
    boolean isUnparsedEntityDeclared(final String p0);
    
    String getBaseUri();
    
    Location getValidationLocation();
    
    void reportProblem(final XMLValidationProblem p0) throws XMLStreamException;
    
    int addDefaultAttribute(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
}
