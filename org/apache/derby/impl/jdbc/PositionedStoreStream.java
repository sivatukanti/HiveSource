// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.EOFException;
import org.apache.derby.iapi.services.io.InputStreamUtil;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import org.apache.derby.iapi.types.Resetable;
import org.apache.derby.iapi.types.PositionedStream;
import java.io.InputStream;

public class PositionedStoreStream extends InputStream implements PositionedStream, Resetable
{
    private final InputStream stream;
    private long pos;
    
    public PositionedStoreStream(final InputStream stream) throws IOException, StandardException {
        this.pos = 0L;
        this.stream = stream;
        ((Resetable)stream).initStream();
        ((Resetable)stream).resetStream();
    }
    
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int read = this.stream.read(b, off, len);
        if (read > -1) {
            this.pos += read;
        }
        return read;
    }
    
    public int read() throws IOException {
        final int read = this.stream.read();
        if (read > -1) {
            ++this.pos;
        }
        return read;
    }
    
    public long skip(final long n) throws IOException {
        final long skip = this.stream.skip(n);
        this.pos += skip;
        return skip;
    }
    
    public void resetStream() throws IOException, StandardException {
        ((Resetable)this.stream).resetStream();
        this.pos = 0L;
    }
    
    public void initStream() throws StandardException {
        ((Resetable)this.stream).initStream();
    }
    
    public void closeStream() {
        ((Resetable)this.stream).closeStream();
    }
    
    public void reposition(final long pos) throws IOException, StandardException {
        if (this.pos > pos) {
            this.resetStream();
        }
        if (this.pos < pos) {
            try {
                InputStreamUtil.skipFully(this.stream, pos - this.pos);
            }
            catch (EOFException ex) {
                this.resetStream();
                throw ex;
            }
            this.pos = pos;
        }
    }
    
    public long getPosition() {
        return this.pos;
    }
    
    public InputStream asInputStream() {
        return this;
    }
}
