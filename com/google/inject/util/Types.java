// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.util;

import com.google.inject.Provider;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import com.google.inject.internal.MoreTypes;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class Types
{
    private Types() {
    }
    
    public static ParameterizedType newParameterizedType(final Type rawType, final Type... typeArguments) {
        return newParameterizedTypeWithOwner(null, rawType, typeArguments);
    }
    
    public static ParameterizedType newParameterizedTypeWithOwner(final Type ownerType, final Type rawType, final Type... typeArguments) {
        return new MoreTypes.ParameterizedTypeImpl(ownerType, rawType, typeArguments);
    }
    
    public static GenericArrayType arrayOf(final Type componentType) {
        return new MoreTypes.GenericArrayTypeImpl(componentType);
    }
    
    public static WildcardType subtypeOf(final Type bound) {
        return new MoreTypes.WildcardTypeImpl(new Type[] { bound }, MoreTypes.EMPTY_TYPE_ARRAY);
    }
    
    public static WildcardType supertypeOf(final Type bound) {
        return new MoreTypes.WildcardTypeImpl(new Type[] { Object.class }, new Type[] { bound });
    }
    
    public static ParameterizedType listOf(final Type elementType) {
        return newParameterizedType(List.class, elementType);
    }
    
    public static ParameterizedType setOf(final Type elementType) {
        return newParameterizedType(Set.class, elementType);
    }
    
    public static ParameterizedType mapOf(final Type keyType, final Type valueType) {
        return newParameterizedType(Map.class, keyType, valueType);
    }
    
    public static ParameterizedType providerOf(final Type providedType) {
        return newParameterizedType(Provider.class, providedType);
    }
}
