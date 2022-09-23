// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class $Type
{
    public static final int VOID = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int BYTE = 3;
    public static final int SHORT = 4;
    public static final int INT = 5;
    public static final int FLOAT = 6;
    public static final int LONG = 7;
    public static final int DOUBLE = 8;
    public static final int ARRAY = 9;
    public static final int OBJECT = 10;
    public static final $Type VOID_TYPE;
    public static final $Type BOOLEAN_TYPE;
    public static final $Type CHAR_TYPE;
    public static final $Type BYTE_TYPE;
    public static final $Type SHORT_TYPE;
    public static final $Type INT_TYPE;
    public static final $Type FLOAT_TYPE;
    public static final $Type LONG_TYPE;
    public static final $Type DOUBLE_TYPE;
    private final int a;
    private final char[] b;
    private final int c;
    private final int d;
    
    private $Type(final int n) {
        this(n, null, 0, 1);
    }
    
    private $Type(final int a, final char[] b, final int c, final int d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    
    public static $Type getType(final String s) {
        return a(s.toCharArray(), 0);
    }
    
    public static $Type getObjectType(final String s) {
        final char[] charArray = s.toCharArray();
        return new $Type((charArray[0] == '[') ? 9 : 10, charArray, 0, charArray.length);
    }
    
    public static $Type getType(final Class clazz) {
        if (!clazz.isPrimitive()) {
            return getType(getDescriptor(clazz));
        }
        if (clazz == Integer.TYPE) {
            return $Type.INT_TYPE;
        }
        if (clazz == Void.TYPE) {
            return $Type.VOID_TYPE;
        }
        if (clazz == Boolean.TYPE) {
            return $Type.BOOLEAN_TYPE;
        }
        if (clazz == Byte.TYPE) {
            return $Type.BYTE_TYPE;
        }
        if (clazz == Character.TYPE) {
            return $Type.CHAR_TYPE;
        }
        if (clazz == Short.TYPE) {
            return $Type.SHORT_TYPE;
        }
        if (clazz == Double.TYPE) {
            return $Type.DOUBLE_TYPE;
        }
        if (clazz == Float.TYPE) {
            return $Type.FLOAT_TYPE;
        }
        return $Type.LONG_TYPE;
    }
    
    public static $Type[] getArgumentTypes(final String s) {
        final char[] charArray = s.toCharArray();
        int n = 1;
        int n2 = 0;
        while (true) {
            final char c = charArray[n++];
            if (c == ')') {
                break;
            }
            if (c == 'L') {
                while (charArray[n++] != ';') {}
                ++n2;
            }
            else {
                if (c == '[') {
                    continue;
                }
                ++n2;
            }
        }
        final $Type[] array = new $Type[n2];
        for (int n3 = 1, n4 = 0; charArray[n3] != ')'; n3 += array[n4].d + ((array[n4].a == 10) ? 2 : 0), ++n4) {
            array[n4] = a(charArray, n3);
        }
        return array;
    }
    
    public static $Type[] getArgumentTypes(final Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final $Type[] array = new $Type[parameterTypes.length];
        for (int i = parameterTypes.length - 1; i >= 0; --i) {
            array[i] = getType(parameterTypes[i]);
        }
        return array;
    }
    
    public static $Type getReturnType(final String s) {
        return a(s.toCharArray(), s.indexOf(41) + 1);
    }
    
    public static $Type getReturnType(final Method method) {
        return getType(method.getReturnType());
    }
    
    private static $Type a(final char[] array, final int n) {
        switch (array[n]) {
            case 'V': {
                return $Type.VOID_TYPE;
            }
            case 'Z': {
                return $Type.BOOLEAN_TYPE;
            }
            case 'C': {
                return $Type.CHAR_TYPE;
            }
            case 'B': {
                return $Type.BYTE_TYPE;
            }
            case 'S': {
                return $Type.SHORT_TYPE;
            }
            case 'I': {
                return $Type.INT_TYPE;
            }
            case 'F': {
                return $Type.FLOAT_TYPE;
            }
            case 'J': {
                return $Type.LONG_TYPE;
            }
            case 'D': {
                return $Type.DOUBLE_TYPE;
            }
            case '[': {
                int n2;
                for (n2 = 1; array[n + n2] == '['; ++n2) {}
                if (array[n + n2] == 'L') {
                    ++n2;
                    while (array[n + n2] != ';') {
                        ++n2;
                    }
                }
                return new $Type(9, array, n, n2 + 1);
            }
            default: {
                int n3;
                for (n3 = 1; array[n + n3] != ';'; ++n3) {}
                return new $Type(10, array, n + 1, n3 - 1);
            }
        }
    }
    
    public int getSort() {
        return this.a;
    }
    
    public int getDimensions() {
        int n;
        for (n = 1; this.b[this.c + n] == '['; ++n) {}
        return n;
    }
    
    public $Type getElementType() {
        return a(this.b, this.c + this.getDimensions());
    }
    
    public String getClassName() {
        switch (this.a) {
            case 0: {
                return "void";
            }
            case 1: {
                return "boolean";
            }
            case 2: {
                return "char";
            }
            case 3: {
                return "byte";
            }
            case 4: {
                return "short";
            }
            case 5: {
                return "int";
            }
            case 6: {
                return "float";
            }
            case 7: {
                return "long";
            }
            case 8: {
                return "double";
            }
            case 9: {
                final StringBuffer sb = new StringBuffer(this.getElementType().getClassName());
                for (int i = this.getDimensions(); i > 0; --i) {
                    sb.append("[]");
                }
                return sb.toString();
            }
            default: {
                return new String(this.b, this.c, this.d).replace('/', '.');
            }
        }
    }
    
    public String getInternalName() {
        return new String(this.b, this.c, this.d);
    }
    
    public String getDescriptor() {
        final StringBuffer sb = new StringBuffer();
        this.a(sb);
        return sb.toString();
    }
    
    public static String getMethodDescriptor(final $Type $Type, final $Type[] array) {
        final StringBuffer sb = new StringBuffer();
        sb.append('(');
        for (int i = 0; i < array.length; ++i) {
            array[i].a(sb);
        }
        sb.append(')');
        $Type.a(sb);
        return sb.toString();
    }
    
    private void a(final StringBuffer sb) {
        switch (this.a) {
            case 0: {
                sb.append('V');
            }
            case 1: {
                sb.append('Z');
            }
            case 2: {
                sb.append('C');
            }
            case 3: {
                sb.append('B');
            }
            case 4: {
                sb.append('S');
            }
            case 5: {
                sb.append('I');
            }
            case 6: {
                sb.append('F');
            }
            case 7: {
                sb.append('J');
            }
            case 8: {
                sb.append('D');
            }
            case 9: {
                sb.append(this.b, this.c, this.d);
            }
            default: {
                sb.append('L');
                sb.append(this.b, this.c, this.d);
                sb.append(';');
            }
        }
    }
    
    public static String getInternalName(final Class clazz) {
        return clazz.getName().replace('.', '/');
    }
    
    public static String getDescriptor(final Class clazz) {
        final StringBuffer sb = new StringBuffer();
        a(sb, clazz);
        return sb.toString();
    }
    
    public static String getConstructorDescriptor(final Constructor constructor) {
        final Class[] parameterTypes = constructor.getParameterTypes();
        final StringBuffer sb = new StringBuffer();
        sb.append('(');
        for (int i = 0; i < parameterTypes.length; ++i) {
            a(sb, parameterTypes[i]);
        }
        return sb.append(")V").toString();
    }
    
    public static String getMethodDescriptor(final Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final StringBuffer sb = new StringBuffer();
        sb.append('(');
        for (int i = 0; i < parameterTypes.length; ++i) {
            a(sb, parameterTypes[i]);
        }
        sb.append(')');
        a(sb, method.getReturnType());
        return sb.toString();
    }
    
    private static void a(final StringBuffer sb, final Class clazz) {
        Class<Float> componentType;
        for (componentType = (Class<Float>)clazz; !componentType.isPrimitive(); componentType = (Class<Float>)componentType.getComponentType()) {
            if (!componentType.isArray()) {
                sb.append('L');
                final String name = componentType.getName();
                for (int length = name.length(), i = 0; i < length; ++i) {
                    final char char1 = name.charAt(i);
                    sb.append((char1 == '.') ? '/' : char1);
                }
                sb.append(';');
                return;
            }
            sb.append('[');
        }
        char c;
        if (componentType == Integer.TYPE) {
            c = 'I';
        }
        else if (componentType == Void.TYPE) {
            c = 'V';
        }
        else if (componentType == Boolean.TYPE) {
            c = 'Z';
        }
        else if (componentType == Byte.TYPE) {
            c = 'B';
        }
        else if (componentType == Character.TYPE) {
            c = 'C';
        }
        else if (componentType == Short.TYPE) {
            c = 'S';
        }
        else if (componentType == Double.TYPE) {
            c = 'D';
        }
        else if (componentType == Float.TYPE) {
            c = 'F';
        }
        else {
            c = 'J';
        }
        sb.append(c);
    }
    
    public int getSize() {
        return (this.a == 7 || this.a == 8) ? 2 : 1;
    }
    
    public int getOpcode(final int n) {
        if (n == 46 || n == 79) {
            switch (this.a) {
                case 1:
                case 3: {
                    return n + 5;
                }
                case 2: {
                    return n + 6;
                }
                case 4: {
                    return n + 7;
                }
                case 5: {
                    return n;
                }
                case 6: {
                    return n + 2;
                }
                case 7: {
                    return n + 1;
                }
                case 8: {
                    return n + 3;
                }
                default: {
                    return n + 4;
                }
            }
        }
        else {
            switch (this.a) {
                case 0: {
                    return n + 5;
                }
                case 1:
                case 2:
                case 3:
                case 4:
                case 5: {
                    return n;
                }
                case 6: {
                    return n + 2;
                }
                case 7: {
                    return n + 1;
                }
                case 8: {
                    return n + 3;
                }
                default: {
                    return n + 4;
                }
            }
        }
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof $Type)) {
            return false;
        }
        final $Type $Type = ($Type)o;
        if (this.a != $Type.a) {
            return false;
        }
        if (this.a == 10 || this.a == 9) {
            if (this.d != $Type.d) {
                return false;
            }
            for (int i = this.c, c = $Type.c; i < i + this.d; ++i, ++c) {
                if (this.b[i] != $Type.b[c]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int hashCode() {
        int n = 13 * this.a;
        if (this.a == 10 || this.a == 9) {
            for (int i = this.c; i < i + this.d; ++i) {
                n = 17 * (n + this.b[i]);
            }
        }
        return n;
    }
    
    public String toString() {
        return this.getDescriptor();
    }
    
    static {
        VOID_TYPE = new $Type(0);
        BOOLEAN_TYPE = new $Type(1);
        CHAR_TYPE = new $Type(2);
        BYTE_TYPE = new $Type(3);
        SHORT_TYPE = new $Type(4);
        INT_TYPE = new $Type(5);
        FLOAT_TYPE = new $Type(6);
        LONG_TYPE = new $Type(7);
        DOUBLE_TYPE = new $Type(8);
    }
}
