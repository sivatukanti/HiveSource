// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TOperationType;

public enum OperationType
{
    UNKNOWN_OPERATION(TOperationType.UNKNOWN), 
    EXECUTE_STATEMENT(TOperationType.EXECUTE_STATEMENT), 
    GET_TYPE_INFO(TOperationType.GET_TYPE_INFO), 
    GET_CATALOGS(TOperationType.GET_CATALOGS), 
    GET_SCHEMAS(TOperationType.GET_SCHEMAS), 
    GET_TABLES(TOperationType.GET_TABLES), 
    GET_TABLE_TYPES(TOperationType.GET_TABLE_TYPES), 
    GET_COLUMNS(TOperationType.GET_COLUMNS), 
    GET_FUNCTIONS(TOperationType.GET_FUNCTIONS);
    
    private TOperationType tOperationType;
    
    private OperationType(final TOperationType tOpType) {
        this.tOperationType = tOpType;
    }
    
    public static OperationType getOperationType(final TOperationType tOperationType) {
        for (final OperationType opType : values()) {
            if (tOperationType.equals(opType.tOperationType)) {
                return opType;
            }
        }
        return OperationType.UNKNOWN_OPERATION;
    }
    
    public TOperationType toTOperationType() {
        return this.tOperationType;
    }
}
