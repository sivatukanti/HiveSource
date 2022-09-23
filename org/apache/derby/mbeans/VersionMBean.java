// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.mbeans;

public interface VersionMBean
{
    String getProductName();
    
    String getProductTechnologyName();
    
    String getProductVendorName();
    
    int getMajorVersion();
    
    int getMinorVersion();
    
    int getMaintenanceVersion();
    
    String getVersionString();
    
    String getBuildNumber();
    
    boolean isBeta();
    
    boolean isAlpha();
}
