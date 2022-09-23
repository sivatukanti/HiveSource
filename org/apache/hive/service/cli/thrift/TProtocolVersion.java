// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.TEnum;

public enum TProtocolVersion implements TEnum
{
    HIVE_CLI_SERVICE_PROTOCOL_V1(0), 
    HIVE_CLI_SERVICE_PROTOCOL_V2(1), 
    HIVE_CLI_SERVICE_PROTOCOL_V3(2), 
    HIVE_CLI_SERVICE_PROTOCOL_V4(3), 
    HIVE_CLI_SERVICE_PROTOCOL_V5(4), 
    HIVE_CLI_SERVICE_PROTOCOL_V6(5), 
    HIVE_CLI_SERVICE_PROTOCOL_V7(6), 
    HIVE_CLI_SERVICE_PROTOCOL_V8(7);
    
    private final int value;
    
    private TProtocolVersion(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static TProtocolVersion findByValue(final int value) {
        switch (value) {
            case 0: {
                return TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V1;
            }
            case 1: {
                return TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V2;
            }
            case 2: {
                return TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V3;
            }
            case 3: {
                return TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V4;
            }
            case 4: {
                return TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V5;
            }
            case 5: {
                return TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V6;
            }
            case 6: {
                return TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V7;
            }
            case 7: {
                return TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8;
            }
            default: {
                return null;
            }
        }
    }
}
