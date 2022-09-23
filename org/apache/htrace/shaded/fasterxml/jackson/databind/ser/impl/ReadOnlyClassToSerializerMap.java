// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.SerializerCache;

public final class ReadOnlyClassToSerializerMap
{
    protected final JsonSerializerMap _map;
    protected SerializerCache.TypeKey _cacheKey;
    
    private ReadOnlyClassToSerializerMap(final JsonSerializerMap map) {
        this._cacheKey = null;
        this._map = map;
    }
    
    public ReadOnlyClassToSerializerMap instance() {
        return new ReadOnlyClassToSerializerMap(this._map);
    }
    
    public static ReadOnlyClassToSerializerMap from(final HashMap<SerializerCache.TypeKey, JsonSerializer<Object>> src) {
        return new ReadOnlyClassToSerializerMap(new JsonSerializerMap(src));
    }
    
    public JsonSerializer<Object> typedValueSerializer(final JavaType type) {
        if (this._cacheKey == null) {
            this._cacheKey = new SerializerCache.TypeKey(type, true);
        }
        else {
            this._cacheKey.resetTyped(type);
        }
        return this._map.find(this._cacheKey);
    }
    
    public JsonSerializer<Object> typedValueSerializer(final Class<?> cls) {
        if (this._cacheKey == null) {
            this._cacheKey = new SerializerCache.TypeKey(cls, true);
        }
        else {
            this._cacheKey.resetTyped(cls);
        }
        return this._map.find(this._cacheKey);
    }
    
    public JsonSerializer<Object> untypedValueSerializer(final JavaType type) {
        if (this._cacheKey == null) {
            this._cacheKey = new SerializerCache.TypeKey(type, false);
        }
        else {
            this._cacheKey.resetUntyped(type);
        }
        return this._map.find(this._cacheKey);
    }
    
    public JsonSerializer<Object> untypedValueSerializer(final Class<?> cls) {
        if (this._cacheKey == null) {
            this._cacheKey = new SerializerCache.TypeKey(cls, false);
        }
        else {
            this._cacheKey.resetUntyped(cls);
        }
        return this._map.find(this._cacheKey);
    }
}
