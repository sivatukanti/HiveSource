// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum PrincipalType implements TEnum
{
    USER(1), 
    ROLE(2), 
    GROUP(3);
    
    private final int value;
    
    private PrincipalType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static PrincipalType findByValue(final int value) {
        switch (value) {
            case 1: {
                return PrincipalType.USER;
            }
            case 2: {
                return PrincipalType.ROLE;
            }
            case 3: {
                return PrincipalType.GROUP;
            }
            default: {
                return null;
            }
        }
    }
}
