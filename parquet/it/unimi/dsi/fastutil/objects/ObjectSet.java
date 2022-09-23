// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.objects;

import java.util.Set;

public interface ObjectSet<K> extends ObjectCollection<K>, Set<K>
{
    ObjectIterator<K> iterator();
    
    boolean remove(final Object p0);
}
