// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TOperationHandle;
import org.apache.hive.service.cli.thrift.TProtocolVersion;

public class OperationHandle extends Handle
{
    private final OperationType opType;
    private final TProtocolVersion protocol;
    private boolean hasResultSet;
    
    public OperationHandle(final OperationType opType, final TProtocolVersion protocol) {
        this.hasResultSet = false;
        this.opType = opType;
        this.protocol = protocol;
    }
    
    public OperationHandle(final TOperationHandle tOperationHandle) {
        this(tOperationHandle, TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V1);
    }
    
    public OperationHandle(final TOperationHandle tOperationHandle, final TProtocolVersion protocol) {
        super(tOperationHandle.getOperationId());
        this.hasResultSet = false;
        this.opType = OperationType.getOperationType(tOperationHandle.getOperationType());
        this.hasResultSet = tOperationHandle.isHasResultSet();
        this.protocol = protocol;
    }
    
    public OperationType getOperationType() {
        return this.opType;
    }
    
    public void setHasResultSet(final boolean hasResultSet) {
        this.hasResultSet = hasResultSet;
    }
    
    public boolean hasResultSet() {
        return this.hasResultSet;
    }
    
    public TOperationHandle toTOperationHandle() {
        final TOperationHandle tOperationHandle = new TOperationHandle();
        tOperationHandle.setOperationId(this.getHandleIdentifier().toTHandleIdentifier());
        tOperationHandle.setOperationType(this.opType.toTOperationType());
        tOperationHandle.setHasResultSet(this.hasResultSet);
        return tOperationHandle;
    }
    
    public TProtocolVersion getProtocolVersion() {
        return this.protocol;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.opType == null) ? 0 : this.opType.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OperationHandle)) {
            return false;
        }
        final OperationHandle other = (OperationHandle)obj;
        return this.opType == other.opType;
    }
    
    @Override
    public String toString() {
        return "OperationHandle [opType=" + this.opType + ", getHandleIdentifier()=" + this.getHandleIdentifier() + "]";
    }
}
