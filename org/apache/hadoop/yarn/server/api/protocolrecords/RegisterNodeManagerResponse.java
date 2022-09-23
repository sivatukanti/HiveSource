// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.server.api.records.NodeAction;
import org.apache.hadoop.yarn.server.api.records.MasterKey;

public interface RegisterNodeManagerResponse
{
    MasterKey getContainerTokenMasterKey();
    
    void setContainerTokenMasterKey(final MasterKey p0);
    
    MasterKey getNMTokenMasterKey();
    
    void setNMTokenMasterKey(final MasterKey p0);
    
    NodeAction getNodeAction();
    
    void setNodeAction(final NodeAction p0);
    
    long getRMIdentifier();
    
    void setRMIdentifier(final long p0);
    
    String getDiagnosticsMessage();
    
    void setDiagnosticsMessage(final String p0);
    
    void setRMVersion(final String p0);
    
    String getRMVersion();
}
