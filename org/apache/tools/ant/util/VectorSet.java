// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

public final class VectorSet<E> extends Vector<E>
{
    private static final long serialVersionUID = 1L;
    private final HashSet<E> set;
    
    public VectorSet() {
        this.set = new HashSet<E>();
    }
    
    public VectorSet(final int initialCapacity) {
        super(initialCapacity);
        this.set = new HashSet<E>();
    }
    
    public VectorSet(final int initialCapacity, final int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
        this.set = new HashSet<E>();
    }
    
    public VectorSet(final Collection<? extends E> c) {
        this.set = new HashSet<E>();
        if (c != null) {
            for (final E e : c) {
                this.add(e);
            }
        }
    }
    
    @Override
    public synchronized boolean add(final E o) {
        if (!this.set.contains(o)) {
            this.doAdd(this.size(), o);
            return true;
        }
        return false;
    }
    
    @Override
    public void add(final int index, final E o) {
        this.doAdd(index, o);
    }
    
    private synchronized void doAdd(final int index, final E o) {
        if (this.set.add(o)) {
            final int count = this.size();
            this.ensureCapacity(count + 1);
            if (index != count) {
                System.arraycopy(this.elementData, index, this.elementData, index + 1, count - index);
            }
            this.elementData[index] = o;
            ++this.elementCount;
        }
    }
    
    @Override
    public synchronized void addElement(final E o) {
        this.doAdd(this.size(), o);
    }
    
    @Override
    public synchronized boolean addAll(final Collection<? extends E> c) {
        boolean changed = false;
        for (final E e : c) {
            changed |= this.add(e);
        }
        return changed;
    }
    
    @Override
    public synchronized boolean addAll(int index, final Collection<? extends E> c) {
        boolean changed = false;
        for (final E e : c) {
            if (!this.set.contains(e)) {
                this.doAdd(index++, e);
                changed = true;
            }
        }
        return changed;
    }
    
    @Override
    public synchronized void clear() {
        super.clear();
        this.set.clear();
    }
    
    @Override
    public Object clone() {
        final VectorSet<E> vs = (VectorSet<E>)super.clone();
        vs.set.addAll((Collection<?>)this.set);
        return vs;
    }
    
    @Override
    public synchronized boolean contains(final Object o) {
        return this.set.contains(o);
    }
    
    @Override
    public synchronized boolean containsAll(final Collection<?> c) {
        return this.set.containsAll(c);
    }
    
    @Override
    public void insertElementAt(final E o, final int index) {
        this.doAdd(index, o);
    }
    
    @Override
    public synchronized E remove(final int index) {
        final E o = this.get(index);
        this.remove(o);
        return o;
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.doRemove(o);
    }
    
    private synchronized boolean doRemove(final Object o) {
        if (this.set.remove(o)) {
            final int index = this.indexOf(o);
            if (index < this.elementData.length - 1) {
                System.arraycopy(this.elementData, index + 1, this.elementData, index, this.elementData.length - index - 1);
            }
            --this.elementCount;
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized boolean removeAll(final Collection<?> c) {
        boolean changed = false;
        for (final Object o : c) {
            changed |= this.remove(o);
        }
        return changed;
    }
    
    @Override
    public synchronized void removeAllElements() {
        this.set.clear();
        super.removeAllElements();
    }
    
    @Override
    public boolean removeElement(final Object o) {
        return this.doRemove(o);
    }
    
    @Override
    public synchronized void removeElementAt(final int index) {
        this.remove(this.get(index));
    }
    
    public synchronized void removeRange(final int fromIndex, int toIndex) {
        while (toIndex > fromIndex) {
            this.remove(--toIndex);
        }
    }
    
    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        if (!(c instanceof Set)) {
            c = new HashSet<Object>(c);
        }
        final LinkedList<E> l = new LinkedList<E>();
        for (final E o : this) {
            if (!c.contains(o)) {
                l.addLast(o);
            }
        }
        if (!l.isEmpty()) {
            this.removeAll(l);
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized E set(final int index, final E o) {
        final E orig = this.get(index);
        if (this.set.add(o)) {
            this.elementData[index] = o;
            this.set.remove(orig);
        }
        else {
            final int oldIndexOfO = this.indexOf(o);
            this.remove(o);
            this.remove(orig);
            this.add((oldIndexOfO > index) ? index : (index - 1), o);
        }
        return orig;
    }
    
    @Override
    public void setElementAt(final E o, final int index) {
        this.set(index, o);
    }
}
