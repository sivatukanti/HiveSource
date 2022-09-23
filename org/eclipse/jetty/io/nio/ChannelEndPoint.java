// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.nio;

import org.eclipse.jetty.util.log.Log;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import org.eclipse.jetty.io.Buffer;
import java.net.SocketException;
import java.nio.channels.SelectableChannel;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.EndPoint;

public class ChannelEndPoint implements EndPoint
{
    private static final Logger LOG;
    protected final ByteChannel _channel;
    protected final ByteBuffer[] _gather2;
    protected final Socket _socket;
    protected final InetSocketAddress _local;
    protected final InetSocketAddress _remote;
    protected volatile int _maxIdleTime;
    private volatile boolean _ishut;
    private volatile boolean _oshut;
    
    public ChannelEndPoint(final ByteChannel channel) throws IOException {
        this._gather2 = new ByteBuffer[2];
        this._channel = channel;
        this._socket = ((channel instanceof SocketChannel) ? ((SocketChannel)channel).socket() : null);
        if (this._socket != null) {
            this._local = (InetSocketAddress)this._socket.getLocalSocketAddress();
            this._remote = (InetSocketAddress)this._socket.getRemoteSocketAddress();
            this._maxIdleTime = this._socket.getSoTimeout();
        }
        else {
            final InetSocketAddress inetSocketAddress = null;
            this._remote = inetSocketAddress;
            this._local = inetSocketAddress;
        }
    }
    
    protected ChannelEndPoint(final ByteChannel channel, final int maxIdleTime) throws IOException {
        this._gather2 = new ByteBuffer[2];
        this._channel = channel;
        this._maxIdleTime = maxIdleTime;
        this._socket = ((channel instanceof SocketChannel) ? ((SocketChannel)channel).socket() : null);
        if (this._socket != null) {
            this._local = (InetSocketAddress)this._socket.getLocalSocketAddress();
            this._remote = (InetSocketAddress)this._socket.getRemoteSocketAddress();
            this._socket.setSoTimeout(this._maxIdleTime);
        }
        else {
            final InetSocketAddress inetSocketAddress = null;
            this._remote = inetSocketAddress;
            this._local = inetSocketAddress;
        }
    }
    
    public boolean isBlocking() {
        return !(this._channel instanceof SelectableChannel) || ((SelectableChannel)this._channel).isBlocking();
    }
    
    public boolean blockReadable(final long millisecs) throws IOException {
        return true;
    }
    
    public boolean blockWritable(final long millisecs) throws IOException {
        return true;
    }
    
    public boolean isOpen() {
        return this._channel.isOpen();
    }
    
    protected final void shutdownChannelInput() throws IOException {
        ChannelEndPoint.LOG.debug("ishut {}", this);
        this._ishut = true;
        if (this._channel.isOpen() && this._socket != null) {
            try {
                if (!this._socket.isInputShutdown()) {
                    this._socket.shutdownInput();
                }
            }
            catch (SocketException e) {
                ChannelEndPoint.LOG.debug(e.toString(), new Object[0]);
                ChannelEndPoint.LOG.ignore(e);
            }
            finally {
                if (this._oshut) {
                    this.close();
                }
            }
        }
    }
    
    public void shutdownInput() throws IOException {
        this.shutdownChannelInput();
    }
    
    protected final void shutdownChannelOutput() throws IOException {
        ChannelEndPoint.LOG.debug("oshut {}", this);
        this._oshut = true;
        if (this._channel.isOpen() && this._socket != null) {
            try {
                if (!this._socket.isOutputShutdown()) {
                    this._socket.shutdownOutput();
                }
            }
            catch (SocketException e) {
                ChannelEndPoint.LOG.debug(e.toString(), new Object[0]);
                ChannelEndPoint.LOG.ignore(e);
            }
            finally {
                if (this._ishut) {
                    this.close();
                }
            }
        }
    }
    
    public void shutdownOutput() throws IOException {
        this.shutdownChannelOutput();
    }
    
    public boolean isOutputShutdown() {
        return this._oshut || !this._channel.isOpen() || (this._socket != null && this._socket.isOutputShutdown());
    }
    
    public boolean isInputShutdown() {
        return this._ishut || !this._channel.isOpen() || (this._socket != null && this._socket.isInputShutdown());
    }
    
    public void close() throws IOException {
        ChannelEndPoint.LOG.debug("close {}", this);
        this._channel.close();
    }
    
    public int fill(final Buffer buffer) throws IOException {
        if (this._ishut) {
            return -1;
        }
        final Buffer buf = buffer.buffer();
        int len = 0;
        if (buf instanceof NIOBuffer) {
            final NIOBuffer nbuf = (NIOBuffer)buf;
            final ByteBuffer bbuf = nbuf.getByteBuffer();
            try {
                synchronized (bbuf) {
                    try {
                        bbuf.position(buffer.putIndex());
                        len = this._channel.read(bbuf);
                    }
                    finally {
                        buffer.setPutIndex(bbuf.position());
                        bbuf.position(0);
                    }
                }
                if (len < 0 && this.isOpen()) {
                    if (!this.isInputShutdown()) {
                        this.shutdownInput();
                    }
                    if (this.isOutputShutdown()) {
                        this._channel.close();
                    }
                }
            }
            catch (IOException x) {
                ChannelEndPoint.LOG.debug("Exception while filling", x);
                try {
                    if (this._channel.isOpen()) {
                        this._channel.close();
                    }
                }
                catch (Exception xx) {
                    ChannelEndPoint.LOG.ignore(xx);
                }
                if (len > 0) {
                    throw x;
                }
                len = -1;
            }
            return len;
        }
        throw new IOException("Not Implemented");
    }
    
    public int flush(final Buffer buffer) throws IOException {
        final Buffer buf = buffer.buffer();
        int len = 0;
        if (buf instanceof NIOBuffer) {
            final NIOBuffer nbuf = (NIOBuffer)buf;
            final ByteBuffer bbuf = nbuf.getByteBuffer();
            synchronized (bbuf) {
                try {
                    bbuf.position(buffer.getIndex());
                    bbuf.limit(buffer.putIndex());
                    len = this._channel.write(bbuf);
                }
                finally {
                    if (len > 0) {
                        buffer.skip(len);
                    }
                    bbuf.position(0);
                    bbuf.limit(bbuf.capacity());
                }
            }
        }
        else if (buf instanceof RandomAccessFileBuffer) {
            len = ((RandomAccessFileBuffer)buf).writeTo(this._channel, buffer.getIndex(), buffer.length());
            if (len > 0) {
                buffer.skip(len);
            }
        }
        else {
            if (buffer.array() == null) {
                throw new IOException("Not Implemented");
            }
            final ByteBuffer b = ByteBuffer.wrap(buffer.array(), buffer.getIndex(), buffer.length());
            len = this._channel.write(b);
            if (len > 0) {
                buffer.skip(len);
            }
        }
        return len;
    }
    
    public int flush(final Buffer header, final Buffer buffer, final Buffer trailer) throws IOException {
        int length = 0;
        final Buffer buf0 = (header == null) ? null : header.buffer();
        final Buffer buf2 = (buffer == null) ? null : buffer.buffer();
        if (this._channel instanceof GatheringByteChannel && header != null && header.length() != 0 && buf0 instanceof NIOBuffer && buffer != null && buffer.length() != 0 && buf2 instanceof NIOBuffer) {
            length = this.gatheringFlush(header, ((NIOBuffer)buf0).getByteBuffer(), buffer, ((NIOBuffer)buf2).getByteBuffer());
        }
        else {
            if (header != null && header.length() > 0) {
                length = this.flush(header);
            }
            if ((header == null || header.length() == 0) && buffer != null && buffer.length() > 0) {
                length += this.flush(buffer);
            }
            if ((header == null || header.length() == 0) && (buffer == null || buffer.length() == 0) && trailer != null && trailer.length() > 0) {
                length += this.flush(trailer);
            }
        }
        return length;
    }
    
    protected int gatheringFlush(final Buffer header, final ByteBuffer bbuf0, final Buffer buffer, final ByteBuffer bbuf1) throws IOException {
        int length;
        synchronized (this) {
            synchronized (bbuf0) {
                synchronized (bbuf1) {
                    try {
                        bbuf0.position(header.getIndex());
                        bbuf0.limit(header.putIndex());
                        bbuf1.position(buffer.getIndex());
                        bbuf1.limit(buffer.putIndex());
                        this._gather2[0] = bbuf0;
                        this._gather2[1] = bbuf1;
                        length = (int)((GatheringByteChannel)this._channel).write(this._gather2);
                        final int hl = header.length();
                        if (length > hl) {
                            header.clear();
                            buffer.skip(length - hl);
                        }
                        else if (length > 0) {
                            header.skip(length);
                        }
                    }
                    finally {
                        bbuf0.position(0);
                        bbuf1.position(0);
                        bbuf0.limit(bbuf0.capacity());
                        bbuf1.limit(bbuf1.capacity());
                    }
                }
            }
        }
        return length;
    }
    
    public ByteChannel getChannel() {
        return this._channel;
    }
    
    public String getLocalAddr() {
        if (this._socket == null) {
            return null;
        }
        if (this._local == null || this._local.getAddress() == null || this._local.getAddress().isAnyLocalAddress()) {
            return "0.0.0.0";
        }
        return this._local.getAddress().getHostAddress();
    }
    
    public String getLocalHost() {
        if (this._socket == null) {
            return null;
        }
        if (this._local == null || this._local.getAddress() == null || this._local.getAddress().isAnyLocalAddress()) {
            return "0.0.0.0";
        }
        return this._local.getAddress().getCanonicalHostName();
    }
    
    public int getLocalPort() {
        if (this._socket == null) {
            return 0;
        }
        if (this._local == null) {
            return -1;
        }
        return this._local.getPort();
    }
    
    public String getRemoteAddr() {
        if (this._socket == null) {
            return null;
        }
        if (this._remote == null) {
            return null;
        }
        return this._remote.getAddress().getHostAddress();
    }
    
    public String getRemoteHost() {
        if (this._socket == null) {
            return null;
        }
        if (this._remote == null) {
            return null;
        }
        return this._remote.getAddress().getCanonicalHostName();
    }
    
    public int getRemotePort() {
        if (this._socket == null) {
            return 0;
        }
        return (this._remote == null) ? -1 : this._remote.getPort();
    }
    
    public Object getTransport() {
        return this._channel;
    }
    
    public void flush() throws IOException {
    }
    
    public int getMaxIdleTime() {
        return this._maxIdleTime;
    }
    
    public void setMaxIdleTime(final int timeMs) throws IOException {
        if (this._socket != null && timeMs != this._maxIdleTime) {
            this._socket.setSoTimeout((timeMs > 0) ? timeMs : 0);
        }
        this._maxIdleTime = timeMs;
    }
    
    static {
        LOG = Log.getLogger(ChannelEndPoint.class);
    }
}
