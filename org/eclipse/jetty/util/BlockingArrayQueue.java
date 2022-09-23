// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.ListIterator;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.Objects;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.BlockingQueue;
import java.util.AbstractList;

public class BlockingArrayQueue<E> extends AbstractList<E> implements BlockingQueue<E>
{
    private static final int HEAD_OFFSET;
    private static final int TAIL_OFFSET;
    public static final int DEFAULT_CAPACITY = 128;
    public static final int DEFAULT_GROWTH = 64;
    private final int _maxCapacity;
    private final int _growCapacity;
    private final int[] _indexes;
    private final Lock _tailLock;
    private final AtomicInteger _size;
    private final Lock _headLock;
    private final Condition _notEmpty;
    private Object[] _elements;
    
    public BlockingArrayQueue() {
        this._indexes = new int[BlockingArrayQueue.TAIL_OFFSET + 1];
        this._tailLock = new ReentrantLock();
        this._size = new AtomicInteger();
        this._headLock = new ReentrantLock();
        this._notEmpty = this._headLock.newCondition();
        this._elements = new Object[128];
        this._growCapacity = 64;
        this._maxCapacity = Integer.MAX_VALUE;
    }
    
    public BlockingArrayQueue(final int maxCapacity) {
        this._indexes = new int[BlockingArrayQueue.TAIL_OFFSET + 1];
        this._tailLock = new ReentrantLock();
        this._size = new AtomicInteger();
        this._headLock = new ReentrantLock();
        this._notEmpty = this._headLock.newCondition();
        this._elements = new Object[maxCapacity];
        this._growCapacity = -1;
        this._maxCapacity = maxCapacity;
    }
    
    public BlockingArrayQueue(final int capacity, final int growBy) {
        this._indexes = new int[BlockingArrayQueue.TAIL_OFFSET + 1];
        this._tailLock = new ReentrantLock();
        this._size = new AtomicInteger();
        this._headLock = new ReentrantLock();
        this._notEmpty = this._headLock.newCondition();
        this._elements = new Object[capacity];
        this._growCapacity = growBy;
        this._maxCapacity = Integer.MAX_VALUE;
    }
    
    public BlockingArrayQueue(final int capacity, final int growBy, final int maxCapacity) {
        this._indexes = new int[BlockingArrayQueue.TAIL_OFFSET + 1];
        this._tailLock = new ReentrantLock();
        this._size = new AtomicInteger();
        this._headLock = new ReentrantLock();
        this._notEmpty = this._headLock.newCondition();
        if (capacity > maxCapacity) {
            throw new IllegalArgumentException();
        }
        this._elements = new Object[capacity];
        this._growCapacity = growBy;
        this._maxCapacity = maxCapacity;
    }
    
    @Override
    public void clear() {
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                this._indexes[BlockingArrayQueue.HEAD_OFFSET] = 0;
                this._indexes[BlockingArrayQueue.TAIL_OFFSET] = 0;
                this._size.set(0);
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    @Override
    public int size() {
        return this._size.get();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.listIterator();
    }
    
    @Override
    public E poll() {
        if (this._size.get() == 0) {
            return null;
        }
        E e = null;
        this._headLock.lock();
        try {
            if (this._size.get() > 0) {
                final int head = this._indexes[BlockingArrayQueue.HEAD_OFFSET];
                e = (E)this._elements[head];
                this._elements[head] = null;
                this._indexes[BlockingArrayQueue.HEAD_OFFSET] = (head + 1) % this._elements.length;
                if (this._size.decrementAndGet() > 0) {
                    this._notEmpty.signal();
                }
            }
        }
        finally {
            this._headLock.unlock();
        }
        return e;
    }
    
    @Override
    public E peek() {
        if (this._size.get() == 0) {
            return null;
        }
        E e = null;
        this._headLock.lock();
        try {
            if (this._size.get() > 0) {
                e = (E)this._elements[this._indexes[BlockingArrayQueue.HEAD_OFFSET]];
            }
        }
        finally {
            this._headLock.unlock();
        }
        return e;
    }
    
    @Override
    public E remove() {
        final E e = this.poll();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }
    
    @Override
    public E element() {
        final E e = this.peek();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }
    
    @Override
    public boolean offer(final E e) {
        Objects.requireNonNull(e);
        boolean notEmpty = false;
        this._tailLock.lock();
        try {
            final int size = this._size.get();
            if (size >= this._maxCapacity) {
                return false;
            }
            if (size == this._elements.length) {
                this._headLock.lock();
                try {
                    if (!this.grow()) {
                        return false;
                    }
                }
                finally {
                    this._headLock.unlock();
                }
            }
            final int tail = this._indexes[BlockingArrayQueue.TAIL_OFFSET];
            this._elements[tail] = e;
            this._indexes[BlockingArrayQueue.TAIL_OFFSET] = (tail + 1) % this._elements.length;
            notEmpty = (this._size.getAndIncrement() == 0);
        }
        finally {
            this._tailLock.unlock();
        }
        if (notEmpty) {
            this._headLock.lock();
            try {
                this._notEmpty.signal();
            }
            finally {
                this._headLock.unlock();
            }
        }
        return true;
    }
    
    @Override
    public boolean add(final E e) {
        if (this.offer(e)) {
            return true;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public void put(final E o) throws InterruptedException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean offer(final E o, final long timeout, final TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E take() throws InterruptedException {
        E e = null;
        this._headLock.lockInterruptibly();
        try {
            try {
                while (this._size.get() == 0) {
                    this._notEmpty.await();
                }
            }
            catch (InterruptedException ie) {
                this._notEmpty.signal();
                throw ie;
            }
            final int head = this._indexes[BlockingArrayQueue.HEAD_OFFSET];
            e = (E)this._elements[head];
            this._elements[head] = null;
            this._indexes[BlockingArrayQueue.HEAD_OFFSET] = (head + 1) % this._elements.length;
            if (this._size.decrementAndGet() > 0) {
                this._notEmpty.signal();
            }
        }
        finally {
            this._headLock.unlock();
        }
        return e;
    }
    
    @Override
    public E poll(final long time, final TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(time);
        E e = null;
        this._headLock.lockInterruptibly();
        try {
            try {
                while (this._size.get() == 0) {
                    if (nanos <= 0L) {
                        return null;
                    }
                    nanos = this._notEmpty.awaitNanos(nanos);
                }
            }
            catch (InterruptedException x) {
                this._notEmpty.signal();
                throw x;
            }
            final int head = this._indexes[BlockingArrayQueue.HEAD_OFFSET];
            e = (E)this._elements[head];
            this._elements[head] = null;
            this._indexes[BlockingArrayQueue.HEAD_OFFSET] = (head + 1) % this._elements.length;
            if (this._size.decrementAndGet() > 0) {
                this._notEmpty.signal();
            }
        }
        finally {
            this._headLock.unlock();
        }
        return e;
    }
    
    @Override
    public boolean remove(final Object o) {
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                if (this.isEmpty()) {
                    return false;
                }
                final int head = this._indexes[BlockingArrayQueue.HEAD_OFFSET];
                final int tail = this._indexes[BlockingArrayQueue.TAIL_OFFSET];
                final int capacity = this._elements.length;
                int i = head;
                while (!Objects.equals(this._elements[i], o)) {
                    if (++i == capacity) {
                        i = 0;
                    }
                    if (i == tail) {
                        return false;
                    }
                }
                this.remove((i >= head) ? (i - head) : (capacity - head + i));
                return true;
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    @Override
    public int remainingCapacity() {
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                return this.getCapacity() - this.size();
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    @Override
    public int drainTo(final Collection<? super E> c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int drainTo(final Collection<? super E> c, final int maxElements) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E get(final int index) {
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                if (index < 0 || index >= this._size.get()) {
                    throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
                }
                int i = this._indexes[BlockingArrayQueue.HEAD_OFFSET] + index;
                final int capacity = this._elements.length;
                if (i >= capacity) {
                    i -= capacity;
                }
                return (E)this._elements[i];
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    @Override
    public void add(final int index, final E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                final int size = this._size.get();
                if (index < 0 || index > size) {
                    throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
                }
                if (index == size) {
                    this.add(e);
                }
                else {
                    if (this._indexes[BlockingArrayQueue.TAIL_OFFSET] == this._indexes[BlockingArrayQueue.HEAD_OFFSET] && !this.grow()) {
                        throw new IllegalStateException("full");
                    }
                    int i = this._indexes[BlockingArrayQueue.HEAD_OFFSET] + index;
                    final int capacity = this._elements.length;
                    if (i >= capacity) {
                        i -= capacity;
                    }
                    this._size.incrementAndGet();
                    int tail = this._indexes[BlockingArrayQueue.TAIL_OFFSET];
                    tail = (this._indexes[BlockingArrayQueue.TAIL_OFFSET] = (tail + 1) % capacity);
                    if (i < tail) {
                        System.arraycopy(this._elements, i, this._elements, i + 1, tail - i);
                        this._elements[i] = e;
                    }
                    else {
                        if (tail > 0) {
                            System.arraycopy(this._elements, 0, this._elements, 1, tail);
                            this._elements[0] = this._elements[capacity - 1];
                        }
                        System.arraycopy(this._elements, i, this._elements, i + 1, capacity - i - 1);
                        this._elements[i] = e;
                    }
                }
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    @Override
    public E set(final int index, final E e) {
        Objects.requireNonNull(e);
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                if (index < 0 || index >= this._size.get()) {
                    throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
                }
                int i = this._indexes[BlockingArrayQueue.HEAD_OFFSET] + index;
                final int capacity = this._elements.length;
                if (i >= capacity) {
                    i -= capacity;
                }
                final E old = (E)this._elements[i];
                this._elements[i] = e;
                return old;
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    @Override
    public E remove(final int index) {
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                if (index < 0 || index >= this._size.get()) {
                    throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
                }
                int i = this._indexes[BlockingArrayQueue.HEAD_OFFSET] + index;
                final int capacity = this._elements.length;
                if (i >= capacity) {
                    i -= capacity;
                }
                final E old = (E)this._elements[i];
                final int tail = this._indexes[BlockingArrayQueue.TAIL_OFFSET];
                if (i < tail) {
                    System.arraycopy(this._elements, i + 1, this._elements, i, tail - i);
                    final int[] indexes = this._indexes;
                    final int tail_OFFSET = BlockingArrayQueue.TAIL_OFFSET;
                    --indexes[tail_OFFSET];
                }
                else {
                    System.arraycopy(this._elements, i + 1, this._elements, i, capacity - i - 1);
                    this._elements[capacity - 1] = this._elements[0];
                    if (tail > 0) {
                        System.arraycopy(this._elements, 1, this._elements, 0, tail);
                        final int[] indexes2 = this._indexes;
                        final int tail_OFFSET2 = BlockingArrayQueue.TAIL_OFFSET;
                        --indexes2[tail_OFFSET2];
                    }
                    else {
                        this._indexes[BlockingArrayQueue.TAIL_OFFSET] = capacity - 1;
                    }
                    this._elements[this._indexes[BlockingArrayQueue.TAIL_OFFSET]] = null;
                }
                this._size.decrementAndGet();
                return old;
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    @Override
    public ListIterator<E> listIterator(final int index) {
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                final Object[] elements = new Object[this.size()];
                if (this.size() > 0) {
                    final int head = this._indexes[BlockingArrayQueue.HEAD_OFFSET];
                    final int tail = this._indexes[BlockingArrayQueue.TAIL_OFFSET];
                    if (head < tail) {
                        System.arraycopy(this._elements, head, elements, 0, tail - head);
                    }
                    else {
                        final int chunk = this._elements.length - head;
                        System.arraycopy(this._elements, head, elements, 0, chunk);
                        System.arraycopy(this._elements, 0, elements, chunk, tail);
                    }
                }
                return new Itr(elements, index);
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    public int getCapacity() {
        this._tailLock.lock();
        try {
            return this._elements.length;
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    public int getMaxCapacity() {
        return this._maxCapacity;
    }
    
    private boolean grow() {
        if (this._growCapacity <= 0) {
            return false;
        }
        this._tailLock.lock();
        try {
            this._headLock.lock();
            try {
                final int head = this._indexes[BlockingArrayQueue.HEAD_OFFSET];
                final int tail = this._indexes[BlockingArrayQueue.TAIL_OFFSET];
                final int capacity = this._elements.length;
                final Object[] elements = new Object[capacity + this._growCapacity];
                int newTail;
                if (head < tail) {
                    newTail = tail - head;
                    System.arraycopy(this._elements, head, elements, 0, newTail);
                }
                else if (head > tail || this._size.get() > 0) {
                    newTail = capacity + tail - head;
                    final int cut = capacity - head;
                    System.arraycopy(this._elements, head, elements, 0, cut);
                    System.arraycopy(this._elements, 0, elements, cut, tail);
                }
                else {
                    newTail = 0;
                }
                this._elements = elements;
                this._indexes[BlockingArrayQueue.HEAD_OFFSET] = 0;
                this._indexes[BlockingArrayQueue.TAIL_OFFSET] = newTail;
                return true;
            }
            finally {
                this._headLock.unlock();
            }
        }
        finally {
            this._tailLock.unlock();
        }
    }
    
    static {
        HEAD_OFFSET = MemoryUtils.getIntegersPerCacheLine() - 1;
        TAIL_OFFSET = BlockingArrayQueue.HEAD_OFFSET + MemoryUtils.getIntegersPerCacheLine();
    }
    
    private class Itr implements ListIterator<E>
    {
        private final Object[] _elements;
        private int _cursor;
        
        public Itr(final Object[] elements, final int offset) {
            this._elements = elements;
            this._cursor = offset;
        }
        
        @Override
        public boolean hasNext() {
            return this._cursor < this._elements.length;
        }
        
        @Override
        public E next() {
            return (E)this._elements[this._cursor++];
        }
        
        @Override
        public boolean hasPrevious() {
            return this._cursor > 0;
        }
        
        @Override
        public E previous() {
            final Object[] elements = this._elements;
            final int cursor = this._cursor - 1;
            this._cursor = cursor;
            return (E)elements[cursor];
        }
        
        @Override
        public int nextIndex() {
            return this._cursor + 1;
        }
        
        @Override
        public int previousIndex() {
            return this._cursor - 1;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void set(final E e) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final E e) {
            throw new UnsupportedOperationException();
        }
    }
}
