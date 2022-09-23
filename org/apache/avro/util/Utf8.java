// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.util;

import java.io.UnsupportedEncodingException;
import org.apache.avro.io.BinaryData;
import java.nio.charset.Charset;

public class Utf8 implements Comparable<Utf8>, CharSequence
{
    private static final byte[] EMPTY;
    private static final Charset UTF8;
    private byte[] bytes;
    private int length;
    private String string;
    private static final Utf8Converter UTF8_CONVERTER;
    
    public Utf8() {
        this.bytes = Utf8.EMPTY;
    }
    
    public Utf8(final String string) {
        this.bytes = Utf8.EMPTY;
        this.bytes = getBytesFor(string);
        this.length = this.bytes.length;
        this.string = string;
    }
    
    public Utf8(final Utf8 other) {
        this.bytes = Utf8.EMPTY;
        this.length = other.length;
        this.bytes = new byte[other.length];
        System.arraycopy(other.bytes, 0, this.bytes, 0, this.length);
        this.string = other.string;
    }
    
    public Utf8(final byte[] bytes) {
        this.bytes = Utf8.EMPTY;
        this.bytes = bytes;
        this.length = bytes.length;
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    @Deprecated
    public int getLength() {
        return this.length;
    }
    
    public int getByteLength() {
        return this.length;
    }
    
    @Deprecated
    public Utf8 setLength(final int newLength) {
        return this.setByteLength(newLength);
    }
    
    public Utf8 setByteLength(final int newLength) {
        if (this.bytes.length < newLength) {
            final byte[] newBytes = new byte[newLength];
            System.arraycopy(this.bytes, 0, newBytes, 0, this.length);
            this.bytes = newBytes;
        }
        this.length = newLength;
        this.string = null;
        return this;
    }
    
    public Utf8 set(final String string) {
        this.bytes = getBytesFor(string);
        this.length = this.bytes.length;
        this.string = string;
        return this;
    }
    
    @Override
    public String toString() {
        if (this.length == 0) {
            return "";
        }
        if (this.string == null) {
            this.string = Utf8.UTF8_CONVERTER.fromUtf8(this.bytes, this.length);
        }
        return this.string;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Utf8)) {
            return false;
        }
        final Utf8 that = (Utf8)o;
        if (this.length != that.length) {
            return false;
        }
        final byte[] thatBytes = that.bytes;
        for (int i = 0; i < this.length; ++i) {
            if (this.bytes[i] != thatBytes[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.length; ++i) {
            hash = hash * 31 + this.bytes[i];
        }
        return hash;
    }
    
    @Override
    public int compareTo(final Utf8 that) {
        return BinaryData.compareBytes(this.bytes, 0, this.length, that.bytes, 0, that.length);
    }
    
    @Override
    public char charAt(final int index) {
        return this.toString().charAt(index);
    }
    
    @Override
    public int length() {
        return this.toString().length();
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.toString().subSequence(start, end);
    }
    
    public static final byte[] getBytesFor(final String str) {
        return Utf8.UTF8_CONVERTER.toUtf8(str);
    }
    
    static {
        EMPTY = new byte[0];
        UTF8 = Charset.forName("UTF-8");
        UTF8_CONVERTER = (System.getProperty("java.version").startsWith("1.6.") ? new Utf8Converter() {
            @Override
            public String fromUtf8(final byte[] bytes, final int length) {
                try {
                    return new String(bytes, 0, length, "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            
            @Override
            public byte[] toUtf8(final String str) {
                try {
                    return str.getBytes("UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        } : new Utf8Converter() {
            @Override
            public String fromUtf8(final byte[] bytes, final int length) {
                return new String(bytes, 0, length, Utf8.UTF8);
            }
            
            @Override
            public byte[] toUtf8(final String str) {
                return str.getBytes(Utf8.UTF8);
            }
        });
    }
    
    private abstract static class Utf8Converter
    {
        public abstract String fromUtf8(final byte[] p0, final int p1);
        
        public abstract byte[] toUtf8(final String p0);
    }
}
