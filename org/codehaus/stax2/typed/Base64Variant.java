// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.typed;

import java.util.Arrays;

public final class Base64Variant
{
    static final char PADDING_CHAR_NONE = '\0';
    public static final int BASE64_VALUE_INVALID = -1;
    public static final int BASE64_VALUE_PADDING = -2;
    private final int[] _asciiToBase64;
    private final char[] _base64ToAsciiC;
    private final byte[] _base64ToAsciiB;
    final String _name;
    final boolean _usesPadding;
    final char _paddingChar;
    final int _maxLineLength;
    
    public Base64Variant(final String name, final String s, final boolean usesPadding, final char paddingChar, final int maxLineLength) {
        this._asciiToBase64 = new int[128];
        this._base64ToAsciiC = new char[64];
        this._base64ToAsciiB = new byte[64];
        this._name = name;
        this._usesPadding = usesPadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
        final int length = s.length();
        if (length != 64) {
            throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + length + ")");
        }
        s.getChars(0, length, this._base64ToAsciiC, 0);
        Arrays.fill(this._asciiToBase64, -1);
        for (int i = 0; i < length; ++i) {
            final char c = this._base64ToAsciiC[i];
            this._base64ToAsciiB[i] = (byte)c;
            this._asciiToBase64[c] = i;
        }
        if (usesPadding) {
            this._asciiToBase64[paddingChar] = -2;
        }
    }
    
    public Base64Variant(final Base64Variant base64Variant, final String s, final int n) {
        this(base64Variant, s, base64Variant._usesPadding, base64Variant._paddingChar, n);
    }
    
    public Base64Variant(final Base64Variant base64Variant, final String name, final boolean usesPadding, final char paddingChar, final int maxLineLength) {
        this._asciiToBase64 = new int[128];
        this._base64ToAsciiC = new char[64];
        this._base64ToAsciiB = new byte[64];
        this._name = name;
        final byte[] base64ToAsciiB = base64Variant._base64ToAsciiB;
        System.arraycopy(base64ToAsciiB, 0, this._base64ToAsciiB, 0, base64ToAsciiB.length);
        final char[] base64ToAsciiC = base64Variant._base64ToAsciiC;
        System.arraycopy(base64ToAsciiC, 0, this._base64ToAsciiC, 0, base64ToAsciiC.length);
        final int[] asciiToBase64 = base64Variant._asciiToBase64;
        System.arraycopy(asciiToBase64, 0, this._asciiToBase64, 0, asciiToBase64.length);
        this._usesPadding = usesPadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
    }
    
    public String getName() {
        return this._name;
    }
    
    public boolean usesPadding() {
        return this._usesPadding;
    }
    
    public boolean usesPaddingChar(final char c) {
        return c == this._paddingChar;
    }
    
    public char getPaddingChar() {
        return this._paddingChar;
    }
    
    public byte getPaddingByte() {
        return (byte)this._paddingChar;
    }
    
    public int getMaxLineLength() {
        return this._maxLineLength;
    }
    
    public int decodeBase64Char(final char c) {
        return (c <= '\u007f') ? this._asciiToBase64[c] : -1;
    }
    
    public int decodeBase64Byte(final byte b) {
        return (b <= 127) ? this._asciiToBase64[b] : -1;
    }
    
    public char encodeBase64BitsAsChar(final int n) {
        return this._base64ToAsciiC[n];
    }
    
    public int encodeBase64Chunk(final int n, final char[] array, int n2) {
        array[n2++] = this._base64ToAsciiC[n >> 18 & 0x3F];
        array[n2++] = this._base64ToAsciiC[n >> 12 & 0x3F];
        array[n2++] = this._base64ToAsciiC[n >> 6 & 0x3F];
        array[n2++] = this._base64ToAsciiC[n & 0x3F];
        return n2;
    }
    
    public int encodeBase64Partial(final int n, final int n2, final char[] array, int n3) {
        array[n3++] = this._base64ToAsciiC[n >> 18 & 0x3F];
        array[n3++] = this._base64ToAsciiC[n >> 12 & 0x3F];
        if (this._usesPadding) {
            array[n3++] = ((n2 == 2) ? this._base64ToAsciiC[n >> 6 & 0x3F] : this._paddingChar);
            array[n3++] = this._paddingChar;
        }
        else if (n2 == 2) {
            array[n3++] = this._base64ToAsciiC[n >> 6 & 0x3F];
        }
        return n3;
    }
    
    public byte encodeBase64BitsAsByte(final int n) {
        return this._base64ToAsciiB[n];
    }
    
    public int encodeBase64Chunk(final int n, final byte[] array, int n2) {
        array[n2++] = this._base64ToAsciiB[n >> 18 & 0x3F];
        array[n2++] = this._base64ToAsciiB[n >> 12 & 0x3F];
        array[n2++] = this._base64ToAsciiB[n >> 6 & 0x3F];
        array[n2++] = this._base64ToAsciiB[n & 0x3F];
        return n2;
    }
    
    public int encodeBase64Partial(final int n, final int n2, final byte[] array, int n3) {
        array[n3++] = this._base64ToAsciiB[n >> 18 & 0x3F];
        array[n3++] = this._base64ToAsciiB[n >> 12 & 0x3F];
        if (this._usesPadding) {
            final byte b = (byte)this._paddingChar;
            array[n3++] = ((n2 == 2) ? this._base64ToAsciiB[n >> 6 & 0x3F] : b);
            array[n3++] = b;
        }
        else if (n2 == 2) {
            array[n3++] = this._base64ToAsciiB[n >> 6 & 0x3F];
        }
        return n3;
    }
    
    @Override
    public String toString() {
        return this._name;
    }
}
