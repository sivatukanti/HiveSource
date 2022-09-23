// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.TEnum;

public enum TFetchOrientation implements TEnum
{
    FETCH_NEXT(0), 
    FETCH_PRIOR(1), 
    FETCH_RELATIVE(2), 
    FETCH_ABSOLUTE(3), 
    FETCH_FIRST(4), 
    FETCH_LAST(5);
    
    private final int value;
    
    private TFetchOrientation(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static TFetchOrientation findByValue(final int value) {
        switch (value) {
            case 0: {
                return TFetchOrientation.FETCH_NEXT;
            }
            case 1: {
                return TFetchOrientation.FETCH_PRIOR;
            }
            case 2: {
                return TFetchOrientation.FETCH_RELATIVE;
            }
            case 3: {
                return TFetchOrientation.FETCH_ABSOLUTE;
            }
            case 4: {
                return TFetchOrientation.FETCH_FIRST;
            }
            case 5: {
                return TFetchOrientation.FETCH_LAST;
            }
            default: {
                return null;
            }
        }
    }
}
