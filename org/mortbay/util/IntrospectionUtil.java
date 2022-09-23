// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.lang.reflect.Member;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class IntrospectionUtil
{
    public static boolean isJavaBeanCompliantSetter(final Method method) {
        return method != null && method.getReturnType() == Void.TYPE && method.getName().startsWith("set") && method.getParameterTypes().length == 1;
    }
    
    public static Method findMethod(final Class clazz, final String methodName, final Class[] args, final boolean checkInheritance, final boolean strictArgs) throws NoSuchMethodException {
        if (clazz == null) {
            throw new NoSuchMethodException("No class");
        }
        if (methodName == null || methodName.trim().equals("")) {
            throw new NoSuchMethodException("No method name");
        }
        Method method = null;
        final Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length && method == null; ++i) {
            if (methods[i].getName().equals(methodName) && checkParams(methods[i].getParameterTypes(), (args == null) ? new Class[0] : args, strictArgs)) {
                method = methods[i];
            }
        }
        if (method != null) {
            return method;
        }
        if (checkInheritance) {
            return findInheritedMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, args, strictArgs);
        }
        throw new NoSuchMethodException("No such method " + methodName + " on class " + clazz.getName());
    }
    
    public static Field findField(final Class clazz, final String targetName, final Class targetType, final boolean checkInheritance, final boolean strictType) throws NoSuchFieldException {
        if (clazz == null) {
            throw new NoSuchFieldException("No class");
        }
        if (targetName == null) {
            throw new NoSuchFieldException("No field name");
        }
        try {
            final Field field = clazz.getDeclaredField(targetName);
            if (strictType) {
                if (field.getType().equals(targetType)) {
                    return field;
                }
            }
            else if (field.getType().isAssignableFrom(targetType)) {
                return field;
            }
            if (checkInheritance) {
                return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), targetName, targetType, strictType);
            }
            throw new NoSuchFieldException("No field with name " + targetName + " in class " + clazz.getName() + " of type " + targetType);
        }
        catch (NoSuchFieldException e) {
            return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), targetName, targetType, strictType);
        }
    }
    
    public static boolean isInheritable(final Package pack, final Member member) {
        if (pack == null) {
            return false;
        }
        if (member == null) {
            return false;
        }
        final int modifiers = member.getModifiers();
        return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers) || (!Modifier.isPrivate(modifiers) && pack.equals(member.getDeclaringClass().getPackage()));
    }
    
    public static boolean checkParams(final Class[] formalParams, final Class[] actualParams, final boolean strict) {
        if (formalParams == null && actualParams == null) {
            return true;
        }
        if (formalParams == null && actualParams != null) {
            return false;
        }
        if (formalParams != null && actualParams == null) {
            return false;
        }
        if (formalParams.length != actualParams.length) {
            return false;
        }
        if (formalParams.length == 0) {
            return true;
        }
        int j = 0;
        if (strict) {
            while (j < formalParams.length && formalParams[j].equals(actualParams[j])) {
                ++j;
            }
        }
        else {
            while (j < formalParams.length && formalParams[j].isAssignableFrom(actualParams[j])) {
                ++j;
            }
        }
        return j == formalParams.length;
    }
    
    public static boolean isSameSignature(final Method methodA, final Method methodB) {
        if (methodA == null) {
            return false;
        }
        if (methodB == null) {
            return false;
        }
        final List parameterTypesA = Arrays.asList(methodA.getParameterTypes());
        final List parameterTypesB = Arrays.asList(methodB.getParameterTypes());
        return methodA.getName().equals(methodB.getName()) && parameterTypesA.containsAll(parameterTypesB);
    }
    
    public static boolean isTypeCompatible(final Class formalType, final Class actualType, final boolean strict) {
        if (formalType == null && actualType != null) {
            return false;
        }
        if (formalType != null && actualType == null) {
            return false;
        }
        if (formalType == null && actualType == null) {
            return true;
        }
        if (strict) {
            return formalType.equals(actualType);
        }
        return formalType.isAssignableFrom(actualType);
    }
    
    public static boolean containsSameMethodSignature(final Method method, final Class c, final boolean checkPackage) {
        if (checkPackage && !c.getPackage().equals(method.getDeclaringClass().getPackage())) {
            return false;
        }
        boolean samesig = false;
        final Method[] methods = c.getDeclaredMethods();
        for (int i = 0; i < methods.length && !samesig; ++i) {
            if (isSameSignature(method, methods[i])) {
                samesig = true;
            }
        }
        return samesig;
    }
    
    public static boolean containsSameFieldName(final Field field, final Class c, final boolean checkPackage) {
        if (checkPackage && !c.getPackage().equals(field.getDeclaringClass().getPackage())) {
            return false;
        }
        boolean sameName = false;
        final Field[] fields = c.getDeclaredFields();
        for (int i = 0; i < fields.length && !sameName; ++i) {
            if (fields[i].getName().equals(field.getName())) {
                sameName = true;
            }
        }
        return sameName;
    }
    
    protected static Method findInheritedMethod(final Package pack, final Class clazz, final String methodName, final Class[] args, final boolean strictArgs) throws NoSuchMethodException {
        if (clazz == null) {
            throw new NoSuchMethodException("No class");
        }
        if (methodName == null) {
            throw new NoSuchMethodException("No method name");
        }
        Method method = null;
        final Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length && method == null; ++i) {
            if (methods[i].getName().equals(methodName) && isInheritable(pack, methods[i]) && checkParams(methods[i].getParameterTypes(), args, strictArgs)) {
                method = methods[i];
            }
        }
        if (method != null) {
            return method;
        }
        return findInheritedMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, args, strictArgs);
    }
    
    protected static Field findInheritedField(final Package pack, final Class clazz, final String fieldName, final Class fieldType, final boolean strictType) throws NoSuchFieldException {
        if (clazz == null) {
            throw new NoSuchFieldException("No class");
        }
        if (fieldName == null) {
            throw new NoSuchFieldException("No field name");
        }
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            if (isInheritable(pack, field) && isTypeCompatible(fieldType, field.getType(), strictType)) {
                return field;
            }
            return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), fieldName, fieldType, strictType);
        }
        catch (NoSuchFieldException e) {
            return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), fieldName, fieldType, strictType);
        }
    }
}
