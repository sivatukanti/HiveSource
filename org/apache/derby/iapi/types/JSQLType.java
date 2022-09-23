// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.Formatable;

public final class JSQLType implements Formatable
{
    public static final byte SQLTYPE = 0;
    public static final byte JAVA_CLASS = 1;
    public static final byte JAVA_PRIMITIVE = 2;
    public static final byte NOT_PRIMITIVE = -1;
    public static final byte BOOLEAN = 0;
    public static final byte CHAR = 1;
    public static final byte BYTE = 2;
    public static final byte SHORT = 3;
    public static final byte INT = 4;
    public static final byte LONG = 5;
    public static final byte FLOAT = 6;
    public static final byte DOUBLE = 7;
    private static final String[] wrapperClassNames;
    private static final String[] primitiveNames;
    private byte category;
    private DataTypeDescriptor sqlType;
    private String javaClassName;
    private byte primitiveKind;
    
    public JSQLType() {
        this.category = 2;
        this.initialize((byte)4);
    }
    
    public JSQLType(final DataTypeDescriptor dataTypeDescriptor) {
        this.category = 2;
        this.initialize(dataTypeDescriptor);
    }
    
    public JSQLType(final String s) {
        this.category = 2;
        final byte primitiveID = getPrimitiveID(s);
        if (primitiveID != -1) {
            this.initialize(primitiveID);
        }
        else {
            this.initialize(s);
        }
    }
    
    public JSQLType(final byte b) {
        this.category = 2;
        this.initialize(b);
    }
    
    public byte getCategory() {
        return this.category;
    }
    
    public byte getPrimitiveKind() {
        return this.primitiveKind;
    }
    
    public String getJavaClassName() {
        return this.javaClassName;
    }
    
    public DataTypeDescriptor getSQLType() throws StandardException {
        if (this.sqlType == null) {
            String s;
            if (this.category == 1) {
                s = this.javaClassName;
            }
            else {
                s = getWrapperClassName(this.primitiveKind);
            }
            this.sqlType = DataTypeDescriptor.getSQLDataTypeDescriptor(s);
        }
        return this.sqlType;
    }
    
    public static String getPrimitiveName(final byte b) {
        return JSQLType.primitiveNames[b];
    }
    
    public int getTypeFormatId() {
        return 307;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        switch (objectInput.readByte()) {
            case 0: {
                this.initialize((DataTypeDescriptor)objectInput.readObject());
                break;
            }
            case 1: {
                this.initialize((String)objectInput.readObject());
                break;
            }
            case 2: {
                this.initialize(objectInput.readByte());
                break;
            }
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeByte(this.category);
        switch (this.category) {
            case 0: {
                objectOutput.writeObject(this.sqlType);
                break;
            }
            case 1: {
                objectOutput.writeObject(this.javaClassName);
                break;
            }
            case 2: {
                objectOutput.writeByte(this.primitiveKind);
                break;
            }
        }
    }
    
    private void initialize(final byte b) {
        this.initialize((byte)2, null, null, b);
    }
    
    private void initialize(final DataTypeDescriptor dataTypeDescriptor) {
        this.initialize((byte)0, dataTypeDescriptor, null, (byte)(-1));
    }
    
    private void initialize(final String s) {
        this.initialize((byte)1, null, s, (byte)(-1));
    }
    
    private void initialize(final byte category, final DataTypeDescriptor sqlType, final String javaClassName, final byte primitiveKind) {
        this.category = category;
        this.sqlType = sqlType;
        this.javaClassName = javaClassName;
        this.primitiveKind = primitiveKind;
    }
    
    private static String getWrapperClassName(final byte b) {
        if (b == -1) {
            return "";
        }
        return JSQLType.wrapperClassNames[b];
    }
    
    private static byte getPrimitiveID(final String anObject) {
        for (byte b = 0; b <= 7; ++b) {
            if (JSQLType.primitiveNames[b].equals(anObject)) {
                return b;
            }
        }
        return -1;
    }
    
    static {
        wrapperClassNames = new String[] { "java.lang.Boolean", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double" };
        primitiveNames = new String[] { "boolean", "char", "byte", "short", "int", "long", "float", "double" };
    }
}
