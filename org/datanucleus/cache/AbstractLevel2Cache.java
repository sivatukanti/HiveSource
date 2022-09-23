// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import org.datanucleus.ClassConstants;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.PersistenceConfiguration;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;

public abstract class AbstractLevel2Cache implements Level2Cache
{
    protected static final Localiser LOCALISER;
    protected NucleusContext nucleusCtx;
    protected int maxSize;
    protected boolean clearAtClose;
    protected long timeout;
    protected String cacheName;
    
    public AbstractLevel2Cache(final NucleusContext nucleusCtx) {
        this.maxSize = -1;
        this.clearAtClose = true;
        this.timeout = -1L;
        this.nucleusCtx = nucleusCtx;
        final PersistenceConfiguration conf = nucleusCtx.getPersistenceConfiguration();
        this.maxSize = conf.getIntProperty("datanucleus.cache.level2.maxSize");
        this.clearAtClose = conf.getBooleanProperty("datanucleus.cache.level2.clearAtClose", true);
        if (conf.hasProperty("datanucleus.cache.level2.timeout")) {
            this.timeout = conf.getIntProperty("datanucleus.cache.level2.timeout");
        }
        this.cacheName = conf.getStringProperty("datanucleus.cache.level2.cacheName");
        if (this.cacheName == null) {
            NucleusLogger.CACHE.warn("No 'datanucleus.cache.level2.cacheName' specified so using name of 'dataNucleus'");
            this.cacheName = "dataNucleus";
        }
    }
    
    @Override
    public Map<Object, CachedPC> getAll(final Collection oids) {
        if (oids == null) {
            return null;
        }
        final Map<Object, CachedPC> objs = new HashMap<Object, CachedPC>();
        for (final Object id : oids) {
            final CachedPC value = this.get(id);
            objs.put(id, value);
        }
        return objs;
    }
    
    @Override
    public void putAll(final Map<Object, CachedPC> objs) {
        if (objs == null) {
            return;
        }
        for (final Map.Entry<Object, CachedPC> entry : objs.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public boolean isEmpty() {
        return this.getSize() == 0;
    }
    
    @Override
    public int getNumberOfPinnedObjects() {
        throw new UnsupportedOperationException("getNumberOfPinnedObjects() method not supported by this plugin");
    }
    
    @Override
    public int getNumberOfUnpinnedObjects() {
        throw new UnsupportedOperationException("getNumberOfUnpinnedObjects() method not supported by this plugin");
    }
    
    @Override
    public void pin(final Object arg0) {
        throw new UnsupportedOperationException("pin(Object) method not supported by this plugin");
    }
    
    @Override
    public void pinAll(final Collection arg0) {
        throw new UnsupportedOperationException("pinAll(Collection) method not supported by this plugin");
    }
    
    @Override
    public void pinAll(final Object[] arg0) {
        throw new UnsupportedOperationException("pinAll(Object[]) method not supported by this plugin");
    }
    
    @Override
    public void pinAll(final Class arg0, final boolean arg1) {
        throw new UnsupportedOperationException("pinAll(Class,boolean) method not supported by this plugin");
    }
    
    @Override
    public void unpin(final Object arg0) {
        throw new UnsupportedOperationException("unpin(Object) method not supported by this plugin");
    }
    
    @Override
    public void unpinAll(final Collection arg0) {
        throw new UnsupportedOperationException("unpinAll(Collection) method not supported by this plugin");
    }
    
    @Override
    public void unpinAll(final Object[] arg0) {
        throw new UnsupportedOperationException("unpinAll(Object[]) method not supported by this plugin");
    }
    
    @Override
    public void unpinAll(final Class arg0, final boolean arg1) {
        throw new UnsupportedOperationException("unpinAll(Class,boolean) method not supported by this plugin");
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
