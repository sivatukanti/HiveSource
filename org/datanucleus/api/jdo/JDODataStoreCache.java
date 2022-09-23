// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.util.Collection;
import org.datanucleus.cache.Level2Cache;
import java.io.Serializable;
import javax.jdo.datastore.DataStoreCache;

public class JDODataStoreCache implements DataStoreCache, Serializable
{
    Level2Cache cache;
    
    public JDODataStoreCache(final Level2Cache cache) {
        this.cache = null;
        this.cache = cache;
    }
    
    public Level2Cache getLevel2Cache() {
        return this.cache;
    }
    
    public void evict(final Object oid) {
        this.cache.evict(oid);
    }
    
    public void evictAll() {
        this.cache.evictAll();
    }
    
    public void evictAll(final Object... oids) {
        this.cache.evictAll(oids);
    }
    
    public void evictAll(final Collection oids) {
        this.cache.evictAll(oids);
    }
    
    public void evictAll(final Class pcClass, final boolean subclasses) {
        this.cache.evictAll(pcClass, subclasses);
    }
    
    public void evictAll(final boolean subclasses, final Class pcClass) {
        this.cache.evictAll(pcClass, subclasses);
    }
    
    public void pin(final Object oid) {
        this.cache.pin(oid);
    }
    
    public void pinAll(final Collection oids) {
        this.cache.pinAll(oids);
    }
    
    public void pinAll(final Object... oids) {
        this.cache.pinAll(oids);
    }
    
    public void pinAll(final Class pcClass, final boolean subclasses) {
        this.cache.pinAll(pcClass, subclasses);
    }
    
    public void pinAll(final boolean subclasses, final Class pcClass) {
        this.cache.pinAll(pcClass, subclasses);
    }
    
    public void unpin(final Object oid) {
        this.cache.unpin(oid);
    }
    
    public void unpinAll(final Collection oids) {
        this.cache.unpinAll(oids);
    }
    
    public void unpinAll(final Object... oids) {
        this.cache.unpinAll(oids);
    }
    
    public void unpinAll(final Class pcClass, final boolean subclasses) {
        this.cache.unpinAll(pcClass, subclasses);
    }
    
    public void unpinAll(final boolean subclasses, final Class pcClass) {
        this.cache.unpinAll(pcClass, subclasses);
    }
}
