// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.io.View;
import org.mortbay.io.Buffer;
import java.io.InputStream;
import org.mortbay.io.ByteArrayBuffer;
import java.util.HashMap;
import java.io.IOException;
import org.mortbay.resource.Resource;
import org.mortbay.resource.ResourceFactory;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.io.Serializable;
import org.mortbay.component.AbstractLifeCycle;

public class ResourceCache extends AbstractLifeCycle implements Serializable
{
    private int _maxCachedFileSize;
    private int _maxCachedFiles;
    private int _maxCacheSize;
    private MimeTypes _mimeTypes;
    protected transient Map _cache;
    protected transient int _cachedSize;
    protected transient int _cachedFiles;
    protected transient Content _mostRecentlyUsed;
    protected transient Content _leastRecentlyUsed;
    
    public ResourceCache(final MimeTypes mimeTypes) {
        this._maxCachedFileSize = 1048576;
        this._maxCachedFiles = 2048;
        this._maxCacheSize = 16777216;
        this._mimeTypes = mimeTypes;
    }
    
    public int getCachedSize() {
        return this._cachedSize;
    }
    
    public int getCachedFiles() {
        return this._cachedFiles;
    }
    
    public int getMaxCachedFileSize() {
        return this._maxCachedFileSize;
    }
    
    public void setMaxCachedFileSize(final int maxCachedFileSize) {
        this._maxCachedFileSize = maxCachedFileSize;
        this.flushCache();
    }
    
    public int getMaxCacheSize() {
        return this._maxCacheSize;
    }
    
    public void setMaxCacheSize(final int maxCacheSize) {
        this._maxCacheSize = maxCacheSize;
        this.flushCache();
    }
    
    public int getMaxCachedFiles() {
        return this._maxCachedFiles;
    }
    
    public void setMaxCachedFiles(final int maxCachedFiles) {
        this._maxCachedFiles = maxCachedFiles;
    }
    
    public void flushCache() {
        if (this._cache != null) {
            synchronized (this) {
                final ArrayList values = new ArrayList(this._cache.values());
                for (final Content content : values) {
                    content.invalidate();
                }
                this._cache.clear();
                this._cachedSize = 0;
                this._cachedFiles = 0;
                this._mostRecentlyUsed = null;
                this._leastRecentlyUsed = null;
            }
        }
    }
    
    public Content lookup(final String pathInContext, final ResourceFactory factory) throws IOException {
        Content content = null;
        synchronized (this._cache) {
            content = this._cache.get(pathInContext);
            if (content != null && content.isValid()) {
                return content;
            }
        }
        final Resource resource = factory.getResource(pathInContext);
        return this.load(pathInContext, resource);
    }
    
    public Content lookup(final String pathInContext, final Resource resource) throws IOException {
        Content content = null;
        synchronized (this._cache) {
            content = this._cache.get(pathInContext);
            if (content != null && content.isValid()) {
                return content;
            }
        }
        return this.load(pathInContext, resource);
    }
    
    private Content load(final String pathInContext, final Resource resource) throws IOException {
        Content content = null;
        if (resource != null && resource.exists() && !resource.isDirectory()) {
            final long len = resource.length();
            if (len > 0L && len < this._maxCachedFileSize && len < this._maxCacheSize) {
                content = new Content(resource);
                this.fill(content);
                synchronized (this._cache) {
                    final Content content2 = this._cache.get(pathInContext);
                    if (content2 != null) {
                        content.release();
                        return content2;
                    }
                    final int must_be_smaller_than = this._maxCacheSize - (int)len;
                    while (this._cachedSize > must_be_smaller_than || (this._maxCachedFiles > 0 && this._cachedFiles >= this._maxCachedFiles)) {
                        this._leastRecentlyUsed.invalidate();
                    }
                    content.cache(pathInContext);
                    return content;
                }
            }
        }
        return null;
    }
    
    public synchronized void doStart() throws Exception {
        this._cache = new HashMap();
        this._cachedSize = 0;
        this._cachedFiles = 0;
    }
    
    public void doStop() throws InterruptedException {
        this.flushCache();
    }
    
    protected void fill(final Content content) throws IOException {
        try {
            final InputStream in = content.getResource().getInputStream();
            final int len = (int)content.getResource().length();
            final Buffer buffer = new ByteArrayBuffer(len);
            buffer.readFrom(in, len);
            in.close();
            content.setBuffer(buffer);
        }
        finally {
            content.getResource().release();
        }
    }
    
    public class Content implements HttpContent
    {
        String _key;
        Resource _resource;
        long _lastModified;
        Content _prev;
        Content _next;
        Buffer _lastModifiedBytes;
        Buffer _contentType;
        Buffer _buffer;
        
        Content(final Resource resource) {
            this._resource = resource;
            this._next = this;
            this._prev = this;
            this._contentType = ResourceCache.this._mimeTypes.getMimeByExtension(this._resource.toString());
            this._lastModified = resource.lastModified();
        }
        
        void cache(final String pathInContext) {
            this._key = pathInContext;
            this._next = ResourceCache.this._mostRecentlyUsed;
            ResourceCache.this._mostRecentlyUsed = this;
            if (this._next != null) {
                this._next._prev = this;
            }
            this._prev = null;
            if (ResourceCache.this._leastRecentlyUsed == null) {
                ResourceCache.this._leastRecentlyUsed = this;
            }
            ResourceCache.this._cache.put(this._key, this);
            final ResourceCache this$0 = ResourceCache.this;
            this$0._cachedSize += this._buffer.length();
            final ResourceCache this$2 = ResourceCache.this;
            ++this$2._cachedFiles;
            if (this._lastModified != -1L) {
                this._lastModifiedBytes = new ByteArrayBuffer(HttpFields.formatDate(this._lastModified, false));
            }
        }
        
        public String getKey() {
            return this._key;
        }
        
        public boolean isCached() {
            return this._key != null;
        }
        
        public Resource getResource() {
            return this._resource;
        }
        
        boolean isValid() {
            if (this._lastModified == this._resource.lastModified()) {
                if (ResourceCache.this._mostRecentlyUsed != this) {
                    final Content tp = this._prev;
                    final Content tn = this._next;
                    this._next = ResourceCache.this._mostRecentlyUsed;
                    ResourceCache.this._mostRecentlyUsed = this;
                    if (this._next != null) {
                        this._next._prev = this;
                    }
                    this._prev = null;
                    if (tp != null) {
                        tp._next = tn;
                    }
                    if (tn != null) {
                        tn._prev = tp;
                    }
                    if (ResourceCache.this._leastRecentlyUsed == this && tp != null) {
                        ResourceCache.this._leastRecentlyUsed = tp;
                    }
                }
                return true;
            }
            this.invalidate();
            return false;
        }
        
        public void invalidate() {
            synchronized (this) {
                ResourceCache.this._cache.remove(this._key);
                this._key = null;
                ResourceCache.this._cachedSize -= this._buffer.length();
                final ResourceCache this$0 = ResourceCache.this;
                --this$0._cachedFiles;
                if (ResourceCache.this._mostRecentlyUsed == this) {
                    ResourceCache.this._mostRecentlyUsed = this._next;
                }
                else {
                    this._prev._next = this._next;
                }
                if (ResourceCache.this._leastRecentlyUsed == this) {
                    ResourceCache.this._leastRecentlyUsed = this._prev;
                }
                else {
                    this._next._prev = this._prev;
                }
                this._prev = null;
                this._next = null;
                if (this._resource != null) {
                    this._resource.release();
                }
                this._resource = null;
            }
        }
        
        public Buffer getLastModified() {
            return this._lastModifiedBytes;
        }
        
        public Buffer getContentType() {
            return this._contentType;
        }
        
        public void setContentType(final Buffer type) {
            this._contentType = type;
        }
        
        public void release() {
        }
        
        public Buffer getBuffer() {
            if (this._buffer == null) {
                return null;
            }
            return new View(this._buffer);
        }
        
        public void setBuffer(final Buffer buffer) {
            this._buffer = buffer;
        }
        
        public long getContentLength() {
            if (this._buffer != null) {
                return this._buffer.length();
            }
            if (this._resource != null) {
                return this._resource.length();
            }
            return -1L;
        }
        
        public InputStream getInputStream() throws IOException {
            return this._resource.getInputStream();
        }
    }
}
