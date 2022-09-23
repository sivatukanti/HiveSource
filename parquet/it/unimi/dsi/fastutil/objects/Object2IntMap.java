// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.objects;

import parquet.it.unimi.dsi.fastutil.ints.IntCollection;
import java.util.Map;

public interface Object2IntMap<K> extends Object2IntFunction<K>, Map<K, Integer>
{
    ObjectSet<Map.Entry<K, Integer>> entrySet();
    
    ObjectSet<Entry<K>> object2IntEntrySet();
    
    ObjectSet<K> keySet();
    
    IntCollection values();
    
    boolean containsValue(final int p0);
    
    public interface Entry<K> extends Map.Entry<K, Integer>
    {
        int setValue(final int p0);
        
        int getIntValue();
    }
    
    public interface FastEntrySet<K> extends ObjectSet<Entry<K>>
    {
        ObjectIterator<Entry<K>> fastIterator();
    }
}
