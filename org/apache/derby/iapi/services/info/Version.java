// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.info;

import java.security.AccessControlException;
import java.security.Permission;
import java.security.AccessController;
import org.apache.derby.security.SystemPermission;
import org.apache.derby.mbeans.VersionMBean;

public class Version implements VersionMBean
{
    private final ProductVersionHolder versionInfo;
    private final String permissionName;
    
    public Version(final ProductVersionHolder versionInfo, final String permissionName) {
        this.versionInfo = versionInfo;
        this.permissionName = permissionName;
    }
    
    private void checkMonitor() {
        try {
            if (System.getSecurityManager() != null) {
                AccessController.checkPermission(new SystemPermission(this.permissionName, "monitor"));
            }
        }
        catch (AccessControlException ex) {
            throw new SecurityException(ex.getMessage());
        }
    }
    
    public String getProductName() {
        this.checkMonitor();
        return this.versionInfo.getProductName();
    }
    
    public String getProductTechnologyName() {
        this.checkMonitor();
        return this.versionInfo.getProductTechnologyName();
    }
    
    public String getProductVendorName() {
        this.checkMonitor();
        return this.versionInfo.getProductVendorName();
    }
    
    public String getVersionString() {
        this.checkMonitor();
        return this.versionInfo.getVersionBuildString(true);
    }
    
    public int getMajorVersion() {
        this.checkMonitor();
        return this.versionInfo.getMajorVersion();
    }
    
    public int getMinorVersion() {
        this.checkMonitor();
        return this.versionInfo.getMinorVersion();
    }
    
    public int getMaintenanceVersion() {
        this.checkMonitor();
        return this.versionInfo.getMaintVersion();
    }
    
    public String getBuildNumber() {
        this.checkMonitor();
        return this.versionInfo.getBuildNumber();
    }
    
    public boolean isBeta() {
        this.checkMonitor();
        return this.versionInfo.isBeta();
    }
    
    public boolean isAlpha() {
        this.checkMonitor();
        return this.versionInfo.isAlpha();
    }
}
