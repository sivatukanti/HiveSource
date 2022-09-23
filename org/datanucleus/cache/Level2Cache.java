// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.util.Map;
import java.util.Collection;
import java.io.Serializable;

public interface Level2Cache extends Serializable
{
    void close();
    
    void evict(final Object p0);
    
    void evictAll();
    
    void evictAll(final Object[] p0);
    
    void evictAll(final Collection p0);
    
    void evictAll(final Class p0, final boolean p1);
    
    void pin(final Object p0);
    
    void pinAll(final Collection p0);
    
    void pinAll(final Object[] p0);
    
    void pinAll(final Class p0, final boolean p1);
    
    void unpin(final Object p0);
    
    void unpinAll(final Collection p0);
    
    void unpinAll(final Object[] p0);
    
    void unpinAll(final Class p0, final boolean p1);
    
    int getNumberOfPinnedObjects();
    
    int getNumberOfUnpinnedObjects();
    
    int getSize();
    
    CachedPC get(final Object p0);
    
    Map<Object, CachedPC> getAll(final Collection p0);
    
    CachedPC put(final Object p0, final CachedPC p1);
    
    void putAll(final Map<Object, CachedPC> p0);
    
    boolean isEmpty();
    
    boolean containsOid(final Object p0);
    
    public static class PinnedClass
    {
        Class cls;
        boolean subclasses;
        
        public PinnedClass(final Class cls, final boolean subclasses) {
            this.cls = cls;
            this.subclasses = subclasses;
        }
        
        @Override
        public int hashCode() {
            return this.cls.hashCode() ^ (this.subclasses ? 0 : 1);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof PinnedClass)) {
                return false;
            }
            final PinnedClass other = (PinnedClass)obj;
            return other.cls.getName().equals(this.cls.getName()) && other.subclasses == this.subclasses;
        }
    }
}
