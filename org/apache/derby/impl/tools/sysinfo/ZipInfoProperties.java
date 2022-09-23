// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.tools.sysinfo;

import org.apache.derby.iapi.services.info.ProductVersionHolder;

public class ZipInfoProperties
{
    private final ProductVersionHolder version;
    private String location;
    
    ZipInfoProperties(final ProductVersionHolder version) {
        this.version = version;
    }
    
    public String getVersionBuildInfo() {
        if (this.version == null) {
            return Main.getTextMessage("SIF04.C");
        }
        if ("DRDA:jcc".equals(this.version.getProductTechnologyName())) {
            return this.version.getSimpleVersionString() + " - (" + this.version.getBuildNumber() + ")";
        }
        return this.version.getVersionBuildString(true);
    }
    
    public String getLocation() {
        if (this.location == null) {
            return Main.getTextMessage("SIF01.H");
        }
        return this.location;
    }
    
    void setLocation(final String location) {
        this.location = location;
    }
}
