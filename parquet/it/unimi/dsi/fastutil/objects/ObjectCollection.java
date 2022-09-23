// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.objects;

import java.util.Collection;

public interface ObjectCollection<K> extends Collection<K>, ObjectIterable<K>
{
    ObjectIterator<K> iterator();
    
    @Deprecated
    ObjectIterator<K> objectIterator();
    
     <T> T[] toArray(final T[] p0);
}
