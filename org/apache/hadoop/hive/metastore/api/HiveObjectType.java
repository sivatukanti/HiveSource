// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum HiveObjectType implements TEnum
{
    GLOBAL(1), 
    DATABASE(2), 
    TABLE(3), 
    PARTITION(4), 
    COLUMN(5);
    
    private final int value;
    
    private HiveObjectType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static HiveObjectType findByValue(final int value) {
        switch (value) {
            case 1: {
                return HiveObjectType.GLOBAL;
            }
            case 2: {
                return HiveObjectType.DATABASE;
            }
            case 3: {
                return HiveObjectType.TABLE;
            }
            case 4: {
                return HiveObjectType.PARTITION;
            }
            case 5: {
                return HiveObjectType.COLUMN;
            }
            default: {
                return null;
            }
        }
    }
}
