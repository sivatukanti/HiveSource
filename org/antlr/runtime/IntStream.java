// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public interface IntStream
{
    void consume();
    
    int LA(final int p0);
    
    int mark();
    
    int index();
    
    void rewind(final int p0);
    
    void rewind();
    
    void release(final int p0);
    
    void seek(final int p0);
    
    int size();
    
    String getSourceName();
}
