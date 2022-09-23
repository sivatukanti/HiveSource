// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public interface CharStream extends IntStream
{
    public static final int EOF = -1;
    
    String substring(final int p0, final int p1);
    
    int LT(final int p0);
    
    int getLine();
    
    void setLine(final int p0);
    
    void setCharPositionInLine(final int p0);
    
    int getCharPositionInLine();
}
