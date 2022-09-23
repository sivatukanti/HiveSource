// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Arrays;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ByteArrayISO8859Writer extends Writer
{
    private byte[] _buf;
    private int _size;
    private ByteArrayOutputStream2 _bout;
    private OutputStreamWriter _writer;
    private boolean _fixed;
    
    public ByteArrayISO8859Writer() {
        this._bout = null;
        this._writer = null;
        this._fixed = false;
        this._buf = new byte[2048];
    }
    
    public ByteArrayISO8859Writer(final int capacity) {
        this._bout = null;
        this._writer = null;
        this._fixed = false;
        this._buf = new byte[capacity];
    }
    
    public ByteArrayISO8859Writer(final byte[] buf) {
        this._bout = null;
        this._writer = null;
        this._fixed = false;
        this._buf = buf;
        this._fixed = true;
    }
    
    public Object getLock() {
        return this.lock;
    }
    
    public int size() {
        return this._size;
    }
    
    public int capacity() {
        return this._buf.length;
    }
    
    public int spareCapacity() {
        return this._buf.length - this._size;
    }
    
    public void setLength(final int l) {
        this._size = l;
    }
    
    public byte[] getBuf() {
        return this._buf;
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        out.write(this._buf, 0, this._size);
    }
    
    public void write(final char c) throws IOException {
        this.ensureSpareCapacity(1);
        if (c >= '\0' && c <= '\u007f') {
            this._buf[this._size++] = (byte)c;
        }
        else {
            final char[] ca = { c };
            this.writeEncoded(ca, 0, 1);
        }
    }
    
    @Override
    public void write(final char[] ca) throws IOException {
        this.ensureSpareCapacity(ca.length);
        for (int i = 0; i < ca.length; ++i) {
            final char c = ca[i];
            if (c < '\0' || c > '\u007f') {
                this.writeEncoded(ca, i, ca.length - i);
                break;
            }
            this._buf[this._size++] = (byte)c;
        }
    }
    
    @Override
    public void write(final char[] ca, final int offset, final int length) throws IOException {
        this.ensureSpareCapacity(length);
        for (int i = 0; i < length; ++i) {
            final char c = ca[offset + i];
            if (c < '\0' || c > '\u007f') {
                this.writeEncoded(ca, offset + i, length - i);
                break;
            }
            this._buf[this._size++] = (byte)c;
        }
    }
    
    @Override
    public void write(final String s) throws IOException {
        if (s == null) {
            this.write("null", 0, 4);
            return;
        }
        final int length = s.length();
        this.ensureSpareCapacity(length);
        for (int i = 0; i < length; ++i) {
            final char c = s.charAt(i);
            if (c < '\0' || c > '\u007f') {
                this.writeEncoded(s.toCharArray(), i, length - i);
                break;
            }
            this._buf[this._size++] = (byte)c;
        }
    }
    
    @Override
    public void write(final String s, final int offset, final int length) throws IOException {
        this.ensureSpareCapacity(length);
        for (int i = 0; i < length; ++i) {
            final char c = s.charAt(offset + i);
            if (c < '\0' || c > '\u007f') {
                this.writeEncoded(s.toCharArray(), offset + i, length - i);
                break;
            }
            this._buf[this._size++] = (byte)c;
        }
    }
    
    private void writeEncoded(final char[] ca, final int offset, final int length) throws IOException {
        if (this._bout == null) {
            this._bout = new ByteArrayOutputStream2(2 * length);
            this._writer = new OutputStreamWriter(this._bout, StandardCharsets.ISO_8859_1);
        }
        else {
            this._bout.reset();
        }
        this._writer.write(ca, offset, length);
        this._writer.flush();
        this.ensureSpareCapacity(this._bout.getCount());
        System.arraycopy(this._bout.getBuf(), 0, this._buf, this._size, this._bout.getCount());
        this._size += this._bout.getCount();
    }
    
    @Override
    public void flush() {
    }
    
    public void resetWriter() {
        this._size = 0;
    }
    
    @Override
    public void close() {
    }
    
    public void destroy() {
        this._buf = null;
    }
    
    public void ensureSpareCapacity(final int n) throws IOException {
        if (this._size + n > this._buf.length) {
            if (this._fixed) {
                throw new IOException("Buffer overflow: " + this._buf.length);
            }
            this._buf = Arrays.copyOf(this._buf, (this._buf.length + n) * 4 / 3);
        }
    }
    
    public byte[] getByteArray() {
        return Arrays.copyOf(this._buf, this._size);
    }
}
