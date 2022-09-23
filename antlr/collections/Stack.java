// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections;

import java.util.NoSuchElementException;

public interface Stack
{
    int height();
    
    Object pop() throws NoSuchElementException;
    
    void push(final Object p0);
    
    Object top() throws NoSuchElementException;
}
