// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr;

public enum XdrDataType
{
    UNKNOWN(-1), 
    BOOLEAN(1), 
    INTEGER(2), 
    BYTES(3), 
    STRING(4), 
    ENUM(5), 
    OPAQUE(6), 
    UNSIGNED_INTEGER(7), 
    STRUCT(8), 
    UNION(9);
    
    private int value;
    
    private XdrDataType(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
}
