// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import org.mortbay.log.Log;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class TypeUtil
{
    public static int CR;
    public static int LF;
    private static final HashMap name2Class;
    private static final HashMap class2Name;
    private static final HashMap class2Value;
    private static Class[] stringArg;
    private static int intCacheSize;
    private static Integer[] integerCache;
    private static String[] integerStrCache;
    private static Integer minusOne;
    private static int longCacheSize;
    private static Long[] longCache;
    private static Long minusOneL;
    
    public static Class fromName(final String name) {
        return TypeUtil.name2Class.get(name);
    }
    
    public static String toName(final Class type) {
        return TypeUtil.class2Name.get(type);
    }
    
    public static Object valueOf(final Class type, final String value) {
        try {
            if (type.equals(String.class)) {
                return value;
            }
            final Method m = TypeUtil.class2Value.get(type);
            if (m != null) {
                return m.invoke(null, value);
            }
            if (type.equals(Character.TYPE) || type.equals(Character.class)) {
                return new Character(value.charAt(0));
            }
            final Constructor c = type.getConstructor((Class[])TypeUtil.stringArg);
            return c.newInstance(value);
        }
        catch (NoSuchMethodException e2) {}
        catch (IllegalAccessException e3) {}
        catch (InstantiationException e4) {}
        catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Error) {
                throw (Error)e.getTargetException();
            }
        }
        return null;
    }
    
    public static Object valueOf(final String type, final String value) {
        return valueOf(fromName(type), value);
    }
    
    public static Integer newInteger(final int i) {
        if (i >= 0 && i < TypeUtil.intCacheSize) {
            if (TypeUtil.integerCache[i] == null) {
                TypeUtil.integerCache[i] = new Integer(i);
            }
            return TypeUtil.integerCache[i];
        }
        if (i == -1) {
            return TypeUtil.minusOne;
        }
        return new Integer(i);
    }
    
    public static Long newLong(final long i) {
        if (i >= 0L && i < TypeUtil.longCacheSize) {
            if (TypeUtil.longCache[(int)i] == null) {
                TypeUtil.longCache[(int)i] = new Long(i);
            }
            return TypeUtil.longCache[(int)i];
        }
        if (i == -1L) {
            return TypeUtil.minusOneL;
        }
        return new Long(i);
    }
    
    public static String toString(final int i) {
        if (i >= 0 && i < TypeUtil.intCacheSize) {
            if (TypeUtil.integerStrCache[i] == null) {
                TypeUtil.integerStrCache[i] = Integer.toString(i);
            }
            return TypeUtil.integerStrCache[i];
        }
        if (i == -1) {
            return "-1";
        }
        return Integer.toString(i);
    }
    
    public static String toString(final long i) {
        if (i >= 0L && i < TypeUtil.intCacheSize) {
            if (TypeUtil.integerStrCache[(int)i] == null) {
                TypeUtil.integerStrCache[(int)i] = Long.toString(i);
            }
            return TypeUtil.integerStrCache[(int)i];
        }
        if (i == -1L) {
            return "-1";
        }
        return Long.toString(i);
    }
    
    public static int parseInt(final String s, final int offset, int length, final int base) throws NumberFormatException {
        int value = 0;
        if (length < 0) {
            length = s.length() - offset;
        }
        for (int i = 0; i < length; ++i) {
            final char c = s.charAt(offset + i);
            int digit = c - '0';
            if (digit < 0 || digit >= base || digit >= 10) {
                digit = '\n' + c - 65;
                if (digit < 10 || digit >= base) {
                    digit = '\n' + c - 97;
                }
            }
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
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            final int bi = 0xFF & bytes[i];
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
    
    public static byte convertHexDigit(final byte b) {
        if (b >= 48 && b <= 57) {
            return (byte)(b - 48);
        }
        if (b >= 97 && b <= 102) {
            return (byte)(b - 97 + 10);
        }
        if (b >= 65 && b <= 70) {
            return (byte)(b - 65 + 10);
        }
        return 0;
    }
    
    public static String toHexString(final byte[] b) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
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
    
    public static String toHexString(final byte[] b, final int offset, final int length) {
        final StringBuffer buf = new StringBuffer();
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
    
    public static void dump(final Class c) {
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
    
    public static byte[] readLine(final InputStream in) throws IOException {
        byte[] buf = new byte[256];
        int i = 0;
        int loops = 0;
        int ch = 0;
        while (true) {
            ch = in.read();
            if (ch < 0) {
                break;
            }
            if (++loops == 1 && ch == TypeUtil.LF) {
                continue;
            }
            if (ch == TypeUtil.CR) {
                break;
            }
            if (ch == TypeUtil.LF) {
                break;
            }
            if (i >= buf.length) {
                final byte[] old_buf = buf;
                buf = new byte[old_buf.length + 256];
                System.arraycopy(old_buf, 0, buf, 0, old_buf.length);
            }
            buf[i++] = (byte)ch;
        }
        if (ch == -1 && i == 0) {
            return null;
        }
        if (ch == TypeUtil.CR && in.available() >= 1 && in.markSupported()) {
            in.mark(1);
            ch = in.read();
            if (ch != TypeUtil.LF) {
                in.reset();
            }
        }
        final byte[] old_buf = buf;
        buf = new byte[i];
        System.arraycopy(old_buf, 0, buf, 0, i);
        return buf;
    }
    
    public static URL jarFor(String className) {
        try {
            className = className.replace('.', '/') + ".class";
            final URL url = Loader.getResource(null, className, false);
            final String s = url.toString();
            if (s.startsWith("jar:file:")) {
                return new URL(s.substring(4, s.indexOf("!/")));
            }
        }
        catch (Exception e) {
            Log.ignore(e);
        }
        return null;
    }
    
    static {
        TypeUtil.CR = 13;
        TypeUtil.LF = 10;
        (name2Class = new HashMap()).put("boolean", Boolean.TYPE);
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
        (class2Name = new HashMap()).put(Boolean.TYPE, "boolean");
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
        class2Value = new HashMap();
        try {
            final Class[] s = { String.class };
            TypeUtil.class2Value.put(Boolean.TYPE, Boolean.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Byte.TYPE, Byte.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Double.TYPE, Double.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Float.TYPE, Float.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Integer.TYPE, Integer.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Long.TYPE, Long.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Short.TYPE, Short.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Boolean.class, Boolean.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Byte.class, Byte.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Double.class, Double.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Float.class, Float.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Integer.class, Integer.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Long.class, Long.class.getMethod("valueOf", (Class[])s));
            TypeUtil.class2Value.put(Short.class, Short.class.getMethod("valueOf", (Class[])s));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        TypeUtil.stringArg = new Class[] { String.class };
        TypeUtil.intCacheSize = Integer.getInteger("org.mortbay.util.TypeUtil.IntegerCacheSize", 600);
        TypeUtil.integerCache = new Integer[TypeUtil.intCacheSize];
        TypeUtil.integerStrCache = new String[TypeUtil.intCacheSize];
        TypeUtil.minusOne = new Integer(-1);
        TypeUtil.longCacheSize = Integer.getInteger("org.mortbay.util.TypeUtil.LongCacheSize", 64);
        TypeUtil.longCache = new Long[TypeUtil.longCacheSize];
        TypeUtil.minusOneL = new Long(-1L);
    }
}
