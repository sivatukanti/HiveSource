// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections.impl;

import java.util.Enumeration;

public class Vector implements Cloneable
{
    protected Object[] data;
    protected int lastElement;
    
    public Vector() {
        this(10);
    }
    
    public Vector(final int n) {
        this.lastElement = -1;
        this.data = new Object[n];
    }
    
    public synchronized void appendElement(final Object o) {
        this.ensureCapacity(this.lastElement + 2);
        this.data[++this.lastElement] = o;
    }
    
    public int capacity() {
        return this.data.length;
    }
    
    public Object clone() {
        Vector vector;
        try {
            vector = (Vector)super.clone();
        }
        catch (CloneNotSupportedException ex) {
            System.err.println("cannot clone Vector.super");
            return null;
        }
        vector.data = new Object[this.size()];
        System.arraycopy(this.data, 0, vector.data, 0, this.size());
        return vector;
    }
    
    public synchronized Object elementAt(final int n) {
        if (n >= this.data.length) {
            throw new ArrayIndexOutOfBoundsException(n + " >= " + this.data.length);
        }
        if (n < 0) {
            throw new ArrayIndexOutOfBoundsException(n + " < 0 ");
        }
        return this.data[n];
    }
    
    public synchronized Enumeration elements() {
        return new VectorEnumerator(this);
    }
    
    public synchronized void ensureCapacity(final int n) {
        if (n + 1 > this.data.length) {
            final Object[] data = this.data;
            int n2 = this.data.length * 2;
            if (n + 1 > n2) {
                n2 = n + 1;
            }
            System.arraycopy(data, 0, this.data = new Object[n2], 0, data.length);
        }
    }
    
    public synchronized boolean removeElement(final Object o) {
        int n;
        for (n = 0; n <= this.lastElement && this.data[n] != o; ++n) {}
        if (n <= this.lastElement) {
            this.data[n] = null;
            final int n2 = this.lastElement - n;
            if (n2 > 0) {
                System.arraycopy(this.data, n + 1, this.data, n, n2);
            }
            --this.lastElement;
            return true;
        }
        return false;
    }
    
    public synchronized void setElementAt(final Object o, final int n) {
        if (n >= this.data.length) {
            throw new ArrayIndexOutOfBoundsException(n + " >= " + this.data.length);
        }
        this.data[n] = o;
        if (n > this.lastElement) {
            this.lastElement = n;
        }
    }
    
    public int size() {
        return this.lastElement + 1;
    }
}
