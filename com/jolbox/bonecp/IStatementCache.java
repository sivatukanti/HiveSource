// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

public interface IStatementCache
{
    StatementHandle get(final String p0);
    
    int size();
    
    void clear();
    
    StatementHandle get(final String p0, final int p1, final int p2, final int p3);
    
    StatementHandle get(final String p0, final int p1, final int p2);
    
    StatementHandle get(final String p0, final int p1);
    
    StatementHandle get(final String p0, final int[] p1);
    
    StatementHandle get(final String p0, final String[] p1);
    
    String calculateCacheKey(final String p0, final String[] p1);
    
    String calculateCacheKey(final String p0, final int[] p1);
    
    String calculateCacheKey(final String p0, final int p1);
    
    String calculateCacheKey(final String p0, final int p1, final int p2);
    
    String calculateCacheKey(final String p0, final int p1, final int p2, final int p3);
    
    void checkForProperClosure();
    
    void putIfAbsent(final String p0, final StatementHandle p1);
}
