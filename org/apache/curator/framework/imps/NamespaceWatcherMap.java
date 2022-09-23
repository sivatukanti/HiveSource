// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.Watcher;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Field;
import org.apache.curator.shaded.com.google.common.cache.CacheBuilder;
import java.util.concurrent.ConcurrentMap;
import java.io.Closeable;

class NamespaceWatcherMap implements Closeable
{
    private final ConcurrentMap<Object, NamespaceWatcher> map;
    private final CuratorFrameworkImpl client;
    
    NamespaceWatcherMap(final CuratorFrameworkImpl client) {
        this.map = CacheBuilder.newBuilder().weakValues().build().asMap();
        this.client = client;
    }
    
    @Override
    public void close() {
        this.map.clear();
    }
    
    @VisibleForTesting
    void drain() throws Exception {
        Runtime.getRuntime().gc();
        final Class mapMakerInternalMapClass = Class.forName("org.apache.curator.shaded.com.google.common.collect.MapMakerInternalMap");
        final Field drainThresholdField = mapMakerInternalMapClass.getDeclaredField("DRAIN_THRESHOLD");
        drainThresholdField.setAccessible(true);
        int drainThreshold = drainThresholdField.getInt(null) + 1;
        while (drainThreshold-- > 0) {
            this.map.get(new Object());
        }
    }
    
    NamespaceWatcher get(final Object key) {
        return this.map.get(key);
    }
    
    NamespaceWatcher remove(final Object key) {
        return this.map.remove(key);
    }
    
    @VisibleForTesting
    boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    NamespaceWatcher getNamespaceWatcher(final Watcher watcher) {
        return this.get(watcher, new NamespaceWatcher(this.client, watcher));
    }
    
    NamespaceWatcher getNamespaceWatcher(final CuratorWatcher watcher) {
        return this.get(watcher, new NamespaceWatcher(this.client, watcher));
    }
    
    private NamespaceWatcher get(final Object watcher, final NamespaceWatcher newNamespaceWatcher) {
        final NamespaceWatcher existingNamespaceWatcher = this.map.putIfAbsent(watcher, newNamespaceWatcher);
        return (existingNamespaceWatcher != null) ? existingNamespaceWatcher : newNamespaceWatcher;
    }
}
