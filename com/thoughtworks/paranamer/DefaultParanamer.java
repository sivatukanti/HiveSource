// 
// Decompiled by Procyon v0.5.36
// 

package com.thoughtworks.paranamer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;

public class DefaultParanamer implements Paranamer
{
    private static final String COMMA = ",";
    private static final String SPACE = " ";
    public static final String __PARANAMER_DATA = "v1.0 \nlookupParameterNames java.lang.AccessibleObject methodOrConstructor \nlookupParameterNames java.lang.AccessibleObject,boolean methodOrCtor,throwExceptionIfMissing \ngetParameterTypeName java.lang.Class cls\n";
    
    public String[] lookupParameterNames(final AccessibleObject methodOrConstructor) {
        return this.lookupParameterNames(methodOrConstructor, true);
    }
    
    public String[] lookupParameterNames(final AccessibleObject methodOrCtor, final boolean throwExceptionIfMissing) {
        Class<?>[] types = null;
        Class<?> declaringClass = null;
        String name = null;
        if (methodOrCtor instanceof Method) {
            final Method method = (Method)methodOrCtor;
            types = method.getParameterTypes();
            name = method.getName();
            declaringClass = method.getDeclaringClass();
        }
        else {
            final Constructor<?> constructor = (Constructor<?>)methodOrCtor;
            types = constructor.getParameterTypes();
            declaringClass = constructor.getDeclaringClass();
            name = "<init>";
        }
        if (types.length == 0) {
            return DefaultParanamer.EMPTY_NAMES;
        }
        final String parameterTypeNames = getParameterTypeNamesCSV(types);
        final String[] names = getParameterNames(declaringClass, parameterTypeNames, name + " ");
        if (names != null) {
            return names;
        }
        if (throwExceptionIfMissing) {
            throw new ParameterNamesNotFoundException("No parameter names found for class '" + declaringClass + "', methodOrCtor " + name + " and parameter types " + parameterTypeNames);
        }
        return Paranamer.EMPTY_NAMES;
    }
    
    private static String[] getParameterNames(final Class<?> declaringClass, final String parameterTypes, final String prefix) {
        final String data = getParameterListResource(declaringClass);
        final String line = findFirstMatchingLine(data, prefix + parameterTypes + " ");
        final String[] parts = line.split(" ");
        if (parts.length == 3 && parts[1].equals(parameterTypes)) {
            final String parameterNames = parts[2];
            return parameterNames.split(",");
        }
        return Paranamer.EMPTY_NAMES;
    }
    
    static String getParameterTypeNamesCSV(final Class<?>[] parameterTypes) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < parameterTypes.length; ++i) {
            sb.append(getParameterTypeName(parameterTypes[i]));
            if (i < parameterTypes.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    private static String getParameterListResource(final Class<?> declaringClass) {
        try {
            final Field field = declaringClass.getDeclaredField("__PARANAMER_DATA");
            if (!Modifier.isStatic(field.getModifiers()) || !field.getType().equals(String.class)) {
                return null;
            }
            return (String)field.get(null);
        }
        catch (NoSuchFieldException e) {
            return null;
        }
        catch (IllegalAccessException e2) {
            return null;
        }
    }
    
    private static String findFirstMatchingLine(final String data, final String prefix) {
        if (data == null) {
            return "";
        }
        final int ix = data.indexOf(prefix);
        if (ix >= 0) {
            final int iy = data.indexOf("\n", ix);
            if (iy > 0) {
                return data.substring(ix, iy);
            }
        }
        return "";
    }
    
    private static String getParameterTypeName(final Class<?> cls) {
        String parameterTypeNameName = cls.getName();
        int arrayNestingDepth = 0;
        for (int ix = parameterTypeNameName.indexOf("["); ix > -1; ix = parameterTypeNameName.indexOf("[")) {
            ++arrayNestingDepth;
            parameterTypeNameName = parameterTypeNameName.replaceFirst("(\\[\\w)|(\\[)", "");
        }
        parameterTypeNameName = parameterTypeNameName.replaceFirst(";", "");
        for (int k = 0; k < arrayNestingDepth; ++k) {
            parameterTypeNameName += "[]";
        }
        return parameterTypeNameName;
    }
}
