// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import java.util.List;
import java.util.Map;
import java.lang.reflect.ParameterizedType;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import java.util.Date;
import net.minidev.json.JSONAwareEx;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

public class JsonReader
{
    private final ConcurrentHashMap<Type, JsonReaderI<?>> cache;
    public JsonReaderI<JSONAwareEx> DEFAULT;
    public JsonReaderI<JSONAwareEx> DEFAULT_ORDERED;
    
    public JsonReader() {
        (this.cache = new ConcurrentHashMap<Type, JsonReaderI<?>>(100)).put(Date.class, BeansMapper.MAPPER_DATE);
        this.cache.put(int[].class, ArraysMapper.MAPPER_PRIM_INT);
        this.cache.put(Integer[].class, ArraysMapper.MAPPER_INT);
        this.cache.put(short[].class, ArraysMapper.MAPPER_PRIM_INT);
        this.cache.put(Short[].class, ArraysMapper.MAPPER_INT);
        this.cache.put(long[].class, ArraysMapper.MAPPER_PRIM_LONG);
        this.cache.put(Long[].class, ArraysMapper.MAPPER_LONG);
        this.cache.put(byte[].class, ArraysMapper.MAPPER_PRIM_BYTE);
        this.cache.put(Byte[].class, ArraysMapper.MAPPER_BYTE);
        this.cache.put(char[].class, ArraysMapper.MAPPER_PRIM_CHAR);
        this.cache.put(Character[].class, ArraysMapper.MAPPER_CHAR);
        this.cache.put(float[].class, ArraysMapper.MAPPER_PRIM_FLOAT);
        this.cache.put(Float[].class, ArraysMapper.MAPPER_FLOAT);
        this.cache.put(double[].class, ArraysMapper.MAPPER_PRIM_DOUBLE);
        this.cache.put(Double[].class, ArraysMapper.MAPPER_DOUBLE);
        this.cache.put(boolean[].class, ArraysMapper.MAPPER_PRIM_BOOL);
        this.cache.put(Boolean[].class, ArraysMapper.MAPPER_BOOL);
        this.DEFAULT = new DefaultMapper<JSONAwareEx>(this);
        this.DEFAULT_ORDERED = new DefaultMapperOrdered(this);
        this.cache.put(JSONAwareEx.class, this.DEFAULT);
        this.cache.put(JSONAware.class, this.DEFAULT);
        this.cache.put(JSONArray.class, this.DEFAULT);
        this.cache.put(JSONObject.class, this.DEFAULT);
    }
    
    public <T> void remapField(final Class<T> type, final String fromJson, final String toJava) {
        JsonReaderI<T> map = this.getMapper(type);
        if (!(map instanceof MapperRemapped)) {
            map = new MapperRemapped<T>(map);
            this.registerReader(type, map);
        }
        ((MapperRemapped)map).renameField(fromJson, toJava);
    }
    
    public <T> void registerReader(final Class<T> type, final JsonReaderI<T> mapper) {
        this.cache.put(type, mapper);
    }
    
    public <T> JsonReaderI<T> getMapper(final Type type) {
        if (type instanceof ParameterizedType) {
            return this.getMapper((ParameterizedType)type);
        }
        return this.getMapper((Class<T>)type);
    }
    
    public <T> JsonReaderI<T> getMapper(final Class<T> type) {
        JsonReaderI<T> map = (JsonReaderI<T>)this.cache.get(type);
        if (map != null) {
            return map;
        }
        if (type instanceof Class) {
            if (Map.class.isAssignableFrom(type)) {
                map = new DefaultMapperCollection<T>(this, type);
            }
            else if (List.class.isAssignableFrom(type)) {
                map = new DefaultMapperCollection<T>(this, type);
            }
            if (map != null) {
                this.cache.put(type, map);
                return map;
            }
        }
        if (type.isArray()) {
            map = new ArraysMapper.GenericMapper<T>(this, type);
        }
        else if (List.class.isAssignableFrom(type)) {
            map = new CollectionMapper.ListClass<T>(this, type);
        }
        else if (Map.class.isAssignableFrom(type)) {
            map = new CollectionMapper.MapClass<T>(this, type);
        }
        else {
            map = new BeansMapper.Bean<T>(this, type);
        }
        this.cache.putIfAbsent(type, map);
        return map;
    }
    
    public <T> JsonReaderI<T> getMapper(final ParameterizedType type) {
        JsonReaderI<T> map = (JsonReaderI<T>)this.cache.get(type);
        if (map != null) {
            return map;
        }
        final Class<T> clz = (Class<T>)type.getRawType();
        if (List.class.isAssignableFrom(clz)) {
            map = new CollectionMapper.ListType<T>(this, type);
        }
        else if (Map.class.isAssignableFrom(clz)) {
            map = new CollectionMapper.MapType<T>(this, type);
        }
        this.cache.putIfAbsent(type, map);
        return map;
    }
}
