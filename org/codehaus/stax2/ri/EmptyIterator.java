// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import java.util.NoSuchElementException;
import java.util.Iterator;

public final class EmptyIterator implements Iterator
{
    static final EmptyIterator sInstance;
    
    private EmptyIterator() {
    }
    
    public static EmptyIterator getInstance() {
        return EmptyIterator.sInstance;
    }
    
    public boolean hasNext() {
        return false;
    }
    
    public Object next() {
        throw new NoSuchElementException();
    }
    
    public void remove() {
        throw new IllegalStateException();
    }
    
    static {
        sInstance = new EmptyIterator();
    }
}
