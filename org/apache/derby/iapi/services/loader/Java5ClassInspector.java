// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.loader;

import java.util.Iterator;
import java.util.HashMap;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import org.apache.derby.iapi.error.StandardException;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

public class Java5ClassInspector extends ClassInspector
{
    public Java5ClassInspector(final ClassFactory classFactory) {
        super(classFactory);
    }
    
    @Override
    public Class[][] getTypeBounds(final Class clazz, final Class clazz2) throws StandardException {
        if (clazz2 == null) {
            return null;
        }
        for (final Type type : clazz2.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType)type;
                if (clazz == parameterizedType.getRawType()) {
                    return this.findTypeBounds(parameterizedType);
                }
            }
        }
        return this.getTypeBounds(clazz, clazz2.getSuperclass());
    }
    
    @Override
    public boolean isVarArgsMethod(final Member member) {
        if (member instanceof Method) {
            return ((Method)member).isVarArgs();
        }
        return member instanceof Constructor && ((Constructor)member).isVarArgs();
    }
    
    @Override
    public Class[] getGenericParameterTypes(final Class clazz, final Class clazz2) throws StandardException {
        final ArrayList<Class<?>> parameterTypes = this.getParameterTypes(clazz, this.getResolvedTypes(this.getTypeChain(clazz, clazz2)));
        if (parameterTypes == null) {
            return null;
        }
        final Class[] a = new Class[parameterTypes.size()];
        parameterTypes.toArray(a);
        return a;
    }
    
    private Class[][] findTypeBounds(final ParameterizedType parameterizedType) {
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        final int length = actualTypeArguments.length;
        final Class[][] array = new Class[length][];
        for (int i = 0; i < length; ++i) {
            array[i] = this.boundType(actualTypeArguments[i]);
        }
        return array;
    }
    
    private Class[] boundType(final Type type) {
        if (type instanceof Class) {
            return new Class[] { (Class)type };
        }
        if (type instanceof TypeVariable) {
            final Type[] bounds = ((TypeVariable)type).getBounds();
            final int length = bounds.length;
            final Class[] array = new Class[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.getRawType(bounds[i]);
            }
            return array;
        }
        return null;
    }
    
    private Class getRawType(final Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return this.getRawType(((ParameterizedType)type).getRawType());
        }
        return null;
    }
    
    private ArrayList<Class<?>> getTypeChain(final Class<?> clazz, final Class<?> e) {
        ArrayList<Class<?>> list = null;
        if (e == null) {
            return null;
        }
        if (!clazz.isAssignableFrom(e)) {
            return null;
        }
        if (e == clazz) {
            list = new ArrayList<Class<?>>();
        }
        if (list == null) {
            list = this.getTypeChain(clazz, e.getSuperclass());
            if (list == null) {
                final Class[] interfaces = e.getInterfaces();
                for (int length = interfaces.length, i = 0; i < length; ++i) {
                    list = this.getTypeChain(clazz, interfaces[i]);
                    if (list != null) {
                        break;
                    }
                }
            }
        }
        if (list != null) {
            list.add(e);
        }
        return list;
    }
    
    private HashMap<Type, Type> getResolvedTypes(final ArrayList<Class<?>> list) {
        if (list == null) {
            return null;
        }
        final HashMap<Type, Type> hashMap = new HashMap<Type, Type>();
        for (final Class<?> clazz : list) {
            this.addResolvedTypes(hashMap, clazz.getGenericSuperclass());
            final Type[] genericInterfaces = clazz.getGenericInterfaces();
            for (int length = genericInterfaces.length, i = 0; i < length; ++i) {
                this.addResolvedTypes(hashMap, genericInterfaces[i]);
            }
        }
        return hashMap;
    }
    
    private void addResolvedTypes(final HashMap<Type, Type> hashMap, final Type type) {
        if (type == null) {
            return;
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            final Class clazz = (Class)parameterizedType.getRawType();
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            final TypeVariable[] typeParameters = clazz.getTypeParameters();
            for (int i = 0; i < actualTypeArguments.length; ++i) {
                hashMap.put(typeParameters[i], actualTypeArguments[i]);
            }
        }
    }
    
    private ArrayList<Class<?>> getParameterTypes(final Class<?> clazz, final HashMap<Type, Type> hashMap) {
        if (hashMap == null) {
            return null;
        }
        final TypeVariable<Class<?>>[] typeParameters = clazz.getTypeParameters();
        final ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        for (Type type : typeParameters) {
            while (hashMap.containsKey(type)) {
                type = hashMap.get(type);
            }
            list.add(this.getRawType(type));
        }
        return list;
    }
}
