// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.nio.channels.SelectableChannel;
import java.io.EOFException;
import org.apache.hadoop.io.LongWritable;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.nio.channels.WritableByteChannel;
import java.io.OutputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class SocketOutputStream extends OutputStream implements WritableByteChannel
{
    private Writer writer;
    
    public SocketOutputStream(final WritableByteChannel channel, final long timeout) throws IOException {
        SocketIOWithTimeout.checkChannelValidity(channel);
        this.writer = new Writer(channel, timeout);
    }
    
    public SocketOutputStream(final Socket socket, final long timeout) throws IOException {
        this(socket.getChannel(), timeout);
    }
    
    @Override
    public void write(final int b) throws IOException {
        final byte[] buf = { (byte)b };
        this.write(buf, 0, 1);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        final ByteBuffer buf = ByteBuffer.wrap(b, off, len);
        while (buf.hasRemaining()) {
            try {
                if (this.write(buf) < 0) {
                    throw new IOException("The stream is closed");
                }
                continue;
            }
            catch (IOException e) {
                if (buf.capacity() > buf.remaining()) {
                    this.writer.close();
                }
                throw e;
            }
            break;
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.writer.channel.close();
        this.writer.close();
    }
    
    public WritableByteChannel getChannel() {
        return this.writer.channel;
    }
    
    @Override
    public boolean isOpen() {
        return this.writer.isOpen();
    }
    
    @Override
    public int write(final ByteBuffer src) throws IOException {
        return this.writer.doIO(src, 4);
    }
    
    public void waitForWritable() throws IOException {
        this.writer.waitForIO(4);
    }
    
    public void transferToFully(final FileChannel fileCh, long position, int count, final LongWritable waitForWritableTime, final LongWritable transferToTime) throws IOException {
        long waitTime = 0L;
        long transferTime = 0L;
        while (count > 0) {
            final long start = System.nanoTime();
            this.waitForWritable();
            final long wait = System.nanoTime();
            final int nTransfered = (int)fileCh.transferTo(position, count, this.getChannel());
            if (nTransfered == 0) {
                if (position >= fileCh.size()) {
                    throw new EOFException("EOF Reached. file size is " + fileCh.size() + " and " + count + " more bytes left to be transfered.");
                }
            }
            else {
                if (nTransfered < 0) {
                    throw new IOException("Unexpected return of " + nTransfered + " from transferTo()");
                }
                position += nTransfered;
                count -= nTransfered;
            }
            final long transfer = System.nanoTime();
            waitTime += wait - start;
            transferTime += transfer - wait;
        }
        if (waitForWritableTime != null) {
            waitForWritableTime.set(waitTime);
        }
        if (transferToTime != null) {
            transferToTime.set(transferTime);
        }
    }
    
    public void transferToFully(final FileChannel fileCh, final long position, final int count) throws IOException {
        this.transferToFully(fileCh, position, count, null, null);
    }
    
    public void setTimeout(final int timeoutMs) {
        this.writer.setTimeout(timeoutMs);
    }
    
    private static class Writer extends SocketIOWithTimeout
    {
        WritableByteChannel channel;
        
        Writer(final WritableByteChannel channel, final long timeout) throws IOException {
            super((SelectableChannel)channel, timeout);
            this.channel = channel;
        }
        
        @Override
        int performIO(final ByteBuffer buf) throws IOException {
            return this.channel.write(buf);
        }
    }
}
