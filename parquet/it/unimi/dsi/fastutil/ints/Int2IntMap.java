// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.ints;

import parquet.it.unimi.dsi.fastutil.objects.ObjectIterator;
import parquet.it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;

public interface Int2IntMap extends Int2IntFunction, Map<Integer, Integer>
{
    ObjectSet<Map.Entry<Integer, Integer>> entrySet();
    
    ObjectSet<Entry> int2IntEntrySet();
    
    IntSet keySet();
    
    IntCollection values();
    
    boolean containsValue(final int p0);
    
    public interface Entry extends Map.Entry<Integer, Integer>
    {
        int getIntKey();
        
        int setValue(final int p0);
        
        int getIntValue();
    }
    
    public interface FastEntrySet extends ObjectSet<Entry>
    {
        ObjectIterator<Entry> fastIterator();
    }
}
