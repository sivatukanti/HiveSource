// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import java.net.SocketException;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.util.internal.ConversionUtil;
import java.net.ServerSocket;
import org.jboss.netty.channel.DefaultServerChannelConfig;

public class DefaultServerSocketChannelConfig extends DefaultServerChannelConfig implements ServerSocketChannelConfig
{
    private final ServerSocket socket;
    private volatile int backlog;
    
    public DefaultServerSocketChannelConfig(final ServerSocket socket) {
        if (socket == null) {
            throw new NullPointerException("socket");
        }
        this.socket = socket;
    }
    
    @Override
    public boolean setOption(final String key, final Object value) {
        if (super.setOption(key, value)) {
            return true;
        }
        if ("receiveBufferSize".equals(key)) {
            this.setReceiveBufferSize(ConversionUtil.toInt(value));
        }
        else if ("reuseAddress".equals(key)) {
            this.setReuseAddress(ConversionUtil.toBoolean(value));
        }
        else {
            if (!"backlog".equals(key)) {
                return false;
            }
            this.setBacklog(ConversionUtil.toInt(value));
        }
        return true;
    }
    
    public boolean isReuseAddress() {
        try {
            return this.socket.getReuseAddress();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setReuseAddress(final boolean reuseAddress) {
        try {
            this.socket.setReuseAddress(reuseAddress);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public int getReceiveBufferSize() {
        try {
            return this.socket.getReceiveBufferSize();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setReceiveBufferSize(final int receiveBufferSize) {
        try {
            this.socket.setReceiveBufferSize(receiveBufferSize);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        this.socket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }
    
    public int getBacklog() {
        return this.backlog;
    }
    
    public void setBacklog(final int backlog) {
        if (backlog < 0) {
            throw new IllegalArgumentException("backlog: " + backlog);
        }
        this.backlog = backlog;
    }
}
