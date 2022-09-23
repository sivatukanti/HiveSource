// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import javax.xml.namespace.NamespaceContext;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.DTDInfo;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.typed.Base64Variant;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.ri.typed.SimpleValueEncoder;
import javax.xml.stream.XMLStreamConstants;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.util.StreamWriterDelegate;

public class Stax2WriterAdapter extends StreamWriterDelegate implements XMLStreamWriter2, XMLStreamConstants
{
    protected String mEncoding;
    protected SimpleValueEncoder mValueEncoder;
    protected final boolean mNsRepairing;
    
    protected Stax2WriterAdapter(final XMLStreamWriter mDelegate) {
        super(mDelegate);
        this.mDelegate = mDelegate;
        final Object property = mDelegate.getProperty("javax.xml.stream.isRepairingNamespaces");
        this.mNsRepairing = (property instanceof Boolean && (boolean)property);
    }
    
    public static XMLStreamWriter2 wrapIfNecessary(final XMLStreamWriter xmlStreamWriter) {
        if (xmlStreamWriter instanceof XMLStreamWriter2) {
            return (XMLStreamWriter2)xmlStreamWriter;
        }
        return new Stax2WriterAdapter(xmlStreamWriter);
    }
    
    public void writeBoolean(final boolean b) throws XMLStreamException {
        this.mDelegate.writeCharacters(b ? "true" : "false");
    }
    
    public void writeInt(final int i) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(i));
    }
    
    public void writeLong(final long l) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(l));
    }
    
    public void writeFloat(final float f) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(f));
    }
    
    public void writeDouble(final double d) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(d));
    }
    
    public void writeInteger(final BigInteger bigInteger) throws XMLStreamException {
        this.mDelegate.writeCharacters(bigInteger.toString());
    }
    
    public void writeDecimal(final BigDecimal bigDecimal) throws XMLStreamException {
        this.mDelegate.writeCharacters(bigDecimal.toString());
    }
    
    public void writeQName(final QName qName) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.serializeQNameValue(qName));
    }
    
    public void writeIntArray(final int[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(array, n, n2));
    }
    
    public void writeLongArray(final long[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(array, n, n2));
    }
    
    public void writeFloatArray(final float[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(array, n, n2));
    }
    
    public void writeDoubleArray(final double[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(array, n, n2));
    }
    
    public void writeBinary(final Base64Variant base64Variant, final byte[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(base64Variant, array, n, n2));
    }
    
    public void writeBinary(final byte[] array, final int n, final int n2) throws XMLStreamException {
        this.writeBinary(Base64Variants.getDefaultVariant(), array, n, n2);
    }
    
    public void writeBooleanAttribute(final String s, final String s2, final String s3, final boolean b) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, b ? "true" : "false");
    }
    
    public void writeIntAttribute(final String s, final String s2, final String s3, final int i) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, String.valueOf(i));
    }
    
    public void writeLongAttribute(final String s, final String s2, final String s3, final long l) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, String.valueOf(l));
    }
    
    public void writeFloatAttribute(final String s, final String s2, final String s3, final float f) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, String.valueOf(f));
    }
    
    public void writeDoubleAttribute(final String s, final String s2, final String s3, final double d) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, String.valueOf(d));
    }
    
    public void writeIntegerAttribute(final String s, final String s2, final String s3, final BigInteger bigInteger) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, bigInteger.toString());
    }
    
    public void writeDecimalAttribute(final String s, final String s2, final String s3, final BigDecimal bigDecimal) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, bigDecimal.toString());
    }
    
    public void writeQNameAttribute(final String s, final String s2, final String s3, final QName qName) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, this.serializeQNameValue(qName));
    }
    
    public void writeIntArrayAttribute(final String s, final String s2, final String s3, final int[] array) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(array, 0, array.length));
    }
    
    public void writeLongArrayAttribute(final String s, final String s2, final String s3, final long[] array) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(array, 0, array.length));
    }
    
    public void writeFloatArrayAttribute(final String s, final String s2, final String s3, final float[] array) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(array, 0, array.length));
    }
    
    public void writeDoubleArrayAttribute(final String s, final String s2, final String s3, final double[] array) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(array, 0, array.length));
    }
    
    public void writeBinaryAttribute(final String s, final String s2, final String s3, final byte[] array) throws XMLStreamException {
        this.writeBinaryAttribute(Base64Variants.getDefaultVariant(), s, s2, s3, array);
    }
    
    public void writeBinaryAttribute(final Base64Variant base64Variant, final String s, final String s2, final String s3, final byte[] array) throws XMLStreamException {
        this.mDelegate.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(base64Variant, array, 0, array.length));
    }
    
    public boolean isPropertySupported(final String s) {
        return false;
    }
    
    public boolean setProperty(final String str, final Object o) {
        throw new IllegalArgumentException("No settable property '" + str + "'");
    }
    
    public XMLStreamLocation2 getLocation() {
        return null;
    }
    
    public String getEncoding() {
        return this.mEncoding;
    }
    
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
        this.mDelegate.writeCharacters("");
        this.mDelegate.writeEndElement();
    }
    
    public void writeSpace(final String s) throws XMLStreamException {
        this.writeRaw(s);
    }
    
    public void writeSpace(final char[] array, final int n, final int n2) throws XMLStreamException {
        this.writeRaw(array, n, n2);
    }
    
    public void writeStartDocument(final String s, final String s2, final boolean b) throws XMLStreamException {
        this.writeStartDocument(s2, s);
    }
    
    public void writeRaw(final String s) throws XMLStreamException {
        this.writeRaw(s, 0, s.length());
    }
    
    public void writeRaw(final String s, final int n, final int n2) throws XMLStreamException {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public void writeRaw(final char[] value, final int offset, final int count) throws XMLStreamException {
        this.writeRaw(new String(value, offset, count));
    }
    
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
    
    public void closeCompletely() throws XMLStreamException {
        this.close();
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
    
    protected String serializeQNameValue(final QName qName) throws XMLStreamException {
        String prefix;
        if (this.mNsRepairing) {
            final String namespaceURI = qName.getNamespaceURI();
            final NamespaceContext namespaceContext = this.getNamespaceContext();
            prefix = ((namespaceContext == null) ? null : namespaceContext.getPrefix(namespaceURI));
            if (prefix == null) {
                final String prefix2 = qName.getPrefix();
                if (prefix2 == null || prefix2.length() == 0) {
                    prefix = "";
                    this.writeDefaultNamespace(namespaceURI);
                }
                else {
                    prefix = prefix2;
                    this.writeNamespace(prefix, namespaceURI);
                }
            }
        }
        else {
            prefix = qName.getPrefix();
        }
        final String localPart = qName.getLocalPart();
        if (prefix == null || prefix.length() == 0) {
            return localPart;
        }
        return prefix + ":" + localPart;
    }
    
    protected SimpleValueEncoder getValueEncoder() {
        if (this.mValueEncoder == null) {
            this.mValueEncoder = new SimpleValueEncoder();
        }
        return this.mValueEncoder;
    }
}
