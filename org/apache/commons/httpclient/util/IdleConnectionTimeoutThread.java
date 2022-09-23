// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient.util;

import java.util.Iterator;
import org.apache.commons.httpclient.HttpConnectionManager;
import java.util.ArrayList;
import java.util.List;

public class IdleConnectionTimeoutThread extends Thread
{
    private List connectionManagers;
    private boolean shutdown;
    private long timeoutInterval;
    private long connectionTimeout;
    
    public IdleConnectionTimeoutThread() {
        this.connectionManagers = new ArrayList();
        this.shutdown = false;
        this.timeoutInterval = 1000L;
        this.connectionTimeout = 3000L;
        this.setDaemon(true);
    }
    
    public synchronized void addConnectionManager(final HttpConnectionManager connectionManager) {
        if (this.shutdown) {
            throw new IllegalStateException("IdleConnectionTimeoutThread has been shutdown");
        }
        this.connectionManagers.add(connectionManager);
    }
    
    public synchronized void removeConnectionManager(final HttpConnectionManager connectionManager) {
        if (this.shutdown) {
            throw new IllegalStateException("IdleConnectionTimeoutThread has been shutdown");
        }
        this.connectionManagers.remove(connectionManager);
    }
    
    protected void handleCloseIdleConnections(final HttpConnectionManager connectionManager) {
        connectionManager.closeIdleConnections(this.connectionTimeout);
    }
    
    public synchronized void run() {
        while (!this.shutdown) {
            for (final HttpConnectionManager connectionManager : this.connectionManagers) {
                this.handleCloseIdleConnections(connectionManager);
            }
            try {
                this.wait(this.timeoutInterval);
            }
            catch (InterruptedException ex) {}
        }
        this.connectionManagers.clear();
    }
    
    public synchronized void shutdown() {
        this.shutdown = true;
        this.notifyAll();
    }
    
    public synchronized void setConnectionTimeout(final long connectionTimeout) {
        if (this.shutdown) {
            throw new IllegalStateException("IdleConnectionTimeoutThread has been shutdown");
        }
        this.connectionTimeout = connectionTimeout;
    }
    
    public synchronized void setTimeoutInterval(final long timeoutInterval) {
        if (this.shutdown) {
            throw new IllegalStateException("IdleConnectionTimeoutThread has been shutdown");
        }
        this.timeoutInterval = timeoutInterval;
    }
}
