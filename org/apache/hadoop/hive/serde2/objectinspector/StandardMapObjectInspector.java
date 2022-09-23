// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.HashMap;
import java.util.Map;

public class StandardMapObjectInspector implements SettableMapObjectInspector
{
    private ObjectInspector mapKeyObjectInspector;
    private ObjectInspector mapValueObjectInspector;
    
    protected StandardMapObjectInspector() {
    }
    
    protected StandardMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector) {
        this.mapKeyObjectInspector = mapKeyObjectInspector;
        this.mapValueObjectInspector = mapValueObjectInspector;
    }
    
    @Override
    public ObjectInspector getMapKeyObjectInspector() {
        return this.mapKeyObjectInspector;
    }
    
    @Override
    public ObjectInspector getMapValueObjectInspector() {
        return this.mapValueObjectInspector;
    }
    
    @Override
    public Object getMapValueElement(final Object data, final Object key) {
        if (data == null || key == null) {
            return null;
        }
        final Map<?, ?> map = (Map<?, ?>)data;
        return map.get(key);
    }
    
    @Override
    public int getMapSize(final Object data) {
        if (data == null) {
            return -1;
        }
        final Map<?, ?> map = (Map<?, ?>)data;
        return map.size();
    }
    
    @Override
    public Map<?, ?> getMap(final Object data) {
        if (data == null) {
            return null;
        }
        final Map<?, ?> map = (Map<?, ?>)data;
        return map;
    }
    
    @Override
    public final ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.MAP;
    }
    
    @Override
    public String getTypeName() {
        return "map<" + this.mapKeyObjectInspector.getTypeName() + "," + this.mapValueObjectInspector.getTypeName() + ">";
    }
    
    @Override
    public Object create() {
        final Map<Object, Object> m = new HashMap<Object, Object>();
        return m;
    }
    
    @Override
    public Object clear(final Object map) {
        final Map<Object, Object> m = (Map<Object, Object>)map;
        m.clear();
        return m;
    }
    
    @Override
    public Object put(final Object map, final Object key, final Object value) {
        final Map<Object, Object> m = (Map<Object, Object>)map;
        m.put(key, value);
        return m;
    }
    
    @Override
    public Object remove(final Object map, final Object key) {
        final Map<Object, Object> m = (Map<Object, Object>)map;
        m.remove(key);
        return m;
    }
}
