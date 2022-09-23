// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.backed;

import java.io.ObjectStreamException;
import org.datanucleus.flush.MapRemoveOperation;
import java.util.Collection;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.SortedMap;
import java.util.Set;
import org.datanucleus.store.scostore.Store;
import java.util.Iterator;
import org.datanucleus.flush.MapClearOperation;
import org.datanucleus.flush.Operation;
import org.datanucleus.flush.MapPutOperation;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.types.SCOContainer;
import org.datanucleus.util.NucleusLogger;
import java.util.Comparator;
import org.datanucleus.store.BackedSCOStoreManager;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.scostore.MapStore;

public class TreeMap extends org.datanucleus.store.types.simple.TreeMap implements BackedSCO
{
    protected transient MapStore backingStore;
    protected transient boolean allowNulls;
    protected transient boolean useCache;
    protected transient boolean isCacheLoaded;
    protected transient boolean queued;
    
    public TreeMap(final ObjectProvider op, final AbstractMemberMetaData mmd) {
        super(op, mmd);
        this.allowNulls = false;
        this.useCache = true;
        this.isCacheLoaded = false;
        this.queued = false;
        final ExecutionContext ec = op.getExecutionContext();
        this.allowNulls = SCOUtils.allowNullsInContainer(this.allowNulls, mmd);
        this.queued = ec.isDelayDatastoreOperationsEnabled();
        this.useCache = SCOUtils.useContainerCache(op, mmd);
        if (!SCOUtils.mapHasSerialisedKeysAndValues(mmd) && mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            this.backingStore = (MapStore)((BackedSCOStoreManager)ec.getStoreManager()).getBackingStoreForField(clr, mmd, java.util.TreeMap.class);
        }
        final Comparator comparator = SCOUtils.getComparator(mmd, op.getExecutionContext().getClassLoaderResolver());
        if (comparator != null) {
            this.delegate = new java.util.TreeMap(comparator);
        }
        else {
            this.delegate = new java.util.TreeMap();
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(SCOUtils.getContainerInfoMessage(op, this.ownerMmd.getName(), this, this.useCache, this.queued, this.allowNulls, SCOUtils.useCachedLazyLoading(op, this.ownerMmd)));
        }
    }
    
    @Override
    public void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        final Map m = (Map)o;
        if (m != null) {
            if (SCOUtils.mapHasSerialisedKeysAndValues(this.ownerMmd) && (this.ownerMmd.getMap().keyIsPersistent() || this.ownerMmd.getMap().valueIsPersistent())) {
                final ExecutionContext ec = this.ownerOP.getExecutionContext();
                for (final Map.Entry entry : m.entrySet()) {
                    final Object key = entry.getKey();
                    final Object value = entry.getValue();
                    if (this.ownerMmd.getMap().keyIsPersistent()) {
                        ObjectProvider objSM = ec.findObjectProvider(key);
                        if (objSM == null) {
                            objSM = ec.newObjectProviderForEmbedded(key, false, this.ownerOP, this.ownerMmd.getAbsoluteFieldNumber());
                        }
                    }
                    if (this.ownerMmd.getMap().valueIsPersistent()) {
                        ObjectProvider objSM = ec.findObjectProvider(value);
                        if (objSM != null) {
                            continue;
                        }
                        objSM = ec.newObjectProviderForEmbedded(value, false, this.ownerOP, this.ownerMmd.getAbsoluteFieldNumber());
                    }
                }
            }
            if (this.backingStore != null && this.useCache && !this.isCacheLoaded) {
                this.isCacheLoaded = true;
            }
            if (forInsert) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(TreeMap.LOCALISER.msg("023007", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + m.size()));
                }
                if (this.useCache) {
                    this.loadFromStore();
                }
                if (this.backingStore != null) {
                    if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                        for (final Map.Entry entry2 : m.entrySet()) {
                            this.ownerOP.getExecutionContext().addOperationToQueue(new MapPutOperation(this.ownerOP, this.backingStore, entry2.getKey(), entry2.getValue()));
                        }
                    }
                    else {
                        this.backingStore.putAll(this.ownerOP, m);
                    }
                }
                this.delegate.putAll(m);
                this.makeDirty();
            }
            else if (forUpdate) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(TreeMap.LOCALISER.msg("023008", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + m.size()));
                }
                this.delegate.clear();
                if (this.backingStore != null) {
                    if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                        this.ownerOP.getExecutionContext().addOperationToQueue(new MapClearOperation(this.ownerOP, this.backingStore));
                    }
                    else {
                        this.backingStore.clear(this.ownerOP);
                    }
                }
                if (this.useCache) {
                    this.loadFromStore();
                }
                if (this.backingStore != null) {
                    if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                        for (final Map.Entry entry2 : m.entrySet()) {
                            this.ownerOP.getExecutionContext().addOperationToQueue(new MapPutOperation(this.ownerOP, this.backingStore, entry2.getKey(), entry2.getValue()));
                        }
                    }
                    else {
                        this.backingStore.putAll(this.ownerOP, m);
                    }
                }
                this.delegate.putAll(m);
                this.makeDirty();
            }
            else {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(TreeMap.LOCALISER.msg("023007", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + m.size()));
                }
                this.delegate.clear();
                this.delegate.putAll(m);
            }
        }
    }
    
    @Override
    public void initialise() {
        if (this.useCache && !SCOUtils.useCachedLazyLoading(this.ownerOP, this.ownerMmd)) {
            this.loadFromStore();
        }
    }
    
    @Override
    public Object getValue() {
        this.loadFromStore();
        return super.getValue();
    }
    
    @Override
    public void load() {
        if (this.useCache) {
            this.loadFromStore();
        }
    }
    
    @Override
    public boolean isLoaded() {
        return this.useCache && this.isCacheLoaded;
    }
    
    protected void loadFromStore() {
        if (this.backingStore != null && !this.isCacheLoaded) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(TreeMap.LOCALISER.msg("023006", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName()));
            }
            this.delegate.clear();
            SCOUtils.populateMapDelegateWithStoreData(this.delegate, this.backingStore, this.ownerOP);
            this.isCacheLoaded = true;
        }
    }
    
    @Override
    public Store getBackingStore() {
        return this.backingStore;
    }
    
    @Override
    public void updateEmbeddedKey(final Object key, final int fieldNumber, final Object newValue) {
        if (this.backingStore != null) {
            this.backingStore.updateEmbeddedKey(this.ownerOP, key, fieldNumber, newValue);
        }
    }
    
    @Override
    public void updateEmbeddedValue(final Object value, final int fieldNumber, final Object newValue) {
        if (this.backingStore != null) {
            this.backingStore.updateEmbeddedValue(this.ownerOP, value, fieldNumber, newValue);
        }
    }
    
    @Override
    public synchronized void unsetOwner() {
        super.unsetOwner();
        if (this.backingStore != null) {
            this.backingStore = null;
        }
    }
    
    @Override
    public Object clone() {
        if (this.useCache) {
            this.loadFromStore();
        }
        return this.delegate.clone();
    }
    
    @Override
    public Comparator comparator() {
        return this.delegate.comparator();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.containsKey(key);
        }
        if (this.backingStore != null) {
            return this.backingStore.containsKey(this.ownerOP, key);
        }
        return this.delegate.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.containsValue(value);
        }
        if (this.backingStore != null) {
            return this.backingStore.containsValue(this.ownerOP, value);
        }
        return this.delegate.containsValue(value);
    }
    
    @Override
    public Set entrySet() {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return new org.datanucleus.store.types.backed.Set(this.ownerOP, this.ownerMmd, false, this.backingStore.entrySetStore());
        }
        return this.delegate.entrySet();
    }
    
    @Override
    public synchronized boolean equals(final Object o) {
        if (this.useCache) {
            this.loadFromStore();
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        final Map m = (Map)o;
        return this.entrySet().equals(m.entrySet());
    }
    
    @Override
    public Object firstKey() {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.firstKey();
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.firstKey();
        }
        final Set keys = this.keySet();
        final Iterator keysIter = keys.iterator();
        return keysIter.next();
    }
    
    @Override
    public Object lastKey() {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.lastKey();
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.lastKey();
        }
        final Set keys = this.keySet();
        final Iterator keysIter = keys.iterator();
        Object last = null;
        while (keysIter.hasNext()) {
            last = keysIter.next();
        }
        return last;
    }
    
    @Override
    public SortedMap headMap(final Object toKey) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.headMap(toKey);
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.headMap(toKey);
        }
        throw new NucleusUserException("Don't currently support TreeMap.headMap() when not using cached containers");
    }
    
    @Override
    public SortedMap subMap(final Object fromKey, final Object toKey) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.subMap(fromKey, toKey);
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.subMap(fromKey, toKey);
        }
        throw new NucleusUserException("Don't currently support TreeMap.subMap() when not using cached container");
    }
    
    @Override
    public SortedMap tailMap(final Object fromKey) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.headMap(fromKey);
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.headMap(fromKey);
        }
        throw new NucleusUserException("Don't currently support TreeMap.tailMap() when not using cached containers");
    }
    
    @Override
    public Object get(final Object key) {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return this.backingStore.get(this.ownerOP, key);
        }
        return this.delegate.get(key);
    }
    
    @Override
    public synchronized int hashCode() {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            int h = 0;
            final Iterator i = this.entrySet().iterator();
            while (i.hasNext()) {
                h += i.next().hashCode();
            }
            return h;
        }
        return this.delegate.hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public Set keySet() {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return new org.datanucleus.store.types.backed.Set(this.ownerOP, this.ownerMmd, false, this.backingStore.keySetStore());
        }
        return this.delegate.keySet();
    }
    
    @Override
    public int size() {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.size();
        }
        if (this.backingStore != null) {
            return this.backingStore.entrySetStore().size(this.ownerOP);
        }
        return this.delegate.size();
    }
    
    @Override
    public Collection values() {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return new org.datanucleus.store.types.backed.Set(this.ownerOP, this.ownerMmd, true, this.backingStore.valueSetStore());
        }
        return this.delegate.values();
    }
    
    @Override
    public void clear() {
        this.makeDirty();
        this.delegate.clear();
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new MapClearOperation(this.ownerOP, this.backingStore));
            }
            else {
                this.backingStore.clear(this.ownerOP);
            }
        }
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        if (!this.allowNulls) {
            if (value == null) {
                throw new NullPointerException("Nulls not allowed for map at field " + this.ownerMmd.getName() + " but value is null");
            }
            if (key == null) {
                throw new NullPointerException("Nulls not allowed for map at field " + this.ownerMmd.getName() + " but key is null");
            }
        }
        if (this.useCache) {
            this.loadFromStore();
        }
        this.makeDirty();
        Object oldValue = null;
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new MapPutOperation(this.ownerOP, this.backingStore, key, value));
            }
            else {
                oldValue = this.backingStore.put(this.ownerOP, key, value);
            }
        }
        final Object delegateOldValue = this.delegate.put(key, value);
        if (this.backingStore == null) {
            oldValue = delegateOldValue;
        }
        else if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
            oldValue = delegateOldValue;
        }
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return oldValue;
    }
    
    @Override
    public void putAll(final Map m) {
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                for (final Map.Entry entry : m.entrySet()) {
                    this.ownerOP.getExecutionContext().addOperationToQueue(new MapPutOperation(this.ownerOP, this.backingStore, entry.getKey(), entry.getValue()));
                }
            }
            else {
                this.backingStore.putAll(this.ownerOP, m);
            }
        }
        this.delegate.putAll(m);
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
    }
    
    @Override
    public Object remove(final Object key) {
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        Object removed = null;
        final Object delegateRemoved = this.delegate.remove(key);
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new MapRemoveOperation(this.ownerOP, this.backingStore, key, delegateRemoved));
                removed = delegateRemoved;
            }
            else {
                removed = this.backingStore.remove(this.ownerOP, key);
            }
        }
        else {
            removed = delegateRemoved;
        }
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return removed;
    }
    
    @Override
    protected Object writeReplace() throws ObjectStreamException {
        if (this.useCache) {
            this.loadFromStore();
            return new java.util.TreeMap(this.delegate);
        }
        return new java.util.TreeMap(this.delegate);
    }
}
