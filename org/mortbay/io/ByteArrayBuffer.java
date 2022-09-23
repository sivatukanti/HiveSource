// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ByteArrayBuffer extends AbstractBuffer
{
    protected byte[] _bytes;
    
    protected ByteArrayBuffer(final int access, final boolean isVolatile) {
        super(access, isVolatile);
    }
    
    public ByteArrayBuffer(final byte[] bytes) {
        this(bytes, 0, bytes.length, 2);
    }
    
    public ByteArrayBuffer(final byte[] bytes, final int index, final int length) {
        this(bytes, index, length, 2);
    }
    
    public ByteArrayBuffer(final byte[] bytes, final int index, final int length, final int access) {
        super(2, false);
        this._bytes = bytes;
        this.setPutIndex(index + length);
        this.setGetIndex(index);
        this._access = access;
    }
    
    public ByteArrayBuffer(final byte[] bytes, final int index, final int length, final int access, final boolean isVolatile) {
        super(2, isVolatile);
        this._bytes = bytes;
        this.setPutIndex(index + length);
        this.setGetIndex(index);
        this._access = access;
    }
    
    public ByteArrayBuffer(final int size) {
        this(new byte[size], 0, size, 2);
        this.setPutIndex(0);
    }
    
    public ByteArrayBuffer(final String value) {
        super(2, false);
        this._bytes = Portable.getBytes(value);
        this.setGetIndex(0);
        this.setPutIndex(this._bytes.length);
        this._access = 0;
        this._string = value;
    }
    
    public ByteArrayBuffer(final String value, final String encoding) throws UnsupportedEncodingException {
        super(2, false);
        this._bytes = value.getBytes(encoding);
        this.setGetIndex(0);
        this.setPutIndex(this._bytes.length);
        this._access = 0;
        this._string = value;
    }
    
    public byte[] array() {
        return this._bytes;
    }
    
    public int capacity() {
        return this._bytes.length;
    }
    
    public void compact() {
        if (this.isReadOnly()) {
            throw new IllegalStateException("READONLY");
        }
        final int s = (this.markIndex() >= 0) ? this.markIndex() : this.getIndex();
        if (s > 0) {
            final int length = this.putIndex() - s;
            if (length > 0) {
                Portable.arraycopy(this._bytes, s, this._bytes, 0, length);
            }
            if (this.markIndex() > 0) {
                this.setMarkIndex(this.markIndex() - s);
            }
            this.setGetIndex(this.getIndex() - s);
            this.setPutIndex(this.putIndex() - s);
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof Buffer)) {
            return false;
        }
        if (obj instanceof Buffer.CaseInsensitve) {
            return this.equalsIgnoreCase((Buffer)obj);
        }
        final Buffer b = (Buffer)obj;
        if (b.length() != this.length()) {
            return false;
        }
        if (this._hash != 0 && obj instanceof AbstractBuffer) {
            final AbstractBuffer ab = (AbstractBuffer)obj;
            if (ab._hash != 0 && this._hash != ab._hash) {
                return false;
            }
        }
        final int get = this.getIndex();
        int bi = b.putIndex();
        int i = this.putIndex();
        while (i-- > get) {
            final byte b2 = this._bytes[i];
            final byte b3 = b.peek(--bi);
            if (b2 != b3) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equalsIgnoreCase(final Buffer b) {
        if (b == this) {
            return true;
        }
        if (b == null || b.length() != this.length()) {
            return false;
        }
        if (this._hash != 0 && b instanceof AbstractBuffer) {
            final AbstractBuffer ab = (AbstractBuffer)b;
            if (ab._hash != 0 && this._hash != ab._hash) {
                return false;
            }
        }
        final int get = this.getIndex();
        int bi = b.putIndex();
        final byte[] barray = b.array();
        if (barray == null) {
            int i = this.putIndex();
            while (i-- > get) {
                byte b2 = this._bytes[i];
                byte b3 = b.peek(--bi);
                if (b2 != b3) {
                    if (97 <= b2 && b2 <= 122) {
                        b2 = (byte)(b2 - 97 + 65);
                    }
                    if (97 <= b3 && b3 <= 122) {
                        b3 = (byte)(b3 - 97 + 65);
                    }
                    if (b2 != b3) {
                        return false;
                    }
                    continue;
                }
            }
        }
        else {
            int i = this.putIndex();
            while (i-- > get) {
                byte b2 = this._bytes[i];
                byte b3 = barray[--bi];
                if (b2 != b3) {
                    if (97 <= b2 && b2 <= 122) {
                        b2 = (byte)(b2 - 97 + 65);
                    }
                    if (97 <= b3 && b3 <= 122) {
                        b3 = (byte)(b3 - 97 + 65);
                    }
                    if (b2 != b3) {
                        return false;
                    }
                    continue;
                }
            }
        }
        return true;
    }
    
    public byte get() {
        return this._bytes[this._get++];
    }
    
    public int hashCode() {
        if (this._hash == 0 || this._hashGet != this._get || this._hashPut != this._put) {
            final int get = this.getIndex();
            int i = this.putIndex();
            while (i-- > get) {
                byte b = this._bytes[i];
                if (97 <= b && b <= 122) {
                    b = (byte)(b - 97 + 65);
                }
                this._hash = 31 * this._hash + b;
            }
            if (this._hash == 0) {
                this._hash = -1;
            }
            this._hashGet = this._get;
            this._hashPut = this._put;
        }
        return this._hash;
    }
    
    public byte peek(final int index) {
        return this._bytes[index];
    }
    
    public int peek(final int index, final byte[] b, final int offset, final int length) {
        int l = length;
        if (index + l > this.capacity()) {
            l = this.capacity() - index;
            if (l == 0) {
                return -1;
            }
        }
        if (l < 0) {
            return -1;
        }
        Portable.arraycopy(this._bytes, index, b, offset, l);
        return l;
    }
    
    public void poke(final int index, final byte b) {
        this._bytes[index] = b;
    }
    
    public int poke(int index, final Buffer src) {
        this._hash = 0;
        int length = src.length();
        if (index + length > this.capacity()) {
            length = this.capacity() - index;
        }
        final byte[] src_array = src.array();
        if (src_array != null) {
            Portable.arraycopy(src_array, src.getIndex(), this._bytes, index, length);
        }
        else if (src_array != null) {
            int s = src.getIndex();
            for (int i = 0; i < length; ++i) {
                this.poke(index++, src_array[s++]);
            }
        }
        else {
            int s = src.getIndex();
            for (int i = 0; i < length; ++i) {
                this._bytes[index++] = src.peek(s++);
            }
        }
        return length;
    }
    
    public int poke(final int index, final byte[] b, final int offset, int length) {
        this._hash = 0;
        if (index + length > this.capacity()) {
            length = this.capacity() - index;
        }
        Portable.arraycopy(b, offset, this._bytes, index, length);
        return length;
    }
    
    public void wrap(final byte[] b, final int off, final int len) {
        if (this.isReadOnly()) {
            throw new IllegalStateException("READONLY");
        }
        if (this.isImmutable()) {
            throw new IllegalStateException("IMMUTABLE");
        }
        this._bytes = b;
        this.clear();
        this.setGetIndex(off);
        this.setPutIndex(off + len);
    }
    
    public void wrap(final byte[] b) {
        if (this.isReadOnly()) {
            throw new IllegalStateException("READONLY");
        }
        if (this.isImmutable()) {
            throw new IllegalStateException("IMMUTABLE");
        }
        this._bytes = b;
        this.setGetIndex(0);
        this.setPutIndex(b.length);
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        out.write(this._bytes, this.getIndex(), this.length());
        this.clear();
    }
    
    public int readFrom(final InputStream in, int max) throws IOException {
        if (max < 0 || max > this.space()) {
            max = this.space();
        }
        int p = this.putIndex();
        int len = 0;
        int total = 0;
        int available = max;
        while (total < max) {
            len = in.read(this._bytes, p, available);
            if (len < 0) {
                break;
            }
            if (len > 0) {
                p += len;
                total += len;
                available -= len;
                this.setPutIndex(p);
            }
            if (in.available() <= 0) {
                break;
            }
        }
        if (len < 0 && total == 0) {
            return -1;
        }
        return total;
    }
    
    public int space() {
        return this._bytes.length - this._put;
    }
    
    public static class CaseInsensitive extends ByteArrayBuffer implements Buffer.CaseInsensitve
    {
        public CaseInsensitive(final String s) {
            super(s);
        }
        
        public CaseInsensitive(final byte[] b, final int o, final int l, final int rw) {
            super(b, o, l, rw);
        }
        
        public boolean equals(final Object obj) {
            return this.equalsIgnoreCase((Buffer)obj);
        }
    }
}
