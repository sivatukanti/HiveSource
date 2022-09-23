// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import java.io.IOException;
import net.minidev.json.parser.ParseException;
import java.lang.reflect.Type;

public class UpdaterMapper<T> extends JsonReaderI<T>
{
    final T obj;
    final JsonReaderI<?> mapper;
    
    public UpdaterMapper(final JsonReader base, final T obj) {
        super(base);
        if (obj == null) {
            throw new NullPointerException("can not update null Object");
        }
        this.obj = obj;
        this.mapper = base.getMapper(obj.getClass());
    }
    
    public UpdaterMapper(final JsonReader base, final T obj, final Type type) {
        super(base);
        if (obj == null) {
            throw new NullPointerException("can not update null Object");
        }
        this.obj = obj;
        this.mapper = base.getMapper(type);
    }
    
    @Override
    public JsonReaderI<?> startObject(final String key) throws ParseException, IOException {
        final Object bean = this.mapper.getValue(this.obj, key);
        if (bean == null) {
            return this.mapper.startObject(key);
        }
        return new UpdaterMapper<Object>(this.base, bean, this.mapper.getType(key));
    }
    
    @Override
    public JsonReaderI<?> startArray(final String key) throws ParseException, IOException {
        return this.mapper.startArray(key);
    }
    
    @Override
    public void setValue(final Object current, final String key, final Object value) throws ParseException, IOException {
        this.mapper.setValue(current, key, value);
    }
    
    @Override
    public void addValue(final Object current, final Object value) throws ParseException, IOException {
        this.mapper.addValue(current, value);
    }
    
    @Override
    public Object createObject() {
        if (this.obj != null) {
            return this.obj;
        }
        return this.mapper.createObject();
    }
    
    @Override
    public Object createArray() {
        if (this.obj != null) {
            return this.obj;
        }
        return this.mapper.createArray();
    }
    
    @Override
    public T convert(final Object current) {
        if (this.obj != null) {
            return this.obj;
        }
        return (T)this.mapper.convert(current);
    }
}
