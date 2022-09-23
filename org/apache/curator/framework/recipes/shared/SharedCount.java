// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.shared;

import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.curator.framework.state.ConnectionState;
import java.util.concurrent.Executor;
import org.apache.curator.shaded.com.google.common.util.concurrent.MoreExecutors;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import java.util.Map;
import org.apache.curator.framework.listen.Listenable;
import java.io.Closeable;

public class SharedCount implements Closeable, SharedCountReader, Listenable<SharedCountListener>
{
    private final Map<SharedCountListener, SharedValueListener> listeners;
    private final SharedValue sharedValue;
    
    public SharedCount(final CuratorFramework client, final String path, final int seedValue) {
        this.listeners = (Map<SharedCountListener, SharedValueListener>)Maps.newConcurrentMap();
        this.sharedValue = new SharedValue(client, path, toBytes(seedValue));
    }
    
    @Override
    public int getCount() {
        return fromBytes(this.sharedValue.getValue());
    }
    
    @Override
    public VersionedValue<Integer> getVersionedValue() {
        final VersionedValue<byte[]> localValue = this.sharedValue.getVersionedValue();
        return new VersionedValue<Integer>(localValue.getVersion(), fromBytes(localValue.getValue()));
    }
    
    public void setCount(final int newCount) throws Exception {
        this.sharedValue.setValue(toBytes(newCount));
    }
    
    @Deprecated
    public boolean trySetCount(final int newCount) throws Exception {
        return this.sharedValue.trySetValue(toBytes(newCount));
    }
    
    public boolean trySetCount(final VersionedValue<Integer> previous, final int newCount) throws Exception {
        final VersionedValue<byte[]> previousCopy = new VersionedValue<byte[]>(previous.getVersion(), toBytes(previous.getValue()));
        return this.sharedValue.trySetValue(previousCopy, toBytes(newCount));
    }
    
    @Override
    public void addListener(final SharedCountListener listener) {
        this.addListener(listener, (Executor)MoreExecutors.sameThreadExecutor());
    }
    
    @Override
    public void addListener(final SharedCountListener listener, final Executor executor) {
        final SharedValueListener valueListener = new SharedValueListener() {
            @Override
            public void valueHasChanged(final SharedValueReader sharedValue, final byte[] newValue) throws Exception {
                listener.countHasChanged(SharedCount.this, fromBytes(newValue));
            }
            
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                listener.stateChanged(client, newState);
            }
        };
        this.sharedValue.getListenable().addListener(valueListener, executor);
        this.listeners.put(listener, valueListener);
    }
    
    @Override
    public void removeListener(final SharedCountListener listener) {
        this.listeners.remove(listener);
    }
    
    public void start() throws Exception {
        this.sharedValue.start();
    }
    
    @Override
    public void close() throws IOException {
        this.sharedValue.close();
    }
    
    @VisibleForTesting
    static byte[] toBytes(final int value) {
        final byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(value);
        return bytes;
    }
    
    private static int fromBytes(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}
