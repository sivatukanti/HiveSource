// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum TxnState implements TEnum
{
    COMMITTED(1), 
    ABORTED(2), 
    OPEN(3);
    
    private final int value;
    
    private TxnState(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static TxnState findByValue(final int value) {
        switch (value) {
            case 1: {
                return TxnState.COMMITTED;
            }
            case 2: {
                return TxnState.ABORTED;
            }
            case 3: {
                return TxnState.OPEN;
            }
            default: {
                return null;
            }
        }
    }
}
