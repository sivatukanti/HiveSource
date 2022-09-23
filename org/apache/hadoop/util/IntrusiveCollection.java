// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.NoSuchElementException;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.Collection;

@InterfaceAudience.Private
public class IntrusiveCollection<E extends Element> implements Collection<E>
{
    private Element root;
    private int size;
    public static final Logger LOG;
    
    public IntrusiveCollection() {
        this.root = new Element() {
            Element first = this;
            Element last = this;
            
            @Override
            public void insertInternal(final IntrusiveCollection<? extends Element> list, final Element prev, final Element next) {
                throw new RuntimeException("Can't insert root element");
            }
            
            @Override
            public void setPrev(final IntrusiveCollection<? extends Element> list, final Element prev) {
                Preconditions.checkState(list == IntrusiveCollection.this);
                this.last = prev;
            }
            
            @Override
            public void setNext(final IntrusiveCollection<? extends Element> list, final Element next) {
                Preconditions.checkState(list == IntrusiveCollection.this);
                this.first = next;
            }
            
            @Override
            public void removeInternal(final IntrusiveCollection<? extends Element> list) {
                throw new RuntimeException("Can't remove root element");
            }
            
            @Override
            public Element getNext(final IntrusiveCollection<? extends Element> list) {
                Preconditions.checkState(list == IntrusiveCollection.this);
                return this.first;
            }
            
            @Override
            public Element getPrev(final IntrusiveCollection<? extends Element> list) {
                Preconditions.checkState(list == IntrusiveCollection.this);
                return this.last;
            }
            
            @Override
            public boolean isInList(final IntrusiveCollection<? extends Element> list) {
                return list == IntrusiveCollection.this;
            }
            
            @Override
            public String toString() {
                return "root";
            }
        };
        this.size = 0;
    }
    
    private Element removeElement(final Element elem) {
        final Element prev = elem.getPrev(this);
        final Element next = elem.getNext(this);
        elem.removeInternal(this);
        prev.setNext(this, next);
        next.setPrev(this, prev);
        --this.size;
        return next;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new IntrusiveIterator();
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public boolean contains(final Object o) {
        try {
            final Element element = (Element)o;
            return element.isInList(this);
        }
        catch (ClassCastException e) {
            return false;
        }
    }
    
    @Override
    public Object[] toArray() {
        final Object[] ret = new Object[this.size];
        int i = 0;
        final Iterator<E> iter = this.iterator();
        while (iter.hasNext()) {
            ret[i++] = iter.next();
        }
        return ret;
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        if (array.length < this.size) {
            return (T[])this.toArray();
        }
        int i = 0;
        final Iterator<E> iter = this.iterator();
        while (iter.hasNext()) {
            array[i++] = (T)iter.next();
        }
        return array;
    }
    
    @Override
    public boolean add(final E elem) {
        if (elem == null) {
            return false;
        }
        if (elem.isInList(this)) {
            return false;
        }
        final Element prev = this.root.getPrev(this);
        prev.setNext(this, elem);
        this.root.setPrev(this, elem);
        elem.insertInternal(this, prev, this.root);
        ++this.size;
        return true;
    }
    
    public boolean addFirst(final Element elem) {
        if (elem == null) {
            return false;
        }
        if (elem.isInList(this)) {
            return false;
        }
        final Element next = this.root.getNext(this);
        next.setPrev(this, elem);
        this.root.setNext(this, elem);
        elem.insertInternal(this, this.root, next);
        ++this.size;
        return true;
    }
    
    @Override
    public boolean remove(final Object o) {
        try {
            final Element elem = (Element)o;
            if (!elem.isInList(this)) {
                return false;
            }
            this.removeElement(elem);
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        for (final Object o : collection) {
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        boolean changed = false;
        for (final E elem : collection) {
            if (this.add(elem)) {
                changed = true;
            }
        }
        return changed;
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        boolean changed = false;
        for (final Object elem : collection) {
            if (this.remove(elem)) {
                changed = true;
            }
        }
        return changed;
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        boolean changed = false;
        final Iterator<E> iter = this.iterator();
        while (iter.hasNext()) {
            final Element elem = iter.next();
            if (!collection.contains(elem)) {
                iter.remove();
                changed = true;
            }
        }
        return changed;
    }
    
    @Override
    public void clear() {
        final Iterator<E> iter = this.iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(IntrusiveCollection.class);
    }
    
    public class IntrusiveIterator implements Iterator<E>
    {
        Element cur;
        Element next;
        
        IntrusiveIterator() {
            this.cur = IntrusiveCollection.this.root;
            this.next = null;
        }
        
        @Override
        public boolean hasNext() {
            if (this.next == null) {
                this.next = this.cur.getNext(IntrusiveCollection.this);
            }
            return this.next != IntrusiveCollection.this.root;
        }
        
        @Override
        public E next() {
            if (this.next == null) {
                this.next = this.cur.getNext(IntrusiveCollection.this);
            }
            if (this.next == IntrusiveCollection.this.root) {
                throw new NoSuchElementException();
            }
            this.cur = this.next;
            this.next = null;
            return (E)this.cur;
        }
        
        @Override
        public void remove() {
            if (this.cur == null) {
                throw new IllegalStateException("Already called remove once on this element.");
            }
            this.next = IntrusiveCollection.this.removeElement(this.cur);
            this.cur = null;
        }
    }
    
    @InterfaceAudience.Private
    public interface Element
    {
        void insertInternal(final IntrusiveCollection<? extends Element> p0, final Element p1, final Element p2);
        
        void setPrev(final IntrusiveCollection<? extends Element> p0, final Element p1);
        
        void setNext(final IntrusiveCollection<? extends Element> p0, final Element p1);
        
        void removeInternal(final IntrusiveCollection<? extends Element> p0);
        
        Element getPrev(final IntrusiveCollection<? extends Element> p0);
        
        Element getNext(final IntrusiveCollection<? extends Element> p0);
        
        boolean isInList(final IntrusiveCollection<? extends Element> p0);
    }
}
