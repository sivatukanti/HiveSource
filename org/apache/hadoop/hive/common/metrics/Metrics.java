// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.metrics;

import javax.management.MalformedObjectNameException;
import java.io.IOException;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import javax.management.ObjectName;

public class Metrics
{
    private static final MetricsMBean metrics;
    private static final ObjectName oname;
    private static final ThreadLocal<HashMap<String, MetricsScope>> threadLocalScopes;
    private static boolean initialized;
    
    private Metrics() {
    }
    
    public static void init() throws Exception {
        synchronized (Metrics.metrics) {
            if (!Metrics.initialized) {
                final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                mbs.registerMBean(Metrics.metrics, Metrics.oname);
                Metrics.initialized = true;
            }
        }
    }
    
    public static Long incrementCounter(final String name) throws IOException {
        if (!Metrics.initialized) {
            return null;
        }
        return incrementCounter(name, 1L);
    }
    
    public static Long incrementCounter(final String name, final long increment) throws IOException {
        if (!Metrics.initialized) {
            return null;
        }
        Long value;
        synchronized (Metrics.metrics) {
            if (!Metrics.metrics.hasKey(name)) {
                value = increment;
                set(name, value);
            }
            else {
                value = (long)get(name) + increment;
                set(name, value);
            }
        }
        return value;
    }
    
    public static void set(final String name, final Object value) throws IOException {
        if (!Metrics.initialized) {
            return;
        }
        Metrics.metrics.put(name, value);
    }
    
    public static Object get(final String name) throws IOException {
        if (!Metrics.initialized) {
            return null;
        }
        return Metrics.metrics.get(name);
    }
    
    public static MetricsScope startScope(final String name) throws IOException {
        if (!Metrics.initialized) {
            return null;
        }
        if (Metrics.threadLocalScopes.get().containsKey(name)) {
            Metrics.threadLocalScopes.get().get(name).open();
        }
        else {
            Metrics.threadLocalScopes.get().put(name, new MetricsScope(name));
        }
        return Metrics.threadLocalScopes.get().get(name);
    }
    
    public static MetricsScope getScope(final String name) throws IOException {
        if (!Metrics.initialized) {
            return null;
        }
        if (Metrics.threadLocalScopes.get().containsKey(name)) {
            return Metrics.threadLocalScopes.get().get(name);
        }
        throw new IOException("No metrics scope named " + name);
    }
    
    public static void endScope(final String name) throws IOException {
        if (!Metrics.initialized) {
            return;
        }
        if (Metrics.threadLocalScopes.get().containsKey(name)) {
            Metrics.threadLocalScopes.get().get(name).close();
        }
    }
    
    static void uninit() throws Exception {
        synchronized (Metrics.metrics) {
            if (Metrics.initialized) {
                final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                if (mbs.isRegistered(Metrics.oname)) {
                    mbs.unregisterMBean(Metrics.oname);
                }
                Metrics.metrics.clear();
                Metrics.initialized = false;
            }
        }
    }
    
    static {
        metrics = new MetricsMBeanImpl();
        try {
            oname = new ObjectName("org.apache.hadoop.hive.common.metrics:type=MetricsMBean");
        }
        catch (MalformedObjectNameException mone) {
            throw new RuntimeException(mone);
        }
        threadLocalScopes = new ThreadLocal<HashMap<String, MetricsScope>>() {
            @Override
            protected HashMap<String, MetricsScope> initialValue() {
                return new HashMap<String, MetricsScope>();
            }
        };
        Metrics.initialized = false;
    }
    
    public static class MetricsScope
    {
        final String name;
        final String numCounter;
        final String timeCounter;
        final String avgTimeCounter;
        private boolean isOpen;
        private Long startTime;
        
        private MetricsScope(final String name) throws IOException {
            this.isOpen = false;
            this.startTime = null;
            this.name = name;
            this.numCounter = name + ".n";
            this.timeCounter = name + ".t";
            this.avgTimeCounter = name + ".avg_t";
            this.open();
        }
        
        public Long getNumCounter() throws IOException {
            return (Long)Metrics.get(this.numCounter);
        }
        
        public Long getTimeCounter() throws IOException {
            return (Long)Metrics.get(this.timeCounter);
        }
        
        public void open() throws IOException {
            if (!this.isOpen) {
                this.isOpen = true;
                this.startTime = System.currentTimeMillis();
                return;
            }
            throw new IOException("Scope named " + this.name + " is not closed, cannot be opened.");
        }
        
        public void close() throws IOException {
            if (this.isOpen) {
                final Long endTime = System.currentTimeMillis();
                synchronized (Metrics.metrics) {
                    final Long num = Metrics.incrementCounter(this.numCounter);
                    final Long time = Metrics.incrementCounter(this.timeCounter, endTime - this.startTime);
                    if (num != null && time != null) {
                        Metrics.set(this.avgTimeCounter, time / (double)num);
                    }
                }
                this.isOpen = false;
                return;
            }
            throw new IOException("Scope named " + this.name + " is not open, cannot be closed.");
        }
        
        public void reopen() throws IOException {
            if (this.isOpen) {
                this.close();
            }
            this.open();
        }
    }
}
