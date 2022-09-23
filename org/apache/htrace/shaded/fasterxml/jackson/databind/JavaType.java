// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.core.type.ResolvedType;

public abstract class JavaType extends ResolvedType implements Serializable, Type
{
    private static final long serialVersionUID = 1L;
    protected final Class<?> _class;
    protected final int _hash;
    protected final Object _valueHandler;
    protected final Object _typeHandler;
    protected final boolean _asStatic;
    
    protected JavaType(final Class<?> raw, final int additionalHash, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        this._class = raw;
        this._hash = raw.getName().hashCode() + additionalHash;
        this._valueHandler = valueHandler;
        this._typeHandler = typeHandler;
        this._asStatic = asStatic;
    }
    
    public abstract JavaType withTypeHandler(final Object p0);
    
    public abstract JavaType withContentTypeHandler(final Object p0);
    
    public abstract JavaType withValueHandler(final Object p0);
    
    public abstract JavaType withContentValueHandler(final Object p0);
    
    public abstract JavaType withStaticTyping();
    
    public JavaType narrowBy(final Class<?> subclass) {
        if (subclass == this._class) {
            return this;
        }
        this._assertSubclass(subclass, this._class);
        JavaType result = this._narrow(subclass);
        if (this._valueHandler != result.getValueHandler()) {
            result = result.withValueHandler(this._valueHandler);
        }
        if (this._typeHandler != result.getTypeHandler()) {
            result = result.withTypeHandler(this._typeHandler);
        }
        return result;
    }
    
    public JavaType forcedNarrowBy(final Class<?> subclass) {
        if (subclass == this._class) {
            return this;
        }
        JavaType result = this._narrow(subclass);
        if (this._valueHandler != result.getValueHandler()) {
            result = result.withValueHandler(this._valueHandler);
        }
        if (this._typeHandler != result.getTypeHandler()) {
            result = result.withTypeHandler(this._typeHandler);
        }
        return result;
    }
    
    public JavaType widenBy(final Class<?> superclass) {
        if (superclass == this._class) {
            return this;
        }
        this._assertSubclass(this._class, superclass);
        return this._widen(superclass);
    }
    
    protected abstract JavaType _narrow(final Class<?> p0);
    
    protected JavaType _widen(final Class<?> superclass) {
        return this._narrow(superclass);
    }
    
    public abstract JavaType narrowContentsBy(final Class<?> p0);
    
    public abstract JavaType widenContentsBy(final Class<?> p0);
    
    @Override
    public final Class<?> getRawClass() {
        return this._class;
    }
    
    @Override
    public final boolean hasRawClass(final Class<?> clz) {
        return this._class == clz;
    }
    
    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this._class.getModifiers());
    }
    
    @Override
    public boolean isConcrete() {
        final int mod = this._class.getModifiers();
        return (mod & 0x600) == 0x0 || this._class.isPrimitive();
    }
    
    @Override
    public boolean isThrowable() {
        return Throwable.class.isAssignableFrom(this._class);
    }
    
    @Override
    public boolean isArrayType() {
        return false;
    }
    
    @Override
    public final boolean isEnumType() {
        return this._class.isEnum();
    }
    
    @Override
    public final boolean isInterface() {
        return this._class.isInterface();
    }
    
    @Override
    public final boolean isPrimitive() {
        return this._class.isPrimitive();
    }
    
    @Override
    public final boolean isFinal() {
        return Modifier.isFinal(this._class.getModifiers());
    }
    
    @Override
    public abstract boolean isContainerType();
    
    @Override
    public boolean isCollectionLikeType() {
        return false;
    }
    
    @Override
    public boolean isMapLikeType() {
        return false;
    }
    
    public final boolean useStaticType() {
        return this._asStatic;
    }
    
    @Override
    public boolean hasGenericTypes() {
        return this.containedTypeCount() > 0;
    }
    
    @Override
    public JavaType getKeyType() {
        return null;
    }
    
    @Override
    public JavaType getContentType() {
        return null;
    }
    
    @Override
    public int containedTypeCount() {
        return 0;
    }
    
    @Override
    public JavaType containedType(final int index) {
        return null;
    }
    
    @Override
    public String containedTypeName(final int index) {
        return null;
    }
    
    public <T> T getValueHandler() {
        return (T)this._valueHandler;
    }
    
    public <T> T getTypeHandler() {
        return (T)this._typeHandler;
    }
    
    public String getGenericSignature() {
        final StringBuilder sb = new StringBuilder(40);
        this.getGenericSignature(sb);
        return sb.toString();
    }
    
    public abstract StringBuilder getGenericSignature(final StringBuilder p0);
    
    public String getErasedSignature() {
        final StringBuilder sb = new StringBuilder(40);
        this.getErasedSignature(sb);
        return sb.toString();
    }
    
    public abstract StringBuilder getErasedSignature(final StringBuilder p0);
    
    protected void _assertSubclass(final Class<?> subclass, final Class<?> superClass) {
        if (!this._class.isAssignableFrom(subclass)) {
            throw new IllegalArgumentException("Class " + subclass.getName() + " is not assignable to " + this._class.getName());
        }
    }
    
    @Override
    public abstract String toString();
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public final int hashCode() {
        return this._hash;
    }
}
