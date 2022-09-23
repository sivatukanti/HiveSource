// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.TEnum;

public enum EventRequestType implements TEnum
{
    INSERT(1), 
    UPDATE(2), 
    DELETE(3);
    
    private final int value;
    
    private EventRequestType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static EventRequestType findByValue(final int value) {
        switch (value) {
            case 1: {
                return EventRequestType.INSERT;
            }
            case 2: {
                return EventRequestType.UPDATE;
            }
            case 3: {
                return EventRequestType.DELETE;
            }
            default: {
                return null;
            }
        }
    }
}
