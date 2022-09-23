// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.mbeans;

import org.apache.derby.iapi.services.monitor.Monitor;

public class Management implements ManagementMBean
{
    private ManagementMBean getManagementService() {
        return (ManagementMBean)Monitor.getSystemModule("org.apache.derby.iapi.services.jmx.ManagementService");
    }
    
    public void startManagement() {
        final ManagementMBean managementService = this.getManagementService();
        if (managementService != null) {
            managementService.startManagement();
        }
    }
    
    public void stopManagement() {
        final ManagementMBean managementService = this.getManagementService();
        if (managementService != null) {
            managementService.stopManagement();
        }
    }
    
    public boolean isManagementActive() {
        final ManagementMBean managementService = this.getManagementService();
        return managementService != null && managementService.isManagementActive();
    }
    
    public String getSystemIdentifier() {
        final ManagementMBean managementService = this.getManagementService();
        if (managementService == null) {
            return null;
        }
        return managementService.getSystemIdentifier();
    }
}
