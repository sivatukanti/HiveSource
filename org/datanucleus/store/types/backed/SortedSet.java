// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.backed;

import java.io.ObjectStreamException;
import java.util.HashSet;
import org.datanucleus.flush.CollectionRemoveOperation;
import org.datanucleus.flush.CollectionClearOperation;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.types.SCOCollectionIterator;
import java.util.Set;
import org.datanucleus.store.scostore.Store;
import org.datanucleus.state.RelationshipManager;
import java.util.Iterator;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.flush.Operation;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.flush.CollectionAddOperation;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.types.SCOContainer;
import org.datanucleus.util.NucleusLogger;
import java.util.Comparator;
import java.util.TreeSet;
import org.datanucleus.store.BackedSCOStoreManager;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.scostore.SetStore;

public class SortedSet extends org.datanucleus.store.types.simple.SortedSet implements BackedSCO
{
    protected transient SetStore backingStore;
    protected transient boolean allowNulls;
    protected transient boolean useCache;
    protected transient boolean isCacheLoaded;
    protected transient boolean queued;
    
    public SortedSet(final ObjectProvider op, final AbstractMemberMetaData mmd) {
        super(op, mmd);
        this.allowNulls = false;
        this.useCache = true;
        this.isCacheLoaded = false;
        this.queued = false;
        final ExecutionContext ec = op.getExecutionContext();
        this.allowNulls = SCOUtils.allowNullsInContainer(this.allowNulls, mmd);
        this.queued = ec.isDelayDatastoreOperationsEnabled();
        this.useCache = SCOUtils.useContainerCache(op, mmd);
        if (!SCOUtils.collectionHasSerialisedElements(mmd) && mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            this.backingStore = (SetStore)((BackedSCOStoreManager)ec.getStoreManager()).getBackingStoreForField(clr, mmd, java.util.SortedSet.class);
        }
        final Comparator comparator = SCOUtils.getComparator(mmd, op.getExecutionContext().getClassLoaderResolver());
        if (comparator != null) {
            this.delegate = new TreeSet(comparator);
        }
        else {
            this.delegate = new TreeSet();
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(SCOUtils.getContainerInfoMessage(op, this.ownerMmd.getName(), this, this.useCache, this.queued, this.allowNulls, SCOUtils.useCachedLazyLoading(op, this.ownerMmd)));
        }
    }
    
    @Override
    public void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        final Collection c = (Collection)o;
        if (c != null) {
            if (SCOUtils.collectionHasSerialisedElements(this.ownerMmd) && this.ownerMmd.getCollection().elementIsPersistent()) {
                final ExecutionContext ec = this.ownerOP.getExecutionContext();
                for (final Object pc : c) {
                    ObjectProvider objSM = ec.findObjectProvider(pc);
                    if (objSM == null) {
                        objSM = ec.newObjectProviderForEmbedded(pc, false, this.ownerOP, this.ownerMmd.getAbsoluteFieldNumber());
                    }
                }
            }
            if (this.backingStore != null && this.useCache && !this.isCacheLoaded) {
                this.isCacheLoaded = true;
            }
            if (forInsert) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(SortedSet.LOCALISER.msg("023007", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
                }
                if (this.useCache) {
                    this.loadFromStore();
                }
                if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
                    final Iterator iter2 = c.iterator();
                    final RelationshipManager relMgr = this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP);
                    while (iter2.hasNext()) {
                        relMgr.relationAdd(this.ownerMmd.getAbsoluteFieldNumber(), iter2.next());
                    }
                }
                if (this.backingStore != null) {
                    if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                        for (final Object element : c) {
                            this.ownerOP.getExecutionContext().addOperationToQueue(new CollectionAddOperation(this.ownerOP, this.backingStore, element));
                        }
                    }
                    else {
                        try {
                            this.backingStore.addAll(this.ownerOP, c, this.useCache ? this.delegate.size() : -1);
                        }
                        catch (NucleusDataStoreException dse) {
                            NucleusLogger.PERSISTENCE.warn(SortedSet.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
                        }
                    }
                }
                this.delegate.addAll(c);
                this.makeDirty();
            }
            else if (forUpdate) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(SortedSet.LOCALISER.msg("023008", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
                }
                if (this.useCache) {
                    this.isCacheLoaded = false;
                    this.loadFromStore();
                    for (final Object elem : c) {
                        if (!this.delegate.contains(elem)) {
                            this.add(elem);
                        }
                    }
                    final java.util.SortedSet delegateCopy = new TreeSet(this.delegate);
                    for (final Object elem2 : delegateCopy) {
                        if (!c.contains(elem2)) {
                            this.remove(elem2);
                        }
                    }
                }
                else {
                    for (final Object elem : c) {
                        if (!this.contains(elem)) {
                            this.add(elem);
                        }
                    }
                    for (final Object elem : this) {
                        if (!c.contains(elem)) {
                            this.remove(elem);
                        }
                    }
                }
            }
            else {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(SortedSet.LOCALISER.msg("023007", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
                }
                this.delegate.clear();
                this.delegate.addAll(c);
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
                NucleusLogger.PERSISTENCE.debug(SortedSet.LOCALISER.msg("023006", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName()));
            }
            this.delegate.clear();
            final Iterator iter = this.backingStore.iterator(this.ownerOP);
            while (iter.hasNext()) {
                this.delegate.add(iter.next());
            }
            this.isCacheLoaded = true;
        }
    }
    
    @Override
    public Store getBackingStore() {
        return this.backingStore;
    }
    
    @Override
    public void updateEmbeddedElement(final Object element, final int fieldNumber, final Object value) {
        if (this.backingStore != null) {
            this.backingStore.updateEmbeddedElement(this.ownerOP, element, fieldNumber, value);
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
    public boolean contains(final Object element) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.contains(element);
        }
        if (this.backingStore != null) {
            return this.backingStore.contains(this.ownerOP, element);
        }
        return this.delegate.contains(element);
    }
    
    @Override
    public synchronized boolean containsAll(final Collection c) {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            final java.util.SortedSet sorted = new TreeSet(c);
            final Iterator iter = this.iterator();
            while (iter.hasNext()) {
                sorted.remove(iter.next());
            }
            return sorted.isEmpty();
        }
        return this.delegate.containsAll(c);
    }
    
    @Override
    public synchronized boolean equals(final Object o) {
        if (this.useCache) {
            this.loadFromStore();
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Set s = (Set)o;
        return s.size() == this.size() && this.containsAll(s);
    }
    
    @Override
    public Object first() {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.first();
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.first();
        }
        final Iterator iter = this.iterator();
        return iter.next();
    }
    
    @Override
    public synchronized int hashCode() {
        if (this.useCache) {
            this.loadFromStore();
        }
        return this.delegate.hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public Iterator iterator() {
        if (this.useCache) {
            this.loadFromStore();
        }
        return new SCOCollectionIterator(this, this.ownerOP, this.delegate, this.backingStore, this.useCache);
    }
    
    @Override
    public java.util.SortedSet headSet(final Object toElement) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.headSet(toElement);
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.headSet(toElement);
        }
        throw new NucleusUserException("Don't currently support SortedSet.headSet() when not using cached collections");
    }
    
    @Override
    public java.util.SortedSet subSet(final Object fromElement, final Object toElement) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.subSet(fromElement, toElement);
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.subSet(fromElement, toElement);
        }
        throw new NucleusUserException("Don't currently support SortedSet.subSet() when not using cached collections");
    }
    
    @Override
    public java.util.SortedSet tailSet(final Object fromElement) {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.headSet(fromElement);
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.headSet(fromElement);
        }
        throw new NucleusUserException("Don't currently support SortedSet.tailSet() when not using cached collections");
    }
    
    @Override
    public Object last() {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.last();
        }
        if (this.useCache) {
            this.loadFromStore();
            return this.delegate.last();
        }
        final Iterator iter = this.iterator();
        Object last = null;
        while (iter.hasNext()) {
            last = iter.next();
        }
        return last;
    }
    
    @Override
    public int size() {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.size();
        }
        if (this.backingStore != null) {
            return this.backingStore.size(this.ownerOP);
        }
        return this.delegate.size();
    }
    
    @Override
    public Object[] toArray() {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return SCOUtils.toArray(this.backingStore, this.ownerOP);
        }
        return this.delegate.toArray();
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return SCOUtils.toArray(this.backingStore, this.ownerOP, a);
        }
        return this.delegate.toArray(a);
    }
    
    @Override
    public boolean add(final Object element) {
        if (!this.allowNulls && element == null) {
            throw new NullPointerException("Nulls not allowed for collection at field " + this.ownerMmd.getName() + " but element is null");
        }
        if (this.useCache) {
            this.loadFromStore();
        }
        if (this.contains(element)) {
            return false;
        }
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP).relationAdd(this.ownerMmd.getAbsoluteFieldNumber(), element);
        }
        boolean backingSuccess = true;
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new CollectionAddOperation(this.ownerOP, this.backingStore, element));
            }
            else {
                try {
                    this.backingStore.add(this.ownerOP, element, this.useCache ? this.delegate.size() : -1);
                }
                catch (NucleusDataStoreException dse) {
                    NucleusLogger.PERSISTENCE.warn(SortedSet.LOCALISER.msg("023013", "add", this.ownerMmd.getName(), dse));
                    backingSuccess = false;
                }
            }
        }
        this.makeDirty();
        final boolean delegateSuccess = this.delegate.add(element);
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return (this.backingStore != null) ? backingSuccess : delegateSuccess;
    }
    
    @Override
    public boolean addAll(final Collection elements) {
        if (this.useCache) {
            this.loadFromStore();
        }
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            final Iterator iter = elements.iterator();
            final RelationshipManager relMgr = this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP);
            while (iter.hasNext()) {
                relMgr.relationAdd(this.ownerMmd.getAbsoluteFieldNumber(), iter.next());
            }
        }
        boolean backingSuccess = true;
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                for (final Object element : elements) {
                    this.ownerOP.getExecutionContext().addOperationToQueue(new CollectionAddOperation(this.ownerOP, this.backingStore, element));
                }
            }
            else {
                try {
                    backingSuccess = this.backingStore.addAll(this.ownerOP, elements, this.useCache ? this.delegate.size() : -1);
                }
                catch (NucleusDataStoreException dse) {
                    NucleusLogger.PERSISTENCE.warn(SortedSet.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
                    backingSuccess = false;
                }
            }
        }
        this.makeDirty();
        final boolean delegateSuccess = this.delegate.addAll(elements);
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return (this.backingStore != null) ? backingSuccess : delegateSuccess;
    }
    
    @Override
    public void clear() {
        this.makeDirty();
        this.delegate.clear();
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new CollectionClearOperation(this.ownerOP, this.backingStore));
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
    public boolean remove(final Object element) {
        return this.remove(element, true);
    }
    
    @Override
    public boolean remove(final Object element, final boolean allowCascadeDelete) {
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        final int size = this.useCache ? this.delegate.size() : -1;
        final boolean contained = this.delegate.contains(element);
        final boolean delegateSuccess = this.delegate.remove(element);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP).relationRemove(this.ownerMmd.getAbsoluteFieldNumber(), element);
        }
        boolean backingSuccess = true;
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                backingSuccess = contained;
                if (backingSuccess) {
                    this.ownerOP.getExecutionContext().addOperationToQueue(new CollectionRemoveOperation(this.ownerOP, this.backingStore, element, allowCascadeDelete));
                }
            }
            else {
                try {
                    backingSuccess = this.backingStore.remove(this.ownerOP, element, size, allowCascadeDelete);
                }
                catch (NucleusDataStoreException dse) {
                    NucleusLogger.PERSISTENCE.warn(SortedSet.LOCALISER.msg("023013", "remove", this.ownerMmd.getName(), dse));
                    backingSuccess = false;
                }
            }
        }
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return (this.backingStore != null) ? backingSuccess : delegateSuccess;
    }
    
    @Override
    public boolean removeAll(final Collection elements) {
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        final int size = this.useCache ? this.delegate.size() : -1;
        Collection contained = null;
        if (this.backingStore != null && SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
            contained = new HashSet();
            for (final Object elem : elements) {
                if (this.contains(elem)) {
                    contained.add(elem);
                }
            }
        }
        final boolean delegateSuccess = this.delegate.removeAll(elements);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            final Iterator iter = elements.iterator();
            final RelationshipManager relMgr = this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP);
            while (iter.hasNext()) {
                relMgr.relationRemove(this.ownerMmd.getAbsoluteFieldNumber(), iter.next());
            }
        }
        if (this.backingStore != null) {
            boolean backingSuccess = true;
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                backingSuccess = false;
                for (final Object element : contained) {
                    backingSuccess = true;
                    this.ownerOP.getExecutionContext().addOperationToQueue(new CollectionRemoveOperation(this.ownerOP, this.backingStore, element, true));
                }
            }
            else {
                try {
                    backingSuccess = this.backingStore.removeAll(this.ownerOP, elements, size);
                }
                catch (NucleusDataStoreException dse) {
                    NucleusLogger.PERSISTENCE.warn(SortedSet.LOCALISER.msg("023013", "removeAll", this.ownerMmd.getName(), dse));
                    backingSuccess = false;
                }
            }
            if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
            return backingSuccess;
        }
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return delegateSuccess;
    }
    
    @Override
    public synchronized boolean retainAll(final Collection c) {
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        boolean modified = false;
        final Iterator iter = this.iterator();
        while (iter.hasNext()) {
            final Object element = iter.next();
            if (!c.contains(element)) {
                iter.remove();
                modified = true;
            }
        }
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return modified;
    }
    
    @Override
    protected Object writeReplace() throws ObjectStreamException {
        if (this.useCache) {
            this.loadFromStore();
            return new TreeSet(this.delegate);
        }
        return new TreeSet(this.delegate);
    }
}
