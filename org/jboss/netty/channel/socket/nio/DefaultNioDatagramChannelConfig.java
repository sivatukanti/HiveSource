// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.net.SocketException;
import java.util.Enumeration;
import java.net.InetAddress;
import java.io.IOException;
import org.jboss.netty.channel.ChannelException;
import java.net.StandardSocketOptions;
import org.jboss.netty.util.internal.DetectionUtil;
import java.net.NetworkInterface;
import org.jboss.netty.util.internal.ConversionUtil;
import java.util.Map;
import java.nio.channels.DatagramChannel;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.socket.DefaultDatagramChannelConfig;

class DefaultNioDatagramChannelConfig extends DefaultDatagramChannelConfig implements NioDatagramChannelConfig
{
    private static final InternalLogger logger;
    private volatile int writeBufferHighWaterMark;
    private volatile int writeBufferLowWaterMark;
    private volatile int writeSpinCount;
    private final DatagramChannel channel;
    
    DefaultNioDatagramChannelConfig(final DatagramChannel channel) {
        super(channel.socket());
        this.writeBufferHighWaterMark = 65536;
        this.writeBufferLowWaterMark = 32768;
        this.writeSpinCount = 16;
        this.channel = channel;
    }
    
    @Override
    public void setOptions(final Map<String, Object> options) {
        super.setOptions(options);
        if (this.getWriteBufferHighWaterMark() < this.getWriteBufferLowWaterMark()) {
            this.setWriteBufferLowWaterMark0(this.getWriteBufferHighWaterMark() >>> 1);
            if (DefaultNioDatagramChannelConfig.logger.isWarnEnabled()) {
                DefaultNioDatagramChannelConfig.logger.warn("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark; setting to the half of the writeBufferHighWaterMark.");
            }
        }
    }
    
    @Override
    public boolean setOption(final String key, final Object value) {
        if (super.setOption(key, value)) {
            return true;
        }
        if ("writeBufferHighWaterMark".equals(key)) {
            this.setWriteBufferHighWaterMark0(ConversionUtil.toInt(value));
        }
        else if ("writeBufferLowWaterMark".equals(key)) {
            this.setWriteBufferLowWaterMark0(ConversionUtil.toInt(value));
        }
        else {
            if (!"writeSpinCount".equals(key)) {
                return false;
            }
            this.setWriteSpinCount(ConversionUtil.toInt(value));
        }
        return true;
    }
    
    public int getWriteBufferHighWaterMark() {
        return this.writeBufferHighWaterMark;
    }
    
    public void setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        if (writeBufferHighWaterMark < this.getWriteBufferLowWaterMark()) {
            throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + this.getWriteBufferLowWaterMark() + "): " + writeBufferHighWaterMark);
        }
        this.setWriteBufferHighWaterMark0(writeBufferHighWaterMark);
    }
    
    private void setWriteBufferHighWaterMark0(final int writeBufferHighWaterMark) {
        if (writeBufferHighWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferHighWaterMark: " + writeBufferHighWaterMark);
        }
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
    }
    
    public int getWriteBufferLowWaterMark() {
        return this.writeBufferLowWaterMark;
    }
    
    public void setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        if (writeBufferLowWaterMark > this.getWriteBufferHighWaterMark()) {
            throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + this.getWriteBufferHighWaterMark() + "): " + writeBufferLowWaterMark);
        }
        this.setWriteBufferLowWaterMark0(writeBufferLowWaterMark);
    }
    
    private void setWriteBufferLowWaterMark0(final int writeBufferLowWaterMark) {
        if (writeBufferLowWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferLowWaterMark: " + writeBufferLowWaterMark);
        }
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
    }
    
    public int getWriteSpinCount() {
        return this.writeSpinCount;
    }
    
    public void setWriteSpinCount(final int writeSpinCount) {
        if (writeSpinCount <= 0) {
            throw new IllegalArgumentException("writeSpinCount must be a positive integer.");
        }
        this.writeSpinCount = writeSpinCount;
    }
    
    @Override
    public void setNetworkInterface(final NetworkInterface networkInterface) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            this.channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public NetworkInterface getNetworkInterface() {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            return this.channel.getOption(StandardSocketOptions.IP_MULTICAST_IF);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getTimeToLive() {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            return this.channel.getOption(StandardSocketOptions.IP_MULTICAST_TTL);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public void setTimeToLive(final int ttl) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            this.channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, ttl);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public InetAddress getInterface() {
        final NetworkInterface inf = this.getNetworkInterface();
        if (inf == null) {
            return null;
        }
        final Enumeration<InetAddress> addresses = inf.getInetAddresses();
        if (addresses.hasMoreElements()) {
            return addresses.nextElement();
        }
        return null;
    }
    
    @Override
    public void setInterface(final InetAddress interfaceAddress) {
        try {
            this.setNetworkInterface(NetworkInterface.getByInetAddress(interfaceAddress));
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public boolean isLoopbackModeDisabled() {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            return this.channel.getOption(StandardSocketOptions.IP_MULTICAST_LOOP);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public void setLoopbackModeDisabled(final boolean loopbackModeDisabled) {
        if (DetectionUtil.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        }
        try {
            this.channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, loopbackModeDisabled);
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DefaultNioDatagramChannelConfig.class);
    }
}
