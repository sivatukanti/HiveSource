// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.Iterator;

public class ArrayIterator implements Iterator
{
    protected int i;
    protected Object array;
    protected int n;
    
    public ArrayIterator(final Object array) {
        this.i = -1;
        this.array = null;
        this.array = array;
        this.n = Array.getLength(array);
    }
    
    public boolean hasNext() {
        return this.i + 1 < this.n && this.n > 0;
    }
    
    public Object next() {
        ++this.i;
        if (this.i >= this.n) {
            throw new NoSuchElementException();
        }
        return Array.get(this.array, this.i);
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
