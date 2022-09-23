// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import org.datanucleus.asm.Type;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.ClassConstants;
import org.datanucleus.asm.MethodVisitor;
import org.datanucleus.util.Localiser;

public final class EnhanceUtils
{
    protected static Localiser LOCALISER;
    public static final String ACN_boolean;
    public static final String ACN_byte;
    public static final String ACN_char;
    public static final String ACN_double;
    public static final String ACN_float;
    public static final String ACN_int;
    public static final String ACN_long;
    public static final String ACN_short;
    public static final String ACN_Boolean;
    public static final String ACN_Byte;
    public static final String ACN_Character;
    public static final String ACN_Double;
    public static final String ACN_Float;
    public static final String ACN_Integer;
    public static final String ACN_Long;
    public static final String ACN_Short;
    public static final String ACN_String;
    public static final String ACN_Object;
    public static final String CD_String;
    public static final String CD_Object;
    
    private EnhanceUtils() {
    }
    
    public static void addBIPUSHToMethod(final MethodVisitor visitor, final int i) {
        if (i < 6) {
            switch (i) {
                case 0: {
                    visitor.visitInsn(3);
                    break;
                }
                case 1: {
                    visitor.visitInsn(4);
                    break;
                }
                case 2: {
                    visitor.visitInsn(5);
                    break;
                }
                case 3: {
                    visitor.visitInsn(6);
                    break;
                }
                case 4: {
                    visitor.visitInsn(7);
                    break;
                }
                case 5: {
                    visitor.visitInsn(8);
                    break;
                }
            }
        }
        else if (i < 127) {
            visitor.visitIntInsn(16, i);
        }
        else if (i < 32767) {
            visitor.visitIntInsn(17, i);
        }
    }
    
    public static void addReturnForType(final MethodVisitor visitor, final Class type) {
        if (type == Integer.TYPE || type == Boolean.TYPE || type == Byte.TYPE || type == Character.TYPE || type == Short.TYPE) {
            visitor.visitInsn(172);
        }
        else if (type == Double.TYPE) {
            visitor.visitInsn(175);
        }
        else if (type == Float.TYPE) {
            visitor.visitInsn(174);
        }
        else if (type == Long.TYPE) {
            visitor.visitInsn(173);
        }
        else {
            visitor.visitInsn(176);
        }
    }
    
    public static void addLoadForType(final MethodVisitor visitor, final Class type, final int number) {
        if (type == Integer.TYPE || type == Boolean.TYPE || type == Byte.TYPE || type == Character.TYPE || type == Short.TYPE) {
            visitor.visitVarInsn(21, number);
        }
        else if (type == Double.TYPE) {
            visitor.visitVarInsn(24, number);
        }
        else if (type == Float.TYPE) {
            visitor.visitVarInsn(23, number);
        }
        else if (type == Long.TYPE) {
            visitor.visitVarInsn(22, number);
        }
        else {
            visitor.visitVarInsn(25, number);
        }
    }
    
    public static String getTypeNameForJDOMethod(final Class cls) {
        if (cls == null) {
            return null;
        }
        if (cls == ClassConstants.BOOLEAN) {
            return "Boolean";
        }
        if (cls == ClassConstants.BYTE) {
            return "Byte";
        }
        if (cls == ClassConstants.CHAR) {
            return "Char";
        }
        if (cls == ClassConstants.DOUBLE) {
            return "Double";
        }
        if (cls == ClassConstants.FLOAT) {
            return "Float";
        }
        if (cls == ClassConstants.INT) {
            return "Int";
        }
        if (cls == ClassConstants.LONG) {
            return "Long";
        }
        if (cls == ClassConstants.SHORT) {
            return "Short";
        }
        if (cls == ClassConstants.JAVA_LANG_STRING) {
            return "String";
        }
        return "Object";
    }
    
    public static String getTypeDescriptorForType(final String clsName) {
        if (clsName == null) {
            return null;
        }
        if (clsName.equals(ClassNameConstants.BOOLEAN)) {
            return Type.BOOLEAN_TYPE.getDescriptor();
        }
        if (clsName.equals(ClassNameConstants.BYTE)) {
            return Type.BYTE_TYPE.getDescriptor();
        }
        if (clsName.equals(ClassNameConstants.CHAR)) {
            return Type.CHAR_TYPE.getDescriptor();
        }
        if (clsName.equals(ClassNameConstants.DOUBLE)) {
            return Type.DOUBLE_TYPE.getDescriptor();
        }
        if (clsName.equals(ClassNameConstants.FLOAT)) {
            return Type.FLOAT_TYPE.getDescriptor();
        }
        if (clsName.equals(ClassNameConstants.INT)) {
            return Type.INT_TYPE.getDescriptor();
        }
        if (clsName.equals(ClassNameConstants.LONG)) {
            return Type.LONG_TYPE.getDescriptor();
        }
        if (clsName.equals(ClassNameConstants.SHORT)) {
            return Type.SHORT_TYPE.getDescriptor();
        }
        if (clsName.equals(ClassNameConstants.JAVA_LANG_STRING)) {
            return EnhanceUtils.CD_String;
        }
        return "L" + clsName.replace('.', '/') + ";";
    }
    
    public static String getTypeDescriptorForJDOMethod(final Class cls) {
        if (cls == null) {
            return null;
        }
        if (cls == ClassConstants.BOOLEAN) {
            return Type.BOOLEAN_TYPE.getDescriptor();
        }
        if (cls == ClassConstants.BYTE) {
            return Type.BYTE_TYPE.getDescriptor();
        }
        if (cls == ClassConstants.CHAR) {
            return Type.CHAR_TYPE.getDescriptor();
        }
        if (cls == ClassConstants.DOUBLE) {
            return Type.DOUBLE_TYPE.getDescriptor();
        }
        if (cls == ClassConstants.FLOAT) {
            return Type.FLOAT_TYPE.getDescriptor();
        }
        if (cls == ClassConstants.INT) {
            return Type.INT_TYPE.getDescriptor();
        }
        if (cls == ClassConstants.LONG) {
            return Type.LONG_TYPE.getDescriptor();
        }
        if (cls == ClassConstants.SHORT) {
            return Type.SHORT_TYPE.getDescriptor();
        }
        if (cls == ClassConstants.JAVA_LANG_STRING) {
            return EnhanceUtils.CD_String;
        }
        return EnhanceUtils.CD_Object;
    }
    
    public static String getASMClassNameForSingleFieldIdentityConstructor(final Class fieldType) {
        if (fieldType == null) {
            return null;
        }
        if (fieldType == ClassConstants.BYTE || fieldType == ClassConstants.JAVA_LANG_BYTE) {
            return EnhanceUtils.ACN_Byte;
        }
        if (fieldType == ClassConstants.CHAR || fieldType == ClassConstants.JAVA_LANG_CHARACTER) {
            return EnhanceUtils.ACN_Character;
        }
        if (fieldType == ClassConstants.INT || fieldType == ClassConstants.JAVA_LANG_INTEGER) {
            return EnhanceUtils.ACN_Integer;
        }
        if (fieldType == ClassConstants.LONG || fieldType == ClassConstants.JAVA_LANG_LONG) {
            return EnhanceUtils.ACN_Long;
        }
        if (fieldType == ClassConstants.SHORT || fieldType == ClassConstants.JAVA_LANG_SHORT) {
            return EnhanceUtils.ACN_Short;
        }
        if (fieldType == ClassConstants.JAVA_LANG_STRING) {
            return EnhanceUtils.ACN_String;
        }
        return EnhanceUtils.ACN_Object;
    }
    
    static {
        EnhanceUtils.LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        ACN_boolean = ClassNameConstants.BOOLEAN;
        ACN_byte = ClassNameConstants.BYTE;
        ACN_char = ClassNameConstants.CHAR;
        ACN_double = ClassNameConstants.DOUBLE;
        ACN_float = ClassNameConstants.FLOAT;
        ACN_int = ClassNameConstants.INT;
        ACN_long = ClassNameConstants.LONG;
        ACN_short = ClassNameConstants.SHORT;
        ACN_Boolean = ClassNameConstants.JAVA_LANG_BOOLEAN.replace('.', '/');
        ACN_Byte = ClassNameConstants.JAVA_LANG_BYTE.replace('.', '/');
        ACN_Character = ClassNameConstants.JAVA_LANG_CHARACTER.replace('.', '/');
        ACN_Double = ClassNameConstants.JAVA_LANG_DOUBLE.replace('.', '/');
        ACN_Float = ClassNameConstants.JAVA_LANG_FLOAT.replace('.', '/');
        ACN_Integer = ClassNameConstants.JAVA_LANG_INTEGER.replace('.', '/');
        ACN_Long = ClassNameConstants.JAVA_LANG_LONG.replace('.', '/');
        ACN_Short = ClassNameConstants.JAVA_LANG_SHORT.replace('.', '/');
        ACN_String = ClassNameConstants.JAVA_LANG_STRING.replace('.', '/');
        ACN_Object = Object.class.getName().replace('.', '/');
        CD_String = Type.getDescriptor(String.class);
        CD_Object = Type.getDescriptor(Object.class);
    }
}
