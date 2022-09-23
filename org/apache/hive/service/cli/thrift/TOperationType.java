// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.TEnum;

public enum TOperationType implements TEnum
{
    EXECUTE_STATEMENT(0), 
    GET_TYPE_INFO(1), 
    GET_CATALOGS(2), 
    GET_SCHEMAS(3), 
    GET_TABLES(4), 
    GET_TABLE_TYPES(5), 
    GET_COLUMNS(6), 
    GET_FUNCTIONS(7), 
    UNKNOWN(8);
    
    private final int value;
    
    private TOperationType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static TOperationType findByValue(final int value) {
        switch (value) {
            case 0: {
                return TOperationType.EXECUTE_STATEMENT;
            }
            case 1: {
                return TOperationType.GET_TYPE_INFO;
            }
            case 2: {
                return TOperationType.GET_CATALOGS;
            }
            case 3: {
                return TOperationType.GET_SCHEMAS;
            }
            case 4: {
                return TOperationType.GET_TABLES;
            }
            case 5: {
                return TOperationType.GET_TABLE_TYPES;
            }
            case 6: {
                return TOperationType.GET_COLUMNS;
            }
            case 7: {
                return TOperationType.GET_FUNCTIONS;
            }
            case 8: {
                return TOperationType.UNKNOWN;
            }
            default: {
                return null;
            }
        }
    }
}
