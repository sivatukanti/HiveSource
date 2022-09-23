// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.type;

import org.apache.htrace.shaded.fasterxml.jackson.core.type.ResolvedType;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public class CollectionLikeType extends TypeBase
{
    private static final long serialVersionUID = 4611641304150899138L;
    protected final JavaType _elementType;
    
    protected CollectionLikeType(final Class<?> collT, final JavaType elemT, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(collT, elemT.hashCode(), valueHandler, typeHandler, asStatic);
        this._elementType = elemT;
    }
    
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return new CollectionLikeType(subclass, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType narrowContentsBy(final Class<?> contentClass) {
        if (contentClass == this._elementType.getRawClass()) {
            return this;
        }
        return new CollectionLikeType(this._class, this._elementType.narrowBy(contentClass), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType widenContentsBy(final Class<?> contentClass) {
        if (contentClass == this._elementType.getRawClass()) {
            return this;
        }
        return new CollectionLikeType(this._class, this._elementType.widenBy(contentClass), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    public static CollectionLikeType construct(final Class<?> rawType, final JavaType elemT) {
        return new CollectionLikeType(rawType, elemT, null, null, false);
    }
    
    @Override
    public CollectionLikeType withTypeHandler(final Object h) {
        return new CollectionLikeType(this._class, this._elementType, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public CollectionLikeType withContentTypeHandler(final Object h) {
        return new CollectionLikeType(this._class, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionLikeType withValueHandler(final Object h) {
        return new CollectionLikeType(this._class, this._elementType, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionLikeType withContentValueHandler(final Object h) {
        return new CollectionLikeType(this._class, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionLikeType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new CollectionLikeType(this._class, this._elementType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public boolean isContainerType() {
        return true;
    }
    
    @Override
    public boolean isCollectionLikeType() {
        return true;
    }
    
    @Override
    public JavaType getContentType() {
        return this._elementType;
    }
    
    @Override
    public int containedTypeCount() {
        return 1;
    }
    
    @Override
    public JavaType containedType(final int index) {
        return (index == 0) ? this._elementType : null;
    }
    
    @Override
    public String containedTypeName(final int index) {
        if (index == 0) {
            return "E";
        }
        return null;
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        return TypeBase._classSignature(this._class, sb, true);
    }
    
    @Override
    public StringBuilder getGenericSignature(final StringBuilder sb) {
        TypeBase._classSignature(this._class, sb, false);
        sb.append('<');
        this._elementType.getGenericSignature(sb);
        sb.append(">;");
        return sb;
    }
    
    @Override
    protected String buildCanonicalName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        if (this._elementType != null) {
            sb.append('<');
            sb.append(this._elementType.toCanonical());
            sb.append('>');
        }
        return sb.toString();
    }
    
    public boolean isTrueCollectionType() {
        return Collection.class.isAssignableFrom(this._class);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final CollectionLikeType other = (CollectionLikeType)o;
        return this._class == other._class && this._elementType.equals(other._elementType);
    }
    
    @Override
    public String toString() {
        return "[collection-like type; class " + this._class.getName() + ", contains " + this._elementType + "]";
    }
}
