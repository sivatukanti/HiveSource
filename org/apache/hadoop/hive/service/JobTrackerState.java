// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.service;

import org.apache.thrift.TEnum;

public enum JobTrackerState implements TEnum
{
    INITIALIZING(1), 
    RUNNING(2);
    
    private final int value;
    
    private JobTrackerState(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static JobTrackerState findByValue(final int value) {
        switch (value) {
            case 1: {
                return JobTrackerState.INITIALIZING;
            }
            case 2: {
                return JobTrackerState.RUNNING;
            }
            default: {
                return null;
            }
        }
    }
}
