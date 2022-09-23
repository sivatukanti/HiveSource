// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum LockState implements TEnum
{
    ACQUIRED(1), 
    WAITING(2), 
    ABORT(3), 
    NOT_ACQUIRED(4);
    
    private final int value;
    
    private LockState(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static LockState findByValue(final int value) {
        switch (value) {
            case 1: {
                return LockState.ACQUIRED;
            }
            case 2: {
                return LockState.WAITING;
            }
            case 3: {
                return LockState.ABORT;
            }
            case 4: {
                return LockState.NOT_ACQUIRED;
            }
            default: {
                return null;
            }
        }
    }
}
