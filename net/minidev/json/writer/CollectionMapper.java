// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import java.util.List;
import net.minidev.json.JSONArray;
import java.util.Map;
import net.minidev.asm.FieldFilter;
import net.minidev.json.JSONUtil;
import net.minidev.json.JSONObject;
import java.lang.reflect.Type;
import net.minidev.asm.BeansAccess;
import java.lang.reflect.ParameterizedType;

public class CollectionMapper
{
    public static class MapType<T> extends JsonReaderI<T>
    {
        final ParameterizedType type;
        final Class<?> rawClass;
        final Class<?> instance;
        final BeansAccess<?> ba;
        final Type keyType;
        final Type valueType;
        final Class<?> keyClass;
        final Class<?> valueClass;
        JsonReaderI<?> subMapper;
        
        public MapType(final JsonReader base, final ParameterizedType type) {
            super(base);
            this.type = type;
            this.rawClass = (Class<?>)type.getRawType();
            if (this.rawClass.isInterface()) {
                this.instance = JSONObject.class;
            }
            else {
                this.instance = this.rawClass;
            }
            this.ba = BeansAccess.get(this.instance, JSONUtil.JSON_SMART_FIELD_FILTER);
            this.keyType = type.getActualTypeArguments()[0];
            this.valueType = type.getActualTypeArguments()[1];
            if (this.keyType instanceof Class) {
                this.keyClass = (Class<?>)this.keyType;
            }
            else {
                this.keyClass = (Class<?>)((ParameterizedType)this.keyType).getRawType();
            }
            if (this.valueType instanceof Class) {
                this.valueClass = (Class<?>)this.valueType;
            }
            else {
                this.valueClass = (Class<?>)((ParameterizedType)this.valueType).getRawType();
            }
        }
        
        @Override
        public Object createObject() {
            try {
                return this.instance.newInstance();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            return null;
        }
        
        @Override
        public JsonReaderI<?> startArray(final String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.valueType);
            }
            return this.subMapper;
        }
        
        @Override
        public JsonReaderI<?> startObject(final String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.valueType);
            }
            return this.subMapper;
        }
        
        @Override
        public void setValue(final Object current, final String key, final Object value) {
            ((Map)current).put(JSONUtil.convertToX(key, this.keyClass), JSONUtil.convertToX(value, this.valueClass));
        }
        
        @Override
        public Object getValue(final Object current, final String key) {
            return ((Map)current).get(JSONUtil.convertToX(key, this.keyClass));
        }
        
        @Override
        public Type getType(final String key) {
            return this.type;
        }
    }
    
    public static class MapClass<T> extends JsonReaderI<T>
    {
        final Class<?> type;
        final Class<?> instance;
        final BeansAccess<?> ba;
        JsonReaderI<?> subMapper;
        
        public MapClass(final JsonReader base, final Class<?> type) {
            super(base);
            this.type = type;
            if (type.isInterface()) {
                this.instance = JSONObject.class;
            }
            else {
                this.instance = type;
            }
            this.ba = BeansAccess.get(this.instance, JSONUtil.JSON_SMART_FIELD_FILTER);
        }
        
        @Override
        public Object createObject() {
            return this.ba.newInstance();
        }
        
        @Override
        public JsonReaderI<?> startArray(final String key) {
            return this.base.DEFAULT;
        }
        
        @Override
        public JsonReaderI<?> startObject(final String key) {
            return this.base.DEFAULT;
        }
        
        @Override
        public void setValue(final Object current, final String key, final Object value) {
            ((Map)current).put(key, value);
        }
        
        @Override
        public Object getValue(final Object current, final String key) {
            return ((Map)current).get(key);
        }
        
        @Override
        public Type getType(final String key) {
            return this.type;
        }
    }
    
    public static class ListType<T> extends JsonReaderI<T>
    {
        final ParameterizedType type;
        final Class<?> rawClass;
        final Class<?> instance;
        final BeansAccess<?> ba;
        final Type valueType;
        final Class<?> valueClass;
        JsonReaderI<?> subMapper;
        
        public ListType(final JsonReader base, final ParameterizedType type) {
            super(base);
            this.type = type;
            this.rawClass = (Class<?>)type.getRawType();
            if (this.rawClass.isInterface()) {
                this.instance = JSONArray.class;
            }
            else {
                this.instance = this.rawClass;
            }
            this.ba = BeansAccess.get(this.instance, JSONUtil.JSON_SMART_FIELD_FILTER);
            this.valueType = type.getActualTypeArguments()[0];
            if (this.valueType instanceof Class) {
                this.valueClass = (Class<?>)this.valueType;
            }
            else {
                this.valueClass = (Class<?>)((ParameterizedType)this.valueType).getRawType();
            }
        }
        
        @Override
        public Object createArray() {
            return this.ba.newInstance();
        }
        
        @Override
        public JsonReaderI<?> startArray(final String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.type.getActualTypeArguments()[0]);
            }
            return this.subMapper;
        }
        
        @Override
        public JsonReaderI<?> startObject(final String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.type.getActualTypeArguments()[0]);
            }
            return this.subMapper;
        }
        
        @Override
        public void addValue(final Object current, final Object value) {
            ((List)current).add(JSONUtil.convertToX(value, this.valueClass));
        }
    }
    
    public static class ListClass<T> extends JsonReaderI<T>
    {
        final Class<?> type;
        final Class<?> instance;
        final BeansAccess<?> ba;
        JsonReaderI<?> subMapper;
        
        public ListClass(final JsonReader base, final Class<?> clazz) {
            super(base);
            this.type = clazz;
            if (clazz.isInterface()) {
                this.instance = JSONArray.class;
            }
            else {
                this.instance = clazz;
            }
            this.ba = BeansAccess.get(this.instance, JSONUtil.JSON_SMART_FIELD_FILTER);
        }
        
        @Override
        public Object createArray() {
            return this.ba.newInstance();
        }
        
        @Override
        public JsonReaderI<?> startArray(final String key) {
            return this.base.DEFAULT;
        }
        
        @Override
        public JsonReaderI<?> startObject(final String key) {
            return this.base.DEFAULT;
        }
        
        @Override
        public void addValue(final Object current, final Object value) {
            ((List)current).add(value);
        }
    }
}
