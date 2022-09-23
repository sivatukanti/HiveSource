// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum CompactionType implements TEnum
{
    MINOR(1), 
    MAJOR(2);
    
    private final int value;
    
    private CompactionType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static CompactionType findByValue(final int value) {
        switch (value) {
            case 1: {
                return CompactionType.MINOR;
            }
            case 2: {
                return CompactionType.MAJOR;
            }
            default: {
                return null;
            }
        }
    }
}
