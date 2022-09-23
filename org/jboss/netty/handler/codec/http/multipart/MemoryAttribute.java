// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import java.io.IOException;
import org.jboss.netty.handler.codec.http.HttpConstants;

public class MemoryAttribute extends AbstractMemoryHttpData implements Attribute
{
    public MemoryAttribute(final String name) {
        super(name, HttpConstants.DEFAULT_CHARSET, 0L);
    }
    
    public MemoryAttribute(final String name, final String value) throws IOException {
        super(name, HttpConstants.DEFAULT_CHARSET, 0L);
        this.setValue(value);
    }
    
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.Attribute;
    }
    
    public String getValue() {
        return this.getChannelBuffer().toString(this.charset);
    }
    
    public void setValue(final String value) throws IOException {
        if (value == null) {
            throw new NullPointerException("value");
        }
        final byte[] bytes = value.getBytes(this.charset.name());
        this.checkSize(bytes.length);
        final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(bytes);
        if (this.definedSize > 0L) {
            this.definedSize = buffer.readableBytes();
        }
        this.setContent(buffer);
    }
    
    @Override
    public void addContent(final ChannelBuffer buffer, final boolean last) throws IOException {
        final int localsize = buffer.readableBytes();
        this.checkSize(this.size + localsize);
        if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
            this.definedSize = this.size + localsize;
        }
        super.addContent(buffer, last);
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
    
    public int compareTo(final InterfaceHttpData other) {
        if (!(other instanceof Attribute)) {
            throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + other.getHttpDataType());
        }
        return this.compareTo((Attribute)other);
    }
    
    public int compareTo(final Attribute o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
    
    @Override
    public String toString() {
        return this.getName() + '=' + this.getValue();
    }
}
