// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public interface TokenSource
{
    Token nextToken();
    
    String getSourceName();
}
