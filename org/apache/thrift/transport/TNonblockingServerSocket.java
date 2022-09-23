// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import org.slf4j.LoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.net.SocketException;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import org.slf4j.Logger;

public class TNonblockingServerSocket extends TNonblockingServerTransport
{
    private static final Logger LOGGER;
    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket_;
    private int clientTimeout_;
    
    public TNonblockingServerSocket(final int port) throws TTransportException {
        this(port, 0);
    }
    
    public TNonblockingServerSocket(final int port, final int clientTimeout) throws TTransportException {
        this(new NonblockingAbstractServerSocketArgs().port(port).clientTimeout(clientTimeout));
    }
    
    public TNonblockingServerSocket(final InetSocketAddress bindAddr) throws TTransportException {
        this(bindAddr, 0);
    }
    
    public TNonblockingServerSocket(final InetSocketAddress bindAddr, final int clientTimeout) throws TTransportException {
        this(new NonblockingAbstractServerSocketArgs().bindAddr(bindAddr).clientTimeout(clientTimeout));
    }
    
    public TNonblockingServerSocket(final NonblockingAbstractServerSocketArgs args) throws TTransportException {
        this.serverSocketChannel = null;
        this.serverSocket_ = null;
        this.clientTimeout_ = 0;
        this.clientTimeout_ = args.clientTimeout;
        try {
            (this.serverSocketChannel = ServerSocketChannel.open()).configureBlocking(false);
            (this.serverSocket_ = this.serverSocketChannel.socket()).setReuseAddress(true);
            this.serverSocket_.bind(args.bindAddr, args.backlog);
        }
        catch (IOException ioe) {
            this.serverSocket_ = null;
            throw new TTransportException("Could not create ServerSocket on address " + args.bindAddr.toString() + ".");
        }
    }
    
    @Override
    public void listen() throws TTransportException {
        if (this.serverSocket_ != null) {
            try {
                this.serverSocket_.setSoTimeout(0);
            }
            catch (SocketException sx) {
                sx.printStackTrace();
            }
        }
    }
    
    @Override
    protected TNonblockingSocket acceptImpl() throws TTransportException {
        if (this.serverSocket_ == null) {
            throw new TTransportException(1, "No underlying server socket.");
        }
        try {
            final SocketChannel socketChannel = this.serverSocketChannel.accept();
            if (socketChannel == null) {
                return null;
            }
            final TNonblockingSocket tsocket = new TNonblockingSocket(socketChannel);
            tsocket.setTimeout(this.clientTimeout_);
            return tsocket;
        }
        catch (IOException iox) {
            throw new TTransportException(iox);
        }
    }
    
    @Override
    public void registerSelector(final Selector selector) {
        try {
            this.serverSocketChannel.register(selector, 16);
        }
        catch (ClosedChannelException ex) {}
    }
    
    @Override
    public void close() {
        if (this.serverSocket_ != null) {
            try {
                this.serverSocket_.close();
            }
            catch (IOException iox) {
                TNonblockingServerSocket.LOGGER.warn("WARNING: Could not close server socket: " + iox.getMessage());
            }
            this.serverSocket_ = null;
        }
    }
    
    @Override
    public void interrupt() {
        this.close();
    }
    
    public int getPort() {
        if (this.serverSocket_ == null) {
            return -1;
        }
        return this.serverSocket_.getLocalPort();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TNonblockingServerTransport.class.getName());
    }
    
    public static class NonblockingAbstractServerSocketArgs extends AbstractServerTransportArgs<NonblockingAbstractServerSocketArgs>
    {
    }
}
