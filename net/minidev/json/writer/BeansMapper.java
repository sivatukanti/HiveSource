// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import java.lang.reflect.Type;
import net.minidev.asm.FieldFilter;
import net.minidev.json.JSONUtil;
import net.minidev.asm.Accessor;
import java.util.HashMap;
import net.minidev.asm.BeansAccess;
import net.minidev.asm.ConvertDate;
import java.util.Date;

public abstract class BeansMapper<T> extends JsonReaderI<T>
{
    public static JsonReaderI<Date> MAPPER_DATE;
    
    static {
        BeansMapper.MAPPER_DATE = new ArraysMapper<Date>() {
            @Override
            public Date convert(final Object current) {
                return ConvertDate.convertToDate(current);
            }
        };
    }
    
    public BeansMapper(final JsonReader base) {
        super(base);
    }
    
    @Override
    public abstract Object getValue(final Object p0, final String p1);
    
    public static class Bean<T> extends JsonReaderI<T>
    {
        final Class<T> clz;
        final BeansAccess<T> ba;
        final HashMap<String, Accessor> index;
        
        public Bean(final JsonReader base, final Class<T> clz) {
            super(base);
            this.clz = clz;
            this.ba = BeansAccess.get(clz, JSONUtil.JSON_SMART_FIELD_FILTER);
            this.index = this.ba.getMap();
        }
        
        @Override
        public void setValue(final Object current, final String key, final Object value) {
            this.ba.set((T)current, key, value);
        }
        
        @Override
        public Object getValue(final Object current, final String key) {
            return this.ba.get((T)current, key);
        }
        
        @Override
        public Type getType(final String key) {
            final Accessor nfo = this.index.get(key);
            return nfo.getGenericType();
        }
        
        @Override
        public JsonReaderI<?> startArray(final String key) {
            final Accessor nfo = this.index.get(key);
            if (nfo == null) {
                throw new RuntimeException("Can not find Array '" + key + "' field in " + this.clz);
            }
            return this.base.getMapper(nfo.getGenericType());
        }
        
        @Override
        public JsonReaderI<?> startObject(final String key) {
            final Accessor f = this.index.get(key);
            if (f == null) {
                throw new RuntimeException("Can not find Object '" + key + "' field in " + this.clz);
            }
            return this.base.getMapper(f.getGenericType());
        }
        
        @Override
        public Object createObject() {
            return this.ba.newInstance();
        }
    }
    
    public static class BeanNoConv<T> extends JsonReaderI<T>
    {
        final Class<T> clz;
        final BeansAccess<T> ba;
        final HashMap<String, Accessor> index;
        
        public BeanNoConv(final JsonReader base, final Class<T> clz) {
            super(base);
            this.clz = clz;
            this.ba = BeansAccess.get(clz, JSONUtil.JSON_SMART_FIELD_FILTER);
            this.index = this.ba.getMap();
        }
        
        @Override
        public void setValue(final Object current, final String key, final Object value) {
            this.ba.set((T)current, key, value);
        }
        
        @Override
        public Object getValue(final Object current, final String key) {
            return this.ba.get((T)current, key);
        }
        
        @Override
        public Type getType(final String key) {
            final Accessor nfo = this.index.get(key);
            return nfo.getGenericType();
        }
        
        @Override
        public JsonReaderI<?> startArray(final String key) {
            final Accessor nfo = this.index.get(key);
            if (nfo == null) {
                throw new RuntimeException("Can not set " + key + " field in " + this.clz);
            }
            return this.base.getMapper(nfo.getGenericType());
        }
        
        @Override
        public JsonReaderI<?> startObject(final String key) {
            final Accessor f = this.index.get(key);
            if (f == null) {
                throw new RuntimeException("Can not set " + key + " field in " + this.clz);
            }
            return this.base.getMapper(f.getGenericType());
        }
        
        @Override
        public Object createObject() {
            return this.ba.newInstance();
        }
    }
}
