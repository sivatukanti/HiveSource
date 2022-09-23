// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.type;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public final class CollectionType extends CollectionLikeType
{
    private static final long serialVersionUID = -7834910259750909424L;
    
    private CollectionType(final Class<?> collT, final JavaType elemT, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(collT, elemT, valueHandler, typeHandler, asStatic);
    }
    
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return new CollectionType(subclass, this._elementType, null, null, this._asStatic);
    }
    
    @Override
    public JavaType narrowContentsBy(final Class<?> contentClass) {
        if (contentClass == this._elementType.getRawClass()) {
            return this;
        }
        return new CollectionType(this._class, this._elementType.narrowBy(contentClass), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType widenContentsBy(final Class<?> contentClass) {
        if (contentClass == this._elementType.getRawClass()) {
            return this;
        }
        return new CollectionType(this._class, this._elementType.widenBy(contentClass), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    public static CollectionType construct(final Class<?> rawType, final JavaType elemT) {
        return new CollectionType(rawType, elemT, null, null, false);
    }
    
    @Override
    public CollectionType withTypeHandler(final Object h) {
        return new CollectionType(this._class, this._elementType, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public CollectionType withContentTypeHandler(final Object h) {
        return new CollectionType(this._class, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionType withValueHandler(final Object h) {
        return new CollectionType(this._class, this._elementType, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionType withContentValueHandler(final Object h) {
        return new CollectionType(this._class, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new CollectionType(this._class, this._elementType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public String toString() {
        return "[collection type; class " + this._class.getName() + ", contains " + this._elementType + "]";
    }
}
