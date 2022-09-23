// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONAwareEx;

public class DefaultMapper<T> extends JsonReaderI<T>
{
    protected DefaultMapper(final JsonReader base) {
        super(base);
    }
    
    @Override
    public JsonReaderI<JSONAwareEx> startObject(final String key) {
        return this.base.DEFAULT;
    }
    
    @Override
    public JsonReaderI<JSONAwareEx> startArray(final String key) {
        return this.base.DEFAULT;
    }
    
    @Override
    public Object createObject() {
        return new JSONObject();
    }
    
    @Override
    public Object createArray() {
        return new JSONArray();
    }
    
    @Override
    public void setValue(final Object current, final String key, final Object value) {
        ((JSONObject)current).put(key, value);
    }
    
    @Override
    public void addValue(final Object current, final Object value) {
        ((JSONArray)current).add(value);
    }
}
