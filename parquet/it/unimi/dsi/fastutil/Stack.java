// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil;

public interface Stack<K>
{
    void push(final K p0);
    
    K pop();
    
    boolean isEmpty();
    
    K top();
    
    K peek(final int p0);
}
