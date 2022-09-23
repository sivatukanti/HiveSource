// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.mbeans;

public interface ManagementMBean
{
    boolean isManagementActive();
    
    String getSystemIdentifier();
    
    void startManagement();
    
    void stopManagement();
}
