// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;
import java.util.Enumeration;

interface TokenManager
{
    Object clone();
    
    void define(final TokenSymbol p0);
    
    String getName();
    
    String getTokenStringAt(final int p0);
    
    TokenSymbol getTokenSymbol(final String p0);
    
    TokenSymbol getTokenSymbolAt(final int p0);
    
    Enumeration getTokenSymbolElements();
    
    Enumeration getTokenSymbolKeys();
    
    Vector getVocabulary();
    
    boolean isReadOnly();
    
    void mapToTokenSymbol(final String p0, final TokenSymbol p1);
    
    int maxTokenType();
    
    int nextTokenType();
    
    void setName(final String p0);
    
    void setReadOnly(final boolean p0);
    
    boolean tokenDefined(final String p0);
}
