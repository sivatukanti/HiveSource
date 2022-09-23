// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;

public abstract class XMLValidator
{
    public static final int CONTENT_ALLOW_NONE = 0;
    public static final int CONTENT_ALLOW_WS = 1;
    public static final int CONTENT_ALLOW_WS_NONSTRICT = 2;
    public static final int CONTENT_ALLOW_VALIDATABLE_TEXT = 3;
    public static final int CONTENT_ALLOW_ANY_TEXT = 4;
    public static final int CONTENT_ALLOW_UNDEFINED = 5;
    
    protected XMLValidator() {
    }
    
    public String getSchemaType() {
        final XMLValidationSchema schema = this.getSchema();
        return (schema == null) ? null : schema.getSchemaType();
    }
    
    public abstract XMLValidationSchema getSchema();
    
    public abstract void validateElementStart(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    public abstract String validateAttribute(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    public abstract String validateAttribute(final String p0, final String p1, final String p2, final char[] p3, final int p4, final int p5) throws XMLStreamException;
    
    public abstract int validateElementAndAttributes() throws XMLStreamException;
    
    public abstract int validateElementEnd(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    public abstract void validateText(final String p0, final boolean p1) throws XMLStreamException;
    
    public abstract void validateText(final char[] p0, final int p1, final int p2, final boolean p3) throws XMLStreamException;
    
    public abstract void validationCompleted(final boolean p0) throws XMLStreamException;
    
    public abstract String getAttributeType(final int p0);
    
    public abstract int getIdAttrIndex();
    
    public abstract int getNotationAttrIndex();
}
