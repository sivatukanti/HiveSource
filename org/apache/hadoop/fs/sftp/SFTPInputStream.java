// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.sftp;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import org.apache.hadoop.util.StringUtils;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import com.jcraft.jsch.ChannelSftp;
import java.io.InputStream;
import org.apache.hadoop.fs.FSInputStream;

class SFTPInputStream extends FSInputStream
{
    public static final String E_SEEK_NOTSUPPORTED = "Seek not supported";
    public static final String E_CLIENT_NULL = "SFTP client null or not connected";
    public static final String E_NULL_INPUTSTREAM = "Null InputStream";
    public static final String E_STREAM_CLOSED = "Stream closed";
    public static final String E_CLIENT_NOTCONNECTED = "Client not connected";
    private InputStream wrappedStream;
    private ChannelSftp channel;
    private FileSystem.Statistics stats;
    private boolean closed;
    private long pos;
    
    SFTPInputStream(final InputStream stream, final ChannelSftp channel, final FileSystem.Statistics stats) {
        if (stream == null) {
            throw new IllegalArgumentException("Null InputStream");
        }
        if (channel == null || !channel.isConnected()) {
            throw new IllegalArgumentException("SFTP client null or not connected");
        }
        this.wrappedStream = stream;
        this.channel = channel;
        this.stats = stats;
        this.pos = 0L;
        this.closed = false;
    }
    
    @Override
    public void seek(final long position) throws IOException {
        throw new IOException("Seek not supported");
    }
    
    @Override
    public boolean seekToNewSource(final long targetPos) throws IOException {
        throw new IOException("Seek not supported");
    }
    
    @Override
    public long getPos() throws IOException {
        return this.pos;
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
        if (this.stats != null & byteRead >= 0) {
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
        if (this.stats != null & result > 0) {
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
        if (!this.channel.isConnected()) {
            throw new IOException("Client not connected");
        }
        try {
            final Session session = this.channel.getSession();
            this.channel.disconnect();
            session.disconnect();
        }
        catch (JSchException e) {
            throw new IOException(StringUtils.stringifyException(e));
        }
    }
}
