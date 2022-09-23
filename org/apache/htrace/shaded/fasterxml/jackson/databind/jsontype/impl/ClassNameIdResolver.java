// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.impl;

import java.util.Map;
import java.util.EnumMap;
import java.util.Collection;
import java.util.EnumSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DatabindContext;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public class ClassNameIdResolver extends TypeIdResolverBase
{
    public ClassNameIdResolver(final JavaType baseType, final TypeFactory typeFactory) {
        super(baseType, typeFactory);
    }
    
    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CLASS;
    }
    
    public void registerSubtype(final Class<?> type, final String name) {
    }
    
    @Override
    public String idFromValue(final Object value) {
        return this._idFrom(value, value.getClass());
    }
    
    @Override
    public String idFromValueAndType(final Object value, final Class<?> type) {
        return this._idFrom(value, type);
    }
    
    @Deprecated
    @Override
    public JavaType typeFromId(final String id) {
        return this._typeFromId(id, this._typeFactory);
    }
    
    @Override
    public JavaType typeFromId(final DatabindContext context, final String id) {
        return this._typeFromId(id, context.getTypeFactory());
    }
    
    protected JavaType _typeFromId(final String id, final TypeFactory typeFactory) {
        if (id.indexOf(60) > 0) {
            final JavaType t = typeFactory.constructFromCanonical(id);
            return t;
        }
        try {
            final Class<?> cls = ClassUtil.findClass(id);
            return typeFactory.constructSpecializedType(this._baseType, cls);
        }
        catch (ClassNotFoundException e2) {
            throw new IllegalArgumentException("Invalid type id '" + id + "' (for id type 'Id.class'): no such class found");
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid type id '" + id + "' (for id type 'Id.class'): " + e.getMessage(), e);
        }
    }
    
    protected final String _idFrom(final Object value, Class<?> cls) {
        if (Enum.class.isAssignableFrom(cls) && !cls.isEnum()) {
            cls = cls.getSuperclass();
        }
        String str = cls.getName();
        if (str.startsWith("java.util")) {
            if (value instanceof EnumSet) {
                final Class<?> enumClass = ClassUtil.findEnumType((EnumSet<?>)value);
                str = TypeFactory.defaultInstance().constructCollectionType(EnumSet.class, enumClass).toCanonical();
            }
            else if (value instanceof EnumMap) {
                final Class<?> enumClass = ClassUtil.findEnumType((EnumMap<?, ?>)value);
                final Class<?> valueClass = Object.class;
                str = TypeFactory.defaultInstance().constructMapType(EnumMap.class, enumClass, valueClass).toCanonical();
            }
            else {
                final String end = str.substring(9);
                if ((end.startsWith(".Arrays$") || end.startsWith(".Collections$")) && str.indexOf("List") >= 0) {
                    str = "java.util.ArrayList";
                }
            }
        }
        else if (str.indexOf(36) >= 0) {
            final Class<?> outer = ClassUtil.getOuterClass(cls);
            if (outer != null) {
                final Class<?> staticType = this._baseType.getRawClass();
                if (ClassUtil.getOuterClass(staticType) == null) {
                    cls = this._baseType.getRawClass();
                    str = cls.getName();
                }
            }
        }
        return str;
    }
}
