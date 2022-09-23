// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.io.IOException;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.resource.Resource;

public class ResourceHttpContent implements HttpContent
{
    final Resource _resource;
    final String _contentType;
    final int _maxBuffer;
    HttpContent _gzip;
    String _etag;
    
    public ResourceHttpContent(final Resource resource, final String contentType) {
        this(resource, contentType, -1, null);
    }
    
    public ResourceHttpContent(final Resource resource, final String contentType, final int maxBuffer) {
        this(resource, contentType, maxBuffer, null);
    }
    
    public ResourceHttpContent(final Resource resource, final String contentType, final int maxBuffer, final HttpContent gzip) {
        this._resource = resource;
        this._contentType = contentType;
        this._maxBuffer = maxBuffer;
        this._gzip = gzip;
    }
    
    @Override
    public String getContentTypeValue() {
        return this._contentType;
    }
    
    @Override
    public HttpField getContentType() {
        return (this._contentType == null) ? null : new HttpField(HttpHeader.CONTENT_TYPE, this._contentType);
    }
    
    @Override
    public HttpField getContentEncoding() {
        return null;
    }
    
    @Override
    public String getContentEncodingValue() {
        return null;
    }
    
    @Override
    public String getCharacterEncoding() {
        return (this._contentType == null) ? null : MimeTypes.getCharsetFromContentType(this._contentType);
    }
    
    @Override
    public MimeTypes.Type getMimeType() {
        return (this._contentType == null) ? null : MimeTypes.CACHE.get(MimeTypes.getContentTypeWithoutCharset(this._contentType));
    }
    
    @Override
    public HttpField getLastModified() {
        final long lm = this._resource.lastModified();
        return (lm >= 0L) ? new HttpField(HttpHeader.LAST_MODIFIED, DateGenerator.formatDate(lm)) : null;
    }
    
    @Override
    public String getLastModifiedValue() {
        final long lm = this._resource.lastModified();
        return (lm >= 0L) ? DateGenerator.formatDate(lm) : null;
    }
    
    @Override
    public ByteBuffer getDirectBuffer() {
        if (this._resource.length() <= 0L || (this._maxBuffer > 0 && this._maxBuffer < this._resource.length())) {
            return null;
        }
        try {
            return BufferUtil.toBuffer(this._resource, true);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public HttpField getETag() {
        return new HttpField(HttpHeader.ETAG, this.getETagValue());
    }
    
    @Override
    public String getETagValue() {
        return this._resource.getWeakETag();
    }
    
    @Override
    public ByteBuffer getIndirectBuffer() {
        if (this._resource.length() <= 0L || (this._maxBuffer > 0 && this._maxBuffer < this._resource.length())) {
            return null;
        }
        try {
            return BufferUtil.toBuffer(this._resource, false);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public HttpField getContentLength() {
        final long l = this._resource.length();
        return (l == -1L) ? null : new HttpField.LongValueHttpField(HttpHeader.CONTENT_LENGTH, this._resource.length());
    }
    
    @Override
    public long getContentLengthValue() {
        return this._resource.length();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this._resource.getInputStream();
    }
    
    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return this._resource.getReadableByteChannel();
    }
    
    @Override
    public Resource getResource() {
        return this._resource;
    }
    
    @Override
    public void release() {
        this._resource.close();
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{r=%s,gz=%b}", this.getClass().getSimpleName(), this.hashCode(), this._resource, this._gzip != null);
    }
    
    @Override
    public HttpContent getGzipContent() {
        return (this._gzip == null) ? null : new GzipHttpContent(this, this._gzip);
    }
}
