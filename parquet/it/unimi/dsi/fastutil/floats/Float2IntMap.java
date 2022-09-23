// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.floats;

import parquet.it.unimi.dsi.fastutil.objects.ObjectIterator;
import parquet.it.unimi.dsi.fastutil.ints.IntCollection;
import parquet.it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;

public interface Float2IntMap extends Float2IntFunction, Map<Float, Integer>
{
    ObjectSet<Map.Entry<Float, Integer>> entrySet();
    
    ObjectSet<Entry> float2IntEntrySet();
    
    FloatSet keySet();
    
    IntCollection values();
    
    boolean containsValue(final int p0);
    
    public interface Entry extends Map.Entry<Float, Integer>
    {
        float getFloatKey();
        
        int setValue(final int p0);
        
        int getIntValue();
    }
    
    public interface FastEntrySet extends ObjectSet<Entry>
    {
        ObjectIterator<Entry> fastIterator();
    }
}
