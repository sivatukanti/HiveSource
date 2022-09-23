// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import java.io.IOException;
import org.jboss.netty.handler.codec.http.HttpConstants;

public class DiskAttribute extends AbstractDiskHttpData implements Attribute
{
    public static String baseDirectory;
    public static boolean deleteOnExitTemporaryFile;
    public static final String prefix = "Attr_";
    public static final String postfix = ".att";
    
    public DiskAttribute(final String name) {
        super(name, HttpConstants.DEFAULT_CHARSET, 0L);
    }
    
    public DiskAttribute(final String name, final String value) throws IOException {
        super(name, HttpConstants.DEFAULT_CHARSET, 0L);
        this.setValue(value);
    }
    
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.Attribute;
    }
    
    public String getValue() throws IOException {
        final byte[] bytes = this.get();
        return new String(bytes, this.charset.name());
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
    
    public int compareTo(final InterfaceHttpData o) {
        if (!(o instanceof Attribute)) {
            throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + o.getHttpDataType());
        }
        return this.compareTo((Attribute)o);
    }
    
    public int compareTo(final Attribute o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
    
    @Override
    public String toString() {
        try {
            return this.getName() + '=' + this.getValue();
        }
        catch (IOException e) {
            return this.getName() + "=IoException";
        }
    }
    
    @Override
    protected boolean deleteOnExit() {
        return DiskAttribute.deleteOnExitTemporaryFile;
    }
    
    @Override
    protected String getBaseDirectory() {
        return DiskAttribute.baseDirectory;
    }
    
    @Override
    protected String getDiskFilename() {
        return this.getName() + ".att";
    }
    
    @Override
    protected String getPostfix() {
        return ".att";
    }
    
    @Override
    protected String getPrefix() {
        return "Attr_";
    }
    
    static {
        DiskAttribute.deleteOnExitTemporaryFile = true;
    }
}
