// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.RPC;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Daemon;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HealthMonitor
{
    private static final Logger LOG;
    private Daemon daemon;
    private long connectRetryInterval;
    private long checkIntervalMillis;
    private long sleepAfterDisconnectMillis;
    private int rpcTimeout;
    private volatile boolean shouldRun;
    private HAServiceProtocol proxy;
    private final HAServiceTarget targetToMonitor;
    private final Configuration conf;
    private State state;
    private List<Callback> callbacks;
    private List<ServiceStateCallback> serviceStateCallbacks;
    private HAServiceStatus lastServiceState;
    
    HealthMonitor(final Configuration conf, final HAServiceTarget target) {
        this.shouldRun = true;
        this.state = State.INITIALIZING;
        this.callbacks = Collections.synchronizedList(new LinkedList<Callback>());
        this.serviceStateCallbacks = Collections.synchronizedList(new LinkedList<ServiceStateCallback>());
        this.lastServiceState = new HAServiceStatus(HAServiceProtocol.HAServiceState.INITIALIZING);
        this.targetToMonitor = target;
        this.conf = conf;
        this.sleepAfterDisconnectMillis = conf.getLong("ha.health-monitor.sleep-after-disconnect.ms", 1000L);
        this.checkIntervalMillis = conf.getLong("ha.health-monitor.check-interval.ms", 1000L);
        this.connectRetryInterval = conf.getLong("ha.health-monitor.connect-retry-interval.ms", 1000L);
        this.rpcTimeout = conf.getInt("ha.health-monitor.rpc-timeout.ms", 45000);
        this.daemon = new MonitorDaemon();
    }
    
    public void addCallback(final Callback cb) {
        this.callbacks.add(cb);
    }
    
    public void removeCallback(final Callback cb) {
        this.callbacks.remove(cb);
    }
    
    public synchronized void addServiceStateCallback(final ServiceStateCallback cb) {
        this.serviceStateCallbacks.add(cb);
    }
    
    public synchronized void removeServiceStateCallback(final ServiceStateCallback cb) {
        this.serviceStateCallbacks.remove(cb);
    }
    
    public void shutdown() {
        HealthMonitor.LOG.info("Stopping HealthMonitor thread");
        this.shouldRun = false;
        this.daemon.interrupt();
    }
    
    public synchronized HAServiceProtocol getProxy() {
        return this.proxy;
    }
    
    private void loopUntilConnected() throws InterruptedException {
        this.tryConnect();
        while (this.proxy == null) {
            Thread.sleep(this.connectRetryInterval);
            this.tryConnect();
        }
        assert this.proxy != null;
    }
    
    private void tryConnect() {
        Preconditions.checkState(this.proxy == null);
        try {
            synchronized (this) {
                this.proxy = this.createProxy();
            }
        }
        catch (IOException e) {
            HealthMonitor.LOG.warn("Could not connect to local service at " + this.targetToMonitor + ": " + e.getMessage());
            this.proxy = null;
            this.enterState(State.SERVICE_NOT_RESPONDING);
        }
    }
    
    protected HAServiceProtocol createProxy() throws IOException {
        return this.targetToMonitor.getHealthMonitorProxy(this.conf, this.rpcTimeout);
    }
    
    private void doHealthChecks() throws InterruptedException {
        while (this.shouldRun) {
            HAServiceStatus status = null;
            boolean healthy = false;
            try {
                status = this.proxy.getServiceStatus();
                this.proxy.monitorHealth();
                healthy = true;
            }
            catch (Throwable t) {
                if (!this.isHealthCheckFailedException(t)) {
                    HealthMonitor.LOG.warn("Transport-level exception trying to monitor health of {}", this.targetToMonitor, t);
                    RPC.stopProxy(this.proxy);
                    this.proxy = null;
                    this.enterState(State.SERVICE_NOT_RESPONDING);
                    Thread.sleep(this.sleepAfterDisconnectMillis);
                    return;
                }
                HealthMonitor.LOG.warn("Service health check failed for {}", this.targetToMonitor, t);
                this.enterState(State.SERVICE_UNHEALTHY);
            }
            if (status != null) {
                this.setLastServiceStatus(status);
            }
            if (healthy) {
                this.enterState(State.SERVICE_HEALTHY);
            }
            Thread.sleep(this.checkIntervalMillis);
        }
    }
    
    private boolean isHealthCheckFailedException(final Throwable t) {
        return t instanceof HealthCheckFailedException || (t instanceof RemoteException && ((RemoteException)t).unwrapRemoteException(HealthCheckFailedException.class) instanceof HealthCheckFailedException);
    }
    
    private synchronized void setLastServiceStatus(final HAServiceStatus status) {
        this.lastServiceState = status;
        for (final ServiceStateCallback cb : this.serviceStateCallbacks) {
            cb.reportServiceStatus(this.lastServiceState);
        }
    }
    
    private synchronized void enterState(final State newState) {
        if (newState != this.state) {
            HealthMonitor.LOG.info("Entering state {}", newState);
            this.state = newState;
            synchronized (this.callbacks) {
                for (final Callback cb : this.callbacks) {
                    cb.enteredState(newState);
                }
            }
        }
    }
    
    synchronized State getHealthState() {
        return this.state;
    }
    
    synchronized HAServiceStatus getLastServiceStatus() {
        return this.lastServiceState;
    }
    
    boolean isAlive() {
        return this.daemon.isAlive();
    }
    
    void join() throws InterruptedException {
        this.daemon.join();
    }
    
    void start() {
        this.daemon.start();
    }
    
    static {
        LOG = LoggerFactory.getLogger(HealthMonitor.class);
    }
    
    @InterfaceAudience.Private
    public enum State
    {
        INITIALIZING, 
        SERVICE_NOT_RESPONDING, 
        SERVICE_HEALTHY, 
        SERVICE_UNHEALTHY, 
        HEALTH_MONITOR_FAILED;
    }
    
    private class MonitorDaemon extends Daemon
    {
        private MonitorDaemon() {
            this.setName("Health Monitor for " + HealthMonitor.this.targetToMonitor);
            this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(final Thread t, final Throwable e) {
                    HealthMonitor.LOG.error("Health monitor failed", e);
                    HealthMonitor.this.enterState(HealthMonitor.State.HEALTH_MONITOR_FAILED);
                }
            });
        }
        
        @Override
        public void run() {
            while (HealthMonitor.this.shouldRun) {
                try {
                    HealthMonitor.this.loopUntilConnected();
                    HealthMonitor.this.doHealthChecks();
                }
                catch (InterruptedException ie) {
                    Preconditions.checkState(!HealthMonitor.this.shouldRun, (Object)"Interrupted but still supposed to run");
                }
            }
        }
    }
    
    interface ServiceStateCallback
    {
        void reportServiceStatus(final HAServiceStatus p0);
    }
    
    interface Callback
    {
        void enteredState(final State p0);
    }
}
