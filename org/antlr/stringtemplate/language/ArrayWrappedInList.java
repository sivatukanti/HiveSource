// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ArrayWrappedInList extends ArrayList
{
    protected Object array;
    protected int n;
    
    public ArrayWrappedInList(final Object array) {
        this.array = null;
        this.array = array;
        this.n = Array.getLength(array);
    }
    
    public Object get(final int i) {
        return Array.get(this.array, i);
    }
    
    public int size() {
        return this.n;
    }
    
    public boolean isEmpty() {
        return this.n == 0;
    }
    
    public Object[] toArray() {
        return (Object[])this.array;
    }
    
    public Iterator iterator() {
        return new ArrayIterator(this.array);
    }
}
