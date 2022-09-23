// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum PartitionEventType implements TEnum
{
    LOAD_DONE(1);
    
    private final int value;
    
    private PartitionEventType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static PartitionEventType findByValue(final int value) {
        switch (value) {
            case 1: {
                return PartitionEventType.LOAD_DONE;
            }
            default: {
                return null;
            }
        }
    }
}
