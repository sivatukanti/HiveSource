// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.DTDInfo;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.XMLStreamReader2;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamLocation2;
import javax.xml.stream.XMLStreamConstants;
import org.codehaus.stax2.XMLStreamWriter2;

public abstract class Stax2WriterImpl implements XMLStreamWriter2, XMLStreamConstants
{
    protected Stax2WriterImpl() {
    }
    
    public boolean isPropertySupported(final String s) {
        return false;
    }
    
    public boolean setProperty(final String str, final Object o) {
        throw new IllegalArgumentException("No settable property '" + str + "'");
    }
    
    public abstract XMLStreamLocation2 getLocation();
    
    public abstract String getEncoding();
    
    public void writeCData(final char[] value, final int offset, final int count) throws XMLStreamException {
        this.writeCData(new String(value, offset, count));
    }
    
    public void writeDTD(final String str, final String str2, final String str3, final String str4) throws XMLStreamException {
        final StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE");
        sb.append(str);
        if (str2 != null) {
            if (str3 != null) {
                sb.append(" PUBLIC \"");
                sb.append(str3);
                sb.append("\" \"");
            }
            else {
                sb.append(" SYSTEM \"");
            }
            sb.append(str2);
            sb.append('\"');
        }
        if (str4 != null && str4.length() > 0) {
            sb.append(" [");
            sb.append(str4);
            sb.append(']');
        }
        sb.append('>');
        this.writeDTD(sb.toString());
    }
    
    public void writeFullEndElement() throws XMLStreamException {
        this.writeCharacters("");
        this.writeEndElement();
    }
    
    public void writeSpace(final String s) throws XMLStreamException {
        this.writeRaw(s);
    }
    
    public void writeSpace(final char[] array, final int n, final int n2) throws XMLStreamException {
        this.writeRaw(array, n, n2);
    }
    
    public abstract void writeStartDocument(final String p0, final String p1, final boolean p2) throws XMLStreamException;
    
    public void writeRaw(final String s) throws XMLStreamException {
        this.writeRaw(s, 0, s.length());
    }
    
    public abstract void writeRaw(final String p0, final int p1, final int p2) throws XMLStreamException;
    
    public abstract void writeRaw(final char[] p0, final int p1, final int p2) throws XMLStreamException;
    
    public void copyEventFromReader(final XMLStreamReader2 xmlStreamReader2, final boolean b) throws XMLStreamException {
        switch (xmlStreamReader2.getEventType()) {
            case 7: {
                final String version = xmlStreamReader2.getVersion();
                if (version != null) {
                    if (version.length() != 0) {
                        if (xmlStreamReader2.standaloneSet()) {
                            this.writeStartDocument(xmlStreamReader2.getVersion(), xmlStreamReader2.getCharacterEncodingScheme(), xmlStreamReader2.isStandalone());
                        }
                        else {
                            this.writeStartDocument(xmlStreamReader2.getCharacterEncodingScheme(), xmlStreamReader2.getVersion());
                        }
                    }
                }
            }
            case 8: {
                this.writeEndDocument();
            }
            case 1: {
                this.copyStartElement(xmlStreamReader2);
            }
            case 2: {
                this.writeEndElement();
            }
            case 6: {
                this.writeSpace(xmlStreamReader2.getTextCharacters(), xmlStreamReader2.getTextStart(), xmlStreamReader2.getTextLength());
            }
            case 12: {
                this.writeCData(xmlStreamReader2.getTextCharacters(), xmlStreamReader2.getTextStart(), xmlStreamReader2.getTextLength());
            }
            case 4: {
                this.writeCharacters(xmlStreamReader2.getTextCharacters(), xmlStreamReader2.getTextStart(), xmlStreamReader2.getTextLength());
            }
            case 5: {
                this.writeComment(xmlStreamReader2.getText());
            }
            case 3: {
                this.writeProcessingInstruction(xmlStreamReader2.getPITarget(), xmlStreamReader2.getPIData());
            }
            case 11: {
                final DTDInfo dtdInfo = xmlStreamReader2.getDTDInfo();
                if (dtdInfo == null) {
                    throw new XMLStreamException("Current state DOCTYPE, but not DTDInfo Object returned -- reader doesn't support DTDs?");
                }
                this.writeDTD(dtdInfo.getDTDRootName(), dtdInfo.getDTDSystemId(), dtdInfo.getDTDPublicId(), dtdInfo.getDTDInternalSubset());
            }
            case 9: {
                this.writeEntityRef(xmlStreamReader2.getLocalName());
            }
            default: {
                throw new XMLStreamException("Unrecognized event type (" + xmlStreamReader2.getEventType() + "); not sure how to copy");
            }
        }
    }
    
    public XMLValidator validateAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        return null;
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidator xmlValidator) throws XMLStreamException {
        return null;
    }
    
    public ValidationProblemHandler setValidationProblemHandler(final ValidationProblemHandler validationProblemHandler) {
        return null;
    }
    
    protected void copyStartElement(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        final int namespaceCount = xmlStreamReader.getNamespaceCount();
        if (namespaceCount > 0) {
            for (int i = 0; i < namespaceCount; ++i) {
                final String namespacePrefix = xmlStreamReader.getNamespacePrefix(i);
                final String namespaceURI = xmlStreamReader.getNamespaceURI(i);
                if (namespacePrefix == null || namespacePrefix.length() == 0) {
                    this.setDefaultNamespace(namespaceURI);
                }
                else {
                    this.setPrefix(namespacePrefix, namespaceURI);
                }
            }
        }
        this.writeStartElement(xmlStreamReader.getPrefix(), xmlStreamReader.getLocalName(), xmlStreamReader.getNamespaceURI());
        if (namespaceCount > 0) {
            for (int j = 0; j < namespaceCount; ++j) {
                final String namespacePrefix2 = xmlStreamReader.getNamespacePrefix(j);
                final String namespaceURI2 = xmlStreamReader.getNamespaceURI(j);
                if (namespacePrefix2 == null || namespacePrefix2.length() == 0) {
                    this.writeDefaultNamespace(namespaceURI2);
                }
                else {
                    this.writeNamespace(namespacePrefix2, namespaceURI2);
                }
            }
        }
        final int attributeCount = xmlStreamReader.getAttributeCount();
        if (attributeCount > 0) {
            for (int k = 0; k < attributeCount; ++k) {
                this.writeAttribute(xmlStreamReader.getAttributePrefix(k), xmlStreamReader.getAttributeNamespace(k), xmlStreamReader.getAttributeLocalName(k), xmlStreamReader.getAttributeValue(k));
            }
        }
    }
}
