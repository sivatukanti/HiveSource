// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsException;
import javax.management.ObjectName;
import org.apache.hadoop.metrics2.impl.MetricsSystemImpl;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.metrics2.MetricsSystem;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public enum DefaultMetricsSystem
{
    INSTANCE;
    
    private AtomicReference<MetricsSystem> impl;
    @VisibleForTesting
    volatile boolean miniClusterMode;
    final transient UniqueNames mBeanNames;
    final transient UniqueNames sourceNames;
    
    private DefaultMetricsSystem() {
        this.impl = new AtomicReference<MetricsSystem>(new MetricsSystemImpl());
        this.miniClusterMode = false;
        this.mBeanNames = new UniqueNames();
        this.sourceNames = new UniqueNames();
    }
    
    public static MetricsSystem initialize(final String prefix) {
        return DefaultMetricsSystem.INSTANCE.init(prefix);
    }
    
    MetricsSystem init(final String prefix) {
        return this.impl.get().init(prefix);
    }
    
    public static MetricsSystem instance() {
        return DefaultMetricsSystem.INSTANCE.getImpl();
    }
    
    public static void shutdown() {
        DefaultMetricsSystem.INSTANCE.shutdownInstance();
    }
    
    void shutdownInstance() {
        final boolean last = this.impl.get().shutdown();
        if (last) {
            synchronized (this) {
                this.mBeanNames.map.clear();
                this.sourceNames.map.clear();
            }
        }
    }
    
    @InterfaceAudience.Private
    public static MetricsSystem setInstance(final MetricsSystem ms) {
        return DefaultMetricsSystem.INSTANCE.setImpl(ms);
    }
    
    MetricsSystem setImpl(final MetricsSystem ms) {
        return this.impl.getAndSet(ms);
    }
    
    MetricsSystem getImpl() {
        return this.impl.get();
    }
    
    @VisibleForTesting
    public static void setMiniClusterMode(final boolean choice) {
        DefaultMetricsSystem.INSTANCE.miniClusterMode = choice;
    }
    
    @VisibleForTesting
    public static boolean inMiniClusterMode() {
        return DefaultMetricsSystem.INSTANCE.miniClusterMode;
    }
    
    @InterfaceAudience.Private
    public static ObjectName newMBeanName(final String name) {
        return DefaultMetricsSystem.INSTANCE.newObjectName(name);
    }
    
    @InterfaceAudience.Private
    public static void removeMBeanName(final ObjectName name) {
        DefaultMetricsSystem.INSTANCE.removeObjectName(name.toString());
    }
    
    @InterfaceAudience.Private
    public static void removeSourceName(final String name) {
        DefaultMetricsSystem.INSTANCE.removeSource(name);
    }
    
    @InterfaceAudience.Private
    public static String sourceName(final String name, final boolean dupOK) {
        return DefaultMetricsSystem.INSTANCE.newSourceName(name, dupOK);
    }
    
    synchronized ObjectName newObjectName(final String name) {
        try {
            if (this.mBeanNames.map.containsKey(name) && !this.miniClusterMode) {
                throw new MetricsException(name + " already exists!");
            }
            return new ObjectName(this.mBeanNames.uniqueName(name));
        }
        catch (Exception e) {
            throw new MetricsException(e);
        }
    }
    
    synchronized void removeObjectName(final String name) {
        this.mBeanNames.map.remove(name);
    }
    
    synchronized void removeSource(final String name) {
        this.sourceNames.map.remove(name);
    }
    
    synchronized String newSourceName(final String name, final boolean dupOK) {
        if (this.sourceNames.map.containsKey(name)) {
            if (dupOK) {
                return name;
            }
            if (!this.miniClusterMode) {
                throw new MetricsException("Metrics source " + name + " already exists!");
            }
        }
        return this.sourceNames.uniqueName(name);
    }
}
