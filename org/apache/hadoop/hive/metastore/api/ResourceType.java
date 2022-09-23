// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum ResourceType implements TEnum
{
    JAR(1), 
    FILE(2), 
    ARCHIVE(3);
    
    private final int value;
    
    private ResourceType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static ResourceType findByValue(final int value) {
        switch (value) {
            case 1: {
                return ResourceType.JAR;
            }
            case 2: {
                return ResourceType.FILE;
            }
            case 3: {
                return ResourceType.ARCHIVE;
            }
            default: {
                return null;
            }
        }
    }
}
