// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.http.GzipHttpContent;
import java.nio.channels.ReadableByteChannel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.eclipse.jetty.http.DateGenerator;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.HttpHeader;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.ByteBuffer;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;
import org.eclipse.jetty.http.ResourceHttpContent;
import org.eclipse.jetty.util.resource.Resource;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.ResourceFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.http.HttpContent;

public class ResourceCache implements HttpContent.Factory
{
    private static final Logger LOG;
    private final ConcurrentMap<String, CachedHttpContent> _cache;
    private final AtomicInteger _cachedSize;
    private final AtomicInteger _cachedFiles;
    private final ResourceFactory _factory;
    private final ResourceCache _parent;
    private final MimeTypes _mimeTypes;
    private final boolean _etags;
    private final boolean _gzip;
    private final boolean _useFileMappedBuffer;
    private int _maxCachedFileSize;
    private int _maxCachedFiles;
    private int _maxCacheSize;
    
    public ResourceCache(final ResourceCache parent, final ResourceFactory factory, final MimeTypes mimeTypes, final boolean useFileMappedBuffer, final boolean etags, final boolean gzip) {
        this._maxCachedFileSize = 134217728;
        this._maxCachedFiles = 2048;
        this._maxCacheSize = 268435456;
        this._factory = factory;
        this._cache = new ConcurrentHashMap<String, CachedHttpContent>();
        this._cachedSize = new AtomicInteger();
        this._cachedFiles = new AtomicInteger();
        this._mimeTypes = mimeTypes;
        this._parent = parent;
        this._useFileMappedBuffer = useFileMappedBuffer;
        this._etags = etags;
        this._gzip = gzip;
    }
    
    public int getCachedSize() {
        return this._cachedSize.get();
    }
    
    public int getCachedFiles() {
        return this._cachedFiles.get();
    }
    
    public int getMaxCachedFileSize() {
        return this._maxCachedFileSize;
    }
    
    public void setMaxCachedFileSize(final int maxCachedFileSize) {
        this._maxCachedFileSize = maxCachedFileSize;
        this.shrinkCache();
    }
    
    public int getMaxCacheSize() {
        return this._maxCacheSize;
    }
    
    public void setMaxCacheSize(final int maxCacheSize) {
        this._maxCacheSize = maxCacheSize;
        this.shrinkCache();
    }
    
    public int getMaxCachedFiles() {
        return this._maxCachedFiles;
    }
    
    public void setMaxCachedFiles(final int maxCachedFiles) {
        this._maxCachedFiles = maxCachedFiles;
        this.shrinkCache();
    }
    
    public boolean isUseFileMappedBuffer() {
        return this._useFileMappedBuffer;
    }
    
    public void flushCache() {
        if (this._cache != null) {
            while (this._cache.size() > 0) {
                for (final String path : this._cache.keySet()) {
                    final CachedHttpContent content = this._cache.remove(path);
                    if (content != null) {
                        content.invalidate();
                    }
                }
            }
        }
    }
    
    @Deprecated
    public HttpContent lookup(final String pathInContext) throws IOException {
        return this.getContent(pathInContext, this._maxCachedFileSize);
    }
    
    @Override
    public HttpContent getContent(final String pathInContext, final int maxBufferSize) throws IOException {
        final CachedHttpContent content = this._cache.get(pathInContext);
        if (content != null && content.isValid()) {
            return content;
        }
        final Resource resource = this._factory.getResource(pathInContext);
        final HttpContent loaded = this.load(pathInContext, resource, maxBufferSize);
        if (loaded != null) {
            return loaded;
        }
        if (this._parent != null) {
            final HttpContent httpContent = this._parent.getContent(pathInContext, maxBufferSize);
            if (httpContent != null) {
                return httpContent;
            }
        }
        return null;
    }
    
    protected boolean isCacheable(final Resource resource) {
        if (this._maxCachedFiles <= 0) {
            return false;
        }
        final long len = resource.length();
        return len > 0L && (this._useFileMappedBuffer || (len < this._maxCachedFileSize && len < this._maxCacheSize));
    }
    
    private HttpContent load(final String pathInContext, final Resource resource, final int maxBufferSize) throws IOException {
        if (resource == null || !resource.exists()) {
            return null;
        }
        if (resource.isDirectory()) {
            return new ResourceHttpContent(resource, this._mimeTypes.getMimeByExtension(resource.toString()), this.getMaxCachedFileSize());
        }
        if (this.isCacheable(resource)) {
            CachedHttpContent content = null;
            if (this._gzip) {
                final String pathInContextGz = pathInContext + ".gz";
                CachedHttpContent contentGz = this._cache.get(pathInContextGz);
                if (contentGz == null || !contentGz.isValid()) {
                    contentGz = null;
                    final Resource resourceGz = this._factory.getResource(pathInContextGz);
                    if (resourceGz.exists() && resourceGz.lastModified() >= resource.lastModified() && resourceGz.length() < resource.length()) {
                        contentGz = new CachedHttpContent(pathInContextGz, resourceGz, null);
                        final CachedHttpContent added = this._cache.putIfAbsent(pathInContextGz, contentGz);
                        if (added != null) {
                            contentGz.invalidate();
                            contentGz = added;
                        }
                    }
                }
                content = new CachedHttpContent(pathInContext, resource, contentGz);
            }
            else {
                content = new CachedHttpContent(pathInContext, resource, null);
            }
            final CachedHttpContent added2 = this._cache.putIfAbsent(pathInContext, content);
            if (added2 != null) {
                content.invalidate();
                content = added2;
            }
            return content;
        }
        final String mt = this._mimeTypes.getMimeByExtension(pathInContext);
        if (this._gzip) {
            final String pathInContextGz = pathInContext + ".gz";
            final CachedHttpContent contentGz = this._cache.get(pathInContextGz);
            if (contentGz != null && contentGz.isValid() && contentGz.getResource().lastModified() >= resource.lastModified()) {
                return new ResourceHttpContent(resource, mt, maxBufferSize, contentGz);
            }
            final Resource resourceGz = this._factory.getResource(pathInContextGz);
            if (resourceGz.exists() && resourceGz.lastModified() >= resource.lastModified() && resourceGz.length() < resource.length()) {
                return new ResourceHttpContent(resource, mt, maxBufferSize, new ResourceHttpContent(resourceGz, this._mimeTypes.getMimeByExtension(pathInContextGz), maxBufferSize));
            }
        }
        return new ResourceHttpContent(resource, mt, maxBufferSize);
    }
    
    private void shrinkCache() {
        while (this._cache.size() > 0 && (this._cachedFiles.get() > this._maxCachedFiles || this._cachedSize.get() > this._maxCacheSize)) {
            final SortedSet<CachedHttpContent> sorted = new TreeSet<CachedHttpContent>(new Comparator<CachedHttpContent>() {
                @Override
                public int compare(final CachedHttpContent c1, final CachedHttpContent c2) {
                    if (c1._lastAccessed < c2._lastAccessed) {
                        return -1;
                    }
                    if (c1._lastAccessed > c2._lastAccessed) {
                        return 1;
                    }
                    if (c1._contentLengthValue < c2._contentLengthValue) {
                        return -1;
                    }
                    return c1._key.compareTo(c2._key);
                }
            });
            for (final CachedHttpContent content : this._cache.values()) {
                sorted.add(content);
            }
            for (final CachedHttpContent content : sorted) {
                if (this._cachedFiles.get() <= this._maxCachedFiles && this._cachedSize.get() <= this._maxCacheSize) {
                    break;
                }
                if (content != this._cache.remove(content.getKey())) {
                    continue;
                }
                content.invalidate();
            }
        }
    }
    
    protected ByteBuffer getIndirectBuffer(final Resource resource) {
        try {
            return BufferUtil.toBuffer(resource, true);
        }
        catch (IOException | IllegalArgumentException ex2) {
            final Exception ex;
            final Exception e = ex;
            ResourceCache.LOG.warn(e);
            return null;
        }
    }
    
    protected ByteBuffer getMappedBuffer(final Resource resource) {
        try {
            if (this._useFileMappedBuffer && resource.getFile() != null && resource.length() < 2147483647L) {
                return BufferUtil.toMappedBuffer(resource.getFile());
            }
        }
        catch (IOException | IllegalArgumentException ex2) {
            final Exception ex;
            final Exception e = ex;
            ResourceCache.LOG.warn(e);
        }
        return null;
    }
    
    protected ByteBuffer getDirectBuffer(final Resource resource) {
        try {
            return BufferUtil.toBuffer(resource, true);
        }
        catch (IOException | IllegalArgumentException ex2) {
            final Exception ex;
            final Exception e = ex;
            ResourceCache.LOG.warn(e);
            return null;
        }
    }
    
    @Override
    public String toString() {
        return "ResourceCache[" + this._parent + "," + this._factory + "]@" + this.hashCode();
    }
    
    static {
        LOG = Log.getLogger(ResourceCache.class);
    }
    
    public class CachedHttpContent implements HttpContent
    {
        final String _key;
        final Resource _resource;
        final int _contentLengthValue;
        final HttpField _contentType;
        final String _characterEncoding;
        final MimeTypes.Type _mimeType;
        final HttpField _contentLength;
        final HttpField _lastModified;
        final long _lastModifiedValue;
        final HttpField _etag;
        final CachedGzipHttpContent _gzipped;
        volatile long _lastAccessed;
        AtomicReference<ByteBuffer> _indirectBuffer;
        AtomicReference<ByteBuffer> _directBuffer;
        
        CachedHttpContent(final String pathInContext, final Resource resource, final CachedHttpContent gzipped) {
            this._indirectBuffer = new AtomicReference<ByteBuffer>();
            this._directBuffer = new AtomicReference<ByteBuffer>();
            this._key = pathInContext;
            this._resource = resource;
            final String contentType = ResourceCache.this._mimeTypes.getMimeByExtension(this._resource.toString());
            this._contentType = ((contentType == null) ? null : new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, contentType));
            this._characterEncoding = ((this._contentType == null) ? null : MimeTypes.getCharsetFromContentType(contentType));
            this._mimeType = ((this._contentType == null) ? null : MimeTypes.CACHE.get(MimeTypes.getContentTypeWithoutCharset(contentType)));
            final boolean exists = resource.exists();
            this._lastModifiedValue = (exists ? resource.lastModified() : -1L);
            this._lastModified = ((this._lastModifiedValue == -1L) ? null : new PreEncodedHttpField(HttpHeader.LAST_MODIFIED, DateGenerator.formatDate(this._lastModifiedValue)));
            this._contentLengthValue = (exists ? ((int)resource.length()) : 0);
            this._contentLength = new PreEncodedHttpField(HttpHeader.CONTENT_LENGTH, Long.toString(this._contentLengthValue));
            if (ResourceCache.this._cachedFiles.incrementAndGet() > ResourceCache.this._maxCachedFiles) {
                ResourceCache.this.shrinkCache();
            }
            this._lastAccessed = System.currentTimeMillis();
            this._etag = (ResourceCache.this._etags ? new PreEncodedHttpField(HttpHeader.ETAG, resource.getWeakETag()) : null);
            this._gzipped = ((gzipped == null) ? null : new CachedGzipHttpContent(this, gzipped));
        }
        
        public String getKey() {
            return this._key;
        }
        
        public boolean isCached() {
            return this._key != null;
        }
        
        public boolean isMiss() {
            return false;
        }
        
        @Override
        public Resource getResource() {
            return this._resource;
        }
        
        @Override
        public HttpField getETag() {
            return this._etag;
        }
        
        @Override
        public String getETagValue() {
            return this._etag.getValue();
        }
        
        boolean isValid() {
            if (this._lastModifiedValue == this._resource.lastModified() && this._contentLengthValue == this._resource.length()) {
                this._lastAccessed = System.currentTimeMillis();
                return true;
            }
            if (this == ResourceCache.this._cache.remove(this._key)) {
                this.invalidate();
            }
            return false;
        }
        
        protected void invalidate() {
            final ByteBuffer indirect = this._indirectBuffer.get();
            if (indirect != null && this._indirectBuffer.compareAndSet(indirect, null)) {
                ResourceCache.this._cachedSize.addAndGet(-BufferUtil.length(indirect));
            }
            final ByteBuffer direct = this._directBuffer.get();
            if (direct != null && !BufferUtil.isMappedBuffer(direct) && this._directBuffer.compareAndSet(direct, null)) {
                ResourceCache.this._cachedSize.addAndGet(-BufferUtil.length(direct));
            }
            ResourceCache.this._cachedFiles.decrementAndGet();
            this._resource.close();
        }
        
        @Override
        public HttpField getLastModified() {
            return this._lastModified;
        }
        
        @Override
        public String getLastModifiedValue() {
            return (this._lastModified == null) ? null : this._lastModified.getValue();
        }
        
        @Override
        public HttpField getContentType() {
            return this._contentType;
        }
        
        @Override
        public String getContentTypeValue() {
            return (this._contentType == null) ? null : this._contentType.getValue();
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
            return this._characterEncoding;
        }
        
        @Override
        public MimeTypes.Type getMimeType() {
            return this._mimeType;
        }
        
        @Override
        public void release() {
        }
        
        @Override
        public ByteBuffer getIndirectBuffer() {
            ByteBuffer buffer = this._indirectBuffer.get();
            if (buffer == null) {
                final ByteBuffer buffer2 = ResourceCache.this.getIndirectBuffer(this._resource);
                if (buffer2 == null) {
                    ResourceCache.LOG.warn("Could not load " + this, new Object[0]);
                }
                else if (this._indirectBuffer.compareAndSet(null, buffer2)) {
                    buffer = buffer2;
                    if (ResourceCache.this._cachedSize.addAndGet(BufferUtil.length(buffer)) > ResourceCache.this._maxCacheSize) {
                        ResourceCache.this.shrinkCache();
                    }
                }
                else {
                    buffer = this._indirectBuffer.get();
                }
            }
            if (buffer == null) {
                return null;
            }
            return buffer.slice();
        }
        
        @Override
        public ByteBuffer getDirectBuffer() {
            ByteBuffer buffer = this._directBuffer.get();
            if (buffer == null) {
                final ByteBuffer mapped = ResourceCache.this.getMappedBuffer(this._resource);
                final ByteBuffer direct = (mapped == null) ? ResourceCache.this.getDirectBuffer(this._resource) : mapped;
                if (direct == null) {
                    ResourceCache.LOG.warn("Could not load " + this, new Object[0]);
                }
                else if (this._directBuffer.compareAndSet(null, direct)) {
                    buffer = direct;
                    if (mapped == null && ResourceCache.this._cachedSize.addAndGet(BufferUtil.length(buffer)) > ResourceCache.this._maxCacheSize) {
                        ResourceCache.this.shrinkCache();
                    }
                }
                else {
                    buffer = this._directBuffer.get();
                }
            }
            if (buffer == null) {
                return null;
            }
            return buffer.asReadOnlyBuffer();
        }
        
        @Override
        public HttpField getContentLength() {
            return this._contentLength;
        }
        
        @Override
        public long getContentLengthValue() {
            return this._contentLengthValue;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            final ByteBuffer indirect = this.getIndirectBuffer();
            if (indirect != null && indirect.hasArray()) {
                return new ByteArrayInputStream(indirect.array(), indirect.arrayOffset() + indirect.position(), indirect.remaining());
            }
            return this._resource.getInputStream();
        }
        
        @Override
        public ReadableByteChannel getReadableByteChannel() throws IOException {
            return this._resource.getReadableByteChannel();
        }
        
        @Override
        public String toString() {
            return String.format("CachedContent@%x{r=%s,e=%b,lm=%s,ct=%s,gz=%b}", this.hashCode(), this._resource, this._resource.exists(), this._lastModified, this._contentType, this._gzipped != null);
        }
        
        @Override
        public HttpContent getGzipContent() {
            return (this._gzipped != null && this._gzipped.isValid()) ? this._gzipped : null;
        }
    }
    
    public class CachedGzipHttpContent extends GzipHttpContent
    {
        private final CachedHttpContent _content;
        private final CachedHttpContent _contentGz;
        private final HttpField _etag;
        
        CachedGzipHttpContent(final CachedHttpContent content, final CachedHttpContent contentGz) {
            super(content, contentGz);
            this._content = content;
            this._contentGz = contentGz;
            this._etag = (ResourceCache.this._etags ? new PreEncodedHttpField(HttpHeader.ETAG, this._content.getResource().getWeakETag("--gzip")) : null);
        }
        
        public boolean isValid() {
            return this._contentGz.isValid() && this._content.isValid() && this._content.getResource().lastModified() <= this._contentGz.getResource().lastModified();
        }
        
        @Override
        public HttpField getETag() {
            if (this._etag != null) {
                return this._etag;
            }
            return super.getETag();
        }
        
        @Override
        public String getETagValue() {
            if (this._etag != null) {
                return this._etag.getValue();
            }
            return super.getETagValue();
        }
        
        @Override
        public String toString() {
            return "Cached" + super.toString();
        }
    }
}
