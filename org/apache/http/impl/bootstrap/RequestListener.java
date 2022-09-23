// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.bootstrap;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.protocol.HttpService;
import java.net.ServerSocket;
import org.apache.http.config.SocketConfig;

class RequestListener implements Runnable
{
    private final SocketConfig socketConfig;
    private final ServerSocket serversocket;
    private final HttpService httpService;
    private final HttpConnectionFactory<? extends HttpServerConnection> connectionFactory;
    private final ExceptionLogger exceptionLogger;
    private final ExecutorService executorService;
    private final AtomicBoolean terminated;
    
    public RequestListener(final SocketConfig socketConfig, final ServerSocket serversocket, final HttpService httpService, final HttpConnectionFactory<? extends HttpServerConnection> connectionFactory, final ExceptionLogger exceptionLogger, final ExecutorService executorService) {
        this.socketConfig = socketConfig;
        this.serversocket = serversocket;
        this.connectionFactory = connectionFactory;
        this.httpService = httpService;
        this.exceptionLogger = exceptionLogger;
        this.executorService = executorService;
        this.terminated = new AtomicBoolean(false);
    }
    
    @Override
    public void run() {
        try {
            while (!this.isTerminated() && !Thread.interrupted()) {
                final Socket socket = this.serversocket.accept();
                socket.setSoTimeout(this.socketConfig.getSoTimeout());
                socket.setKeepAlive(this.socketConfig.isSoKeepAlive());
                socket.setTcpNoDelay(this.socketConfig.isTcpNoDelay());
                if (this.socketConfig.getRcvBufSize() > 0) {
                    socket.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
                }
                if (this.socketConfig.getSndBufSize() > 0) {
                    socket.setSendBufferSize(this.socketConfig.getSndBufSize());
                }
                if (this.socketConfig.getSoLinger() >= 0) {
                    socket.setSoLinger(true, this.socketConfig.getSoLinger());
                }
                final HttpServerConnection conn = (HttpServerConnection)this.connectionFactory.createConnection(socket);
                final Worker worker = new Worker(this.httpService, conn, this.exceptionLogger);
                this.executorService.execute(worker);
            }
        }
        catch (Exception ex) {
            this.exceptionLogger.log(ex);
        }
    }
    
    public boolean isTerminated() {
        return this.terminated.get();
    }
    
    public void terminate() throws IOException {
        if (this.terminated.compareAndSet(false, true)) {
            this.serversocket.close();
        }
    }
}
