// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.nio.channels.ByteChannel;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.eclipse.jetty.util.thread.Scheduler;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.util.log.Logger;

public class ChannelEndPoint extends AbstractEndPoint
{
    private static final Logger LOG;
    private final SocketChannel _channel;
    private final Socket _socket;
    private volatile boolean _ishut;
    private volatile boolean _oshut;
    
    public ChannelEndPoint(final Scheduler scheduler, final SocketChannel channel) {
        super(scheduler, (InetSocketAddress)channel.socket().getLocalSocketAddress(), (InetSocketAddress)channel.socket().getRemoteSocketAddress());
        this._channel = channel;
        this._socket = channel.socket();
    }
    
    @Override
    public boolean isOptimizedForDirectBuffers() {
        return true;
    }
    
    @Override
    public boolean isOpen() {
        return this._channel.isOpen();
    }
    
    protected void shutdownInput() {
        if (ChannelEndPoint.LOG.isDebugEnabled()) {
            ChannelEndPoint.LOG.debug("ishut {}", this);
        }
        this._ishut = true;
        if (this._oshut) {
            this.close();
        }
    }
    
    @Override
    public void shutdownOutput() {
        if (ChannelEndPoint.LOG.isDebugEnabled()) {
            ChannelEndPoint.LOG.debug("oshut {}", this);
        }
        this._oshut = true;
        if (this._channel.isOpen()) {
            try {
                if (!this._socket.isOutputShutdown()) {
                    this._socket.shutdownOutput();
                }
            }
            catch (IOException e) {
                ChannelEndPoint.LOG.debug(e);
            }
            finally {
                if (this._ishut) {
                    this.close();
                }
            }
        }
    }
    
    @Override
    public boolean isOutputShutdown() {
        return this._oshut || !this._channel.isOpen() || this._socket.isOutputShutdown();
    }
    
    @Override
    public boolean isInputShutdown() {
        return this._ishut || !this._channel.isOpen() || this._socket.isInputShutdown();
    }
    
    @Override
    public void close() {
        super.close();
        if (ChannelEndPoint.LOG.isDebugEnabled()) {
            ChannelEndPoint.LOG.debug("close {}", this);
        }
        try {
            this._channel.close();
        }
        catch (IOException e) {
            ChannelEndPoint.LOG.debug(e);
        }
        finally {
            this._ishut = true;
            this._oshut = true;
        }
    }
    
    @Override
    public int fill(final ByteBuffer buffer) throws IOException {
        if (this._ishut) {
            return -1;
        }
        final int pos = BufferUtil.flipToFill(buffer);
        try {
            final int filled = this._channel.read(buffer);
            if (ChannelEndPoint.LOG.isDebugEnabled()) {
                ChannelEndPoint.LOG.debug("filled {} {}", filled, this);
            }
            if (filled > 0) {
                this.notIdle();
            }
            else if (filled == -1) {
                this.shutdownInput();
            }
            return filled;
        }
        catch (IOException e) {
            ChannelEndPoint.LOG.debug(e);
            this.shutdownInput();
            return -1;
        }
        finally {
            BufferUtil.flipToFlush(buffer, pos);
        }
    }
    
    @Override
    public boolean flush(final ByteBuffer... buffers) throws IOException {
        long flushed = 0L;
        try {
            if (buffers.length == 1) {
                flushed = this._channel.write(buffers[0]);
            }
            else if (buffers.length > 1) {
                flushed = this._channel.write(buffers, 0, buffers.length);
            }
            else {
                for (final ByteBuffer b : buffers) {
                    if (b.hasRemaining()) {
                        final int l = this._channel.write(b);
                        if (l > 0) {
                            flushed += l;
                        }
                        if (b.hasRemaining()) {
                            break;
                        }
                    }
                }
            }
            if (ChannelEndPoint.LOG.isDebugEnabled()) {
                ChannelEndPoint.LOG.debug("flushed {} {}", flushed, this);
            }
        }
        catch (IOException e) {
            throw new EofException(e);
        }
        if (flushed > 0L) {
            this.notIdle();
        }
        for (final ByteBuffer b : buffers) {
            if (!BufferUtil.isEmpty(b)) {
                return false;
            }
        }
        return true;
    }
    
    public ByteChannel getChannel() {
        return this._channel;
    }
    
    @Override
    public Object getTransport() {
        return this._channel;
    }
    
    public Socket getSocket() {
        return this._socket;
    }
    
    @Override
    protected void onIncompleteFlush() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void needsFillInterest() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    static {
        LOG = Log.getLogger(ChannelEndPoint.class);
    }
}
