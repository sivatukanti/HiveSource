// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.util.internal.ConversionUtil;
import java.util.Iterator;
import java.util.Map;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.buffer.ChannelBufferFactory;

public class DefaultChannelConfig implements ChannelConfig
{
    private volatile ChannelBufferFactory bufferFactory;
    private volatile int connectTimeoutMillis;
    
    public DefaultChannelConfig() {
        this.bufferFactory = HeapChannelBufferFactory.getInstance();
        this.connectTimeoutMillis = 10000;
    }
    
    public void setOptions(final Map<String, Object> options) {
        for (final Map.Entry<String, Object> e : options.entrySet()) {
            this.setOption(e.getKey(), e.getValue());
        }
    }
    
    public boolean setOption(final String key, final Object value) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if ("pipelineFactory".equals(key)) {
            this.setPipelineFactory((ChannelPipelineFactory)value);
        }
        else if ("connectTimeoutMillis".equals(key)) {
            this.setConnectTimeoutMillis(ConversionUtil.toInt(value));
        }
        else {
            if (!"bufferFactory".equals(key)) {
                return false;
            }
            this.setBufferFactory((ChannelBufferFactory)value);
        }
        return true;
    }
    
    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }
    
    public ChannelBufferFactory getBufferFactory() {
        return this.bufferFactory;
    }
    
    public void setBufferFactory(final ChannelBufferFactory bufferFactory) {
        if (bufferFactory == null) {
            throw new NullPointerException("bufferFactory");
        }
        this.bufferFactory = bufferFactory;
    }
    
    public ChannelPipelineFactory getPipelineFactory() {
        return null;
    }
    
    public void setConnectTimeoutMillis(final int connectTimeoutMillis) {
        if (connectTimeoutMillis < 0) {
            throw new IllegalArgumentException("connectTimeoutMillis: " + connectTimeoutMillis);
        }
        this.connectTimeoutMillis = connectTimeoutMillis;
    }
    
    public void setPipelineFactory(final ChannelPipelineFactory pipelineFactory) {
    }
}
