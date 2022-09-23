// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.dom;

import java.text.MessageFormat;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.XMLStreamLocation2;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Node;
import org.codehaus.stax2.ri.typed.SimpleValueEncoder;
import org.w3c.dom.Document;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.stax2.XMLStreamWriter2;

public abstract class DOMWrappingWriter implements XMLStreamWriter2
{
    static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";
    static final String DEFAULT_XML_VERSION = "1.0";
    protected final boolean mNsAware;
    protected final boolean mNsRepairing;
    protected String mEncoding;
    protected NamespaceContext mNsContext;
    protected final Document mDocument;
    protected SimpleValueEncoder mValueEncoder;
    
    protected DOMWrappingWriter(final Node node, final boolean mNsAware, final boolean mNsRepairing) throws XMLStreamException {
        this.mEncoding = null;
        if (node == null) {
            throw new IllegalArgumentException("Can not pass null Node for constructing a DOM-based XMLStreamWriter");
        }
        this.mNsAware = mNsAware;
        this.mNsRepairing = mNsRepairing;
        switch (node.getNodeType()) {
            case 9: {
                this.mDocument = (Document)node;
                break;
            }
            case 1: {
                this.mDocument = node.getOwnerDocument();
                break;
            }
            case 11: {
                this.mDocument = node.getOwnerDocument();
                break;
            }
            default: {
                throw new XMLStreamException("Can not create an XMLStreamWriter for a DOM node of type " + node.getClass());
            }
        }
        if (this.mDocument == null) {
            throw new XMLStreamException("Can not create an XMLStreamWriter for given node (of type " + node.getClass() + "): did not have owner document");
        }
    }
    
    public void close() {
    }
    
    public void flush() {
    }
    
    public abstract NamespaceContext getNamespaceContext();
    
    public abstract String getPrefix(final String p0);
    
    public abstract Object getProperty(final String p0);
    
    public abstract void setDefaultNamespace(final String p0);
    
    public void setNamespaceContext(final NamespaceContext mNsContext) {
        this.mNsContext = mNsContext;
    }
    
    public abstract void setPrefix(final String p0, final String p1) throws XMLStreamException;
    
    public abstract void writeAttribute(final String p0, final String p1) throws XMLStreamException;
    
    public abstract void writeAttribute(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    public abstract void writeAttribute(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    public void writeCData(final String s) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createCDATASection(s));
    }
    
    public void writeCharacters(final char[] value, final int offset, final int count) throws XMLStreamException {
        this.writeCharacters(new String(value, offset, count));
    }
    
    public void writeCharacters(final String s) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createTextNode(s));
    }
    
    public void writeComment(final String s) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createComment(s));
    }
    
    public abstract void writeDefaultNamespace(final String p0) throws XMLStreamException;
    
    public void writeDTD(final String s) throws XMLStreamException {
        this.reportUnsupported("writeDTD()");
    }
    
    public abstract void writeEmptyElement(final String p0) throws XMLStreamException;
    
    public abstract void writeEmptyElement(final String p0, final String p1) throws XMLStreamException;
    
    public abstract void writeEmptyElement(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    public abstract void writeEndDocument() throws XMLStreamException;
    
    public void writeEntityRef(final String s) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createEntityReference(s));
    }
    
    public void writeProcessingInstruction(final String s) throws XMLStreamException {
        this.writeProcessingInstruction(s, null);
    }
    
    public void writeProcessingInstruction(final String s, final String s2) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createProcessingInstruction(s, s2));
    }
    
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument("UTF-8", "1.0");
    }
    
    public void writeStartDocument(final String s) throws XMLStreamException {
        this.writeStartDocument(null, s);
    }
    
    public void writeStartDocument(final String mEncoding, final String s) throws XMLStreamException {
        this.mEncoding = mEncoding;
    }
    
    public XMLStreamLocation2 getLocation() {
        return null;
    }
    
    public String getEncoding() {
        return this.mEncoding;
    }
    
    public abstract boolean isPropertySupported(final String p0);
    
    public abstract boolean setProperty(final String p0, final Object p1);
    
    public void writeCData(final char[] value, final int offset, final int count) throws XMLStreamException {
        this.writeCData(new String(value, offset, count));
    }
    
    public abstract void writeDTD(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    public void writeFullEndElement() throws XMLStreamException {
        this.writeEndElement();
    }
    
    public void writeSpace(final char[] value, final int offset, final int count) throws XMLStreamException {
        this.writeSpace(new String(value, offset, count));
    }
    
    public void writeSpace(final String s) throws XMLStreamException {
        this.writeCharacters(s);
    }
    
    public void writeStartDocument(final String s, final String s2, final boolean b) throws XMLStreamException {
        this.writeStartDocument(s2, s);
    }
    
    public XMLValidator validateAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        return null;
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
    
    public void writeRaw(final String s) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }
    
    public void writeRaw(final String s, final int n, final int n2) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }
    
    public void writeRaw(final char[] array, final int n, final int n2) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }
    
    public void copyEventFromReader(final XMLStreamReader2 xmlStreamReader2, final boolean b) throws XMLStreamException {
    }
    
    public void closeCompletely() {
    }
    
    public void writeBoolean(final boolean b) throws XMLStreamException {
        this.writeCharacters(b ? "true" : "false");
    }
    
    public void writeInt(final int i) throws XMLStreamException {
        this.writeCharacters(String.valueOf(i));
    }
    
    public void writeLong(final long l) throws XMLStreamException {
        this.writeCharacters(String.valueOf(l));
    }
    
    public void writeFloat(final float f) throws XMLStreamException {
        this.writeCharacters(String.valueOf(f));
    }
    
    public void writeDouble(final double d) throws XMLStreamException {
        this.writeCharacters(String.valueOf(d));
    }
    
    public void writeInteger(final BigInteger bigInteger) throws XMLStreamException {
        this.writeCharacters(bigInteger.toString());
    }
    
    public void writeDecimal(final BigDecimal bigDecimal) throws XMLStreamException {
        this.writeCharacters(bigDecimal.toString());
    }
    
    public void writeQName(final QName qName) throws XMLStreamException {
        this.writeCharacters(this.serializeQNameValue(qName));
    }
    
    public void writeIntArray(final int[] array, final int n, final int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(array, n, n2));
    }
    
    public void writeLongArray(final long[] array, final int n, final int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(array, n, n2));
    }
    
    public void writeFloatArray(final float[] array, final int n, final int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(array, n, n2));
    }
    
    public void writeDoubleArray(final double[] array, final int n, final int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(array, n, n2));
    }
    
    public void writeBinary(final byte[] array, final int n, final int n2) throws XMLStreamException {
        this.writeBinary(Base64Variants.getDefaultVariant(), array, n, n2);
    }
    
    public void writeBinary(final Base64Variant base64Variant, final byte[] array, final int n, final int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(base64Variant, array, n, n2));
    }
    
    public void writeBooleanAttribute(final String s, final String s2, final String s3, final boolean b) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, b ? "true" : "false");
    }
    
    public void writeIntAttribute(final String s, final String s2, final String s3, final int i) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, String.valueOf(i));
    }
    
    public void writeLongAttribute(final String s, final String s2, final String s3, final long l) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, String.valueOf(l));
    }
    
    public void writeFloatAttribute(final String s, final String s2, final String s3, final float f) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, String.valueOf(f));
    }
    
    public void writeDoubleAttribute(final String s, final String s2, final String s3, final double d) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, String.valueOf(d));
    }
    
    public void writeIntegerAttribute(final String s, final String s2, final String s3, final BigInteger bigInteger) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, bigInteger.toString());
    }
    
    public void writeDecimalAttribute(final String s, final String s2, final String s3, final BigDecimal bigDecimal) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, bigDecimal.toString());
    }
    
    public void writeQNameAttribute(final String s, final String s2, final String s3, final QName qName) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, this.serializeQNameValue(qName));
    }
    
    public void writeIntArrayAttribute(final String s, final String s2, final String s3, final int[] array) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(array, 0, array.length));
    }
    
    public void writeLongArrayAttribute(final String s, final String s2, final String s3, final long[] array) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(array, 0, array.length));
    }
    
    public void writeFloatArrayAttribute(final String s, final String s2, final String s3, final float[] array) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(array, 0, array.length));
    }
    
    public void writeDoubleArrayAttribute(final String s, final String s2, final String s3, final double[] array) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(array, 0, array.length));
    }
    
    public void writeBinaryAttribute(final String s, final String s2, final String s3, final byte[] array) throws XMLStreamException {
        this.writeBinaryAttribute(Base64Variants.getDefaultVariant(), s, s2, s3, array);
    }
    
    public void writeBinaryAttribute(final Base64Variant base64Variant, final String s, final String s2, final String s3, final byte[] array) throws XMLStreamException {
        this.writeAttribute(s, s2, s3, this.getValueEncoder().encodeAsString(base64Variant, array, 0, array.length));
    }
    
    protected abstract void appendLeaf(final Node p0) throws IllegalStateException;
    
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
    
    protected static void throwOutputError(final String msg) throws XMLStreamException {
        throw new XMLStreamException(msg);
    }
    
    protected static void throwOutputError(final String pattern, final Object o) throws XMLStreamException {
        throwOutputError(MessageFormat.format(pattern, o));
    }
    
    protected void reportUnsupported(final String str) {
        throw new UnsupportedOperationException(str + " can not be used with DOM-backed writer");
    }
}
