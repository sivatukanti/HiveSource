// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.util.Iterator;
import java.util.Map;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.buffer.ChannelBufferFactory;

public class DefaultServerChannelConfig implements ChannelConfig
{
    private volatile ChannelPipelineFactory pipelineFactory;
    private volatile ChannelBufferFactory bufferFactory;
    
    public DefaultServerChannelConfig() {
        this.bufferFactory = HeapChannelBufferFactory.getInstance();
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
        else {
            if (!"bufferFactory".equals(key)) {
                return false;
            }
            this.setBufferFactory((ChannelBufferFactory)value);
        }
        return true;
    }
    
    public ChannelPipelineFactory getPipelineFactory() {
        return this.pipelineFactory;
    }
    
    public void setPipelineFactory(final ChannelPipelineFactory pipelineFactory) {
        if (pipelineFactory == null) {
            throw new NullPointerException("pipelineFactory");
        }
        this.pipelineFactory = pipelineFactory;
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
    
    public int getConnectTimeoutMillis() {
        return 0;
    }
    
    public void setConnectTimeoutMillis(final int connectTimeoutMillis) {
    }
}
