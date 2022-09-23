// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.FileDescriptor;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.BufferedInputStream;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class BufferedFSInputStream extends BufferedInputStream implements Seekable, PositionedReadable, HasFileDescriptor
{
    public BufferedFSInputStream(final FSInputStream in, final int size) {
        super(in, size);
    }
    
    @Override
    public long getPos() throws IOException {
        if (this.in == null) {
            throw new IOException("Stream is closed!");
        }
        return ((FSInputStream)this.in).getPos() - (this.count - this.pos);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        this.seek(this.getPos() + n);
        return n;
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        if (this.in == null) {
            throw new IOException("Stream is closed!");
        }
        if (pos < 0L) {
            throw new EOFException("Cannot seek to a negative offset");
        }
        if (this.pos != this.count) {
            final long end = ((FSInputStream)this.in).getPos();
            final long start = end - this.count;
            if (pos >= start && pos < end) {
                this.pos = (int)(pos - start);
                return;
            }
        }
        this.pos = 0;
        this.count = 0;
        ((FSInputStream)this.in).seek(pos);
    }
    
    @Override
    public boolean seekToNewSource(final long targetPos) throws IOException {
        this.pos = 0;
        this.count = 0;
        return ((FSInputStream)this.in).seekToNewSource(targetPos);
    }
    
    @Override
    public int read(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        return ((FSInputStream)this.in).read(position, buffer, offset, length);
    }
    
    @Override
    public void readFully(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        ((FSInputStream)this.in).readFully(position, buffer, offset, length);
    }
    
    @Override
    public void readFully(final long position, final byte[] buffer) throws IOException {
        ((FSInputStream)this.in).readFully(position, buffer);
    }
    
    @Override
    public FileDescriptor getFileDescriptor() throws IOException {
        if (this.in instanceof HasFileDescriptor) {
            return ((HasFileDescriptor)this.in).getFileDescriptor();
        }
        return null;
    }
}
