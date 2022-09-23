// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil;

import java.util.Iterator;

public interface BidirectionalIterator<K> extends Iterator<K>
{
    K previous();
    
    boolean hasPrevious();
}
