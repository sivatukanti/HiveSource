// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.simple;

import org.datanucleus.ClassConstants;
import java.io.ObjectStreamException;
import org.datanucleus.state.RelationshipManager;
import java.util.SortedSet;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.store.types.SCOCollectionIterator;
import java.util.Iterator;
import org.datanucleus.state.FetchPlanState;
import java.util.Comparator;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.util.NucleusLogger;
import java.util.Collection;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.types.SCOMtoN;
import org.datanucleus.store.types.SCOCollection;

public class TreeSet extends java.util.TreeSet implements SCOCollection, SCOMtoN, Cloneable
{
    protected static final Localiser LOCALISER;
    protected transient ObjectProvider ownerOP;
    protected transient AbstractMemberMetaData ownerMmd;
    protected java.util.TreeSet delegate;
    
    public TreeSet(final ObjectProvider ownerOP, final AbstractMemberMetaData mmd) {
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
            NucleusLogger.PERSISTENCE.debug(TreeSet.LOCALISER.msg("023003", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + this.size(), SCOUtils.getSCOWrapperOptionsMessage(true, false, true, false)));
        }
    }
    
    @Override
    public void initialise() {
        this.initialiseDelegate();
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(TreeSet.LOCALISER.msg("023003", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + this.size(), SCOUtils.getSCOWrapperOptionsMessage(true, false, true, false)));
        }
    }
    
    protected void initialiseDelegate() {
        final Comparator comparator = SCOUtils.getComparator(this.ownerMmd, this.ownerOP.getExecutionContext().getClassLoaderResolver());
        if (comparator != null) {
            this.delegate = new java.util.TreeSet(comparator);
        }
        else {
            this.delegate = new java.util.TreeSet();
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
        final Comparator comparator = SCOUtils.getComparator(this.ownerMmd, this.ownerOP.getExecutionContext().getClassLoaderResolver());
        Collection detached = null;
        if (comparator != null) {
            detached = new java.util.TreeSet(comparator);
        }
        else {
            detached = new java.util.TreeSet();
        }
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
        return this.delegate.clone();
    }
    
    @Override
    public Comparator comparator() {
        return this.delegate.comparator();
    }
    
    @Override
    public boolean contains(final Object element) {
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
    public Object first() {
        return this.delegate.first();
    }
    
    @Override
    public synchronized int hashCode() {
        return this.delegate.hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    @Override
    public Iterator iterator() {
        return new SCOCollectionIterator(this, this.ownerOP, this.delegate, null, true);
    }
    
    @Override
    public SortedSet headSet(final Object toElement) {
        return this.delegate.headSet(toElement);
    }
    
    @Override
    public SortedSet subSet(final Object fromElement, final Object toElement) {
        return this.delegate.subSet(fromElement, toElement);
    }
    
    @Override
    public SortedSet tailSet(final Object fromElement) {
        return this.delegate.headSet(fromElement);
    }
    
    @Override
    public Object last() {
        return this.delegate.last();
    }
    
    @Override
    public int size() {
        return this.delegate.size();
    }
    
    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        return this.delegate.toArray(a);
    }
    
    @Override
    public boolean add(final Object element) {
        final boolean success = this.delegate.add(element);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP).relationAdd(this.ownerMmd.getAbsoluteFieldNumber(), element);
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
    public boolean addAll(final Collection elements) {
        final boolean success = this.delegate.addAll(elements);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            final Iterator iter = elements.iterator();
            while (iter.hasNext()) {
                this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP).relationAdd(this.ownerMmd.getAbsoluteFieldNumber(), iter.next());
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
    public void clear() {
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            final Iterator iter = this.delegate.iterator();
            final RelationshipManager relMgr = this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP);
            while (iter.hasNext()) {
                relMgr.relationRemove(this.ownerMmd.getAbsoluteFieldNumber(), iter.next());
            }
        }
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
    public synchronized boolean remove(final Object element) {
        return this.remove(element, true);
    }
    
    @Override
    public synchronized boolean remove(final Object element, final boolean allowCascadeDelete) {
        final boolean success = this.delegate.remove(element);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP).relationRemove(this.ownerMmd.getAbsoluteFieldNumber(), element);
        }
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
    public boolean removeAll(final Collection elements) {
        final boolean success = this.delegate.removeAll(elements);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            final Iterator iter = elements.iterator();
            final RelationshipManager relMgr = this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP);
            while (iter.hasNext()) {
                relMgr.relationRemove(this.ownerMmd.getAbsoluteFieldNumber(), iter.next());
            }
        }
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
        return new java.util.TreeSet(this.delegate);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
