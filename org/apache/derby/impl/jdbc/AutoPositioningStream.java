// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.error.StandardException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

final class AutoPositioningStream extends BinaryToRawStream
{
    private final ConnectionChild conChild;
    private long pos;
    private final PositionedStoreStream positionedStream;
    
    AutoPositioningStream(final ConnectionChild conChild, final InputStream inputStream, final Object o) throws IOException {
        super(inputStream, o);
        this.positionedStream = (PositionedStoreStream)inputStream;
        this.pos = this.positionedStream.getPosition();
        this.conChild = conChild;
    }
    
    public int read() throws IOException {
        synchronized (this.conChild.getConnectionSynchronization()) {
            try {
                this.setPosition();
            }
            catch (EOFException ex) {
                return -1;
            }
            final int read = this.positionedStream.read();
            if (read >= 0) {
                ++this.pos;
            }
            return read;
        }
    }
    
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        synchronized (this.conChild.getConnectionSynchronization()) {
            try {
                this.setPosition();
            }
            catch (EOFException ex) {
                return -1;
            }
            final int read = this.positionedStream.read(array, n, n2);
            if (read > 0) {
                this.pos += read;
            }
            return read;
        }
    }
    
    public long skip(final long n) throws IOException {
        synchronized (this.conChild.getConnectionSynchronization()) {
            this.setPosition();
            final long skip = this.positionedStream.skip(n);
            this.pos += skip;
            return skip;
        }
    }
    
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    private void setPosition() throws IOException {
        try {
            if (this.pos != this.positionedStream.getPosition()) {
                this.positionedStream.reposition(this.pos);
            }
        }
        catch (StandardException ex) {
            throw Util.newIOException(ex);
        }
    }
}
