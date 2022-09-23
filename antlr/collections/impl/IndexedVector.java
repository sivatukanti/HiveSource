// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections.impl;

import java.util.Enumeration;
import java.util.Hashtable;

public class IndexedVector
{
    protected Vector elements;
    protected Hashtable index;
    
    public IndexedVector() {
        this.elements = new Vector(10);
        this.index = new Hashtable(10);
    }
    
    public IndexedVector(final int initialCapacity) {
        this.elements = new Vector(initialCapacity);
        this.index = new Hashtable(initialCapacity);
    }
    
    public synchronized void appendElement(final Object key, final Object value) {
        this.elements.appendElement(value);
        this.index.put(key, value);
    }
    
    public Object elementAt(final int n) {
        return this.elements.elementAt(n);
    }
    
    public Enumeration elements() {
        return this.elements.elements();
    }
    
    public Object getElement(final Object key) {
        return this.index.get(key);
    }
    
    public synchronized boolean removeElement(final Object o) {
        final Object value = this.index.get(o);
        if (value == null) {
            return false;
        }
        this.index.remove(o);
        this.elements.removeElement(value);
        return false;
    }
    
    public int size() {
        return this.elements.size();
    }
}
