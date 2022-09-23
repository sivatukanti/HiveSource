// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInfo
{
    protected String username;
    protected ResourceInfo resourcesUsed;
    protected int numPendingApplications;
    protected int numActiveApplications;
    
    UserInfo() {
    }
    
    UserInfo(final String username, final Resource resUsed, final int activeApps, final int pendingApps) {
        this.username = username;
        this.resourcesUsed = new ResourceInfo(resUsed);
        this.numActiveApplications = activeApps;
        this.numPendingApplications = pendingApps;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public ResourceInfo getResourcesUsed() {
        return this.resourcesUsed;
    }
    
    public int getNumPendingApplications() {
        return this.numPendingApplications;
    }
    
    public int getNumActiveApplications() {
        return this.numActiveApplications;
    }
}
