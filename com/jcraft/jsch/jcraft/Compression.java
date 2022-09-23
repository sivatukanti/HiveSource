// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jcraft;

import com.jcraft.jzlib.ZStream;

public class Compression implements com.jcraft.jsch.Compression
{
    private static final int BUF_SIZE = 4096;
    private final int buffer_margin = 52;
    private int type;
    private ZStream stream;
    private byte[] tmpbuf;
    private byte[] inflated_buf;
    
    public Compression() {
        this.tmpbuf = new byte[4096];
        this.stream = new ZStream();
    }
    
    public void init(final int type, final int level) {
        if (type == 1) {
            this.stream.deflateInit(level);
            this.type = 1;
        }
        else if (type == 0) {
            this.stream.inflateInit();
            this.inflated_buf = new byte[4096];
            this.type = 0;
        }
    }
    
    public byte[] compress(final byte[] buf, final int start, final int[] len) {
        this.stream.next_in = buf;
        this.stream.next_in_index = start;
        this.stream.avail_in = len[0] - start;
        int outputlen = start;
        byte[] outputbuf = buf;
        int tmp = 0;
        do {
            this.stream.next_out = this.tmpbuf;
            this.stream.next_out_index = 0;
            this.stream.avail_out = 4096;
            final int status = this.stream.deflate(1);
            switch (status) {
                case 0: {
                    tmp = 4096 - this.stream.avail_out;
                    if (outputbuf.length < outputlen + tmp + 52) {
                        final byte[] foo = new byte[(outputlen + tmp + 52) * 2];
                        System.arraycopy(outputbuf, 0, foo, 0, outputbuf.length);
                        outputbuf = foo;
                    }
                    System.arraycopy(this.tmpbuf, 0, outputbuf, outputlen, tmp);
                    outputlen += tmp;
                    continue;
                }
                default: {
                    System.err.println("compress: deflate returnd " + status);
                    continue;
                }
            }
        } while (this.stream.avail_out == 0);
        len[0] = outputlen;
        return outputbuf;
    }
    
    public byte[] uncompress(byte[] buffer, final int start, final int[] length) {
        int inflated_end = 0;
        this.stream.next_in = buffer;
        this.stream.next_in_index = start;
        this.stream.avail_in = length[0];
        while (true) {
            this.stream.next_out = this.tmpbuf;
            this.stream.next_out_index = 0;
            this.stream.avail_out = 4096;
            final int status = this.stream.inflate(1);
            switch (status) {
                case 0: {
                    if (this.inflated_buf.length < inflated_end + 4096 - this.stream.avail_out) {
                        int len = this.inflated_buf.length * 2;
                        if (len < inflated_end + 4096 - this.stream.avail_out) {
                            len = inflated_end + 4096 - this.stream.avail_out;
                        }
                        final byte[] foo = new byte[len];
                        System.arraycopy(this.inflated_buf, 0, foo, 0, inflated_end);
                        this.inflated_buf = foo;
                    }
                    System.arraycopy(this.tmpbuf, 0, this.inflated_buf, inflated_end, 4096 - this.stream.avail_out);
                    inflated_end += 4096 - this.stream.avail_out;
                    length[0] = inflated_end;
                    continue;
                }
                case -5: {
                    if (inflated_end > buffer.length - start) {
                        final byte[] foo2 = new byte[inflated_end + start];
                        System.arraycopy(buffer, 0, foo2, 0, start);
                        System.arraycopy(this.inflated_buf, 0, foo2, start, inflated_end);
                        buffer = foo2;
                    }
                    else {
                        System.arraycopy(this.inflated_buf, 0, buffer, start, inflated_end);
                    }
                    length[0] = inflated_end;
                    return buffer;
                }
                default: {
                    System.err.println("uncompress: inflate returnd " + status);
                    return null;
                }
            }
        }
    }
}
