// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import parquet.it.unimi.dsi.fastutil.objects.ObjectIterator;
import parquet.it.unimi.dsi.fastutil.ints.IntCollection;
import parquet.it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;

public interface Long2IntMap extends Long2IntFunction, Map<Long, Integer>
{
    ObjectSet<Map.Entry<Long, Integer>> entrySet();
    
    ObjectSet<Entry> long2IntEntrySet();
    
    LongSet keySet();
    
    IntCollection values();
    
    boolean containsValue(final int p0);
    
    public interface Entry extends Map.Entry<Long, Integer>
    {
        long getLongKey();
        
        int setValue(final int p0);
        
        int getIntValue();
    }
    
    public interface FastEntrySet extends ObjectSet<Entry>
    {
        ObjectIterator<Entry> fastIterator();
    }
}
