// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import java.io.InputStream;
import java.io.File;
import java.nio.charset.Charset;
import org.jboss.netty.buffer.ChannelBuffer;
import java.io.IOException;

public class MixedAttribute implements Attribute
{
    private Attribute attribute;
    private final long limitSize;
    protected long maxSize;
    
    public MixedAttribute(final String name, final long limitSize) {
        this.maxSize = -1L;
        this.limitSize = limitSize;
        this.attribute = new MemoryAttribute(name);
    }
    
    public MixedAttribute(final String name, final String value, final long limitSize) {
        this.maxSize = -1L;
        this.limitSize = limitSize;
        if (value.length() > this.limitSize) {
            try {
                this.attribute = new DiskAttribute(name, value);
            }
            catch (IOException e) {
                try {
                    this.attribute = new MemoryAttribute(name, value);
                }
                catch (IOException e2) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        else {
            try {
                this.attribute = new MemoryAttribute(name, value);
            }
            catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    
    public void setMaxSize(final long maxSize) {
        this.maxSize = maxSize;
        this.attribute.setMaxSize(maxSize);
    }
    
    public void checkSize(final long newSize) throws IOException {
        if (this.maxSize >= 0L && newSize > this.maxSize) {
            throw new IOException("Size exceed allowed maximum capacity");
        }
    }
    
    public void addContent(final ChannelBuffer buffer, final boolean last) throws IOException {
        if (this.attribute instanceof MemoryAttribute) {
            this.checkSize(this.attribute.length() + buffer.readableBytes());
            if (this.attribute.length() + buffer.readableBytes() > this.limitSize) {
                final DiskAttribute diskAttribute = new DiskAttribute(this.attribute.getName());
                diskAttribute.setMaxSize(this.maxSize);
                if (((MemoryAttribute)this.attribute).getChannelBuffer() != null) {
                    diskAttribute.addContent(((MemoryAttribute)this.attribute).getChannelBuffer(), false);
                }
                this.attribute = diskAttribute;
            }
        }
        this.attribute.addContent(buffer, last);
    }
    
    public void delete() {
        this.attribute.delete();
    }
    
    public byte[] get() throws IOException {
        return this.attribute.get();
    }
    
    public ChannelBuffer getChannelBuffer() throws IOException {
        return this.attribute.getChannelBuffer();
    }
    
    public Charset getCharset() {
        return this.attribute.getCharset();
    }
    
    public String getString() throws IOException {
        return this.attribute.getString();
    }
    
    public String getString(final Charset encoding) throws IOException {
        return this.attribute.getString(encoding);
    }
    
    public boolean isCompleted() {
        return this.attribute.isCompleted();
    }
    
    public boolean isInMemory() {
        return this.attribute.isInMemory();
    }
    
    public long length() {
        return this.attribute.length();
    }
    
    public boolean renameTo(final File dest) throws IOException {
        return this.attribute.renameTo(dest);
    }
    
    public void setCharset(final Charset charset) {
        this.attribute.setCharset(charset);
    }
    
    public void setContent(final ChannelBuffer buffer) throws IOException {
        this.checkSize(buffer.readableBytes());
        if (buffer.readableBytes() > this.limitSize && this.attribute instanceof MemoryAttribute) {
            (this.attribute = new DiskAttribute(this.attribute.getName())).setMaxSize(this.maxSize);
        }
        this.attribute.setContent(buffer);
    }
    
    public void setContent(final File file) throws IOException {
        this.checkSize(file.length());
        if (file.length() > this.limitSize && this.attribute instanceof MemoryAttribute) {
            (this.attribute = new DiskAttribute(this.attribute.getName())).setMaxSize(this.maxSize);
        }
        this.attribute.setContent(file);
    }
    
    public void setContent(final InputStream inputStream) throws IOException {
        if (this.attribute instanceof MemoryAttribute) {
            (this.attribute = new DiskAttribute(this.attribute.getName())).setMaxSize(this.maxSize);
        }
        this.attribute.setContent(inputStream);
    }
    
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return this.attribute.getHttpDataType();
    }
    
    public String getName() {
        return this.attribute.getName();
    }
    
    public int compareTo(final InterfaceHttpData o) {
        return this.attribute.compareTo(o);
    }
    
    @Override
    public String toString() {
        return "Mixed: " + this.attribute.toString();
    }
    
    public String getValue() throws IOException {
        return this.attribute.getValue();
    }
    
    public void setValue(final String value) throws IOException {
        if (value != null) {
            this.checkSize(value.getBytes().length);
        }
        this.attribute.setValue(value);
    }
    
    public ChannelBuffer getChunk(final int length) throws IOException {
        return this.attribute.getChunk(length);
    }
    
    public File getFile() throws IOException {
        return this.attribute.getFile();
    }
}
