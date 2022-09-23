// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum LockLevel implements TEnum
{
    DB(1), 
    TABLE(2), 
    PARTITION(3);
    
    private final int value;
    
    private LockLevel(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static LockLevel findByValue(final int value) {
        switch (value) {
            case 1: {
                return LockLevel.DB;
            }
            case 2: {
                return LockLevel.TABLE;
            }
            case 3: {
                return LockLevel.PARTITION;
            }
            default: {
                return null;
            }
        }
    }
}
