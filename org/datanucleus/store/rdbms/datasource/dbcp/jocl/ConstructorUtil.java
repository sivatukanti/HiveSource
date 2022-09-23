// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.jocl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

public class ConstructorUtil
{
    public static Constructor getConstructor(final Class type, final Class[] argTypes) {
        if (null == type || null == argTypes) {
            throw new NullPointerException();
        }
        Constructor ctor = null;
        try {
            ctor = type.getConstructor((Class[])argTypes);
        }
        catch (Exception e) {
            ctor = null;
        }
        if (null == ctor) {
            final Constructor[] ctors = type.getConstructors();
            for (int i = 0; i < ctors.length; ++i) {
                final Class[] paramtypes = ctors[i].getParameterTypes();
                if (paramtypes.length == argTypes.length) {
                    boolean canuse = true;
                    for (int j = 0; j < paramtypes.length; ++j) {
                        if (!paramtypes[j].isAssignableFrom(argTypes[j])) {
                            canuse = false;
                            break;
                        }
                    }
                    if (canuse) {
                        ctor = ctors[i];
                        break;
                    }
                }
            }
        }
        return ctor;
    }
    
    public static Object invokeConstructor(final Class type, final Class[] argTypes, final Object[] argValues) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return getConstructor(type, argTypes).newInstance(argValues);
    }
}
