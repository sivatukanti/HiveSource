// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.type;

import org.apache.htrace.shaded.fasterxml.jackson.core.type.ResolvedType;
import java.lang.reflect.Type;
import java.lang.reflect.Array;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public final class ArrayType extends TypeBase
{
    private static final long serialVersionUID = 9040058063449087477L;
    protected final JavaType _componentType;
    protected final Object _emptyArray;
    
    private ArrayType(final JavaType componentType, final Object emptyInstance, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(emptyInstance.getClass(), componentType.hashCode(), valueHandler, typeHandler, asStatic);
        this._componentType = componentType;
        this._emptyArray = emptyInstance;
    }
    
    public static ArrayType construct(final JavaType componentType, final Object valueHandler, final Object typeHandler) {
        final Object emptyInstance = Array.newInstance(componentType.getRawClass(), 0);
        return new ArrayType(componentType, emptyInstance, null, null, false);
    }
    
    @Override
    public ArrayType withTypeHandler(final Object h) {
        if (h == this._typeHandler) {
            return this;
        }
        return new ArrayType(this._componentType, this._emptyArray, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public ArrayType withContentTypeHandler(final Object h) {
        if (h == this._componentType.getTypeHandler()) {
            return this;
        }
        return new ArrayType(this._componentType.withTypeHandler(h), this._emptyArray, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ArrayType withValueHandler(final Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new ArrayType(this._componentType, this._emptyArray, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ArrayType withContentValueHandler(final Object h) {
        if (h == this._componentType.getValueHandler()) {
            return this;
        }
        return new ArrayType(this._componentType.withValueHandler(h), this._emptyArray, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ArrayType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new ArrayType(this._componentType.withStaticTyping(), this._emptyArray, this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    protected String buildCanonicalName() {
        return this._class.getName();
    }
    
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        if (!subclass.isArray()) {
            throw new IllegalArgumentException("Incompatible narrowing operation: trying to narrow " + this.toString() + " to class " + subclass.getName());
        }
        final Class<?> newCompClass = subclass.getComponentType();
        final JavaType newCompType = TypeFactory.defaultInstance().constructType(newCompClass);
        return construct(newCompType, this._valueHandler, this._typeHandler);
    }
    
    @Override
    public JavaType narrowContentsBy(final Class<?> contentClass) {
        if (contentClass == this._componentType.getRawClass()) {
            return this;
        }
        return construct(this._componentType.narrowBy(contentClass), this._valueHandler, this._typeHandler);
    }
    
    @Override
    public JavaType widenContentsBy(final Class<?> contentClass) {
        if (contentClass == this._componentType.getRawClass()) {
            return this;
        }
        return construct(this._componentType.widenBy(contentClass), this._valueHandler, this._typeHandler);
    }
    
    @Override
    public boolean isArrayType() {
        return true;
    }
    
    @Override
    public boolean isAbstract() {
        return false;
    }
    
    @Override
    public boolean isConcrete() {
        return true;
    }
    
    @Override
    public boolean hasGenericTypes() {
        return this._componentType.hasGenericTypes();
    }
    
    @Override
    public String containedTypeName(final int index) {
        if (index == 0) {
            return "E";
        }
        return null;
    }
    
    @Override
    public boolean isContainerType() {
        return true;
    }
    
    @Override
    public JavaType getContentType() {
        return this._componentType;
    }
    
    @Override
    public int containedTypeCount() {
        return 1;
    }
    
    @Override
    public JavaType containedType(final int index) {
        return (index == 0) ? this._componentType : null;
    }
    
    @Override
    public StringBuilder getGenericSignature(final StringBuilder sb) {
        sb.append('[');
        return this._componentType.getGenericSignature(sb);
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        sb.append('[');
        return this._componentType.getErasedSignature(sb);
    }
    
    @Override
    public String toString() {
        return "[array type, component type: " + this._componentType + "]";
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
        final ArrayType other = (ArrayType)o;
        return this._componentType.equals(other._componentType);
    }
}
