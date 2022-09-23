// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import org.datanucleus.ClassConstants;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.datanucleus.util.NucleusLogger;
import java.util.Iterator;
import java.util.HashSet;
import org.datanucleus.util.WeakValueMap;
import java.util.HashMap;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.ApiAdapter;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.util.Localiser;

public class WeakLevel2Cache implements Level2Cache
{
    private static Localiser LOCALISER;
    protected Collection<PinnedClass> pinnedClasses;
    protected Collection pinnedIds;
    protected Map pinnedCache;
    protected transient Map unpinnedCache;
    protected ApiAdapter apiAdapter;
    private int maxSize;
    
    protected WeakLevel2Cache() {
        this.maxSize = -1;
    }
    
    public WeakLevel2Cache(final NucleusContext nucleusCtx) {
        this.maxSize = -1;
        this.apiAdapter = nucleusCtx.getApiAdapter();
        this.pinnedCache = new HashMap();
        this.unpinnedCache = new WeakValueMap();
        this.maxSize = nucleusCtx.getPersistenceConfiguration().getIntProperty("datanucleus.cache.level2.maxSize");
    }
    
    @Override
    public void close() {
        this.evictAll();
        this.pinnedCache = null;
        this.unpinnedCache = null;
    }
    
    @Override
    public synchronized void evict(final Object oid) {
        if (oid == null) {
            return;
        }
        this.unpinnedCache.remove(oid);
        this.pinnedCache.remove(oid);
    }
    
    @Override
    public synchronized void evictAll() {
        this.unpinnedCache.clear();
        this.pinnedCache.clear();
    }
    
    @Override
    public synchronized void evictAll(final Class pcClass, final boolean subclasses) {
        if (pcClass == null) {
            return;
        }
        final Collection oidsToEvict = new HashSet();
        final Collection pinnedObjects = this.pinnedCache.entrySet();
        for (final Map.Entry entry : pinnedObjects) {
            final CachedPC pc = entry.getValue();
            if (pcClass.getName().equals(pc.getObjectClass().getName()) || (subclasses && pcClass.isAssignableFrom(pc.getObjectClass()))) {
                oidsToEvict.add(entry.getKey());
            }
        }
        final Collection unpinnedObjects = this.unpinnedCache.entrySet();
        for (final Map.Entry entry2 : unpinnedObjects) {
            final CachedPC pc2 = entry2.getValue();
            if ((pc2 != null && pcClass.getName().equals(pc2.getObjectClass().getName())) || (subclasses && pcClass.isAssignableFrom(pc2.getObjectClass()))) {
                oidsToEvict.add(entry2.getKey());
            }
        }
        if (!oidsToEvict.isEmpty()) {
            this.evictAll(oidsToEvict);
        }
    }
    
    @Override
    public synchronized void evictAll(final Collection oids) {
        if (oids == null) {
            return;
        }
        final Iterator iter = oids.iterator();
        while (iter.hasNext()) {
            this.evict(iter.next());
        }
    }
    
    @Override
    public synchronized void evictAll(final Object[] oids) {
        if (oids == null) {
            return;
        }
        for (int i = 0; i < oids.length; ++i) {
            this.evict(oids[i]);
        }
    }
    
    @Override
    public synchronized void pin(final Object oid) {
        if (oid == null) {
            return;
        }
        if (this.pinnedIds == null) {
            this.pinnedIds = new HashSet();
        }
        else if (!this.pinnedIds.contains(oid)) {
            this.pinnedIds.add(oid);
        }
        final Object pc = this.unpinnedCache.get(oid);
        if (pc != null) {
            this.pinnedCache.put(oid, pc);
            this.unpinnedCache.remove(oid);
        }
    }
    
    @Override
    public synchronized void pinAll(final Class cls, final boolean subs) {
        if (cls == null) {
            return;
        }
        if (this.pinnedClasses == null) {
            this.pinnedClasses = new HashSet<PinnedClass>();
        }
        final PinnedClass pinnedCls = new PinnedClass(cls, subs);
        if (this.pinnedClasses.contains(pinnedCls)) {
            return;
        }
        this.pinnedClasses.add(pinnedCls);
        final Collection unpinnedObjects = this.unpinnedCache.values();
        for (final CachedPC obj : unpinnedObjects) {
            if ((subs && cls.isInstance(obj.getObjectClass())) || cls.getName().equals(obj.getObjectClass().getName())) {
                this.pin(obj);
            }
        }
    }
    
    @Override
    public synchronized void pinAll(final Collection oids) {
        if (oids == null) {
            return;
        }
        final Iterator iter = oids.iterator();
        while (iter.hasNext()) {
            this.pin(iter.next());
        }
    }
    
    @Override
    public synchronized void pinAll(final Object[] oids) {
        if (oids == null) {
            return;
        }
        for (int i = 0; i < oids.length; ++i) {
            this.pin(oids[i]);
        }
    }
    
    @Override
    public synchronized void unpin(final Object oid) {
        if (oid == null) {
            return;
        }
        final Object pc = this.pinnedCache.get(oid);
        if (pc != null) {
            this.unpinnedCache.put(oid, pc);
            this.pinnedCache.remove(oid);
        }
        if (this.pinnedIds != null && this.pinnedIds.contains(oid)) {
            this.pinnedIds.remove(oid);
        }
    }
    
    @Override
    public synchronized void unpinAll(final Class cls, final boolean subs) {
        if (cls == null) {
            return;
        }
        if (this.pinnedClasses != null) {
            final PinnedClass pinnedCls = new PinnedClass(cls, subs);
            this.pinnedClasses.remove(pinnedCls);
        }
        final Collection pinnedObjects = this.pinnedCache.values();
        for (final CachedPC obj : pinnedObjects) {
            if ((subs && cls.isInstance(obj.getObjectClass())) || cls.getName().equals(obj.getObjectClass().getName())) {
                this.unpin(obj);
            }
        }
    }
    
    @Override
    public synchronized void unpinAll(final Collection oids) {
        if (oids == null) {
            return;
        }
        final Iterator iter = oids.iterator();
        while (iter.hasNext()) {
            this.unpin(iter.next());
        }
    }
    
    @Override
    public synchronized void unpinAll(final Object[] oids) {
        if (oids == null) {
            return;
        }
        for (int i = 0; i < oids.length; ++i) {
            this.unpin(oids[i]);
        }
    }
    
    @Override
    public synchronized CachedPC get(final Object oid) {
        if (oid == null) {
            return null;
        }
        CachedPC pc = this.pinnedCache.get(oid);
        if (pc != null) {
            return pc;
        }
        pc = this.unpinnedCache.get(oid);
        return pc;
    }
    
    @Override
    public Map<Object, CachedPC> getAll(final Collection oids) {
        if (oids == null) {
            return null;
        }
        final Map<Object, CachedPC> objs = new HashMap<Object, CachedPC>();
        for (final Object oid : oids) {
            final CachedPC obj = this.get(oid);
            if (obj != null) {
                objs.put(oid, obj);
            }
        }
        return objs;
    }
    
    @Override
    public int getNumberOfPinnedObjects() {
        return this.pinnedCache.size();
    }
    
    @Override
    public int getNumberOfUnpinnedObjects() {
        return this.unpinnedCache.size();
    }
    
    @Override
    public int getSize() {
        return this.getNumberOfPinnedObjects() + this.getNumberOfUnpinnedObjects();
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
    public synchronized CachedPC put(final Object oid, final CachedPC pc) {
        if (oid == null || pc == null) {
            NucleusLogger.CACHE.warn(WeakLevel2Cache.LOCALISER.msg("004011"));
            return null;
        }
        if (this.maxSize >= 0 && this.getSize() == this.maxSize) {
            return null;
        }
        boolean toBePinned = false;
        if (this.pinnedClasses != null) {
            for (final PinnedClass pinCls : this.pinnedClasses) {
                if (pinCls.cls.getName().equals(pc.getObjectClass().getName()) || (pinCls.subclasses && pinCls.cls.isAssignableFrom(pc.getObjectClass()))) {
                    toBePinned = true;
                    break;
                }
            }
        }
        if (this.pinnedIds != null && this.pinnedIds.contains(oid)) {
            toBePinned = true;
        }
        Object obj = null;
        if (this.pinnedCache.get(oid) != null) {
            obj = this.pinnedCache.put(oid, pc);
            if (obj != null) {
                return (CachedPC)obj;
            }
        }
        else if (toBePinned) {
            this.pinnedCache.put(oid, pc);
            this.unpinnedCache.remove(oid);
        }
        else {
            obj = this.unpinnedCache.put(oid, pc);
            if (obj != null) {
                return (CachedPC)obj;
            }
        }
        return null;
    }
    
    @Override
    public boolean containsOid(final Object oid) {
        return this.pinnedCache.containsKey(oid) || this.unpinnedCache.containsKey(oid);
    }
    
    @Override
    public boolean isEmpty() {
        return this.pinnedCache.isEmpty() && this.unpinnedCache.isEmpty();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.unpinnedCache = new WeakValueMap();
    }
    
    static {
        WeakLevel2Cache.LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
