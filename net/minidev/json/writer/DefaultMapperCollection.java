// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import java.util.List;
import java.util.Map;
import java.lang.reflect.Constructor;

public class DefaultMapperCollection<T> extends JsonReaderI<T>
{
    Class<T> clz;
    
    public DefaultMapperCollection(final JsonReader base, final Class<T> clz) {
        super(base);
        this.clz = clz;
    }
    
    @Override
    public JsonReaderI<T> startObject(final String key) {
        return this;
    }
    
    @Override
    public JsonReaderI<T> startArray(final String key) {
        return this;
    }
    
    @Override
    public Object createObject() {
        try {
            final Constructor<T> c = this.clz.getConstructor((Class<?>[])new Class[0]);
            return c.newInstance(new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Object createArray() {
        try {
            final Constructor<T> c = this.clz.getConstructor((Class<?>[])new Class[0]);
            return c.newInstance(new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public void setValue(final Object current, final String key, final Object value) {
        ((Map)current).put(key, value);
    }
    
    @Override
    public void addValue(final Object current, final Object value) {
        ((List)current).add(value);
    }
}
