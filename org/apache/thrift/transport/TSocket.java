// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import org.slf4j.LoggerFactory;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.SocketException;
import java.net.Socket;
import org.slf4j.Logger;

public class TSocket extends TIOStreamTransport
{
    private static final Logger LOGGER;
    private Socket socket_;
    private String host_;
    private int port_;
    private int timeout_;
    
    public TSocket(final Socket socket) throws TTransportException {
        this.socket_ = null;
        this.host_ = null;
        this.port_ = 0;
        this.timeout_ = 0;
        this.socket_ = socket;
        try {
            this.socket_.setSoLinger(false, 0);
            this.socket_.setTcpNoDelay(true);
            this.socket_.setKeepAlive(true);
        }
        catch (SocketException sx) {
            TSocket.LOGGER.warn("Could not configure socket.", sx);
        }
        if (this.isOpen()) {
            try {
                this.inputStream_ = new BufferedInputStream(this.socket_.getInputStream(), 1024);
                this.outputStream_ = new BufferedOutputStream(this.socket_.getOutputStream(), 1024);
            }
            catch (IOException iox) {
                this.close();
                throw new TTransportException(1, iox);
            }
        }
    }
    
    public TSocket(final String host, final int port) {
        this(host, port, 0);
    }
    
    public TSocket(final String host, final int port, final int timeout) {
        this.socket_ = null;
        this.host_ = null;
        this.port_ = 0;
        this.timeout_ = 0;
        this.host_ = host;
        this.port_ = port;
        this.timeout_ = timeout;
        this.initSocket();
    }
    
    private void initSocket() {
        this.socket_ = new Socket();
        try {
            this.socket_.setSoLinger(false, 0);
            this.socket_.setTcpNoDelay(true);
            this.socket_.setKeepAlive(true);
            this.socket_.setSoTimeout(this.timeout_);
        }
        catch (SocketException sx) {
            TSocket.LOGGER.error("Could not configure socket.", sx);
        }
    }
    
    public void setTimeout(final int timeout) {
        this.timeout_ = timeout;
        try {
            this.socket_.setSoTimeout(timeout);
        }
        catch (SocketException sx) {
            TSocket.LOGGER.warn("Could not set socket timeout.", sx);
        }
    }
    
    public Socket getSocket() {
        if (this.socket_ == null) {
            this.initSocket();
        }
        return this.socket_;
    }
    
    @Override
    public boolean isOpen() {
        return this.socket_ != null && this.socket_.isConnected();
    }
    
    @Override
    public void open() throws TTransportException {
        if (this.isOpen()) {
            throw new TTransportException(2, "Socket already connected.");
        }
        if (this.host_.length() == 0) {
            throw new TTransportException(1, "Cannot open null host.");
        }
        if (this.port_ <= 0) {
            throw new TTransportException(1, "Cannot open without port.");
        }
        if (this.socket_ == null) {
            this.initSocket();
        }
        try {
            this.socket_.connect(new InetSocketAddress(this.host_, this.port_), this.timeout_);
            this.inputStream_ = new BufferedInputStream(this.socket_.getInputStream(), 1024);
            this.outputStream_ = new BufferedOutputStream(this.socket_.getOutputStream(), 1024);
        }
        catch (IOException iox) {
            this.close();
            throw new TTransportException(1, iox);
        }
    }
    
    @Override
    public void close() {
        super.close();
        if (this.socket_ != null) {
            try {
                this.socket_.close();
            }
            catch (IOException iox) {
                TSocket.LOGGER.warn("Could not close socket.", iox);
            }
            this.socket_ = null;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TSocket.class.getName());
    }
}
