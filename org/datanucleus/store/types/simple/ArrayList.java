// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.simple;

import org.datanucleus.ClassConstants;
import java.io.ObjectStreamException;
import org.datanucleus.state.RelationshipManager;
import java.util.ListIterator;
import org.datanucleus.store.scostore.ListStore;
import org.datanucleus.store.types.SCOListIterator;
import java.util.Iterator;
import java.util.List;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.util.NucleusLogger;
import java.util.Collection;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.types.SCOList;

public class ArrayList extends java.util.ArrayList implements SCOList, Cloneable
{
    protected static final Localiser LOCALISER;
    protected transient ObjectProvider ownerOP;
    protected transient AbstractMemberMetaData ownerMmd;
    protected java.util.ArrayList delegate;
    
    public ArrayList(final ObjectProvider ownerOP, final AbstractMemberMetaData mmd) {
        super(0);
        this.ownerOP = ownerOP;
        this.ownerMmd = mmd;
    }
    
    @Override
    public void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        final Collection c = (Collection)o;
        if (c != null) {
            this.delegate = (java.util.ArrayList)c;
        }
        else {
            this.delegate = new java.util.ArrayList();
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ArrayList.LOCALISER.msg("023003", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + this.size(), SCOUtils.getSCOWrapperOptionsMessage(true, false, true, false)));
        }
    }
    
    @Override
    public void initialise() {
        this.delegate = new java.util.ArrayList();
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ArrayList.LOCALISER.msg("023003", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + this.size(), SCOUtils.getSCOWrapperOptionsMessage(true, false, true, false)));
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
        final Collection detached = new java.util.ArrayList();
        SCOUtils.detachCopyForCollection(this.ownerOP, this.toArray(), state, detached);
        return detached;
    }
    
    @Override
    public void attachCopy(final Object value) {
        final Collection c = (Collection)value;
        final boolean elementsWithoutIdentity = SCOUtils.collectionHasElementsWithoutIdentity(this.ownerMmd);
        final List attachedElements = new java.util.ArrayList(c.size());
        SCOUtils.attachCopyForCollection(this.ownerOP, c.toArray(), attachedElements, elementsWithoutIdentity);
        SCOUtils.updateListWithListElements(this, attachedElements);
    }
    
    @Override
    public Object clone() {
        return this.delegate.clone();
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
    public Object get(final int index) {
        return this.delegate.get(index);
    }
    
    @Override
    public int indexOf(final Object element) {
        return this.delegate.indexOf(element);
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    @Override
    public Iterator iterator() {
        return new SCOListIterator(this, this.ownerOP, this.delegate, null, true, -1);
    }
    
    @Override
    public ListIterator listIterator() {
        return new SCOListIterator(this, this.ownerOP, this.delegate, null, true, -1);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        return new SCOListIterator(this, this.ownerOP, this.delegate, null, true, index);
    }
    
    @Override
    public int lastIndexOf(final Object element) {
        return this.delegate.lastIndexOf(element);
    }
    
    @Override
    public int size() {
        return this.delegate.size();
    }
    
    @Override
    public synchronized List subList(final int from, final int to) {
        return this.delegate.subList(from, to);
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
    public void add(final int index, final Object element) {
        this.delegate.add(index, element);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP).relationAdd(this.ownerMmd.getAbsoluteFieldNumber(), element);
        }
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
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
            final RelationshipManager relMgr = this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP);
            while (iter.hasNext()) {
                relMgr.relationAdd(this.ownerMmd.getAbsoluteFieldNumber(), iter.next());
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
    public boolean addAll(final int index, final Collection elements) {
        final boolean success = this.delegate.addAll(index, elements);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            final Iterator iter = elements.iterator();
            final RelationshipManager relMgr = this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP);
            while (iter.hasNext()) {
                relMgr.relationAdd(this.ownerMmd.getAbsoluteFieldNumber(), iter.next());
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
    public synchronized void clear() {
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            final Iterator iter = this.delegate.iterator();
            final RelationshipManager relMgr = this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP);
            while (iter.hasNext()) {
                relMgr.relationRemove(this.ownerMmd.getAbsoluteFieldNumber(), iter.next());
            }
        }
        if (this.ownerOP != null && !this.delegate.isEmpty() && SCOUtils.hasDependentElement(this.ownerMmd)) {
            final List copy = new java.util.ArrayList(this.delegate);
            final Iterator iter2 = copy.iterator();
            while (iter2.hasNext()) {
                this.ownerOP.getExecutionContext().deleteObjectInternal(iter2.next());
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
    public synchronized Object remove(final int index) {
        final Object element = this.delegate.remove(index);
        if (this.ownerOP != null && this.ownerOP.getExecutionContext().getManageRelations()) {
            this.ownerOP.getExecutionContext().getRelationshipManager(this.ownerOP).relationRemove(this.ownerMmd.getAbsoluteFieldNumber(), element);
        }
        if (this.ownerOP != null && SCOUtils.hasDependentElement(this.ownerMmd)) {
            this.ownerOP.getExecutionContext().deleteObjectInternal(element);
        }
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return element;
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
    
    @Override
    public Object set(final int index, final Object element, final boolean allowDependentField) {
        final Object obj = this.delegate.set(index, element);
        if (this.ownerOP != null && allowDependentField && !this.delegate.contains(obj) && SCOUtils.hasDependentElement(this.ownerMmd)) {
            this.ownerOP.getExecutionContext().deleteObjectInternal(obj);
        }
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return obj;
    }
    
    @Override
    public Object set(final int index, final Object element) {
        return this.set(index, element, true);
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new java.util.ArrayList(this.delegate);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
