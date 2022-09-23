// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum LockType implements TEnum
{
    SHARED_READ(1), 
    SHARED_WRITE(2), 
    EXCLUSIVE(3);
    
    private final int value;
    
    private LockType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static LockType findByValue(final int value) {
        switch (value) {
            case 1: {
                return LockType.SHARED_READ;
            }
            case 2: {
                return LockType.SHARED_WRITE;
            }
            case 3: {
                return LockType.EXCLUSIVE;
            }
            default: {
                return null;
            }
        }
    }
}
