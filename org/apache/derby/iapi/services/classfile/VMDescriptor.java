// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

public interface VMDescriptor
{
    public static final char C_VOID = 'V';
    public static final String VOID = "V";
    public static final char C_BOOLEAN = 'Z';
    public static final String BOOLEAN = "Z";
    public static final char C_BYTE = 'B';
    public static final String BYTE = "B";
    public static final char C_CHAR = 'C';
    public static final String CHAR = "C";
    public static final char C_SHORT = 'S';
    public static final String SHORT = "S";
    public static final char C_INT = 'I';
    public static final String INT = "I";
    public static final char C_LONG = 'J';
    public static final String LONG = "J";
    public static final char C_FLOAT = 'F';
    public static final String FLOAT = "F";
    public static final char C_DOUBLE = 'D';
    public static final String DOUBLE = "D";
    public static final char C_ARRAY = '[';
    public static final String ARRAY = "[";
    public static final char C_CLASS = 'L';
    public static final String CLASS = "L";
    public static final char C_METHOD = '(';
    public static final String METHOD = "(";
    public static final char C_ENDCLASS = ';';
    public static final String ENDCLASS = ";";
    public static final char C_ENDMETHOD = ')';
    public static final String ENDMETHOD = ")";
    public static final char C_PACKAGE = '/';
    public static final String PACKAGE = "/";
    public static final int CONSTANT_Class = 7;
    public static final int CONSTANT_Fieldref = 9;
    public static final int CONSTANT_Methodref = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_String = 8;
    public static final int CONSTANT_Integer = 3;
    public static final int CONSTANT_Float = 4;
    public static final int CONSTANT_Long = 5;
    public static final int CONSTANT_Double = 6;
    public static final int CONSTANT_NameAndType = 12;
    public static final int CONSTANT_Utf8 = 1;
    public static final int JAVA_CLASS_FORMAT_MAGIC = -889275714;
    public static final int JAVA_CLASS_FORMAT_MAJOR_VERSION = 45;
    public static final int JAVA_CLASS_FORMAT_MINOR_VERSION = 3;
}
