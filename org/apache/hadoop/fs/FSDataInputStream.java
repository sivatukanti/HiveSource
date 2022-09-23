// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.FileInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import org.apache.hadoop.io.ByteBufferPool;
import java.nio.ByteBuffer;
import org.apache.hadoop.util.IdentityHashStore;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.DataInputStream;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FSDataInputStream extends DataInputStream implements Seekable, PositionedReadable, ByteBufferReadable, HasFileDescriptor, CanSetDropBehind, CanSetReadahead, HasEnhancedByteBufferAccess, CanUnbuffer, StreamCapabilities
{
    private final IdentityHashStore<ByteBuffer, ByteBufferPool> extendedReadBuffers;
    private static final EnumSet<ReadOption> EMPTY_READ_OPTIONS_SET;
    
    public FSDataInputStream(final InputStream in) {
        super(in);
        this.extendedReadBuffers = new IdentityHashStore<ByteBuffer, ByteBufferPool>(0);
        if (!(in instanceof Seekable) || !(in instanceof PositionedReadable)) {
            throw new IllegalArgumentException("In is not an instance of Seekable or PositionedReadable");
        }
    }
    
    @Override
    public void seek(final long desired) throws IOException {
        ((Seekable)this.in).seek(desired);
    }
    
    @Override
    public long getPos() throws IOException {
        return ((Seekable)this.in).getPos();
    }
    
    @Override
    public int read(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        return ((PositionedReadable)this.in).read(position, buffer, offset, length);
    }
    
    @Override
    public void readFully(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        ((PositionedReadable)this.in).readFully(position, buffer, offset, length);
    }
    
    @Override
    public void readFully(final long position, final byte[] buffer) throws IOException {
        ((PositionedReadable)this.in).readFully(position, buffer, 0, buffer.length);
    }
    
    @Override
    public boolean seekToNewSource(final long targetPos) throws IOException {
        return ((Seekable)this.in).seekToNewSource(targetPos);
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS" })
    public InputStream getWrappedStream() {
        return this.in;
    }
    
    @Override
    public int read(final ByteBuffer buf) throws IOException {
        if (this.in instanceof ByteBufferReadable) {
            return ((ByteBufferReadable)this.in).read(buf);
        }
        throw new UnsupportedOperationException("Byte-buffer read unsupported by input stream");
    }
    
    @Override
    public FileDescriptor getFileDescriptor() throws IOException {
        if (this.in instanceof HasFileDescriptor) {
            return ((HasFileDescriptor)this.in).getFileDescriptor();
        }
        if (this.in instanceof FileInputStream) {
            return ((FileInputStream)this.in).getFD();
        }
        return null;
    }
    
    @Override
    public void setReadahead(final Long readahead) throws IOException, UnsupportedOperationException {
        try {
            ((CanSetReadahead)this.in).setReadahead(readahead);
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("this stream does not support setting the readahead caching strategy.");
        }
    }
    
    @Override
    public void setDropBehind(final Boolean dropBehind) throws IOException, UnsupportedOperationException {
        try {
            ((CanSetDropBehind)this.in).setDropBehind(dropBehind);
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("this stream does not support setting the drop-behind caching setting.");
        }
    }
    
    @Override
    public ByteBuffer read(final ByteBufferPool bufferPool, final int maxLength, final EnumSet<ReadOption> opts) throws IOException, UnsupportedOperationException {
        try {
            return ((HasEnhancedByteBufferAccess)this.in).read(bufferPool, maxLength, opts);
        }
        catch (ClassCastException e) {
            final ByteBuffer buffer = ByteBufferUtil.fallbackRead(this, bufferPool, maxLength);
            if (buffer != null) {
                this.extendedReadBuffers.put(buffer, bufferPool);
            }
            return buffer;
        }
    }
    
    public final ByteBuffer read(final ByteBufferPool bufferPool, final int maxLength) throws IOException, UnsupportedOperationException {
        return this.read(bufferPool, maxLength, FSDataInputStream.EMPTY_READ_OPTIONS_SET);
    }
    
    @Override
    public void releaseBuffer(final ByteBuffer buffer) {
        try {
            ((HasEnhancedByteBufferAccess)this.in).releaseBuffer(buffer);
        }
        catch (ClassCastException e) {
            final ByteBufferPool bufferPool = this.extendedReadBuffers.remove(buffer);
            if (bufferPool == null) {
                throw new IllegalArgumentException("tried to release a buffer that was not created by this stream.");
            }
            bufferPool.putBuffer(buffer);
        }
    }
    
    @Override
    public void unbuffer() {
        StreamCapabilitiesPolicy.unbuffer(this.in);
    }
    
    @Override
    public boolean hasCapability(final String capability) {
        return this.in instanceof StreamCapabilities && ((StreamCapabilities)this.in).hasCapability(capability);
    }
    
    @Override
    public String toString() {
        return super.toString() + ": " + this.in;
    }
    
    static {
        EMPTY_READ_OPTIONS_SET = EnumSet.noneOf(ReadOption.class);
    }
}
