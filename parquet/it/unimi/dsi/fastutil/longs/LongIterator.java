// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import java.util.Iterator;

public interface LongIterator extends Iterator<Long>
{
    long nextLong();
    
    int skip(final int p0);
}
