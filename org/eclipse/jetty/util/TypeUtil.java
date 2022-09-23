// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import java.lang.annotation.Annotation;
import org.eclipse.jetty.util.annotation.Name;
import java.util.Map;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.eclipse.jetty.util.log.Logger;

public class TypeUtil
{
    private static final Logger LOG;
    public static final Class<?>[] NO_ARGS;
    public static final int CR = 13;
    public static final int LF = 10;
    private static final HashMap<String, Class<?>> name2Class;
    private static final HashMap<Class<?>, String> class2Name;
    private static final HashMap<Class<?>, Method> class2Value;
    
    public static <T> List<T> asList(final T[] a) {
        if (a == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(a);
    }
    
    public static Class<?> fromName(final String name) {
        return TypeUtil.name2Class.get(name);
    }
    
    public static String toName(final Class<?> type) {
        return TypeUtil.class2Name.get(type);
    }
    
    public static Object valueOf(final Class<?> type, final String value) {
        try {
            if (type.equals(String.class)) {
                return value;
            }
            try {
                final Method m = TypeUtil.class2Value.get(type);
                if (m != null) {
                    return m.invoke(null, value);
                }
                try {
                    if (type.equals(Character.TYPE) || type.equals(Character.class)) {
                        return value.charAt(0);
                    }
                    try {
                        final Constructor<?> c = type.getConstructor(String.class);
                        return c.newInstance(value);
                    }
                    catch (IllegalAccessException x) {
                        TypeUtil.LOG.ignore(x);
                    }
                }
                catch (IllegalAccessException ex) {}
            }
            catch (IllegalAccessException ex2) {}
        }
        catch (NoSuchMethodException ex3) {}
        catch (IllegalAccessException ex4) {}
        catch (InstantiationException ex5) {}
        catch (InvocationTargetException x2) {
            if (x2.getTargetException() instanceof Error) {
                throw (Error)x2.getTargetException();
            }
            TypeUtil.LOG.ignore(x2);
        }
        return null;
    }
    
    public static Object valueOf(final String type, final String value) {
        return valueOf(fromName(type), value);
    }
    
    public static int parseInt(final String s, final int offset, int length, final int base) throws NumberFormatException {
        int value = 0;
        if (length < 0) {
            length = s.length() - offset;
        }
        for (int i = 0; i < length; ++i) {
            final char c = s.charAt(offset + i);
            final int digit = convertHexDigit((int)c);
            if (digit < 0 || digit >= base) {
                throw new NumberFormatException(s.substring(offset, offset + length));
            }
            value = value * base + digit;
        }
        return value;
    }
    
    public static int parseInt(final byte[] b, final int offset, int length, final int base) throws NumberFormatException {
        int value = 0;
        if (length < 0) {
            length = b.length - offset;
        }
        for (int i = 0; i < length; ++i) {
            final char c = (char)(0xFF & b[offset + i]);
            int digit = c - '0';
            if (digit < 0 || digit >= base || digit >= 10) {
                digit = '\n' + c - 65;
                if (digit < 10 || digit >= base) {
                    digit = '\n' + c - 97;
                }
            }
            if (digit < 0 || digit >= base) {
                throw new NumberFormatException(new String(b, offset, length));
            }
            value = value * base + digit;
        }
        return value;
    }
    
    public static byte[] parseBytes(final String s, final int base) {
        final byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            bytes[i / 2] = (byte)parseInt(s, i, 2, base);
        }
        return bytes;
    }
    
    public static String toString(final byte[] bytes, final int base) {
        final StringBuilder buf = new StringBuilder();
        for (final byte b : bytes) {
            final int bi = 0xFF & b;
            int c = 48 + bi / base % base;
            if (c > 57) {
                c = 97 + (c - 48 - 10);
            }
            buf.append((char)c);
            c = 48 + bi % base;
            if (c > 57) {
                c = 97 + (c - 48 - 10);
            }
            buf.append((char)c);
        }
        return buf.toString();
    }
    
    public static byte convertHexDigit(final byte c) {
        final byte b = (byte)((c & 0x1F) + (c >> 6) * 25 - 16);
        if (b < 0 || b > 15) {
            throw new NumberFormatException("!hex " + c);
        }
        return b;
    }
    
    public static int convertHexDigit(final char c) {
        final int d = (c & '\u001f') + (c >> 6) * 25 - 16;
        if (d < 0 || d > 15) {
            throw new NumberFormatException("!hex " + c);
        }
        return d;
    }
    
    public static int convertHexDigit(final int c) {
        final int d = (c & 0x1F) + (c >> 6) * 25 - 16;
        if (d < 0 || d > 15) {
            throw new NumberFormatException("!hex " + c);
        }
        return d;
    }
    
    public static void toHex(final byte b, final Appendable buf) {
        try {
            int d = 0xF & (0xF0 & b) >> 4;
            buf.append((char)(((d > 9) ? 55 : 48) + d));
            d = (0xF & b);
            buf.append((char)(((d > 9) ? 55 : 48) + d));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void toHex(final int value, final Appendable buf) throws IOException {
        int d = 0xF & (0xF0000000 & value) >> 28;
        buf.append((char)(((d > 9) ? 55 : 48) + d));
        d = (0xF & (0xF000000 & value) >> 24);
        buf.append((char)(((d > 9) ? 55 : 48) + d));
        d = (0xF & (0xF00000 & value) >> 20);
        buf.append((char)(((d > 9) ? 55 : 48) + d));
        d = (0xF & (0xF0000 & value) >> 16);
        buf.append((char)(((d > 9) ? 55 : 48) + d));
        d = (0xF & (0xF000 & value) >> 12);
        buf.append((char)(((d > 9) ? 55 : 48) + d));
        d = (0xF & (0xF00 & value) >> 8);
        buf.append((char)(((d > 9) ? 55 : 48) + d));
        d = (0xF & (0xF0 & value) >> 4);
        buf.append((char)(((d > 9) ? 55 : 48) + d));
        d = (0xF & value);
        buf.append((char)(((d > 9) ? 55 : 48) + d));
        Integer.toString(0, 36);
    }
    
    public static void toHex(final long value, final Appendable buf) throws IOException {
        toHex((int)(value >> 32), buf);
        toHex((int)value, buf);
    }
    
    public static String toHexString(final byte b) {
        return toHexString(new byte[] { b }, 0, 1);
    }
    
    public static String toHexString(final byte[] b) {
        return toHexString(b, 0, b.length);
    }
    
    public static String toHexString(final byte[] b, final int offset, final int length) {
        final StringBuilder buf = new StringBuilder();
        for (int i = offset; i < offset + length; ++i) {
            final int bi = 0xFF & b[i];
            int c = 48 + bi / 16 % 16;
            if (c > 57) {
                c = 65 + (c - 48 - 10);
            }
            buf.append((char)c);
            c = 48 + bi % 16;
            if (c > 57) {
                c = 97 + (c - 48 - 10);
            }
            buf.append((char)c);
        }
        return buf.toString();
    }
    
    public static byte[] fromHexString(final String s) {
        if (s.length() % 2 != 0) {
            throw new IllegalArgumentException(s);
        }
        final byte[] array = new byte[s.length() / 2];
        for (int i = 0; i < array.length; ++i) {
            final int b = Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            array[i] = (byte)(0xFF & b);
        }
        return array;
    }
    
    public static void dump(final Class<?> c) {
        System.err.println("Dump: " + c);
        dump(c.getClassLoader());
    }
    
    public static void dump(ClassLoader cl) {
        System.err.println("Dump Loaders:");
        while (cl != null) {
            System.err.println("  loader " + cl);
            cl = cl.getParent();
        }
    }
    
    public static Object call(final Class<?> oClass, final String methodName, final Object obj, final Object[] arg) throws InvocationTargetException, NoSuchMethodException {
        Objects.requireNonNull(oClass, "Class cannot be null");
        Objects.requireNonNull(methodName, "Method name cannot be null");
        if (StringUtil.isBlank(methodName)) {
            throw new IllegalArgumentException("Method name cannot be blank");
        }
        for (final Method method : oClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                if (method.getParameterCount() == arg.length) {
                    if (Modifier.isStatic(method.getModifiers()) == (obj == null)) {
                        if (obj != null || method.getDeclaringClass() == oClass) {
                            try {
                                return method.invoke(obj, arg);
                            }
                            catch (IllegalAccessException | IllegalArgumentException ex3) {
                                final Exception ex;
                                final Exception e = ex;
                                TypeUtil.LOG.ignore(e);
                            }
                        }
                    }
                }
            }
        }
        Object[] args_with_opts = null;
        for (final Method method2 : oClass.getMethods()) {
            if (method2.getName().equals(methodName)) {
                if (method2.getParameterCount() == arg.length + 1) {
                    if (method2.getParameterTypes()[arg.length].isArray()) {
                        if (Modifier.isStatic(method2.getModifiers()) == (obj == null)) {
                            if (obj != null || method2.getDeclaringClass() == oClass) {
                                if (args_with_opts == null) {
                                    args_with_opts = ArrayUtil.addToArray(arg, new Object[0], Object.class);
                                }
                                try {
                                    return method2.invoke(obj, args_with_opts);
                                }
                                catch (IllegalAccessException | IllegalArgumentException ex4) {
                                    final Exception ex2;
                                    final Exception e2 = ex2;
                                    TypeUtil.LOG.ignore(e2);
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new NoSuchMethodException(methodName);
    }
    
    public static Object construct(final Class<?> klass, final Object[] arguments) throws InvocationTargetException, NoSuchMethodException {
        Objects.requireNonNull(klass, "Class cannot be null");
        for (final Constructor<?> constructor : klass.getConstructors()) {
            Label_0077: {
                if (arguments == null) {
                    if (constructor.getParameterCount() != 0) {
                        break Label_0077;
                    }
                }
                else if (constructor.getParameterCount() != arguments.length) {
                    break Label_0077;
                }
                try {
                    return constructor.newInstance(arguments);
                }
                catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex2) {
                    final Exception ex;
                    final Exception e = ex;
                    TypeUtil.LOG.ignore(e);
                }
            }
        }
        throw new NoSuchMethodException("<init>");
    }
    
    public static Object construct(final Class<?> klass, final Object[] arguments, final Map<String, Object> namedArgMap) throws InvocationTargetException, NoSuchMethodException {
        Objects.requireNonNull(klass, "Class cannot be null");
        Objects.requireNonNull(namedArgMap, "Named Argument Map cannot be null");
        for (final Constructor<?> constructor : klass.getConstructors()) {
            Label_0432: {
                if (arguments == null) {
                    if (constructor.getParameterCount() != 0) {
                        break Label_0432;
                    }
                }
                else if (constructor.getParameterCount() != arguments.length) {
                    break Label_0432;
                }
                try {
                    final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
                    if (arguments == null || arguments.length == 0) {
                        if (TypeUtil.LOG.isDebugEnabled()) {
                            TypeUtil.LOG.debug("Constructor has no arguments", new Object[0]);
                        }
                        return constructor.newInstance(arguments);
                    }
                    try {
                        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
                            if (TypeUtil.LOG.isDebugEnabled()) {
                                TypeUtil.LOG.debug("Constructor has no parameter annotations", new Object[0]);
                            }
                            return constructor.newInstance(arguments);
                        }
                        try {
                            final Object[] swizzled = new Object[arguments.length];
                            int count = 0;
                            for (final Annotation[] array2 : parameterAnnotations) {
                                final Annotation[] annotations = array2;
                                for (final Annotation annotation : array2) {
                                    if (annotation instanceof Name) {
                                        final Name param = (Name)annotation;
                                        if (namedArgMap.containsKey(param.value())) {
                                            if (TypeUtil.LOG.isDebugEnabled()) {
                                                TypeUtil.LOG.debug("placing named {} in position {}", param.value(), count);
                                            }
                                            swizzled[count] = namedArgMap.get(param.value());
                                        }
                                        else {
                                            if (TypeUtil.LOG.isDebugEnabled()) {
                                                TypeUtil.LOG.debug("placing {} in position {}", arguments[count], count);
                                            }
                                            swizzled[count] = arguments[count];
                                        }
                                        ++count;
                                    }
                                    else if (TypeUtil.LOG.isDebugEnabled()) {
                                        TypeUtil.LOG.debug("passing on annotation {}", annotation);
                                    }
                                }
                            }
                            return constructor.newInstance(swizzled);
                        }
                        catch (IllegalAccessException e) {
                            TypeUtil.LOG.ignore(e);
                        }
                    }
                    catch (IllegalAccessException ex) {}
                }
                catch (InstantiationException ex2) {}
                catch (IllegalAccessException ex3) {}
                catch (IllegalArgumentException ex4) {}
            }
        }
        throw new NoSuchMethodException("<init>");
    }
    
    public static boolean isTrue(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return (boolean)o;
        }
        return Boolean.parseBoolean(o.toString());
    }
    
    public static boolean isFalse(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return !(boolean)o;
        }
        return "false".equalsIgnoreCase(o.toString());
    }
    
    static {
        LOG = Log.getLogger(TypeUtil.class);
        NO_ARGS = new Class[0];
        (name2Class = new HashMap<String, Class<?>>()).put("boolean", Boolean.TYPE);
        TypeUtil.name2Class.put("byte", Byte.TYPE);
        TypeUtil.name2Class.put("char", Character.TYPE);
        TypeUtil.name2Class.put("double", Double.TYPE);
        TypeUtil.name2Class.put("float", Float.TYPE);
        TypeUtil.name2Class.put("int", Integer.TYPE);
        TypeUtil.name2Class.put("long", Long.TYPE);
        TypeUtil.name2Class.put("short", Short.TYPE);
        TypeUtil.name2Class.put("void", Void.TYPE);
        TypeUtil.name2Class.put("java.lang.Boolean.TYPE", Boolean.TYPE);
        TypeUtil.name2Class.put("java.lang.Byte.TYPE", Byte.TYPE);
        TypeUtil.name2Class.put("java.lang.Character.TYPE", Character.TYPE);
        TypeUtil.name2Class.put("java.lang.Double.TYPE", Double.TYPE);
        TypeUtil.name2Class.put("java.lang.Float.TYPE", Float.TYPE);
        TypeUtil.name2Class.put("java.lang.Integer.TYPE", Integer.TYPE);
        TypeUtil.name2Class.put("java.lang.Long.TYPE", Long.TYPE);
        TypeUtil.name2Class.put("java.lang.Short.TYPE", Short.TYPE);
        TypeUtil.name2Class.put("java.lang.Void.TYPE", Void.TYPE);
        TypeUtil.name2Class.put("java.lang.Boolean", Boolean.class);
        TypeUtil.name2Class.put("java.lang.Byte", Byte.class);
        TypeUtil.name2Class.put("java.lang.Character", Character.class);
        TypeUtil.name2Class.put("java.lang.Double", Double.class);
        TypeUtil.name2Class.put("java.lang.Float", Float.class);
        TypeUtil.name2Class.put("java.lang.Integer", Integer.class);
        TypeUtil.name2Class.put("java.lang.Long", Long.class);
        TypeUtil.name2Class.put("java.lang.Short", Short.class);
        TypeUtil.name2Class.put("Boolean", Boolean.class);
        TypeUtil.name2Class.put("Byte", Byte.class);
        TypeUtil.name2Class.put("Character", Character.class);
        TypeUtil.name2Class.put("Double", Double.class);
        TypeUtil.name2Class.put("Float", Float.class);
        TypeUtil.name2Class.put("Integer", Integer.class);
        TypeUtil.name2Class.put("Long", Long.class);
        TypeUtil.name2Class.put("Short", Short.class);
        TypeUtil.name2Class.put(null, Void.TYPE);
        TypeUtil.name2Class.put("string", String.class);
        TypeUtil.name2Class.put("String", String.class);
        TypeUtil.name2Class.put("java.lang.String", String.class);
        (class2Name = new HashMap<Class<?>, String>()).put(Boolean.TYPE, "boolean");
        TypeUtil.class2Name.put(Byte.TYPE, "byte");
        TypeUtil.class2Name.put(Character.TYPE, "char");
        TypeUtil.class2Name.put(Double.TYPE, "double");
        TypeUtil.class2Name.put(Float.TYPE, "float");
        TypeUtil.class2Name.put(Integer.TYPE, "int");
        TypeUtil.class2Name.put(Long.TYPE, "long");
        TypeUtil.class2Name.put(Short.TYPE, "short");
        TypeUtil.class2Name.put(Void.TYPE, "void");
        TypeUtil.class2Name.put(Boolean.class, "java.lang.Boolean");
        TypeUtil.class2Name.put(Byte.class, "java.lang.Byte");
        TypeUtil.class2Name.put(Character.class, "java.lang.Character");
        TypeUtil.class2Name.put(Double.class, "java.lang.Double");
        TypeUtil.class2Name.put(Float.class, "java.lang.Float");
        TypeUtil.class2Name.put(Integer.class, "java.lang.Integer");
        TypeUtil.class2Name.put(Long.class, "java.lang.Long");
        TypeUtil.class2Name.put(Short.class, "java.lang.Short");
        TypeUtil.class2Name.put(null, "void");
        TypeUtil.class2Name.put(String.class, "java.lang.String");
        class2Value = new HashMap<Class<?>, Method>();
        try {
            final Class<?>[] s = (Class<?>[])new Class[] { String.class };
            TypeUtil.class2Value.put(Boolean.TYPE, Boolean.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Byte.TYPE, Byte.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Double.TYPE, Double.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Float.TYPE, Float.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Integer.TYPE, Integer.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Long.TYPE, Long.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Short.TYPE, Short.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Boolean.class, Boolean.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Byte.class, Byte.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Double.class, Double.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Float.class, Float.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Integer.class, Integer.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Long.class, Long.class.getMethod("valueOf", s));
            TypeUtil.class2Value.put(Short.class, Short.class.getMethod("valueOf", s));
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }
}
