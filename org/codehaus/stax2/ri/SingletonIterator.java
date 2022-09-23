// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class SingletonIterator implements Iterator
{
    private final Object mValue;
    private boolean mDone;
    
    public SingletonIterator(final Object mValue) {
        this.mDone = false;
        this.mValue = mValue;
    }
    
    public boolean hasNext() {
        return !this.mDone;
    }
    
    public Object next() {
        if (this.mDone) {
            throw new NoSuchElementException();
        }
        this.mDone = true;
        return this.mValue;
    }
    
    public void remove() {
        throw new UnsupportedOperationException("Can not remove item from SingletonIterator.");
    }
}
