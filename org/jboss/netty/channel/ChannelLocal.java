// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.util.Set;
import java.util.Collections;
import java.util.Iterator;
import org.jboss.netty.util.internal.ConcurrentIdentityWeakKeyHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Map;

public class ChannelLocal<T> implements Iterable<Map.Entry<Channel, T>>
{
    private final ConcurrentMap<Channel, T> map;
    private final ChannelFutureListener remover;
    private final boolean removeOnClose;
    
    public ChannelLocal() {
        this(false);
    }
    
    public ChannelLocal(final boolean removeOnClose) {
        this.map = new ConcurrentIdentityWeakKeyHashMap<Channel, T>();
        this.remover = new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                ChannelLocal.this.remove(future.getChannel());
            }
        };
        this.removeOnClose = removeOnClose;
    }
    
    protected T initialValue(final Channel channel) {
        return null;
    }
    
    public T get(final Channel channel) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        T value = this.map.get(channel);
        if (value == null) {
            value = this.initialValue(channel);
            if (value != null) {
                final T oldValue = this.setIfAbsent(channel, value);
                if (oldValue != null) {
                    value = oldValue;
                }
            }
        }
        return value;
    }
    
    public T set(final Channel channel, final T value) {
        if (value == null) {
            return this.remove(channel);
        }
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        final T old = this.map.put(channel, value);
        if (this.removeOnClose) {
            channel.getCloseFuture().addListener(this.remover);
        }
        return old;
    }
    
    public T setIfAbsent(final Channel channel, final T value) {
        if (value == null) {
            return this.get(channel);
        }
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        final T mapping = this.map.putIfAbsent(channel, value);
        if (this.removeOnClose && mapping == null) {
            channel.getCloseFuture().addListener(this.remover);
        }
        return mapping;
    }
    
    public T remove(final Channel channel) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        final T removed = this.map.remove(channel);
        if (removed == null) {
            return this.initialValue(channel);
        }
        if (this.removeOnClose) {
            channel.getCloseFuture().removeListener(this.remover);
        }
        return removed;
    }
    
    public Iterator<Map.Entry<Channel, T>> iterator() {
        return Collections.unmodifiableSet((Set<? extends Map.Entry<Channel, T>>)this.map.entrySet()).iterator();
    }
}
