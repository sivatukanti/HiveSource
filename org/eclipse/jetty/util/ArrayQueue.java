// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.AbstractList;

public class ArrayQueue<E> extends AbstractList<E> implements Queue<E>
{
    public static final int DEFAULT_CAPACITY = 64;
    public static final int DEFAULT_GROWTH = 32;
    protected final Object _lock;
    protected final int _growCapacity;
    protected Object[] _elements;
    protected int _nextE;
    protected int _nextSlot;
    protected int _size;
    
    public ArrayQueue() {
        this(64, -1);
    }
    
    public ArrayQueue(final Object lock) {
        this(64, -1, lock);
    }
    
    public ArrayQueue(final int capacity) {
        this(capacity, -1);
    }
    
    public ArrayQueue(final int initCapacity, final int growBy) {
        this(initCapacity, growBy, null);
    }
    
    public ArrayQueue(final int initCapacity, final int growBy, final Object lock) {
        this._lock = ((lock == null) ? this : lock);
        this._growCapacity = growBy;
        this._elements = new Object[initCapacity];
    }
    
    public Object lock() {
        return this._lock;
    }
    
    public int getCapacity() {
        synchronized (this._lock) {
            return this._elements.length;
        }
    }
    
    public int getNextSlotUnsafe() {
        return this._nextSlot;
    }
    
    @Override
    public boolean add(final E e) {
        if (!this.offer(e)) {
            throw new IllegalStateException("Full");
        }
        return true;
    }
    
    @Override
    public boolean offer(final E e) {
        synchronized (this._lock) {
            return this.enqueue(e);
        }
    }
    
    protected boolean enqueue(final E e) {
        if (this._size == this._elements.length && !this.growUnsafe()) {
            return false;
        }
        ++this._size;
        this._elements[this._nextSlot++] = e;
        if (this._nextSlot == this._elements.length) {
            this._nextSlot = 0;
        }
        return true;
    }
    
    public void addUnsafe(final E e) {
        if (!this.enqueue(e)) {
            throw new IllegalStateException("Full");
        }
    }
    
    @Override
    public E element() {
        synchronized (this._lock) {
            if (this.isEmpty()) {
                throw new NoSuchElementException();
            }
            return this.at(this._nextE);
        }
    }
    
    private E at(final int index) {
        return (E)this._elements[index];
    }
    
    @Override
    public E peek() {
        synchronized (this._lock) {
            if (this._size == 0) {
                return null;
            }
            return this.at(this._nextE);
        }
    }
    
    public E peekUnsafe() {
        if (this._size == 0) {
            return null;
        }
        return this.at(this._nextE);
    }
    
    @Override
    public E poll() {
        synchronized (this._lock) {
            if (this._size == 0) {
                return null;
            }
            return this.dequeue();
        }
    }
    
    public E pollUnsafe() {
        if (this._size == 0) {
            return null;
        }
        return this.dequeue();
    }
    
    protected E dequeue() {
        final E e = this.at(this._nextE);
        this._elements[this._nextE] = null;
        --this._size;
        if (++this._nextE == this._elements.length) {
            this._nextE = 0;
        }
        return e;
    }
    
    @Override
    public E remove() {
        synchronized (this._lock) {
            if (this._size == 0) {
                throw new NoSuchElementException();
            }
            return this.dequeue();
        }
    }
    
    @Override
    public void clear() {
        synchronized (this._lock) {
            this._size = 0;
            this._nextE = 0;
            this._nextSlot = 0;
        }
    }
    
    @Override
    public boolean isEmpty() {
        synchronized (this._lock) {
            return this._size == 0;
        }
    }
    
    @Override
    public int size() {
        synchronized (this._lock) {
            return this._size;
        }
    }
    
    public int sizeUnsafe() {
        return this._size;
    }
    
    @Override
    public E get(final int index) {
        synchronized (this._lock) {
            if (index < 0 || index >= this._size) {
                throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
            }
            return this.getUnsafe(index);
        }
    }
    
    public E getUnsafe(final int index) {
        final int i = (this._nextE + index) % this._elements.length;
        return this.at(i);
    }
    
    @Override
    public E remove(final int index) {
        synchronized (this._lock) {
            if (index < 0 || index >= this._size) {
                throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
            }
            final int i = (this._nextE + index) % this._elements.length;
            final E old = this.at(i);
            if (i < this._nextSlot) {
                System.arraycopy(this._elements, i + 1, this._elements, i, this._nextSlot - i);
                --this._nextSlot;
                --this._size;
            }
            else {
                System.arraycopy(this._elements, i + 1, this._elements, i, this._elements.length - i - 1);
                if (this._nextSlot > 0) {
                    this._elements[this._elements.length - 1] = this._elements[0];
                    System.arraycopy(this._elements, 1, this._elements, 0, this._nextSlot - 1);
                    --this._nextSlot;
                }
                else {
                    this._nextSlot = this._elements.length - 1;
                }
                --this._size;
            }
            return old;
        }
    }
    
    @Override
    public E set(final int index, final E element) {
        synchronized (this._lock) {
            if (index < 0 || index >= this._size) {
                throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
            }
            int i = this._nextE + index;
            if (i >= this._elements.length) {
                i -= this._elements.length;
            }
            final E old = this.at(i);
            this._elements[i] = element;
            return old;
        }
    }
    
    @Override
    public void add(final int index, final E element) {
        synchronized (this._lock) {
            if (index < 0 || index > this._size) {
                throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
            }
            if (this._size == this._elements.length && !this.growUnsafe()) {
                throw new IllegalStateException("Full");
            }
            if (index == this._size) {
                this.add(element);
            }
            else {
                int i = this._nextE + index;
                if (i >= this._elements.length) {
                    i -= this._elements.length;
                }
                ++this._size;
                ++this._nextSlot;
                if (this._nextSlot == this._elements.length) {
                    this._nextSlot = 0;
                }
                if (i < this._nextSlot) {
                    System.arraycopy(this._elements, i, this._elements, i + 1, this._nextSlot - i);
                    this._elements[i] = element;
                }
                else {
                    if (this._nextSlot > 0) {
                        System.arraycopy(this._elements, 0, this._elements, 1, this._nextSlot);
                        this._elements[0] = this._elements[this._elements.length - 1];
                    }
                    System.arraycopy(this._elements, i, this._elements, i + 1, this._elements.length - i - 1);
                    this._elements[i] = element;
                }
            }
        }
    }
    
    protected void resizeUnsafe(int newCapacity) {
        newCapacity = Math.max(newCapacity, this._size);
        final Object[] elements = new Object[newCapacity];
        if (this._size > 0) {
            if (this._nextSlot > this._nextE) {
                System.arraycopy(this._elements, this._nextE, elements, 0, this._size);
            }
            else {
                final int split = this._elements.length - this._nextE;
                System.arraycopy(this._elements, this._nextE, elements, 0, split);
                System.arraycopy(this._elements, 0, elements, split, this._nextSlot);
            }
        }
        this._elements = elements;
        this._nextE = 0;
        this._nextSlot = this._size;
    }
    
    protected boolean growUnsafe() {
        if (this._growCapacity <= 0) {
            return false;
        }
        this.resizeUnsafe(this._elements.length + this._growCapacity);
        return true;
    }
}
