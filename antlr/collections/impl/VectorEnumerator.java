// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections.impl;

import java.util.NoSuchElementException;
import java.util.Enumeration;

class VectorEnumerator implements Enumeration
{
    Vector vector;
    int i;
    
    VectorEnumerator(final Vector vector) {
        this.vector = vector;
        this.i = 0;
    }
    
    public boolean hasMoreElements() {
        synchronized (this.vector) {
            return this.i <= this.vector.lastElement;
        }
    }
    
    public Object nextElement() {
        synchronized (this.vector) {
            if (this.i <= this.vector.lastElement) {
                return this.vector.data[this.i++];
            }
            throw new NoSuchElementException("VectorEnumerator");
        }
    }
}
