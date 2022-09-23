// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

public class EncodingUtils
{
    public static final void encodeBigEndian(final int integer, final byte[] buf) {
        encodeBigEndian(integer, buf, 0);
    }
    
    public static final void encodeBigEndian(final int integer, final byte[] buf, final int offset) {
        buf[offset] = (byte)(0xFF & integer >> 24);
        buf[offset + 1] = (byte)(0xFF & integer >> 16);
        buf[offset + 2] = (byte)(0xFF & integer >> 8);
        buf[offset + 3] = (byte)(0xFF & integer);
    }
    
    public static final int decodeBigEndian(final byte[] buf) {
        return decodeBigEndian(buf, 0);
    }
    
    public static final int decodeBigEndian(final byte[] buf, final int offset) {
        return (buf[offset] & 0xFF) << 24 | (buf[offset + 1] & 0xFF) << 16 | (buf[offset + 2] & 0xFF) << 8 | (buf[offset + 3] & 0xFF);
    }
    
    public static final boolean testBit(final byte v, final int position) {
        return testBit((int)v, position);
    }
    
    public static final boolean testBit(final short v, final int position) {
        return testBit((int)v, position);
    }
    
    public static final boolean testBit(final int v, final int position) {
        return (v & 1 << position) != 0x0;
    }
    
    public static final boolean testBit(final long v, final int position) {
        return (v & 1L << position) != 0x0L;
    }
    
    public static final byte clearBit(final byte v, final int position) {
        return (byte)clearBit((int)v, position);
    }
    
    public static final short clearBit(final short v, final int position) {
        return (short)clearBit((int)v, position);
    }
    
    public static final int clearBit(final int v, final int position) {
        return v & ~(1 << position);
    }
    
    public static final long clearBit(final long v, final int position) {
        return v & ~(1L << position);
    }
    
    public static final byte setBit(final byte v, final int position, final boolean value) {
        return (byte)setBit((int)v, position, value);
    }
    
    public static final short setBit(final short v, final int position, final boolean value) {
        return (short)setBit((int)v, position, value);
    }
    
    public static final int setBit(final int v, final int position, final boolean value) {
        if (value) {
            return v | 1 << position;
        }
        return clearBit(v, position);
    }
    
    public static final long setBit(final long v, final int position, final boolean value) {
        if (value) {
            return v | 1L << position;
        }
        return clearBit(v, position);
    }
}
