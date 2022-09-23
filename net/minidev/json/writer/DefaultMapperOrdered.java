// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import net.minidev.json.JSONArray;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minidev.json.JSONAwareEx;

public class DefaultMapperOrdered extends JsonReaderI<JSONAwareEx>
{
    protected DefaultMapperOrdered(final JsonReader base) {
        super(base);
    }
    
    @Override
    public JsonReaderI<JSONAwareEx> startObject(final String key) {
        return this.base.DEFAULT_ORDERED;
    }
    
    @Override
    public JsonReaderI<JSONAwareEx> startArray(final String key) {
        return this.base.DEFAULT_ORDERED;
    }
    
    @Override
    public void setValue(final Object current, final String key, final Object value) {
        ((Map)current).put(key, value);
    }
    
    @Override
    public Object createObject() {
        return new LinkedHashMap();
    }
    
    @Override
    public void addValue(final Object current, final Object value) {
        ((JSONArray)current).add(value);
    }
    
    @Override
    public Object createArray() {
        return new JSONArray();
    }
}
