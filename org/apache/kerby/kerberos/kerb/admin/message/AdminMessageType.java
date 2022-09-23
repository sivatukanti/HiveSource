// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.message;

import org.apache.kerby.xdr.EnumType;

public enum AdminMessageType implements EnumType
{
    NONE(-1), 
    ADD_PRINCIPAL_REQ(0), 
    ADD_PRINCIPAL_REP(1), 
    DELETE_PRINCIPAL_REQ(2), 
    DELETE_PRINCIPAL_REP(3), 
    RENAME_PRINCIPAL_REQ(4), 
    RENAME_PRINCIPAL_REP(5), 
    GET_PRINCS_REQ(6), 
    GET_PRINCS_REP(7);
    
    private int value;
    
    private AdminMessageType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    public static AdminMessageType findType(final int value) {
        if (value >= 0) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (AdminMessageType)e;
                }
            }
        }
        return AdminMessageType.NONE;
    }
}
