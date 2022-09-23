// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.backed;

import java.io.ObjectStreamException;
import org.datanucleus.flush.ListSetOperation;
import java.util.HashSet;
import org.datanucleus.flush.CollectionRemoveOperation;
import org.datanucleus.flush.ListRemoveAtOperation;
import org.datanucleus.flush.ListAddAtOperation;
import java.util.ListIterator;
import org.datanucleus.store.types.SCOListIterator;
import java.util.List;
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
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.scostore.ListStore;

public class Stack extends org.datanucleus.store.types.simple.Stack implements BackedSCO
{
    protected transient ListStore backingStore;
    protected transient boolean allowNulls;
    protected transient boolean useCache;
    protected transient boolean isCacheLoaded;
    protected transient boolean queued;
    
    public Stack(final ObjectProvider op, final AbstractMemberMetaData mmd) {
        super(op, mmd);
        this.allowNulls = false;
        this.useCache = true;
        this.isCacheLoaded = false;
        this.queued = false;
        this.delegate = new java.util.Stack();
        final ExecutionContext ec = op.getExecutionContext();
        this.allowNulls = SCOUtils.allowNullsInContainer(this.allowNulls, mmd);
        this.queued = ec.isDelayDatastoreOperationsEnabled();
        this.useCache = SCOUtils.useContainerCache(op, mmd);
        if (!SCOUtils.collectionHasSerialisedElements(mmd) && mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            this.backingStore = (ListStore)((BackedSCOStoreManager)ec.getStoreManager()).getBackingStoreForField(clr, mmd, java.util.Stack.class);
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
                    NucleusLogger.PERSISTENCE.debug(Stack.LOCALISER.msg("023007", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
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
                            NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
                        }
                    }
                }
                this.delegate.addAll(c);
                this.makeDirty();
            }
            else if (forUpdate) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(Stack.LOCALISER.msg("023008", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
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
                            NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
                        }
                    }
                }
                this.delegate.addAll(c);
                this.makeDirty();
            }
            else {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(Stack.LOCALISER.msg("023007", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + c.size()));
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
                NucleusLogger.PERSISTENCE.debug(Stack.LOCALISER.msg("023006", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName()));
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
    public boolean empty() {
        return this.isEmpty();
    }
    
    @Override
    public synchronized boolean equals(final Object o) {
        if (this.useCache) {
            this.loadFromStore();
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        final List l = (List)o;
        if (l.size() != this.size()) {
            return false;
        }
        final Object[] elements = this.toArray();
        final Object[] otherElements = l.toArray();
        for (int i = 0; i < elements.length; ++i) {
            if (!elements[i].equals(otherElements[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Object get(final int index) {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return this.backingStore.get(this.ownerOP, index);
        }
        return this.delegate.get(index);
    }
    
    @Override
    public int indexOf(final Object element) {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return this.backingStore.indexOf(this.ownerOP, element);
        }
        return this.delegate.indexOf(element);
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
        return new SCOListIterator(this, this.ownerOP, this.delegate, this.backingStore, this.useCache, -1);
    }
    
    @Override
    public ListIterator listIterator() {
        if (this.useCache) {
            this.loadFromStore();
        }
        return new SCOListIterator(this, this.ownerOP, this.delegate, this.backingStore, this.useCache, -1);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        if (this.useCache) {
            this.loadFromStore();
        }
        return new SCOListIterator(this, this.ownerOP, this.delegate, this.backingStore, this.useCache, index);
    }
    
    @Override
    public int lastIndexOf(final Object element) {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return this.backingStore.lastIndexOf(this.ownerOP, element);
        }
        return this.delegate.lastIndexOf(element);
    }
    
    @Override
    public Object peek() {
        return this.get(0);
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
    public synchronized List subList(final int from, final int to) {
        if (this.useCache) {
            this.loadFromStore();
        }
        else if (this.backingStore != null) {
            return this.backingStore.subList(this.ownerOP, from, to);
        }
        return this.delegate.subList(from, to);
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
    public void add(final int index, final Object element) {
        if (!this.allowNulls && element == null) {
            throw new NullPointerException("Nulls not allowed for collection at field " + this.ownerMmd.getName() + " but element is null");
        }
        if (this.useCache) {
            this.loadFromStore();
        }
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new ListAddAtOperation(this.ownerOP, this.backingStore, index, element));
            }
            else {
                try {
                    this.backingStore.add(this.ownerOP, element, index, this.useCache ? this.delegate.size() : -1);
                }
                catch (NucleusDataStoreException dse) {
                    NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "add", this.ownerMmd.getName(), dse));
                }
            }
        }
        this.makeDirty();
        this.delegate.add(index, element);
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
    }
    
    @Override
    public boolean add(final Object element) {
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
                    NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "add", this.ownerMmd.getName(), dse));
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
    public void addElement(final Object element) {
        this.add(element);
    }
    
    @Override
    public boolean addAll(final Collection elements) {
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
                    NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
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
    public boolean addAll(final int index, final Collection elements) {
        if (this.useCache) {
            this.loadFromStore();
        }
        boolean backingSuccess = true;
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                int pos = index;
                for (final Object element : elements) {
                    this.ownerOP.getExecutionContext().addOperationToQueue(new ListAddAtOperation(this.ownerOP, this.backingStore, pos++, element));
                }
            }
            else {
                try {
                    backingSuccess = this.backingStore.addAll(this.ownerOP, elements, index, this.useCache ? this.delegate.size() : -1);
                }
                catch (NucleusDataStoreException dse) {
                    NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "addAll", this.ownerMmd.getName(), dse));
                    backingSuccess = false;
                }
            }
        }
        this.makeDirty();
        final boolean delegateSuccess = this.delegate.addAll(index, elements);
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
    public Object pop() {
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new ListRemoveAtOperation(this.ownerOP, this.backingStore, 0));
            }
            else {
                this.backingStore.remove(this.ownerOP, 0, this.useCache ? this.delegate.size() : -1);
            }
        }
        final Object removed = this.delegate.remove(0);
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return removed;
    }
    
    @Override
    public Object push(final Object element) {
        if (!this.allowNulls && element == null) {
            throw new NullPointerException("Nulls not allowed for collection at field " + this.ownerMmd.getName() + " but element is null");
        }
        if (this.useCache) {
            this.loadFromStore();
        }
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new ListAddAtOperation(this.ownerOP, this.backingStore, 0, element));
            }
            else {
                this.backingStore.add(this.ownerOP, element, 0, this.useCache ? this.delegate.size() : -1);
            }
        }
        this.makeDirty();
        this.delegate.add(0, element);
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return element;
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
                    NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "remove", this.ownerMmd.getName(), dse));
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
                    NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "removeAll", this.ownerMmd.getName(), dse));
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
    public boolean removeElement(final Object element) {
        return this.remove(element);
    }
    
    @Override
    public Object remove(final int index) {
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        final int size = this.useCache ? this.delegate.size() : -1;
        final Object delegateObject = this.useCache ? this.delegate.remove(index) : null;
        Object backingObject = null;
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                backingObject = delegateObject;
                this.ownerOP.getExecutionContext().addOperationToQueue(new ListRemoveAtOperation(this.ownerOP, this.backingStore, index));
            }
            else {
                try {
                    backingObject = this.backingStore.remove(this.ownerOP, index, size);
                }
                catch (NucleusDataStoreException dse) {
                    NucleusLogger.PERSISTENCE.warn(Stack.LOCALISER.msg("023013", "remove", this.ownerMmd.getName(), dse));
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
    public void removeElementAt(final int index) {
        this.remove(index);
    }
    
    @Override
    public void removeAllElements() {
        this.clear();
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
    public Object set(final int index, final Object element, final boolean allowDependentField) {
        if (!this.allowNulls && element == null) {
            throw new NullPointerException("Nulls not allowed for collection at field " + this.ownerMmd.getName() + " but element is null");
        }
        this.makeDirty();
        if (this.useCache) {
            this.loadFromStore();
        }
        final Object delegateReturn = this.delegate.set(index, element);
        if (this.backingStore != null) {
            if (SCOUtils.useQueuedUpdate(this.queued, this.ownerOP)) {
                this.ownerOP.getExecutionContext().addOperationToQueue(new ListSetOperation(this.ownerOP, this.backingStore, index, element, allowDependentField));
            }
            else {
                this.backingStore.set(this.ownerOP, index, element, allowDependentField);
            }
        }
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return delegateReturn;
    }
    
    @Override
    public Object set(final int index, final Object element) {
        return this.set(index, element, true);
    }
    
    @Override
    public void setElementAt(final Object element, final int index) {
        this.set(index, element);
    }
    
    @Override
    protected Object writeReplace() throws ObjectStreamException {
        if (this.useCache) {
            this.loadFromStore();
            final java.util.Stack stack = new java.util.Stack();
            stack.addAll(this.delegate);
            return stack;
        }
        final java.util.Stack stack = new java.util.Stack();
        stack.addAll(this.delegate);
        return stack;
    }
}
