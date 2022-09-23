// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.util;

import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedValueDecoder;
import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.AttributeInfo;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.XMLStreamReader2;
import javax.xml.stream.util.StreamReaderDelegate;

public class StreamReader2Delegate extends StreamReaderDelegate implements XMLStreamReader2
{
    protected XMLStreamReader2 mDelegate2;
    
    public StreamReader2Delegate(final XMLStreamReader2 xmlStreamReader2) {
        super(xmlStreamReader2);
        this.mDelegate2 = xmlStreamReader2;
    }
    
    @Override
    public void setParent(final XMLStreamReader parent) {
        super.setParent(parent);
        this.mDelegate2 = (XMLStreamReader2)parent;
    }
    
    public void closeCompletely() throws XMLStreamException {
        this.mDelegate2.closeCompletely();
    }
    
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        return this.mDelegate2.getAttributeInfo();
    }
    
    public DTDInfo getDTDInfo() throws XMLStreamException {
        return this.mDelegate2.getDTDInfo();
    }
    
    public int getDepth() {
        return this.mDelegate2.getDepth();
    }
    
    public Object getFeature(final String s) {
        return this.mDelegate2.getFeature(s);
    }
    
    public LocationInfo getLocationInfo() {
        return this.mDelegate2.getLocationInfo();
    }
    
    public NamespaceContext getNonTransientNamespaceContext() {
        return this.mDelegate2.getNonTransientNamespaceContext();
    }
    
    public String getPrefixedName() {
        return this.mDelegate2.getPrefixedName();
    }
    
    public int getText(final Writer writer, final boolean b) throws IOException, XMLStreamException {
        return this.mDelegate2.getText(writer, b);
    }
    
    public boolean isEmptyElement() throws XMLStreamException {
        return this.mDelegate2.isEmptyElement();
    }
    
    public boolean isPropertySupported(final String s) {
        return this.mDelegate2.isPropertySupported(s);
    }
    
    public void setFeature(final String s, final Object o) {
        this.mDelegate2.setFeature(s, o);
    }
    
    public boolean setProperty(final String s, final Object o) {
        return this.mDelegate2.setProperty(s, o);
    }
    
    public void skipElement() throws XMLStreamException {
        this.mDelegate2.skipElement();
    }
    
    public ValidationProblemHandler setValidationProblemHandler(final ValidationProblemHandler validationProblemHandler) {
        return this.mDelegate2.setValidationProblemHandler(validationProblemHandler);
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        return this.mDelegate2.stopValidatingAgainst(xmlValidationSchema);
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidator xmlValidator) throws XMLStreamException {
        return this.mDelegate2.stopValidatingAgainst(xmlValidator);
    }
    
    public XMLValidator validateAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        return this.mDelegate2.validateAgainst(xmlValidationSchema);
    }
    
    public int getAttributeIndex(final String s, final String s2) {
        return this.mDelegate2.getAttributeIndex(s, s2);
    }
    
    public boolean getAttributeAsBoolean(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsBoolean(n);
    }
    
    public BigDecimal getAttributeAsDecimal(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsDecimal(n);
    }
    
    public double getAttributeAsDouble(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsDouble(n);
    }
    
    public float getAttributeAsFloat(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsFloat(n);
    }
    
    public int getAttributeAsInt(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsInt(n);
    }
    
    public BigInteger getAttributeAsInteger(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsInteger(n);
    }
    
    public long getAttributeAsLong(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsLong(n);
    }
    
    public QName getAttributeAsQName(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsQName(n);
    }
    
    public int[] getAttributeAsIntArray(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsIntArray(n);
    }
    
    public long[] getAttributeAsLongArray(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsLongArray(n);
    }
    
    public float[] getAttributeAsFloatArray(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsFloatArray(n);
    }
    
    public double[] getAttributeAsDoubleArray(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsDoubleArray(n);
    }
    
    public void getElementAs(final TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        this.mDelegate2.getElementAs(typedValueDecoder);
    }
    
    public boolean getElementAsBoolean() throws XMLStreamException {
        return this.mDelegate2.getElementAsBoolean();
    }
    
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        return this.mDelegate2.getElementAsDecimal();
    }
    
    public double getElementAsDouble() throws XMLStreamException {
        return this.mDelegate2.getElementAsDouble();
    }
    
    public float getElementAsFloat() throws XMLStreamException {
        return this.mDelegate2.getElementAsFloat();
    }
    
    public int getElementAsInt() throws XMLStreamException {
        return this.mDelegate2.getElementAsInt();
    }
    
    public BigInteger getElementAsInteger() throws XMLStreamException {
        return this.mDelegate2.getElementAsInteger();
    }
    
    public long getElementAsLong() throws XMLStreamException {
        return this.mDelegate2.getElementAsLong();
    }
    
    public QName getElementAsQName() throws XMLStreamException {
        return this.mDelegate2.getElementAsQName();
    }
    
    public byte[] getElementAsBinary() throws XMLStreamException {
        return this.mDelegate2.getElementAsBinary();
    }
    
    public byte[] getElementAsBinary(final Base64Variant base64Variant) throws XMLStreamException {
        return this.mDelegate2.getElementAsBinary(base64Variant);
    }
    
    public void getAttributeAs(final int n, final TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        this.mDelegate2.getAttributeAs(n, typedValueDecoder);
    }
    
    public int getAttributeAsArray(final int n, final TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsArray(n, typedArrayDecoder);
    }
    
    public byte[] getAttributeAsBinary(final int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsBinary(n);
    }
    
    public byte[] getAttributeAsBinary(final int n, final Base64Variant base64Variant) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsBinary(n, base64Variant);
    }
    
    public int readElementAsDoubleArray(final double[] array, final int n, final int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsDoubleArray(array, n, n2);
    }
    
    public int readElementAsFloatArray(final float[] array, final int n, final int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsFloatArray(array, n, n2);
    }
    
    public int readElementAsIntArray(final int[] array, final int n, final int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsIntArray(array, n, n2);
    }
    
    public int readElementAsLongArray(final long[] array, final int n, final int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsLongArray(array, n, n2);
    }
    
    public int readElementAsArray(final TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        return this.mDelegate2.readElementAsArray(typedArrayDecoder);
    }
    
    public int readElementAsBinary(final byte[] array, final int n, final int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsBinary(array, n, n2);
    }
    
    public int readElementAsBinary(final byte[] array, final int n, final int n2, final Base64Variant base64Variant) throws XMLStreamException {
        return this.mDelegate2.readElementAsBinary(array, n, n2, base64Variant);
    }
}
