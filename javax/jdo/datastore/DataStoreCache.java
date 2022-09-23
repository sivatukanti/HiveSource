// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.datastore;

import java.util.Collection;

public interface DataStoreCache
{
    void evict(final Object p0);
    
    void evictAll();
    
    void evictAll(final Object... p0);
    
    void evictAll(final Collection p0);
    
    @Deprecated
    void evictAll(final Class p0, final boolean p1);
    
    void evictAll(final boolean p0, final Class p1);
    
    void pin(final Object p0);
    
    void pinAll(final Collection p0);
    
    void pinAll(final Object... p0);
    
    @Deprecated
    void pinAll(final Class p0, final boolean p1);
    
    void pinAll(final boolean p0, final Class p1);
    
    void unpin(final Object p0);
    
    void unpinAll(final Collection p0);
    
    void unpinAll(final Object... p0);
    
    @Deprecated
    void unpinAll(final Class p0, final boolean p1);
    
    void unpinAll(final boolean p0, final Class p1);
    
    public static class EmptyDataStoreCache implements DataStoreCache
    {
        public void evict(final Object oid) {
        }
        
        public void evictAll() {
        }
        
        public void evictAll(final Object... oids) {
        }
        
        public void evictAll(final Collection oids) {
        }
        
        public void evictAll(final Class pcClass, final boolean subclasses) {
        }
        
        public void evictAll(final boolean subclasses, final Class pcClass) {
        }
        
        public void pin(final Object oid) {
        }
        
        public void pinAll(final Object... oids) {
        }
        
        public void pinAll(final Collection oids) {
        }
        
        public void pinAll(final Class pcClass, final boolean subclasses) {
        }
        
        public void pinAll(final boolean subclasses, final Class pcClass) {
        }
        
        public void unpin(final Object oid) {
        }
        
        public void unpinAll(final Object... oids) {
        }
        
        public void unpinAll(final Collection oids) {
        }
        
        public void unpinAll(final Class pcClass, final boolean subclasses) {
        }
        
        public void unpinAll(final boolean subclasses, final Class pcClass) {
        }
    }
}
