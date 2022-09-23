// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.symbol;

import java.util.List;

public interface SymbolResolver
{
    Class getType(final List p0);
    
    Class getPrimaryClass();
    
    Class resolveClass(final String p0);
    
    boolean supportsImplicitVariables();
    
    boolean caseSensitiveSymbolNames();
}
