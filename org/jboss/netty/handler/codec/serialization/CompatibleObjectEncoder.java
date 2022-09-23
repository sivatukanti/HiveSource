// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.concurrent.atomic.AtomicReference;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class CompatibleObjectEncoder extends OneToOneEncoder
{
    private final AtomicReference<ChannelBuffer> buffer;
    private final int resetInterval;
    private volatile ObjectOutputStream oout;
    private int writtenObjects;
    
    public CompatibleObjectEncoder() {
        this(16);
    }
    
    public CompatibleObjectEncoder(final int resetInterval) {
        this.buffer = new AtomicReference<ChannelBuffer>();
        if (resetInterval < 0) {
            throw new IllegalArgumentException("resetInterval: " + resetInterval);
        }
        this.resetInterval = resetInterval;
    }
    
    protected ObjectOutputStream newObjectOutputStream(final OutputStream out) throws Exception {
        return new ObjectOutputStream(out);
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext context, final Channel channel, final Object msg) throws Exception {
        final ChannelBuffer buffer = this.buffer(context);
        final ObjectOutputStream oout = this.oout;
        if (this.resetInterval != 0) {
            ++this.writtenObjects;
            if (this.writtenObjects % this.resetInterval == 0) {
                oout.reset();
                buffer.discardReadBytes();
            }
        }
        oout.writeObject(msg);
        oout.flush();
        final ChannelBuffer encoded = buffer.readBytes(buffer.readableBytes());
        return encoded;
    }
    
    private ChannelBuffer buffer(final ChannelHandlerContext ctx) throws Exception {
        ChannelBuffer buf = this.buffer.get();
        if (buf == null) {
            final ChannelBufferFactory factory = ctx.getChannel().getConfig().getBufferFactory();
            buf = ChannelBuffers.dynamicBuffer(factory);
            if (this.buffer.compareAndSet(null, buf)) {
                boolean success = false;
                try {
                    this.oout = this.newObjectOutputStream(new ChannelBufferOutputStream(buf));
                    success = true;
                }
                finally {
                    if (!success) {
                        this.oout = null;
                    }
                }
            }
            else {
                buf = this.buffer.get();
            }
        }
        return buf;
    }
}
