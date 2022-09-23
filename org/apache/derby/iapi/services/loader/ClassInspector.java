// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.loader;

import java.lang.reflect.Array;
import org.apache.derby.iapi.error.StandardException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public class ClassInspector
{
    private static final String[] primTypeNames;
    private static final String[] nonPrimTypeNames;
    private static final String OBJECT_TYPE_NAME = "java.lang.Object";
    private static final String STRING_TYPE_NAME = "java.lang.String";
    private static final String BIGDECIMAL_TYPE_NAME = "java.math.BigDecimal";
    private final ClassFactory cf;
    
    public ClassInspector(final ClassFactory cf) {
        this.cf = cf;
    }
    
    public boolean instanceOf(final String s, final Object o) throws ClassNotFoundException {
        final Class class1 = this.getClass(s);
        return class1 != null && class1.isInstance(o);
    }
    
    public boolean assignableTo(final String s, final String s2) {
        try {
            final Class class1 = this.getClass(s2);
            if (class1 == null) {
                return false;
            }
            final Class class2 = this.getClass(s);
            if (class2 == null) {
                return !class1.isPrimitive() || class1 == Void.TYPE;
            }
            return class1.isAssignableFrom(class2);
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public boolean accessible(final String s) throws ClassNotFoundException {
        final Class class1 = this.getClass(s);
        return class1 != null && Modifier.isPublic(class1.getModifiers());
    }
    
    public String getType(final Member member) {
        Class<?> clazz;
        if (member instanceof Method) {
            clazz = ((Method)member).getReturnType();
        }
        else if (member instanceof Field) {
            clazz = ((Field)member).getType();
        }
        else if (member instanceof Constructor) {
            clazz = ((Constructor)member).getDeclaringClass();
        }
        else {
            clazz = Void.TYPE;
        }
        return readableClassName(clazz);
    }
    
    public Member findPublicMethod(final String s, final String name, final String[] array, final String[] array2, final boolean[] array3, final boolean b, final boolean b2, final boolean b3) throws ClassNotFoundException, StandardException {
        final Class class1 = this.getClass(s);
        if (class1 == null) {
            return null;
        }
        if (class1.isPrimitive()) {
            return null;
        }
        if (array == null) {
            final Method[] methods = class1.getMethods();
            for (int i = 0; i < methods.length; ++i) {
                if (!b || Modifier.isStatic(methods[i].getModifiers())) {
                    if (!b3 || this.isVarArgsMethod(methods[i])) {
                        if (name.equals(methods[i].getName())) {
                            return methods[i];
                        }
                    }
                }
            }
            return null;
        }
        final Class[] parameterTypes = new Class[array.length];
        Class[] array4 = null;
        if (array2 != null) {
            array4 = new Class[array2.length];
        }
        for (int j = 0; j < parameterTypes.length; ++j) {
            parameterTypes[j] = this.getClass(array[j]);
            if (array2 != null) {
                if (array2[j].equals(array[j])) {
                    array4[j] = null;
                }
                else {
                    array4[j] = this.getClass(array2[j]);
                }
            }
        }
        if (parameterTypes.length == 0) {
            try {
                final Method method = class1.getMethod(name, (Class[])parameterTypes);
                if (b && !Modifier.isStatic(method.getModifiers())) {
                    return null;
                }
                return method;
            }
            catch (NoSuchMethodException ex) {
                if (!class1.isInterface()) {
                    return null;
                }
            }
        }
        Method[] methods2 = class1.getMethods();
        if (class1.isInterface()) {
            final Method[] methods3 = Object.class.getMethods();
            if (methods2.length == 0) {
                methods2 = methods3;
            }
            else {
                final Member[] array5 = new Member[methods2.length + methods3.length];
                System.arraycopy(methods2, 0, array5, 0, methods2.length);
                System.arraycopy(methods3, 0, array5, methods2.length, methods3.length);
                methods2 = (Method[])array5;
            }
        }
        return this.resolveMethod(class1, name, parameterTypes, array4, array3, b, b2, methods2, b3);
    }
    
    public Member findPublicField(final String s, final String s2, final boolean b) throws StandardException {
        Throwable t = null;
        try {
            final Class class1 = this.getClass(s);
            if (class1 == null) {
                return null;
            }
            if (class1.isArray() || class1.isPrimitive()) {
                return null;
            }
            final int n = b ? 9 : 1;
            final Field field = class1.getField(s2);
            if ((field.getModifiers() & n) == n) {
                if (class1.isInterface() || field.getDeclaringClass().equals(class1)) {
                    return field;
                }
                try {
                    class1.getDeclaredField(s2);
                }
                catch (NoSuchFieldException ex4) {
                    return field;
                }
            }
        }
        catch (ClassNotFoundException ex) {
            t = ex;
        }
        catch (NoSuchFieldException ex2) {
            t = ex2;
        }
        catch (SecurityException ex3) {
            t = ex3;
        }
        throw StandardException.newException(b ? "42X72" : "42X68", t, s2, s);
    }
    
    public Member findPublicConstructor(final String s, final String[] array, final String[] array2, final boolean[] array3) throws ClassNotFoundException, StandardException {
        final Class class1 = this.getClass(s);
        if (class1 == null) {
            return null;
        }
        if (class1.isArray() || class1.isPrimitive() || class1.isInterface()) {
            return null;
        }
        final Class[] parameterTypes = new Class[array.length];
        Class[] array4 = null;
        if (array2 != null) {
            array4 = new Class[array2.length];
        }
        boolean b = false;
        for (int i = 0; i < parameterTypes.length; ++i) {
            parameterTypes[i] = this.getClass(array[i]);
            if (parameterTypes[i] == null) {
                b = true;
            }
            if (array2 != null) {
                if (array2[i].equals(array[i])) {
                    array4[i] = null;
                }
                else {
                    array4[i] = this.getClass(array2[i]);
                }
            }
        }
        try {
            if (!b && array2 == null) {
                return class1.getConstructor((Class[])parameterTypes);
            }
        }
        catch (NoSuchMethodException ex) {
            if (parameterTypes.length == 0) {
                return null;
            }
        }
        return this.resolveMethod(class1, "<init>", parameterTypes, array4, array3, false, false, class1.getConstructors(), false);
    }
    
    public Class[][] getTypeBounds(final Class clazz, final Class clazz2) throws StandardException {
        throw StandardException.newException("XBCM5.S", "Java 5");
    }
    
    public boolean isVarArgsMethod(final Member member) {
        return false;
    }
    
    public Class[] getGenericParameterTypes(final Class clazz, final Class clazz2) throws StandardException {
        throw StandardException.newException("XBCM5.S", "Java 5");
    }
    
    public String[] getParameterTypes(final Member member) {
        Class<?>[] array;
        if (member instanceof Method) {
            array = ((Method)member).getParameterTypes();
        }
        else {
            array = (Class<?>[])((Constructor)member).getParameterTypes();
        }
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = readableClassName(array[i]);
        }
        return array2;
    }
    
    public static boolean primitiveType(final String s) {
        for (int i = 0; i < ClassInspector.primTypeNames.length; ++i) {
            if (s.equals(ClassInspector.primTypeNames[i])) {
                return true;
            }
        }
        return false;
    }
    
    private Member resolveMethod(final Class clazz, final String s, final Class[] array, final Class[] array2, final boolean[] array3, final boolean b, final boolean b2, final Member[] array4, final boolean b3) throws StandardException {
        int n = -1;
        int n2 = 1;
        boolean b4;
        boolean b5;
        do {
            b4 = false;
            b5 = false;
        Label_0291:
            for (int i = 0; i < array4.length; ++i) {
                final Member member = array4[i];
                if (member != null) {
                    if (i != n) {
                        final Class<?>[] array5 = (member instanceof Method) ? ((Method)member).getParameterTypes() : ((Constructor)member).getParameterTypes();
                        if (n2 != 0) {
                            if (b2) {
                                if (array5.length < array.length) {
                                    array4[i] = null;
                                    continue;
                                }
                            }
                            else if (array5.length != array.length) {
                                array4[i] = null;
                                continue;
                            }
                            if (b && !Modifier.isStatic(member.getModifiers())) {
                                array4[i] = null;
                                continue;
                            }
                            if (b3 && !this.isVarArgsMethod(member)) {
                                array4[i] = null;
                                continue;
                            }
                            if (!s.startsWith("<") && !s.equals(member.getName())) {
                                array4[i] = null;
                                continue;
                            }
                            if (b2) {
                                for (int j = array.length - 1; j < array5.length; ++j) {
                                    if (!array5[j].equals(array[array.length - 1])) {
                                        array4[i] = null;
                                        continue Label_0291;
                                    }
                                }
                            }
                        }
                        if (!this.signatureConvertableFromTo(array, array2, array5, array3, true)) {
                            array4[i] = null;
                        }
                        else if (n == -1) {
                            n = i;
                        }
                        else {
                            b4 = true;
                        }
                    }
                }
            }
            n2 = 0;
        } while (b4 && b5);
        if (b4) {
            final StringBuffer sb = new StringBuffer();
            for (int k = 0; k < array.length; ++k) {
                if (k != 0) {
                    sb.append(", ");
                }
                sb.append((array[k] == null) ? "null" : array[k].getName());
                if (array2 != null && array2[k] != null) {
                    sb.append("(").append(array2[k].getName()).append(")");
                }
            }
            throw StandardException.newException("42X73", clazz.getName(), s, sb.toString());
        }
        if (n == -1) {
            return null;
        }
        return array4[n];
    }
    
    public Class getClass(String substring) throws ClassNotFoundException {
        if (substring == null || substring.length() == 0) {
            return null;
        }
        int n = 0;
        int length = substring.length();
        for (int beginIndex = length - 2; beginIndex >= 0 && substring.substring(beginIndex, beginIndex + 2).equals("[]"); beginIndex -= 2, length -= 2) {
            ++n;
        }
        if (length <= 0) {
            return Class.forName(substring);
        }
        if (n != 0) {
            substring = substring.substring(0, length);
        }
        Class<?> clazz = null;
        if (length >= 3 && length <= 7) {
            if ("int".equals(substring)) {
                clazz = Integer.TYPE;
            }
            else if ("short".equals(substring)) {
                clazz = Short.TYPE;
            }
            else if ("boolean".equals(substring)) {
                clazz = Boolean.TYPE;
            }
            else if ("byte".equals(substring)) {
                clazz = Byte.TYPE;
            }
            else if ("float".equals(substring)) {
                clazz = Float.TYPE;
            }
            else if ("double".equals(substring)) {
                clazz = Double.TYPE;
            }
            else if ("long".equals(substring)) {
                clazz = Long.TYPE;
            }
            else if ("char".equals(substring)) {
                clazz = Character.TYPE;
            }
            else if ("void".equals(substring)) {
                clazz = Void.TYPE;
            }
        }
        if (clazz == null) {
            clazz = (Class<?>)this.cf.loadApplicationClass(substring);
        }
        if (n == 0) {
            return clazz;
        }
        if (n == 1) {
            return Array.newInstance(clazz, 0).getClass();
        }
        return Array.newInstance(clazz, new int[n]).getClass();
    }
    
    private boolean isMethodMoreSpecificOrEqual(final Member member, final Member member2, final boolean[] array) {
        Class<?>[] array2;
        Class<?>[] array3;
        if (member instanceof Method) {
            if (!this.classConvertableFromTo(member.getDeclaringClass(), member2.getDeclaringClass(), true)) {
                return false;
            }
            array2 = ((Method)member).getParameterTypes();
            array3 = ((Method)member2).getParameterTypes();
        }
        else {
            array2 = (Class<?>[])((Constructor)member).getParameterTypes();
            array3 = (Class<?>[])((Constructor)member2).getParameterTypes();
        }
        return this.signatureConvertableFromTo(array2, null, array3, array, true);
    }
    
    private boolean signatureConvertableFromTo(final Class[] array, final Class[] array2, final Class[] array3, final boolean[] array4, final boolean b) {
        int n = array.length;
        if (array3.length < n) {
            n = array3.length;
        }
        for (int i = 0; i < n; ++i) {
            final Class clazz = array[i];
            final Class clazz2 = array3[i];
            if (clazz == null) {
                if (clazz2.isPrimitive() && (array2 == null || (array4 != null && !array4[i]))) {
                    return false;
                }
            }
            else if (!this.classConvertableFromTo(clazz, clazz2, b) && (array2 == null || array2[i] == null || !this.classConvertableFromTo(array2[i], clazz2, b))) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean classConvertableFromTo(final Class clazz, final Class clazz2, final boolean b) {
        if (clazz.getName().equals(clazz2.getName())) {
            return true;
        }
        if (clazz.isArray() && clazz2.isArray()) {
            return this.classConvertableFromTo(clazz.getComponentType(), clazz2.getComponentType(), b);
        }
        if ((!clazz2.isPrimitive() || !clazz.isPrimitive()) && !b) {
            return false;
        }
        final String name = clazz.getName();
        final String name2 = clazz2.getName();
        if (clazz == Boolean.TYPE || name.equals(ClassInspector.nonPrimTypeNames[0])) {
            if (clazz2 == Boolean.TYPE || name2.equals(ClassInspector.nonPrimTypeNames[0])) {
                return true;
            }
        }
        else if (clazz == Byte.TYPE || name.equals(ClassInspector.nonPrimTypeNames[1])) {
            if (clazz2 == Byte.TYPE || name2.equals(ClassInspector.nonPrimTypeNames[1]) || clazz2 == Short.TYPE || clazz2 == Integer.TYPE || clazz2 == Long.TYPE || clazz2 == Float.TYPE || clazz2 == Double.TYPE) {
                return true;
            }
        }
        else if (clazz == Character.TYPE || name.equals(ClassInspector.nonPrimTypeNames[2])) {
            if (clazz2 == Character.TYPE || name2.equals(ClassInspector.nonPrimTypeNames[2]) || clazz2 == Integer.TYPE || clazz2 == Long.TYPE || clazz2 == Float.TYPE || clazz2 == Double.TYPE) {
                return true;
            }
        }
        else if (clazz == Short.TYPE || name.equals(ClassInspector.nonPrimTypeNames[3])) {
            if (clazz2 == Short.TYPE || name2.equals(ClassInspector.nonPrimTypeNames[4])) {
                return true;
            }
        }
        else if (clazz == Integer.TYPE || name.equals(ClassInspector.nonPrimTypeNames[4])) {
            if (clazz2 == Integer.TYPE || name2.equals(ClassInspector.nonPrimTypeNames[4])) {
                return true;
            }
        }
        else if (clazz == Long.TYPE || name.equals(ClassInspector.nonPrimTypeNames[5])) {
            if (clazz2 == Long.TYPE || name2.equals(ClassInspector.nonPrimTypeNames[5])) {
                return true;
            }
        }
        else if (clazz == Float.TYPE || name.equals(ClassInspector.nonPrimTypeNames[6])) {
            if (clazz2 == Float.TYPE || name2.equals(ClassInspector.nonPrimTypeNames[6])) {
                return true;
            }
        }
        else if ((clazz == Double.TYPE || name.equals(ClassInspector.nonPrimTypeNames[7])) && (clazz2 == Double.TYPE || name2.equals(ClassInspector.nonPrimTypeNames[7]))) {
            return true;
        }
        return false;
    }
    
    public static String readableClassName(Class componentType) {
        if (!componentType.isArray()) {
            return componentType.getName();
        }
        int n = 0;
        do {
            ++n;
            componentType = componentType.getComponentType();
        } while (componentType.isArray());
        final StringBuffer sb = new StringBuffer(componentType.getName());
        for (int i = 0; i < n; ++i) {
            sb.append("[]");
        }
        return sb.toString();
    }
    
    public String getDeclaringClass(final Member member) {
        return member.getDeclaringClass().getName();
    }
    
    static {
        primTypeNames = new String[] { "boolean", "byte", "char", "short", "int", "long", "float", "double" };
        nonPrimTypeNames = new String[] { "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double" };
    }
}
