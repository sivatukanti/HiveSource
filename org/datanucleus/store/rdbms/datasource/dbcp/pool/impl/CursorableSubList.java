// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class CursorableSubList extends CursorableLinkedList implements List
{
    protected CursorableLinkedList _list;
    protected Listable _pre;
    protected Listable _post;
    
    CursorableSubList(final CursorableLinkedList list, final int from, final int to) {
        this._list = null;
        this._pre = null;
        this._post = null;
        if (0 > from || list.size() < to) {
            throw new IndexOutOfBoundsException();
        }
        if (from > to) {
            throw new IllegalArgumentException();
        }
        this._list = list;
        if (from < list.size()) {
            this._head.setNext(this._list.getListableAt(from));
            this._pre = ((null == this._head.next()) ? null : this._head.next().prev());
        }
        else {
            this._pre = this._list.getListableAt(from - 1);
        }
        if (from == to) {
            this._head.setNext(null);
            this._head.setPrev(null);
            if (to < list.size()) {
                this._post = this._list.getListableAt(to);
            }
            else {
                this._post = null;
            }
        }
        else {
            this._head.setPrev(this._list.getListableAt(to - 1));
            this._post = this._head.prev().next();
        }
        this._size = to - from;
        this._modCount = this._list._modCount;
    }
    
    @Override
    public void clear() {
        this.checkForComod();
        final Iterator it = this.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
    
    @Override
    public Iterator iterator() {
        this.checkForComod();
        return super.iterator();
    }
    
    @Override
    public int size() {
        this.checkForComod();
        return super.size();
    }
    
    @Override
    public boolean isEmpty() {
        this.checkForComod();
        return super.isEmpty();
    }
    
    @Override
    public Object[] toArray() {
        this.checkForComod();
        return super.toArray();
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        this.checkForComod();
        return super.toArray(a);
    }
    
    @Override
    public boolean contains(final Object o) {
        this.checkForComod();
        return super.contains(o);
    }
    
    @Override
    public boolean remove(final Object o) {
        this.checkForComod();
        return super.remove(o);
    }
    
    @Override
    public Object removeFirst() {
        this.checkForComod();
        return super.removeFirst();
    }
    
    @Override
    public Object removeLast() {
        this.checkForComod();
        return super.removeLast();
    }
    
    @Override
    public boolean addAll(final Collection c) {
        this.checkForComod();
        return super.addAll(c);
    }
    
    @Override
    public boolean add(final Object o) {
        this.checkForComod();
        return super.add(o);
    }
    
    @Override
    public boolean addFirst(final Object o) {
        this.checkForComod();
        return super.addFirst(o);
    }
    
    @Override
    public boolean addLast(final Object o) {
        this.checkForComod();
        return super.addLast(o);
    }
    
    @Override
    public boolean removeAll(final Collection c) {
        this.checkForComod();
        return super.removeAll(c);
    }
    
    @Override
    public boolean containsAll(final Collection c) {
        this.checkForComod();
        return super.containsAll(c);
    }
    
    @Override
    public boolean addAll(final int index, final Collection c) {
        this.checkForComod();
        return super.addAll(index, c);
    }
    
    @Override
    public int hashCode() {
        this.checkForComod();
        return super.hashCode();
    }
    
    @Override
    public boolean retainAll(final Collection c) {
        this.checkForComod();
        return super.retainAll(c);
    }
    
    @Override
    public Object set(final int index, final Object element) {
        this.checkForComod();
        return super.set(index, element);
    }
    
    @Override
    public boolean equals(final Object o) {
        this.checkForComod();
        return super.equals(o);
    }
    
    @Override
    public Object get(final int index) {
        this.checkForComod();
        return super.get(index);
    }
    
    @Override
    public Object getFirst() {
        this.checkForComod();
        return super.getFirst();
    }
    
    @Override
    public Object getLast() {
        this.checkForComod();
        return super.getLast();
    }
    
    @Override
    public void add(final int index, final Object element) {
        this.checkForComod();
        super.add(index, element);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        this.checkForComod();
        return super.listIterator(index);
    }
    
    @Override
    public Object remove(final int index) {
        this.checkForComod();
        return super.remove(index);
    }
    
    @Override
    public int indexOf(final Object o) {
        this.checkForComod();
        return super.indexOf(o);
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        this.checkForComod();
        return super.lastIndexOf(o);
    }
    
    @Override
    public ListIterator listIterator() {
        this.checkForComod();
        return super.listIterator();
    }
    
    @Override
    public List subList(final int fromIndex, final int toIndex) {
        this.checkForComod();
        return super.subList(fromIndex, toIndex);
    }
    
    @Override
    protected Listable insertListable(final Listable before, final Listable after, final Object value) {
        ++this._modCount;
        ++this._size;
        final Listable elt = this._list.insertListable((null == before) ? this._pre : before, (null == after) ? this._post : after, value);
        if (null == this._head.next()) {
            this._head.setNext(elt);
            this._head.setPrev(elt);
        }
        if (before == this._head.prev()) {
            this._head.setPrev(elt);
        }
        if (after == this._head.next()) {
            this._head.setNext(elt);
        }
        this.broadcastListableInserted(elt);
        return elt;
    }
    
    @Override
    protected void removeListable(final Listable elt) {
        ++this._modCount;
        --this._size;
        if (this._head.next() == elt && this._head.prev() == elt) {
            this._head.setNext(null);
            this._head.setPrev(null);
        }
        if (this._head.next() == elt) {
            this._head.setNext(elt.next());
        }
        if (this._head.prev() == elt) {
            this._head.setPrev(elt.prev());
        }
        this._list.removeListable(elt);
        this.broadcastListableRemoved(elt);
    }
    
    protected void checkForComod() throws ConcurrentModificationException {
        if (this._modCount != this._list._modCount) {
            throw new ConcurrentModificationException();
        }
    }
}
