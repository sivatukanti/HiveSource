// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public interface TokenStream extends IntStream
{
    Token LT(final int p0);
    
    int range();
    
    Token get(final int p0);
    
    TokenSource getTokenSource();
    
    String toString(final int p0, final int p1);
    
    String toString(final Token p0, final Token p1);
}
