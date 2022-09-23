// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.util.ajax.WaitingContinuation;
import org.mortbay.util.ajax.Continuation;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.mortbay.io.EndPoint;
import java.net.Socket;
import java.io.IOException;
import org.mortbay.log.Log;
import org.mortbay.component.LifeCycle;
import org.mortbay.thread.ThreadPool;

public abstract class AbstractConnector extends AbstractBuffers implements Connector
{
    private String _name;
    private Server _server;
    private ThreadPool _threadPool;
    private String _host;
    private int _port;
    private String _integralScheme;
    private int _integralPort;
    private String _confidentialScheme;
    private int _confidentialPort;
    private int _acceptQueueSize;
    private int _acceptors;
    private int _acceptorPriorityOffset;
    private boolean _useDNS;
    private boolean _forwarded;
    private String _hostHeader;
    private String _forwardedHostHeader;
    private String _forwardedServerHeader;
    private String _forwardedForHeader;
    private boolean _reuseAddress;
    protected int _maxIdleTime;
    protected int _lowResourceMaxIdleTime;
    protected int _soLingerTime;
    private transient Thread[] _acceptorThread;
    Object _statsLock;
    transient long _statsStartedAt;
    transient int _requests;
    transient int _connections;
    transient int _connectionsOpen;
    transient int _connectionsOpenMin;
    transient int _connectionsOpenMax;
    transient long _connectionsDurationMin;
    transient long _connectionsDurationMax;
    transient long _connectionsDurationTotal;
    transient int _connectionsRequestsMin;
    transient int _connectionsRequestsMax;
    
    public AbstractConnector() {
        this._port = 0;
        this._integralScheme = "https";
        this._integralPort = 0;
        this._confidentialScheme = "https";
        this._confidentialPort = 0;
        this._acceptQueueSize = 0;
        this._acceptors = 1;
        this._acceptorPriorityOffset = 0;
        this._forwardedHostHeader = "X-Forwarded-Host";
        this._forwardedServerHeader = "X-Forwarded-Server";
        this._forwardedForHeader = "X-Forwarded-For";
        this._reuseAddress = true;
        this._maxIdleTime = 200000;
        this._lowResourceMaxIdleTime = -1;
        this._soLingerTime = -1;
        this._statsLock = new Object();
        this._statsStartedAt = -1L;
    }
    
    public Server getServer() {
        return this._server;
    }
    
    public void setServer(final Server server) {
        this._server = server;
    }
    
    public ThreadPool getThreadPool() {
        return this._threadPool;
    }
    
    public void setThreadPool(final ThreadPool pool) {
        this._threadPool = pool;
    }
    
    public void setHost(final String host) {
        this._host = host;
    }
    
    public String getHost() {
        return this._host;
    }
    
    public void setPort(final int port) {
        this._port = port;
    }
    
    public int getPort() {
        return this._port;
    }
    
    public int getMaxIdleTime() {
        return this._maxIdleTime;
    }
    
    public void setMaxIdleTime(final int maxIdleTime) {
        this._maxIdleTime = maxIdleTime;
    }
    
    public int getLowResourceMaxIdleTime() {
        return this._lowResourceMaxIdleTime;
    }
    
    public void setLowResourceMaxIdleTime(final int maxIdleTime) {
        this._lowResourceMaxIdleTime = maxIdleTime;
    }
    
    public int getSoLingerTime() {
        return this._soLingerTime;
    }
    
    public int getAcceptQueueSize() {
        return this._acceptQueueSize;
    }
    
    public void setAcceptQueueSize(final int acceptQueueSize) {
        this._acceptQueueSize = acceptQueueSize;
    }
    
    public int getAcceptors() {
        return this._acceptors;
    }
    
    public void setAcceptors(final int acceptors) {
        this._acceptors = acceptors;
    }
    
    public void setSoLingerTime(final int soLingerTime) {
        this._soLingerTime = soLingerTime;
    }
    
    protected void doStart() throws Exception {
        if (this._server == null) {
            throw new IllegalStateException("No server");
        }
        this.open();
        super.doStart();
        if (this._threadPool == null) {
            this._threadPool = this._server.getThreadPool();
        }
        if (this._threadPool != this._server.getThreadPool() && this._threadPool instanceof LifeCycle) {
            ((LifeCycle)this._threadPool).start();
        }
        synchronized (this) {
            this._acceptorThread = new Thread[this.getAcceptors()];
            for (int i = 0; i < this._acceptorThread.length; ++i) {
                if (!this._threadPool.dispatch(new Acceptor(i))) {
                    Log.warn("insufficient maxThreads configured for {}", this);
                    break;
                }
            }
        }
        Log.info("Started {}", this);
    }
    
    protected void doStop() throws Exception {
        Log.info("Stopped {}", this);
        try {
            this.close();
        }
        catch (IOException e) {
            Log.warn(e);
        }
        if (this._threadPool == this._server.getThreadPool()) {
            this._threadPool = null;
        }
        else if (this._threadPool instanceof LifeCycle) {
            ((LifeCycle)this._threadPool).stop();
        }
        super.doStop();
        Thread[] acceptors = null;
        synchronized (this) {
            acceptors = this._acceptorThread;
            this._acceptorThread = null;
        }
        if (acceptors != null) {
            for (int i = 0; i < acceptors.length; ++i) {
                final Thread thread = acceptors[i];
                if (thread != null) {
                    thread.interrupt();
                }
            }
        }
    }
    
    public void join() throws InterruptedException {
        final Thread[] threads = this._acceptorThread;
        if (threads != null) {
            for (int i = 0; i < threads.length; ++i) {
                if (threads[i] != null) {
                    threads[i].join();
                }
            }
        }
    }
    
    protected void configure(final Socket socket) throws IOException {
        try {
            socket.setTcpNoDelay(true);
            if (this._maxIdleTime >= 0) {
                socket.setSoTimeout(this._maxIdleTime);
            }
            if (this._soLingerTime >= 0) {
                socket.setSoLinger(true, this._soLingerTime / 1000);
            }
            else {
                socket.setSoLinger(false, 0);
            }
        }
        catch (Exception e) {
            Log.ignore(e);
        }
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        if (this.isForwarded()) {
            this.checkForwardedHeaders(endpoint, request);
        }
    }
    
    protected void checkForwardedHeaders(final EndPoint endpoint, final Request request) throws IOException {
        final HttpFields httpFields = request.getConnection().getRequestFields();
        final String forwardedHost = this.getLeftMostValue(httpFields.getStringField(this.getForwardedHostHeader()));
        final String forwardedServer = this.getLeftMostValue(httpFields.getStringField(this.getForwardedServerHeader()));
        final String forwardedFor = this.getLeftMostValue(httpFields.getStringField(this.getForwardedForHeader()));
        if (this._hostHeader != null) {
            httpFields.put(HttpHeaders.HOST_BUFFER, this._hostHeader);
            request.setServerName(null);
            request.setServerPort(-1);
            request.getServerName();
        }
        else if (forwardedHost != null) {
            httpFields.put(HttpHeaders.HOST_BUFFER, forwardedHost);
            request.setServerName(null);
            request.setServerPort(-1);
            request.getServerName();
        }
        else if (forwardedServer != null) {
            request.setServerName(forwardedServer);
        }
        if (forwardedFor != null) {
            request.setRemoteAddr(forwardedFor);
            InetAddress inetAddress = null;
            if (this._useDNS) {
                try {
                    inetAddress = InetAddress.getByName(forwardedFor);
                }
                catch (UnknownHostException e) {
                    Log.ignore(e);
                }
            }
            request.setRemoteHost((inetAddress == null) ? forwardedFor : inetAddress.getHostName());
        }
    }
    
    protected String getLeftMostValue(final String headerValue) {
        if (headerValue == null) {
            return null;
        }
        final int commaIndex = headerValue.indexOf(44);
        if (commaIndex == -1) {
            return headerValue;
        }
        return headerValue.substring(0, commaIndex);
    }
    
    public void persist(final EndPoint endpoint) throws IOException {
    }
    
    public int getConfidentialPort() {
        return this._confidentialPort;
    }
    
    public String getConfidentialScheme() {
        return this._confidentialScheme;
    }
    
    public boolean isIntegral(final Request request) {
        return false;
    }
    
    public int getIntegralPort() {
        return this._integralPort;
    }
    
    public String getIntegralScheme() {
        return this._integralScheme;
    }
    
    public boolean isConfidential(final Request request) {
        return false;
    }
    
    public void setConfidentialPort(final int confidentialPort) {
        this._confidentialPort = confidentialPort;
    }
    
    public void setConfidentialScheme(final String confidentialScheme) {
        this._confidentialScheme = confidentialScheme;
    }
    
    public void setIntegralPort(final int integralPort) {
        this._integralPort = integralPort;
    }
    
    public void setIntegralScheme(final String integralScheme) {
        this._integralScheme = integralScheme;
    }
    
    public Continuation newContinuation() {
        return new WaitingContinuation();
    }
    
    protected abstract void accept(final int p0) throws IOException, InterruptedException;
    
    public void stopAccept(final int acceptorID) throws Exception {
    }
    
    public boolean getResolveNames() {
        return this._useDNS;
    }
    
    public void setResolveNames(final boolean resolve) {
        this._useDNS = resolve;
    }
    
    public boolean isForwarded() {
        return this._forwarded;
    }
    
    public void setForwarded(final boolean check) {
        if (check) {
            Log.debug(this + " is forwarded");
        }
        this._forwarded = check;
    }
    
    public String getHostHeader() {
        return this._hostHeader;
    }
    
    public void setHostHeader(final String hostHeader) {
        this._hostHeader = hostHeader;
    }
    
    public String getForwardedHostHeader() {
        return this._forwardedHostHeader;
    }
    
    public void setForwardedHostHeader(final String forwardedHostHeader) {
        this._forwardedHostHeader = forwardedHostHeader;
    }
    
    public String getForwardedServerHeader() {
        return this._forwardedServerHeader;
    }
    
    public void setForwardedServerHeader(final String forwardedServerHeader) {
        this._forwardedServerHeader = forwardedServerHeader;
    }
    
    public String getForwardedForHeader() {
        return this._forwardedForHeader;
    }
    
    public void setForwardedForHeader(final String forwardedRemoteAddressHeader) {
        this._forwardedForHeader = forwardedRemoteAddressHeader;
    }
    
    public String toString() {
        String name = this.getClass().getName();
        final int dot = name.lastIndexOf(46);
        if (dot > 0) {
            name = name.substring(dot + 1);
        }
        return name + "@" + ((this.getHost() == null) ? "0.0.0.0" : this.getHost()) + ":" + ((this.getLocalPort() <= 0) ? this.getPort() : this.getLocalPort());
    }
    
    public String getName() {
        if (this._name == null) {
            this._name = ((this.getHost() == null) ? "0.0.0.0" : this.getHost()) + ":" + ((this.getLocalPort() <= 0) ? this.getPort() : this.getLocalPort());
        }
        return this._name;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public int getRequests() {
        return this._requests;
    }
    
    public long getConnectionsDurationMin() {
        return this._connectionsDurationMin;
    }
    
    public long getConnectionsDurationTotal() {
        return this._connectionsDurationTotal;
    }
    
    public int getConnectionsOpenMin() {
        return this._connectionsOpenMin;
    }
    
    public int getConnectionsRequestsMin() {
        return this._connectionsRequestsMin;
    }
    
    public int getConnections() {
        return this._connections;
    }
    
    public int getConnectionsOpen() {
        return this._connectionsOpen;
    }
    
    public int getConnectionsOpenMax() {
        return this._connectionsOpenMax;
    }
    
    public long getConnectionsDurationAve() {
        return (this._connections == 0) ? 0L : (this._connectionsDurationTotal / this._connections);
    }
    
    public long getConnectionsDurationMax() {
        return this._connectionsDurationMax;
    }
    
    public int getConnectionsRequestsAve() {
        return (this._connections == 0) ? 0 : (this._requests / this._connections);
    }
    
    public int getConnectionsRequestsMax() {
        return this._connectionsRequestsMax;
    }
    
    public void statsReset() {
        this._statsStartedAt = ((this._statsStartedAt == -1L) ? -1L : System.currentTimeMillis());
        this._connections = 0;
        this._connectionsOpenMin = this._connectionsOpen;
        this._connectionsOpenMax = this._connectionsOpen;
        this._connectionsOpen = 0;
        this._connectionsDurationMin = 0L;
        this._connectionsDurationMax = 0L;
        this._connectionsDurationTotal = 0L;
        this._requests = 0;
        this._connectionsRequestsMin = 0;
        this._connectionsRequestsMax = 0;
    }
    
    public void setStatsOn(final boolean on) {
        if (on && this._statsStartedAt != -1L) {
            return;
        }
        Log.debug("Statistics on = " + on + " for " + this);
        this.statsReset();
        this._statsStartedAt = (on ? System.currentTimeMillis() : -1L);
    }
    
    public boolean getStatsOn() {
        return this._statsStartedAt != -1L;
    }
    
    public long getStatsOnMs() {
        return (this._statsStartedAt != -1L) ? (System.currentTimeMillis() - this._statsStartedAt) : 0L;
    }
    
    protected void connectionOpened(final HttpConnection connection) {
        if (this._statsStartedAt == -1L) {
            return;
        }
        synchronized (this._statsLock) {
            ++this._connectionsOpen;
            if (this._connectionsOpen > this._connectionsOpenMax) {
                this._connectionsOpenMax = this._connectionsOpen;
            }
        }
    }
    
    protected void connectionClosed(final HttpConnection connection) {
        if (this._statsStartedAt >= 0L) {
            final long duration = System.currentTimeMillis() - connection.getTimeStamp();
            final int requests = connection.getRequests();
            synchronized (this._statsLock) {
                this._requests += requests;
                ++this._connections;
                --this._connectionsOpen;
                this._connectionsDurationTotal += duration;
                if (this._connectionsOpen < 0) {
                    this._connectionsOpen = 0;
                }
                if (this._connectionsOpen < this._connectionsOpenMin) {
                    this._connectionsOpenMin = this._connectionsOpen;
                }
                if (this._connectionsDurationMin == 0L || duration < this._connectionsDurationMin) {
                    this._connectionsDurationMin = duration;
                }
                if (duration > this._connectionsDurationMax) {
                    this._connectionsDurationMax = duration;
                }
                if (this._connectionsRequestsMin == 0 || requests < this._connectionsRequestsMin) {
                    this._connectionsRequestsMin = requests;
                }
                if (requests > this._connectionsRequestsMax) {
                    this._connectionsRequestsMax = requests;
                }
            }
        }
        connection.destroy();
    }
    
    public int getAcceptorPriorityOffset() {
        return this._acceptorPriorityOffset;
    }
    
    public void setAcceptorPriorityOffset(final int offset) {
        this._acceptorPriorityOffset = offset;
    }
    
    public boolean getReuseAddress() {
        return this._reuseAddress;
    }
    
    public void setReuseAddress(final boolean reuseAddress) {
        this._reuseAddress = reuseAddress;
    }
    
    private class Acceptor implements Runnable
    {
        int _acceptor;
        
        Acceptor(final int id) {
            this._acceptor = 0;
            this._acceptor = id;
        }
        
        public void run() {
            final Thread current = Thread.currentThread();
            final String name;
            synchronized (AbstractConnector.this) {
                if (AbstractConnector.this._acceptorThread == null) {
                    return;
                }
                AbstractConnector.this._acceptorThread[this._acceptor] = current;
                name = AbstractConnector.this._acceptorThread[this._acceptor].getName();
                current.setName(name + " - Acceptor" + this._acceptor + " " + AbstractConnector.this);
            }
            final int old_priority = current.getPriority();
            try {
                current.setPriority(old_priority - AbstractConnector.this._acceptorPriorityOffset);
                while (AbstractConnector.this.isRunning() && AbstractConnector.this.getConnection() != null) {
                    try {
                        AbstractConnector.this.accept(this._acceptor);
                        continue;
                    }
                    catch (EofException e) {
                        Log.ignore(e);
                        continue;
                    }
                    catch (IOException e2) {
                        Log.ignore(e2);
                        continue;
                    }
                    catch (ThreadDeath e3) {
                        throw e3;
                    }
                    catch (Throwable e4) {
                        Log.warn(e4);
                        continue;
                    }
                    break;
                }
            }
            finally {
                current.setPriority(old_priority);
                current.setName(name);
                synchronized (AbstractConnector.this) {
                    if (AbstractConnector.this._acceptorThread != null) {
                        AbstractConnector.this._acceptorThread[this._acceptor] = null;
                    }
                }
            }
        }
    }
}
