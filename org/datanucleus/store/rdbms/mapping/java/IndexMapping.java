// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

public final class IndexMapping extends SingleFieldMapping
{
    @Override
    public boolean includeInFetchStatement() {
        return false;
    }
    
    @Override
    public Class getJavaType() {
        return Integer.class;
    }
}
