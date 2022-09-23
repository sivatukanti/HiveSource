// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Container<T>
{
    private T item;
    
    public Container() {
    }
    
    public Container(final T item) {
        this.item = item;
    }
    
    public T get() {
        return this.item;
    }
    
    public void set(final T item) {
        this.item = item;
    }
}
