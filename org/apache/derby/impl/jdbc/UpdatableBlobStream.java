// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.services.io.InputStreamUtil;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;

class UpdatableBlobStream extends InputStream
{
    private boolean materialized;
    private InputStream stream;
    private long pos;
    private final EmbedBlob blob;
    private final long maxPos;
    
    UpdatableBlobStream(final EmbedBlob embedBlob, final InputStream inputStream) throws IOException {
        this(embedBlob, inputStream, 0L, Long.MAX_VALUE);
    }
    
    UpdatableBlobStream(final EmbedBlob blob, final InputStream stream, final long n, final long n2) throws IOException {
        this.blob = blob;
        this.stream = stream;
        this.maxPos = n + n2;
        if (n > 0L) {
            this.skip(n);
        }
    }
    
    private void updateIfRequired() throws IOException {
        if (this.materialized) {
            return;
        }
        if (this.blob.isMaterialized()) {
            this.materialized = true;
            try {
                this.stream = this.blob.getBinaryStream();
            }
            catch (SQLException ex) {
                throw Util.newIOException(ex);
            }
            InputStreamUtil.skipFully(this.stream, this.pos);
        }
    }
    
    public int read() throws IOException {
        this.updateIfRequired();
        if (this.pos >= this.maxPos) {
            return -1;
        }
        final int read = this.stream.read();
        if (read >= 0) {
            ++this.pos;
        }
        return read;
    }
    
    public int read(final byte[] b, final int off, final int n) throws IOException {
        this.updateIfRequired();
        final long b2 = this.maxPos - this.pos;
        if (b2 == 0L && n > 0) {
            return -1;
        }
        final int read = this.stream.read(b, off, (int)Math.min(n, b2));
        if (read > 0) {
            this.pos += read;
        }
        return read;
    }
    
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    public long skip(final long n) throws IOException {
        this.updateIfRequired();
        final long skip = this.stream.skip(n);
        if (skip > 0L) {
            this.pos += skip;
        }
        return skip;
    }
}
