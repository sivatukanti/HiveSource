// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;

public abstract class AbstractService implements Service
{
    private static final Log LOG;
    private STATE state;
    private final String name;
    private long startTime;
    private HiveConf hiveConf;
    private final List<ServiceStateChangeListener> listeners;
    
    public AbstractService(final String name) {
        this.state = STATE.NOTINITED;
        this.listeners = new ArrayList<ServiceStateChangeListener>();
        this.name = name;
    }
    
    @Override
    public synchronized STATE getServiceState() {
        return this.state;
    }
    
    @Override
    public synchronized void init(final HiveConf hiveConf) {
        this.ensureCurrentState(STATE.NOTINITED);
        this.hiveConf = hiveConf;
        this.changeState(STATE.INITED);
        AbstractService.LOG.info("Service:" + this.getName() + " is inited.");
    }
    
    @Override
    public synchronized void start() {
        this.startTime = System.currentTimeMillis();
        this.ensureCurrentState(STATE.INITED);
        this.changeState(STATE.STARTED);
        AbstractService.LOG.info("Service:" + this.getName() + " is started.");
    }
    
    @Override
    public synchronized void stop() {
        if (this.state == STATE.STOPPED || this.state == STATE.INITED || this.state == STATE.NOTINITED) {
            return;
        }
        this.ensureCurrentState(STATE.STARTED);
        this.changeState(STATE.STOPPED);
        AbstractService.LOG.info("Service:" + this.getName() + " is stopped.");
    }
    
    @Override
    public synchronized void register(final ServiceStateChangeListener l) {
        this.listeners.add(l);
    }
    
    @Override
    public synchronized void unregister(final ServiceStateChangeListener l) {
        this.listeners.remove(l);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public synchronized HiveConf getHiveConf() {
        return this.hiveConf;
    }
    
    @Override
    public long getStartTime() {
        return this.startTime;
    }
    
    private void ensureCurrentState(final STATE currentState) {
        ServiceOperations.ensureCurrentState(this.state, currentState);
    }
    
    private void changeState(final STATE newState) {
        this.state = newState;
        for (final ServiceStateChangeListener l : this.listeners) {
            l.stateChanged(this);
        }
    }
    
    static {
        LOG = LogFactory.getLog(AbstractService.class);
    }
}
