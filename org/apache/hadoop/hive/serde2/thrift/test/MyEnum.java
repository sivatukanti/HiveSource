// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift.test;

import org.apache.thrift.TEnum;

public enum MyEnum implements TEnum
{
    LLAMA(1), 
    ALPACA(2);
    
    private final int value;
    
    private MyEnum(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static MyEnum findByValue(final int value) {
        switch (value) {
            case 1: {
                return MyEnum.LLAMA;
            }
            case 2: {
                return MyEnum.ALPACA;
            }
            default: {
                return null;
            }
        }
    }
}
