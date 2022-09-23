// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.simple;

import java.io.ObjectStreamException;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.store.types.SCOCollectionIterator;
import java.util.Iterator;
import org.datanucleus.state.FetchPlanState;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.util.NucleusLogger;
import java.util.Collection;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;
import java.io.Serializable;
import org.datanucleus.store.types.SCOMtoN;
import org.datanucleus.store.types.SCOCollection;
import java.util.AbstractQueue;

public class Queue extends AbstractQueue implements SCOCollection, SCOMtoN, Cloneable, Serializable
{
    protected static final Localiser LOCALISER;
    protected ObjectProvider ownerOP;
    protected AbstractMemberMetaData ownerMmd;
    protected java.util.Queue delegate;
    
    public Queue(final ObjectProvider ownerOP, final AbstractMemberMetaData mmd) {
        this.ownerOP = ownerOP;
        this.ownerMmd = mmd;
    }
    
    @Override
    public void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        final Collection c = (Collection)o;
        if (c != null) {
            this.initialiseDelegate();
            this.delegate.addAll(c);
        }
        else {
            this.initialiseDelegate();
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(Queue.LOCALISER.msg("023003", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + this.size(), SCOUtils.getSCOWrapperOptionsMessage(true, false, false, false)));
        }
    }
    
    @Override
    public void initialise() {
        this.initialiseDelegate();
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(Queue.LOCALISER.msg("023003", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + this.size(), SCOUtils.getSCOWrapperOptionsMessage(true, false, false, false)));
        }
    }
    
    protected void initialiseDelegate() {
        final Comparator comparator = SCOUtils.getComparator(this.ownerMmd, this.ownerOP.getExecutionContext().getClassLoaderResolver());
        if (comparator != null) {
            this.delegate = new PriorityQueue(5, comparator);
        }
        else {
            this.delegate = new PriorityQueue();
        }
    }
    
    @Override
    public Object getValue() {
        return this.delegate;
    }
    
    @Override
    public void load() {
    }
    
    @Override
    public boolean isLoaded() {
        return true;
    }
    
    @Override
    public void updateEmbeddedElement(final Object element, final int fieldNumber, final Object value) {
        this.makeDirty();
    }
    
    @Override
    public String getFieldName() {
        return this.ownerMmd.getName();
    }
    
    @Override
    public Object getOwner() {
        return (this.ownerOP != null) ? this.ownerOP.getObject() : null;
    }
    
    @Override
    public synchronized void unsetOwner() {
        if (this.ownerOP != null) {
            this.ownerOP = null;
            this.ownerMmd = null;
        }
    }
    
    public void makeDirty() {
        if (this.ownerOP != null) {
            this.ownerOP.makeDirty(this.ownerMmd.getAbsoluteFieldNumber());
        }
    }
    
    @Override
    public Object detachCopy(final FetchPlanState state) {
        final Collection detached = new PriorityQueue();
        SCOUtils.detachCopyForCollection(this.ownerOP, this.toArray(), state, detached);
        return detached;
    }
    
    @Override
    public void attachCopy(final Object value) {
        final Collection c = (Collection)value;
        final boolean elementsWithoutIdentity = SCOUtils.collectionHasElementsWithoutIdentity(this.ownerMmd);
        SCOUtils.attachCopyElements(this.ownerOP, this, c, elementsWithoutIdentity);
    }
    
    @Override
    public Object clone() {
        return null;
    }
    
    @Override
    public synchronized boolean contains(final Object element) {
        return this.delegate.contains(element);
    }
    
    @Override
    public synchronized boolean containsAll(final Collection c) {
        return this.delegate.containsAll(c);
    }
    
    @Override
    public synchronized boolean equals(final Object o) {
        return this.delegate.equals(o);
    }
    
    @Override
    public synchronized int hashCode() {
        return this.delegate.hashCode();
    }
    
    @Override
    public synchronized boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    @Override
    public synchronized Iterator iterator() {
        return new SCOCollectionIterator(this, this.ownerOP, this.delegate, null, true);
    }
    
    @Override
    public synchronized Object peek() {
        return this.delegate.peek();
    }
    
    @Override
    public synchronized int size() {
        return this.delegate.size();
    }
    
    @Override
    public synchronized Object[] toArray() {
        return this.delegate.toArray();
    }
    
    @Override
    public synchronized Object[] toArray(final Object[] a) {
        return this.delegate.toArray(a);
    }
    
    @Override
    public synchronized boolean add(final Object element) {
        final boolean success = this.delegate.add(element);
        if (success) {
            this.makeDirty();
            if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
        }
        return success;
    }
    
    @Override
    public synchronized boolean addAll(final Collection elements) {
        final boolean success = this.delegate.addAll(elements);
        if (success) {
            this.makeDirty();
            if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
        }
        return success;
    }
    
    @Override
    public synchronized void clear() {
        if (this.ownerOP != null && !this.delegate.isEmpty() && SCOUtils.hasDependentElement(this.ownerMmd)) {
            final Iterator iter = this.delegate.iterator();
            while (iter.hasNext()) {
                this.ownerOP.getExecutionContext().deleteObjectInternal(iter.next());
            }
        }
        this.delegate.clear();
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
    }
    
    @Override
    public synchronized boolean offer(final Object element) {
        final boolean success = this.delegate.offer(element);
        if (success) {
            this.makeDirty();
            if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
        }
        return success;
    }
    
    @Override
    public synchronized Object poll() {
        final Object obj = this.delegate.poll();
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return obj;
    }
    
    @Override
    public synchronized boolean remove(final Object element) {
        return this.remove(element, true);
    }
    
    @Override
    public synchronized boolean remove(final Object element, final boolean allowCascadeDelete) {
        final boolean success = this.delegate.remove(element);
        if (this.ownerOP != null && allowCascadeDelete && SCOUtils.hasDependentElement(this.ownerMmd)) {
            this.ownerOP.getExecutionContext().deleteObjectInternal(element);
        }
        if (success) {
            this.makeDirty();
            if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
        }
        return success;
    }
    
    @Override
    public synchronized boolean removeAll(final Collection elements) {
        final boolean success = this.delegate.removeAll(elements);
        if (this.ownerOP != null && elements != null && !elements.isEmpty() && SCOUtils.hasDependentElement(this.ownerMmd)) {
            final Iterator iter = elements.iterator();
            while (iter.hasNext()) {
                this.ownerOP.getExecutionContext().deleteObjectInternal(iter.next());
            }
        }
        if (success) {
            this.makeDirty();
            if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
        }
        return success;
    }
    
    @Override
    public synchronized boolean retainAll(final Collection c) {
        final boolean success = this.delegate.retainAll(c);
        if (success) {
            this.makeDirty();
            if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
        }
        return success;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new PriorityQueue(this.delegate);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", org.datanucleus.store.types.simple.PriorityQueue.class.getClassLoader());
    }
}
