// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.Stack;

public class IdentityStack<E> extends Stack<E>
{
    private static final long serialVersionUID = -5555522620060077046L;
    
    public static <E> IdentityStack<E> getInstance(final Stack<E> s) {
        if (s instanceof IdentityStack) {
            return (IdentityStack<E>)(IdentityStack)s;
        }
        final IdentityStack<E> result = new IdentityStack<E>();
        if (s != null) {
            result.addAll((Collection<?>)s);
        }
        return result;
    }
    
    public IdentityStack() {
    }
    
    public IdentityStack(final E o) {
        this.push(o);
    }
    
    @Override
    public synchronized boolean contains(final Object o) {
        return this.indexOf(o) >= 0;
    }
    
    @Override
    public synchronized int indexOf(final Object o, final int pos) {
        for (int size = this.size(), i = pos; i < size; ++i) {
            if (this.get(i) == o) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public synchronized int lastIndexOf(final Object o, final int pos) {
        for (int i = pos; i >= 0; --i) {
            if (this.get(i) == o) {
                return i;
            }
        }
        return -1;
    }
}
