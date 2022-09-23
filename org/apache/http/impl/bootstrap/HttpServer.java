// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.bootstrap;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import org.apache.http.HttpServerConnection;
import javax.net.ssl.SSLServerSocket;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ExecutorService;
import org.apache.http.ExceptionLogger;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.protocol.HttpService;
import javax.net.ServerSocketFactory;
import org.apache.http.config.SocketConfig;
import java.net.InetAddress;

public class HttpServer
{
    private final int port;
    private final InetAddress ifAddress;
    private final SocketConfig socketConfig;
    private final ServerSocketFactory serverSocketFactory;
    private final HttpService httpService;
    private final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
    private final SSLServerSetupHandler sslSetupHandler;
    private final ExceptionLogger exceptionLogger;
    private final ExecutorService listenerExecutorService;
    private final ThreadGroup workerThreads;
    private final ExecutorService workerExecutorService;
    private final AtomicReference<Status> status;
    private volatile ServerSocket serverSocket;
    private volatile RequestListener requestListener;
    
    HttpServer(final int port, final InetAddress ifAddress, final SocketConfig socketConfig, final ServerSocketFactory serverSocketFactory, final HttpService httpService, final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory, final SSLServerSetupHandler sslSetupHandler, final ExceptionLogger exceptionLogger) {
        this.port = port;
        this.ifAddress = ifAddress;
        this.socketConfig = socketConfig;
        this.serverSocketFactory = serverSocketFactory;
        this.httpService = httpService;
        this.connectionFactory = connectionFactory;
        this.sslSetupHandler = sslSetupHandler;
        this.exceptionLogger = exceptionLogger;
        this.listenerExecutorService = Executors.newSingleThreadExecutor(new ThreadFactoryImpl("HTTP-listener-" + this.port));
        this.workerThreads = new ThreadGroup("HTTP-workers");
        this.workerExecutorService = Executors.newCachedThreadPool(new ThreadFactoryImpl("HTTP-worker", this.workerThreads));
        this.status = new AtomicReference<Status>(Status.READY);
    }
    
    public InetAddress getInetAddress() {
        final ServerSocket localSocket = this.serverSocket;
        if (localSocket != null) {
            return localSocket.getInetAddress();
        }
        return null;
    }
    
    public int getLocalPort() {
        final ServerSocket localSocket = this.serverSocket;
        if (localSocket != null) {
            return localSocket.getLocalPort();
        }
        return -1;
    }
    
    public void start() throws IOException {
        if (this.status.compareAndSet(Status.READY, Status.ACTIVE)) {
            (this.serverSocket = this.serverSocketFactory.createServerSocket(this.port, this.socketConfig.getBacklogSize(), this.ifAddress)).setReuseAddress(this.socketConfig.isSoReuseAddress());
            if (this.socketConfig.getRcvBufSize() > 0) {
                this.serverSocket.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
            }
            if (this.sslSetupHandler != null && this.serverSocket instanceof SSLServerSocket) {
                this.sslSetupHandler.initialize((SSLServerSocket)this.serverSocket);
            }
            this.requestListener = new RequestListener(this.socketConfig, this.serverSocket, this.httpService, this.connectionFactory, this.exceptionLogger, this.workerExecutorService);
            this.listenerExecutorService.execute(this.requestListener);
        }
    }
    
    public void stop() {
        if (this.status.compareAndSet(Status.ACTIVE, Status.STOPPING)) {
            final RequestListener local = this.requestListener;
            if (local != null) {
                try {
                    local.terminate();
                }
                catch (IOException ex) {
                    this.exceptionLogger.log(ex);
                }
            }
            this.workerThreads.interrupt();
            this.listenerExecutorService.shutdown();
            this.workerExecutorService.shutdown();
        }
    }
    
    public void awaitTermination(final long timeout, final TimeUnit timeUnit) throws InterruptedException {
        this.workerExecutorService.awaitTermination(timeout, timeUnit);
    }
    
    public void shutdown(final long gracePeriod, final TimeUnit timeUnit) {
        this.stop();
        if (gracePeriod > 0L) {
            try {
                this.awaitTermination(gracePeriod, timeUnit);
            }
            catch (InterruptedException ex2) {
                Thread.currentThread().interrupt();
            }
        }
        final List<Runnable> runnables = this.workerExecutorService.shutdownNow();
        for (final Runnable runnable : runnables) {
            if (runnable instanceof Worker) {
                final Worker worker = (Worker)runnable;
                final HttpServerConnection conn = worker.getConnection();
                try {
                    conn.shutdown();
                }
                catch (IOException ex) {
                    this.exceptionLogger.log(ex);
                }
            }
        }
    }
    
    enum Status
    {
        READY, 
        ACTIVE, 
        STOPPING;
    }
}
