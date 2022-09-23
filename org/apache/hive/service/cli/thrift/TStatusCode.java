// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.TEnum;

public enum TStatusCode implements TEnum
{
    SUCCESS_STATUS(0), 
    SUCCESS_WITH_INFO_STATUS(1), 
    STILL_EXECUTING_STATUS(2), 
    ERROR_STATUS(3), 
    INVALID_HANDLE_STATUS(4);
    
    private final int value;
    
    private TStatusCode(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static TStatusCode findByValue(final int value) {
        switch (value) {
            case 0: {
                return TStatusCode.SUCCESS_STATUS;
            }
            case 1: {
                return TStatusCode.SUCCESS_WITH_INFO_STATUS;
            }
            case 2: {
                return TStatusCode.STILL_EXECUTING_STATUS;
            }
            case 3: {
                return TStatusCode.ERROR_STATUS;
            }
            case 4: {
                return TStatusCode.INVALID_HANDLE_STATUS;
            }
            default: {
                return null;
            }
        }
    }
}
