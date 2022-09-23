// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import parquet.org.slf4j.LoggerFactory;
import java.net.Socket;
import java.net.SocketException;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import parquet.org.slf4j.Logger;

public class TServerSocket extends TServerTransport
{
    private static final Logger LOGGER;
    private ServerSocket serverSocket_;
    private int clientTimeout_;
    
    public TServerSocket(final ServerSocket serverSocket) {
        this(serverSocket, 0);
    }
    
    public TServerSocket(final ServerSocket serverSocket, final int clientTimeout) {
        this.serverSocket_ = null;
        this.clientTimeout_ = 0;
        this.serverSocket_ = serverSocket;
        this.clientTimeout_ = clientTimeout;
    }
    
    public TServerSocket(final int port) throws TTransportException {
        this(port, 0);
    }
    
    public TServerSocket(final int port, final int clientTimeout) throws TTransportException {
        this(new InetSocketAddress(port), clientTimeout);
    }
    
    public TServerSocket(final InetSocketAddress bindAddr) throws TTransportException {
        this(bindAddr, 0);
    }
    
    public TServerSocket(final InetSocketAddress bindAddr, final int clientTimeout) throws TTransportException {
        this.serverSocket_ = null;
        this.clientTimeout_ = 0;
        this.clientTimeout_ = clientTimeout;
        try {
            (this.serverSocket_ = new ServerSocket()).setReuseAddress(true);
            this.serverSocket_.bind(bindAddr);
        }
        catch (IOException ioe) {
            this.serverSocket_ = null;
            throw new TTransportException("Could not create ServerSocket on address " + bindAddr.toString() + ".");
        }
    }
    
    @Override
    public void listen() throws TTransportException {
        if (this.serverSocket_ != null) {
            try {
                this.serverSocket_.setSoTimeout(0);
            }
            catch (SocketException sx) {
                TServerSocket.LOGGER.error("Could not set socket timeout.", sx);
            }
        }
    }
    
    @Override
    protected TSocket acceptImpl() throws TTransportException {
        if (this.serverSocket_ == null) {
            throw new TTransportException(1, "No underlying server socket.");
        }
        try {
            final Socket result = this.serverSocket_.accept();
            final TSocket result2 = new TSocket(result);
            result2.setTimeout(this.clientTimeout_);
            return result2;
        }
        catch (IOException iox) {
            throw new TTransportException(iox);
        }
    }
    
    @Override
    public void close() {
        if (this.serverSocket_ != null) {
            try {
                this.serverSocket_.close();
            }
            catch (IOException iox) {
                TServerSocket.LOGGER.warn("Could not close server socket.", iox);
            }
            this.serverSocket_ = null;
        }
    }
    
    @Override
    public void interrupt() {
        this.close();
    }
    
    public ServerSocket getServerSocket() {
        return this.serverSocket_;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TServerSocket.class.getName());
    }
}
