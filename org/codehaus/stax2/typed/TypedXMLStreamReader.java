// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.typed;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public interface TypedXMLStreamReader extends XMLStreamReader
{
    boolean getElementAsBoolean() throws XMLStreamException;
    
    int getElementAsInt() throws XMLStreamException;
    
    long getElementAsLong() throws XMLStreamException;
    
    float getElementAsFloat() throws XMLStreamException;
    
    double getElementAsDouble() throws XMLStreamException;
    
    BigInteger getElementAsInteger() throws XMLStreamException;
    
    BigDecimal getElementAsDecimal() throws XMLStreamException;
    
    QName getElementAsQName() throws XMLStreamException;
    
    byte[] getElementAsBinary() throws XMLStreamException;
    
    byte[] getElementAsBinary(final Base64Variant p0) throws XMLStreamException;
    
    void getElementAs(final TypedValueDecoder p0) throws XMLStreamException;
    
    int readElementAsBinary(final byte[] p0, final int p1, final int p2, final Base64Variant p3) throws XMLStreamException;
    
    int readElementAsBinary(final byte[] p0, final int p1, final int p2) throws XMLStreamException;
    
    int readElementAsIntArray(final int[] p0, final int p1, final int p2) throws XMLStreamException;
    
    int readElementAsLongArray(final long[] p0, final int p1, final int p2) throws XMLStreamException;
    
    int readElementAsFloatArray(final float[] p0, final int p1, final int p2) throws XMLStreamException;
    
    int readElementAsDoubleArray(final double[] p0, final int p1, final int p2) throws XMLStreamException;
    
    int readElementAsArray(final TypedArrayDecoder p0) throws XMLStreamException;
    
    int getAttributeIndex(final String p0, final String p1);
    
    boolean getAttributeAsBoolean(final int p0) throws XMLStreamException;
    
    int getAttributeAsInt(final int p0) throws XMLStreamException;
    
    long getAttributeAsLong(final int p0) throws XMLStreamException;
    
    float getAttributeAsFloat(final int p0) throws XMLStreamException;
    
    double getAttributeAsDouble(final int p0) throws XMLStreamException;
    
    BigInteger getAttributeAsInteger(final int p0) throws XMLStreamException;
    
    BigDecimal getAttributeAsDecimal(final int p0) throws XMLStreamException;
    
    QName getAttributeAsQName(final int p0) throws XMLStreamException;
    
    void getAttributeAs(final int p0, final TypedValueDecoder p1) throws XMLStreamException;
    
    byte[] getAttributeAsBinary(final int p0) throws XMLStreamException;
    
    byte[] getAttributeAsBinary(final int p0, final Base64Variant p1) throws XMLStreamException;
    
    int[] getAttributeAsIntArray(final int p0) throws XMLStreamException;
    
    long[] getAttributeAsLongArray(final int p0) throws XMLStreamException;
    
    float[] getAttributeAsFloatArray(final int p0) throws XMLStreamException;
    
    double[] getAttributeAsDoubleArray(final int p0) throws XMLStreamException;
    
    int getAttributeAsArray(final int p0, final TypedArrayDecoder p1) throws XMLStreamException;
}
