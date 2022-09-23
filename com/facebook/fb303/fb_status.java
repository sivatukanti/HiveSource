// 
// Decompiled by Procyon v0.5.36
// 

package com.facebook.fb303;

import org.apache.thrift.TEnum;

public enum fb_status implements TEnum
{
    DEAD(0), 
    STARTING(1), 
    ALIVE(2), 
    STOPPING(3), 
    STOPPED(4), 
    WARNING(5);
    
    private final int value;
    
    private fb_status(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static fb_status findByValue(final int value) {
        switch (value) {
            case 0: {
                return fb_status.DEAD;
            }
            case 1: {
                return fb_status.STARTING;
            }
            case 2: {
                return fb_status.ALIVE;
            }
            case 3: {
                return fb_status.STOPPING;
            }
            case 4: {
                return fb_status.STOPPED;
            }
            case 5: {
                return fb_status.WARNING;
            }
            default: {
                return null;
            }
        }
    }
}
