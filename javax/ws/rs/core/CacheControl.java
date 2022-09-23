// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import java.util.HashMap;
import java.util.ArrayList;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Map;
import java.util.List;

public class CacheControl
{
    private boolean _private;
    private List<String> privateFields;
    private boolean noCache;
    private List<String> noCacheFields;
    private boolean noStore;
    private boolean noTransform;
    private boolean mustRevalidate;
    private boolean proxyRevalidate;
    private int maxAge;
    private int sMaxAge;
    private Map<String, String> cacheExtension;
    private static final RuntimeDelegate.HeaderDelegate<CacheControl> delegate;
    
    public CacheControl() {
        this.maxAge = -1;
        this.sMaxAge = -1;
        this._private = false;
        this.noCache = false;
        this.noStore = false;
        this.noTransform = true;
        this.mustRevalidate = false;
        this.proxyRevalidate = false;
    }
    
    public static CacheControl valueOf(final String value) throws IllegalArgumentException {
        return CacheControl.delegate.fromString(value);
    }
    
    public boolean isMustRevalidate() {
        return this.mustRevalidate;
    }
    
    public void setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }
    
    public boolean isProxyRevalidate() {
        return this.proxyRevalidate;
    }
    
    public void setProxyRevalidate(final boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }
    
    public int getSMaxAge() {
        return this.sMaxAge;
    }
    
    public void setSMaxAge(final int sMaxAge) {
        this.sMaxAge = sMaxAge;
    }
    
    public List<String> getNoCacheFields() {
        if (this.noCacheFields == null) {
            this.noCacheFields = new ArrayList<String>();
        }
        return this.noCacheFields;
    }
    
    public void setNoCache(final boolean noCache) {
        this.noCache = noCache;
    }
    
    public boolean isNoCache() {
        return this.noCache;
    }
    
    public boolean isPrivate() {
        return this._private;
    }
    
    public List<String> getPrivateFields() {
        if (this.privateFields == null) {
            this.privateFields = new ArrayList<String>();
        }
        return this.privateFields;
    }
    
    public void setPrivate(final boolean _private) {
        this._private = _private;
    }
    
    public boolean isNoTransform() {
        return this.noTransform;
    }
    
    public void setNoTransform(final boolean noTransform) {
        this.noTransform = noTransform;
    }
    
    public boolean isNoStore() {
        return this.noStore;
    }
    
    public void setNoStore(final boolean noStore) {
        this.noStore = noStore;
    }
    
    public Map<String, String> getCacheExtension() {
        if (this.cacheExtension == null) {
            this.cacheExtension = new HashMap<String, String>();
        }
        return this.cacheExtension;
    }
    
    @Override
    public String toString() {
        return CacheControl.delegate.toString(this);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this._private ? 1 : 0);
        hash = 41 * hash + ((this.privateFields != null) ? this.privateFields.hashCode() : 0);
        hash = 41 * hash + (this.noCache ? 1 : 0);
        hash = 41 * hash + ((this.noCacheFields != null) ? this.noCacheFields.hashCode() : 0);
        hash = 41 * hash + (this.noStore ? 1 : 0);
        hash = 41 * hash + (this.noTransform ? 1 : 0);
        hash = 41 * hash + (this.mustRevalidate ? 1 : 0);
        hash = 41 * hash + (this.proxyRevalidate ? 1 : 0);
        hash = 41 * hash + this.maxAge;
        hash = 41 * hash + this.sMaxAge;
        hash = 41 * hash + ((this.cacheExtension != null) ? this.cacheExtension.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CacheControl other = (CacheControl)obj;
        return this._private == other._private && (this.privateFields == other.privateFields || (this.privateFields != null && this.privateFields.equals(other.privateFields))) && this.noCache == other.noCache && (this.noCacheFields == other.noCacheFields || (this.noCacheFields != null && this.noCacheFields.equals(other.noCacheFields))) && this.noStore == other.noStore && this.noTransform == other.noTransform && this.mustRevalidate == other.mustRevalidate && this.proxyRevalidate == other.proxyRevalidate && this.maxAge == other.maxAge && this.sMaxAge == other.sMaxAge && (this.cacheExtension == other.cacheExtension || (this.cacheExtension != null && this.cacheExtension.equals(other.cacheExtension)));
    }
    
    static {
        delegate = RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class);
    }
}
