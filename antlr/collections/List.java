// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public interface List
{
    void add(final Object p0);
    
    void append(final Object p0);
    
    Object elementAt(final int p0) throws NoSuchElementException;
    
    Enumeration elements();
    
    boolean includes(final Object p0);
    
    int length();
}
