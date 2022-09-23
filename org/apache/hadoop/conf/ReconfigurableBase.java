// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import java.util.Iterator;
import java.util.Collections;
import com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.Time;
import java.io.IOException;
import java.util.Collection;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.Map;
import org.slf4j.Logger;

public abstract class ReconfigurableBase extends Configured implements Reconfigurable
{
    private static final Logger LOG;
    private ReconfigurationUtil reconfigurationUtil;
    private Thread reconfigThread;
    private volatile boolean shouldRun;
    private Object reconfigLock;
    private long startTime;
    private long endTime;
    private Map<ReconfigurationUtil.PropertyChange, Optional<String>> status;
    
    public ReconfigurableBase() {
        super(new Configuration());
        this.reconfigurationUtil = new ReconfigurationUtil();
        this.reconfigThread = null;
        this.shouldRun = true;
        this.reconfigLock = new Object();
        this.startTime = 0L;
        this.endTime = 0L;
        this.status = null;
    }
    
    public ReconfigurableBase(final Configuration conf) {
        super((conf == null) ? new Configuration() : conf);
        this.reconfigurationUtil = new ReconfigurationUtil();
        this.reconfigThread = null;
        this.shouldRun = true;
        this.reconfigLock = new Object();
        this.startTime = 0L;
        this.endTime = 0L;
        this.status = null;
    }
    
    @VisibleForTesting
    public void setReconfigurationUtil(final ReconfigurationUtil ru) {
        this.reconfigurationUtil = Preconditions.checkNotNull(ru);
    }
    
    protected abstract Configuration getNewConf();
    
    @VisibleForTesting
    public Collection<ReconfigurationUtil.PropertyChange> getChangedProperties(final Configuration newConf, final Configuration oldConf) {
        return this.reconfigurationUtil.parseChangedProperties(newConf, oldConf);
    }
    
    public void startReconfigurationTask() throws IOException {
        synchronized (this.reconfigLock) {
            if (!this.shouldRun) {
                final String errorMessage = "The server is stopped.";
                ReconfigurableBase.LOG.warn(errorMessage);
                throw new IOException(errorMessage);
            }
            if (this.reconfigThread != null) {
                final String errorMessage = "Another reconfiguration task is running.";
                ReconfigurableBase.LOG.warn(errorMessage);
                throw new IOException(errorMessage);
            }
            (this.reconfigThread = new ReconfigurationThread(this)).setDaemon(true);
            this.reconfigThread.setName("Reconfiguration Task");
            this.reconfigThread.start();
            this.startTime = Time.now();
        }
    }
    
    public ReconfigurationTaskStatus getReconfigurationTaskStatus() {
        synchronized (this.reconfigLock) {
            if (this.reconfigThread != null) {
                return new ReconfigurationTaskStatus(this.startTime, 0L, null);
            }
            return new ReconfigurationTaskStatus(this.startTime, this.endTime, this.status);
        }
    }
    
    public void shutdownReconfigurationTask() {
        final Thread tempThread;
        synchronized (this.reconfigLock) {
            this.shouldRun = false;
            if (this.reconfigThread == null) {
                return;
            }
            tempThread = this.reconfigThread;
            this.reconfigThread = null;
        }
        try {
            tempThread.join();
        }
        catch (InterruptedException ex) {}
    }
    
    @Override
    public final void reconfigureProperty(final String property, final String newVal) throws ReconfigurationException {
        if (this.isPropertyReconfigurable(property)) {
            ReconfigurableBase.LOG.info("changing property " + property + " to " + newVal);
            synchronized (this.getConf()) {
                this.getConf().get(property);
                final String effectiveValue = this.reconfigurePropertyImpl(property, newVal);
                if (newVal != null) {
                    this.getConf().set(property, effectiveValue);
                }
                else {
                    this.getConf().unset(property);
                }
            }
            return;
        }
        throw new ReconfigurationException(property, newVal, this.getConf().get(property));
    }
    
    @Override
    public abstract Collection<String> getReconfigurableProperties();
    
    @Override
    public boolean isPropertyReconfigurable(final String property) {
        return this.getReconfigurableProperties().contains(property);
    }
    
    protected abstract String reconfigurePropertyImpl(final String p0, final String p1) throws ReconfigurationException;
    
    static {
        LOG = LoggerFactory.getLogger(ReconfigurableBase.class);
    }
    
    private static class ReconfigurationThread extends Thread
    {
        private ReconfigurableBase parent;
        
        ReconfigurationThread(final ReconfigurableBase base) {
            this.parent = base;
        }
        
        @Override
        public void run() {
            ReconfigurableBase.LOG.info("Starting reconfiguration task.");
            final Configuration oldConf = this.parent.getConf();
            final Configuration newConf = this.parent.getNewConf();
            final Collection<ReconfigurationUtil.PropertyChange> changes = this.parent.getChangedProperties(newConf, oldConf);
            final Map<ReconfigurationUtil.PropertyChange, Optional<String>> results = (Map<ReconfigurationUtil.PropertyChange, Optional<String>>)Maps.newHashMap();
            final ConfigRedactor oldRedactor = new ConfigRedactor(oldConf);
            final ConfigRedactor newRedactor = new ConfigRedactor(newConf);
            for (final ReconfigurationUtil.PropertyChange change : changes) {
                String errorMessage = null;
                final String oldValRedacted = oldRedactor.redact(change.prop, change.oldVal);
                final String newValRedacted = newRedactor.redact(change.prop, change.newVal);
                if (!this.parent.isPropertyReconfigurable(change.prop)) {
                    ReconfigurableBase.LOG.info(String.format("Property %s is not configurable: old value: %s, new value: %s", change.prop, oldValRedacted, newValRedacted));
                }
                else {
                    ReconfigurableBase.LOG.info("Change property: " + change.prop + " from \"" + ((change.oldVal == null) ? "<default>" : oldValRedacted) + "\" to \"" + ((change.newVal == null) ? "<default>" : newValRedacted) + "\".");
                    try {
                        final String effectiveValue = this.parent.reconfigurePropertyImpl(change.prop, change.newVal);
                        if (change.newVal != null) {
                            oldConf.set(change.prop, effectiveValue);
                        }
                        else {
                            oldConf.unset(change.prop);
                        }
                    }
                    catch (ReconfigurationException e) {
                        errorMessage = e.getCause().getMessage();
                    }
                    results.put(change, Optional.ofNullable(errorMessage));
                }
            }
            synchronized (this.parent.reconfigLock) {
                this.parent.endTime = Time.now();
                this.parent.status = (Map<ReconfigurationUtil.PropertyChange, Optional<String>>)Collections.unmodifiableMap((Map<?, ?>)results);
                this.parent.reconfigThread = null;
            }
        }
    }
}
