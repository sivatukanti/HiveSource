// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.net.SocketAddress;
import org.slf4j.Logger;

public class TNonblockingSocket extends TNonblockingTransport
{
    private static final Logger LOGGER;
    private final SocketAddress socketAddress_;
    private final SocketChannel socketChannel_;
    
    public TNonblockingSocket(final String host, final int port) throws IOException {
        this(host, port, 0);
    }
    
    public TNonblockingSocket(final String host, final int port, final int timeout) throws IOException {
        this(SocketChannel.open(), timeout, new InetSocketAddress(host, port));
    }
    
    public TNonblockingSocket(final SocketChannel socketChannel) throws IOException {
        this(socketChannel, 0, null);
        if (!socketChannel.isConnected()) {
            throw new IOException("Socket must already be connected");
        }
    }
    
    private TNonblockingSocket(final SocketChannel socketChannel, final int timeout, final SocketAddress socketAddress) throws IOException {
        this.socketChannel_ = socketChannel;
        this.socketAddress_ = socketAddress;
        socketChannel.configureBlocking(false);
        final Socket socket = socketChannel.socket();
        socket.setSoLinger(false, 0);
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        this.setTimeout(timeout);
    }
    
    @Override
    public SelectionKey registerSelector(final Selector selector, final int interests) throws IOException {
        return this.socketChannel_.register(selector, interests);
    }
    
    public void setTimeout(final int timeout) {
        try {
            this.socketChannel_.socket().setSoTimeout(timeout);
        }
        catch (SocketException sx) {
            TNonblockingSocket.LOGGER.warn("Could not set socket timeout.", sx);
        }
    }
    
    public SocketChannel getSocketChannel() {
        return this.socketChannel_;
    }
    
    @Override
    public boolean isOpen() {
        return this.socketChannel_.isOpen() && this.socketChannel_.isConnected();
    }
    
    @Override
    public void open() throws TTransportException {
        throw new RuntimeException("open() is not implemented for TNonblockingSocket");
    }
    
    @Override
    public int read(final ByteBuffer buffer) throws IOException {
        return this.socketChannel_.read(buffer);
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        if ((this.socketChannel_.validOps() & 0x1) != 0x1) {
            throw new TTransportException(1, "Cannot read from write-only socket channel");
        }
        try {
            return this.socketChannel_.read(ByteBuffer.wrap(buf, off, len));
        }
        catch (IOException iox) {
            throw new TTransportException(0, iox);
        }
    }
    
    @Override
    public int write(final ByteBuffer buffer) throws IOException {
        return this.socketChannel_.write(buffer);
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        if ((this.socketChannel_.validOps() & 0x4) != 0x4) {
            throw new TTransportException(1, "Cannot write to write-only socket channel");
        }
        try {
            this.socketChannel_.write(ByteBuffer.wrap(buf, off, len));
        }
        catch (IOException iox) {
            throw new TTransportException(0, iox);
        }
    }
    
    @Override
    public void flush() throws TTransportException {
    }
    
    @Override
    public void close() {
        try {
            this.socketChannel_.close();
        }
        catch (IOException iox) {
            TNonblockingSocket.LOGGER.warn("Could not close socket.", iox);
        }
    }
    
    @Override
    public boolean startConnect() throws IOException {
        return this.socketChannel_.connect(this.socketAddress_);
    }
    
    @Override
    public boolean finishConnect() throws IOException {
        return this.socketChannel_.finishConnect();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TNonblockingSocket.class.getName());
    }
}
