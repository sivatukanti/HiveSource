// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.util;

import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.XMLStreamWriter2;

public class StreamWriter2Delegate extends StreamWriterDelegate implements XMLStreamWriter2
{
    protected XMLStreamWriter2 mDelegate2;
    
    public StreamWriter2Delegate(final XMLStreamWriter2 xmlStreamWriter2) {
        super(xmlStreamWriter2);
    }
    
    @Override
    public void setParent(final XMLStreamWriter parent) {
        super.setParent(parent);
        this.mDelegate2 = (XMLStreamWriter2)parent;
    }
    
    public void closeCompletely() throws XMLStreamException {
        this.mDelegate2.closeCompletely();
    }
    
    public void copyEventFromReader(final XMLStreamReader2 xmlStreamReader2, final boolean b) throws XMLStreamException {
        this.mDelegate2.copyEventFromReader(xmlStreamReader2, b);
    }
    
    public String getEncoding() {
        return this.mDelegate2.getEncoding();
    }
    
    public XMLStreamLocation2 getLocation() {
        return this.mDelegate2.getLocation();
    }
    
    public boolean isPropertySupported(final String s) {
        return this.mDelegate2.isPropertySupported(s);
    }
    
    public boolean setProperty(final String s, final Object o) {
        return this.mDelegate2.setProperty(s, o);
    }
    
    public void writeCData(final char[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeCData(array, n, n2);
    }
    
    public void writeDTD(final String s, final String s2, final String s3, final String s4) throws XMLStreamException {
        this.mDelegate2.writeDTD(s, s2, s3, s4);
    }
    
    public void writeFullEndElement() throws XMLStreamException {
        this.mDelegate2.writeFullEndElement();
    }
    
    public void writeRaw(final String s) throws XMLStreamException {
        this.mDelegate2.writeRaw(s);
    }
    
    public void writeRaw(final String s, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeRaw(s, n, n2);
    }
    
    public void writeRaw(final char[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeRaw(array, n, n2);
    }
    
    public void writeSpace(final String s) throws XMLStreamException {
        this.mDelegate2.writeSpace(s);
    }
    
    public void writeSpace(final char[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeSpace(array, n, n2);
    }
    
    public void writeStartDocument(final String s, final String s2, final boolean b) throws XMLStreamException {
        this.mDelegate2.writeStartDocument(s, s2, b);
    }
    
    public void writeBinary(final byte[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeBinary(array, n, n2);
    }
    
    public void writeBinary(final Base64Variant base64Variant, final byte[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeBinary(base64Variant, array, n, n2);
    }
    
    public void writeBinaryAttribute(final String s, final String s2, final String s3, final byte[] array) throws XMLStreamException {
        this.mDelegate2.writeBinaryAttribute(s, s2, s3, array);
    }
    
    public void writeBinaryAttribute(final Base64Variant base64Variant, final String s, final String s2, final String s3, final byte[] array) throws XMLStreamException {
        this.mDelegate2.writeBinaryAttribute(base64Variant, s, s2, s3, array);
    }
    
    public void writeBoolean(final boolean b) throws XMLStreamException {
        this.mDelegate2.writeBoolean(b);
    }
    
    public void writeBooleanAttribute(final String s, final String s2, final String s3, final boolean b) throws XMLStreamException {
        this.mDelegate2.writeBooleanAttribute(s, s2, s3, b);
    }
    
    public void writeDecimal(final BigDecimal bigDecimal) throws XMLStreamException {
        this.mDelegate2.writeDecimal(bigDecimal);
    }
    
    public void writeDecimalAttribute(final String s, final String s2, final String s3, final BigDecimal bigDecimal) throws XMLStreamException {
        this.mDelegate2.writeDecimalAttribute(s, s2, s3, bigDecimal);
    }
    
    public void writeDouble(final double n) throws XMLStreamException {
        this.mDelegate2.writeDouble(n);
    }
    
    public void writeDoubleArray(final double[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeDoubleArray(array, n, n2);
    }
    
    public void writeDoubleArrayAttribute(final String s, final String s2, final String s3, final double[] array) throws XMLStreamException {
        this.mDelegate2.writeDoubleArrayAttribute(s, s2, s3, array);
    }
    
    public void writeDoubleAttribute(final String s, final String s2, final String s3, final double n) throws XMLStreamException {
        this.mDelegate2.writeDoubleAttribute(s, s2, s3, n);
    }
    
    public void writeFloat(final float n) throws XMLStreamException {
        this.mDelegate2.writeFloat(n);
    }
    
    public void writeFloatArray(final float[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeFloatArray(array, n, n2);
    }
    
    public void writeFloatArrayAttribute(final String s, final String s2, final String s3, final float[] array) throws XMLStreamException {
        this.mDelegate2.writeFloatArrayAttribute(s, s2, s3, array);
    }
    
    public void writeFloatAttribute(final String s, final String s2, final String s3, final float n) throws XMLStreamException {
        this.mDelegate2.writeFloatAttribute(s, s2, s3, n);
    }
    
    public void writeInt(final int n) throws XMLStreamException {
        this.mDelegate2.writeInt(n);
    }
    
    public void writeIntArray(final int[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeIntArray(array, n, n2);
    }
    
    public void writeIntArrayAttribute(final String s, final String s2, final String s3, final int[] array) throws XMLStreamException {
        this.mDelegate2.writeIntArrayAttribute(s, s2, s3, array);
    }
    
    public void writeIntAttribute(final String s, final String s2, final String s3, final int n) throws XMLStreamException {
        this.mDelegate2.writeIntAttribute(s, s2, s3, n);
    }
    
    public void writeInteger(final BigInteger bigInteger) throws XMLStreamException {
        this.mDelegate2.writeInteger(bigInteger);
    }
    
    public void writeIntegerAttribute(final String s, final String s2, final String s3, final BigInteger bigInteger) throws XMLStreamException {
        this.mDelegate2.writeIntegerAttribute(s, s2, s3, bigInteger);
    }
    
    public void writeLong(final long n) throws XMLStreamException {
        this.mDelegate2.writeLong(n);
    }
    
    public void writeLongArray(final long[] array, final int n, final int n2) throws XMLStreamException {
        this.mDelegate2.writeLongArray(array, n, n2);
    }
    
    public void writeLongArrayAttribute(final String s, final String s2, final String s3, final long[] array) throws XMLStreamException {
        this.mDelegate2.writeLongArrayAttribute(s, s2, s3, array);
    }
    
    public void writeLongAttribute(final String s, final String s2, final String s3, final long n) throws XMLStreamException {
        this.mDelegate2.writeLongAttribute(s, s2, s3, n);
    }
    
    public void writeQName(final QName qName) throws XMLStreamException {
        this.mDelegate2.writeQName(qName);
    }
    
    public void writeQNameAttribute(final String s, final String s2, final String s3, final QName qName) throws XMLStreamException {
        this.mDelegate2.writeQNameAttribute(s, s2, s3, qName);
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
}
