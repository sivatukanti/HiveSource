// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.reflection;

import com.sun.jersey.core.osgi.OsgiRegistry;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import com.sun.jersey.impl.ImplMessages;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

public class ReflectionHelper
{
    private static final Logger LOGGER;
    private static final PrivilegedAction NoOpPrivilegedACTION;
    
    public static Class getDeclaringClass(final AccessibleObject ao) {
        if (ao instanceof Method) {
            return ((Method)ao).getDeclaringClass();
        }
        if (ao instanceof Field) {
            return ((Field)ao).getDeclaringClass();
        }
        if (ao instanceof Constructor) {
            return ((Constructor)ao).getDeclaringClass();
        }
        throw new RuntimeException();
    }
    
    public static String objectToString(final Object o) {
        if (o == null) {
            return "null";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(o.getClass().getName()).append('@').append(Integer.toHexString(o.hashCode()));
        return sb.toString();
    }
    
    public static String methodInstanceToString(final Object o, final Method m) {
        final StringBuffer sb = new StringBuffer();
        sb.append(o.getClass().getName()).append('@').append(Integer.toHexString(o.hashCode())).append('.').append(m.getName()).append('(');
        final Class[] params = m.getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            sb.append(getTypeName(params[i]));
            if (i < params.length - 1) {
                sb.append(",");
            }
        }
        sb.append(')');
        return sb.toString();
    }
    
    private static String getTypeName(final Class type) {
        if (type.isArray()) {
            try {
                Class cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    ++dimensions;
                    cl = cl.getComponentType();
                }
                final StringBuffer sb = new StringBuffer();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; ++i) {
                    sb.append("[]");
                }
                return sb.toString();
            }
            catch (Throwable t) {}
        }
        return type.getName();
    }
    
    public static PrivilegedAction<Class<?>> classForNamePA(final String name) {
        return classForNamePA(name, getContextClassLoader());
    }
    
    public static PrivilegedAction<Class<?>> classForNamePA(final String name, final ClassLoader cl) {
        return new PrivilegedAction<Class<?>>() {
            @Override
            public Class<?> run() {
                if (cl != null) {
                    try {
                        return Class.forName(name, false, cl);
                    }
                    catch (ClassNotFoundException ex) {
                        if (ReflectionHelper.LOGGER.isLoggable(Level.FINE)) {
                            ReflectionHelper.LOGGER.log(Level.FINE, "Unable to load class " + name + " using the supplied class loader " + cl.getClass().getName() + ".", ex);
                        }
                    }
                }
                try {
                    return Class.forName(name);
                }
                catch (ClassNotFoundException ex) {
                    if (ReflectionHelper.LOGGER.isLoggable(Level.FINE)) {
                        ReflectionHelper.LOGGER.log(Level.FINE, "Unable to load class " + name + " using the current class loader.", ex);
                    }
                    return null;
                }
            }
        };
    }
    
    public static <T> PrivilegedExceptionAction<Class<T>> classForNameWithExceptionPEA(final String name) throws ClassNotFoundException {
        return classForNameWithExceptionPEA(name, getContextClassLoader());
    }
    
    public static <T> PrivilegedExceptionAction<Class<T>> classForNameWithExceptionPEA(final String name, final ClassLoader cl) throws ClassNotFoundException {
        return new PrivilegedExceptionAction<Class<T>>() {
            @Override
            public Class<T> run() throws ClassNotFoundException {
                if (cl != null) {
                    try {
                        return (Class<T>)Class.forName(name, false, cl);
                    }
                    catch (ClassNotFoundException ex) {}
                }
                return (Class<T>)Class.forName(name);
            }
        };
    }
    
    public static PrivilegedAction<ClassLoader> getContextClassLoaderPA() {
        return new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        };
    }
    
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(getContextClassLoaderPA());
    }
    
    public static PrivilegedAction setAccessibleMethodPA(final Method m) {
        if (Modifier.isPublic(m.getModifiers())) {
            return ReflectionHelper.NoOpPrivilegedACTION;
        }
        return new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                if (!m.isAccessible()) {
                    m.setAccessible(true);
                }
                return m;
            }
        };
    }
    
    public static Class getGenericClass(final Type parameterizedType) throws IllegalArgumentException {
        final Type t = getTypeArgumentOfParameterizedType(parameterizedType);
        if (t == null) {
            return null;
        }
        final Class c = getClassOfType(t);
        if (c == null) {
            throw new IllegalArgumentException(ImplMessages.GENERIC_TYPE_NOT_SUPPORTED(t, parameterizedType));
        }
        return c;
    }
    
    public static TypeClassPair getTypeArgumentAndClass(final Type parameterizedType) throws IllegalArgumentException {
        final Type t = getTypeArgumentOfParameterizedType(parameterizedType);
        if (t == null) {
            return null;
        }
        final Class c = getClassOfType(t);
        if (c == null) {
            throw new IllegalArgumentException(ImplMessages.GENERIC_TYPE_NOT_SUPPORTED(t, parameterizedType));
        }
        return new TypeClassPair(t, c);
    }
    
    private static Type getTypeArgumentOfParameterizedType(final Type parameterizedType) {
        if (!(parameterizedType instanceof ParameterizedType)) {
            return null;
        }
        final ParameterizedType type = (ParameterizedType)parameterizedType;
        final Type[] genericTypes = type.getActualTypeArguments();
        if (genericTypes.length != 1) {
            return null;
        }
        return genericTypes[0];
    }
    
    private static Class getClassOfType(final Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof GenericArrayType) {
            final GenericArrayType arrayType = (GenericArrayType)type;
            final Type t = arrayType.getGenericComponentType();
            if (t instanceof Class) {
                return getArrayClass((Class)t);
            }
        }
        else if (type instanceof ParameterizedType) {
            final ParameterizedType subType = (ParameterizedType)type;
            final Type t = subType.getRawType();
            if (t instanceof Class) {
                return (Class)t;
            }
        }
        return null;
    }
    
    public static Class getArrayClass(final Class c) {
        try {
            final Object o = Array.newInstance(c, 0);
            return o.getClass();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static PrivilegedAction<Method> getValueOfStringMethodPA(final Class<?> c) {
        return new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    final Method m = c.getDeclaredMethod("valueOf", String.class);
                    if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType() == c) {
                        return null;
                    }
                    return m;
                }
                catch (NoSuchMethodException nsme) {
                    return null;
                }
            }
        };
    }
    
    public static PrivilegedAction<Method> getFromStringStringMethodPA(final Class<?> c) {
        return new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    final Method m = c.getDeclaredMethod("fromString", String.class);
                    if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType() == c) {
                        return null;
                    }
                    return m;
                }
                catch (NoSuchMethodException nsme) {
                    return null;
                }
            }
        };
    }
    
    public static PrivilegedAction<Constructor> getStringConstructorPA(final Class<?> c) {
        return new PrivilegedAction<Constructor>() {
            @Override
            public Constructor run() {
                try {
                    return c.getConstructor(String.class);
                }
                catch (SecurityException e) {
                    throw e;
                }
                catch (Exception e2) {
                    return null;
                }
            }
        };
    }
    
    public static Class[] getParameterizedClassArguments(final DeclaringClassInterfacePair p) {
        if (p.genericInterface instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)p.genericInterface;
            final Type[] as = pt.getActualTypeArguments();
            final Class[] cas = new Class[as.length];
            for (int i = 0; i < as.length; ++i) {
                final Type a = as[i];
                if (a instanceof Class) {
                    cas[i] = (Class)a;
                }
                else if (a instanceof ParameterizedType) {
                    pt = (ParameterizedType)a;
                    cas[i] = (Class)pt.getRawType();
                }
                else if (a instanceof TypeVariable) {
                    final ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable)a);
                    cas[i] = ((ctp != null) ? ctp.c : Object.class);
                }
            }
            return cas;
        }
        return null;
    }
    
    public static Type[] getParameterizedTypeArguments(final DeclaringClassInterfacePair p) {
        if (p.genericInterface instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)p.genericInterface;
            final Type[] as = pt.getActualTypeArguments();
            final Type[] ras = new Type[as.length];
            for (int i = 0; i < as.length; ++i) {
                final Type a = as[i];
                if (a instanceof Class) {
                    ras[i] = a;
                }
                else if (a instanceof ParameterizedType) {
                    pt = (ParameterizedType)a;
                    ras[i] = a;
                }
                else if (a instanceof TypeVariable) {
                    final ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable)a);
                    ras[i] = ctp.t;
                }
            }
            return ras;
        }
        return null;
    }
    
    public static DeclaringClassInterfacePair getClass(final Class concrete, final Class iface) {
        return getClass(concrete, iface, concrete);
    }
    
    private static DeclaringClassInterfacePair getClass(final Class concrete, final Class iface, Class c) {
        final Type[] gis = c.getGenericInterfaces();
        final DeclaringClassInterfacePair p = getType(concrete, iface, c, gis);
        if (p != null) {
            return p;
        }
        c = c.getSuperclass();
        if (c == null || c == Object.class) {
            return null;
        }
        return getClass(concrete, iface, c);
    }
    
    private static DeclaringClassInterfacePair getType(final Class concrete, final Class iface, final Class c, final Type[] ts) {
        for (final Type t : ts) {
            final DeclaringClassInterfacePair p = getType(concrete, iface, c, t);
            if (p != null) {
                return p;
            }
        }
        return null;
    }
    
    private static DeclaringClassInterfacePair getType(final Class concrete, final Class iface, final Class c, final Type t) {
        if (t instanceof Class) {
            if (t == iface) {
                return new DeclaringClassInterfacePair(concrete, c, t);
            }
            return getClass(concrete, iface, (Class)t);
        }
        else {
            if (!(t instanceof ParameterizedType)) {
                return null;
            }
            final ParameterizedType pt = (ParameterizedType)t;
            if (pt.getRawType() == iface) {
                return new DeclaringClassInterfacePair(concrete, c, t);
            }
            return getClass(concrete, iface, (Class)pt.getRawType());
        }
    }
    
    public static ClassTypePair resolveTypeVariable(final Class c, final Class dc, final TypeVariable tv) {
        return resolveTypeVariable(c, dc, tv, new HashMap<TypeVariable, Type>());
    }
    
    private static ClassTypePair resolveTypeVariable(final Class c, final Class dc, final TypeVariable tv, final Map<TypeVariable, Type> map) {
        final Type[] arr$;
        final Type[] gis = arr$ = c.getGenericInterfaces();
        for (final Type gi : arr$) {
            if (gi instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)gi;
                final ClassTypePair ctp = resolveTypeVariable(pt, (Class)pt.getRawType(), dc, tv, map);
                if (ctp != null) {
                    return ctp;
                }
            }
        }
        final Type gsc = c.getGenericSuperclass();
        if (gsc instanceof ParameterizedType) {
            final ParameterizedType pt2 = (ParameterizedType)gsc;
            return resolveTypeVariable(pt2, c.getSuperclass(), dc, tv, map);
        }
        if (gsc instanceof Class) {
            return resolveTypeVariable(c.getSuperclass(), dc, tv, map);
        }
        return null;
    }
    
    private static ClassTypePair resolveTypeVariable(ParameterizedType pt, Class c, final Class dc, final TypeVariable tv, final Map<TypeVariable, Type> map) {
        final Type[] typeArguments = pt.getActualTypeArguments();
        final TypeVariable[] typeParameters = c.getTypeParameters();
        final Map<TypeVariable, Type> submap = new HashMap<TypeVariable, Type>();
        for (int i = 0; i < typeArguments.length; ++i) {
            if (typeArguments[i] instanceof TypeVariable) {
                final Type t = map.get(typeArguments[i]);
                submap.put(typeParameters[i], t);
            }
            else {
                submap.put(typeParameters[i], typeArguments[i]);
            }
        }
        if (c != dc) {
            return resolveTypeVariable(c, dc, tv, submap);
        }
        Type t2 = submap.get(tv);
        if (t2 instanceof Class) {
            return new ClassTypePair((Class)t2);
        }
        if (t2 instanceof GenericArrayType) {
            t2 = ((GenericArrayType)t2).getGenericComponentType();
            if (t2 instanceof Class) {
                c = (Class)t2;
                try {
                    return new ClassTypePair(getArrayClass(c));
                }
                catch (Exception e) {
                    return null;
                }
            }
            if (t2 instanceof ParameterizedType) {
                final Type rt = ((ParameterizedType)t2).getRawType();
                if (!(rt instanceof Class)) {
                    return null;
                }
                c = (Class)rt;
                try {
                    return new ClassTypePair(getArrayClass(c), t2);
                }
                catch (Exception e2) {
                    return null;
                }
            }
            return null;
        }
        if (!(t2 instanceof ParameterizedType)) {
            return null;
        }
        pt = (ParameterizedType)t2;
        if (pt.getRawType() instanceof Class) {
            return new ClassTypePair((Class)pt.getRawType(), pt);
        }
        return null;
    }
    
    public static PrivilegedAction<Method> findMethodOnClassPA(final Class<?> c, final Method m) {
        return new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    return c.getMethod(m.getName(), (Class[])m.getParameterTypes());
                }
                catch (NoSuchMethodException nsme) {
                    for (final Method _m : c.getMethods()) {
                        if (_m.getName().equals(m.getName()) && _m.getParameterTypes().length == m.getParameterTypes().length && compareParameterTypes(m.getGenericParameterTypes(), _m.getGenericParameterTypes())) {
                            return _m;
                        }
                    }
                    return null;
                }
            }
        };
    }
    
    public static OsgiRegistry getOsgiRegistryInstance() {
        try {
            final Class<?> bundleReferenceClass = Class.forName("org.osgi.framework.BundleReference");
            if (bundleReferenceClass != null) {
                return OsgiRegistry.getInstance();
            }
        }
        catch (Exception ex) {}
        return null;
    }
    
    private static boolean compareParameterTypes(final Type[] ts, final Type[] _ts) {
        for (int i = 0; i < ts.length; ++i) {
            if (!ts[i].equals(_ts[i]) && !(_ts[i] instanceof TypeVariable)) {
                return false;
            }
        }
        return true;
    }
    
    static {
        LOGGER = Logger.getLogger(ReflectionHelper.class.getName());
        NoOpPrivilegedACTION = new PrivilegedAction() {
            @Override
            public Object run() {
                return null;
            }
        };
    }
    
    public static final class TypeClassPair
    {
        public final Type t;
        public final Class c;
        
        public TypeClassPair(final Type t, final Class c) {
            this.t = t;
            this.c = c;
        }
    }
    
    public static class DeclaringClassInterfacePair
    {
        public final Class concreteClass;
        public final Class declaringClass;
        public final Type genericInterface;
        
        private DeclaringClassInterfacePair(final Class concreteClass, final Class declaringClass, final Type genericInteface) {
            this.concreteClass = concreteClass;
            this.declaringClass = declaringClass;
            this.genericInterface = genericInteface;
        }
    }
    
    public static class ClassTypePair
    {
        public final Class c;
        public final Type t;
        
        public ClassTypePair(final Class c) {
            this(c, c);
        }
        
        public ClassTypePair(final Class c, final Type t) {
            this.c = c;
            this.t = t;
        }
    }
}
