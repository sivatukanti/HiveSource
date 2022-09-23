// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.http;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.util.internal.ConversionUtil;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.jboss.netty.channel.socket.SocketChannelConfig;

public final class HttpTunnelingSocketChannelConfig implements SocketChannelConfig
{
    private final HttpTunnelingClientSocketChannel channel;
    private volatile String serverName;
    private volatile String serverPath;
    private volatile SSLContext sslContext;
    private volatile String[] enabledSslCipherSuites;
    private volatile String[] enabledSslProtocols;
    private volatile boolean enableSslSessionCreation;
    
    HttpTunnelingSocketChannelConfig(final HttpTunnelingClientSocketChannel channel) {
        this.serverPath = "/netty-tunnel";
        this.enableSslSessionCreation = true;
        this.channel = channel;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getServerPath() {
        return this.serverPath;
    }
    
    public void setServerPath(final String serverPath) {
        if (serverPath == null) {
            throw new NullPointerException("serverPath");
        }
        this.serverPath = serverPath;
    }
    
    public SSLContext getSslContext() {
        return this.sslContext;
    }
    
    public void setSslContext(final SSLContext sslContext) {
        this.sslContext = sslContext;
    }
    
    public String[] getEnabledSslCipherSuites() {
        final String[] suites = this.enabledSslCipherSuites;
        if (suites == null) {
            return null;
        }
        return suites.clone();
    }
    
    public void setEnabledSslCipherSuites(final String[] suites) {
        if (suites == null) {
            this.enabledSslCipherSuites = null;
        }
        else {
            this.enabledSslCipherSuites = suites.clone();
        }
    }
    
    public String[] getEnabledSslProtocols() {
        final String[] protocols = this.enabledSslProtocols;
        if (protocols == null) {
            return null;
        }
        return protocols.clone();
    }
    
    public void setEnabledSslProtocols(final String[] protocols) {
        if (protocols == null) {
            this.enabledSslProtocols = null;
        }
        else {
            this.enabledSslProtocols = protocols.clone();
        }
    }
    
    public boolean isEnableSslSessionCreation() {
        return this.enableSslSessionCreation;
    }
    
    public void setEnableSslSessionCreation(final boolean flag) {
        this.enableSslSessionCreation = flag;
    }
    
    public void setOptions(final Map<String, Object> options) {
        for (final Map.Entry<String, Object> e : options.entrySet()) {
            this.setOption(e.getKey(), e.getValue());
        }
    }
    
    public boolean setOption(final String key, final Object value) {
        if (this.channel.realChannel.getConfig().setOption(key, value)) {
            return true;
        }
        if ("serverName".equals(key)) {
            this.setServerName(String.valueOf(value));
        }
        else if ("serverPath".equals(key)) {
            this.setServerPath(String.valueOf(value));
        }
        else if ("sslContext".equals(key)) {
            this.setSslContext((SSLContext)value);
        }
        else if ("enabledSslCipherSuites".equals(key)) {
            this.setEnabledSslCipherSuites(ConversionUtil.toStringArray(value));
        }
        else if ("enabledSslProtocols".equals(key)) {
            this.setEnabledSslProtocols(ConversionUtil.toStringArray(value));
        }
        else {
            if (!"enableSslSessionCreation".equals(key)) {
                return false;
            }
            this.setEnableSslSessionCreation(ConversionUtil.toBoolean(value));
        }
        return true;
    }
    
    public int getReceiveBufferSize() {
        return this.channel.realChannel.getConfig().getReceiveBufferSize();
    }
    
    public int getSendBufferSize() {
        return this.channel.realChannel.getConfig().getSendBufferSize();
    }
    
    public int getSoLinger() {
        return this.channel.realChannel.getConfig().getSoLinger();
    }
    
    public int getTrafficClass() {
        return this.channel.realChannel.getConfig().getTrafficClass();
    }
    
    public boolean isKeepAlive() {
        return this.channel.realChannel.getConfig().isKeepAlive();
    }
    
    public boolean isReuseAddress() {
        return this.channel.realChannel.getConfig().isReuseAddress();
    }
    
    public boolean isTcpNoDelay() {
        return this.channel.realChannel.getConfig().isTcpNoDelay();
    }
    
    public void setKeepAlive(final boolean keepAlive) {
        this.channel.realChannel.getConfig().setKeepAlive(keepAlive);
    }
    
    public void setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        this.channel.realChannel.getConfig().setPerformancePreferences(connectionTime, latency, bandwidth);
    }
    
    public void setReceiveBufferSize(final int receiveBufferSize) {
        this.channel.realChannel.getConfig().setReceiveBufferSize(receiveBufferSize);
    }
    
    public void setReuseAddress(final boolean reuseAddress) {
        this.channel.realChannel.getConfig().setReuseAddress(reuseAddress);
    }
    
    public void setSendBufferSize(final int sendBufferSize) {
        this.channel.realChannel.getConfig().setSendBufferSize(sendBufferSize);
    }
    
    public void setSoLinger(final int soLinger) {
        this.channel.realChannel.getConfig().setSoLinger(soLinger);
    }
    
    public void setTcpNoDelay(final boolean tcpNoDelay) {
        this.channel.realChannel.getConfig().setTcpNoDelay(tcpNoDelay);
    }
    
    public void setTrafficClass(final int trafficClass) {
        this.channel.realChannel.getConfig().setTrafficClass(trafficClass);
    }
    
    public ChannelBufferFactory getBufferFactory() {
        return this.channel.realChannel.getConfig().getBufferFactory();
    }
    
    public int getConnectTimeoutMillis() {
        return this.channel.realChannel.getConfig().getConnectTimeoutMillis();
    }
    
    public ChannelPipelineFactory getPipelineFactory() {
        return this.channel.realChannel.getConfig().getPipelineFactory();
    }
    
    public void setBufferFactory(final ChannelBufferFactory bufferFactory) {
        this.channel.realChannel.getConfig().setBufferFactory(bufferFactory);
    }
    
    public void setConnectTimeoutMillis(final int connectTimeoutMillis) {
        this.channel.realChannel.getConfig().setConnectTimeoutMillis(connectTimeoutMillis);
    }
    
    public void setPipelineFactory(final ChannelPipelineFactory pipelineFactory) {
        this.channel.realChannel.getConfig().setPipelineFactory(pipelineFactory);
    }
}
