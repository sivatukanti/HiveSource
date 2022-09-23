// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.ftp;

import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.commons.net.ftp.FTPClient;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.FSInputStream;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FTPInputStream extends FSInputStream
{
    InputStream wrappedStream;
    FTPClient client;
    FileSystem.Statistics stats;
    boolean closed;
    long pos;
    
    public FTPInputStream(final InputStream stream, final FTPClient client, final FileSystem.Statistics stats) {
        if (stream == null) {
            throw new IllegalArgumentException("Null InputStream");
        }
        if (client == null || !client.isConnected()) {
            throw new IllegalArgumentException("FTP client null or not connected");
        }
        this.wrappedStream = stream;
        this.client = client;
        this.stats = stats;
        this.pos = 0L;
        this.closed = false;
    }
    
    @Override
    public long getPos() throws IOException {
        return this.pos;
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        throw new IOException("Seek not supported");
    }
    
    @Override
    public boolean seekToNewSource(final long targetPos) throws IOException {
        throw new IOException("Seek not supported");
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        final int byteRead = this.wrappedStream.read();
        if (byteRead >= 0) {
            ++this.pos;
        }
        if (this.stats != null && byteRead >= 0) {
            this.stats.incrementBytesRead(1L);
        }
        return byteRead;
    }
    
    @Override
    public synchronized int read(final byte[] buf, final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        final int result = this.wrappedStream.read(buf, off, len);
        if (result > 0) {
            this.pos += result;
        }
        if (this.stats != null && result > 0) {
            this.stats.incrementBytesRead(result);
        }
        return result;
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (this.closed) {
            return;
        }
        super.close();
        this.closed = true;
        if (!this.client.isConnected()) {
            throw new FTPException("Client not connected");
        }
        final boolean cmdCompleted = this.client.completePendingCommand();
        this.client.logout();
        this.client.disconnect();
        if (!cmdCompleted) {
            throw new FTPException("Could not complete transfer, Reply Code - " + this.client.getReplyCode());
        }
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int readLimit) {
    }
    
    @Override
    public void reset() throws IOException {
        throw new IOException("Mark not supported");
    }
}
