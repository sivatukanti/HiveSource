// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TOperationState;

public enum OperationState
{
    INITIALIZED(TOperationState.INITIALIZED_STATE, false), 
    RUNNING(TOperationState.RUNNING_STATE, false), 
    FINISHED(TOperationState.FINISHED_STATE, true), 
    CANCELED(TOperationState.CANCELED_STATE, true), 
    CLOSED(TOperationState.CLOSED_STATE, true), 
    ERROR(TOperationState.ERROR_STATE, true), 
    UNKNOWN(TOperationState.UKNOWN_STATE, false), 
    PENDING(TOperationState.PENDING_STATE, false);
    
    private final TOperationState tOperationState;
    private final boolean terminal;
    
    private OperationState(final TOperationState tOperationState, final boolean terminal) {
        this.tOperationState = tOperationState;
        this.terminal = terminal;
    }
    
    public static OperationState getOperationState(final TOperationState tOperationState) {
        return values()[tOperationState.getValue()];
    }
    
    public static void validateTransition(final OperationState oldState, final OperationState newState) throws HiveSQLException {
        Label_0199: {
            switch (oldState) {
                case INITIALIZED: {
                    switch (newState) {
                        case PENDING:
                        case RUNNING:
                        case CANCELED:
                        case CLOSED: {
                            return;
                        }
                        default: {
                            break Label_0199;
                        }
                    }
                    break;
                }
                case PENDING: {
                    switch (newState) {
                        case RUNNING:
                        case CANCELED:
                        case CLOSED:
                        case FINISHED:
                        case ERROR: {
                            return;
                        }
                        default: {
                            break Label_0199;
                        }
                    }
                    break;
                }
                case RUNNING: {
                    switch (newState) {
                        case CANCELED:
                        case CLOSED:
                        case FINISHED:
                        case ERROR: {
                            return;
                        }
                        default: {
                            break Label_0199;
                        }
                    }
                    break;
                }
                case CANCELED:
                case FINISHED:
                case ERROR: {
                    if (OperationState.CLOSED.equals(newState)) {
                        return;
                    }
                    break;
                }
            }
        }
        throw new HiveSQLException("Illegal Operation state transition from " + oldState + " to " + newState);
    }
    
    public void validateTransition(final OperationState newState) throws HiveSQLException {
        validateTransition(this, newState);
    }
    
    public TOperationState toTOperationState() {
        return this.tOperationState;
    }
    
    public boolean isTerminal() {
        return this.terminal;
    }
}
