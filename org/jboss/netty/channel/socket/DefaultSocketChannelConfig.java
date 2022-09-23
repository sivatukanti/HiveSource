// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import java.net.SocketException;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.util.internal.ConversionUtil;
import java.net.Socket;
import org.jboss.netty.channel.DefaultChannelConfig;

public class DefaultSocketChannelConfig extends DefaultChannelConfig implements SocketChannelConfig
{
    private final Socket socket;
    
    public DefaultSocketChannelConfig(final Socket socket) {
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
        else if ("sendBufferSize".equals(key)) {
            this.setSendBufferSize(ConversionUtil.toInt(value));
        }
        else if ("tcpNoDelay".equals(key)) {
            this.setTcpNoDelay(ConversionUtil.toBoolean(value));
        }
        else if ("keepAlive".equals(key)) {
            this.setKeepAlive(ConversionUtil.toBoolean(value));
        }
        else if ("reuseAddress".equals(key)) {
            this.setReuseAddress(ConversionUtil.toBoolean(value));
        }
        else if ("soLinger".equals(key)) {
            this.setSoLinger(ConversionUtil.toInt(value));
        }
        else {
            if (!"trafficClass".equals(key)) {
                return false;
            }
            this.setTrafficClass(ConversionUtil.toInt(value));
        }
        return true;
    }
    
    public int getReceiveBufferSize() {
        try {
            return this.socket.getReceiveBufferSize();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public int getSendBufferSize() {
        try {
            return this.socket.getSendBufferSize();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public int getSoLinger() {
        try {
            return this.socket.getSoLinger();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public int getTrafficClass() {
        try {
            return this.socket.getTrafficClass();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isKeepAlive() {
        try {
            return this.socket.getKeepAlive();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isReuseAddress() {
        try {
            return this.socket.getReuseAddress();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isTcpNoDelay() {
        try {
            return this.socket.getTcpNoDelay();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setKeepAlive(final boolean keepAlive) {
        try {
            this.socket.setKeepAlive(keepAlive);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        this.socket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }
    
    public void setReceiveBufferSize(final int receiveBufferSize) {
        try {
            this.socket.setReceiveBufferSize(receiveBufferSize);
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
    
    public void setSendBufferSize(final int sendBufferSize) {
        try {
            this.socket.setSendBufferSize(sendBufferSize);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setSoLinger(final int soLinger) {
        try {
            if (soLinger < 0) {
                this.socket.setSoLinger(false, 0);
            }
            else {
                this.socket.setSoLinger(true, soLinger);
            }
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setTcpNoDelay(final boolean tcpNoDelay) {
        try {
            this.socket.setTcpNoDelay(tcpNoDelay);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setTrafficClass(final int trafficClass) {
        try {
            this.socket.setTrafficClass(trafficClass);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
}
