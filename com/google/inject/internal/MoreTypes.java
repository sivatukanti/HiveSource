// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.io.Serializable;
import com.google.inject.internal.util.$ImmutableMap;
import java.lang.reflect.GenericDeclaration;
import java.util.NoSuchElementException;
import java.util.Arrays;
import com.google.inject.internal.util.$Objects;
import java.lang.reflect.Array;
import com.google.inject.internal.util.$Preconditions;
import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;
import com.google.inject.util.Types;
import java.lang.reflect.ParameterizedType;
import javax.inject.Provider;
import com.google.inject.spi.Message;
import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import java.util.Map;
import java.lang.reflect.Type;

public class MoreTypes
{
    public static final Type[] EMPTY_TYPE_ARRAY;
    private static final Map<TypeLiteral<?>, TypeLiteral<?>> PRIMITIVE_TO_WRAPPER;
    
    private MoreTypes() {
    }
    
    public static <T> TypeLiteral<T> canonicalizeForKey(final TypeLiteral<T> typeLiteral) {
        final Type type = typeLiteral.getType();
        if (!isFullySpecified(type)) {
            final Errors errors = new Errors().keyNotFullySpecified(typeLiteral);
            throw new ConfigurationException(errors.getMessages());
        }
        if (typeLiteral.getRawType() == Provider.class) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            final TypeLiteral<T> guiceProviderType = (TypeLiteral<T>)TypeLiteral.get(Types.providerOf(parameterizedType.getActualTypeArguments()[0]));
            return guiceProviderType;
        }
        final TypeLiteral<T> wrappedPrimitives = (TypeLiteral<T>)MoreTypes.PRIMITIVE_TO_WRAPPER.get(typeLiteral);
        return (wrappedPrimitives != null) ? wrappedPrimitives : typeLiteral;
    }
    
    private static boolean isFullySpecified(final Type type) {
        if (type instanceof Class) {
            return true;
        }
        if (type instanceof CompositeType) {
            return ((CompositeType)type).isFullySpecified();
        }
        return !(type instanceof TypeVariable) && ((CompositeType)canonicalize(type)).isFullySpecified();
    }
    
    public static Type canonicalize(final Type type) {
        if (type instanceof Class) {
            final Class<?> c = (Class<?>)type;
            return (Type)(c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c);
        }
        if (type instanceof CompositeType) {
            return type;
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType p = (ParameterizedType)type;
            return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());
        }
        if (type instanceof GenericArrayType) {
            final GenericArrayType g = (GenericArrayType)type;
            return new GenericArrayTypeImpl(g.getGenericComponentType());
        }
        if (type instanceof WildcardType) {
            final WildcardType w = (WildcardType)type;
            return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
        }
        return type;
    }
    
    public static Class<?> getRawType(final Type type) {
        if (type instanceof Class) {
            return (Class<?>)type;
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            final Type rawType = parameterizedType.getRawType();
            $Preconditions.checkArgument(rawType instanceof Class, "Expected a Class, but <%s> is of type %s", type, type.getClass().getName());
            return (Class<?>)rawType;
        }
        if (type instanceof GenericArrayType) {
            final Type componentType = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
    }
    
    public static boolean equals(final Type a, final Type b) {
        if (a == b) {
            return true;
        }
        if (a instanceof Class) {
            return a.equals(b);
        }
        if (a instanceof ParameterizedType) {
            if (!(b instanceof ParameterizedType)) {
                return false;
            }
            final ParameterizedType pa = (ParameterizedType)a;
            final ParameterizedType pb = (ParameterizedType)b;
            return $Objects.equal(pa.getOwnerType(), pb.getOwnerType()) && pa.getRawType().equals(pb.getRawType()) && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());
        }
        else if (a instanceof GenericArrayType) {
            if (!(b instanceof GenericArrayType)) {
                return false;
            }
            final GenericArrayType ga = (GenericArrayType)a;
            final GenericArrayType gb = (GenericArrayType)b;
            return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
        }
        else if (a instanceof WildcardType) {
            if (!(b instanceof WildcardType)) {
                return false;
            }
            final WildcardType wa = (WildcardType)a;
            final WildcardType wb = (WildcardType)b;
            return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds()) && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());
        }
        else {
            if (!(a instanceof TypeVariable)) {
                return false;
            }
            if (!(b instanceof TypeVariable)) {
                return false;
            }
            final TypeVariable<?> va = (TypeVariable<?>)a;
            final TypeVariable<?> vb = (TypeVariable<?>)b;
            return va.getGenericDeclaration() == vb.getGenericDeclaration() && va.getName().equals(vb.getName());
        }
    }
    
    private static int hashCodeOrZero(final Object o) {
        return (o != null) ? o.hashCode() : 0;
    }
    
    public static String typeToString(final Type type) {
        return (type instanceof Class) ? ((Class)type).getName() : type.toString();
    }
    
    public static Type getGenericSupertype(final Type type, Class<?> rawType, final Class<?> toResolve) {
        if (toResolve == rawType) {
            return type;
        }
        if (toResolve.isInterface()) {
            final Class[] interfaces = rawType.getInterfaces();
            for (int i = 0, length = interfaces.length; i < length; ++i) {
                if (interfaces[i] == toResolve) {
                    return rawType.getGenericInterfaces()[i];
                }
                if (toResolve.isAssignableFrom(interfaces[i])) {
                    return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
                }
            }
        }
        if (!rawType.isInterface()) {
            while (rawType != Object.class) {
                final Class<?> rawSupertype = rawType.getSuperclass();
                if (rawSupertype == toResolve) {
                    return rawType.getGenericSuperclass();
                }
                if (toResolve.isAssignableFrom(rawSupertype)) {
                    return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
                }
                rawType = rawSupertype;
            }
        }
        return toResolve;
    }
    
    public static Type resolveTypeVariable(final Type type, final Class<?> rawType, final TypeVariable unknown) {
        final Class<?> declaredByRaw = declaringClassOf(unknown);
        if (declaredByRaw == null) {
            return unknown;
        }
        final Type declaredBy = getGenericSupertype(type, rawType, declaredByRaw);
        if (declaredBy instanceof ParameterizedType) {
            final int index = indexOf(declaredByRaw.getTypeParameters(), unknown);
            return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
        }
        return unknown;
    }
    
    private static int indexOf(final Object[] array, final Object toFind) {
        for (int i = 0; i < array.length; ++i) {
            if (toFind.equals(array[i])) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }
    
    private static Class<?> declaringClassOf(final TypeVariable typeVariable) {
        final GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
        return (Class<?>)((genericDeclaration instanceof Class) ? ((Class)genericDeclaration) : null);
    }
    
    private static void checkNotPrimitive(final Type type, final String use) {
        $Preconditions.checkArgument(!(type instanceof Class) || !((Class)type).isPrimitive(), "Primitive types are not allowed in %s: %s", use, type);
    }
    
    static {
        EMPTY_TYPE_ARRAY = new Type[0];
        PRIMITIVE_TO_WRAPPER = new $ImmutableMap.Builder<TypeLiteral<Boolean>, TypeLiteral<Boolean>>().put(TypeLiteral.get(Boolean.TYPE), TypeLiteral.get(Boolean.class)).put(TypeLiteral.get((Class<Boolean>)Byte.TYPE), TypeLiteral.get((Class<Boolean>)Byte.class)).put(TypeLiteral.get((Class<Boolean>)Short.TYPE), TypeLiteral.get((Class<Boolean>)Short.class)).put(TypeLiteral.get((Class<Boolean>)Integer.TYPE), TypeLiteral.get((Class<Boolean>)Integer.class)).put(TypeLiteral.get((Class<Boolean>)Long.TYPE), TypeLiteral.get((Class<Boolean>)Long.class)).put(TypeLiteral.get((Class<Boolean>)Float.TYPE), TypeLiteral.get((Class<Boolean>)Float.class)).put(TypeLiteral.get((Class<Boolean>)Double.TYPE), TypeLiteral.get((Class<Boolean>)Double.class)).put(TypeLiteral.get((Class<Boolean>)Character.TYPE), TypeLiteral.get((Class<Boolean>)Character.class)).put(TypeLiteral.get((Class<Boolean>)Void.TYPE), TypeLiteral.get((Class<Boolean>)Void.class)).build();
    }
    
    public static class ParameterizedTypeImpl implements ParameterizedType, Serializable, CompositeType
    {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;
        private static final long serialVersionUID = 0L;
        
        public ParameterizedTypeImpl(final Type ownerType, final Type rawType, final Type... typeArguments) {
            if (rawType instanceof Class) {
                final Class rawTypeAsClass = (Class)rawType;
                $Preconditions.checkArgument(ownerType != null || rawTypeAsClass.getEnclosingClass() == null, "No owner type for enclosed %s", rawType);
                $Preconditions.checkArgument(ownerType == null || rawTypeAsClass.getEnclosingClass() != null, "Owner type for unenclosed %s", rawType);
            }
            this.ownerType = ((ownerType == null) ? null : MoreTypes.canonicalize(ownerType));
            this.rawType = MoreTypes.canonicalize(rawType);
            this.typeArguments = typeArguments.clone();
            for (int t = 0; t < this.typeArguments.length; ++t) {
                $Preconditions.checkNotNull(this.typeArguments[t], (Object)"type parameter");
                checkNotPrimitive(this.typeArguments[t], "type parameters");
                this.typeArguments[t] = MoreTypes.canonicalize(this.typeArguments[t]);
            }
        }
        
        public Type[] getActualTypeArguments() {
            return this.typeArguments.clone();
        }
        
        public Type getRawType() {
            return this.rawType;
        }
        
        public Type getOwnerType() {
            return this.ownerType;
        }
        
        public boolean isFullySpecified() {
            if (this.ownerType != null && !isFullySpecified(this.ownerType)) {
                return false;
            }
            if (!isFullySpecified(this.rawType)) {
                return false;
            }
            for (final Type type : this.typeArguments) {
                if (!isFullySpecified(type)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof ParameterizedType && MoreTypes.equals(this, (Type)other);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ hashCodeOrZero(this.ownerType);
        }
        
        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder(30 * (this.typeArguments.length + 1));
            stringBuilder.append(MoreTypes.typeToString(this.rawType));
            if (this.typeArguments.length == 0) {
                return stringBuilder.toString();
            }
            stringBuilder.append("<").append(MoreTypes.typeToString(this.typeArguments[0]));
            for (int i = 1; i < this.typeArguments.length; ++i) {
                stringBuilder.append(", ").append(MoreTypes.typeToString(this.typeArguments[i]));
            }
            return stringBuilder.append(">").toString();
        }
    }
    
    public static class GenericArrayTypeImpl implements GenericArrayType, Serializable, CompositeType
    {
        private final Type componentType;
        private static final long serialVersionUID = 0L;
        
        public GenericArrayTypeImpl(final Type componentType) {
            this.componentType = MoreTypes.canonicalize(componentType);
        }
        
        public Type getGenericComponentType() {
            return this.componentType;
        }
        
        public boolean isFullySpecified() {
            return isFullySpecified(this.componentType);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof GenericArrayType && MoreTypes.equals(this, (Type)o);
        }
        
        @Override
        public int hashCode() {
            return this.componentType.hashCode();
        }
        
        @Override
        public String toString() {
            return MoreTypes.typeToString(this.componentType) + "[]";
        }
    }
    
    public static class WildcardTypeImpl implements WildcardType, Serializable, CompositeType
    {
        private final Type upperBound;
        private final Type lowerBound;
        private static final long serialVersionUID = 0L;
        
        public WildcardTypeImpl(final Type[] upperBounds, final Type[] lowerBounds) {
            $Preconditions.checkArgument(lowerBounds.length <= 1, (Object)"Must have at most one lower bound.");
            $Preconditions.checkArgument(upperBounds.length == 1, (Object)"Must have exactly one upper bound.");
            if (lowerBounds.length == 1) {
                $Preconditions.checkNotNull(lowerBounds[0], (Object)"lowerBound");
                checkNotPrimitive(lowerBounds[0], "wildcard bounds");
                $Preconditions.checkArgument(upperBounds[0] == Object.class, (Object)"bounded both ways");
                this.lowerBound = MoreTypes.canonicalize(lowerBounds[0]);
                this.upperBound = Object.class;
            }
            else {
                $Preconditions.checkNotNull(upperBounds[0], (Object)"upperBound");
                checkNotPrimitive(upperBounds[0], "wildcard bounds");
                this.lowerBound = null;
                this.upperBound = MoreTypes.canonicalize(upperBounds[0]);
            }
        }
        
        public Type[] getUpperBounds() {
            return new Type[] { this.upperBound };
        }
        
        public Type[] getLowerBounds() {
            return (this.lowerBound != null) ? new Type[] { this.lowerBound } : MoreTypes.EMPTY_TYPE_ARRAY;
        }
        
        public boolean isFullySpecified() {
            return isFullySpecified(this.upperBound) && (this.lowerBound == null || isFullySpecified(this.lowerBound));
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof WildcardType && MoreTypes.equals(this, (Type)other);
        }
        
        @Override
        public int hashCode() {
            return ((this.lowerBound != null) ? (31 + this.lowerBound.hashCode()) : 1) ^ 31 + this.upperBound.hashCode();
        }
        
        @Override
        public String toString() {
            if (this.lowerBound != null) {
                return "? super " + MoreTypes.typeToString(this.lowerBound);
            }
            if (this.upperBound == Object.class) {
                return "?";
            }
            return "? extends " + MoreTypes.typeToString(this.upperBound);
        }
    }
    
    private interface CompositeType
    {
        boolean isFullySpecified();
    }
}
