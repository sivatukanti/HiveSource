// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

public class OperationStatus
{
    private final OperationState state;
    private final HiveSQLException operationException;
    
    public OperationStatus(final OperationState state, final HiveSQLException operationException) {
        this.state = state;
        this.operationException = operationException;
    }
    
    public OperationState getState() {
        return this.state;
    }
    
    public HiveSQLException getOperationException() {
        return this.operationException;
    }
}
