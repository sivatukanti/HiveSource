// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.DefaultFileRegion;
import java.nio.channels.GatheringByteChannel;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.lang.ref.SoftReference;
import org.jboss.netty.util.internal.ByteBufferUtil;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.CompositeChannelBuffer;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.util.ExternalResourceReleasable;

final class SocketSendBufferPool implements ExternalResourceReleasable
{
    private static final SendBuffer EMPTY_BUFFER;
    private static final int DEFAULT_PREALLOCATION_SIZE = 65536;
    private static final int ALIGN_SHIFT = 4;
    private static final int ALIGN_MASK = 15;
    private PreallocationRef poolHead;
    private Preallocation current;
    
    SocketSendBufferPool() {
        this.current = new Preallocation(65536);
    }
    
    SendBuffer acquire(final Object message) {
        if (message instanceof ChannelBuffer) {
            return this.acquire((ChannelBuffer)message);
        }
        if (message instanceof FileRegion) {
            return this.acquire((FileRegion)message);
        }
        throw new IllegalArgumentException("unsupported message type: " + message.getClass());
    }
    
    private SendBuffer acquire(final FileRegion src) {
        if (src.getCount() == 0L) {
            return SocketSendBufferPool.EMPTY_BUFFER;
        }
        return new FileSendBuffer(src);
    }
    
    private SendBuffer acquire(final ChannelBuffer src) {
        final int size = src.readableBytes();
        if (size == 0) {
            return SocketSendBufferPool.EMPTY_BUFFER;
        }
        if (src instanceof CompositeChannelBuffer && ((CompositeChannelBuffer)src).useGathering()) {
            return new GatheringSendBuffer(src.toByteBuffers());
        }
        if (src.isDirect()) {
            return new UnpooledSendBuffer(src.toByteBuffer());
        }
        if (src.readableBytes() > 65536) {
            return new UnpooledSendBuffer(src.toByteBuffer());
        }
        Preallocation current = this.current;
        ByteBuffer buffer = current.buffer;
        final int remaining = buffer.remaining();
        PooledSendBuffer dst;
        if (size < remaining) {
            final int nextPos = buffer.position() + size;
            final ByteBuffer slice = buffer.duplicate();
            buffer.position(align(nextPos));
            slice.limit(nextPos);
            final Preallocation preallocation = current;
            ++preallocation.refCnt;
            dst = new PooledSendBuffer(current, slice);
        }
        else if (size > remaining) {
            current = (this.current = this.getPreallocation());
            buffer = current.buffer;
            final ByteBuffer slice2 = buffer.duplicate();
            buffer.position(align(size));
            slice2.limit(size);
            final Preallocation preallocation2 = current;
            ++preallocation2.refCnt;
            dst = new PooledSendBuffer(current, slice2);
        }
        else {
            final Preallocation preallocation3 = current;
            ++preallocation3.refCnt;
            this.current = this.getPreallocation0();
            dst = new PooledSendBuffer(current, current.buffer);
        }
        final ByteBuffer dstbuf = dst.buffer;
        dstbuf.mark();
        src.getBytes(src.readerIndex(), dstbuf);
        dstbuf.reset();
        return dst;
    }
    
    private Preallocation getPreallocation() {
        final Preallocation current = this.current;
        if (current.refCnt == 0) {
            current.buffer.clear();
            return current;
        }
        return this.getPreallocation0();
    }
    
    private Preallocation getPreallocation0() {
        PreallocationRef ref = this.poolHead;
        if (ref != null) {
            do {
                final Preallocation p = ref.get();
                ref = ref.next;
                if (p != null) {
                    this.poolHead = ref;
                    return p;
                }
            } while (ref != null);
            this.poolHead = ref;
        }
        return new Preallocation(65536);
    }
    
    private static int align(final int pos) {
        int q = pos >>> 4;
        final int r = pos & 0xF;
        if (r != 0) {
            ++q;
        }
        return q << 4;
    }
    
    public void releaseExternalResources() {
        if (this.current.buffer != null) {
            ByteBufferUtil.destroy(this.current.buffer);
        }
    }
    
    static {
        EMPTY_BUFFER = new EmptySendBuffer();
    }
    
    private static final class Preallocation
    {
        final ByteBuffer buffer;
        int refCnt;
        
        Preallocation(final int capacity) {
            this.buffer = ByteBuffer.allocateDirect(capacity);
        }
    }
    
    private final class PreallocationRef extends SoftReference<Preallocation>
    {
        final PreallocationRef next;
        
        PreallocationRef(final Preallocation prealloation, final PreallocationRef next) {
            super(prealloation);
            this.next = next;
        }
    }
    
    static class UnpooledSendBuffer implements SendBuffer
    {
        final ByteBuffer buffer;
        final int initialPos;
        
        UnpooledSendBuffer(final ByteBuffer buffer) {
            this.buffer = buffer;
            this.initialPos = buffer.position();
        }
        
        public final boolean finished() {
            return !this.buffer.hasRemaining();
        }
        
        public final long writtenBytes() {
            return this.buffer.position() - this.initialPos;
        }
        
        public final long totalBytes() {
            return this.buffer.limit() - this.initialPos;
        }
        
        public final long transferTo(final WritableByteChannel ch) throws IOException {
            return ch.write(this.buffer);
        }
        
        public final long transferTo(final DatagramChannel ch, final SocketAddress raddr) throws IOException {
            return ch.send(this.buffer, raddr);
        }
        
        public void release() {
        }
    }
    
    final class PooledSendBuffer extends UnpooledSendBuffer
    {
        private final Preallocation parent;
        
        PooledSendBuffer(final Preallocation parent, final ByteBuffer buffer) {
            super(buffer);
            this.parent = parent;
        }
        
        @Override
        public void release() {
            final Preallocation parent2;
            final Preallocation parent = parent2 = this.parent;
            final int refCnt = parent2.refCnt - 1;
            parent2.refCnt = refCnt;
            if (refCnt == 0) {
                parent.buffer.clear();
                if (parent != SocketSendBufferPool.this.current) {
                    SocketSendBufferPool.this.poolHead = new PreallocationRef(parent, SocketSendBufferPool.this.poolHead);
                }
            }
        }
    }
    
    static class GatheringSendBuffer implements SendBuffer
    {
        private final ByteBuffer[] buffers;
        private final int last;
        private long written;
        private final int total;
        
        GatheringSendBuffer(final ByteBuffer[] buffers) {
            this.buffers = buffers;
            this.last = buffers.length - 1;
            int total = 0;
            for (final ByteBuffer buf : buffers) {
                total += buf.remaining();
            }
            this.total = total;
        }
        
        public boolean finished() {
            return !this.buffers[this.last].hasRemaining();
        }
        
        public long writtenBytes() {
            return this.written;
        }
        
        public long totalBytes() {
            return this.total;
        }
        
        public long transferTo(final WritableByteChannel ch) throws IOException {
            if (ch instanceof GatheringByteChannel) {
                final long w = ((GatheringByteChannel)ch).write(this.buffers);
                this.written += w;
                return w;
            }
            int send = 0;
            for (final ByteBuffer buf : this.buffers) {
                if (buf.hasRemaining()) {
                    final int w2 = ch.write(buf);
                    if (w2 == 0) {
                        break;
                    }
                    send += w2;
                }
            }
            this.written += send;
            return send;
        }
        
        public long transferTo(final DatagramChannel ch, final SocketAddress raddr) throws IOException {
            int send = 0;
            for (final ByteBuffer buf : this.buffers) {
                if (buf.hasRemaining()) {
                    final int w = ch.send(buf, raddr);
                    if (w == 0) {
                        break;
                    }
                    send += w;
                }
            }
            this.written += send;
            return send;
        }
        
        public void release() {
        }
    }
    
    final class FileSendBuffer implements SendBuffer
    {
        private final FileRegion file;
        private long writtenBytes;
        
        FileSendBuffer(final FileRegion file) {
            this.file = file;
        }
        
        public boolean finished() {
            return this.writtenBytes >= this.file.getCount();
        }
        
        public long writtenBytes() {
            return this.writtenBytes;
        }
        
        public long totalBytes() {
            return this.file.getCount();
        }
        
        public long transferTo(final WritableByteChannel ch) throws IOException {
            final long localWrittenBytes = this.file.transferTo(ch, this.writtenBytes);
            this.writtenBytes += localWrittenBytes;
            return localWrittenBytes;
        }
        
        public long transferTo(final DatagramChannel ch, final SocketAddress raddr) {
            throw new UnsupportedOperationException();
        }
        
        public void release() {
            if (this.file instanceof DefaultFileRegion && ((DefaultFileRegion)this.file).releaseAfterTransfer()) {
                this.file.releaseExternalResources();
            }
        }
    }
    
    static final class EmptySendBuffer implements SendBuffer
    {
        public boolean finished() {
            return true;
        }
        
        public long writtenBytes() {
            return 0L;
        }
        
        public long totalBytes() {
            return 0L;
        }
        
        public long transferTo(final WritableByteChannel ch) {
            return 0L;
        }
        
        public long transferTo(final DatagramChannel ch, final SocketAddress raddr) {
            return 0L;
        }
        
        public void release() {
        }
    }
    
    interface SendBuffer
    {
        boolean finished();
        
        long writtenBytes();
        
        long totalBytes();
        
        long transferTo(final WritableByteChannel p0) throws IOException;
        
        long transferTo(final DatagramChannel p0, final SocketAddress p1) throws IOException;
        
        void release();
    }
}
