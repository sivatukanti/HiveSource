// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.nio.channels.ReadableByteChannel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.resource.Resource;

public class GzipHttpContent implements HttpContent
{
    private final HttpContent _content;
    private final HttpContent _contentGz;
    public static final String ETAG_GZIP = "--gzip";
    public static final String ETAG_GZIP_QUOTE = "--gzip\"";
    public static final PreEncodedHttpField CONTENT_ENCODING_GZIP;
    
    public static String removeGzipFromETag(final String etag) {
        if (etag == null) {
            return null;
        }
        final int i = etag.indexOf("--gzip\"");
        if (i < 0) {
            return etag;
        }
        return etag.substring(0, i) + '\"';
    }
    
    public GzipHttpContent(final HttpContent content, final HttpContent contentGz) {
        this._content = content;
        this._contentGz = contentGz;
    }
    
    @Override
    public int hashCode() {
        return this._content.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this._content.equals(obj);
    }
    
    @Override
    public Resource getResource() {
        return this._content.getResource();
    }
    
    @Override
    public HttpField getETag() {
        return new HttpField(HttpHeader.ETAG, this.getETagValue());
    }
    
    @Override
    public String getETagValue() {
        return this._content.getResource().getWeakETag("--gzip");
    }
    
    @Override
    public HttpField getLastModified() {
        return this._content.getLastModified();
    }
    
    @Override
    public String getLastModifiedValue() {
        return this._content.getLastModifiedValue();
    }
    
    @Override
    public HttpField getContentType() {
        return this._content.getContentType();
    }
    
    @Override
    public String getContentTypeValue() {
        return this._content.getContentTypeValue();
    }
    
    @Override
    public HttpField getContentEncoding() {
        return GzipHttpContent.CONTENT_ENCODING_GZIP;
    }
    
    @Override
    public String getContentEncodingValue() {
        return GzipHttpContent.CONTENT_ENCODING_GZIP.getValue();
    }
    
    @Override
    public String getCharacterEncoding() {
        return this._content.getCharacterEncoding();
    }
    
    @Override
    public MimeTypes.Type getMimeType() {
        return this._content.getMimeType();
    }
    
    @Override
    public void release() {
        this._content.release();
    }
    
    @Override
    public ByteBuffer getIndirectBuffer() {
        return this._contentGz.getIndirectBuffer();
    }
    
    @Override
    public ByteBuffer getDirectBuffer() {
        return this._contentGz.getDirectBuffer();
    }
    
    @Override
    public HttpField getContentLength() {
        return this._contentGz.getContentLength();
    }
    
    @Override
    public long getContentLengthValue() {
        return this._contentGz.getContentLengthValue();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this._contentGz.getInputStream();
    }
    
    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return this._contentGz.getReadableByteChannel();
    }
    
    @Override
    public String toString() {
        return String.format("GzipHttpContent@%x{r=%s|%s,lm=%s|%s,ct=%s}", this.hashCode(), this._content.getResource(), this._contentGz.getResource(), this._content.getResource().lastModified(), this._contentGz.getResource().lastModified(), this.getContentType());
    }
    
    @Override
    public HttpContent getGzipContent() {
        return null;
    }
    
    static {
        CONTENT_ENCODING_GZIP = new PreEncodedHttpField(HttpHeader.CONTENT_ENCODING, "gzip");
    }
}
