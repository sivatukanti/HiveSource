// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.log.Logger;

public abstract class AbstractBuffer implements Buffer
{
    private static final Logger LOG;
    private static final boolean __boundsChecking;
    protected static final String __IMMUTABLE = "IMMUTABLE";
    protected static final String __READONLY = "READONLY";
    protected static final String __READWRITE = "READWRITE";
    protected static final String __VOLATILE = "VOLATILE";
    protected int _access;
    protected boolean _volatile;
    protected int _get;
    protected int _put;
    protected int _hash;
    protected int _hashGet;
    protected int _hashPut;
    protected int _mark;
    protected String _string;
    protected View _view;
    
    public AbstractBuffer(final int access, final boolean isVolatile) {
        if (access == 0 && isVolatile) {
            throw new IllegalArgumentException("IMMUTABLE && VOLATILE");
        }
        this.setMarkIndex(-1);
        this._access = access;
        this._volatile = isVolatile;
    }
    
    public byte[] asArray() {
        final byte[] bytes = new byte[this.length()];
        final byte[] array = this.array();
        if (array != null) {
            System.arraycopy(array, this.getIndex(), bytes, 0, bytes.length);
        }
        else {
            this.peek(this.getIndex(), bytes, 0, this.length());
        }
        return bytes;
    }
    
    public ByteArrayBuffer duplicate(final int access) {
        final Buffer b = this.buffer();
        if (this instanceof CaseInsensitve || b instanceof CaseInsensitve) {
            return new ByteArrayBuffer.CaseInsensitive(this.asArray(), 0, this.length(), access);
        }
        return new ByteArrayBuffer(this.asArray(), 0, this.length(), access);
    }
    
    public Buffer asNonVolatileBuffer() {
        if (!this.isVolatile()) {
            return this;
        }
        return this.duplicate(this._access);
    }
    
    public Buffer asImmutableBuffer() {
        if (this.isImmutable()) {
            return this;
        }
        return this.duplicate(0);
    }
    
    public Buffer asReadOnlyBuffer() {
        if (this.isReadOnly()) {
            return this;
        }
        return new View(this, this.markIndex(), this.getIndex(), this.putIndex(), 1);
    }
    
    public Buffer asMutableBuffer() {
        if (!this.isImmutable()) {
            return this;
        }
        final Buffer b = this.buffer();
        if (b.isReadOnly()) {
            return this.duplicate(2);
        }
        return new View(b, this.markIndex(), this.getIndex(), this.putIndex(), this._access);
    }
    
    public Buffer buffer() {
        return this;
    }
    
    public void clear() {
        this.setMarkIndex(-1);
        this.setGetIndex(0);
        this.setPutIndex(0);
    }
    
    public void compact() {
        if (this.isReadOnly()) {
            throw new IllegalStateException("READONLY");
        }
        final int s = (this.markIndex() >= 0) ? this.markIndex() : this.getIndex();
        if (s > 0) {
            final byte[] array = this.array();
            final int length = this.putIndex() - s;
            if (length > 0) {
                if (array != null) {
                    System.arraycopy(this.array(), s, this.array(), 0, length);
                }
                else {
                    this.poke(0, this.peek(s, length));
                }
            }
            if (this.markIndex() > 0) {
                this.setMarkIndex(this.markIndex() - s);
            }
            this.setGetIndex(this.getIndex() - s);
            this.setPutIndex(this.putIndex() - s);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof Buffer)) {
            return false;
        }
        final Buffer b = (Buffer)obj;
        if (this instanceof CaseInsensitve || b instanceof CaseInsensitve) {
            return this.equalsIgnoreCase(b);
        }
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
            final byte b2 = this.peek(i);
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
        if (b.length() != this.length()) {
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
        final byte[] array = this.array();
        final byte[] barray = b.array();
        if (array != null && barray != null) {
            int i = this.putIndex();
            while (i-- > get) {
                byte b2 = array[i];
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
        else {
            int i = this.putIndex();
            while (i-- > get) {
                byte b2 = this.peek(i);
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
        return true;
    }
    
    public byte get() {
        return this.peek(this._get++);
    }
    
    public int get(final byte[] b, final int offset, int length) {
        final int gi = this.getIndex();
        final int l = this.length();
        if (l == 0) {
            return -1;
        }
        if (length > l) {
            length = l;
        }
        length = this.peek(gi, b, offset, length);
        if (length > 0) {
            this.setGetIndex(gi + length);
        }
        return length;
    }
    
    public Buffer get(final int length) {
        final int gi = this.getIndex();
        final Buffer view = this.peek(gi, length);
        this.setGetIndex(gi + length);
        return view;
    }
    
    public final int getIndex() {
        return this._get;
    }
    
    public boolean hasContent() {
        return this._put > this._get;
    }
    
    @Override
    public int hashCode() {
        if (this._hash == 0 || this._hashGet != this._get || this._hashPut != this._put) {
            final int get = this.getIndex();
            final byte[] array = this.array();
            if (array == null) {
                int i = this.putIndex();
                while (i-- > get) {
                    byte b = this.peek(i);
                    if (97 <= b && b <= 122) {
                        b = (byte)(b - 97 + 65);
                    }
                    this._hash = 31 * this._hash + b;
                }
            }
            else {
                int i = this.putIndex();
                while (i-- > get) {
                    byte b = array[i];
                    if (97 <= b && b <= 122) {
                        b = (byte)(b - 97 + 65);
                    }
                    this._hash = 31 * this._hash + b;
                }
            }
            if (this._hash == 0) {
                this._hash = -1;
            }
            this._hashGet = this._get;
            this._hashPut = this._put;
        }
        return this._hash;
    }
    
    public boolean isImmutable() {
        return this._access <= 0;
    }
    
    public boolean isReadOnly() {
        return this._access <= 1;
    }
    
    public boolean isVolatile() {
        return this._volatile;
    }
    
    public int length() {
        return this._put - this._get;
    }
    
    public void mark() {
        this.setMarkIndex(this._get - 1);
    }
    
    public void mark(final int offset) {
        this.setMarkIndex(this._get + offset);
    }
    
    public int markIndex() {
        return this._mark;
    }
    
    public byte peek() {
        return this.peek(this._get);
    }
    
    public Buffer peek(final int index, final int length) {
        if (this._view == null) {
            this._view = new View(this, -1, index, index + length, this.isReadOnly() ? 1 : 2);
        }
        else {
            this._view.update(this.buffer());
            this._view.setMarkIndex(-1);
            this._view.setGetIndex(0);
            this._view.setPutIndex(index + length);
            this._view.setGetIndex(index);
        }
        return this._view;
    }
    
    public int poke(int index, final Buffer src) {
        this._hash = 0;
        int length = src.length();
        if (index + length > this.capacity()) {
            length = this.capacity() - index;
        }
        final byte[] src_array = src.array();
        final byte[] dst_array = this.array();
        if (src_array != null && dst_array != null) {
            System.arraycopy(src_array, src.getIndex(), dst_array, index, length);
        }
        else if (src_array != null) {
            int s = src.getIndex();
            for (int i = 0; i < length; ++i) {
                this.poke(index++, src_array[s++]);
            }
        }
        else if (dst_array != null) {
            int s = src.getIndex();
            for (int i = 0; i < length; ++i) {
                dst_array[index++] = src.peek(s++);
            }
        }
        else {
            int s = src.getIndex();
            for (int i = 0; i < length; ++i) {
                this.poke(index++, src.peek(s++));
            }
        }
        return length;
    }
    
    public int poke(int index, final byte[] b, final int offset, int length) {
        this._hash = 0;
        if (index + length > this.capacity()) {
            length = this.capacity() - index;
        }
        final byte[] dst_array = this.array();
        if (dst_array != null) {
            System.arraycopy(b, offset, dst_array, index, length);
        }
        else {
            int s = offset;
            for (int i = 0; i < length; ++i) {
                this.poke(index++, b[s++]);
            }
        }
        return length;
    }
    
    public int put(final Buffer src) {
        final int pi = this.putIndex();
        final int l = this.poke(pi, src);
        this.setPutIndex(pi + l);
        return l;
    }
    
    public void put(final byte b) {
        final int pi = this.putIndex();
        this.poke(pi, b);
        this.setPutIndex(pi + 1);
    }
    
    public int put(final byte[] b, final int offset, final int length) {
        final int pi = this.putIndex();
        final int l = this.poke(pi, b, offset, length);
        this.setPutIndex(pi + l);
        return l;
    }
    
    public int put(final byte[] b) {
        final int pi = this.putIndex();
        final int l = this.poke(pi, b, 0, b.length);
        this.setPutIndex(pi + l);
        return l;
    }
    
    public final int putIndex() {
        return this._put;
    }
    
    public void reset() {
        if (this.markIndex() >= 0) {
            this.setGetIndex(this.markIndex());
        }
    }
    
    public void rewind() {
        this.setGetIndex(0);
        this.setMarkIndex(-1);
    }
    
    public void setGetIndex(final int getIndex) {
        if (AbstractBuffer.__boundsChecking) {
            if (this.isImmutable()) {
                throw new IllegalStateException("IMMUTABLE");
            }
            if (getIndex < 0) {
                throw new IllegalArgumentException("getIndex<0: " + getIndex + "<0");
            }
            if (getIndex > this.putIndex()) {
                throw new IllegalArgumentException("getIndex>putIndex: " + getIndex + ">" + this.putIndex());
            }
        }
        this._get = getIndex;
        this._hash = 0;
    }
    
    public void setMarkIndex(final int index) {
        if (index >= 0 && this.isImmutable()) {
            throw new IllegalStateException("IMMUTABLE");
        }
        this._mark = index;
    }
    
    public void setPutIndex(final int putIndex) {
        if (AbstractBuffer.__boundsChecking) {
            if (this.isImmutable()) {
                throw new IllegalStateException("IMMUTABLE");
            }
            if (putIndex > this.capacity()) {
                throw new IllegalArgumentException("putIndex>capacity: " + putIndex + ">" + this.capacity());
            }
            if (this.getIndex() > putIndex) {
                throw new IllegalArgumentException("getIndex>putIndex: " + this.getIndex() + ">" + putIndex);
            }
        }
        this._put = putIndex;
        this._hash = 0;
    }
    
    public int skip(int n) {
        if (this.length() < n) {
            n = this.length();
        }
        this.setGetIndex(this.getIndex() + n);
        return n;
    }
    
    public Buffer slice() {
        return this.peek(this.getIndex(), this.length());
    }
    
    public Buffer sliceFromMark() {
        return this.sliceFromMark(this.getIndex() - this.markIndex() - 1);
    }
    
    public Buffer sliceFromMark(final int length) {
        if (this.markIndex() < 0) {
            return null;
        }
        final Buffer view = this.peek(this.markIndex(), length);
        this.setMarkIndex(-1);
        return view;
    }
    
    public int space() {
        return this.capacity() - this._put;
    }
    
    public String toDetailString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("[");
        buf.append(super.hashCode());
        buf.append(",");
        buf.append(this.buffer().hashCode());
        buf.append(",m=");
        buf.append(this.markIndex());
        buf.append(",g=");
        buf.append(this.getIndex());
        buf.append(",p=");
        buf.append(this.putIndex());
        buf.append(",c=");
        buf.append(this.capacity());
        buf.append("]={");
        if (this.markIndex() >= 0) {
            for (int i = this.markIndex(); i < this.getIndex(); ++i) {
                final byte b = this.peek(i);
                TypeUtil.toHex(b, buf);
            }
            buf.append("}{");
        }
        int count = 0;
        for (int j = this.getIndex(); j < this.putIndex(); ++j) {
            final byte b2 = this.peek(j);
            TypeUtil.toHex(b2, buf);
            if (count++ == 50 && this.putIndex() - j > 20) {
                buf.append(" ... ");
                j = this.putIndex() - 20;
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    @Override
    public String toString() {
        if (this.isImmutable()) {
            if (this._string == null) {
                this._string = new String(this.asArray(), 0, this.length());
            }
            return this._string;
        }
        return new String(this.asArray(), 0, this.length());
    }
    
    public String toString(final String charset) {
        try {
            final byte[] bytes = this.array();
            if (bytes != null) {
                return new String(bytes, this.getIndex(), this.length(), charset);
            }
            return new String(this.asArray(), 0, this.length(), charset);
        }
        catch (Exception e) {
            AbstractBuffer.LOG.warn(e);
            return new String(this.asArray(), 0, this.length());
        }
    }
    
    public String toDebugString() {
        return this.getClass() + "@" + super.hashCode();
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        final byte[] array = this.array();
        if (array != null) {
            out.write(array, this.getIndex(), this.length());
        }
        else {
            int len = this.length();
            final byte[] buf = new byte[(len > 1024) ? 1024 : len];
            int offset = this._get;
            while (len > 0) {
                final int l = this.peek(offset, buf, 0, (len > buf.length) ? buf.length : len);
                out.write(buf, 0, l);
                offset += l;
                len -= l;
            }
        }
        this.clear();
    }
    
    public int readFrom(final InputStream in, final int max) throws IOException {
        final byte[] array = this.array();
        int s = this.space();
        if (s > max) {
            s = max;
        }
        if (array != null) {
            final int l = in.read(array, this._put, s);
            if (l > 0) {
                this._put += l;
            }
            return l;
        }
        final byte[] buf = new byte[(s > 1024) ? 1024 : s];
        final int total = 0;
        while (s > 0) {
            final int i = in.read(buf, 0, buf.length);
            if (i < 0) {
                return (total > 0) ? total : -1;
            }
            final int p = this.put(buf, 0, i);
            assert i == p;
            s -= i;
        }
        return total;
    }
    
    static {
        LOG = Log.getLogger(AbstractBuffer.class);
        __boundsChecking = Boolean.getBoolean("org.eclipse.jetty.io.AbstractBuffer.boundsChecking");
    }
}
