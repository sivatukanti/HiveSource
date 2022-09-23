// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.AbstractQueue;

public class ConcurrentArrayQueue<T> extends AbstractQueue<T>
{
    public static final int DEFAULT_BLOCK_SIZE = 512;
    public static final Object REMOVED_ELEMENT;
    private static final int HEAD_OFFSET;
    private static final int TAIL_OFFSET;
    private final AtomicReferenceArray<Block<T>> _blocks;
    private final int _blockSize;
    
    public ConcurrentArrayQueue() {
        this(512);
    }
    
    public ConcurrentArrayQueue(final int blockSize) {
        this._blocks = new AtomicReferenceArray<Block<T>>(ConcurrentArrayQueue.TAIL_OFFSET + 1);
        this._blockSize = blockSize;
        final Block<T> block = this.newBlock();
        this._blocks.set(ConcurrentArrayQueue.HEAD_OFFSET, block);
        this._blocks.set(ConcurrentArrayQueue.TAIL_OFFSET, block);
    }
    
    public int getBlockSize() {
        return this._blockSize;
    }
    
    protected Block<T> getHeadBlock() {
        return this._blocks.get(ConcurrentArrayQueue.HEAD_OFFSET);
    }
    
    protected Block<T> getTailBlock() {
        return this._blocks.get(ConcurrentArrayQueue.TAIL_OFFSET);
    }
    
    @Override
    public boolean offer(T item) {
        item = Objects.requireNonNull(item);
        Block<T> currentTailBlock;
        final Block<T> initialTailBlock = currentTailBlock = this.getTailBlock();
        int tail = currentTailBlock.tail();
        while (true) {
            if (tail == this.getBlockSize()) {
                Block<T> nextTailBlock = currentTailBlock.next();
                if (nextTailBlock == null) {
                    nextTailBlock = this.newBlock();
                    if (currentTailBlock.link(nextTailBlock)) {
                        currentTailBlock = nextTailBlock;
                    }
                    else {
                        currentTailBlock = currentTailBlock.next();
                    }
                }
                else {
                    currentTailBlock = nextTailBlock;
                }
                tail = currentTailBlock.tail();
            }
            else if (currentTailBlock.peek(tail) == null) {
                if (currentTailBlock.store(tail, item)) {
                    break;
                }
                ++tail;
            }
            else {
                ++tail;
            }
        }
        this.updateTailBlock(initialTailBlock, currentTailBlock);
        return true;
    }
    
    private void updateTailBlock(final Block<T> oldTailBlock, final Block<T> newTailBlock) {
        if (oldTailBlock != newTailBlock) {
            this.casTailBlock(oldTailBlock, newTailBlock);
        }
    }
    
    protected boolean casTailBlock(final Block<T> current, final Block<T> update) {
        return this._blocks.compareAndSet(ConcurrentArrayQueue.TAIL_OFFSET, current, update);
    }
    
    @Override
    public T poll() {
        Block<T> currentHeadBlock;
        final Block<T> initialHeadBlock = currentHeadBlock = this.getHeadBlock();
        int head = currentHeadBlock.head();
        T result = null;
        while (true) {
            if (head == this.getBlockSize()) {
                final Block<T> nextHeadBlock = currentHeadBlock.next();
                if (nextHeadBlock == null) {
                    break;
                }
                currentHeadBlock = nextHeadBlock;
                head = currentHeadBlock.head();
            }
            else {
                final Object element = currentHeadBlock.peek(head);
                if (element == ConcurrentArrayQueue.REMOVED_ELEMENT) {
                    ++head;
                }
                else {
                    result = (T)element;
                    if (result == null) {
                        break;
                    }
                    if (currentHeadBlock.remove(head, result, true)) {
                        break;
                    }
                    ++head;
                }
            }
        }
        this.updateHeadBlock(initialHeadBlock, currentHeadBlock);
        return result;
    }
    
    private void updateHeadBlock(final Block<T> oldHeadBlock, final Block<T> newHeadBlock) {
        if (oldHeadBlock != newHeadBlock) {
            this.casHeadBlock(oldHeadBlock, newHeadBlock);
        }
    }
    
    protected boolean casHeadBlock(final Block<T> current, final Block<T> update) {
        return this._blocks.compareAndSet(ConcurrentArrayQueue.HEAD_OFFSET, current, update);
    }
    
    @Override
    public T peek() {
        Block<T> currentHeadBlock = this.getHeadBlock();
        int head = currentHeadBlock.head();
        while (true) {
            if (head == this.getBlockSize()) {
                final Block<T> nextHeadBlock = currentHeadBlock.next();
                if (nextHeadBlock == null) {
                    return null;
                }
                currentHeadBlock = nextHeadBlock;
                head = currentHeadBlock.head();
            }
            else {
                final T element = currentHeadBlock.peek(head);
                if (element != ConcurrentArrayQueue.REMOVED_ELEMENT) {
                    return element;
                }
                ++head;
            }
        }
    }
    
    @Override
    public boolean remove(final Object o) {
        Block<T> currentHeadBlock = this.getHeadBlock();
        int head = currentHeadBlock.head();
        boolean result = false;
        while (true) {
            if (head == this.getBlockSize()) {
                final Block<T> nextHeadBlock = currentHeadBlock.next();
                if (nextHeadBlock == null) {
                    break;
                }
                currentHeadBlock = nextHeadBlock;
                head = currentHeadBlock.head();
            }
            else {
                final Object element = currentHeadBlock.peek(head);
                if (element == ConcurrentArrayQueue.REMOVED_ELEMENT) {
                    ++head;
                }
                else {
                    if (element == null) {
                        break;
                    }
                    if (element.equals(o)) {
                        if (currentHeadBlock.remove(head, o, false)) {
                            result = true;
                            break;
                        }
                        ++head;
                    }
                    else {
                        ++head;
                    }
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        return super.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        return super.retainAll(c);
    }
    
    @Override
    public Iterator<T> iterator() {
        final List<Object[]> blocks = new ArrayList<Object[]>();
        for (Block<T> currentHeadBlock = this.getHeadBlock(); currentHeadBlock != null; currentHeadBlock = currentHeadBlock.next()) {
            final Object[] elements = currentHeadBlock.arrayCopy();
            blocks.add(elements);
        }
        return new Iterator<T>() {
            private int blockIndex;
            private int index;
            
            @Override
            public boolean hasNext() {
                while (this.blockIndex != blocks.size()) {
                    final Object element = ((Object[])blocks.get(this.blockIndex))[this.index];
                    if (element == null) {
                        return false;
                    }
                    if (element != ConcurrentArrayQueue.REMOVED_ELEMENT) {
                        return true;
                    }
                    this.advance();
                }
                return false;
            }
            
            @Override
            public T next() {
                while (this.blockIndex != blocks.size()) {
                    final Object element = ((Object[])blocks.get(this.blockIndex))[this.index];
                    if (element == null) {
                        throw new NoSuchElementException();
                    }
                    this.advance();
                    if (element != ConcurrentArrayQueue.REMOVED_ELEMENT) {
                        final T e = (T)element;
                        return e;
                    }
                }
                throw new NoSuchElementException();
            }
            
            private void advance() {
                if (++this.index == ConcurrentArrayQueue.this.getBlockSize()) {
                    this.index = 0;
                    ++this.blockIndex;
                }
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public int size() {
        Block<T> currentHeadBlock = this.getHeadBlock();
        int head = currentHeadBlock.head();
        int size = 0;
        while (true) {
            if (head == this.getBlockSize()) {
                final Block<T> nextHeadBlock = currentHeadBlock.next();
                if (nextHeadBlock == null) {
                    break;
                }
                currentHeadBlock = nextHeadBlock;
                head = currentHeadBlock.head();
            }
            else {
                final Object element = currentHeadBlock.peek(head);
                if (element == ConcurrentArrayQueue.REMOVED_ELEMENT) {
                    ++head;
                }
                else {
                    if (element == null) {
                        break;
                    }
                    ++size;
                    ++head;
                }
            }
        }
        return size;
    }
    
    protected Block<T> newBlock() {
        return new Block<T>(this.getBlockSize());
    }
    
    protected int getBlockCount() {
        int result = 0;
        for (Block<T> headBlock = this.getHeadBlock(); headBlock != null; headBlock = headBlock.next()) {
            ++result;
        }
        return result;
    }
    
    static {
        REMOVED_ELEMENT = new Object() {
            @Override
            public String toString() {
                return "X";
            }
        };
        HEAD_OFFSET = MemoryUtils.getIntegersPerCacheLine() - 1;
        TAIL_OFFSET = MemoryUtils.getIntegersPerCacheLine() * 2 - 1;
    }
    
    protected static final class Block<E>
    {
        private static final int headOffset;
        private static final int tailOffset;
        private final AtomicReferenceArray<Object> elements;
        private final AtomicReference<Block<E>> next;
        private final AtomicIntegerArray indexes;
        
        protected Block(final int blockSize) {
            this.next = new AtomicReference<Block<E>>();
            this.indexes = new AtomicIntegerArray(ConcurrentArrayQueue.TAIL_OFFSET + 1);
            this.elements = new AtomicReferenceArray<Object>(blockSize);
        }
        
        public E peek(final int index) {
            return (E)this.elements.get(index);
        }
        
        public boolean store(final int index, final E item) {
            final boolean result = this.elements.compareAndSet(index, null, item);
            if (result) {
                this.indexes.incrementAndGet(Block.tailOffset);
            }
            return result;
        }
        
        public boolean remove(final int index, final Object item, final boolean updateHead) {
            final boolean result = this.elements.compareAndSet(index, item, ConcurrentArrayQueue.REMOVED_ELEMENT);
            if (result && updateHead) {
                this.indexes.incrementAndGet(Block.headOffset);
            }
            return result;
        }
        
        public Block<E> next() {
            return this.next.get();
        }
        
        public boolean link(final Block<E> nextBlock) {
            return this.next.compareAndSet(null, nextBlock);
        }
        
        public int head() {
            return this.indexes.get(Block.headOffset);
        }
        
        public int tail() {
            return this.indexes.get(Block.tailOffset);
        }
        
        public Object[] arrayCopy() {
            final Object[] result = new Object[this.elements.length()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = this.elements.get(i);
            }
            return result;
        }
        
        static {
            headOffset = MemoryUtils.getIntegersPerCacheLine() - 1;
            tailOffset = MemoryUtils.getIntegersPerCacheLine() * 2 - 1;
        }
    }
}
