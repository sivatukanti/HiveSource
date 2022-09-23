// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary.objectinspector;

import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryMap;
import java.util.Map;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardMapObjectInspector;

public class LazyBinaryMapObjectInspector extends StandardMapObjectInspector
{
    protected LazyBinaryMapObjectInspector() {
    }
    
    protected LazyBinaryMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector) {
        super(mapKeyObjectInspector, mapValueObjectInspector);
    }
    
    @Override
    public Map<?, ?> getMap(final Object data) {
        if (data == null) {
            return null;
        }
        return ((LazyBinaryMap)data).getMap();
    }
    
    @Override
    public int getMapSize(final Object data) {
        if (data == null) {
            return -1;
        }
        return ((LazyBinaryMap)data).getMapSize();
    }
    
    @Override
    public Object getMapValueElement(final Object data, final Object key) {
        if (data == null || key == null) {
            return null;
        }
        return ((LazyBinaryMap)data).getMapValueElement(key);
    }
}
