// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import java.io.EOFException;
import java.io.IOException;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.InputStream;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class FSInputStream extends InputStream implements Seekable, PositionedReadable
{
    private static final Logger LOG;
    
    @Override
    public abstract void seek(final long p0) throws IOException;
    
    @Override
    public abstract long getPos() throws IOException;
    
    @Override
    public abstract boolean seekToNewSource(final long p0) throws IOException;
    
    @Override
    public int read(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        this.validatePositionedReadArgs(position, buffer, offset, length);
        if (length == 0) {
            return 0;
        }
        synchronized (this) {
            final long oldPos = this.getPos();
            int nread = -1;
            try {
                this.seek(position);
                nread = this.read(buffer, offset, length);
            }
            catch (EOFException e) {
                FSInputStream.LOG.debug("Downgrading EOFException raised trying to read {} bytes at offset {}", length, offset, e);
            }
            finally {
                this.seek(oldPos);
            }
            return nread;
        }
    }
    
    protected void validatePositionedReadArgs(final long position, final byte[] buffer, final int offset, final int length) throws EOFException {
        Preconditions.checkArgument(length >= 0, (Object)"length is negative");
        if (position < 0L) {
            throw new EOFException("position is negative");
        }
        Preconditions.checkArgument(buffer != null, (Object)"Null buffer");
        if (buffer.length - offset < length) {
            throw new IndexOutOfBoundsException("Requested more bytes than destination buffer size: request length=" + length + ", with offset =" + offset + "; buffer capacity =" + (buffer.length - offset));
        }
    }
    
    @Override
    public void readFully(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        this.validatePositionedReadArgs(position, buffer, offset, length);
        int nbytes;
        for (int nread = 0; nread < length; nread += nbytes) {
            nbytes = this.read(position + nread, buffer, offset + nread, length - nread);
            if (nbytes < 0) {
                throw new EOFException("End of file reached before reading fully.");
            }
        }
    }
    
    @Override
    public void readFully(final long position, final byte[] buffer) throws IOException {
        this.readFully(position, buffer, 0, buffer.length);
    }
    
    static {
        LOG = LoggerFactory.getLogger(FSInputStream.class);
    }
}
