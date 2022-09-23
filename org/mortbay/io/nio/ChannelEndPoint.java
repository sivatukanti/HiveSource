// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io.nio;

import java.nio.channels.GatheringByteChannel;
import org.mortbay.io.Buffer;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import org.mortbay.io.EndPoint;

public class ChannelEndPoint implements EndPoint
{
    protected final ByteChannel _channel;
    protected final ByteBuffer[] _gather2;
    protected final Socket _socket;
    protected final InetSocketAddress _local;
    protected final InetSocketAddress _remote;
    
    public ChannelEndPoint(final ByteChannel channel) {
        this._gather2 = new ByteBuffer[2];
        this._channel = channel;
        if (channel instanceof SocketChannel) {
            this._socket = ((SocketChannel)channel).socket();
            this._local = (InetSocketAddress)this._socket.getLocalSocketAddress();
            this._remote = (InetSocketAddress)this._socket.getRemoteSocketAddress();
        }
        else {
            this._socket = null;
            this._local = null;
            this._remote = null;
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
    
    public void shutdownOutput() throws IOException {
        if (this._channel.isOpen() && this._channel instanceof SocketChannel) {
            final Socket socket = ((SocketChannel)this._channel).socket();
            if (!socket.isClosed() && !socket.isOutputShutdown()) {
                socket.shutdownOutput();
            }
        }
    }
    
    public void close() throws IOException {
        if (this._socket != null && !this._socket.isOutputShutdown()) {
            this._socket.shutdownOutput();
        }
        this._channel.close();
    }
    
    public int fill(final Buffer buffer) throws IOException {
        final Buffer buf = buffer.buffer();
        int len = 0;
        if (buf instanceof NIOBuffer) {
            final NIOBuffer nbuf = (NIOBuffer)buf;
            final ByteBuffer bbuf = nbuf.getByteBuffer();
            synchronized (nbuf) {
                try {
                    bbuf.position(buffer.putIndex());
                    len = this._channel.read(bbuf);
                    if (len < 0) {
                        this._channel.close();
                    }
                }
                finally {
                    buffer.setPutIndex(bbuf.position());
                    bbuf.position(0);
                }
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
        if (this._channel instanceof GatheringByteChannel && header != null && header.length() != 0 && header instanceof NIOBuffer && buffer != null && buffer.length() != 0 && buffer instanceof NIOBuffer) {
            final NIOBuffer nbuf0 = (NIOBuffer)buf0;
            final ByteBuffer bbuf0 = nbuf0.getByteBuffer();
            final NIOBuffer nbuf2 = (NIOBuffer)buf2;
            final ByteBuffer bbuf2 = nbuf2.getByteBuffer();
            synchronized (this) {
                synchronized (bbuf0) {
                    synchronized (bbuf2) {
                        try {
                            bbuf0.position(header.getIndex());
                            bbuf0.limit(header.putIndex());
                            bbuf2.position(buffer.getIndex());
                            bbuf2.limit(buffer.putIndex());
                            this._gather2[0] = bbuf0;
                            this._gather2[1] = bbuf2;
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
                            if (!header.isImmutable()) {
                                header.setGetIndex(bbuf0.position());
                            }
                            if (!buffer.isImmutable()) {
                                buffer.setGetIndex(bbuf2.position());
                            }
                            bbuf0.position(0);
                            bbuf2.position(0);
                            bbuf0.limit(bbuf0.capacity());
                            bbuf2.limit(bbuf2.capacity());
                        }
                    }
                }
            }
        }
        else {
            if (header != null) {
                if (buffer != null && buffer.length() > 0 && header.space() > buffer.length()) {
                    header.put(buffer);
                    buffer.clear();
                }
                if (trailer != null && trailer.length() > 0 && header.space() > trailer.length()) {
                    header.put(trailer);
                    trailer.clear();
                }
            }
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
        if (this._remote == null) {
            return -1;
        }
        return (this._remote == null) ? -1 : this._remote.getPort();
    }
    
    public Object getTransport() {
        return this._channel;
    }
    
    public void flush() throws IOException {
    }
    
    public boolean isBufferingInput() {
        return false;
    }
    
    public boolean isBufferingOutput() {
        return false;
    }
    
    public boolean isBufferred() {
        return false;
    }
}
