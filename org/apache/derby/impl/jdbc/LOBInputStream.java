// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.EOFException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.ExceptionUtil;
import java.io.IOException;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.types.PositionedStream;
import java.io.InputStream;

public class LOBInputStream extends InputStream implements PositionedStream
{
    private boolean closed;
    private final LOBStreamControl control;
    private long pos;
    private long updateCount;
    
    LOBInputStream(final LOBStreamControl control, final long pos) {
        this.closed = false;
        this.control = control;
        this.pos = pos;
        this.updateCount = control.getUpdateCount();
    }
    
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this.closed) {
            throw new IOException(MessageService.getTextMessage("J104"));
        }
        try {
            final int read = this.control.read(array, n, n2, this.pos);
            if (read != -1) {
                this.pos += read;
                return read;
            }
            return -1;
        }
        catch (StandardException ex) {
            final String sqlState = ex.getSQLState();
            if (sqlState.equals(ExceptionUtil.getSQLStateFromIdentifier("XJ076.S"))) {
                return -1;
            }
            if (sqlState.equals(ExceptionUtil.getSQLStateFromIdentifier("XJ078.S"))) {
                throw new ArrayIndexOutOfBoundsException(ex.getMessage());
            }
            throw Util.newIOException(ex);
        }
    }
    
    public void close() throws IOException {
        this.closed = true;
    }
    
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException(MessageService.getTextMessage("J104"));
        }
        try {
            final int read = this.control.read(this.pos);
            if (read != -1) {
                ++this.pos;
            }
            return read;
        }
        catch (StandardException ex) {
            throw Util.newIOException(ex);
        }
    }
    
    boolean isObsolete() {
        return this.updateCount != this.control.getUpdateCount();
    }
    
    void reInitialize() {
        this.updateCount = this.control.getUpdateCount();
        this.pos = 0L;
    }
    
    long length() throws IOException {
        return this.control.getLength();
    }
    
    public InputStream asInputStream() {
        return this;
    }
    
    public long getPosition() {
        return this.pos;
    }
    
    public void reposition(final long pos) throws IOException {
        if (pos > this.length()) {
            this.pos = 0L;
            throw new EOFException();
        }
        this.pos = pos;
    }
}
