// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.socket.DatagramChannelConfig;
import org.jboss.netty.channel.ChannelConfig;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import org.jboss.netty.channel.ChannelFuture;
import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.util.internal.DetectionUtil;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.InternetProtocolFamily;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import java.nio.channels.MembershipKey;
import java.util.List;
import java.net.InetAddress;
import java.util.Map;
import java.nio.channels.DatagramChannel;

public class NioDatagramChannel extends AbstractNioChannel<java.nio.channels.DatagramChannel> implements DatagramChannel
{
    private final NioDatagramChannelConfig config;
    private Map<InetAddress, List<MembershipKey>> memberships;
    
    NioDatagramChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final NioDatagramWorker worker, final InternetProtocolFamily family) {
        super(null, factory, pipeline, sink, worker, openNonBlockingChannel(family));
        this.config = new DefaultNioDatagramChannelConfig((java.nio.channels.DatagramChannel)this.channel);
        Channels.fireChannelOpen(this);
    }
    
    private static java.nio.channels.DatagramChannel openNonBlockingChannel(final InternetProtocolFamily family) {
        try {
            java.nio.channels.DatagramChannel channel = null;
            if (DetectionUtil.javaVersion() < 7 || family == null) {
                channel = java.nio.channels.DatagramChannel.open();
            }
            else {
                switch (family) {
                    case IPv4: {
                        channel = java.nio.channels.DatagramChannel.open(ProtocolFamilyConverter.convert(family));
                        break;
                    }
                    case IPv6: {
                        channel = java.nio.channels.DatagramChannel.open(ProtocolFamilyConverter.convert(family));
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
            }
            channel.configureBlocking(false);
            return channel;
        }
        catch (IOException e) {
            throw new ChannelException("Failed to open a DatagramChannel.", e);
        }
    }
    
    @Override
    public NioDatagramWorker getWorker() {
        return (NioDatagramWorker)super.getWorker();
    }
    
    public boolean isBound() {
        return this.isOpen() && ((java.nio.channels.DatagramChannel)this.channel).socket().isBound();
    }
    
    public boolean isConnected() {
        return ((java.nio.channels.DatagramChannel)this.channel).isConnected();
    }
    
    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
    
    @Override
    public NioDatagramChannelConfig getConfig() {
        return this.config;
    }
    
    java.nio.channels.DatagramChannel getDatagramChannel() {
        return (java.nio.channels.DatagramChannel)this.channel;
    }
    
    public ChannelFuture joinGroup(final InetAddress multicastAddress) {
        try {
            return this.joinGroup(multicastAddress, NetworkInterface.getByInetAddress(this.getLocalAddress().getAddress()), null);
        }
        catch (SocketException e) {
            return Channels.failedFuture(this, e);
        }
    }
    
    public ChannelFuture joinGroup(final InetSocketAddress multicastAddress, final NetworkInterface networkInterface) {
        return this.joinGroup(multicastAddress.getAddress(), networkInterface, null);
    }
    
    public ChannelFuture joinGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        if (multicastAddress == null) {
            throw new NullPointerException("multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException("networkInterface");
        }
        try {
            MembershipKey key;
            if (source == null) {
                key = ((java.nio.channels.DatagramChannel)this.channel).join(multicastAddress, networkInterface);
            }
            else {
                key = ((java.nio.channels.DatagramChannel)this.channel).join(multicastAddress, networkInterface, source);
            }
            synchronized (this) {
                if (this.memberships == null) {
                    this.memberships = new HashMap<InetAddress, List<MembershipKey>>();
                }
                List<MembershipKey> keys = this.memberships.get(multicastAddress);
                if (keys == null) {
                    keys = new ArrayList<MembershipKey>();
                    this.memberships.put(multicastAddress, keys);
                }
                keys.add(key);
            }
        }
        catch (Throwable e) {
            return Channels.failedFuture(this, e);
        }
        return Channels.succeededFuture(this);
    }
    
    public ChannelFuture leaveGroup(final InetAddress multicastAddress) {
        try {
            return this.leaveGroup(multicastAddress, NetworkInterface.getByInetAddress(this.getLocalAddress().getAddress()), null);
        }
        catch (SocketException e) {
            return Channels.failedFuture(this, e);
        }
    }
    
    public ChannelFuture leaveGroup(final InetSocketAddress multicastAddress, final NetworkInterface networkInterface) {
        return this.leaveGroup(multicastAddress.getAddress(), networkInterface, null);
    }
    
    public ChannelFuture leaveGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        if (multicastAddress == null) {
            throw new NullPointerException("multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException("networkInterface");
        }
        synchronized (this) {
            if (this.memberships != null) {
                final List<MembershipKey> keys = this.memberships.get(multicastAddress);
                if (keys != null) {
                    final Iterator<MembershipKey> keyIt = keys.iterator();
                    while (keyIt.hasNext()) {
                        final MembershipKey key = keyIt.next();
                        if (networkInterface.equals(key.networkInterface()) && ((source == null && key.sourceAddress() == null) || (source != null && source.equals(key.sourceAddress())))) {
                            key.drop();
                            keyIt.remove();
                        }
                    }
                    if (keys.isEmpty()) {
                        this.memberships.remove(multicastAddress);
                    }
                }
            }
        }
        return Channels.succeededFuture(this);
    }
    
    public ChannelFuture block(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress sourceToBlock) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        if (multicastAddress == null) {
            throw new NullPointerException("multicastAddress");
        }
        if (sourceToBlock == null) {
            throw new NullPointerException("sourceToBlock");
        }
        if (networkInterface == null) {
            throw new NullPointerException("networkInterface");
        }
        synchronized (this) {
            if (this.memberships != null) {
                final List<MembershipKey> keys = this.memberships.get(multicastAddress);
                for (final MembershipKey key : keys) {
                    if (networkInterface.equals(key.networkInterface())) {
                        try {
                            key.block(sourceToBlock);
                        }
                        catch (IOException e) {
                            return Channels.failedFuture(this, e);
                        }
                    }
                }
            }
        }
        return Channels.succeededFuture(this);
    }
    
    public ChannelFuture block(final InetAddress multicastAddress, final InetAddress sourceToBlock) {
        try {
            this.block(multicastAddress, NetworkInterface.getByInetAddress(this.getLocalAddress().getAddress()), sourceToBlock);
        }
        catch (SocketException e) {
            return Channels.failedFuture(this, e);
        }
        return Channels.succeededFuture(this);
    }
    
    @Override
    InetSocketAddress getLocalSocketAddress() throws Exception {
        return (InetSocketAddress)((java.nio.channels.DatagramChannel)this.channel).socket().getLocalSocketAddress();
    }
    
    @Override
    InetSocketAddress getRemoteSocketAddress() throws Exception {
        return (InetSocketAddress)((java.nio.channels.DatagramChannel)this.channel).socket().getRemoteSocketAddress();
    }
    
    @Override
    public ChannelFuture write(final Object message, final SocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.equals(this.getRemoteAddress())) {
            return super.write(message, null);
        }
        return super.write(message, remoteAddress);
    }
}
