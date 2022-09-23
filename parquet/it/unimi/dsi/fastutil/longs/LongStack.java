// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import parquet.it.unimi.dsi.fastutil.Stack;

public interface LongStack extends Stack<Long>
{
    void push(final long p0);
    
    long popLong();
    
    long topLong();
    
    long peekLong(final int p0);
}
