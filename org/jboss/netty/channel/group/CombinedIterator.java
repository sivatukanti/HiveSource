// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.group;

import java.util.NoSuchElementException;
import java.util.Iterator;

final class CombinedIterator<E> implements Iterator<E>
{
    private final Iterator<E> i1;
    private final Iterator<E> i2;
    private Iterator<E> currentIterator;
    
    CombinedIterator(final Iterator<E> i1, final Iterator<E> i2) {
        if (i1 == null) {
            throw new NullPointerException("i1");
        }
        if (i2 == null) {
            throw new NullPointerException("i2");
        }
        this.i1 = i1;
        this.i2 = i2;
        this.currentIterator = i1;
    }
    
    public boolean hasNext() {
        while (!this.currentIterator.hasNext()) {
            if (this.currentIterator != this.i1) {
                return false;
            }
            this.currentIterator = this.i2;
        }
        return true;
    }
    
    public E next() {
        try {
            final E e = this.currentIterator.next();
            return e;
        }
        catch (NoSuchElementException e2) {
            if (this.currentIterator == this.i1) {
                this.currentIterator = this.i2;
                return this.currentIterator.next();
            }
            throw e2;
        }
    }
    
    public void remove() {
        this.currentIterator.remove();
    }
}
