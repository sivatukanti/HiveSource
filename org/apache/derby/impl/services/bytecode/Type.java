// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

import org.apache.derby.iapi.services.classfile.ClassHolder;

final class Type
{
    static final Type LONG;
    static final Type INT;
    static final Type SHORT;
    static final Type BYTE;
    static final Type BOOLEAN;
    static final Type FLOAT;
    static final Type DOUBLE;
    static final Type STRING;
    private final String javaName;
    private final short vmType;
    private final String vmName;
    final String vmNameSimple;
    
    Type(final String javaName, final String vmName) {
        this.vmName = vmName;
        this.javaName = javaName;
        this.vmType = BCJava.vmTypeId(vmName);
        this.vmNameSimple = ClassHolder.convertToInternalClassName(javaName);
    }
    
    String javaName() {
        return this.javaName;
    }
    
    String vmName() {
        return this.vmName;
    }
    
    short vmType() {
        return this.vmType;
    }
    
    int width() {
        return width(this.vmType);
    }
    
    static int width(final short n) {
        switch (n) {
            case -1: {
                return 0;
            }
            case 3:
            case 5: {
                return 2;
            }
            default: {
                return 1;
            }
        }
    }
    
    static {
        LONG = new Type("long", "J");
        INT = new Type("int", "I");
        SHORT = new Type("short", "S");
        BYTE = new Type("byte", "B");
        BOOLEAN = new Type("boolean", "Z");
        FLOAT = new Type("float", "F");
        DOUBLE = new Type("double", "D");
        STRING = new Type("java.lang.String", "Ljava/lang/String;");
    }
}
