// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io.nio;

import java.nio.channels.Channels;
import org.mortbay.io.Buffer;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import org.mortbay.io.AbstractBuffer;

public class DirectNIOBuffer extends AbstractBuffer implements NIOBuffer
{
    protected ByteBuffer _buf;
    private ReadableByteChannel _in;
    private InputStream _inStream;
    private WritableByteChannel _out;
    private OutputStream _outStream;
    
    public DirectNIOBuffer(final int size) {
        super(2, false);
        (this._buf = ByteBuffer.allocateDirect(size)).position(0);
        this._buf.limit(this._buf.capacity());
    }
    
    public DirectNIOBuffer(final ByteBuffer buffer, final boolean immutable) {
        super(immutable ? 0 : 2, false);
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException();
        }
        this._buf = buffer;
        this.setGetIndex(buffer.position());
        this.setPutIndex(buffer.limit());
    }
    
    public DirectNIOBuffer(final File file) throws IOException {
        super(1, false);
        final FileInputStream fis = new FileInputStream(file);
        final FileChannel fc = fis.getChannel();
        this._buf = fc.map(FileChannel.MapMode.READ_ONLY, 0L, file.length());
        this.setGetIndex(0);
        this.setPutIndex((int)file.length());
        this._access = 0;
    }
    
    public boolean isDirect() {
        return true;
    }
    
    public byte[] array() {
        return null;
    }
    
    public int capacity() {
        return this._buf.capacity();
    }
    
    public byte peek(final int position) {
        return this._buf.get(position);
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
        try {
            this._buf.position(index);
            this._buf.get(b, offset, l);
        }
        finally {
            this._buf.position(0);
        }
        return l;
    }
    
    public void poke(final int index, final byte b) {
        if (this.isReadOnly()) {
            throw new IllegalStateException("READONLY");
        }
        if (index < 0) {
            throw new IllegalArgumentException("index<0: " + index + "<0");
        }
        if (index > this.capacity()) {
            throw new IllegalArgumentException("index>capacity(): " + index + ">" + this.capacity());
        }
        this._buf.put(index, b);
    }
    
    public int poke(final int index, final Buffer src) {
        if (this.isReadOnly()) {
            throw new IllegalStateException("READONLY");
        }
        final byte[] array = src.array();
        if (array != null) {
            final int length = this.poke(index, array, src.getIndex(), src.length());
            return length;
        }
        final Buffer src_buf = src.buffer();
        if (src_buf instanceof DirectNIOBuffer) {
            ByteBuffer src_bytebuf = ((DirectNIOBuffer)src_buf)._buf;
            if (src_bytebuf == this._buf) {
                src_bytebuf = this._buf.duplicate();
            }
            try {
                this._buf.position(index);
                final int space = this._buf.remaining();
                int length2 = src.length();
                if (length2 > space) {
                    length2 = space;
                }
                src_bytebuf.position(src.getIndex());
                src_bytebuf.limit(src.getIndex() + length2);
                this._buf.put(src_bytebuf);
                return length2;
            }
            finally {
                this._buf.position(0);
                src_bytebuf.limit(src_bytebuf.capacity());
                src_bytebuf.position(0);
            }
        }
        return super.poke(index, src);
    }
    
    public int poke(final int index, final byte[] b, final int offset, int length) {
        if (this.isReadOnly()) {
            throw new IllegalStateException("READONLY");
        }
        if (index < 0) {
            throw new IllegalArgumentException("index<0: " + index + "<0");
        }
        if (index + length > this.capacity()) {
            length = this.capacity() - index;
            if (length < 0) {
                throw new IllegalArgumentException("index>capacity(): " + index + ">" + this.capacity());
            }
        }
        try {
            this._buf.position(index);
            final int space = this._buf.remaining();
            if (length > space) {
                length = space;
            }
            if (length > 0) {
                this._buf.put(b, offset, length);
            }
            return length;
        }
        finally {
            this._buf.position(0);
        }
    }
    
    public ByteBuffer getByteBuffer() {
        return this._buf;
    }
    
    public int readFrom(final InputStream in, int max) throws IOException {
        if (this._in == null || !this._in.isOpen() || in != this._inStream) {
            this._in = Channels.newChannel(in);
            this._inStream = in;
        }
        if (max < 0 || max > this.space()) {
            max = this.space();
        }
        int p = this.putIndex();
        try {
            int len = 0;
            int total = 0;
            int available = max;
            int loop = 0;
            while (total < max) {
                this._buf.position(p);
                this._buf.limit(p + available);
                len = this._in.read(this._buf);
                if (len < 0) {
                    this._in = null;
                    this._inStream = in;
                    break;
                }
                if (len > 0) {
                    p += len;
                    total += len;
                    available -= len;
                    this.setPutIndex(p);
                    loop = 0;
                }
                else if (loop++ > 1) {
                    break;
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
        catch (IOException e) {
            this._in = null;
            this._inStream = in;
            throw e;
        }
        finally {
            if (this._in != null && !this._in.isOpen()) {
                this._in = null;
                this._inStream = in;
            }
            this._buf.position(0);
            this._buf.limit(this._buf.capacity());
        }
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        if (this._out == null || !this._out.isOpen() || this._out != this._outStream) {
            this._out = Channels.newChannel(out);
            this._outStream = out;
        }
        synchronized (this._buf) {
            try {
                int loop = 0;
                while (this.hasContent() && this._out.isOpen()) {
                    this._buf.position(this.getIndex());
                    this._buf.limit(this.putIndex());
                    final int len = this._out.write(this._buf);
                    if (len < 0) {
                        break;
                    }
                    if (len > 0) {
                        this.skip(len);
                        loop = 0;
                    }
                    else {
                        if (loop++ > 1) {
                            break;
                        }
                        continue;
                    }
                }
            }
            catch (IOException e) {
                this._out = null;
                this._outStream = null;
                throw e;
            }
            finally {
                if (this._out != null && !this._out.isOpen()) {
                    this._out = null;
                    this._outStream = null;
                }
                this._buf.position(0);
                this._buf.limit(this._buf.capacity());
            }
        }
    }
}
