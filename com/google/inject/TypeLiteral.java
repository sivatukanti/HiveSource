// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.Field;
import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;
import com.google.inject.internal.util.$ImmutableList;
import java.util.List;
import com.google.inject.util.Types;
import java.lang.reflect.ParameterizedType;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.MoreTypes;
import java.lang.reflect.Type;

public class TypeLiteral<T>
{
    final Class<? super T> rawType;
    final Type type;
    final int hashCode;
    
    protected TypeLiteral() {
        this.type = getSuperclassTypeParameter(this.getClass());
        this.rawType = (Class<? super T>)MoreTypes.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }
    
    TypeLiteral(final Type type) {
        this.type = MoreTypes.canonicalize($Preconditions.checkNotNull(type, (Object)"type"));
        this.rawType = (Class<? super T>)MoreTypes.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }
    
    static Type getSuperclassTypeParameter(final Class<?> subclass) {
        final Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        final ParameterizedType parameterized = (ParameterizedType)superclass;
        return MoreTypes.canonicalize(parameterized.getActualTypeArguments()[0]);
    }
    
    static TypeLiteral<?> fromSuperclassTypeParameter(final Class<?> subclass) {
        return new TypeLiteral<Object>(getSuperclassTypeParameter(subclass));
    }
    
    public final Class<? super T> getRawType() {
        return this.rawType;
    }
    
    public final Type getType() {
        return this.type;
    }
    
    final TypeLiteral<Provider<T>> providerType() {
        return (TypeLiteral<Provider<T>>)get(Types.providerOf(this.getType()));
    }
    
    @Override
    public final int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public final boolean equals(final Object o) {
        return o instanceof TypeLiteral && MoreTypes.equals(this.type, ((TypeLiteral)o).type);
    }
    
    @Override
    public final String toString() {
        return MoreTypes.typeToString(this.type);
    }
    
    public static TypeLiteral<?> get(final Type type) {
        return new TypeLiteral<Object>(type);
    }
    
    public static <T> TypeLiteral<T> get(final Class<T> type) {
        return new TypeLiteral<T>(type);
    }
    
    private List<TypeLiteral<?>> resolveAll(final Type[] types) {
        final TypeLiteral<?>[] result = (TypeLiteral<?>[])new TypeLiteral[types.length];
        for (int t = 0; t < types.length; ++t) {
            result[t] = this.resolve(types[t]);
        }
        return $ImmutableList.of(result);
    }
    
    TypeLiteral<?> resolve(final Type toResolve) {
        return get(this.resolveType(toResolve));
    }
    
    Type resolveType(Type toResolve) {
        while (toResolve instanceof TypeVariable) {
            final TypeVariable original = (TypeVariable)toResolve;
            toResolve = MoreTypes.resolveTypeVariable(this.type, this.rawType, original);
            if (toResolve == original) {
                return toResolve;
            }
        }
        if (toResolve instanceof GenericArrayType) {
            final GenericArrayType original2 = (GenericArrayType)toResolve;
            final Type componentType = original2.getGenericComponentType();
            final Type newComponentType = this.resolveType(componentType);
            return (componentType == newComponentType) ? original2 : Types.arrayOf(newComponentType);
        }
        if (toResolve instanceof ParameterizedType) {
            final ParameterizedType original3 = (ParameterizedType)toResolve;
            final Type ownerType = original3.getOwnerType();
            final Type newOwnerType = this.resolveType(ownerType);
            boolean changed = newOwnerType != ownerType;
            Type[] args = original3.getActualTypeArguments();
            for (int t = 0, length = args.length; t < length; ++t) {
                final Type resolvedTypeArgument = this.resolveType(args[t]);
                if (resolvedTypeArgument != args[t]) {
                    if (!changed) {
                        args = args.clone();
                        changed = true;
                    }
                    args[t] = resolvedTypeArgument;
                }
            }
            return changed ? Types.newParameterizedTypeWithOwner(newOwnerType, original3.getRawType(), args) : original3;
        }
        if (toResolve instanceof WildcardType) {
            final WildcardType original4 = (WildcardType)toResolve;
            final Type[] originalLowerBound = original4.getLowerBounds();
            final Type[] originalUpperBound = original4.getUpperBounds();
            if (originalLowerBound.length == 1) {
                final Type lowerBound = this.resolveType(originalLowerBound[0]);
                if (lowerBound != originalLowerBound[0]) {
                    return Types.supertypeOf(lowerBound);
                }
            }
            else if (originalUpperBound.length == 1) {
                final Type upperBound = this.resolveType(originalUpperBound[0]);
                if (upperBound != originalUpperBound[0]) {
                    return Types.subtypeOf(upperBound);
                }
            }
            return original4;
        }
        return toResolve;
    }
    
    public TypeLiteral<?> getSupertype(final Class<?> supertype) {
        $Preconditions.checkArgument(supertype.isAssignableFrom(this.rawType), "%s is not a supertype of %s", supertype, this.type);
        return this.resolve(MoreTypes.getGenericSupertype(this.type, this.rawType, supertype));
    }
    
    public TypeLiteral<?> getFieldType(final Field field) {
        $Preconditions.checkArgument(field.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", field, this.type);
        return this.resolve(field.getGenericType());
    }
    
    public List<TypeLiteral<?>> getParameterTypes(final Member methodOrConstructor) {
        Type[] genericParameterTypes;
        if (methodOrConstructor instanceof Method) {
            final Method method = (Method)methodOrConstructor;
            $Preconditions.checkArgument(method.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", method, this.type);
            genericParameterTypes = method.getGenericParameterTypes();
        }
        else {
            if (!(methodOrConstructor instanceof Constructor)) {
                throw new IllegalArgumentException("Not a method or a constructor: " + methodOrConstructor);
            }
            final Constructor<?> constructor = (Constructor<?>)methodOrConstructor;
            $Preconditions.checkArgument(constructor.getDeclaringClass().isAssignableFrom(this.rawType), "%s does not construct a supertype of %s", constructor, this.type);
            genericParameterTypes = constructor.getGenericParameterTypes();
        }
        return this.resolveAll(genericParameterTypes);
    }
    
    public List<TypeLiteral<?>> getExceptionTypes(final Member methodOrConstructor) {
        Type[] genericExceptionTypes;
        if (methodOrConstructor instanceof Method) {
            final Method method = (Method)methodOrConstructor;
            $Preconditions.checkArgument(method.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", method, this.type);
            genericExceptionTypes = method.getGenericExceptionTypes();
        }
        else {
            if (!(methodOrConstructor instanceof Constructor)) {
                throw new IllegalArgumentException("Not a method or a constructor: " + methodOrConstructor);
            }
            final Constructor<?> constructor = (Constructor<?>)methodOrConstructor;
            $Preconditions.checkArgument(constructor.getDeclaringClass().isAssignableFrom(this.rawType), "%s does not construct a supertype of %s", constructor, this.type);
            genericExceptionTypes = constructor.getGenericExceptionTypes();
        }
        return this.resolveAll(genericExceptionTypes);
    }
    
    public TypeLiteral<?> getReturnType(final Method method) {
        $Preconditions.checkArgument(method.getDeclaringClass().isAssignableFrom(this.rawType), "%s is not defined by a supertype of %s", method, this.type);
        return this.resolve(method.getGenericReturnType());
    }
}
