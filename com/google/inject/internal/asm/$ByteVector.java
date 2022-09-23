// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.asm;

public class $ByteVector
{
    byte[] a;
    int b;
    
    public $ByteVector() {
        this.a = new byte[64];
    }
    
    public $ByteVector(final int n) {
        this.a = new byte[n];
    }
    
    public $ByteVector putByte(final int n) {
        int b = this.b;
        if (b + 1 > this.a.length) {
            this.a(1);
        }
        this.a[b++] = (byte)n;
        this.b = b;
        return this;
    }
    
    $ByteVector a(final int n, final int n2) {
        int b = this.b;
        if (b + 2 > this.a.length) {
            this.a(2);
        }
        final byte[] a = this.a;
        a[b++] = (byte)n;
        a[b++] = (byte)n2;
        this.b = b;
        return this;
    }
    
    public $ByteVector putShort(final int n) {
        int b = this.b;
        if (b + 2 > this.a.length) {
            this.a(2);
        }
        final byte[] a = this.a;
        a[b++] = (byte)(n >>> 8);
        a[b++] = (byte)n;
        this.b = b;
        return this;
    }
    
    $ByteVector b(final int n, final int n2) {
        int b = this.b;
        if (b + 3 > this.a.length) {
            this.a(3);
        }
        final byte[] a = this.a;
        a[b++] = (byte)n;
        a[b++] = (byte)(n2 >>> 8);
        a[b++] = (byte)n2;
        this.b = b;
        return this;
    }
    
    public $ByteVector putInt(final int n) {
        int b = this.b;
        if (b + 4 > this.a.length) {
            this.a(4);
        }
        final byte[] a = this.a;
        a[b++] = (byte)(n >>> 24);
        a[b++] = (byte)(n >>> 16);
        a[b++] = (byte)(n >>> 8);
        a[b++] = (byte)n;
        this.b = b;
        return this;
    }
    
    public $ByteVector putLong(final long n) {
        int b = this.b;
        if (b + 8 > this.a.length) {
            this.a(8);
        }
        final byte[] a = this.a;
        final int n2 = (int)(n >>> 32);
        a[b++] = (byte)(n2 >>> 24);
        a[b++] = (byte)(n2 >>> 16);
        a[b++] = (byte)(n2 >>> 8);
        a[b++] = (byte)n2;
        final int n3 = (int)n;
        a[b++] = (byte)(n3 >>> 24);
        a[b++] = (byte)(n3 >>> 16);
        a[b++] = (byte)(n3 >>> 8);
        a[b++] = (byte)n3;
        this.b = b;
        return this;
    }
    
    public $ByteVector putUTF8(final String s) {
        final int length = s.length();
        if (this.b + 2 + length > this.a.length) {
            this.a(2 + length);
        }
        int b = this.b;
        byte[] array = this.a;
        array[b++] = (byte)(length >>> 8);
        array[b++] = (byte)length;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 < '\u0001' || char1 > '\u007f') {
                int n = i;
                for (int j = i; j < length; ++j) {
                    final char char2 = s.charAt(j);
                    if (char2 >= '\u0001' && char2 <= '\u007f') {
                        ++n;
                    }
                    else if (char2 > '\u07ff') {
                        n += 3;
                    }
                    else {
                        n += 2;
                    }
                }
                array[this.b] = (byte)(n >>> 8);
                array[this.b + 1] = (byte)n;
                if (this.b + 2 + n > array.length) {
                    this.b = b;
                    this.a(2 + n);
                    array = this.a;
                }
                for (int k = i; k < length; ++k) {
                    final char char3 = s.charAt(k);
                    if (char3 >= '\u0001' && char3 <= '\u007f') {
                        array[b++] = (byte)char3;
                    }
                    else if (char3 > '\u07ff') {
                        array[b++] = (byte)(0xE0 | (char3 >> 12 & 0xF));
                        array[b++] = (byte)(0x80 | (char3 >> 6 & 0x3F));
                        array[b++] = (byte)(0x80 | (char3 & '?'));
                    }
                    else {
                        array[b++] = (byte)(0xC0 | (char3 >> 6 & 0x1F));
                        array[b++] = (byte)(0x80 | (char3 & '?'));
                    }
                }
                break;
            }
            array[b++] = (byte)char1;
        }
        this.b = b;
        return this;
    }
    
    public $ByteVector putByteArray(final byte[] array, final int n, final int n2) {
        if (this.b + n2 > this.a.length) {
            this.a(n2);
        }
        if (array != null) {
            System.arraycopy(array, n, this.a, this.b, n2);
        }
        this.b += n2;
        return this;
    }
    
    private void a(final int n) {
        final int n2 = 2 * this.a.length;
        final int n3 = this.b + n;
        final byte[] a = new byte[(n2 > n3) ? n2 : n3];
        System.arraycopy(this.a, 0, a, 0, this.b);
        this.a = a;
    }
}
