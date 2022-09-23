// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.type;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public final class MapType extends MapLikeType
{
    private static final long serialVersionUID = -811146779148281500L;
    
    private MapType(final Class<?> mapType, final JavaType keyT, final JavaType valueT, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(mapType, keyT, valueT, valueHandler, typeHandler, asStatic);
    }
    
    public static MapType construct(final Class<?> rawType, final JavaType keyT, final JavaType valueT) {
        return new MapType(rawType, keyT, valueT, null, null, false);
    }
    
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return new MapType(subclass, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType narrowContentsBy(final Class<?> contentClass) {
        if (contentClass == this._valueType.getRawClass()) {
            return this;
        }
        return new MapType(this._class, this._keyType, this._valueType.narrowBy(contentClass), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType widenContentsBy(final Class<?> contentClass) {
        if (contentClass == this._valueType.getRawClass()) {
            return this;
        }
        return new MapType(this._class, this._keyType, this._valueType.widenBy(contentClass), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType narrowKey(final Class<?> keySubclass) {
        if (keySubclass == this._keyType.getRawClass()) {
            return this;
        }
        return new MapType(this._class, this._keyType.narrowBy(keySubclass), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType widenKey(final Class<?> keySubclass) {
        if (keySubclass == this._keyType.getRawClass()) {
            return this;
        }
        return new MapType(this._class, this._keyType.widenBy(keySubclass), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withTypeHandler(final Object h) {
        return new MapType(this._class, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public MapType withContentTypeHandler(final Object h) {
        return new MapType(this._class, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withValueHandler(final Object h) {
        return new MapType(this._class, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withContentValueHandler(final Object h) {
        return new MapType(this._class, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new MapType(this._class, this._keyType.withStaticTyping(), this._valueType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public MapType withKeyTypeHandler(final Object h) {
        return new MapType(this._class, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withKeyValueHandler(final Object h) {
        return new MapType(this._class, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public String toString() {
        return "[map type; class " + this._class.getName() + ", " + this._keyType + " -> " + this._valueType + "]";
    }
}
