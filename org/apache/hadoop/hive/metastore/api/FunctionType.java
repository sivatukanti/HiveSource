// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum FunctionType implements TEnum
{
    JAVA(1);
    
    private final int value;
    
    private FunctionType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static FunctionType findByValue(final int value) {
        switch (value) {
            case 1: {
                return FunctionType.JAVA;
            }
            default: {
                return null;
            }
        }
    }
}
