// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.util.Collection;
import org.datanucleus.NucleusContext;

public class NullLevel2Cache extends AbstractLevel2Cache implements Level2Cache
{
    public NullLevel2Cache(final NucleusContext nucleusCtx) {
        super(nucleusCtx);
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void evict(final Object oid) {
    }
    
    @Override
    public void evictAll() {
    }
    
    @Override
    public void evictAll(final Class pcClass, final boolean subclasses) {
    }
    
    @Override
    public void evictAll(final Collection oids) {
    }
    
    @Override
    public void evictAll(final Object[] oids) {
    }
    
    @Override
    public void pin(final Object oid) {
    }
    
    @Override
    public void pinAll(final Class pcClass, final boolean subclasses) {
    }
    
    @Override
    public void pinAll(final Collection oids) {
    }
    
    @Override
    public void pinAll(final Object[] oids) {
    }
    
    @Override
    public void unpin(final Object oid) {
    }
    
    @Override
    public void unpinAll(final Class pcClass, final boolean subclasses) {
    }
    
    @Override
    public void unpinAll(final Collection oids) {
    }
    
    @Override
    public void unpinAll(final Object[] oids) {
    }
    
    @Override
    public boolean containsOid(final Object oid) {
        return false;
    }
    
    @Override
    public CachedPC get(final Object oid) {
        return null;
    }
    
    @Override
    public int getNumberOfPinnedObjects() {
        return 0;
    }
    
    @Override
    public int getNumberOfUnpinnedObjects() {
        return 0;
    }
    
    @Override
    public int getSize() {
        return 0;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public CachedPC put(final Object oid, final CachedPC pc) {
        return null;
    }
}
