// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal.jzlib;

public final class ZStream
{
    public byte[] next_in;
    public int next_in_index;
    public int avail_in;
    public long total_in;
    public byte[] next_out;
    public int next_out_index;
    public int avail_out;
    public long total_out;
    public String msg;
    Deflate dstate;
    Inflate istate;
    long adler;
    int crc32;
    
    public int inflateInit() {
        return this.inflateInit(15);
    }
    
    public int inflateInit(final Enum<?> wrapperType) {
        return this.inflateInit(15, wrapperType);
    }
    
    public int inflateInit(final int w) {
        return this.inflateInit(w, JZlib.WrapperType.ZLIB);
    }
    
    public int inflateInit(final int w, final Enum wrapperType) {
        this.istate = new Inflate();
        return this.istate.inflateInit(this, w, (JZlib.WrapperType)wrapperType);
    }
    
    public int inflate(final int f) {
        if (this.istate == null) {
            return -2;
        }
        return this.istate.inflate(this, f);
    }
    
    public int inflateEnd() {
        if (this.istate == null) {
            return -2;
        }
        final int ret = this.istate.inflateEnd(this);
        this.istate = null;
        return ret;
    }
    
    public int inflateSync() {
        if (this.istate == null) {
            return -2;
        }
        return this.istate.inflateSync(this);
    }
    
    public int inflateSetDictionary(final byte[] dictionary, final int dictLength) {
        if (this.istate == null) {
            return -2;
        }
        return Inflate.inflateSetDictionary(this, dictionary, dictLength);
    }
    
    public int deflateInit(final int level) {
        return this.deflateInit(level, 15);
    }
    
    public int deflateInit(final int level, final Enum<?> wrapperType) {
        return this.deflateInit(level, 15, wrapperType);
    }
    
    public int deflateInit(final int level, final int bits) {
        return this.deflateInit(level, bits, JZlib.WrapperType.ZLIB);
    }
    
    public int deflateInit(final int level, final int bits, final Enum<?> wrapperType) {
        return this.deflateInit(level, bits, 8, wrapperType);
    }
    
    public int deflateInit(final int level, final int bits, final int memLevel, final Enum wrapperType) {
        this.dstate = new Deflate();
        return this.dstate.deflateInit(this, level, bits, memLevel, (JZlib.WrapperType)wrapperType);
    }
    
    public int deflate(final int flush) {
        if (this.dstate == null) {
            return -2;
        }
        return this.dstate.deflate(this, flush);
    }
    
    public int deflateEnd() {
        if (this.dstate == null) {
            return -2;
        }
        final int ret = this.dstate.deflateEnd();
        this.dstate = null;
        return ret;
    }
    
    public int deflateParams(final int level, final int strategy) {
        if (this.dstate == null) {
            return -2;
        }
        return this.dstate.deflateParams(this, level, strategy);
    }
    
    public int deflateSetDictionary(final byte[] dictionary, final int dictLength) {
        if (this.dstate == null) {
            return -2;
        }
        return this.dstate.deflateSetDictionary(this, dictionary, dictLength);
    }
    
    void flush_pending() {
        int len = this.dstate.pending;
        if (len > this.avail_out) {
            len = this.avail_out;
        }
        if (len == 0) {
            return;
        }
        if (this.dstate.pending_buf.length <= this.dstate.pending_out || this.next_out.length <= this.next_out_index || this.dstate.pending_buf.length < this.dstate.pending_out + len || this.next_out.length < this.next_out_index + len) {
            System.out.println(this.dstate.pending_buf.length + ", " + this.dstate.pending_out + ", " + this.next_out.length + ", " + this.next_out_index + ", " + len);
            System.out.println("avail_out=" + this.avail_out);
        }
        System.arraycopy(this.dstate.pending_buf, this.dstate.pending_out, this.next_out, this.next_out_index, len);
        this.next_out_index += len;
        final Deflate dstate = this.dstate;
        dstate.pending_out += len;
        this.total_out += len;
        this.avail_out -= len;
        final Deflate dstate2 = this.dstate;
        dstate2.pending -= len;
        if (this.dstate.pending == 0) {
            this.dstate.pending_out = 0;
        }
    }
    
    int read_buf(final byte[] buf, final int start, final int size) {
        int len = this.avail_in;
        if (len > size) {
            len = size;
        }
        if (len == 0) {
            return 0;
        }
        this.avail_in -= len;
        switch (this.dstate.wrapperType) {
            case ZLIB: {
                this.adler = Adler32.adler32(this.adler, this.next_in, this.next_in_index, len);
                break;
            }
            case GZIP: {
                this.crc32 = CRC32.crc32(this.crc32, this.next_in, this.next_in_index, len);
                break;
            }
        }
        System.arraycopy(this.next_in, this.next_in_index, buf, start, len);
        this.next_in_index += len;
        this.total_in += len;
        return len;
    }
    
    public void free() {
        this.next_in = null;
        this.next_out = null;
        this.msg = null;
    }
}
