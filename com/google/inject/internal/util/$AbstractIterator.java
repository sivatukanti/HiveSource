// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public abstract class $AbstractIterator<T> implements Iterator<T>
{
    private State state;
    private T next;
    
    public $AbstractIterator() {
        this.state = State.NOT_READY;
    }
    
    protected abstract T computeNext();
    
    protected final T endOfData() {
        this.state = State.DONE;
        return null;
    }
    
    public boolean hasNext() {
        $Preconditions.checkState(this.state != State.FAILED);
        switch (this.state) {
            case DONE: {
                return false;
            }
            case READY: {
                return true;
            }
            default: {
                return this.tryToComputeNext();
            }
        }
    }
    
    private boolean tryToComputeNext() {
        this.state = State.FAILED;
        this.next = this.computeNext();
        if (this.state != State.DONE) {
            this.state = State.READY;
            return true;
        }
        return false;
    }
    
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.state = State.NOT_READY;
        return this.next;
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    private enum State
    {
        READY, 
        NOT_READY, 
        DONE, 
        FAILED;
    }
}
