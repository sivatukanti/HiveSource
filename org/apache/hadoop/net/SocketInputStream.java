// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.nio.channels.SelectableChannel;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceAudience;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
public class SocketInputStream extends InputStream implements ReadableByteChannel
{
    private Reader reader;
    
    public SocketInputStream(final ReadableByteChannel channel, final long timeout) throws IOException {
        SocketIOWithTimeout.checkChannelValidity(channel);
        this.reader = new Reader(channel, timeout);
    }
    
    public SocketInputStream(final Socket socket, final long timeout) throws IOException {
        this(socket.getChannel(), timeout);
    }
    
    public SocketInputStream(final Socket socket) throws IOException {
        this(socket.getChannel(), socket.getSoTimeout());
    }
    
    @Override
    public int read() throws IOException {
        final byte[] buf = { 0 };
        final int ret = this.read(buf, 0, 1);
        if (ret > 0) {
            return buf[0] & 0xFF;
        }
        if (ret != -1) {
            throw new IOException("Could not read from stream");
        }
        return ret;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.read(ByteBuffer.wrap(b, off, len));
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.reader.channel.close();
        this.reader.close();
    }
    
    public ReadableByteChannel getChannel() {
        return this.reader.channel;
    }
    
    @Override
    public boolean isOpen() {
        return this.reader.isOpen();
    }
    
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        return this.reader.doIO(dst, 1);
    }
    
    public void waitForReadable() throws IOException {
        this.reader.waitForIO(1);
    }
    
    public void setTimeout(final long timeoutMs) {
        this.reader.setTimeout(timeoutMs);
    }
    
    private static class Reader extends SocketIOWithTimeout
    {
        ReadableByteChannel channel;
        
        Reader(final ReadableByteChannel channel, final long timeout) throws IOException {
            super((SelectableChannel)channel, timeout);
            this.channel = channel;
        }
        
        @Override
        int performIO(final ByteBuffer buf) throws IOException {
            return this.channel.read(buf);
        }
    }
}
