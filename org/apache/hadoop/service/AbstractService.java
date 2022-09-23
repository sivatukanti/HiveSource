// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import org.slf4j.LoggerFactory;
import java.util.Collection;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AbstractService implements Service
{
    private static final Logger LOG;
    private final String name;
    private final ServiceStateModel stateModel;
    private long startTime;
    private volatile Configuration config;
    private final ServiceOperations.ServiceListeners listeners;
    private static ServiceOperations.ServiceListeners globalListeners;
    private Exception failureCause;
    private STATE failureState;
    private final AtomicBoolean terminationNotification;
    private final List<LifecycleEvent> lifecycleHistory;
    private final Map<String, String> blockerMap;
    private final Object stateChangeLock;
    
    public AbstractService(final String name) {
        this.listeners = new ServiceOperations.ServiceListeners();
        this.failureState = null;
        this.terminationNotification = new AtomicBoolean(false);
        this.lifecycleHistory = new ArrayList<LifecycleEvent>(5);
        this.blockerMap = new HashMap<String, String>();
        this.stateChangeLock = new Object();
        this.name = name;
        this.stateModel = new ServiceStateModel(name);
    }
    
    @Override
    public final STATE getServiceState() {
        return this.stateModel.getState();
    }
    
    @Override
    public final synchronized Throwable getFailureCause() {
        return this.failureCause;
    }
    
    @Override
    public synchronized STATE getFailureState() {
        return this.failureState;
    }
    
    protected void setConfig(final Configuration conf) {
        this.config = conf;
    }
    
    @Override
    public void init(final Configuration conf) {
        if (conf == null) {
            throw new ServiceStateException("Cannot initialize service " + this.getName() + ": null configuration");
        }
        if (this.isInState(STATE.INITED)) {
            return;
        }
        synchronized (this.stateChangeLock) {
            if (this.enterState(STATE.INITED) != STATE.INITED) {
                this.setConfig(conf);
                try {
                    this.serviceInit(this.config);
                    if (this.isInState(STATE.INITED)) {
                        this.notifyListeners();
                    }
                }
                catch (Exception e) {
                    this.noteFailure(e);
                    ServiceOperations.stopQuietly(AbstractService.LOG, this);
                    throw ServiceStateException.convert(e);
                }
            }
        }
    }
    
    @Override
    public void start() {
        if (this.isInState(STATE.STARTED)) {
            return;
        }
        synchronized (this.stateChangeLock) {
            if (this.stateModel.enterState(STATE.STARTED) != STATE.STARTED) {
                try {
                    this.startTime = System.currentTimeMillis();
                    this.serviceStart();
                    if (this.isInState(STATE.STARTED)) {
                        AbstractService.LOG.debug("Service {} is started", this.getName());
                        this.notifyListeners();
                    }
                }
                catch (Exception e) {
                    this.noteFailure(e);
                    ServiceOperations.stopQuietly(AbstractService.LOG, this);
                    throw ServiceStateException.convert(e);
                }
            }
        }
    }
    
    @Override
    public void stop() {
        if (this.isInState(STATE.STOPPED)) {
            return;
        }
        synchronized (this.stateChangeLock) {
            if (this.enterState(STATE.STOPPED) != STATE.STOPPED) {
                try {
                    this.serviceStop();
                }
                catch (Exception e) {
                    this.noteFailure(e);
                    throw ServiceStateException.convert(e);
                }
                finally {
                    this.terminationNotification.set(true);
                    synchronized (this.terminationNotification) {
                        this.terminationNotification.notifyAll();
                    }
                    this.notifyListeners();
                }
            }
            else {
                AbstractService.LOG.debug("Ignoring re-entrant call to stop()");
            }
        }
    }
    
    @Override
    public final void close() throws IOException {
        this.stop();
    }
    
    protected final void noteFailure(final Exception exception) {
        AbstractService.LOG.debug("noteFailure", exception);
        if (exception == null) {
            return;
        }
        synchronized (this) {
            if (this.failureCause == null) {
                this.failureCause = exception;
                this.failureState = this.getServiceState();
                AbstractService.LOG.info("Service {} failed in state {}", this.getName(), this.failureState, exception);
            }
        }
    }
    
    @Override
    public final boolean waitForServiceToStop(final long timeout) {
        boolean completed = this.terminationNotification.get();
        while (!completed) {
            try {
                synchronized (this.terminationNotification) {
                    this.terminationNotification.wait(timeout);
                }
                completed = true;
            }
            catch (InterruptedException e) {
                completed = this.terminationNotification.get();
            }
        }
        return this.terminationNotification.get();
    }
    
    protected void serviceInit(final Configuration conf) throws Exception {
        if (conf != this.config) {
            AbstractService.LOG.debug("Config has been overridden during init");
            this.setConfig(conf);
        }
    }
    
    protected void serviceStart() throws Exception {
    }
    
    protected void serviceStop() throws Exception {
    }
    
    @Override
    public void registerServiceListener(final ServiceStateChangeListener l) {
        this.listeners.add(l);
    }
    
    @Override
    public void unregisterServiceListener(final ServiceStateChangeListener l) {
        this.listeners.remove(l);
    }
    
    public static void registerGlobalListener(final ServiceStateChangeListener l) {
        AbstractService.globalListeners.add(l);
    }
    
    public static boolean unregisterGlobalListener(final ServiceStateChangeListener l) {
        return AbstractService.globalListeners.remove(l);
    }
    
    @VisibleForTesting
    static void resetGlobalListeners() {
        AbstractService.globalListeners.reset();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Configuration getConfig() {
        return this.config;
    }
    
    @Override
    public long getStartTime() {
        return this.startTime;
    }
    
    private void notifyListeners() {
        try {
            this.listeners.notifyListeners(this);
            AbstractService.globalListeners.notifyListeners(this);
        }
        catch (Throwable e) {
            AbstractService.LOG.warn("Exception while notifying listeners of {}", this, e);
        }
    }
    
    private void recordLifecycleEvent() {
        final LifecycleEvent event = new LifecycleEvent();
        event.time = System.currentTimeMillis();
        event.state = this.getServiceState();
        this.lifecycleHistory.add(event);
    }
    
    @Override
    public synchronized List<LifecycleEvent> getLifecycleHistory() {
        return new ArrayList<LifecycleEvent>(this.lifecycleHistory);
    }
    
    private STATE enterState(final STATE newState) {
        assert this.stateModel != null : "null state in " + this.name + " " + this.getClass();
        final STATE oldState = this.stateModel.enterState(newState);
        if (oldState != newState) {
            AbstractService.LOG.debug("Service: {} entered state {}", this.getName(), this.getServiceState());
            this.recordLifecycleEvent();
        }
        return oldState;
    }
    
    @Override
    public final boolean isInState(final STATE expected) {
        return this.stateModel.isInState(expected);
    }
    
    @Override
    public String toString() {
        return "Service " + this.name + " in state " + this.stateModel;
    }
    
    protected void putBlocker(final String name, final String details) {
        synchronized (this.blockerMap) {
            this.blockerMap.put(name, details);
        }
    }
    
    public void removeBlocker(final String name) {
        synchronized (this.blockerMap) {
            this.blockerMap.remove(name);
        }
    }
    
    @Override
    public Map<String, String> getBlockers() {
        synchronized (this.blockerMap) {
            final Map<String, String> map = new HashMap<String, String>(this.blockerMap);
            return map;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractService.class);
        AbstractService.globalListeners = new ServiceOperations.ServiceListeners();
    }
}
