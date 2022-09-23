// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import org.objectweb.asm.Label;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.objectweb.asm.Type;
import org.objectweb.asm.MethodVisitor;

public class ASMUtil
{
    public static void autoBoxing(final MethodVisitor mv, final Class<?> clz) {
        autoBoxing(mv, Type.getType(clz));
    }
    
    public static Accessor[] getAccessors(final Class<?> type, FieldFilter filter) {
        Class<?> nextClass = type;
        final HashMap<String, Accessor> map = new HashMap<String, Accessor>();
        if (filter == null) {
            filter = BasicFiledFilter.SINGLETON;
        }
        while (nextClass != Object.class) {
            final Field[] declaredFields = nextClass.getDeclaredFields();
            Field[] array;
            for (int length = (array = declaredFields).length, i = 0; i < length; ++i) {
                final Field field = array[i];
                final String fn = field.getName();
                if (!map.containsKey(fn)) {
                    final Accessor acc = new Accessor(nextClass, field, filter);
                    if (acc.isUsable()) {
                        map.put(fn, acc);
                    }
                }
            }
            nextClass = nextClass.getSuperclass();
        }
        return map.values().toArray(new Accessor[map.size()]);
    }
    
    protected static void autoBoxing(final MethodVisitor mv, final Type fieldType) {
        switch (fieldType.getSort()) {
            case 1: {
                mv.visitMethodInsn(184, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                break;
            }
            case 3: {
                mv.visitMethodInsn(184, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                break;
            }
            case 2: {
                mv.visitMethodInsn(184, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                break;
            }
            case 4: {
                mv.visitMethodInsn(184, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                break;
            }
            case 5: {
                mv.visitMethodInsn(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                break;
            }
            case 6: {
                mv.visitMethodInsn(184, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                break;
            }
            case 7: {
                mv.visitMethodInsn(184, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                break;
            }
            case 8: {
                mv.visitMethodInsn(184, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                break;
            }
        }
    }
    
    protected static void autoUnBoxing1(final MethodVisitor mv, final Type fieldType) {
        switch (fieldType.getSort()) {
            case 1: {
                mv.visitTypeInsn(192, "java/lang/Boolean");
                mv.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z");
                break;
            }
            case 3: {
                mv.visitTypeInsn(192, "java/lang/Byte");
                mv.visitMethodInsn(182, "java/lang/Byte", "byteValue", "()B");
                break;
            }
            case 2: {
                mv.visitTypeInsn(192, "java/lang/Character");
                mv.visitMethodInsn(182, "java/lang/Character", "charValue", "()C");
                break;
            }
            case 4: {
                mv.visitTypeInsn(192, "java/lang/Short");
                mv.visitMethodInsn(182, "java/lang/Short", "shortValue", "()S");
                break;
            }
            case 5: {
                mv.visitTypeInsn(192, "java/lang/Integer");
                mv.visitMethodInsn(182, "java/lang/Integer", "intValue", "()I");
                break;
            }
            case 6: {
                mv.visitTypeInsn(192, "java/lang/Float");
                mv.visitMethodInsn(182, "java/lang/Float", "floatValue", "()F");
                break;
            }
            case 7: {
                mv.visitTypeInsn(192, "java/lang/Long");
                mv.visitMethodInsn(182, "java/lang/Long", "longValue", "()J");
                break;
            }
            case 8: {
                mv.visitTypeInsn(192, "java/lang/Double");
                mv.visitMethodInsn(182, "java/lang/Double", "doubleValue", "()D");
                break;
            }
            case 9: {
                mv.visitTypeInsn(192, fieldType.getInternalName());
                break;
            }
            default: {
                mv.visitTypeInsn(192, fieldType.getInternalName());
                break;
            }
        }
    }
    
    protected static void autoUnBoxing2(final MethodVisitor mv, final Type fieldType) {
        switch (fieldType.getSort()) {
            case 1: {
                mv.visitTypeInsn(192, "java/lang/Boolean");
                mv.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z");
                break;
            }
            case 3: {
                mv.visitTypeInsn(192, "java/lang/Number");
                mv.visitMethodInsn(182, "java/lang/Number", "byteValue", "()B");
                break;
            }
            case 2: {
                mv.visitTypeInsn(192, "java/lang/Character");
                mv.visitMethodInsn(182, "java/lang/Character", "charValue", "()C");
                break;
            }
            case 4: {
                mv.visitTypeInsn(192, "java/lang/Number");
                mv.visitMethodInsn(182, "java/lang/Number", "shortValue", "()S");
                break;
            }
            case 5: {
                mv.visitTypeInsn(192, "java/lang/Number");
                mv.visitMethodInsn(182, "java/lang/Number", "intValue", "()I");
                break;
            }
            case 6: {
                mv.visitTypeInsn(192, "java/lang/Number");
                mv.visitMethodInsn(182, "java/lang/Number", "floatValue", "()F");
                break;
            }
            case 7: {
                mv.visitTypeInsn(192, "java/lang/Number");
                mv.visitMethodInsn(182, "java/lang/Number", "longValue", "()J");
                break;
            }
            case 8: {
                mv.visitTypeInsn(192, "java/lang/Number");
                mv.visitMethodInsn(182, "java/lang/Number", "doubleValue", "()D");
                break;
            }
            case 9: {
                mv.visitTypeInsn(192, fieldType.getInternalName());
                break;
            }
            default: {
                mv.visitTypeInsn(192, fieldType.getInternalName());
                break;
            }
        }
    }
    
    public static Label[] newLabels(final int cnt) {
        final Label[] r = new Label[cnt];
        for (int i = 0; i < cnt; ++i) {
            r[i] = new Label();
        }
        return r;
    }
    
    public static String getSetterName(final String key) {
        final int len = key.length();
        final char[] b = new char[len + 3];
        b[0] = 's';
        b[1] = 'e';
        b[2] = 't';
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        b[3] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 3] = key.charAt(i);
        }
        return new String(b);
    }
    
    public static String getGetterName(final String key) {
        final int len = key.length();
        final char[] b = new char[len + 3];
        b[0] = 'g';
        b[1] = 'e';
        b[2] = 't';
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        b[3] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 3] = key.charAt(i);
        }
        return new String(b);
    }
    
    public static String getIsName(final String key) {
        final int len = key.length();
        final char[] b = new char[len + 2];
        b[0] = 'i';
        b[1] = 's';
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        b[2] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 2] = key.charAt(i);
        }
        return new String(b);
    }
}
