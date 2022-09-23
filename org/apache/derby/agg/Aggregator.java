// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.agg;

import java.io.Serializable;

public interface Aggregator<V, R, A extends Aggregator<V, R, A>> extends Serializable
{
    void init();
    
    void accumulate(final V p0);
    
    void merge(final A p0);
    
    R terminate();
}
