// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum GrantRevokeType implements TEnum
{
    GRANT(1), 
    REVOKE(2);
    
    private final int value;
    
    private GrantRevokeType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static GrantRevokeType findByValue(final int value) {
        switch (value) {
            case 1: {
                return GrantRevokeType.GRANT;
            }
            case 2: {
                return GrantRevokeType.REVOKE;
            }
            default: {
                return null;
            }
        }
    }
}
