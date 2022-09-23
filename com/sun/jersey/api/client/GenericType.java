// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericType<T>
{
    private final Type t;
    private final Class c;
    
    protected GenericType() {
        final Type superclass = this.getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            throw new RuntimeException("Missing type parameter.");
        }
        final ParameterizedType parameterized = (ParameterizedType)superclass;
        this.t = parameterized.getActualTypeArguments()[0];
        this.c = getClass(this.t);
    }
    
    public GenericType(final Type genericType) {
        if (genericType == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        this.t = genericType;
        this.c = getClass(this.t);
    }
    
    private static Class getClass(final Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            if (parameterizedType.getRawType() instanceof Class) {
                return (Class)parameterizedType.getRawType();
            }
        }
        else if (type instanceof GenericArrayType) {
            final GenericArrayType array = (GenericArrayType)type;
            return ReflectionHelper.getArrayClass((Class)((ParameterizedType)array.getGenericComponentType()).getRawType());
        }
        throw new IllegalArgumentException("Type parameter not a class or parameterized type whose raw type is a class");
    }
    
    public final Type getType() {
        return this.t;
    }
    
    public final Class<T> getRawClass() {
        return (Class<T>)this.c;
    }
}
