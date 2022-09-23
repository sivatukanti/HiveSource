// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.cache;

import org.apache.curator.shaded.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface LongAddable
{
    void increment();
    
    void add(final long p0);
    
    long sum();
}
