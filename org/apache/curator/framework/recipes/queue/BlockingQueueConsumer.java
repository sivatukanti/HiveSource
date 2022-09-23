// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.CuratorFramework;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.curator.framework.state.ConnectionStateListener;

public class BlockingQueueConsumer<T> implements QueueConsumer<T>
{
    private final ConnectionStateListener connectionStateListener;
    private final BlockingQueue<T> items;
    
    public BlockingQueueConsumer(final ConnectionStateListener connectionStateListener) {
        this(connectionStateListener, (BlockingQueue)new LinkedBlockingQueue());
    }
    
    public BlockingQueueConsumer(final ConnectionStateListener connectionStateListener, final int capacity) {
        this(connectionStateListener, (BlockingQueue)new ArrayBlockingQueue(capacity));
    }
    
    public BlockingQueueConsumer(final ConnectionStateListener connectionStateListener, final BlockingQueue<T> queue) {
        this.connectionStateListener = connectionStateListener;
        this.items = queue;
    }
    
    @Override
    public void consumeMessage(final T message) throws Exception {
        this.items.add(message);
    }
    
    public List<T> getItems() {
        return (List<T>)ImmutableList.copyOf((Collection<?>)this.items);
    }
    
    public int size() {
        return this.items.size();
    }
    
    public T take() throws InterruptedException {
        return this.items.take();
    }
    
    public T take(final int time, final TimeUnit unit) throws InterruptedException {
        return this.items.poll(time, unit);
    }
    
    public int drainTo(final Collection<? super T> c) {
        return this.items.drainTo(c);
    }
    
    @Override
    public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
        this.connectionStateListener.stateChanged(client, newState);
    }
}
