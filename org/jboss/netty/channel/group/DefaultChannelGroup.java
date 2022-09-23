// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.group;

import java.net.SocketAddress;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import org.jboss.netty.channel.ServerChannel;
import org.jboss.netty.channel.ChannelFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.netty.channel.ChannelFutureListener;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.channel.Channel;
import java.util.AbstractSet;

public class DefaultChannelGroup extends AbstractSet<Channel> implements ChannelGroup
{
    private static final AtomicInteger nextId;
    private final String name;
    private final ConcurrentMap<Integer, Channel> serverChannels;
    private final ConcurrentMap<Integer, Channel> nonServerChannels;
    private final ChannelFutureListener remover;
    
    public DefaultChannelGroup() {
        this("group-0x" + Integer.toHexString(DefaultChannelGroup.nextId.incrementAndGet()));
    }
    
    public DefaultChannelGroup(final String name) {
        this.serverChannels = new ConcurrentHashMap<Integer, Channel>();
        this.nonServerChannels = new ConcurrentHashMap<Integer, Channel>();
        this.remover = new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                DefaultChannelGroup.this.remove(future.getChannel());
            }
        };
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean isEmpty() {
        return this.nonServerChannels.isEmpty() && this.serverChannels.isEmpty();
    }
    
    @Override
    public int size() {
        return this.nonServerChannels.size() + this.serverChannels.size();
    }
    
    public Channel find(final Integer id) {
        final Channel c = this.nonServerChannels.get(id);
        if (c != null) {
            return c;
        }
        return this.serverChannels.get(id);
    }
    
    @Override
    public boolean contains(final Object o) {
        if (o instanceof Integer) {
            return this.nonServerChannels.containsKey(o) || this.serverChannels.containsKey(o);
        }
        if (!(o instanceof Channel)) {
            return false;
        }
        final Channel c = (Channel)o;
        if (o instanceof ServerChannel) {
            return this.serverChannels.containsKey(c.getId());
        }
        return this.nonServerChannels.containsKey(c.getId());
    }
    
    @Override
    public boolean add(final Channel channel) {
        final ConcurrentMap<Integer, Channel> map = (channel instanceof ServerChannel) ? this.serverChannels : this.nonServerChannels;
        final boolean added = map.putIfAbsent(channel.getId(), channel) == null;
        if (added) {
            channel.getCloseFuture().addListener(this.remover);
        }
        return added;
    }
    
    @Override
    public boolean remove(final Object o) {
        Channel c = null;
        if (o instanceof Integer) {
            c = this.nonServerChannels.remove(o);
            if (c == null) {
                c = this.serverChannels.remove(o);
            }
        }
        else if (o instanceof Channel) {
            c = (Channel)o;
            if (c instanceof ServerChannel) {
                c = this.serverChannels.remove(c.getId());
            }
            else {
                c = this.nonServerChannels.remove(c.getId());
            }
        }
        if (c == null) {
            return false;
        }
        c.getCloseFuture().removeListener(this.remover);
        return true;
    }
    
    @Override
    public void clear() {
        this.nonServerChannels.clear();
        this.serverChannels.clear();
    }
    
    @Override
    public Iterator<Channel> iterator() {
        return new CombinedIterator<Channel>(this.serverChannels.values().iterator(), this.nonServerChannels.values().iterator());
    }
    
    @Override
    public Object[] toArray() {
        final Collection<Channel> channels = new ArrayList<Channel>(this.size());
        channels.addAll(this.serverChannels.values());
        channels.addAll(this.nonServerChannels.values());
        return channels.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        final Collection<Channel> channels = new ArrayList<Channel>(this.size());
        channels.addAll(this.serverChannels.values());
        channels.addAll(this.nonServerChannels.values());
        return channels.toArray(a);
    }
    
    public ChannelGroupFuture close() {
        final Map<Integer, ChannelFuture> futures = new LinkedHashMap<Integer, ChannelFuture>(this.size());
        for (final Channel c : this.serverChannels.values()) {
            futures.put(c.getId(), c.close().awaitUninterruptibly());
        }
        for (final Channel c : this.nonServerChannels.values()) {
            futures.put(c.getId(), c.close());
        }
        return new DefaultChannelGroupFuture(this, futures);
    }
    
    public ChannelGroupFuture disconnect() {
        final Map<Integer, ChannelFuture> futures = new LinkedHashMap<Integer, ChannelFuture>(this.size());
        for (final Channel c : this.serverChannels.values()) {
            futures.put(c.getId(), c.disconnect().awaitUninterruptibly());
        }
        for (final Channel c : this.nonServerChannels.values()) {
            futures.put(c.getId(), c.disconnect());
        }
        return new DefaultChannelGroupFuture(this, futures);
    }
    
    public ChannelGroupFuture setInterestOps(final int interestOps) {
        final Map<Integer, ChannelFuture> futures = new LinkedHashMap<Integer, ChannelFuture>(this.size());
        for (final Channel c : this.serverChannels.values()) {
            futures.put(c.getId(), c.setInterestOps(interestOps).awaitUninterruptibly());
        }
        for (final Channel c : this.nonServerChannels.values()) {
            futures.put(c.getId(), c.setInterestOps(interestOps));
        }
        return new DefaultChannelGroupFuture(this, futures);
    }
    
    public ChannelGroupFuture setReadable(final boolean readable) {
        final Map<Integer, ChannelFuture> futures = new LinkedHashMap<Integer, ChannelFuture>(this.size());
        for (final Channel c : this.serverChannels.values()) {
            futures.put(c.getId(), c.setReadable(readable).awaitUninterruptibly());
        }
        for (final Channel c : this.nonServerChannels.values()) {
            futures.put(c.getId(), c.setReadable(readable));
        }
        return new DefaultChannelGroupFuture(this, futures);
    }
    
    public ChannelGroupFuture unbind() {
        final Map<Integer, ChannelFuture> futures = new LinkedHashMap<Integer, ChannelFuture>(this.size());
        for (final Channel c : this.serverChannels.values()) {
            futures.put(c.getId(), c.unbind().awaitUninterruptibly());
        }
        for (final Channel c : this.nonServerChannels.values()) {
            futures.put(c.getId(), c.unbind());
        }
        return new DefaultChannelGroupFuture(this, futures);
    }
    
    public ChannelGroupFuture write(final Object message) {
        final Map<Integer, ChannelFuture> futures = new LinkedHashMap<Integer, ChannelFuture>(this.size());
        if (message instanceof ChannelBuffer) {
            final ChannelBuffer buf = (ChannelBuffer)message;
            for (final Channel c : this.nonServerChannels.values()) {
                futures.put(c.getId(), c.write(buf.duplicate()));
            }
        }
        else {
            for (final Channel c2 : this.nonServerChannels.values()) {
                futures.put(c2.getId(), c2.write(message));
            }
        }
        return new DefaultChannelGroupFuture(this, futures);
    }
    
    public ChannelGroupFuture write(final Object message, final SocketAddress remoteAddress) {
        final Map<Integer, ChannelFuture> futures = new LinkedHashMap<Integer, ChannelFuture>(this.size());
        if (message instanceof ChannelBuffer) {
            final ChannelBuffer buf = (ChannelBuffer)message;
            for (final Channel c : this.nonServerChannels.values()) {
                futures.put(c.getId(), c.write(buf.duplicate(), remoteAddress));
            }
        }
        else {
            for (final Channel c2 : this.nonServerChannels.values()) {
                futures.put(c2.getId(), c2.write(message, remoteAddress));
            }
        }
        return new DefaultChannelGroupFuture(this, futures);
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o;
    }
    
    public int compareTo(final ChannelGroup o) {
        final int v = this.getName().compareTo(o.getName());
        if (v != 0) {
            return v;
        }
        return System.identityHashCode(this) - System.identityHashCode(o);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(name: " + this.getName() + ", size: " + this.size() + ')';
    }
    
    static {
        nextId = new AtomicInteger();
    }
}
