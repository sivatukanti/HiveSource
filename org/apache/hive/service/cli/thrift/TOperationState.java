// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.TEnum;

public enum TOperationState implements TEnum
{
    INITIALIZED_STATE(0), 
    RUNNING_STATE(1), 
    FINISHED_STATE(2), 
    CANCELED_STATE(3), 
    CLOSED_STATE(4), 
    ERROR_STATE(5), 
    UKNOWN_STATE(6), 
    PENDING_STATE(7);
    
    private final int value;
    
    private TOperationState(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static TOperationState findByValue(final int value) {
        switch (value) {
            case 0: {
                return TOperationState.INITIALIZED_STATE;
            }
            case 1: {
                return TOperationState.RUNNING_STATE;
            }
            case 2: {
                return TOperationState.FINISHED_STATE;
            }
            case 3: {
                return TOperationState.CANCELED_STATE;
            }
            case 4: {
                return TOperationState.CLOSED_STATE;
            }
            case 5: {
                return TOperationState.ERROR_STATE;
            }
            case 6: {
                return TOperationState.UKNOWN_STATE;
            }
            case 7: {
                return TOperationState.PENDING_STATE;
            }
            default: {
                return null;
            }
        }
    }
}
