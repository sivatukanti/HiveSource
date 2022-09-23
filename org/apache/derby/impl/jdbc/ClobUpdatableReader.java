// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLException;
import java.io.IOException;
import java.io.Reader;

final class ClobUpdatableReader extends Reader
{
    private Reader streamReader;
    private long pos;
    private long lastUpdateCount;
    private final EmbedClob clob;
    private InternalClob iClob;
    private final long maxPos;
    private volatile boolean closed;
    
    public ClobUpdatableReader(final EmbedClob embedClob) throws IOException, SQLException {
        this(embedClob, 1L, Long.MAX_VALUE);
    }
    
    public ClobUpdatableReader(final EmbedClob clob, final long pos, final long n) throws IOException, SQLException {
        this.lastUpdateCount = -1L;
        this.closed = false;
        this.clob = clob;
        this.iClob = clob.getInternalClob();
        this.pos = pos;
        long maxPos = pos + n;
        if (maxPos < n || maxPos < pos) {
            maxPos = Long.MAX_VALUE;
        }
        this.maxPos = maxPos;
    }
    
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Reader closed");
        }
        if (this.pos >= this.maxPos) {
            return -1;
        }
        this.updateReaderIfRequired();
        final int read = this.streamReader.read();
        if (read > 0) {
            ++this.pos;
        }
        return read;
    }
    
    public int read(final char[] array, final int n, final int n2) throws IOException {
        if (this.closed) {
            throw new IOException("Reader closed");
        }
        if (this.pos >= this.maxPos) {
            return -1;
        }
        this.updateReaderIfRequired();
        final int read = this.streamReader.read(array, n, (int)Math.min(n2, this.maxPos - this.pos));
        if (read > 0) {
            this.pos += read;
        }
        return read;
    }
    
    public long skip(final long a) throws IOException {
        if (this.closed) {
            throw new IOException("Reader closed");
        }
        if (this.pos >= this.maxPos) {
            return 0L;
        }
        this.updateReaderIfRequired();
        final long skip = this.streamReader.skip(Math.min(a, this.maxPos - this.pos));
        if (skip > 0L) {
            this.pos += skip;
        }
        return skip;
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (this.streamReader != null) {
                this.streamReader.close();
            }
        }
    }
    
    private void updateReaderIfRequired() throws IOException {
        if (this.iClob.isReleased()) {
            this.iClob = this.clob.getInternalClob();
            this.lastUpdateCount = -1L;
            if (this.iClob.isReleased()) {
                this.close();
                return;
            }
        }
        if (this.lastUpdateCount != this.iClob.getUpdateCount()) {
            this.lastUpdateCount = this.iClob.getUpdateCount();
            try {
                this.streamReader = this.iClob.getReader(this.pos);
            }
            catch (SQLException ex) {
                throw new IOException(ex.getMessage());
            }
        }
    }
}
