// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import java.io.IOException;
import org.jboss.netty.handler.codec.http.HttpConstants;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public abstract class AbstractHttpData implements HttpData
{
    private static final Pattern STRIP_PATTERN;
    private static final Pattern REPLACE_PATTERN;
    protected final String name;
    protected long definedSize;
    protected long size;
    protected Charset charset;
    protected boolean completed;
    protected long maxSize;
    
    protected AbstractHttpData(String name, final Charset charset, final long size) {
        this.charset = HttpConstants.DEFAULT_CHARSET;
        this.maxSize = -1L;
        if (name == null) {
            throw new NullPointerException("name");
        }
        name = AbstractHttpData.REPLACE_PATTERN.matcher(name).replaceAll(" ");
        name = AbstractHttpData.STRIP_PATTERN.matcher(name).replaceAll("");
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }
        this.name = name;
        if (charset != null) {
            this.setCharset(charset);
        }
        this.definedSize = size;
    }
    
    public void setMaxSize(final long maxSize) {
        this.maxSize = maxSize;
    }
    
    public void checkSize(final long newSize) throws IOException {
        if (this.maxSize >= 0L && newSize > this.maxSize) {
            throw new IOException("Size exceed allowed maximum capacity");
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isCompleted() {
        return this.completed;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public void setCharset(final Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }
    
    public long length() {
        return this.size;
    }
    
    static {
        STRIP_PATTERN = Pattern.compile("(?:^\\s+|\\s+$|\\n)");
        REPLACE_PATTERN = Pattern.compile("[\\r\\t]");
    }
}
