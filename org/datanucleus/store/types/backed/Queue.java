// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.backed;

import java.io.ObjectStreamException;
import org.datanucleus.flush.CollectionRemoveOperation;
import org.datanucleus.flush.ListRemoveAtOperation;
import org.datanucleus.store.types.SCOCollectionIterator;
import java.util.HashSet;
import org.datanucleus.store.scostore.Store;
import java.util.Iterator;
import org.datanucleus.flush.CollectionClearOperation;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.flush.Operation;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.flush.CollectionAddOperation;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.types.SCOContainer;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.BackedSCOStoreManager;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.store.types.SCOUtils;
import java.util.PriorityQueue;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.scostore.ListStore;

public class Queue extends org.datanucleus.store.types.simple.Queue implements BackedSCO
{
    protected transient ListStore backingStore;
    protected transient boolean allowNulls;
    protected transient boolean useCache;
    protected transient boolean isCacheLoaded;
    protected transient boolean queued;
    
    public Queue(final ObjectProvider op, final AbstractMemberMetaData mmd) {
        super(op, mmd);
        this.allowNulls = false;
        this.useCache = true;
        this.isCacheLoaded = false;
        this.queued = false;
        this.delegate = new PriorityQueue();
        final ExecutionContext ec = op.getExecutionContext();
        this.allowNulls = SCOUtils.allowNullsInContainer(this.allowNulls, mmd);
        this.queued = ec.isDelayDatastoreOperationsEnabled();
        this.useCache = SCOUtils.useContainerCache(op, mmd);
        if (!SCOUtils.collectionHasSerialisedElements(mmd) && mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            this.backingStore = (ListStore)((BackedSCOStoreManager)ec.getStoreManager()).getBackingStoreForField(clr, mmd, java.util.Queue.class);
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
                    NucleusLogger.PERSISTENCE.debug(Queue.LOCALISER.msg("023007", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
                }
                if (this.useCache) {
                    this.loadFromStore();
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
                            NucleusLogger.PERSISTENCE.warn(Queue.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
                        }
                    }
                }
                this.makeDirty();
                this.delegate.addAll(c);
            }
            else if (forUpdate) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(Queue.LOCALISER.msg("023008", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
                }
                if (this.backingStore != null) {
                    if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                        this.ownerOP.getExecutionContext().addOperationToQueue(new CollectionClearOperation(this.ownerOP, this.backingStore));
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
                        for (final Object element : c) {
                            this.ownerOP.getExecutionContext().addOperationToQueue(new CollectionAddOperation(this.ownerOP, this.backingStore, element));
                        }
                    }
                    else {
                        try {
                            this.backingStore.addAll(this.ownerOP, c, this.useCache ? this.delegate.size() : -1);
                        }
                        catch (NucleusDataStoreException dse) {
                            NucleusLogger.PERSISTENCE.warn(Queue.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
                        }
                    }
                }
                this.delegate.addAll(c);
                this.makeDirty();
            }
            else {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(Queue.LOCALISER.msg("023007", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
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
                NucleusLogger.PERSISTENCE.debug(Queue.LOCALISER.msg("023006", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName()));
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
        return super.clone();
    }
    
    @Override
    public synchronized boolean contains(final Object element) {
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
            final HashSet h = new HashSet(c);
            final Iterator iter = this.iterator();
            while (iter.hasNext()) {
                h.remove(iter.next());
            }
            return h.isEmpty();
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
        if (!(o instanceof java.util.Queue)) {
            return false;
        }
        final java.util.Queue c = (java.util.Queue)o;
        return c.size() == this.size() && this.containsAll(c);
    }
    
    @Override
    public synchronized int hashCode() {
        if (this.useCache) {
            this.loadFromStore();
        }
        return this.delegate.hashCode();
    }
    
    @Override
    public synchronized boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public synchronized Iterator iterator() {
        if (this.useCache) {
            this.loadFromStore();
        }
        return new SCOCollectionIterator(this, this.ownerOP, this.delegate, this.backingStore, this.useCache);
    }
    
    @Override
    public synchronized Object peek() {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return this.backingStore.get(this.ownerOP, 0);
        }
        return this.delegate.peek();
    }
    
    @Override
    public synchronized int size() {
        if (this.useCache && this.isCacheLoaded) {
            return this.delegate.size();
        }
        if (this.backingStore != null) {
            return this.backingStore.size(this.ownerOP);
        }
        return this.delegate.size();
    }
    
    @Override
    public synchronized Object[] toArray() {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return SCOUtils.toArray(this.backingStore, this.ownerOP);
        }
        return this.delegate.toArray();
    }
    
    @Override
    public synchronized Object[] toArray(final Object[] a) {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return SCOUtils.toArray(this.backingStore, this.ownerOP, a);
        }
        return this.delegate.toArray(a);
    }
    
    @Override
    public String toString() {
        final StringBuffer s = new StringBuffer("[");
        int i = 0;
        final Iterator iter = this.iterator();
        while (iter.hasNext()) {
            if (i > 0) {
                s.append(',');
            }
            s.append(iter.next());
            ++i;
        }
        s.append("]");
        return s.toString();
    }
    
    @Override
    public synchronized boolean add(final Object element) {
        if (!this.allowNulls && element == null) {
            throw new NullPointerException("Nulls not allowed for collection at field " + this.ownerMmd.getName() + " but element is null");
        }
        if (this.useCache) {
            this.loadFromStore();
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
                    NucleusLogger.PERSISTENCE.warn(Queue.LOCALISER.msg("023013", "add", this.ownerMmd.getName(), dse));
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
    public synchronized boolean addAll(final Collection elements) {
        if (this.useCache) {
            this.loadFromStore();
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
                    NucleusLogger.PERSISTENCE.warn(Queue.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
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
    public synchronized void clear() {
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
    public synchronized boolean offer(final Object element) {
        return this.add(element);
    }
    
    @Override
    public synchronized Object poll() {
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        final int size = this.useCache ? this.delegate.size() : -1;
        final Object delegateObject = this.delegate.poll();
        Object backingObject = null;
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new ListRemoveAtOperation(this.ownerOP, this.backingStore, 0));
            }
            else {
                try {
                    backingObject = this.backingStore.remove(this.ownerOP, 0, size);
                }
                catch (NucleusDataStoreException dse) {
                    backingObject = null;
                }
            }
        }
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return (this.backingStore != null) ? backingObject : delegateObject;
    }
    
    @Override
    public synchronized boolean remove(final Object element) {
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
                    NucleusLogger.PERSISTENCE.warn(Queue.LOCALISER.msg("023013", "remove", this.ownerMmd.getName(), dse));
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
    public synchronized boolean removeAll(final Collection elements) {
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
                    NucleusLogger.PERSISTENCE.warn(Queue.LOCALISER.msg("023013", "removeAll", this.ownerMmd.getName(), dse));
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
            return new PriorityQueue(this.delegate);
        }
        return new PriorityQueue(this.delegate);
    }
}
