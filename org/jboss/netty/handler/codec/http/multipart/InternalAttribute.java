// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.nio.charset.Charset;
import java.util.List;

public class InternalAttribute implements InterfaceHttpData
{
    protected final List<String> value;
    private final Charset charset;
    
    public InternalAttribute(final Charset charset) {
        this.value = new ArrayList<String>();
        this.charset = charset;
    }
    
    public HttpDataType getHttpDataType() {
        return HttpDataType.InternalAttribute;
    }
    
    public void addValue(final String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value.add(value);
    }
    
    public void addValue(final String value, final int rank) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value.add(rank, value);
    }
    
    public void setValue(final String value, final int rank) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value.set(rank, value);
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Attribute)) {
            return false;
        }
        final Attribute attribute = (Attribute)o;
        return this.getName().equalsIgnoreCase(attribute.getName());
    }
    
    public int compareTo(final InterfaceHttpData o) {
        if (!(o instanceof InternalAttribute)) {
            throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + o.getHttpDataType());
        }
        return this.compareTo((InternalAttribute)o);
    }
    
    public int compareTo(final InternalAttribute o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
    
    public int size() {
        int size = 0;
        for (final String elt : this.value) {
            try {
                size += elt.getBytes(this.charset.name()).length;
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return size;
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (final String elt : this.value) {
            result.append(elt);
        }
        return result.toString();
    }
    
    public ChannelBuffer toChannelBuffer() {
        final ChannelBuffer[] buffers = new ChannelBuffer[this.value.size()];
        for (int i = 0; i < buffers.length; ++i) {
            buffers[i] = ChannelBuffers.copiedBuffer(this.value.get(i), this.charset);
        }
        return ChannelBuffers.wrappedBuffer(buffers);
    }
    
    public String getName() {
        return "InternalAttribute";
    }
}
