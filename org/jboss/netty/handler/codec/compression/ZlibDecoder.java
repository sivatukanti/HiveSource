// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.compression;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.util.internal.jzlib.JZlib;
import org.jboss.netty.util.internal.jzlib.ZStream;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class ZlibDecoder extends OneToOneDecoder
{
    private final ZStream z;
    private byte[] dictionary;
    private volatile boolean finished;
    
    public ZlibDecoder() {
        this(ZlibWrapper.ZLIB);
    }
    
    public ZlibDecoder(final ZlibWrapper wrapper) {
        this.z = new ZStream();
        if (wrapper == null) {
            throw new NullPointerException("wrapper");
        }
        synchronized (this.z) {
            final int resultCode = this.z.inflateInit(ZlibUtil.convertWrapperType(wrapper));
            if (resultCode != 0) {
                ZlibUtil.fail(this.z, "initialization failure", resultCode);
            }
        }
    }
    
    public ZlibDecoder(final byte[] dictionary) {
        this.z = new ZStream();
        if (dictionary == null) {
            throw new NullPointerException("dictionary");
        }
        this.dictionary = dictionary;
        synchronized (this.z) {
            final int resultCode = this.z.inflateInit(JZlib.W_ZLIB);
            if (resultCode != 0) {
                ZlibUtil.fail(this.z, "initialization failure", resultCode);
            }
        }
    }
    
    public boolean isClosed() {
        return this.finished;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer) || this.finished) {
            return msg;
        }
        synchronized (this.z) {
            try {
                final ChannelBuffer compressed = (ChannelBuffer)msg;
                final byte[] in = new byte[compressed.readableBytes()];
                compressed.readBytes(in);
                this.z.next_in = in;
                this.z.next_in_index = 0;
                this.z.avail_in = in.length;
                final byte[] out = new byte[in.length << 1];
                final ChannelBuffer decompressed = ChannelBuffers.dynamicBuffer(compressed.order(), out.length, ctx.getChannel().getConfig().getBufferFactory());
                this.z.next_out = out;
                this.z.next_out_index = 0;
                this.z.avail_out = out.length;
            Label_0352:
                while (true) {
                    int resultCode = this.z.inflate(2);
                    if (this.z.next_out_index > 0) {
                        decompressed.writeBytes(out, 0, this.z.next_out_index);
                        this.z.avail_out = out.length;
                    }
                    this.z.next_out_index = 0;
                    switch (resultCode) {
                        case 7: {
                            if (this.dictionary == null) {
                                ZlibUtil.fail(this.z, "decompression failure", resultCode);
                                continue;
                            }
                            resultCode = this.z.inflateSetDictionary(this.dictionary, this.dictionary.length);
                            if (resultCode != 0) {
                                ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            this.finished = true;
                            this.z.inflateEnd();
                            break Label_0352;
                        }
                        case 5: {
                            continue;
                        }
                        case 0: {
                            if (this.z.avail_in <= 0) {
                                break Label_0352;
                            }
                            continue;
                        }
                        default: {
                            ZlibUtil.fail(this.z, "decompression failure", resultCode);
                            continue;
                        }
                    }
                }
                if (decompressed.writerIndex() != 0) {
                    return decompressed;
                }
                return null;
            }
            finally {
                this.z.next_in = null;
                this.z.next_out = null;
            }
        }
    }
}
