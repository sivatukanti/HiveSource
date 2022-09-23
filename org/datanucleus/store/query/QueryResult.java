// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import java.util.Collection;

public interface QueryResult extends Collection
{
    void close();
    
    void disconnect();
}
