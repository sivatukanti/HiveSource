// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.typed;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface TypedXMLStreamWriter extends XMLStreamWriter
{
    void writeBoolean(final boolean p0) throws XMLStreamException;
    
    void writeInt(final int p0) throws XMLStreamException;
    
    void writeLong(final long p0) throws XMLStreamException;
    
    void writeFloat(final float p0) throws XMLStreamException;
    
    void writeDouble(final double p0) throws XMLStreamException;
    
    void writeInteger(final BigInteger p0) throws XMLStreamException;
    
    void writeDecimal(final BigDecimal p0) throws XMLStreamException;
    
    void writeQName(final QName p0) throws XMLStreamException;
    
    void writeBinary(final byte[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeBinary(final Base64Variant p0, final byte[] p1, final int p2, final int p3) throws XMLStreamException;
    
    void writeIntArray(final int[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeLongArray(final long[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeFloatArray(final float[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeDoubleArray(final double[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeBooleanAttribute(final String p0, final String p1, final String p2, final boolean p3) throws XMLStreamException;
    
    void writeIntAttribute(final String p0, final String p1, final String p2, final int p3) throws XMLStreamException;
    
    void writeLongAttribute(final String p0, final String p1, final String p2, final long p3) throws XMLStreamException;
    
    void writeFloatAttribute(final String p0, final String p1, final String p2, final float p3) throws XMLStreamException;
    
    void writeDoubleAttribute(final String p0, final String p1, final String p2, final double p3) throws XMLStreamException;
    
    void writeIntegerAttribute(final String p0, final String p1, final String p2, final BigInteger p3) throws XMLStreamException;
    
    void writeDecimalAttribute(final String p0, final String p1, final String p2, final BigDecimal p3) throws XMLStreamException;
    
    void writeQNameAttribute(final String p0, final String p1, final String p2, final QName p3) throws XMLStreamException;
    
    void writeBinaryAttribute(final String p0, final String p1, final String p2, final byte[] p3) throws XMLStreamException;
    
    void writeBinaryAttribute(final Base64Variant p0, final String p1, final String p2, final String p3, final byte[] p4) throws XMLStreamException;
    
    void writeIntArrayAttribute(final String p0, final String p1, final String p2, final int[] p3) throws XMLStreamException;
    
    void writeLongArrayAttribute(final String p0, final String p1, final String p2, final long[] p3) throws XMLStreamException;
    
    void writeFloatArrayAttribute(final String p0, final String p1, final String p2, final float[] p3) throws XMLStreamException;
    
    void writeDoubleArrayAttribute(final String p0, final String p1, final String p2, final double[] p3) throws XMLStreamException;
}
